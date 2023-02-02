val javaVersion = JavaVersion.VERSION_17
val cxfVersjon = "3.5.5"
val ktorVersion = "2.2.3"
val jacksonVersion = "2.14.2"
val mockkVersion = "1.13.4"
val kotlinxCoroutinesVersion = "1.6.4"

project.base.archivesName.set("app")

plugins {
    application
    id("java")
    kotlin("jvm") version "1.8.10"
    id("com.diffplug.spotless") version "6.13.0"
    id("com.github.bjornvester.wsdl2java") version "1.2"
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    // implementation("com.github.navikt:tiltakspenger-libs:0.0.7")
    implementation("com.github.navikt.tiltakspenger-libs:arenatiltak-dtos:0.0.21")
    implementation("com.github.navikt.tiltakspenger-libs:arenaytelser-dtos:0.0.20")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutinesVersion")
    implementation("com.github.navikt:rapids-and-rivers:2022122311551671792919.2bdd972d7bdb")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains:annotations:24.0.0")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("io.github.microutils:kotlin-logging-jvm:3.0.4")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jvm:$ktorVersion")
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
    // old version because of https://github.com/bjornvester/wsdl2java-gradle-plugin#configure-binding-files
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2")
    implementation("no.nav.common:cxf:2.2023.01.09_08.56-ae38750bc0d9")
    constraints {
        implementation("commons-collections:commons-collections") {
            version {
                require("3.2.2")
            }
            because("https://security.snyk.io/vuln/SNYK-JAVA-COMMONSCOLLECTIONS-30078")
        }
    }
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:1.4.2")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersjon")
    implementation("org.apache.cxf:cxf-core:$cxfVersjon")

    runtimeOnly("org.apache.cxf:cxf-rt-features-metrics:$cxfVersjon")
    runtimeOnly("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
    // old version because of https://issues.apache.org/jira/browse/CXF-8727
    runtimeOnly("jakarta.annotation:jakarta.annotation-api:1.3.5")

    testImplementation(platform("org.junit:junit-bom:5.9.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("org.skyscreamer:jsonassert:1.5.1")
    testImplementation("org.junit-pioneer:junit-pioneer:1.9.1")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.1")
    testImplementation("org.hamcrest:hamcrest-core:2.2")
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
        ktlint("0.45.2")
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
