dependencies {
    implementation(project(":krate-core"))
    implementation("redis.clients:jedis:5.2.0")
    testImplementation("org.testcontainers:testcontainers:1.20.4")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
}
