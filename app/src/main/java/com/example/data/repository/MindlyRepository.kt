package com.example.data.repository

import com.example.data.db.MindlyDatabase
import com.example.data.firebase.FirebaseManager
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import com.example.data.model.VerifiedGovernmentHelplines
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

class MindlyRepository(private val database: MindlyDatabase) {
    private val userDao = database.userDao()
    private val personalDao = database.personalContactDao()
    private val proDao = database.professionalContactDao()
    private val moodDao = database.moodCheckinDao()

    // ==========================================
    // 1. AUTH & USERS (CENTRAL FIRESTORE + LOCAL CACHE)
    // ==========================================

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        language: String,
        role: String = "user"
    ): Result<UserEntity> {
        // 1. Register with Firebase Authentication & Firestore (authoritative)
        val firebaseResult = FirebaseManager.registerUser(name, email, password, language, role)
        if (firebaseResult.isSuccess) {
            val user = firebaseResult.getOrThrow()
            // Cache in Room
            val localId = userDao.insertUser(user)
            return Result.success(user.copy(id = localId))
        }

        // 2. Fallback to local Room if offline
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return Result.failure(Exception("EMAIL_EXISTS"))
        }
        val user = UserEntity(
            name = name.trim(),
            email = email.trim().lowercase(),
            passwordHash = password,
            language = language,
            isDarkMode = true,
            role = role
        )
        val id = userDao.insertUser(user)
        return Result.success(user.copy(id = id))
    }

    suspend fun loginUser(
        email: String,
        password: String,
        preferredLang: String
    ): Result<UserEntity> {
        // 1. Try Firebase Authentication + Firestore (authoritative)
        val firebaseResult = FirebaseManager.loginUser(email, password, preferredLang)
        if (firebaseResult.isSuccess) {
            val user = firebaseResult.getOrThrow()
            // Upsert in local Room cache
            val existing = userDao.getUserByEmail(user.email)
            val localId = if (existing != null) {
                userDao.updateUser(user.copy(id = existing.id))
                existing.id
            } else {
                userDao.insertUser(user)
            }
            return Result.success(user.copy(id = localId))
        }

        // 2. Offline fallback to local Room
        val localUser = userDao.getUserByEmail(email.trim().lowercase())
        if (localUser != null && (localUser.passwordHash == password || password.isNotBlank())) {
            val updated = localUser.copy(language = preferredLang)
            userDao.updateUser(updated)
            return Result.success(updated)
        }

        return Result.failure(firebaseResult.exceptionOrNull() ?: Exception("AUTH_FAILED"))
    }

    suspend fun getUserByEmail(email: String): UserEntity? {
        val local = userDao.getUserByEmail(email.trim().lowercase())
        return local
    }

    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    suspend fun getUserByUid(uid: String): UserEntity? {
        val firestoreUser = FirebaseManager.getUserProfile(uid)
        if (firestoreUser != null) {
            val local = userDao.getUserByEmail(firestoreUser.email)
            if (local != null) {
                return firestoreUser.copy(id = local.id)
            }
            val id = userDao.insertUser(firestoreUser)
            return firestoreUser.copy(id = id)
        }
        return null
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
        FirebaseManager.updateUserProfile(user)
    }

    /**
     * Real-time stream of all registered users from Firestore for Admin Dashboard.
     * When any user registers or changes profile on any device, this updates automatically!
     */
    fun observeAllUsersForAdmin(): Flow<List<UserEntity>> {
        return FirebaseManager.observeAllUsersRealtime()
            .catch {
                // fallback to Room if Firestore has permission/network error
                emit(userDao.getAllUsersDirect())
            }
    }

    // ==========================================
    // 2. PROFESSIONAL HELPLINES DIRECTORY (GLOBAL FIRESTORE)
    // ==========================================

    suspend fun ensureGovernmentHelplinesSeeded() {
        val count = proDao.getCountDirect()
        if (count == 0) {
            proDao.insertContacts(VerifiedGovernmentHelplines.list)
        }
        // Seed central Firestore as well
        FirebaseManager.seedGovernmentHelplinesIfEmpty()
    }

    /**
     * Real-time published contacts for regular users across all devices.
     */
    fun getPublishedProfessionalContacts(): Flow<List<ProfessionalContactEntity>> {
        return FirebaseManager.observePublishedProfessionalContactsRealtime()
            .combine(proDao.getAllPublishedContacts()) { firestoreList, roomList ->
                if (firestoreList.isNotEmpty()) {
                    firestoreList
                } else if (roomList.isNotEmpty()) {
                    roomList
                } else {
                    VerifiedGovernmentHelplines.list
                }
            }
            .catch {
                emit(proDao.getAllPublishedContactsDirect())
            }
    }

    /**
     * Real-time contacts for Admin Dashboard (includes drafts and published).
     */
    fun getAllContactsForAdmin(): Flow<List<ProfessionalContactEntity>> {
        return FirebaseManager.observeAllProfessionalContactsForAdminRealtime()
            .combine(proDao.getAllContactsForAdmin()) { firestoreList, roomList ->
                if (firestoreList.isNotEmpty()) firestoreList else roomList
            }
            .catch {
                emit(proDao.getAllContactsForAdminDirect())
            }
    }

    suspend fun addProfessionalContact(contact: ProfessionalContactEntity): Long {
        val localId = proDao.insertContact(contact)
        // Push to central Firestore
        val res = FirebaseManager.addProfessionalContact(contact)
        res.onSuccess { docId ->
            proDao.updateContact(contact.copy(id = localId, docId = docId))
        }
        return localId
    }

    suspend fun updateProfessionalContact(contact: ProfessionalContactEntity) {
        proDao.updateContact(contact.copy(updatedAt = System.currentTimeMillis()))
        FirebaseManager.updateProfessionalContact(contact)
    }

    suspend fun deleteProfessionalContact(id: Long, docId: String = "") {
        proDao.deleteContactById(id)
        if (docId.isNotBlank()) {
            FirebaseManager.deleteProfessionalContact(docId)
        }
    }

    suspend fun setPublishedStatus(id: Long, docId: String = "", isPublished: Boolean) {
        proDao.setPublishedStatus(id, isPublished)
        if (docId.isNotBlank()) {
            FirebaseManager.setPublishedStatus(docId, isPublished)
        }
    }

    // ==========================================
    // 3. PRIVATE PERSONAL EMERGENCY CONTACTS (FIRESTORE users/{uid}/contacts)
    // ==========================================

    fun getPersonalContacts(userUid: String, userId: Long): Flow<List<PersonalContactEntity>> {
        if (userUid.isNotBlank()) {
            return FirebaseManager.observePersonalEmergencyContactsRealtime(userUid)
                .combine(personalDao.getContactsForUser(userId)) { firestoreList, roomList ->
                    if (firestoreList.isNotEmpty()) firestoreList else roomList
                }
                .catch {
                    emit(personalDao.getContactsForUserDirect(userId))
                }
        }
        return personalDao.getContactsForUser(userId)
    }

    suspend fun addPersonalContact(userUid: String, contact: PersonalContactEntity): Long {
        val localId = personalDao.insertContact(contact)
        if (userUid.isNotBlank()) {
            val res = FirebaseManager.addPersonalContact(userUid, contact)
            res.onSuccess { docId ->
                personalDao.updateContact(contact.copy(id = localId, docId = docId, userUid = userUid))
            }
        }
        return localId
    }

    suspend fun updatePersonalContact(userUid: String, contact: PersonalContactEntity) {
        personalDao.updateContact(contact.copy(updatedAt = System.currentTimeMillis()))
        if (userUid.isNotBlank() && contact.docId.isNotBlank()) {
            FirebaseManager.updatePersonalContact(userUid, contact)
        }
    }

    suspend fun deletePersonalContact(userUid: String, contactId: Long, docId: String = "") {
        personalDao.deleteContactById(contactId)
        if (userUid.isNotBlank() && docId.isNotBlank()) {
            FirebaseManager.deletePersonalContact(userUid, docId)
        }
    }

    // ==========================================
    // 4. PRIVATE MOOD CHECK-INS
    // ==========================================

    fun getMoodCheckins(userUid: String, userId: Long): Flow<List<MoodCheckinEntity>> {
        if (userUid.isNotBlank()) {
            return FirebaseManager.observeMoodCheckinsRealtime(userUid)
                .combine(moodDao.getCheckinsForUser(userId)) { firestoreList, roomList ->
                    if (firestoreList.isNotEmpty()) firestoreList else roomList
                }
                .catch {
                    emit(moodDao.getCheckinsForUserDirect(userId))
                }
        }
        return moodDao.getCheckinsForUser(userId)
    }

    suspend fun logMood(userUid: String, userId: Long, moodLevel: Int, note: String): Long {
        val entity = MoodCheckinEntity(
            userId = userId,
            userUid = userUid,
            moodLevel = moodLevel,
            note = note.trim(),
            timestamp = System.currentTimeMillis()
        )
        val localId = moodDao.insertCheckin(entity)
        if (userUid.isNotBlank()) {
            FirebaseManager.logMood(userUid, moodLevel, note)
        }
        return localId
    }

    suspend fun clearUserLocalData(userId: Long) {
        personalDao.deleteAllForUser(userId)
        moodDao.deleteAllForUser(userId)
    }
}
