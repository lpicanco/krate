/*
 * Copyright (c) 2022-2025, the original author or authors.
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

package com.neutrine.krate

/**
 * Interface for rate limiters implementations.
 * A rate limiter is a component that allows to limit the rate of some actions
 */
interface RateLimiter {
    /**
     * Try to take a token from the rate limiter.
     * @return true if the rate limit is not exceeded, false otherwise
     */
    suspend fun tryTake(): Boolean

    /**
     * Try to take a token for the given [key] from the rate limiter.
     * @param key the key to use to take the token
     * @return true if the rate limit is not exceeded, false otherwise
     */
    suspend fun tryTake(key: String): Boolean

    /**
     * Wait until a token is available.
     */
    suspend fun awaitUntilTake()

    /**
     * Wait until a token for the given [key] is available.
     * @param key the key to use to take the token
     */
    suspend fun awaitUntilTake(key: String)
}
