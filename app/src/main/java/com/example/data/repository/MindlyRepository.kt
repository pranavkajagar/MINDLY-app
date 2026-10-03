package com.example.data.repository

import com.example.data.db.MindlyDatabase
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

class MindlyRepository(private val database: MindlyDatabase) {
    private val userDao = database.userDao()
    private val personalDao = database.personalContactDao()
    private val proDao = database.professionalContactDao()
    private val moodDao = database.moodCheckinDao()

    // Auth & Users
    suspend fun getUserByEmail(email: String): UserEntity? = userDao.getUserByEmail(email.trim().lowercase())

    suspend fun getUserById(id: Long): UserEntity? = userDao.getUserById(id)

    fun observeUser(id: Long): Flow<UserEntity?> = userDao.observeUserById(id)

    suspend fun registerUser(name: String, email: String, passwordHash: String, language: String, role: String = "user"): Result<UserEntity> {
        val existing = userDao.getUserByEmail(email.trim().lowercase())
        if (existing != null) {
            return Result.failure(Exception("EMAIL_EXISTS"))
        }
        val user = UserEntity(
            name = name.trim(),
            email = email.trim().lowercase(),
            passwordHash = passwordHash,
            language = language,
            isDarkMode = true,
            role = role
        )
        val id = userDao.insertUser(user)
        return Result.success(user.copy(id = id))
    }

    suspend fun updateUser(user: UserEntity) {
        userDao.updateUser(user)
    }

    // Personal Emergency Contacts
    fun getPersonalContacts(userId: Long): Flow<List<PersonalContactEntity>> =
        personalDao.getContactsForUser(userId)

    suspend fun addPersonalContact(contact: PersonalContactEntity): Long =
        personalDao.insertContact(contact)

    suspend fun updatePersonalContact(contact: PersonalContactEntity) =
        personalDao.updateContact(contact.copy(updatedAt = System.currentTimeMillis()))

    suspend fun deletePersonalContact(id: Long) =
        personalDao.deleteContactById(id)

    // Professional Helplines Directory
    suspend fun ensureGovernmentHelplinesSeeded() {
        val count = proDao.getCountDirect()
        if (count == 0) {
            proDao.insertContacts(com.example.data.model.VerifiedGovernmentHelplines.list)
        }
    }

    fun getPublishedProfessionalContacts(): Flow<List<ProfessionalContactEntity>> =
        proDao.getAllPublishedContacts()

    fun searchProfessionalContacts(query: String): Flow<List<ProfessionalContactEntity>> {
        val trimmed = query.trim()
        return if (trimmed.isEmpty()) {
            proDao.getAllPublishedContacts()
        } else {
            proDao.searchPublishedContacts(trimmed)
        }
    }

    fun getAllContactsForAdmin(): Flow<List<ProfessionalContactEntity>> =
        proDao.getAllContactsForAdmin()

    suspend fun addProfessionalContact(contact: ProfessionalContactEntity): Long =
        proDao.insertContact(contact)

    suspend fun updateProfessionalContact(contact: ProfessionalContactEntity) =
        proDao.updateContact(contact.copy(updatedAt = System.currentTimeMillis()))

    suspend fun deleteProfessionalContact(id: Long) =
        proDao.deleteContactById(id)

    suspend fun setPublishedStatus(id: Long, isPublished: Boolean) =
        proDao.setPublishedStatus(id, isPublished)

    // Mood Check-ins
    fun getMoodCheckins(userId: Long): Flow<List<MoodCheckinEntity>> =
        moodDao.getCheckinsForUser(userId)

    suspend fun logMood(userId: Long, moodLevel: Int, note: String): Long {
        val entity = MoodCheckinEntity(
            userId = userId,
            moodLevel = moodLevel,
            note = note.trim(),
            timestamp = System.currentTimeMillis()
        )
        return moodDao.insertCheckin(entity)
    }

    // Privacy & Account
    suspend fun clearUserLocalData(userId: Long) {
        personalDao.deleteAllForUser(userId)
        moodDao.deleteAllForUser(userId)
    }

    // Admin Stats
    val totalUsersCount: Flow<Int> = userDao.getUserCount()
    val publishedHelplinesCount: Flow<Int> = proDao.getPublishedCount()
    val personalContactsCount: Flow<Int> = personalDao.getTotalCount()
    val moodLogsCount: Flow<Int> = moodDao.getTotalCount()
}
