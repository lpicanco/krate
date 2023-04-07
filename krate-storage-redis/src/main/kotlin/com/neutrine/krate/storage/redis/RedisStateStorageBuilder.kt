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

package com.neutrine.krate.storage.redis

/**
 * A builder for [RedisStateStorage] instances.
 */
class RedisStateStorageBuilder {
    /**
     * The host of the Redis server.
     */
    var host: String = "localhost"

    /**
     * The port of the Redis server.
     */
    var port: Int = 6379

    fun build() = RedisStateStorage(host, port)
}

/**
 * Creates a [RedisStateStorage] instance.
 */
fun redisStateStorage(init: RedisStateStorageBuilder.() -> Unit): RedisStateStorage {
    return RedisStateStorageBuilder().apply(init).build()
}
