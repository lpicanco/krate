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

import com.neutrine.krate.algorithms.BucketState
import java.time.Duration
import kotlin.time.toKotlinDuration

interface StateStorage {
    fun getBucketState(key: String): BucketState?
    suspend fun compareAndSet(key: String, compareAndSetFunction: (current: BucketState?) -> BucketState)

    companion object {
        val DEFAULT_RETRY_DELAY = Duration.ofMillis(100L).toKotlinDuration()
    }
}
