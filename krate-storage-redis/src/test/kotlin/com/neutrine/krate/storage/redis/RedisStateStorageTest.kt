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

package com.neutrine.krate.storage.redis

import com.neutrine.krate.algorithms.BucketState
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

@Testcontainers
internal class RedisStateStorageTest {
    private val clock: Clock = Clock.fixed(Instant.parse("2022-08-14T00:44:00Z"), UTC)
    private lateinit var storage: RedisStateStorage

    @Container
    val redis: GenericContainer<*> =
        GenericContainer(DockerImageName.parse("redis:5.0.3-alpine"))
            .withExposedPorts(6379)

    @BeforeEach
    fun setup() {
        storage = RedisStateStorage(redis.host, redis.firstMappedPort)
    }

    @Test
    fun `should compare and set a new state when not exists`() =
        runTest {
            val newState = BucketState(10, clock.instant())
            storage.compareAndSet("42") { current ->
                assertNull(current)
                newState
            }

            assertEquals(newState, storage.getBucketState("42"))
        }

    @Test
    fun `should compare and set a new state when not exists concurrently`() =
        runTest {
            val key = "42"
            val newState = BucketState(10, clock.instant())
            val concurrentState = BucketState(8, clock.instant().plusSeconds(10))

            launch {
                storage.compareAndSet(key) { concurrentState }
            }
            storage.compareAndSet(key) { current ->
                advanceTimeBy(100)
                current?.copy(current.remainingTokens - 1) ?: newState
            }

            assertEquals(7, storage.getBucketState(key)?.remainingTokens)
        }

    @Test
    fun `should compare and set a new state when exists`() =
        runTest {
            val currentState = BucketState(10, clock.instant())
            val newState = BucketState(9, clock.instant().plusSeconds(10))
            storage.compareAndSet("42") { currentState }

            storage.compareAndSet("42") { current ->
                assertEquals(currentState, current)
                newState
            }

            assertEquals(newState, storage.getBucketState("42"))
        }

    @Test
    fun `should compare and set a new state concurrently`() =
        runTest {
            val key = "42"
            val currentState = BucketState(10, clock.instant())
            val concurrentState = BucketState(8, clock.instant().plusSeconds(10))
            storage.compareAndSet(key) { currentState }

            launch {
                storage.compareAndSet(key) { concurrentState }
            }

            storage.compareAndSet(key) { current ->
                advanceTimeBy(100)
                current!!.copy(current.remainingTokens - 1)
            }

            assertEquals(7, storage.getBucketState(key)?.remainingTokens)
        }

    @Test
    fun `should return the current state`() =
        runTest {
            val currentState = BucketState(10, clock.instant())
            storage.compareAndSet("42") { currentState }

            assertEquals(currentState, storage.getBucketState("42"))
        }

    @Test
    fun `should return null when the state not exists`() {
        assertNull(storage.getBucketState("404"))
    }
}
