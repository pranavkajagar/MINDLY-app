package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.MindlyDatabase
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

    // Reactive Personal Contacts
    val personalContacts: StateFlow<List<PersonalContactEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getPersonalContacts(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Reactive Mood History
    val moodHistory: StateFlow<List<MoodCheckinEntity>> = _currentUser.flatMapLatest { user ->
        if (user != null) {
            repository.getMoodCheckins(user.id)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Published Helplines Directory with Problem Filter
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

    // Admin Helplines
    val adminHelplines: StateFlow<List<ProfessionalContactEntity>> =
        repository.getAllContactsForAdmin()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Admin Stats
    val adminStats: StateFlow<AdminStats> = combine(
        repository.totalUsersCount,
        repository.publishedHelplinesCount,
        repository.personalContactsCount,
        repository.moodLogsCount
    ) { users, helplines, personal, moods ->
        AdminStats(
            totalUsers = users,
            publishedHelplines = helplines,
            totalPersonalContacts = personal,
            totalMoodLogs = moods
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AdminStats())

    init {
        // Ensure verified Government helplines are in the Room database
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            repository.ensureGovernmentHelplinesSeeded()
        }

        // Restore session if user previously signed in with their real name
        val savedUserId = sharedPrefs.getLong("current_user_id", -1L)
        if (savedUserId != -1L) {
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
        sharedPrefs.edit().putLong("current_user_id", user.id).apply()
    }

    // Problem filter for Help directory
    fun setProblemFilter(tag: String) {
        selectedProblemFilter.value = tag
    }

    fun clearProblemFilter() {
        selectedProblemFilter.value = "All"
    }

    // Auth actions
    fun login(email: String, password: String, preferredLang: AppLanguage) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null

            val user = repository.getUserByEmail(email.trim().lowercase())
            if (user != null && user.passwordHash == password) {
                val updated = user.copy(language = preferredLang.code)
                repository.updateUser(updated)
                setCurrentUser(updated)
            } else {
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

            val result = repository.registerUser(name, email, password, language.code, "user")
            result.onSuccess { newUser ->
                setCurrentUser(newUser)
            }.onFailure {
                _authError.value = "error_email_exists"
            }
            _isAuthLoading.value = false
        }
    }

    fun quickAdminLogin() {
        viewModelScope.launch {
            val admin = repository.getUserByEmail("admin@mindly.org")
            if (admin != null) {
                setCurrentUser(admin)
            }
        }
    }

    fun quickUserLogin(customName: String = "Pranav") {
        viewModelScope.launch {
            var user = repository.getUserByEmail("pranav@mindly.org")
            if (user == null) {
                val registered = repository.registerUser(customName, "pranav@mindly.org", "Pranav@123", "en", "user")
                user = registered.getOrNull()
            }
            if (user != null) {
                setCurrentUser(user)
            }
        }
    }

    fun logout() {
        sharedPrefs.edit().remove("current_user_id").apply()
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
        if (user.passwordHash != oldPw) {
            onResult(false, "Current password is incorrect.")
            return
        }
        if (newPw.length < 6) {
            onResult(false, "New password must be at least 6 characters.")
            return
        }
        viewModelScope.launch {
            val updated = user.copy(passwordHash = newPw)
            repository.updateUser(updated)
            _currentUser.value = updated
            onResult(true, "Password updated successfully.")
        }
    }

    // Settings actions
    fun setLanguage(lang: AppLanguage) {
        _language.value = lang
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                val updated = user.copy(language = lang.code)
                repository.updateUser(updated)
                _currentUser.value = updated
            }
        }
    }

    fun setDarkMode(darkMode: Boolean) {
        _isDarkMode.value = darkMode
        val user = _currentUser.value
        if (user != null) {
            viewModelScope.launch {
                val updated = user.copy(isDarkMode = darkMode)
                repository.updateUser(updated)
                _currentUser.value = updated
            }
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
                name = name.trim(),
                relationship = relationship,
                phone = phone.trim(),
                email = email.trim(),
                notes = notes.trim()
            )
            repository.addPersonalContact(contact)
        }
    }

    fun updatePersonalContact(contact: PersonalContactEntity) {
        viewModelScope.launch {
            repository.updatePersonalContact(contact)
        }
    }

    fun deletePersonalContact(id: Long) {
        viewModelScope.launch {
            repository.deletePersonalContact(id)
        }
    }

    // Mood Log
    fun logMood(level: Int, note: String, onDone: () -> Unit) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.logMood(user.id, level, note)
            onDone()
        }
    }

    // Admin Helplines CRUD
    fun addProfessionalContact(contact: ProfessionalContactEntity) {
        viewModelScope.launch {
            repository.addProfessionalContact(contact)
        }
    }

    fun updateProfessionalContact(contact: ProfessionalContactEntity) {
        viewModelScope.launch {
            repository.updateProfessionalContact(contact)
        }
    }

    fun deleteProfessionalContact(id: Long) {
        viewModelScope.launch {
            repository.deleteProfessionalContact(id)
        }
    }

    fun togglePublishStatus(id: Long, currentStatus: Boolean) {
        viewModelScope.launch {
            repository.setPublishedStatus(id, !currentStatus)
        }
    }
}
