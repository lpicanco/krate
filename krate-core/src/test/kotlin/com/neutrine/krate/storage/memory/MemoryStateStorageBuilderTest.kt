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

import com.neutrine.krate.algorithms.BucketState
import io.mockk.coVerify
import io.mockk.spyk
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

class MemoryStateStorageBuilderTest {

    @Test
    fun `should return an instance of SimpleMemoryStateStorage`() {
        val simpleMemoryStateStorage = memoryStateStorage()
        assertTrue(simpleMemoryStateStorage is SimpleMemoryStateStorage)
    }

    @Test
    fun `should return an instance of SimpleMemoryStateStorageWithEviction with default values`() {
        val simpleMemoryStateStorage = memoryStateStorageWithEviction()
        assertTrue(simpleMemoryStateStorage is SimpleMemoryStateStorageWithEviction)
    }

    @Test
    fun `should return an instance of SimpleMemoryStateStorageWithEviction with a custom clock, stateStorage, ttl of 2h and checkInterval of 30m`() = runTest {
        val ttl = 2.hours
        val checkInterval = 30.minutes
        val fixedClock = Clock.fixed(Instant.parse("2023-04-07T11:00:00Z"), ZoneOffset.UTC)
        val customStateStorage = spyk<SimpleMemoryStateStorage>()
        val testScope = TestScope()

        val stateStorage = memoryStateStorageWithEviction {
            clock = fixedClock
            ttlAfterLastAccess = ttl
            expirationCheckInterval = checkInterval
            stateStorage = customStateStorage
            scope = testScope
        }

        assertTrue(stateStorage is SimpleMemoryStateStorageWithEviction)

        val state42 = BucketState(10, fixedClock.instant().minusMillis(ttl.inWholeMilliseconds - 1))
        val state410 = BucketState(10, fixedClock.instant().minusMillis(ttl.inWholeMilliseconds))
        stateStorage.compareAndSet("42") { state42 }
        coVerify { customStateStorage.compareAndSet("42", any()) }
        stateStorage.compareAndSet("410") { state410 }

        testScope.advanceTimeBy(checkInterval.inWholeMilliseconds + 1)

        assertEquals(state42, stateStorage.getBucketState("42"))
        assertNull(stateStorage.getBucketState("410"))
    }
}
