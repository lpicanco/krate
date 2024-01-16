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
import java.util.concurrent.atomic.AtomicReference

/**
 * Interface the provides a map implementation to store the state of the rate limiter.
 */
interface BucketStateMap {
    /**
     * Returns the [AtomicReference] of the [BucketState] for the given [key].
     * If the [key] is not present in the map, it returns `null`.
     */
    fun getBucketStateReference(key: String): AtomicReference<BucketState>?

    /**
     * Puts the [value] in the map if the [key] is not present.
     * If the [key] is already present, it returns the [AtomicReference] of the [BucketState] for the given [key].
     * If the [key] is not present in the map, it returns `null`.
     */
    fun putIfAbsent(
        key: String,
        value: AtomicReference<BucketState>,
    ): AtomicReference<BucketState>?
}
