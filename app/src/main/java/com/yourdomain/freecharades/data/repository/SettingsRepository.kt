package com.yourdomain.freecharades.data.repository

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class GameSettings(
    val durationSeconds: Int = 60,
    val soundEnabled: Boolean = true,
    val hapticsEnabled: Boolean = true
)

/** Only these three preferences are persisted; round results stay in memory. */
class SettingsRepository private constructor(context: Context) {
    private val preferences = context.applicationContext.getSharedPreferences("charades_settings", Context.MODE_PRIVATE)
    private val mutableSettings = MutableStateFlow(
        GameSettings(
            durationSeconds = preferences.getInt("duration_seconds", 60).takeIf { it in durations } ?: 60,
            soundEnabled = preferences.getBoolean("sound_enabled", true),
            hapticsEnabled = preferences.getBoolean("haptics_enabled", true)
        )
    )
    val settings: StateFlow<GameSettings> = mutableSettings.asStateFlow()

    fun setDurationSeconds(seconds: Int) {
        require(seconds in durations)
        update(mutableSettings.value.copy(durationSeconds = seconds))
    }

    fun setSoundEnabled(enabled: Boolean) = update(mutableSettings.value.copy(soundEnabled = enabled))
    fun setHapticsEnabled(enabled: Boolean) = update(mutableSettings.value.copy(hapticsEnabled = enabled))
    fun reset() = update(GameSettings())

    private fun update(settings: GameSettings) {
        preferences.edit()
            .putInt("duration_seconds", settings.durationSeconds)
            .putBoolean("sound_enabled", settings.soundEnabled)
            .putBoolean("haptics_enabled", settings.hapticsEnabled)
            .apply()
        mutableSettings.value = settings
    }

    companion object {
        val durations = listOf(30, 60, 90, 120)
        @Volatile private var instance: SettingsRepository? = null
        fun get(context: Context): SettingsRepository = instance ?: synchronized(this) {
            instance ?: SettingsRepository(context).also { instance = it }
        }
    }
}
