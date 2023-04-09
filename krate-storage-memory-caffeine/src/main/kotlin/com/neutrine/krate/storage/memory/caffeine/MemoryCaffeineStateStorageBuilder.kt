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

package com.neutrine.krate.storage.memory.caffeine

import com.neutrine.krate.storage.StateStorage
import com.neutrine.krate.storage.memory.MemoryStateStorage
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours

/**
 * A builder for [CaffeineBucketStateMap] instances.
 */
class MemoryCaffeineStateStorageBuilder {
    /**
     * The maximum size of the cache.
     */
    var maximumSize: Long = 50_000

    /**
     * The port of the Redis server.
     */
    var expireAfterAccess: Duration = 2.hours

    fun build() = MemoryStateStorage(CaffeineBucketStateMap(maximumSize, expireAfterAccess))
}

/**
 * Creates a [CaffeineBucketStateMap] instance.
 */
fun memoryCaffeineStateStorage(init: MemoryCaffeineStateStorageBuilder.() -> Unit = {}): StateStorage {
    return MemoryCaffeineStateStorageBuilder().apply(init).build()
}
