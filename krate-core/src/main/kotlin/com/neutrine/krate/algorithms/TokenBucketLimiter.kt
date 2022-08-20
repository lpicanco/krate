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

package com.neutrine.krate.algorithms

import com.neutrine.krate.RateLimiter
import com.neutrine.krate.storage.MemoryStateStorage
import com.neutrine.krate.storage.StateStorage
import java.lang.Long.max
import java.lang.Long.min
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicLong

class TokenBucketLimiter(
    val capacity: Long,
    val refillTokenInterval: Duration,
    val stateStorage: StateStorage<TokenBucketState> = MemoryStateStorage(),
    private val clock: Clock
) : RateLimiter {
    override fun tryTake(): Boolean {
        val bucket = getOrCreateBucket(key = null)

        refreshTokens(bucket)

        if (bucket.remainingTokens.get() <= 0) {
            return false
        }

        takeToken(bucket)
        return true
    }

    private fun refreshTokens(bucket: TokenBucketState) {
        bucket.remainingTokens.updateAndGet { current ->
            val now = clock.instant()

            val tokensToAdd = bucket.lastUpdated.until(now, ChronoUnit.MILLIS) / refillTokenInterval.toMillis()

            if (tokensToAdd > 0) {
                bucket.lastUpdated = now
            }
            min(capacity, current + tokensToAdd)
        }
    }

    private fun takeToken(bucket: TokenBucketState): Boolean {
        return bucket.remainingTokens.getAndUpdate { current ->
            max(0, current - 1)
        } > 0
    }

    private fun getOrCreateBucket(key: String?): TokenBucketState {
        return stateStorage.getOrCreate(key) {
            TokenBucketState(AtomicLong(capacity), clock.instant())
        }
    }
}

data class TokenBucketState(
    var remainingTokens: AtomicLong,
    var lastUpdated: Instant
)
