import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0"
    id("org.sonarqube") version "3.5.0.2730"
    jacoco
    id("maven-publish")
    id("signing")
    id("io.github.gradle-nexus.publish-plugin") version "1.0.0"
}

allprojects {
    group = "com.neutrine.krate"
    version = "1.0"

    repositories {
        mavenCentral()
    }
}

sonarqube {
    properties {
        property("sonar.projectKey", "lpicanco_krate")
        property("sonar.organization", "lpicanco")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.junit.reportPaths", "build/test-results/test")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
    }
}

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "jacoco")
    apply(plugin = "maven-publish")
    apply(plugin = "signing")

    sonarqube {
        properties {
            property("sonar.sources", "src")
            property("sonar.junit.reportPaths", "build/test-results/test")
        }
    }

    dependencies {
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")

        testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
        testImplementation("io.mockk:mockk:1.12.5")
        testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    }

    tasks.withType<Test> {
        useJUnitPlatform()
        finalizedBy(tasks.jacocoTestReport)
    }

    configure<JavaPluginExtension> {
        withSourcesJar()
        withJavadocJar()
    }

    tasks.jacocoTestReport {
        reports {
            xml.required.set(true)
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
    }

    configure<PublishingExtension> {
        publications {
            create<MavenPublication>("maven") {
                groupId = "io.github.lpicanco"
                artifactId = project.name
                version = project.version.toString()

                from(components["java"])

                pom {
                    name.set("$groupId:${project.name}")
                    description.set("Rate Limit Library for Kotlin.")
                    url.set("https://github.com/lpicanco/krate")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("lpicanco")
                            name.set("Luiz Picanço")
                            email.set("lpicanco@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:https://github.com/lpicanco/krate.git")
                        developerConnection.set("scm:git:https://github.com/lpicanco/krate.git")
                        url.set("https://github.com/lpicanco/krate")
                    }
                }
            }
        }

        repositories {
            maven {
                val releaseRepo = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                val snapshotRepo = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")

                name = "OSSRH"
                url = if (isReleaseVersion) releaseRepo else snapshotRepo

                credentials {
                    username = findProperty("ossrhUsername")?.toString() ?: System.getenv("OSSRH_USERNAME")
                    password = findProperty("ossrhPassword")?.toString() ?: System.getenv("OSSRH_PASSWORD")
                }
            }
        }
    }

    configure<SigningExtension> {
        sign(project.extensions.getByType<PublishingExtension>().publications["maven"])
    }

    tasks.withType<Sign> {
        onlyIf { isReleaseVersion }
    }
}
