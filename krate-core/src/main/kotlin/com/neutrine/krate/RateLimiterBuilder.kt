/*
 * Copyright (c) 2022, the original author or authors.
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
import com.neutrine.krate.algorithms.TokenBucketState
import com.neutrine.krate.storage.MemoryStateStorage
import com.neutrine.krate.storage.StateStorage
import java.time.Clock
import java.time.Duration
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalUnit
import kotlin.math.roundToLong

class RateLimiterBuilder(private val maxRate: Long) {
    var maxBurst: Long = maxRate
    var maxRateTimeUnit: TemporalUnit = ChronoUnit.SECONDS
    var stateStorage: StateStorage<TokenBucketState> = MemoryStateStorage()
    var clock: Clock = Clock.systemDefaultZone()
    fun build(): RateLimiter {
        val refillTokenIntervalInMillis = (1.0 / (maxRate.toDouble() / maxRateTimeUnit.duration.seconds)) * 1000

        return TokenBucketLimiter(
            capacity = maxBurst,
            refillTokenInterval = Duration.ofMillis(refillTokenIntervalInMillis.roundToLong()),
            stateStorage = stateStorage,
            clock = clock
        )
    }
}

fun rateLimiter(maxRate: Long, init: RateLimiterBuilder.() -> Unit): RateLimiter {
    return RateLimiterBuilder(maxRate).apply(init).build()
}
