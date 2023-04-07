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

import com.neutrine.krate.algorithms.BucketState
import com.neutrine.krate.storage.StateStorage
import kotlinx.coroutines.delay
import redis.clients.jedis.JedisPool
import java.time.Instant

/**
 * A [StateStorage] implementation using Redis.
 *
 * @param host The Redis host.
 * @param port The Redis port.
 */
class RedisStateStorage(
    val host: String,
    val port: Int
) : StateStorage {
    private val redisPool: JedisPool = JedisPool(host, port)

    override fun getBucketState(key: String): BucketState? {
        return redisPool.resource.use { jedis ->
            jedis.hgetAll(computeKey(key)).toBucketState()
        }
    }

    override suspend fun compareAndSet(key: String, compareAndSetFunction: (current: BucketState?) -> BucketState): Unit =
        redisPool.resource.use { jedis ->
            val computedKey = computeKey(key)
            jedis.watch(computedKey)
            val currentState = getBucketState(key)
            val newState = compareAndSetFunction(currentState)
            val transaction = jedis.multi()

            transaction.hset(computedKey, newState.toMap())

            if (transaction.exec() == null) {
                delay(StateStorage.DEFAULT_RETRY_DELAY)
                return compareAndSet(key, compareAndSetFunction)
            }
        }

    private fun computeKey(key: String) = "krate:instance:$key"

    private fun BucketState.toMap(): Map<String, String> = mapOf(
        KEY_REMAINING_TOKENS to remainingTokens.toString(),
        KEY_LAST_UPDATED to lastUpdated.toEpochMilli().toString()
    )

    private fun Map<String, String>.toBucketState(): BucketState? {
        val remainingTokens: Long? = this["remainingTokens"]?.toLongOrNull()
        val lastUpdated: Instant? = this["lastUpdated"]?.toLongOrNull()?.let {
            Instant.ofEpochMilli(it)
        }

        if (remainingTokens == null || lastUpdated == null) {
            return null
        }

        return BucketState(remainingTokens, lastUpdated)
    }

    companion object {
        private const val KEY_REMAINING_TOKENS: String = "remainingTokens"
        private const val KEY_LAST_UPDATED: String = "lastUpdated"
    }
}
