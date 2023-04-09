# Krate
Krate is a rate limiter library for Kotlin.

[![Kotlin](https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
![GitHub](https://img.shields.io/github/license/lpicanco/krate)
![Build Status](https://img.shields.io/github/actions/workflow/status/lpicanco/krate/jdk11.yml?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lpicanco_krate&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=lpicanco_krate)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=lpicanco_krate&metric=coverage)](https://sonarcloud.io/summary/new_code?id=lpicanco_krate)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.lpicanco/krate-core)](https://search.maven.org/artifact/io.github.lpicanco/krate-core)


## Features
1. Token Bucket Algorithm: Krate uses the token bucket algorithm, a widely-used technique for rate limiting, to ensure efficient and fair resource allocation.
2. Burst Support: Krate allows for bursty request patterns, letting clients make a certain number of requests in a short time before being rate-limited.
3. Redis Support: Krate offers support for Redis as a backend for storing rate limit information, enabling distributed rate limiting across multiple instances of your application.
4. Keys eviction support: Krate automatically evicts unused keys, optimizing memory usage and ensuring optimal performance.

## Installation
### Gradle
```groovy
implementation 'io.github.lpicanco:krate-core:${krateVersion}'
```

### Gradle Kotlin DSL
```kotlin
implementation("io.github.lpicanco:krate-core:${krateVersion}")
```

### Maven
```xml
<dependency>
  <groupId>io.github.lpicanco</groupId>
  <artifactId>krate-core</artifactId>
  <version>${krateVersion}</version>
</dependency>
```

## How to use

### Rate of 5 per second
```kotlin
import com.neutrine.krate.rateLimiter

// Create a rate limiter with a rate of 5 per second.
val rateLimiter = rateLimiter(maxRate = 5)

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake()
```

### Rate of 10 per minute with a max burst of 10.
```kotlin
// Create a rate limiter with a rate of 5 per minute and a max burst of 10.
val rateLimiter = rateLimiter(maxRate = 5) {
    maxBurst = 10
    maxRateTimeUnit = ChronoUnit.MINUTES
}

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake()
```

### Rate of 5 per second, blocking
```kotlin
// Create a rate limiter with a rate of 5 per second.
val rateLimiter = rateLimiter(maxRate = 5)

// Wait until a token is available.
rateLimiter.awaitUntilTake()
```

### Rate of 5 per second, expiring keys unused in the last 2 hours
```kotlin
import com.neutrine.krate.rateLimiter

// Create a rate limiter with a rate of 5 per second. Unused keys will be evicted after 2 hours.
val rateLimiter = rateLimiter(maxRate = 5) {
    stateStorage = memoryStateStorageWithEviction {
        ttlAfterLastAccess = 2.hours
    }    
}

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake("myKey")
```

### Rate of 10 per second, using caffeine to evict keys unused in the last 2 hours
```kotlin
import com.neutrine.krate.rateLimiter

// Create a rate limiter with a rate of 5 per second. Unused keys will be evicted after 2 hours.
val rateLimiter = rateLimiter(maxRate = 10) {
    stateStorage = memoryCaffeineStateStorage {
        expireAfterAccess = 2.hours
    }
}

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake("myKey")
```

### Rate of 5 per second using Redis as state storage
```kotlin
// Create a rate limiter with a rate of 5 per second and redis.
val rateLimiter = rateLimiter(maxRate = 5) {
    stateStorage = redisStateStorage {
        host = "my-custom-uri.endpoint"
    }
}

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake()
```
