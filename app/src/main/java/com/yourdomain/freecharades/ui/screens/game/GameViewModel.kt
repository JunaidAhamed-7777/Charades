package com.yourdomain.freecharades.ui.screens.game

import android.app.Application
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.yourdomain.freecharades.data.repository.CategoryRepository
import com.yourdomain.freecharades.domain.game.DeckEngine
import com.yourdomain.freecharades.domain.game.DeckSessionState
import com.yourdomain.freecharades.domain.game.DeckSnapshot
import com.yourdomain.freecharades.domain.game.GamePhase
import com.yourdomain.freecharades.domain.model.Category
import com.yourdomain.freecharades.domain.model.Word
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.random.Random

data class GameUiState(
    val phase: GamePhase = GamePhase.LOADING,
    val category: Category? = null,
    val word: Word? = null,
    val countdown: Int = 3,
    val guessCount: Int = 0,
    val index: Int = 0,
    val totalWords: Int = 0,
    val error: String? = null
)

class GameViewModel(application: Application, private val savedStateHandle: SavedStateHandle) : AndroidViewModel(application) {
    private val categories = CategoryRepository.get(application)
    private val mutableState = MutableStateFlow(GameUiState())
    val state = mutableState.asStateFlow()
    private var engine: DeckEngine? = null
    private var loadJob: Job? = null
    private var tickerJob: Job? = null

    fun prepare(categoryId: String, sessionId: String) {
        val sameSession = savedStateHandle.get<String>(CATEGORY_KEY) == categoryId &&
            savedStateHandle.get<String>(SESSION_KEY) == sessionId
        if (sameSession && (engine != null || loadJob?.isActive == true)) return
        val restored = if (sameSession) restoreSession() else null
        savedStateHandle[CATEGORY_KEY] = categoryId
        savedStateHandle[SESSION_KEY] = sessionId
        loadJob?.cancel()
        tickerJob?.cancel()
        engine = null
        mutableState.value = GameUiState()
        loadJob = viewModelScope.launch {
            try {
                val category = categories.load().firstOrNull { it.id == categoryId }
                if (category == null) {
                    mutableState.value = GameUiState(phase = GamePhase.ERROR, error = "This category could not be found. Choose another category.")
                } else {
                    mutableState.value = GameUiState(category = category)
                    // The detail encounter's navigation key also reproduces its shuffle before
                    // Android has had a chance to save the complete ordered list.
                    engine = DeckEngine(category.words, now(), restored, Random(sessionId.hashCode()))
                    refresh()
                    runCountdown()
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableState.value = GameUiState(phase = GamePhase.ERROR, error = "The photo library could not be opened. Reinstall the complete APK.")
            }
        }
    }

    fun next() {
        engine?.next(now())?.let(::publish)
        runCountdown()
    }

    fun incrementGuesses() { engine?.incrementGuesses(now())?.let(::publish) }
    fun decrementGuesses() { engine?.decrementGuesses(now())?.let(::publish) }

    /** Recompute from elapsed time on resume; locking the screen never pauses the countdown. */
    fun refresh() { engine?.snapshot(now())?.let(::publish) }

    private fun runCountdown() {
        tickerJob?.cancel()
        if (mutableState.value.phase != GamePhase.COUNTDOWN) return
        tickerJob = viewModelScope.launch {
            while (isActive && mutableState.value.phase == GamePhase.COUNTDOWN) {
                delay(50)
                refresh()
            }
        }
    }

    private fun publish(snapshot: DeckSnapshot) {
        mutableState.value = mutableState.value.copy(
            phase = snapshot.phase,
            word = snapshot.word,
            countdown = snapshot.countdown,
            guessCount = snapshot.guessCount,
            index = snapshot.index,
            totalWords = snapshot.totalWords
        )
        engine?.save()?.let { saved ->
            savedStateHandle[ORDER_KEY] = ArrayList(saved.order)
            savedStateHandle[INDEX_KEY] = saved.index
            savedStateHandle[GUESSES_KEY] = saved.guessCount
            savedStateHandle[DEADLINE_KEY] = saved.countdownDeadlineMs
        }
    }

    private fun restoreSession(): DeckSessionState? {
        val order = savedStateHandle.get<ArrayList<String>>(ORDER_KEY) ?: return null
        return DeckSessionState(
            order = order.toList(),
            index = savedStateHandle[INDEX_KEY] ?: 0,
            guessCount = savedStateHandle[GUESSES_KEY] ?: 0,
            countdownDeadlineMs = savedStateHandle[DEADLINE_KEY] ?: 0L
        )
    }

    private fun now() = SystemClock.elapsedRealtime()

    private companion object {
        const val CATEGORY_KEY = "deck_category"
        const val SESSION_KEY = "deck_session"
        const val ORDER_KEY = "deck_order"
        const val INDEX_KEY = "deck_index"
        const val GUESSES_KEY = "deck_guesses"
        const val DEADLINE_KEY = "deck_deadline"
    }
}
