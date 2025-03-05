import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

val javaVersion = JavaVersion.VERSION_21
val jvmVersion = JvmTarget.JVM_21

val ktorVersion = "3.1.1"
val jacksonVersion = "2.18.3"
val mockkVersion = "1.13.17"
val kotlinxCoroutinesVersion = "1.10.1"
val testContainersVersion = "1.20.5"
val felleslibVersion = "0.0.387"
val kotestVersion = "5.9.1"

project.base.archivesName.set("app")

plugins {
    application
    id("java")
    kotlin("jvm") version "2.1.10"
    id("com.diffplug.spotless") version "7.0.2"
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
    maven {
        url = uri("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
    }
    maven("https://plugins.gradle.org/m2/")
    maven("https://build.shibboleth.net/maven/releases/")
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation("com.github.navikt.tiltakspenger-libs:arenatiltak-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:arenaytelser-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:periodisering:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:logging:$felleslibVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains:annotations:26.0.2")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("ch.qos.logback:logback-classic:1.5.17")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")

    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-apache:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("com.zaxxer:HikariCP:6.2.1")
    implementation("com.oracle.database.jdbc:ojdbc11:23.7.0.25.01")
    implementation("com.github.seratch:kotliquery:1.9.1")

    implementation("org.slf4j:jul-to-slf4j:2.0.17")
    implementation("org.slf4j:log4j-over-slf4j:2.0.17")
    implementation("org.slf4j:jcl-over-slf4j:2.0.17")

    testImplementation(platform("org.junit:junit-bom:5.12.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.xmlunit:xmlunit-matchers:2.10.0")
    testImplementation("org.hamcrest:hamcrest-core:3.0")
    testImplementation("org.flywaydb:flyway-database-oracle:11.3.4")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:oracle-free:$testContainersVersion")
    // need quarkus-junit-4-mock because of https://github.com/testcontainers/testcontainers-java/issues/970
    testImplementation("io.quarkus:quarkus-junit4-mock:3.19.2")

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions:$kotestVersion")

    testImplementation("no.nav.security:mock-oauth2-server:2.1.10")
}

configurations.all {
    // exclude JUnit 4
    exclude(group = "junit", module = "junit")
}

java {
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}

spotless {
    kotlin {
        ktlint()
            .editorConfigOverride(
                mapOf(
                    "ktlint_standard_max-line-length" to "off",
                    "ktlint_standard_function-signature" to "disabled",
                    "ktlint_standard_function-expression-body" to "disabled",
                ),
            )
    }
}

tasks {
    compileJava {
    }
    kotlin {
        compilerOptions {
            jvmTarget.set(jvmVersion)
            freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
        }
    }
    test {
        // JUnit 5 support
        useJUnitPlatform()
        // https://phauer.com/2018/best-practices-unit-testing-kotlin/
        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
        testLogging {
            // We only want to log failed and skipped tests when running Gradle.
            events("skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    jar {
        manifest.attributes["Main-Class"] = "no.nav.tiltakspenger.arena.ApplicationKt"
        manifest.attributes["Class-Path"] = configurations
            .runtimeClasspath
            .get()
            .joinToString(separator = " ") { file -> "${file.name}" }
    }
    register<Copy>("gitHooks") {
        from(file(".scripts/pre-commit"))
        into(file(".git/hooks"))
    }
}
