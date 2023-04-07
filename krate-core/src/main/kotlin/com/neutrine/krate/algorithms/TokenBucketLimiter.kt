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
import com.neutrine.krate.storage.StateStorage
import com.neutrine.krate.storage.memory.SimpleMemoryStateStorage
import kotlinx.coroutines.delay
import java.lang.Long.max
import java.lang.Long.min
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.time.Duration.Companion.milliseconds

class TokenBucketLimiter(
    val capacity: Long,
    val refillTokenInterval: Duration,
    private val clock: Clock,
    private val stateStorage: StateStorage = SimpleMemoryStateStorage()
) : RateLimiter {
    override suspend fun tryTake(): Boolean {
        return tryTakeFromState(null)
    }

    override suspend fun tryTake(key: String): Boolean {
        return tryTakeFromState(key)
    }

    override suspend fun awaitUntilTake() {
        while (!tryTake()) {
            delay(POLLING_DELAY)
        }
    }

    override suspend fun awaitUntilTake(key: String) {
        while (!tryTake(key)) {
            delay(POLLING_DELAY)
        }
    }

    private suspend fun tryTakeFromState(key: String?): Boolean {
        var hasTokens = true
        stateStorage.compareAndSet(key.orEmpty()) { current ->
            if (current == null) {
                BucketState(capacity - 1, clock.instant())
            } else {
                val now = clock.instant()
                val tokensToAdd = current.lastUpdated.until(now, ChronoUnit.MILLIS) / refillTokenInterval.toMillis()
                val totalTokens = min(capacity, current.remainingTokens + tokensToAdd)
                val lastUpdated = if (tokensToAdd > 0) now else current.lastUpdated
                hasTokens = totalTokens > 0

                current.copy(
                    remainingTokens = max(0, totalTokens - 1),
                    lastUpdated = lastUpdated
                )
            }
        }

        return hasTokens
    }

    companion object {
        private val POLLING_DELAY = 100.milliseconds
    }
}

data class BucketState(
    val remainingTokens: Long,
    val lastUpdated: Instant
)
