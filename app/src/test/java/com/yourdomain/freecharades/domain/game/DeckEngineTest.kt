package com.yourdomain.freecharades.domain.game

import com.yourdomain.freecharades.domain.model.Word
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class DeckEngineTest {
    private val words = (1..5).map { Word("word_$it", "Prompt $it", "images/$it.jpg") }
    private fun engine(saved: DeckSessionState? = null, now: Long = 0) = DeckEngine(words, now, saved, Random(7))

    @Test fun firstPictureRevealsOnlyAfterThreeSeconds() {
        val game = engine()
        assertEquals(3, game.snapshot(0).countdown)
        assertEquals(2, game.snapshot(1000).countdown)
        assertEquals(1, game.snapshot(2999).countdown)
        assertEquals(GamePhase.REVEAL, game.snapshot(3000).phase)
    }

    @Test fun nextAlwaysStartsNewCountdownAndResetsManualCounter() {
        val game = engine()
        game.incrementGuesses(3000)
        assertEquals(2, game.incrementGuesses(3001).guessCount)
        val next = game.next(4000)
        assertEquals(GamePhase.COUNTDOWN, next.phase)
        assertEquals(3, next.countdown)
        assertEquals(0, next.guessCount)
        assertEquals(1, next.index)
        assertEquals(GamePhase.REVEAL, game.snapshot(7000).phase)
    }

    @Test fun countdownRejectsNextAndCounterChanges() {
        val game = engine()
        assertEquals(0, game.next(100).index)
        assertEquals(0, game.incrementGuesses(200).guessCount)
        assertEquals(0, game.decrementGuesses(300).guessCount)
    }

    @Test fun counterClampsAtZeroWithoutAnArtificialMaximum() {
        val game = engine()
        assertEquals(0, game.decrementGuesses(3000).guessCount)
        repeat(1000) { game.incrementGuesses(3000) }
        assertEquals(1000, game.snapshot(3000).guessCount)
        assertEquals(999, game.decrementGuesses(3000).guessCount)
    }

    @Test fun counterSaturatesInsteadOfOverflowingAtIntegerLimit() {
        val game = engine(DeckSessionState(words.map { it.id }, 0, Int.MAX_VALUE, 0))
        assertEquals(Int.MAX_VALUE, game.incrementGuesses(0).guessCount)
        assertEquals(Int.MAX_VALUE - 1, game.decrementGuesses(0).guessCount)
    }

    @Test fun allPicturesAppearOnceAndExhaustionDoesNotReshuffle() {
        val game = engine()
        val played = mutableListOf<String>()
        repeat(words.size) { index ->
            val now = (index + 1) * 3000L
            played += requireNotNull(game.snapshot(now).word).id
            game.next(now)
        }
        val exhausted = game.snapshot(20000)
        assertEquals(words.size, played.distinct().size)
        assertEquals(words.map { it.id }.toSet(), played.toSet())
        assertEquals(GamePhase.EXHAUSTED, exhausted.phase)
        assertNull(exhausted.word)
        assertEquals(words.size, game.next(21000).index)
    }

    @Test fun savedStateRestoresOrderPictureAndGuessCounter() {
        val original = engine()
        original.next(3000)
        original.incrementGuesses(6000)
        original.incrementGuesses(6000)
        val saved = original.save()
        val restored = engine(saved, 6100)
        assertEquals(saved.order, restored.save().order)
        assertEquals(original.snapshot(6100), restored.snapshot(6100))
        assertEquals(2, restored.snapshot(6100).guessCount)
    }

    @Test fun lockedOrKilledProcessDoesNotPauseCountdown() {
        val original = engine()
        val restoredDuringCountdown = engine(original.save(), 1200)
        assertEquals(2, restoredDuringCountdown.snapshot(1200).countdown)
        val restoredAfterCountdown = engine(original.save(), 60000)
        assertEquals(GamePhase.REVEAL, restoredAfterCountdown.snapshot(60000).phase)
    }

    @Test fun changedOrInvalidCatalogSnapshotStartsAFreshSession() {
        val invalid = DeckSessionState(listOf("missing"), 500, -1, 1000)
        val fresh = engine(invalid, 5000).snapshot(5000)
        assertEquals(GamePhase.COUNTDOWN, fresh.phase)
        assertEquals(0, fresh.index)
        assertEquals(0, fresh.guessCount)
    }

    @Test fun rebootDoesNotWaitForOldMonotonicDeadline() {
        val saved = DeckSessionState(words.map { it.id }, 2, 3, 900000)
        assertEquals(GamePhase.REVEAL, engine(saved, 1000).snapshot(1000).phase)
    }

    @Test fun freshSessionShuffleContainsExactlyTheCatalog() {
        val first = DeckEngine(words, 0, random = Random(1)).save().order
        val second = DeckEngine(words, 0, random = Random(2)).save().order
        assertEquals(words.map { it.id }.toSet(), first.toSet())
        assertEquals(first.toSet(), second.toSet())
        assertTrue(first != second)
    }

    @Test(expected = IllegalArgumentException::class)
    fun emptyDeckIsRejected() { DeckEngine(emptyList(), 0) }

    @Test(expected = IllegalArgumentException::class)
    fun duplicateIdsAreRejected() { DeckEngine(words + words.first(), 0) }
}
