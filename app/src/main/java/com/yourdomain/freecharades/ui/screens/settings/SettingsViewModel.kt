package com.yourdomain.freecharades.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.yourdomain.freecharades.data.repository.SettingsRepository

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SettingsRepository.get(application)
    val settings = repository.settings
    fun setDurationSeconds(seconds: Int) = repository.setDurationSeconds(seconds)
    fun setSoundEnabled(enabled: Boolean) = repository.setSoundEnabled(enabled)
    fun setHapticsEnabled(enabled: Boolean) = repository.setHapticsEnabled(enabled)
    fun reset() = repository.reset()
}
