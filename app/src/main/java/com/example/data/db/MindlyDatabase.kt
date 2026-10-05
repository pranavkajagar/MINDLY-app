package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.MoodCheckinEntity
import com.example.data.model.PersonalContactEntity
import com.example.data.model.ProfessionalContactEntity
import com.example.data.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        PersonalContactEntity::class,
        ProfessionalContactEntity::class,
        MoodCheckinEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MindlyDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun personalContactDao(): PersonalContactDao
    abstract fun professionalContactDao(): ProfessionalContactDao
    abstract fun moodCheckinDao(): MoodCheckinDao

    companion object {
        @Volatile
        private var INSTANCE: MindlyDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): MindlyDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MindlyDatabase::class.java,
                    "mindly_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabasePrepopulateCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabasePrepopulateCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(database: MindlyDatabase) {
            val userDao = database.userDao()
            val proDao = database.professionalContactDao()

            // 1. Seed Admin Account only (Password: Admin@123)
            userDao.insertUser(
                UserEntity(
                    id = 1,
                    name = "Admin",
                    email = "admin@mindly.org",
                    passwordHash = "Admin@123",
                    language = "en",
                    isDarkMode = true,
                    role = "admin"
                )
            )

            // NO user or personal contacts seeded by default.
            // Users will register with their own name and add their own personal contacts.

            // 2. Seed Verified Authentic Professional Helplines with Problem Tags
            val helplines = listOf(
                ProfessionalContactEntity(
                    name = "Tele-MANAS (Govt of India)",
                    professionalType = "Mental Health Helpline",
                    organization = "Ministry of Health & Family Welfare",
                    phone = "14416",
                    email = "telemanas@gov.in",
                    location = "Pan India (All States)",
                    availableHours = "24/7 (Toll Free)",
                    languages = "English, Kannada, Hindi, Tamil, Telugu, Malayalam, Bengali, Marathi + 13 others",
                    description = "National Tele-Mental Health Programme providing free 24/7 crisis de-escalation, suicide prevention, counseling, and psychiatric referrals across India.",
                    problemCategories = "crisis, anxiety, depression, student, relationship, sleep",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "KIRAN Mental Health Rehabilitation",
                    professionalType = "Mental Health Helpline",
                    organization = "DEPwD, Govt of India",
                    phone = "1800-599-0019",
                    email = "kiran.helpline@gov.in",
                    location = "Pan India",
                    availableHours = "24/7 (Toll Free)",
                    languages = "English, Hindi, Kannada, Marathi, Gujarati, Punjabi, Odia, Assamese",
                    description = "Dedicated national helpline for early screening, psychological first-aid, panic management, depressive distress, and rehabilitation.",
                    problemCategories = "crisis, anxiety, depression, student, sleep",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "NIMHANS Centre for Well-Being",
                    professionalType = "Hospital / Specialized Clinic",
                    organization = "NIMHANS Bengaluru",
                    phone = "080-26995000",
                    email = "wellbeing@nimhans.ac.in",
                    location = "Bengaluru, Karnataka",
                    availableHours = "9:00 AM - 5:00 PM (Mon-Sat)",
                    languages = "Kannada, English, Hindi",
                    description = "Premier institute of national importance providing specialized outpatient counseling, clinical psychology, stress clinics, and psychotherapy.",
                    problemCategories = "anxiety, depression, sleep, relationship, student",
                    isEmergency = false,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "National Drug De-Addiction Helpline",
                    professionalType = "Mental Health Helpline",
                    organization = "Ministry of Social Justice, Govt of India",
                    phone = "1800-11-0031",
                    email = "deaddiction-support@gov.in",
                    location = "Pan India",
                    availableHours = "24/7 (Toll Free)",
                    languages = "English, Hindi, Regional Languages",
                    description = "Toll-free national helpline offering confidential counseling, medical referrals, relapse prevention, and rehabilitation for substance dependency.",
                    problemCategories = "addiction",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "NIMHANS Centre for Addiction Medicine",
                    professionalType = "Hospital / Specialized Clinic",
                    organization = "NIMHANS Bengaluru",
                    phone = "080-26995393",
                    email = "cam@nimhans.ac.in",
                    location = "Bengaluru, Karnataka",
                    availableHours = "9:00 AM - 4:30 PM (Mon-Sat)",
                    languages = "Kannada, English, Hindi",
                    description = "World Health Organization collaborating centre specializing in detoxification, de-addiction therapy, behavioral addictions, and psychiatric rehabilitation.",
                    problemCategories = "addiction",
                    isEmergency = false,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "Fortis National Exam & Youth Helpline",
                    professionalType = "Support Organization",
                    organization = "Fortis Healthcare Mental Health",
                    phone = "+91 83768 04102",
                    email = "mentalhealth@fortishealthcare.com",
                    location = "Pan India",
                    availableHours = "24/7 Always Open",
                    languages = "English, Hindi + 14 regional languages",
                    description = "Specialized national helpline focused on academic anxiety, exam stress, youth emotional pressures, and career uncertainty.",
                    problemCategories = "student, anxiety",
                    isEmergency = false,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "Vandrevala Foundation 24/7 Helpline",
                    professionalType = "Mental Health Helpline",
                    organization = "Cyrus & Priya Vandrevala Foundation",
                    phone = "+91 9999 666 555",
                    email = "help@vandrevalafoundation.com",
                    location = "Pan India",
                    availableHours = "24/7 Always Open",
                    languages = "English, Hindi, Kannada, Tamil, Gujarati, Telugu",
                    description = "Free, confidential 24-hour psychological crisis intervention and counseling by trained clinical psychologists for distress and relationship conflict.",
                    problemCategories = "crisis, anxiety, depression, relationship",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "AASRA Suicide Prevention & Crisis Line",
                    professionalType = "Mental Health Helpline",
                    organization = "AASRA Trust",
                    phone = "+91 98204 66726",
                    email = "aasrahelpline@yahoo.com",
                    location = "Navi Mumbai / National",
                    availableHours = "24/7",
                    languages = "English, Hindi",
                    description = "Confidential, non-judgmental 24-hour helpline for people experiencing severe emotional despair, acute grief, or suicidal thoughts.",
                    problemCategories = "crisis, depression",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "iCall Psychosocial Helpline",
                    professionalType = "Support Organization",
                    organization = "Tata Institute of Social Sciences (TISS)",
                    phone = "+91 91529 87821",
                    email = "icall@tiss.edu",
                    location = "Mumbai / National",
                    availableHours = "10:00 AM - 8:00 PM (Mon-Sat)",
                    languages = "English, Hindi, Marathi",
                    description = "Professional psychosocial counseling service by trained mental health professionals addressing relationship conflict, grief, student stress, and depression.",
                    problemCategories = "relationship, student, anxiety, depression",
                    isEmergency = false,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "Sneha Suicide Prevention Helpline",
                    professionalType = "Mental Health Helpline",
                    organization = "Sneha Suicide Prevention Centre",
                    phone = "+91 44 2464 0050",
                    email = "help@snehaindia.org",
                    location = "Chennai / National",
                    availableHours = "24/7",
                    languages = "English, Tamil, Hindi",
                    description = "Dedicated non-profit voluntary organization providing unconditional emotional support to anyone feeling isolated, hopeless, or suicidal.",
                    problemCategories = "crisis, depression, relationship",
                    isEmergency = true,
                    isPublished = true
                ),
                ProfessionalContactEntity(
                    name = "112 National Emergency Response Service",
                    professionalType = "Emergency Service",
                    organization = "Govt of India Emergency Support",
                    phone = "112",
                    email = "emergency112@nic.in",
                    location = "All India",
                    availableHours = "24/7 Immediate",
                    languages = "All Regional Languages",
                    description = "Unified national emergency number for immediate ambulance, police, or medical rescue during life-threatening crises.",
                    problemCategories = "emergency, crisis",
                    isEmergency = true,
                    isPublished = true
                )
            )

            for (helpline in helplines) {
                proDao.insertContact(helpline)
            }
        }
    }
}
