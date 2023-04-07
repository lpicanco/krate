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
import java.time.Clock
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MemoryStateStorageBuilder {
    fun build() = SimpleMemoryStateStorage()
}

class MemoryStateStorageWithEvictionBuilder() {
    var ttlAfterLastAccess: Duration = 2.hours
    var expirationCheckInterval: Duration = 10.minutes
    var clock: Clock = Clock.systemDefaultZone()
    var stateStorage: SimpleMemoryStateStorage = SimpleMemoryStateStorage()
    var scope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    fun build() = SimpleMemoryStateStorageWithEviction(clock, stateStorage, ttlAfterLastAccess, expirationCheckInterval, scope)
}

fun memoryStateStorage(init: MemoryStateStorageBuilder.() -> Unit = {}): StateStorage {
    return MemoryStateStorageBuilder().apply(init).build()
}

fun memoryStateStorageWithEviction(init: MemoryStateStorageWithEvictionBuilder.() -> Unit = {}): SimpleMemoryStateStorageWithEviction {
    return MemoryStateStorageWithEvictionBuilder().apply(init).build()
}
