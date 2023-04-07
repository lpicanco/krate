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

import com.neutrine.krate.storage.StateStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class SimpleMemoryStateStorageWithEviction(
    private val clock: Clock,
    private val stateStorage: SimpleMemoryStateStorage = SimpleMemoryStateStorage(),
    private val ttlAfterLastAccess: Duration = 2.hours,
    expirationCheckInterval: Duration = 10.minutes,
    scope: CoroutineScope = CoroutineScope(Dispatchers.Default)
) : StateStorage by stateStorage {

    init {
        scope.launch {
            while (true) {
                delay(expirationCheckInterval)
                expireKeys()
            }
        }
    }

    private fun expireKeys() {
        val cutoffTime = clock.instant().minusMillis(ttlAfterLastAccess.inWholeMilliseconds)

        stateStorage.state.values.removeIf {
            it.get().lastUpdated <= cutoffTime
        }
    }
}