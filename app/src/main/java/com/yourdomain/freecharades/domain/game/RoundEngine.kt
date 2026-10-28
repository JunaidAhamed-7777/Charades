package com.yourdomain.freecharades.domain.game

import com.yourdomain.freecharades.domain.model.Word
import kotlin.math.ceil
import kotlin.random.Random

enum class RoundPhase { LOADING, COUNTDOWN, PLAYING, PAUSED, FINISHED, ERROR }

data class RoundResult(val word: Word, val guessed: Boolean)

data class RoundSnapshot(
    val phase: RoundPhase,
    val word: Word?,
    val secondsRemaining: Int,
    val countdown: Int,
    val results: List<RoundResult>,
    val totalWords: Int
) {
    val correct: Int get() = results.count { it.guessed }
    val skipped: Int get() = results.count { !it.guessed }
}

/** A monotonic-clock state machine. Each prompt can contribute at most one result per round. */
class RoundEngine(
    words: List<Word>,
    private val durationSeconds: Int,
    nowMs: Long,
    random: Random = Random.Default
) {
    private val deck = words.distinctBy { it.id }.shuffled(random)
    private var index = 0
    private var phase = RoundPhase.COUNTDOWN
    private var deadlineMs = nowMs + COUNTDOWN_MS
    private var pausedPhase = RoundPhase.PLAYING
    private var pausedRemainingMs = 0L
    private val results = mutableListOf<RoundResult>()

    init {
        require(deck.isNotEmpty()) { "Cannot start an empty deck" }
        require(durationSeconds > 0) { "Round duration must be positive" }
    }

    fun tick(nowMs: Long): RoundSnapshot {
        if (phase == RoundPhase.COUNTDOWN && nowMs >= deadlineMs) {
            phase = RoundPhase.PLAYING
            deadlineMs += durationSeconds * 1000L
        }
        if (phase == RoundPhase.PLAYING && nowMs >= deadlineMs) phase = RoundPhase.FINISHED
        return snapshot(nowMs)
    }

    fun answer(guessed: Boolean, nowMs: Long): RoundSnapshot {
        tick(nowMs)
        if (phase == RoundPhase.PLAYING) {
            results += RoundResult(deck[index], guessed)
            index++
            if (index >= deck.size) phase = RoundPhase.FINISHED
        }
        return snapshot(nowMs)
    }

    fun pause(nowMs: Long): RoundSnapshot {
        tick(nowMs)
        if (phase == RoundPhase.COUNTDOWN || phase == RoundPhase.PLAYING) {
            pausedPhase = phase
            pausedRemainingMs = (deadlineMs - nowMs).coerceAtLeast(0)
            phase = RoundPhase.PAUSED
        }
        return snapshot(nowMs)
    }

    fun resume(nowMs: Long): RoundSnapshot {
        if (phase == RoundPhase.PAUSED) {
            deadlineMs = nowMs + pausedRemainingMs
            phase = pausedPhase
        }
        return tick(nowMs)
    }

    fun finish(nowMs: Long): RoundSnapshot {
        phase = RoundPhase.FINISHED
        return snapshot(nowMs)
    }

    private fun snapshot(nowMs: Long): RoundSnapshot {
        val remainingMs = if (phase == RoundPhase.PAUSED) pausedRemainingMs else (deadlineMs - nowMs).coerceAtLeast(0)
        val countingDown = phase == RoundPhase.COUNTDOWN || (phase == RoundPhase.PAUSED && pausedPhase == RoundPhase.COUNTDOWN)
        return RoundSnapshot(
            phase = phase,
            word = deck.getOrNull(index),
            secondsRemaining = when {
                countingDown -> durationSeconds
                phase == RoundPhase.FINISHED && index < deck.size -> 0
                else -> ceil(remainingMs / 1000.0).toInt().coerceIn(0, durationSeconds)
            },
            countdown = if (countingDown) ceil(remainingMs / 1000.0).toInt().coerceIn(0, 3) else 0,
            results = results.toList(),
            totalWords = deck.size
        )
    }

    companion object { private const val COUNTDOWN_MS = 3000L }
}
