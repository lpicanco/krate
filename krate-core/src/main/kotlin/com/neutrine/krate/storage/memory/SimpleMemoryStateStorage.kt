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

package com.neutrine.krate.storage.memory

import com.neutrine.krate.algorithms.BucketState
import com.neutrine.krate.storage.StateStorage
import com.neutrine.krate.storage.StateStorage.Companion.DEFAULT_RETRY_DELAY
import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

class SimpleMemoryStateStorage : StateStorage {

    internal val state: ConcurrentHashMap<String, AtomicReference<BucketState>> = ConcurrentHashMap()

    override fun getBucketState(key: String): BucketState? {
        return state[key]?.get()
    }

    override suspend fun compareAndSet(key: String, compareAndSetFunction: (current: BucketState?) -> BucketState) {
        val currentState = state[key]
        val currentStateValue = currentState?.get()
        val newStateValue = compareAndSetFunction(currentStateValue)

        val putResult = state.putIfAbsent(key, AtomicReference(newStateValue))

        // Check if an item was added or updated after currentState read
        if ((currentState == null && putResult != null) ||
            currentState?.compareAndSet(currentStateValue, newStateValue) == false
        ) {
            delay(DEFAULT_RETRY_DELAY)
            return compareAndSet(key, compareAndSetFunction)
        }
    }
}
