# krate
Rate Limit Library for Kotlin


## How to use

### Rate of 5 per second
```kotlin
import com.neutrine.krate.rateLimiter

// Create a rate limiter with a rate of 5 per second.
val rateLimiter = rateLimiter(maxRate = 5)

// Use the rate limiter.
rateLimiter.tryTake()
```

### Rate of 10 per minute with a max burst of 10.
```kotlin
import com.neutrine.krate.rateLimiter
import java.time.temporal.ChronoUnit

// Create a rate limiter with a rate of 10 per minute and a max burst of 10.
val rateLimiter = rateLimiter(maxRate = 5) {
    maxBurst = 10
    maxRateTimeUnit = ChronoUnit.MINUTES
}

// Use the rate limiter.
rateLimiter.tryTake()
```
