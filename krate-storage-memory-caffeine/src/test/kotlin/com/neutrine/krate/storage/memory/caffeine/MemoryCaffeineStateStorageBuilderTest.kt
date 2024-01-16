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

import com.neutrine.krate.algorithms.BucketState
import com.neutrine.krate.storage.memory.MemoryStateStorage
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Instant

class MemoryCaffeineStateStorageBuilderTest {
    @Test
    fun `should return an instance of MemoryStateStorage with CaffeineBucketStateMap`() =
        runTest {
            val stateStorage = memoryCaffeineStateStorage()
            assertTrue(stateStorage is MemoryStateStorage)

            val state = BucketState(10, Instant.now())
            stateStorage.compareAndSet("A") { state }
            stateStorage.compareAndSet("B") { state }
            stateStorage.compareAndSet("C") { state }

            assertEquals(state, stateStorage.getBucketState("A"))
            assertEquals(state, stateStorage.getBucketState("B"))
            assertEquals(state, stateStorage.getBucketState("C"))
        }
}
