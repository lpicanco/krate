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

import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import com.neutrine.krate.algorithms.BucketState
import com.neutrine.krate.storage.memory.BucketStateMap
import java.util.concurrent.atomic.AtomicReference
import kotlin.time.Duration
import kotlin.time.toJavaDuration

/**
 * Simple in-memory implementation of [BucketStateMap] using [Caffeine] as cache.
 */
class CaffeineBucketStateMap(
    maximumSize: Long,
    expireAfterAccess: Duration
) : BucketStateMap {
    private val cache: Cache<String, AtomicReference<BucketState>> = Caffeine.newBuilder()
        .maximumSize(maximumSize)
        .expireAfterAccess(expireAfterAccess.toJavaDuration())
        .build()

    override fun getBucketStateReference(key: String): AtomicReference<BucketState>? {
        return cache.getIfPresent(key)
    }

    override fun putIfAbsent(key: String, value: AtomicReference<BucketState>): AtomicReference<BucketState>? {
        return cache.asMap().putIfAbsent(key, value)
    }
}
