package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MindlyDatabase
import com.example.data.firebase.FirebaseManager
import com.example.data.model.AwarenessCategory
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import com.example.data.repository.MindlyRepository
import com.example.i18n.AppLanguage
import com.example.ui.components.NavigationScreen
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class SubScreen {
    object None : SubScreen()
    data class AwarenessDetail(val category: AwarenessCategory) : SubScreen()
    object BreathingExercise : SubScreen()
    object GroundingExercise : SubScreen()
    object MoodHistory : SubScreen()
    object EmergencyHelp : SubScreen()
    object AdminDashboard : SubScreen()
}

data class AdminStats(
    val totalUsers: Int = 0,
    val publishedHelplines: Int = 0,
    val totalPersonalContacts: Int = 0,
    val totalMoodLogs: Int = 0
)

class MindlyViewModel(application: Application) : AndroidViewModel(application) {
    private val database = MindlyDatabase.getDatabase(application, viewModelScope)
    private val repository = MindlyRepository(database)
    private val sharedPrefs = application.getSharedPreferences("mindly_prefs", Context.MODE_PRIVATE)

    // Current User & Session
    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _language = MutableStateFlow(AppLanguage.ENGLISH)
    val language: StateFlow<AppLanguage> = _language.asStateFlow()

    private val _isDarkMode = MutableStateFlow(true)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Navigation
    private val _currentTab = MutableStateFlow(NavigationScreen.HOME)
    val currentTab: StateFlow<NavigationScreen> = _currentTab.asStateFlow()

    private val _subScreenStack = MutableStateFlow<List<SubScreen>>(emptyList())
    val subScreen: StateFlow<SubScreen> = _subScreenStack.combine(_subScreenStack) { stack, _ ->
        stack.lastOrNull() ?: SubScreen.None
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), SubScreen.None)

    // Auth Form State
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    // Help Directory Search & Filters
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow("All")
    val selectedProblemFilter = MutableStateFlow("All")
    val emergencyOnlyFilter = MutableStateFlow(false)

    // Reactive Personal Contacts (Private to user UID)
    val personalContacts: StateFlow<List<PersonalContactEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getPersonalContacts(user.uid, user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive Mood History
    val moodHistory: StateFlow<List<MoodCheckinEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getMoodCheckins(user.uid, user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Published Helplines Directory with Problem Filter (Global Firestore)
    private val allPublishedContacts = repository.getPublishedProfessionalContacts()
    val filteredHelplines: StateFlow<List<ProfessionalContactEntity>> = combine(
        allPublishedContacts,
        searchQuery,
        selectedCategoryFilter,
        emergencyOnlyFilter,
        selectedProblemFilter
    ) { dbList, query, category, emergencyOnly, problemTag ->
        val sourceList = if (dbList.isNotEmpty()) dbList else com.example.data.model.VerifiedGovernmentHelplines.list
        sourceList.filter { item ->
            val matchesQuery = query.isBlank() ||
                item.name.contains(query, ignoreCase = true) ||
                item.organization.contains(query, ignoreCase = true) ||
                item.description.contains(query, ignoreCase = true) ||
                item.location.contains(query, ignoreCase = true) ||
                item.phone.contains(query, ignoreCase = true)

            val matchesCategory = category == "All" || item.professionalType.equals(category, ignoreCase = true)
            val matchesEmergency = !emergencyOnly || item.isEmergency
            val matchesProblem = problemTag == "All" || item.problemCategories.contains(problemTag, ignoreCase = true)

            matchesQuery && matchesCategory && matchesEmergency && matchesProblem
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), com.example.data.model.VerifiedGovernmentHelplines.list)

    // Admin Helplines (Global Firestore)
    val adminHelplines: StateFlow<List<ProfessionalContactEntity>> =
        repository.getAllContactsForAdmin()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Real-Time Admin Users List from Central Firestore
    val adminUsers: StateFlow<List<UserEntity>> =
        repository.observeAllUsersForAdmin()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Stats dynamically calculated from Central Firestore real-time listeners!
    val adminStats: StateFlow<AdminStats> = combine(
        adminUsers,
        adminHelplines,
        personalContacts,
        moodHistory
    ) { users, helplines, personal, moods ->
        AdminStats(
            totalUsers = users.size,
            publishedHelplines = helplines.count { it.isPublished },
            totalPersonalContacts = personal.size,
            totalMoodLogs = moods.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    init {
        // Initialize Firebase
        FirebaseManager.init(application)

        // Ensure verified Government helplines are in the central database
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.ensureGovernmentHelplinesSeeded()
        }

        // Restore session if user previously signed in
        val savedUserId = sharedPrefs.getLong("current_user_id", -1L)
        val savedUserUid = sharedPrefs.getString("current_user_uid", null)
        if (savedUserUid != null) {
            viewModelScope.launch {
                val savedUser = repository.getUserByUid(savedUserUid)
                if (savedUser != null) {
                    setCurrentUser(savedUser)
                }
            }
        } else if (savedUserId != -1L) {
            viewModelScope.launch {
                val savedUser = repository.getUserById(savedUserId)
                if (savedUser != null) {
                    setCurrentUser(savedUser)
                }
            }
        }
    }

    private fun setCurrentUser(user: UserEntity) {
        _currentUser.value = user
        _language.value = AppLanguage.fromCode(user.language)
        _isDarkMode.value = user.isDarkMode
        _authError.value = null
        sharedPrefs.edit()
            .putLong("current_user_id", user.id)
            .putString("current_user_uid", user.uid)
            .apply()
    }

    // Problem filter for Help directory
    fun setProblemFilter(tag: String) {
        selectedProblemFilter.value = tag
    }

    fun clearProblemFilter() {
        selectedProblemFilter.value = "All"
    }

    // Auth actions (Firebase Authentication + Central Firestore)
    fun login(email: String, password: String, preferredLang: AppLanguage) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null

            val result = repository.loginUser(email, password, preferredLang.code)
            result.onSuccess { user ->
                setCurrentUser(user)
            }.onFailure {
                _authError.value = "error_auth_failed"
            }
            _isAuthLoading.value = false
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        language: AppLanguage
    ) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null

            if (name.isBlank()) {
                _authError.value = "error_empty_name"
                _isAuthLoading.value = false
                return@launch
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                _authError.value = "error_invalid_email"
                _isAuthLoading.value = false
                return@launch
            }
            if (password.length < 6) {
                _authError.value = "error_short_password"
                _isAuthLoading.value = false
                return@launch
            }
            if (password != confirmPassword) {
                _authError.value = "error_password_mismatch"
                _isAuthLoading.value = false
                return@launch
            }

            // Real-time user registration to Firebase Auth + central Firestore
            val result = repository.registerUser(name, email, password, language.code, "user")
            result.onSuccess { newUser ->
                setCurrentUser(newUser)
            }.onFailure {
                _authError.value = "error_email_exists"
            }
            _isAuthLoading.value = false
        }
    }

    fun verifyAndLoginAdminPin(pin: String): Boolean {
        if (pin.trim() == "1314") {
            viewModelScope.launch {
                var admin = repository.getUserByEmail("admin@mindly.org")
                if (admin == null) {
                    val registered = repository.registerUser("Admin", "admin@mindly.org", "Admin@123", "en", "admin")
                    admin = registered.getOrNull()
                }
                if (admin != null) {
                    if (admin.role != "admin") {
                        val updated = admin.copy(role = "admin")
                        repository.updateUser(updated)
                        admin = updated
                    }
                    setCurrentUser(admin)
                    pushSubScreen(SubScreen.AdminDashboard)
                }
            }
            return true
        }
        return false
    }

    fun logout() {
        sharedPrefs.edit()
            .remove("current_user_id")
            .remove("current_user_uid")
            .apply()
        try {
            FirebaseManager.auth.signOut()
        } catch (e: Exception) {
            // ignore
        }
        _currentUser.value = null
        _subScreenStack.value = emptyList()
        _currentTab.value = NavigationScreen.HOME
    }

    fun updateUserName(newName: String) {
        val user = _currentUser.value ?: return
        if (newName.isBlank()) return
        viewModelScope.launch {
            val updated = user.copy(name = newName.trim())
            repository.updateUser(updated)
            _currentUser.value = updated
        }
    }

    fun changePassword(oldPw: String, newPw: String, onResult: (Boolean, String) -> Unit) {
        val user = _currentUser.value ?: return
        if (newPw.length < 6) {
            onResult(false, "Password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            val updated = user.copy(passwordHash = newPw)
            repository.updateUser(updated)
            _currentUser.value = updated
            onResult(true, "Password updated successfully.")
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(language = lang.code)
            repository.updateUser(updated)
            _currentUser.value = updated
        }
    }

    fun setDarkMode(isDark: Boolean) {
        _isDarkMode.value = isDark
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(isDarkMode = isDark)
            repository.updateUser(updated)
            _currentUser.value = updated
        }
    }

    fun clearUserData() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.clearUserLocalData(user.id)
        }
    }

    // Navigation actions
    fun navigateToTab(tab: NavigationScreen) {
        _currentTab.value = tab
        _subScreenStack.value = emptyList()
    }

    fun pushSubScreen(sub: SubScreen) {
        if (sub is SubScreen.AdminDashboard) {
            val user = _currentUser.value
            if (user?.role != "admin") return
        }
        _subScreenStack.value = _subScreenStack.value + sub
    }

    fun navigateBack(): Boolean {
        val currentStack = _subScreenStack.value
        if (currentStack.isNotEmpty()) {
            _subScreenStack.value = currentStack.dropLast(1)
            return true
        }
        return false
    }

    // Personal Contacts CRUD
    fun addPersonalContact(name: String, relationship: String, phone: String, email: String, notes: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val contact = PersonalContactEntity(
                userId = user.id,
                userUid = user.uid,
                name = name.trim(),
                relationship = relationship,
                phone = phone.trim(),
                email = email.trim(),
                notes = notes.trim()
            )
            repository.addPersonalContact(user.uid, contact)
        }
    }

    fun updatePersonalContact(contact: PersonalContactEntity) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.updatePersonalContact(user.uid, contact)
        }
    }

    fun deletePersonalContact(id: Long, docId: String = "") {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.deletePersonalContact(user.uid, id, docId)
        }
    }

    // Mood Log
    fun logMood(level: Int, note: String, onDone: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.logMood(user.uid, user.id, level, note)
            onDone()
        }
    }

    // Admin Helplines CRUD with Role-Based Security
    fun addProfessionalContact(contact: ProfessionalContactEntity) {
        val user = _currentUser.value
        if (user?.role != "admin") return
        viewModelScope.launch {
            repository.addProfessionalContact(contact)
        }
    }

    fun updateProfessionalContact(contact: ProfessionalContactEntity) {
        val user = _currentUser.value
        if (user?.role != "admin") return
        viewModelScope.launch {
            repository.updateProfessionalContact(contact)
        }
    }

    fun deleteProfessionalContact(id: Long, docId: String = "") {
        val user = _currentUser.value
        if (user?.role != "admin") return
        viewModelScope.launch {
            repository.deleteProfessionalContact(id, docId)
        }
    }

    fun togglePublishStatus(id: Long, currentStatus: Boolean, docId: String = "") {
        val user = _currentUser.value
        if (user?.role != "admin") return
        viewModelScope.launch {
            repository.setPublishedStatus(id, docId, !currentStatus)
        }
    }
}
