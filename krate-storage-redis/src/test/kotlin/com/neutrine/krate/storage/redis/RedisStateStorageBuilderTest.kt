package com.neutrine.krate.storage.redis

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class RedisStateStorageBuilderTest {
    @Test
    fun `should return an instance of RedisStateStorageBuilder with default values`() {
        val stateStorage: RedisStateStorage = redisStateStorage {}

        assertEquals("localhost", stateStorage.host)
        assertEquals(6379, stateStorage.port)
    }

    @Test
    fun `should return an instance of RedisStateStorageBuilder with custom values`() {
        val stateStorage: RedisStateStorage =
            redisStateStorage {
                host = "127.0.0.1"
                port = 4242
            }

        assertEquals("127.0.0.1", stateStorage.host)
        assertEquals(4242, stateStorage.port)
    }
}
