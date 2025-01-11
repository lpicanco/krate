dependencies {
    implementation(project(":krate-core"))
    implementation("redis.clients:jedis:5.1.3")
    testImplementation("org.testcontainers:testcontainers:1.19.8")
    testImplementation("org.testcontainers:junit-jupiter:1.20.4")
}
