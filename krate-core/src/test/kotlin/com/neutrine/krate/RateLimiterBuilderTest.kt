package com.neutrine.krate

import com.neutrine.krate.algorithms.TokenBucketLimiter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

internal class RateLimiterBuilderTest {

    @Test
    fun `should return an instance of TokenBucketLimiter with a rate of 60 per minute`() {
        val rateLimiter = rateLimiter {
            maxBurst = 70
            maxRate = 60
            maxRateTimeUnit = ChronoUnit.MINUTES
        }

        assertTrue(rateLimiter is TokenBucketLimiter)
        val tokenBucketLimiter = rateLimiter as TokenBucketLimiter

        assertEquals(70, tokenBucketLimiter.capacity)
        assertEquals(Duration.ofSeconds(1), tokenBucketLimiter.refillTokenInterval)
    }

    @Test
    fun `should return an instance of TokenBucketLimiter with a rate of 5 per minute`() {
        val rateLimiter = rateLimiter {
            maxBurst = 70
            maxRate = 5
            maxRateTimeUnit = ChronoUnit.MINUTES
        }

        assertTrue(rateLimiter is TokenBucketLimiter)
        val tokenBucketLimiter = rateLimiter as TokenBucketLimiter

        assertEquals(70, tokenBucketLimiter.capacity)
        assertEquals(Duration.ofSeconds(12), tokenBucketLimiter.refillTokenInterval)
    }

    @Test
    fun `should return an instance of TokenBucketLimiter with a rate of 5 per second`() {
        val rateLimiter = rateLimiter {
            maxBurst = 10
            maxRate = 5
            maxRateTimeUnit = ChronoUnit.SECONDS
        }

        assertTrue(rateLimiter is TokenBucketLimiter)
        val tokenBucketLimiter = rateLimiter as TokenBucketLimiter

        assertEquals(10, tokenBucketLimiter.capacity)
        assertEquals(Duration.ofMillis(200), tokenBucketLimiter.refillTokenInterval)
    }

    @Test
    fun `should return an instance of TokenBucketLimiter a rate of 30 per second`() {
        val rateLimiter = rateLimiter {
            maxBurst = 50
            maxRate = 30
            maxRateTimeUnit = ChronoUnit.SECONDS
        }

        assertTrue(rateLimiter is TokenBucketLimiter)
        val tokenBucketLimiter = rateLimiter as TokenBucketLimiter

        assertEquals(50, tokenBucketLimiter.capacity)
        assertEquals(Duration.ofMillis(33), tokenBucketLimiter.refillTokenInterval)
    }

    @Test
    fun `should return an instance of TokenBucketLimiter a rate of 5 per hour`() {
        val rateLimiter = rateLimiter {
            maxBurst = 5
            maxRate = 5
            maxRateTimeUnit = ChronoUnit.HOURS
        }

        assertTrue(rateLimiter is TokenBucketLimiter)
        val tokenBucketLimiter = rateLimiter as TokenBucketLimiter

        assertEquals(5, tokenBucketLimiter.capacity)
        assertEquals(Duration.ofMinutes(12), tokenBucketLimiter.refillTokenInterval)
    }
}
