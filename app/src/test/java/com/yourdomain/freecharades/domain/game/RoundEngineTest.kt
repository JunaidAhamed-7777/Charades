package com.yourdomain.freecharades.domain.game

import com.yourdomain.freecharades.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class RoundEngineTest {
    private val words = (1..5).map { Word("word_$it", "Prompt $it", "images/$it.jpg") }
    private fun engine(deck: List<Word> = words, seconds: Int = 60) = RoundEngine(deck, seconds, 0, Random(7))

    @Test fun countdownCannotScoreAndStartsWithFullDuration() {
        val round = engine()
        assertEquals(3, round.tick(0).countdown)
        assertEquals(1, round.tick(2999).countdown)
        assertEquals(0, round.answer(true, 2999).correct)
        val playing = round.tick(3000)
        assertEquals(RoundPhase.PLAYING, playing.phase)
        assertEquals(60, playing.secondsRemaining)
    }

    @Test fun delayedTimerTickDoesNotExtendRound() {
        val round = engine()
        assertEquals(57, round.tick(6500).secondsRemaining)
        assertEquals(RoundPhase.FINISHED, round.tick(63000).phase)
    }

    @Test fun allPromptsAppearOnceAndDeckExhaustionEndsRound() {
        val round = engine()
        round.tick(3000)
        repeat(words.size) { round.answer(it % 2 == 0, 4000L + it) }
        val finished = round.tick(5000)
        assertEquals(RoundPhase.FINISHED, finished.phase)
        assertNull(finished.word)
        assertEquals(words.map { it.id }.toSet(), finished.results.map { it.word.id }.toSet())
        assertEquals(words.size, finished.results.size)
        assertEquals(3, finished.correct)
        assertEquals(2, finished.skipped)
    }

    @Test fun duplicatePromptIdsNeverEnterDeckTwice() {
        val round = engine(words + words.first())
        assertEquals(words.size, round.tick(0).totalWords)
    }

    @Test fun responseAtDeadlineCannotChangeScore() {
        val round = engine(seconds = 30)
        round.tick(3000)
        assertEquals(1, round.answer(true, 32999).correct)
        val expired = round.answer(true, 33000)
        assertEquals(RoundPhase.FINISHED, expired.phase)
        assertEquals(1, expired.correct)
        assertEquals(0, expired.secondsRemaining)
    }

    @Test fun pausedRoundFreezesTimeAndRejectsAnswers() {
        val round = engine()
        round.tick(3000)
        val paused = round.pause(13500)
        assertEquals(50, paused.secondsRemaining)
        assertEquals(RoundPhase.PAUSED, round.tick(200000).phase)
        assertTrue(round.answer(true, 200000).results.isEmpty())
        assertEquals(50, round.resume(200000).secondsRemaining)
        assertEquals(49, round.tick(201000).secondsRemaining)
        assertEquals(RoundPhase.FINISHED, round.tick(249500).phase)
    }

    @Test fun countdownCanPauseAndResumeWithoutConsumingRoundTime() {
        val round = engine()
        assertEquals(2, round.pause(1250).countdown)
        assertEquals(2, round.resume(10000).countdown)
        assertEquals(RoundPhase.COUNTDOWN, round.tick(11749).phase)
        val started = round.tick(11750)
        assertEquals(RoundPhase.PLAYING, started.phase)
        assertEquals(60, started.secondsRemaining)
    }

    @Test fun pauseAtExpiredDeadlineFinishesInsteadOfExtendingRound() {
        assertEquals(RoundPhase.FINISHED, engine().pause(100000).phase)
    }

    @Test fun finishedRoundCannotResumeOrRecordMoreAnswers() {
        val round = engine()
        round.tick(3000)
        round.answer(true, 4000)
        round.finish(4001)
        assertEquals(RoundPhase.FINISHED, round.resume(4002).phase)
        assertEquals(1, round.answer(false, 4003).results.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyDeckIsRejected() { engine(emptyList()) }

    @Test(expected = IllegalArgumentException::class)
    fun nonpositiveDurationIsRejected() { engine(seconds = 0) }
}
