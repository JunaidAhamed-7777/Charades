package com.yourdomain.freecharades.domain.game

import com.yourdomain.freecharades.domain.model.Word
import kotlin.math.ceil
import kotlin.random.Random

enum class GamePhase { LOADING, COUNTDOWN, REVEAL, ERROR, EXHAUSTED }

/** The small amount of session state Android saves for screen/process recreation. */
data class DeckSessionState(
    val order: List<String>,
    val index: Int,
    val guessCount: Int,
    val countdownDeadlineMs: Long
)

data class DeckSnapshot(
    val phase: GamePhase,
    val word: Word?,
    val countdown: Int,
    val guessCount: Int,
    val index: Int,
    val totalWords: Int
)

/** One shuffle per category encounter, then a three-second reveal before every picture. */
class DeckEngine(
    words: List<Word>,
    nowMs: Long,
    restored: DeckSessionState? = null,
    random: Random = Random.Default
) {
    private val deck: List<Word>
    private var index: Int
    private var guessCount: Int
    private var countdownDeadlineMs: Long

    init {
        require(words.isNotEmpty()) { "Cannot start an empty deck" }
        require(words.map { it.id }.distinct().size == words.size) { "Prompt IDs must be unique" }
        val sourceById = words.associateBy { it.id }
        val saved = restored?.takeIf {
            it.order.size == words.size && it.order.toSet() == sourceById.keys &&
                it.index in 0..words.size && it.guessCount >= 0 && it.countdownDeadlineMs >= 0
        }
        deck = saved?.order?.map { sourceById.getValue(it) } ?: words.shuffled(random)
        index = saved?.index ?: 0
        guessCount = saved?.guessCount ?: 0
        // elapsedRealtime survives process recreation. If Android rebooted, reveal rather than
        // waiting for a deadline from the previous boot's much larger monotonic clock.
        countdownDeadlineMs = saved?.countdownDeadlineMs?.takeIf { it <= nowMs + COUNTDOWN_MS }
            ?: if (saved == null) nowMs + COUNTDOWN_MS else 0
    }

    fun snapshot(nowMs: Long): DeckSnapshot {
        if (countdownDeadlineMs > 0 && nowMs >= countdownDeadlineMs) countdownDeadlineMs = 0
        val phase = when {
            index >= deck.size -> GamePhase.EXHAUSTED
            countdownDeadlineMs > 0 -> GamePhase.COUNTDOWN
            else -> GamePhase.REVEAL
        }
        return DeckSnapshot(
            phase = phase,
            word = deck.getOrNull(index),
            countdown = if (phase == GamePhase.COUNTDOWN) ceil((countdownDeadlineMs - nowMs) / 1000.0).toInt().coerceIn(1, 3) else 0,
            guessCount = guessCount,
            index = index,
            totalWords = deck.size
        )
    }

    fun next(nowMs: Long): DeckSnapshot {
        if (snapshot(nowMs).phase == GamePhase.REVEAL) {
            index++
            guessCount = 0
            countdownDeadlineMs = if (index < deck.size) nowMs + COUNTDOWN_MS else 0
        }
        return snapshot(nowMs)
    }

    fun incrementGuesses(nowMs: Long): DeckSnapshot {
        if (snapshot(nowMs).phase == GamePhase.REVEAL && guessCount < Int.MAX_VALUE) guessCount++
        return snapshot(nowMs)
    }

    fun decrementGuesses(nowMs: Long): DeckSnapshot {
        if (snapshot(nowMs).phase == GamePhase.REVEAL && guessCount > 0) guessCount--
        return snapshot(nowMs)
    }

    fun save() = DeckSessionState(deck.map { it.id }, index, guessCount, countdownDeadlineMs)

    companion object { private const val COUNTDOWN_MS = 3000L }
}
