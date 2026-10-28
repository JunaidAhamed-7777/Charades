package com.yourdomain.freecharades.ui.screens.game

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.yourdomain.freecharades.data.repository.CategoryRepository
import com.yourdomain.freecharades.data.repository.SettingsRepository
import com.yourdomain.freecharades.domain.game.RoundEngine
import com.yourdomain.freecharades.domain.game.RoundPhase
import com.yourdomain.freecharades.domain.game.RoundResult
import com.yourdomain.freecharades.domain.game.RoundSnapshot
import com.yourdomain.freecharades.domain.model.Category
import com.yourdomain.freecharades.domain.model.Word
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class GameUiState(
    val phase: RoundPhase = RoundPhase.LOADING,
    val category: Category? = null,
    val word: Word? = null,
    val secondsRemaining: Int = 60,
    val durationSeconds: Int = 60,
    val countdown: Int = 3,
    val correct: Int = 0,
    val skipped: Int = 0,
    val totalWords: Int = 0,
    val results: List<RoundResult> = emptyList(),
    val error: String? = null
)

class GameViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val categories = CategoryRepository.get(application)
    private val settingsRepository = SettingsRepository.get(application)
    val settings = settingsRepository.settings
    private val mutableState = MutableStateFlow(GameUiState())
    val state = mutableState.asStateFlow()
    private var engine: RoundEngine? = null
    private var loadJob: Job? = null
    private var tickerJob: Job? = null

    fun prepare(categoryId: String) {
        if (savedStateHandle.get<String>("categoryId") == categoryId &&
            (engine != null || loadJob?.isActive == true)) return
        savedStateHandle["categoryId"] = categoryId
        loadJob?.cancel()
        tickerJob?.cancel()
        engine = null
        mutableState.value = GameUiState()
        loadJob = viewModelScope.launch {
            try {
                val category = categories.load().firstOrNull { it.id == categoryId }
                if (category == null) {
                    mutableState.value = GameUiState(phase = RoundPhase.ERROR, error = "This deck could not be found. Choose another deck.")
                } else {
                    mutableState.value = GameUiState(category = category)
                    restart()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = GameUiState(phase = RoundPhase.ERROR, error = "The photo library could not be opened. Reinstall the complete APK.")
            }
        }
    }

    fun correct() { engine?.answer(true, now())?.let(::publish) }
    fun skip() { engine?.answer(false, now())?.let(::publish) }
    fun pause() { engine?.pause(now())?.let(::publish) }
    fun resume() { engine?.resume(now())?.let(::publish) }
    fun finish() { engine?.finish(now())?.let(::publish) }

    fun restart() {
        val category = mutableState.value.category ?: return
        tickerJob?.cancel()
        val duration = settings.value.durationSeconds
        mutableState.value = GameUiState(category = category, durationSeconds = duration, secondsRemaining = duration)
        engine = RoundEngine(category.words, duration, now())
        publish(requireNotNull(engine).tick(now()))
        tickerJob = viewModelScope.launch {
            while (isActive && mutableState.value.phase != RoundPhase.FINISHED) {
                delay(100)
                engine?.tick(now())?.let(::publish)
            }
        }
    }

    private fun publish(snapshot: RoundSnapshot) {
        mutableState.value = mutableState.value.copy(
            phase = snapshot.phase,
            word = snapshot.word,
            secondsRemaining = snapshot.secondsRemaining,
            countdown = snapshot.countdown,
            correct = snapshot.correct,
            skipped = snapshot.skipped,
            totalWords = snapshot.totalWords,
            results = snapshot.results
        )
    }

    private fun now() = SystemClock.elapsedRealtime()
}
