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

package com.neutrine.krate.storage

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class MemoryStateStorageTest {

    @Test
    fun `should create and return the state by key when not exists`() {
        val state = SimpleState(42)
        val storage = MemoryStateStorage<SimpleState>()

        val returnedState = storage.getOrCreate("42") { state }

        assertEquals(state, returnedState)
    }

    @Test
    fun `should return an existing state by key`() {
        val state = SimpleState(42)
        val storage = MemoryStateStorage<SimpleState>()

        storage.getOrCreate("42") { state }
        state.value = 43

        val returnedState = storage.getOrCreate("42") { SimpleState(0) }
        assertEquals(state, returnedState)
    }

    @Test
    fun `should create and return the global state when not exists`() {
        val state = SimpleState(42)
        val storage = MemoryStateStorage<SimpleState>()

        val returnedState = storage.getOrCreate(null) { state }

        assertEquals(state, returnedState)
    }

    @Test
    fun `should return an existing global state`() {
        val state = SimpleState(42)
        val storage = MemoryStateStorage<SimpleState>()

        storage.getOrCreate(null) { state }
        state.value = 43

        val returnedState = storage.getOrCreate(null) { SimpleState(0) }
        assertEquals(state, returnedState)
    }

    private data class SimpleState(var value: Int)
}
