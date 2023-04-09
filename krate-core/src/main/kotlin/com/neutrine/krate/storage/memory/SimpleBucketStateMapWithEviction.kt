/*
 * Copyright (c) 2023, the original author or authors.
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

package com.neutrine.krate.storage.memory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * A simple in-memory state map implementation that expires keys after a given time-to-live.
 * @param clock the clock to use to get the current time
 * @param stateMap the state map to use to store the state
 * @param ttlAfterLastAccess the time-to-live after the last access
 * @param expirationCheckInterval the interval at which to check for expired keys
 * @param coroutineScope the [CoroutineScope] to use to run the expiration check
 */
class SimpleBucketStateMapWithEviction(
    private val clock: Clock,
    private val stateMap: SimpleBucketStateMap = SimpleBucketStateMap(),
    private val ttlAfterLastAccess: Duration = 2.hours,
    expirationCheckInterval: Duration = 10.minutes,
    coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : BucketStateMap by stateMap {

    init {
        coroutineScope.launch {
            while (true) {
                delay(expirationCheckInterval)
                expireKeys()
            }
        }
    }

    private fun expireKeys() {
        val cutoffTime = clock.instant().minusMillis(ttlAfterLastAccess.inWholeMilliseconds)

        stateMap.state.values.removeIf {
            it.get().lastUpdated <= cutoffTime
        }
    }
}
