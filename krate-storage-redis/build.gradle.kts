dependencies {
    implementation(project(":krate-core"))
    implementation("redis.clients:jedis:4.2.3")
    testImplementation("org.testcontainers:testcontainers:1.17.3")
    testImplementation("org.testcontainers:junit-jupiter:1.17.3")
}
