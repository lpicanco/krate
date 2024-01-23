dependencies {
    implementation(project(":krate-core"))
    implementation("redis.clients:jedis:4.4.0")
    testImplementation("org.testcontainers:testcontainers:1.19.3")
    testImplementation("org.testcontainers:junit-jupiter:1.19.3")
}
