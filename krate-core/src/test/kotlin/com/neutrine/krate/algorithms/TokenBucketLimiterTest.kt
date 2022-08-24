package com.neutrine.krate.algorithms

import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Duration
import java.time.Instant

internal class TokenBucketLimiterTest {
    private val clock: Clock = mockk()
    private var currentTime = Instant.parse("2022-08-14T00:44:00Z")

    @BeforeEach
    fun setup() {
        every { clock.instant() } answers { currentTime }
    }

    @Nested
    inner class TryTakeTest {
        @Test
        fun `should return true if there are remaining tokens`() {
            val tokenBucket = TokenBucketLimiter(1, Duration.ofMinutes(1), clock = clock)
            assertTrue(tokenBucket.tryTake())
        }

        @Test
        fun `should return false if there are no remaining tokens`() {
            val tokenBucket = TokenBucketLimiter(1, Duration.ofMinutes(1), clock = clock)
            assertTrue(tokenBucket.tryTake())
            currentTime = currentTime.plusSeconds(10)
            assertFalse(tokenBucket.tryTake())
        }

        @Test
        fun `should restore all tokens after the full refill interval`() {
            val tokenBucket = TokenBucketLimiter(2L, Duration.ofMinutes(1), clock = clock)

            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plus(Duration.ofMinutes(2))

            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())
        }

        @Test
        fun `should restore one token after refill interval`() {
            val tokenBucket = TokenBucketLimiter(2L, Duration.ofMinutes(1), clock = clock)

            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plusSeconds(60)

            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())
        }

        @Test
        fun `should return true if taking between refill intervals`() {
            val tokenBucket = TokenBucketLimiter(3L, Duration.ofMinutes(1), clock = clock)

            assertTrue(tokenBucket.tryTake())
            currentTime = currentTime.plusSeconds(59)
            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plusSeconds(1)

            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plus(Duration.ofMinutes(1))
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plus(Duration.ofMinutes(2))
            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())

            currentTime = currentTime.plus(Duration.ofMinutes(3))
            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertTrue(tokenBucket.tryTake())
            assertFalse(tokenBucket.tryTake())
        }
    }

    @Nested
    inner class AwaitUntilTakeTest {
        @Test
        fun `should not await if there are remaining tokens`() = runTest {
            val tokenBucket = TokenBucketLimiter(1, Duration.ofMinutes(1), clock = clock)
            withTimeout(100) {
                tokenBucket.awaitUntilTake()
            }
        }

        @Test
        fun `should await until one token is available`() = runTest {
            val tokenBucket = TokenBucketLimiter(1L, Duration.ofMinutes(1), clock = clock)
            var completed = false

            launch {
                tokenBucket.awaitUntilTake()
                tokenBucket.awaitUntilTake()
                completed = true
            }

            withTimeout(100) {
                advanceTimeBy(1000)
                assertFalse(completed)
                currentTime = currentTime.plusSeconds(60)
                advanceTimeBy(1000)

                assertTrue(completed)
            }
        }
    }
}
