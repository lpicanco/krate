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
import io.mockk.clearAllMocks
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.SpyK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

@ExtendWith(MockKExtension::class)
class SimpleMemoryStateStorageWithEvictionTest {
    private val testScope = TestScope()
    private val clock = Clock.fixed(Instant.parse("2022-08-14T00:44:00Z"), ZoneOffset.UTC)
    private val ttlAfterLastAccess: Duration = 1.hours
    private val expirationCheckInterval: Duration = 10.minutes

    @SpyK
    private var baseStateStorage: SimpleMemoryStateStorage = SimpleMemoryStateStorage()

    @InjectMockKs
    private lateinit var stateStorage: SimpleMemoryStateStorageWithEviction

    @BeforeEach
    fun setup() {
        clearAllMocks()
    }

    @Test
    fun `should expire keys not accessed for more than ttlAfterLastAccess`() = runTest {
        val state42 = BucketState(10, clock.instant().minusMillis(ttlAfterLastAccess.inWholeMilliseconds - 1))
        val state410 = BucketState(10, clock.instant().minusMillis(ttlAfterLastAccess.inWholeMilliseconds))

        stateStorage.compareAndSet("42") { state42 }
        coVerify { baseStateStorage.compareAndSet("42", any()) }
        stateStorage.compareAndSet("410") { state410 }

        testScope.advanceTimeBy(expirationCheckInterval.inWholeMilliseconds + 1)

        assertEquals(state42, stateStorage.getBucketState("42"))
        verify { baseStateStorage.getBucketState("42") }
        assertNull(stateStorage.getBucketState("410"))
    }

    @Test
    fun `should not expire keys before expirationCheckInterval`() = runTest {
        val state410 = BucketState(10, clock.instant().minusMillis(ttlAfterLastAccess.inWholeMilliseconds))
        stateStorage.compareAndSet("410") { state410 }

        testScope.advanceTimeBy(expirationCheckInterval.inWholeMilliseconds)
        assertEquals(state410, stateStorage.getBucketState("410"))

        testScope.advanceTimeBy(1)
        assertNull(stateStorage.getBucketState("410"))
    }
}
