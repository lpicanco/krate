/*
 * Copyright (c) 2022-2023, the original author or authors.
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
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference

/**
 * Simple in-memory implementation of [BucketStateMap].
 * This implementation is thread-safe and uses [ConcurrentHashMap] to store the state.
 */
class SimpleBucketStateMap : BucketStateMap {
    internal val state: ConcurrentHashMap<String, AtomicReference<BucketState>> = ConcurrentHashMap()

    override fun getBucketStateReference(key: String): AtomicReference<BucketState>? {
        return state[key]
    }

    override fun putIfAbsent(
        key: String,
        value: AtomicReference<BucketState>,
    ): AtomicReference<BucketState>? {
        return state.putIfAbsent(key, value)
    }
}
