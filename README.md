# krate
Rate Limit Library for Kotlin


## How to use

```kotlin
import com.neutrine.krate.rateLimiter
import java.time.temporal.ChronoUnit

// Create a rate limiter with a rate of 5 per second and a max burst of 10.
val rateLimiter = rateLimiter {
    maxBurst = 10
    maxRate = 5
    maxRateTimeUnit = ChronoUnit.SECONDS
}

// Use the rate limiter.
rateLimiter.tryTake()
```
