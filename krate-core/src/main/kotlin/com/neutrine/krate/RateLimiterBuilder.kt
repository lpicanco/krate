/*
 * Copyright (c) 2022-2025, the original author or authors.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.neutrine.krate

import com.neutrine.krate.algorithms.TokenBucketLimiter
import com.neutrine.krate.storage.StateStorage
import com.neutrine.krate.storage.memory.memoryStateStorage
import java.time.Clock
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.math.roundToLong

/**
 * A builder for [RateLimiter] instances.
 * @param maxRate the maximum rate at which tokens can be consumed
 */
class RateLimiterBuilder(private val maxRate: Long) {
    /**
     * The maximum number of tokens that can be stored in the bucket. Defaults to [maxRate].
     */
    var maxBurst: Long = maxRate

    /**
     * The [TemporalUnit] of the [maxRate]. Defaults to [ChronoUnit.SECONDS].
     */
    var maxRateTimeUnit: TemporalUnit = ChronoUnit.SECONDS

    /**
     * The clock to use to get the current time. Defaults to the system clock.
     */
    var clock: Clock = Clock.systemDefaultZone()

    /**
     * The state storage to use to store the bucket state.
     */
    var stateStorage: StateStorage = memoryStateStorage()

    fun build(): RateLimiter {
        val refillTokenIntervalInMillis = (1.0 / (maxRate.toDouble() / maxRateTimeUnit.duration.seconds)) * 1000

        return TokenBucketLimiter(
            capacity = maxBurst,
            refillTokenInterval = Duration.ofMillis(refillTokenIntervalInMillis.roundToLong()),
            stateStorage = stateStorage,
            clock = clock,
        )
    }
}

/**
 * Creates a [RateLimiter] instance with the specified [maxRate].
 * @param maxRate the maximum rate at which tokens can be consumed
 * @param init the builder configuration
 *
 * To create a rate limiter that allows 5 requests per second:
 * ```kotlin
 * val rateLimiter = rateLimiter(maxRate = 5)
 * ```
 *
 * To create a rate limiter that allows 5 requests per second, expiring keys unused for 2 hours:
 * ```kotlin
 * val rateLimiter = rateLimiter(maxRate = 5) {
 *  stateStorage = memoryStateStorageWithEviction {
 *      ttlAfterLastAccess = 2.hours
 *  }
 * }
 */
fun rateLimiter(
    maxRate: Long,
    init: RateLimiterBuilder.() -> Unit = {},
): RateLimiter {
    return RateLimiterBuilder(maxRate).apply(init).build()
}
