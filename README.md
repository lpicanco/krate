# krate
Rate Limit Library for Kotlin


## How to use

### Rate of 5 per second
```kotlin
import com.neutrine.krate.rateLimiter

// Create a rate limiter with a rate of 5 per second.
val rateLimiter = rateLimiter(maxRate = 5) {}

// Use the rate limiter.
val taken: Boolean = rateLimiter.tryTake()
```

### Rate of 10 per minute with a max burst of 10.
```kotlin
// Create a rate limiter with a rate of 10 per minute and a max burst of 10.
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
val rateLimiter = rateLimiter(maxRate = 5) {}

// Wait until a token is available.
rateLimiter.awaitUntilTake()
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
