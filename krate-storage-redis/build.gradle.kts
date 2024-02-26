dependencies {
    implementation(project(":krate-core"))
    implementation("redis.clients:jedis:5.1.1")
    testImplementation("org.testcontainers:testcontainers:1.19.5")
    testImplementation("org.testcontainers:junit-jupiter:1.19.5")
}
