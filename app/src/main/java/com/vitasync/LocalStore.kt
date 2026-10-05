package com.vitasync

import android.content.Context
import java.time.LocalDate

class LocalStore(context: Context) {
    private val preferences = context.getSharedPreferences("vitasync_local", Context.MODE_PRIVATE)

    fun read(): DailyLog {
        val today = LocalDate.now().toString()
        val lessonIndex = preferences.getInt("lesson_index", 0).coerceIn(0, lessons.size)
        if (preferences.getString("record_date", null) != today) {
            val freshLog = DailyLog(lessonIndex = lessonIndex)
            write(freshLog)
            return freshLog
        }
        return DailyLog(
            sleepHours = preferences.getFloat("sleep_hours", 0f),
            mealsLogged = preferences.getInt("meals_logged", 0),
            waterMl = preferences.getInt("water_ml", 0),
            workoutDone = preferences.getBoolean("workout_done", false),
            lessonIndex = lessonIndex,
        )
    }

    fun write(log: DailyLog) {
        preferences.edit()
            .putString("record_date", LocalDate.now().toString())
            .putFloat("sleep_hours", log.sleepHours)
            .putInt("meals_logged", log.mealsLogged)
            .putInt("water_ml", log.waterMl)
            .putBoolean("workout_done", log.workoutDone)
            .putInt("lesson_index", log.lessonIndex)
            .apply()
    }

    fun readWorkoutPlace(): String =
        preferences.getString("workout_place", "Casa")?.takeIf { it == "Casa" || it == "Academia" } ?: "Casa"

    fun writeWorkoutPlace(place: String) {
        if (place == "Casa" || place == "Academia") {
            preferences.edit().putString("workout_place", place).apply()
        }
    }

    fun clear() {
        preferences.edit().clear().apply()
    }
}
