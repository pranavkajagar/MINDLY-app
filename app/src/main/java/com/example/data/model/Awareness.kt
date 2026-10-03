package com.example.data.model

data class AwarenessCategory(
    val id: String,
    val titleKey: String,
    val subtitle: String,
    val iconName: String,
    val overview: String,
    val signs: List<String>,
    val copingStrategies: List<String>,
    val professionalHelpSigns: List<String>,
    val urgentSigns: List<String>
)

object AwarenessRepository {
    val categories: List<AwarenessCategory> = listOf(
        AwarenessCategory(
            id = "stress",
            titleKey = "cat_stress",
            subtitle = "Understanding pressure, overwhelm, and resilience",
            iconName = "bolt",
            overview = "Stress is the body and mind's natural physical and mental response to external challenges or demands. While acute stress can help us respond to short-term threats, chronic stress taxes our nervous system, impairing sleep, concentration, and emotional wellbeing.",
            signs = listOf(
                "Persistent irritability, feeling overwhelmed, or short temper",
                "Frequent tension headaches, tight neck/shoulders, or jaw clenching",
                "Difficulty falling or staying asleep; waking up exhausted",
                "Racing thoughts and trouble concentrating on everyday tasks",
                "Changes in appetite or digestive discomfort"
            ),
            copingStrategies = listOf(
                "Box Breathing (4-4-4-4): Inhale for 4s, hold for 4s, exhale for 4s, hold for 4s to reset your vagus nerve.",
                "Boundary Setting: Politely decline non-essential demands and schedule deliberate recovery windows.",
                "Gentle Physical Movement: A brisk 15-minute walk outdoors releases endorphins and clears adrenaline.",
                "Brain Dump Journaling: Write down everything causing worry to externalize mental clutter."
            ),
            professionalHelpSigns = listOf(
                "Stress remains continuous for several weeks without relief",
                "It interferes significantly with work, school, or personal relationships",
                "You find yourself relying heavily on unhealthy coping mechanisms"
            ),
            urgentSigns = listOf(
                "Feelings of complete despair, chest pains, or sudden inability to care for yourself"
            )
        ),
        AwarenessCategory(
            id = "anxiety",
            titleKey = "cat_anxiety",
            subtitle = "Navigating persistent worry, fear, and nervous energy",
            iconName = "waves",
            overview = "Anxiety involves persistent, excessive feelings of worry or dread about everyday situations. Unlike everyday nervousness before an exam or interview, anxiety can feel constant, pervasive, and disproportionate to the actual trigger.",
            signs = listOf(
                "Constant sense of impending danger, panic, or catastrophe",
                "Rapid heart rate, rapid breathing (hyperventilation), or shakiness",
                "Excessive sweating, dry mouth, or nervous stomach / nausea",
                "Restlessness and inability to relax your body or mind",
                "Avoiding places, conversations, or tasks that trigger anxious feelings"
            ),
            copingStrategies = listOf(
                "5-4-3-2-1 Sensory Grounding: Name 5 things you see, 4 you can touch, 3 you hear, 2 you smell, and 1 you taste.",
                "Cognitive Reframing: Ask yourself: 'Is this thought 100% true right now, or is it anxiety predicting the worst?'",
                "Limit Stimulants: Reduce caffeine, high-sugar energy drinks, and excessive screen doomscrolling.",
                "Progressive Muscle Relaxation: Tense muscle groups for 5 seconds, then deliberately release and feel the warmth."
            ),
            professionalHelpSigns = listOf(
                "Anxiety prevents you from leaving the house, working, or attending classes",
                "You experience recurring panic attacks or debilitating dread",
                "Self-soothing techniques no longer provide temporary relief"
            ),
            urgentSigns = listOf(
                "Severe panic attack with chest tightening where you feel you cannot breathe, or sudden thoughts of wanting to end the pain permanently"
            )
        ),
        AwarenessCategory(
            id = "depression",
            titleKey = "cat_depression",
            subtitle = "Recognizing low mood, emptiness, and loss of interest",
            iconName = "cloud",
            overview = "Depression is more than feeling sad for a day or two. It is a recognized mood condition that casts an enduring shadow of low energy, emotional numbness, hopelessness, and loss of interest in activities once enjoyed.",
            signs = listOf(
                "Persistent sadness, emptiness, or emotional numbness lasting over two weeks",
                "Loss of interest or pleasure in hobbies, social gatherings, and daily routines",
                "Noticeable exhaustion, lethargy, and everyday tasks feeling heavy or impossible",
                "Feelings of worthlessness, excessive guilt, or harsh self-criticism",
                "Changes in weight, appetite, or sleeping too much / insomnia"
            ),
            copingStrategies = listOf(
                "Micro-Goals: Break large tasks into tiny 2-minute steps (e.g., getting out of bed, drinking one glass of water).",
                "Sunlight & Fresh Air: Spend 10 to 15 minutes in morning natural sunlight to regulate circadian rhythms.",
                "Compassionate Self-Talk: Treat yourself with the same gentle patience you would offer a struggling loved one.",
                "Stay Connected: Even sending a simple message to one trusted person breaks the wall of isolation."
            ),
            professionalHelpSigns = listOf(
                "Depressive symptoms persist continuously for more than two weeks",
                "Inability to maintain personal hygiene, work responsibilities, or school duties",
                "Feeling detached from reality or trapped in deep hopelessness"
            ),
            urgentSigns = listOf(
                "Thoughts of self-harm or suicide. Contact Tele-MANAS (14416) or emergency services (112) immediately."
            )
        ),
        AwarenessCategory(
            id = "sleep",
            titleKey = "cat_sleep",
            subtitle = "Restoring healthy circadian rhythms and nocturnal peace",
            iconName = "moon",
            overview = "Sleep is foundational to mental health. Sleep deprivation lowers emotional resilience, amplifies anxiety, and exacerbates depressive symptoms. Conversely, mental distress often manifests first as difficulty falling or staying asleep.",
            signs = listOf(
                "Taking more than 30–45 minutes to fall asleep regularly",
                "Waking up multiple times throughout the night with anxious thoughts",
                "Waking up very early in the morning unable to return to sleep",
                "Daytime brain fog, heavy fatigue, and mood volatility",
                "Lying awake worrying about not being able to sleep"
            ),
            copingStrategies = listOf(
                "Consistent Sleep Schedule: Go to bed and wake up at the exact same hour every day, including weekends.",
                "Digital Sunset: Put away bright phone, tablet, and computer screens at least 45 minutes before sleep.",
                "Cool, Dark Bedroom: Keep the sleep environment around 18–20°C with blackout curtains.",
                "The 20-Minute Rule: If unable to sleep after 20 minutes, get out of bed to read in dim light until drowsy."
            ),
            professionalHelpSigns = listOf(
                "Chronic insomnia or daytime sleepiness persisting for over a month",
                "Nightmares related to past trauma disrupting rest continuously"
            ),
            urgentSigns = listOf(
                "Severe disorientation, hallucinations from extreme sleep deprivation, or sleep apnea with choking episodes"
            )
        ),
        AwarenessCategory(
            id = "panic",
            titleKey = "cat_panic",
            subtitle = "Managing sudden surges of intense fear and physical symptoms",
            iconName = "shield_alert",
            overview = "A panic attack is a sudden surge of intense fear or discomfort that peaks within minutes. It triggers an overwhelming fight-or-flight response, often accompanied by intense physical sensations that can mimic a physical medical emergency.",
            signs = listOf(
                "Pounding, racing heartbeat or heart palpitations",
                "Sensations of shortness of breath, smothering, or choking",
                "Trembling, shaking, sweating, or sudden chills and hot flashes",
                "Dizziness, lightheadedness, or feeling faint",
                "Intense fear of losing control, 'going crazy', or dying"
            ),
            copingStrategies = listOf(
                "Remember: Panic attacks are temporary; they usually peak in 10 minutes and always subside.",
                "Tactile Anchoring: Hold an ice cube or splash cold water on your face to stimulate the dive reflex.",
                "Slow Diaphragmatic Breath: Inhale deeply into your belly for 4 counts, exhale for 6 counts.",
                "Name the Event: Tell yourself internally: 'This is my body's false alarm. It is uncomfortable, but I am safe.'"
            ),
            professionalHelpSigns = listOf(
                "Recurring unexpected panic attacks that cause fear of the next attack",
                "Avoiding grocery stores, driving, or public transit due to fear of having an attack"
            ),
            urgentSigns = listOf(
                "First-time unexplained severe chest pain or shortness of breath—always rule out cardiac issues with emergency medical personnel"
            )
        ),
        AwarenessCategory(
            id = "loneliness",
            titleKey = "cat_loneliness",
            subtitle = "Rebuilding meaningful human connection and belonging",
            iconName = "user_heart",
            overview = "Loneliness is the distressing feeling that occurs when there is a perceived gap between the social connections we desire and those we actually experience. One can feel lonely even in a crowded room or within a large family.",
            signs = listOf(
                "Feeling unseen, misunderstood, or emotionally distant from those around you",
                "Hesitancy to reach out because of fear of being a burden or being rejected",
                "Excessive time spent passively consuming social media while feeling excluded",
                "A feeling of internal emptiness that companions cannot seem to fill"
            ),
            copingStrategies = listOf(
                "Quality Over Quantity: Focus on cultivating depth with one trusted friend rather than broad superficial networks.",
                "Community Micro-Connections: Say a friendly hello to a barista, librarian, or neighbor.",
                "Shared Interest Groups: Join book clubs, volunteer groups, or local hobby meetups.",
                "Support Helplines: Helplines like Tele-MANAS (14416) are there not just for suicide crisis, but also for when you need a caring, non-judgmental voice."
            ),
            professionalHelpSigns = listOf(
                "Prolonged social withdrawal leading to deep depression",
                "Severe social phobia preventing any interpersonal communication"
            ),
            urgentSigns = listOf(
                "Feeling that no one would miss you or thoughts of self-harm"
            )
        ),
        AwarenessCategory(
            id = "student_stress",
            titleKey = "cat_student_stress",
            subtitle = "Balancing academic expectations, deadlines, and self-worth",
            iconName = "school",
            overview = "Students frequently encounter intense pressures surrounding exams, parental expectations, career competition, and identity development. Chronic academic strain can lead to burnout, imposter syndrome, and emotional exhaustion.",
            signs = listOf(
                "Intense anxiety or panic before exams and project submissions",
                "Procrastination driven by fear of failure or perfectionism",
                "Sacrificing meals, sleep, and social connections solely for study",
                "Tying 100% of personal self-esteem to grades, ranks, or test marks"
            ),
            copingStrategies = listOf(
                "Pomodoro Technique: Work in focused 25-minute bursts with mandatory 5-minute movement breaks.",
                "Decouple Worth from Marks: Remember your value as a human being is never defined by a percentage or scorecard.",
                "Campus Counselling: Utilize university/college wellness centers and peer support groups.",
                "Healthy Routine: Never pull consecutive all-nighters; memory consolidation happens during sleep."
            ),
            professionalHelpSigns = listOf(
                "Inability to attend exams or classes due to paralyzing anxiety",
                "Severe depression following an academic setback"
            ),
            urgentSigns = listOf(
                "Extreme despair regarding academic results or thoughts of suicide. Immediate help is available at 14416."
            )
        ),
        AwarenessCategory(
            id = "emotional_wellbeing",
            titleKey = "cat_emotional_wellbeing",
            subtitle = "Fostering emotional literacy, self-compassion, and balance",
            iconName = "sparkles",
            overview = "Emotional wellbeing refers to the ability to understand, process, and navigate the full spectrum of human emotions. It doesn't mean being happy 100% of the time, but rather being able to recover from adversity with self-compassion.",
            signs = listOf(
                "Frequent emotional outbursts or volatile mood swings",
                "Difficulty identifying or naming what you are feeling",
                "Suppressing or burying emotions until they explode under minor pressure",
                "Persistent harsh inner critic and feelings of inadequacy"
            ),
            copingStrategies = listOf(
                "Name It to Tame It: Accurately label emotions (e.g., 'I am feeling disappointed and fatigued, not just angry').",
                "Mindful Acceptance: Allow emotions to arise without immediately judging them as 'bad' or 'unwanted'.",
                "Daily Gratitude Journal: Jot down three specific things that went well or brought peace today.",
                "Creative Expression: Channel feelings into art, music, writing, cooking, or movement."
            ),
            professionalHelpSigns = listOf(
                "Emotional dysregulation that harms relationships or job security",
                "Difficulty coping with past unresolved emotional pain or trauma"
            ),
            urgentSigns = listOf(
                "Sudden urge to harm oneself or destructive reckless behaviors"
            )
        )
    )
}
