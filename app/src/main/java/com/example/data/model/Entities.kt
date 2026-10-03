package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val language: String = "en",
    val isDarkMode: Boolean = true,
    val role: String = "user", // "user" or "admin"
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "personal_contacts")
data class PersonalContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val name: String,
    val relationship: String,
    val phone: String,
    val email: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "professional_contacts")
data class ProfessionalContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val professionalType: String, // Helpline, Psychiatrist, Psychologist, Counsellor, Hospital, Emergency, Support Organization
    val organization: String,
    val phone: String,
    val email: String = "",
    val location: String = "",
    val availableHours: String = "24/7",
    val languages: String = "English, Hindi, Kannada",
    val description: String = "",
    val problemCategories: String = "", // e.g. "crisis, anxiety, depression, student"
    val isEmergency: Boolean = true,
    val isPublished: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class ProblemCategory(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String,
    val tag: String
)

object ProblemDirectory {
    val categories: List<ProblemCategory> = listOf(
        ProblemCategory(
            id = "crisis",
            title = "Suicide & Severe Crisis",
            subtitle = "Immediate 24/7 crisis de-escalation & intervention",
            iconName = "crisis",
            tag = "crisis"
        ),
        ProblemCategory(
            id = "anxiety",
            title = "Anxiety & Panic Attacks",
            subtitle = "Overwhelming fear, panic, trembling & nervous tension",
            iconName = "anxiety",
            tag = "anxiety"
        ),
        ProblemCategory(
            id = "depression",
            title = "Depression & Low Mood",
            subtitle = "Persistent sadness, hopelessness, emotional numbness",
            iconName = "depression",
            tag = "depression"
        ),
        ProblemCategory(
            id = "student",
            title = "Student & Exam Stress",
            subtitle = "Academic pressure, board exams, burnout & career dread",
            iconName = "student",
            tag = "student"
        ),
        ProblemCategory(
            id = "addiction",
            title = "Addiction & De-addiction",
            subtitle = "Alcohol, drug dependency, relapse support & rehabilitation",
            iconName = "addiction",
            tag = "addiction"
        ),
        ProblemCategory(
            id = "relationship",
            title = "Relationship & Family Conflict",
            subtitle = "Heartbreak, family friction, emotional isolation & grief",
            iconName = "relationship",
            tag = "relationship"
        ),
        ProblemCategory(
            id = "sleep",
            title = "Sleep & Insomnia",
            subtitle = "Chronic insomnia, night anxiety & exhaustion",
            iconName = "sleep",
            tag = "sleep"
        ),
        ProblemCategory(
            id = "emergency",
            title = "Immediate Medical Rescue",
            subtitle = "Life-threatening emergencies, ambulance & urgent rescue",
            iconName = "emergency",
            tag = "emergency"
        )
    )
}

enum class MoodType(val key: String, val emoji: String, val level: Int) {
    VERY_LOW("mood_very_low", "😔", 1),
    LOW("mood_low", "🙁", 2),
    OKAY("mood_okay", "😐", 3),
    GOOD("mood_good", "🙂", 4),
    GREAT("mood_great", "😄", 5);

    companion object {
        fun fromLevel(level: Int): MoodType = entries.find { it.level == level } ?: OKAY
        fun fromName(name: String): MoodType = runCatching { valueOf(name) }.getOrDefault(OKAY)
    }
}

@Entity(tableName = "mood_checkins")
data class MoodCheckinEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userId: Long,
    val moodLevel: Int,
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
