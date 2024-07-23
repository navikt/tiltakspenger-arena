val javaVersion = JavaVersion.VERSION_21

val cxfVersjon = "4.0.4"
val ktorVersion = "2.3.12"
val jacksonVersion = "2.17.2"
val mockkVersion = "1.13.12"
val kotlinxCoroutinesVersion = "1.8.1"
val tokenSupportVersion = "5.0.1"
val testContainersVersion = "1.20.0"
val felleslibVersion = "0.0.159"
val kotestVersion = "5.9.1"

project.base.archivesName.set("app")

plugins {
    application
    id("java")
    kotlin("jvm") version "2.0.0"
    id("com.diffplug.spotless") version "6.25.0"
    id("com.github.bjornvester.wsdl2java") version "2.0.2"
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
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    // implementation("com.github.navikt:tiltakspenger-libs:0.0.7")
    implementation("com.google.guava:guava:33.2.1-jre")
    implementation("com.github.navikt.tiltakspenger-libs:arenatiltak-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:arenaytelser-dtos:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:periodisering:$felleslibVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutinesVersion")
    implementation("com.github.navikt:rapids-and-rivers:2024061408021718344972.64ebbdb34321")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains:annotations:24.1.0")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.4")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.5")
    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-utils-jvm:$ktorVersion")
    implementation("io.ktor:ktor-http-jvm:$ktorVersion")
    implementation("io.ktor:ktor-io-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-logging-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("no.nav.security:token-validation-ktor-v2:$tokenSupportVersion")
    implementation("no.nav.security:token-client-core:$tokenSupportVersion")
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:2.2.0")
    implementation("no.nav.common:cxf:3.2024.05.23_05.46-2b29fa343e8e")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api") {
        version {
            strictly("3.0.1")
        }
    }
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:3.0.2")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersjon")
    implementation("org.apache.cxf:cxf-core:$cxfVersjon")

    //implementation("org.flywaydb:flyway-core:9.19.3")
    implementation("org.flywaydb:flyway-database-oracle:10.16.0")
    implementation("com.zaxxer:HikariCP:5.1.0")
    implementation("com.oracle.database.jdbc:ojdbc8:23.4.0.24.05") //TODO: Er denne riktig?
    implementation("com.github.seratch:kotliquery:1.9.0")


    runtimeOnly("org.apache.cxf:cxf-rt-features-metrics:$cxfVersjon")
    runtimeOnly("com.sun.xml.messaging.saaj:saaj-impl:3.0.4")
    // old version because of https://issues.apache.org/jira/browse/CXF-8727
    runtimeOnly("jakarta.annotation:jakarta.annotation-api:3.0.0")

    implementation("org.slf4j:jul-to-slf4j:2.0.13")
    implementation("org.slf4j:log4j-over-slf4j:2.0.13")
    implementation("org.slf4j:jcl-over-slf4j:2.0.13")

    testImplementation(platform("org.junit:junit-bom:5.10.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("org.skyscreamer:jsonassert:1.5.3")
    testImplementation("org.junit-pioneer:junit-pioneer:2.2.0")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    testImplementation("org.xmlunit:xmlunit-matchers:2.10.0")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:oracle-xe:1.20.0")
    // need quarkus-junit-4-mock because of https://github.com/testcontainers/testcontainers-java/issues/970
    testImplementation("io.quarkus:quarkus-junit4-mock:3.12.3")

    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions:$kotestVersion")

    testImplementation("no.nav.security:mock-oauth2-server:2.1.8")
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
        ktlint("0.48.2")
    }
}

wsdl2java {
    wsdlDir.set(layout.projectDirectory.dir("src/main/resources/wsdl"))
    cxfVersion.set("$cxfVersjon")
    bindingFile.set(layout.projectDirectory.file("src/main/resources/bindings/bindings.xml"))
}

java.sourceSets["main"].java {
    srcDir("build/generated/sources/wsdl2java/java")
}

tasks {
    compileJava {
    }
    compileKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = javaVersion.toString()
        kotlinOptions.freeCompilerArgs += "-opt-in=kotlin.RequiresOptIn"
    }
    test {
        // JUnit 5 support
        useJUnitPlatform()
        // https://phauer.com/2018/best-practices-unit-testing-kotlin/
        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
    }
    jar {
        manifest.attributes["Main-Class"] = "no.nav.tiltakspenger.arena.ApplicationKt"
        manifest.attributes["Class-Path"] = configurations
            .runtimeClasspath
            .get()
            .joinToString(separator = " ") { file -> "${file.name}" }
    }
}

task("addPreCommitGitHookOnBuild") {
    println("⚈ ⚈ ⚈ Running Add Pre Commit Git Hook Script on Build ⚈ ⚈ ⚈")
    exec {
        commandLine("cp", "./.scripts/pre-commit", "./.git/hooks")
    }
    println("✅ Added Pre Commit Git Hook Script.")
}
