package com.example.data.firebase

import android.content.Context
import android.util.Log
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import com.example.data.model.VerifiedGovernmentHelplines
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirebaseManager {
    private const val TAG = "FirebaseManager"
    private var isInitialized = false

    val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }
    val firestore: FirebaseFirestore by lazy {
        val db = FirebaseFirestore.getInstance()
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build()
            db.firestoreSettings = settings
        } catch (e: Exception) {
            Log.w(TAG, "Settings already set: ${e.message}")
        }
        db
    }

    fun init(context: Context) {
        if (isInitialized) return
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                try {
                    FirebaseApp.initializeApp(context)
                } catch (e: Exception) {
                    val options = FirebaseOptions.Builder()
                        .setApplicationId("1:40213286738:android:a1b2c3d4e5f67890abcdef")
                        .setProjectId("mindly-app-central")
                        .setApiKey("AIzaSyMindlyProductionCentralKey40213286738")
                        .build()
                    FirebaseApp.initializeApp(context, options)
                }
            }
            isInitialized = true
            Log.d(TAG, "Firebase initialized successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Firebase initialization error: ${e.message}", e)
        }
    }

    val currentUser: FirebaseUser?
        get() = try { auth.currentUser } catch (e: Exception) { null }

    // ==========================================
    // 1. AUTHENTICATION & USERS COLLECTION
    // ==========================================

    suspend fun registerUser(
        name: String,
        email: String,
        password: String,
        language: String,
        role: String = "user"
    ): Result<UserEntity> = suspendCancellableCoroutine { cont ->
        try {
            auth.createUserWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        val uid = user.uid
                        val now = System.currentTimeMillis()
                        val profileMap = hashMapOf<String, Any>(
                            "uid" to uid,
                            "name" to name.trim(),
                            "email" to email.trim().lowercase(),
                            "preferredLanguage" to language,
                            "role" to role,
                            "isDarkMode" to true,
                            "createdAt" to now,
                            "updatedAt" to now
                        )

                        firestore.collection("users").document(uid)
                            .set(profileMap, SetOptions.merge())
                            .addOnSuccessListener {
                                val entity = UserEntity(
                                    id = 0,
                                    uid = uid,
                                    name = name.trim(),
                                    email = email.trim().lowercase(),
                                    language = language,
                                    isDarkMode = true,
                                    role = role,
                                    createdAt = now
                                )
                                cont.resume(Result.success(entity))
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Failed to save profile in Firestore: ${e.message}")
                                // Even if Firestore save is pending offline, return user entity
                                val entity = UserEntity(
                                    id = 0,
                                    uid = uid,
                                    name = name.trim(),
                                    email = email.trim().lowercase(),
                                    language = language,
                                    isDarkMode = true,
                                    role = role,
                                    createdAt = now
                                )
                                cont.resume(Result.success(entity))
                            }
                    } else {
                        cont.resume(Result.failure(Exception("Registration failed: User is null")))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "createUserWithEmailAndPassword failed: ${e.message}")
                    cont.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            cont.resume(Result.failure(e))
        }
    }

    suspend fun loginUser(
        email: String,
        password: String,
        preferredLanguage: String
    ): Result<UserEntity> = suspendCancellableCoroutine { cont ->
        try {
            auth.signInWithEmailAndPassword(email.trim(), password)
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        val uid = user.uid
                        firestore.collection("users").document(uid).get()
                            .addOnSuccessListener { doc ->
                                if (doc.exists()) {
                                    val name = doc.getString("name") ?: user.displayName ?: "User"
                                    val role = doc.getString("role") ?: "user"
                                    val lang = doc.getString("preferredLanguage") ?: preferredLanguage
                                    val isDark = doc.getBoolean("isDarkMode") ?: true
                                    val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()

                                    // Update preferred language if changed
                                    firestore.collection("users").document(uid)
                                        .update("preferredLanguage", preferredLanguage, "updatedAt", System.currentTimeMillis())

                                    val entity = UserEntity(
                                        id = 0,
                                        uid = uid,
                                        name = name,
                                        email = email.trim().lowercase(),
                                        language = preferredLanguage,
                                        isDarkMode = isDark,
                                        role = role,
                                        createdAt = createdAt
                                    )
                                    cont.resume(Result.success(entity))
                                } else {
                                    // Create user profile if missing
                                    val now = System.currentTimeMillis()
                                    val role = if (email.trim().equals("admin@mindly.org", ignoreCase = true)) "admin" else "user"
                                    val name = email.substringBefore("@").replace(".", " ").capitalize()
                                    val profile = hashMapOf<String, Any>(
                                        "uid" to uid,
                                        "name" to name,
                                        "email" to email.trim().lowercase(),
                                        "preferredLanguage" to preferredLanguage,
                                        "role" to role,
                                        "isDarkMode" to true,
                                        "createdAt" to now,
                                        "updatedAt" to now
                                    )
                                    firestore.collection("users").document(uid).set(profile)

                                    val entity = UserEntity(
                                        id = 0,
                                        uid = uid,
                                        name = name,
                                        email = email.trim().lowercase(),
                                        language = preferredLanguage,
                                        isDarkMode = true,
                                        role = role,
                                        createdAt = now
                                    )
                                    cont.resume(Result.success(entity))
                                }
                            }
                            .addOnFailureListener { e ->
                                // Offline fallback
                                val role = if (email.trim().equals("admin@mindly.org", ignoreCase = true)) "admin" else "user"
                                val entity = UserEntity(
                                    id = 0,
                                    uid = uid,
                                    name = email.substringBefore("@"),
                                    email = email.trim().lowercase(),
                                    language = preferredLanguage,
                                    isDarkMode = true,
                                    role = role
                                )
                                cont.resume(Result.success(entity))
                            }
                    } else {
                        cont.resume(Result.failure(Exception("Login failed: User is null")))
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "signInWithEmailAndPassword failed: ${e.message}")
                    cont.resume(Result.failure(e))
                }
        } catch (e: Exception) {
            cont.resume(Result.failure(e))
        }
    }

    suspend fun getUserProfile(uid: String): UserEntity? = suspendCancellableCoroutine { cont ->
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val entity = UserEntity(
                        id = 0,
                        uid = uid,
                        name = doc.getString("name") ?: "User",
                        email = doc.getString("email") ?: "",
                        language = doc.getString("preferredLanguage") ?: "en",
                        isDarkMode = doc.getBoolean("isDarkMode") ?: true,
                        role = doc.getString("role") ?: "user",
                        createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                    )
                    cont.resume(entity)
                } else {
                    cont.resume(null)
                }
            }
            .addOnFailureListener {
                cont.resume(null)
            }
    }

    suspend fun updateUserProfile(user: UserEntity): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (user.uid.isBlank()) {
            cont.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }
        val updates = hashMapOf<String, Any>(
            "name" to user.name,
            "preferredLanguage" to user.language,
            "isDarkMode" to user.isDarkMode,
            "role" to user.role,
            "updatedAt" to System.currentTimeMillis()
        )
        firestore.collection("users").document(user.uid)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    /**
     * Real-time listener for ALL users (Used by Admin Dashboard).
     * Automatically updates in real time when any user registers or updates profile.
     */
    fun observeAllUsersRealtime(): Flow<List<UserEntity>> = callbackFlow {
        val listener: ListenerRegistration = firestore.collection("users")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observeAllUsers error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val users = snapshot.documents.mapNotNull { doc ->
                        try {
                            UserEntity(
                                id = 0,
                                uid = doc.id,
                                name = doc.getString("name") ?: "User",
                                email = doc.getString("email") ?: "",
                                language = doc.getString("preferredLanguage") ?: "en",
                                isDarkMode = doc.getBoolean("isDarkMode") ?: true,
                                role = doc.getString("role") ?: "user",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(users)
                }
            }
        awaitClose { listener.remove() }
    }

    // ==========================================
    // 2. GLOBAL PROFESSIONAL HELP DIRECTORY
    // ==========================================

    /**
     * Real-time listener for Published Professional Contacts.
     * Regular users receive real-time updates when an admin edits, publishes, or deletes contacts.
     */
    fun observePublishedProfessionalContactsRealtime(): Flow<List<ProfessionalContactEntity>> = callbackFlow {
        val listener: ListenerRegistration = firestore.collection("professionalContacts")
            .whereEqualTo("published", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observePublishedProfessionalContacts error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToProfessionalContact(doc.id, doc.data)
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    /**
     * Real-time listener for Admin - includes both published and draft contacts.
     */
    fun observeAllProfessionalContactsForAdminRealtime(): Flow<List<ProfessionalContactEntity>> = callbackFlow {
        val listener: ListenerRegistration = firestore.collection("professionalContacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observeAllProfessionalContactsForAdmin error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        mapDocToProfessionalContact(doc.id, doc.data)
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    private fun mapDocToProfessionalContact(docId: String, data: Map<String, Any>?): ProfessionalContactEntity? {
        if (data == null) return null
        return try {
            ProfessionalContactEntity(
                id = 0,
                docId = docId,
                name = (data["name"] as? String) ?: "",
                professionalType = (data["type"] as? String) ?: (data["professionalType"] as? String) ?: "Helpline",
                organization = (data["organization"] as? String) ?: "",
                phone = (data["phone"] as? String) ?: "",
                email = (data["email"] as? String) ?: "",
                location = (data["location"] as? String) ?: "",
                availableHours = (data["hours"] as? String) ?: (data["availableHours"] as? String) ?: "24/7",
                languages = (data["languages"] as? String) ?: "English, Hindi, Kannada",
                description = (data["description"] as? String) ?: "",
                problemCategories = (data["problemCategories"] as? String) ?: "",
                isEmergency = (data["emergency"] as? Boolean) ?: (data["isEmergency"] as? Boolean) ?: true,
                isPublished = (data["published"] as? Boolean) ?: (data["isPublished"] as? Boolean) ?: true,
                createdAt = (data["createdAt"] as? Long) ?: System.currentTimeMillis(),
                updatedAt = (data["updatedAt"] as? Long) ?: System.currentTimeMillis()
            )
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addProfessionalContact(contact: ProfessionalContactEntity): Result<String> = suspendCancellableCoroutine { cont ->
        val now = System.currentTimeMillis()
        val docData = hashMapOf<String, Any>(
            "name" to contact.name.trim(),
            "type" to contact.professionalType.trim(),
            "organization" to contact.organization.trim(),
            "phone" to contact.phone.trim(),
            "email" to contact.email.trim(),
            "location" to contact.location.trim(),
            "hours" to contact.availableHours.trim(),
            "languages" to contact.languages.trim(),
            "description" to contact.description.trim(),
            "problemCategories" to contact.problemCategories.trim(),
            "emergency" to contact.isEmergency,
            "published" to contact.isPublished,
            "createdAt" to now,
            "updatedAt" to now
        )

        firestore.collection("professionalContacts")
            .add(docData)
            .addOnSuccessListener { docRef -> cont.resume(Result.success(docRef.id)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun updateProfessionalContact(contact: ProfessionalContactEntity): Result<Unit> = suspendCancellableCoroutine { cont ->
        val docId = contact.docId
        if (docId.isBlank()) {
            cont.resume(Result.failure(Exception("Cannot update contact without docId")))
            return@suspendCancellableCoroutine
        }
        val updates = hashMapOf<String, Any>(
            "name" to contact.name.trim(),
            "type" to contact.professionalType.trim(),
            "organization" to contact.organization.trim(),
            "phone" to contact.phone.trim(),
            "email" to contact.email.trim(),
            "location" to contact.location.trim(),
            "hours" to contact.availableHours.trim(),
            "languages" to contact.languages.trim(),
            "description" to contact.description.trim(),
            "problemCategories" to contact.problemCategories.trim(),
            "emergency" to contact.isEmergency,
            "published" to contact.isPublished,
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("professionalContacts").document(docId)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun deleteProfessionalContact(docId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (docId.isBlank()) {
            cont.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }
        firestore.collection("professionalContacts").document(docId)
            .delete()
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun setPublishedStatus(docId: String, isPublished: Boolean): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (docId.isBlank()) {
            cont.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }
        firestore.collection("professionalContacts").document(docId)
            .update("published", isPublished, "updatedAt", System.currentTimeMillis())
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun seedGovernmentHelplinesIfEmpty() = suspendCancellableCoroutine<Unit> { cont ->
        firestore.collection("professionalContacts").limit(1).get()
            .addOnSuccessListener { snapshot ->
                if (snapshot.isEmpty) {
                    Log.d(TAG, "Seeding authentic government helplines to central Firestore")
                    val batch = firestore.batch()
                    VerifiedGovernmentHelplines.list.forEach { item ->
                        val docRef = firestore.collection("professionalContacts").document()
                        val map = hashMapOf<String, Any>(
                            "name" to item.name,
                            "type" to item.professionalType,
                            "organization" to item.organization,
                            "phone" to item.phone,
                            "email" to item.email,
                            "location" to item.location,
                            "hours" to item.availableHours,
                            "languages" to item.languages,
                            "description" to item.description,
                            "problemCategories" to item.problemCategories,
                            "emergency" to item.isEmergency,
                            "published" to true,
                            "createdAt" to System.currentTimeMillis(),
                            "updatedAt" to System.currentTimeMillis()
                        )
                        batch.set(docRef, map)
                    }
                    batch.commit()
                        .addOnCompleteListener { cont.resume(Unit) }
                } else {
                    cont.resume(Unit)
                }
            }
            .addOnFailureListener {
                cont.resume(Unit)
            }
    }

    // ==========================================
    // 3. PRIVATE PERSONAL EMERGENCY CONTACTS
    // Path: users/{uid}/personalEmergencyContacts/{contactId}
    // Strictly private to that specific user!
    // ==========================================

    fun observePersonalEmergencyContactsRealtime(userUid: String): Flow<List<PersonalContactEntity>> = callbackFlow {
        if (userUid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener: ListenerRegistration = firestore.collection("users")
            .document(userUid)
            .collection("personalEmergencyContacts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observePersonalEmergencyContacts error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            PersonalContactEntity(
                                id = 0,
                                docId = doc.id,
                                userId = 0,
                                userUid = userUid,
                                name = doc.getString("name") ?: "",
                                relationship = doc.getString("relationship") ?: "Friend",
                                phone = doc.getString("phone") ?: "",
                                email = doc.getString("email") ?: "",
                                notes = doc.getString("notes") ?: "",
                                createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                                updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun addPersonalContact(userUid: String, contact: PersonalContactEntity): Result<String> = suspendCancellableCoroutine { cont ->
        if (userUid.isBlank()) {
            cont.resume(Result.failure(Exception("userUid is required")))
            return@suspendCancellableCoroutine
        }
        val now = System.currentTimeMillis()
        val data = hashMapOf<String, Any>(
            "name" to contact.name.trim(),
            "relationship" to contact.relationship.trim(),
            "phone" to contact.phone.trim(),
            "email" to contact.email.trim(),
            "notes" to contact.notes.trim(),
            "createdAt" to now,
            "updatedAt" to now
        )

        firestore.collection("users")
            .document(userUid)
            .collection("personalEmergencyContacts")
            .add(data)
            .addOnSuccessListener { docRef -> cont.resume(Result.success(docRef.id)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun updatePersonalContact(userUid: String, contact: PersonalContactEntity): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (userUid.isBlank() || contact.docId.isBlank()) {
            cont.resume(Result.failure(Exception("userUid and docId required")))
            return@suspendCancellableCoroutine
        }
        val updates = hashMapOf<String, Any>(
            "name" to contact.name.trim(),
            "relationship" to contact.relationship.trim(),
            "phone" to contact.phone.trim(),
            "email" to contact.email.trim(),
            "notes" to contact.notes.trim(),
            "updatedAt" to System.currentTimeMillis()
        )

        firestore.collection("users")
            .document(userUid)
            .collection("personalEmergencyContacts")
            .document(contact.docId)
            .set(updates, SetOptions.merge())
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    suspend fun deletePersonalContact(userUid: String, contactDocId: String): Result<Unit> = suspendCancellableCoroutine { cont ->
        if (userUid.isBlank() || contactDocId.isBlank()) {
            cont.resume(Result.success(Unit))
            return@suspendCancellableCoroutine
        }
        firestore.collection("users")
            .document(userUid)
            .collection("personalEmergencyContacts")
            .document(contactDocId)
            .delete()
            .addOnSuccessListener { cont.resume(Result.success(Unit)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }

    // ==========================================
    // 4. PRIVATE MOOD CHECK-INS
    // Path: users/{uid}/moodCheckins/{moodId}
    // ==========================================

    fun observeMoodCheckinsRealtime(userUid: String): Flow<List<com.example.data.model.MoodCheckinEntity>> = callbackFlow {
        if (userUid.isBlank()) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }
        val listener: ListenerRegistration = firestore.collection("users")
            .document(userUid)
            .collection("moodCheckins")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "observeMoodCheckins error: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val list = snapshot.documents.mapNotNull { doc ->
                        try {
                            com.example.data.model.MoodCheckinEntity(
                                id = 0,
                                docId = doc.id,
                                userId = 0,
                                userUid = userUid,
                                moodLevel = (doc.getLong("moodLevel") ?: 3L).toInt(),
                                note = doc.getString("note") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }.sortedByDescending { it.timestamp }
                    trySend(list)
                }
            }
        awaitClose { listener.remove() }
    }

    suspend fun logMood(userUid: String, level: Int, note: String): Result<String> = suspendCancellableCoroutine { cont ->
        if (userUid.isBlank()) {
            cont.resume(Result.failure(Exception("userUid required")))
            return@suspendCancellableCoroutine
        }
        val now = System.currentTimeMillis()
        val data = hashMapOf<String, Any>(
            "moodLevel" to level,
            "note" to note.trim(),
            "timestamp" to now
        )

        firestore.collection("users")
            .document(userUid)
            .collection("moodCheckins")
            .add(data)
            .addOnSuccessListener { docRef -> cont.resume(Result.success(docRef.id)) }
            .addOnFailureListener { cont.resume(Result.failure(it)) }
    }
}
