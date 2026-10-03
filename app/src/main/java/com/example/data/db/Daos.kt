package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    suspend fun getUserById(id: Long): UserEntity?

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    fun observeUserById(id: Long): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: UserEntity): Long

    @Update
    suspend fun updateUser(user: UserEntity)

    @Query("SELECT COUNT(*) FROM users")
    fun getUserCount(): Flow<Int>
}

@Dao
interface PersonalContactDao {
    @Query("SELECT * FROM personal_contacts WHERE userId = :userId ORDER BY createdAt DESC")
    fun getContactsForUser(userId: Long): Flow<List<PersonalContactEntity>>

    @Query("SELECT * FROM personal_contacts WHERE id = :id LIMIT 1")
    suspend fun getContactById(id: Long): PersonalContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: PersonalContactEntity): Long

    @Update
    suspend fun updateContact(contact: PersonalContactEntity)

    @Query("DELETE FROM personal_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("DELETE FROM personal_contacts WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Long)

    @Query("SELECT COUNT(*) FROM personal_contacts")
    fun getTotalCount(): Flow<Int>
}

@Dao
interface ProfessionalContactDao {
    @Query("SELECT * FROM professional_contacts WHERE isPublished = 1 ORDER BY isEmergency DESC, name ASC")
    fun getAllPublishedContacts(): Flow<List<ProfessionalContactEntity>>

    @Query("SELECT * FROM professional_contacts ORDER BY id DESC")
    fun getAllContactsForAdmin(): Flow<List<ProfessionalContactEntity>>

    @Query("""
        SELECT * FROM professional_contacts 
        WHERE isPublished = 1 
        AND (name LIKE '%' || :query || '%' 
             OR organization LIKE '%' || :query || '%' 
             OR description LIKE '%' || :query || '%' 
             OR professionalType LIKE '%' || :query || '%'
             OR location LIKE '%' || :query || '%')
        ORDER BY isEmergency DESC, name ASC
    """)
    fun searchPublishedContacts(query: String): Flow<List<ProfessionalContactEntity>>

    @Query("SELECT * FROM professional_contacts WHERE id = :id LIMIT 1")
    suspend fun getContactById(id: Long): ProfessionalContactEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact: ProfessionalContactEntity): Long

    @Update
    suspend fun updateContact(contact: ProfessionalContactEntity)

    @Query("DELETE FROM professional_contacts WHERE id = :id")
    suspend fun deleteContactById(id: Long)

    @Query("UPDATE professional_contacts SET isPublished = :published, updatedAt = :updatedAt WHERE id = :id")
    suspend fun setPublishedStatus(id: Long, published: Boolean, updatedAt: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM professional_contacts WHERE isPublished = 1")
    fun getPublishedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM professional_contacts")
    suspend fun getCountDirect(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContacts(contacts: List<ProfessionalContactEntity>)
}

@Dao
interface MoodCheckinDao {
    @Query("SELECT * FROM mood_checkins WHERE userId = :userId ORDER BY timestamp DESC")
    fun getCheckinsForUser(userId: Long): Flow<List<MoodCheckinEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckin(checkin: MoodCheckinEntity): Long

    @Query("DELETE FROM mood_checkins WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Long)

    @Query("SELECT COUNT(*) FROM mood_checkins")
    fun getTotalCount(): Flow<Int>
}
