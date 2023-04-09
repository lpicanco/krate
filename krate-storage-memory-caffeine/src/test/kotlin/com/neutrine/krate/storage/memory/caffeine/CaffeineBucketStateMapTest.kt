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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration.Companion.hours

class CaffeineBucketStateMapTest {
    private lateinit var stateMap: CaffeineBucketStateMap

    @BeforeEach
    fun setup() {
        stateMap = CaffeineBucketStateMap(10, 2.hours)
    }

    @Test
    fun `should return the bucket reference`() {
        val state = AtomicReference(BucketState(10, Instant.now()))
        stateMap.putIfAbsent("42", state)

        assertEquals(state, stateMap.getBucketStateReference("42"))
    }

    @Test
    fun `should only put if absent`() {
        val state = AtomicReference(BucketState(10, Instant.now()))

        var oldValue = stateMap.putIfAbsent("42", state)
        assertNull(oldValue)

        oldValue = stateMap.putIfAbsent("42", AtomicReference(BucketState(34, Instant.now())))
        assertEquals(state, oldValue)
        assertEquals(state, stateMap.getBucketStateReference("42"))
    }
}
