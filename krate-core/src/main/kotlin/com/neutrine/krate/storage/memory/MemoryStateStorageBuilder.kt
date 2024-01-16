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

/**
 * A builder for [MemoryStateStorage] instances.
 */
class MemoryStateStorageBuilder {
    fun build() = MemoryStateStorage()
}

/**
 * A builder for [MemoryStateStorage] instances with eviction.
 */
class MemoryStateStorageWithEvictionBuilder {
    /**
     * The time-to-live after the last access.
     */
    var ttlAfterLastAccess: Duration = 2.hours

    /**
     * The interval at which to check for expired keys.
     */
    var expirationCheckInterval: Duration = 10.minutes

    /**
     * The clock to use to get the current time.
     */
    var clock: Clock = Clock.systemDefaultZone()

    /**
     * The state storage to use to store the state.
     */
    var bucketStateMap: SimpleBucketStateMap = SimpleBucketStateMap()

    /**
     * The [CoroutineScope] to use to run the expiration check.
     */
    var coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)

    fun build() =
        MemoryStateStorage(
            SimpleBucketStateMapWithEviction(
                clock,
                bucketStateMap,
                ttlAfterLastAccess,
                expirationCheckInterval,
                coroutineScope,
            ),
        )
}

/**
 * Creates a [MemoryStateStorage] instance.
 * @param init the builder configuration
 */
fun memoryStateStorage(init: MemoryStateStorageBuilder.() -> Unit = {}): StateStorage {
    return MemoryStateStorageBuilder().apply(init).build()
}

/**
 * Creates a [MemoryStateStorage] instance with eviction.
 * @param init the builder configuration
 */
fun memoryStateStorageWithEviction(init: MemoryStateStorageWithEvictionBuilder.() -> Unit = {}): StateStorage {
    return MemoryStateStorageWithEvictionBuilder().apply(init).build()
}
