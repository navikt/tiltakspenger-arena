val javaVersion = JavaVersion.VERSION_17
val prometheusVersion = "0.15.0"
val cxfVersion = "3.5.2"
val ktorVersion = "2.0.2"
val jacksonVersion = "2.13.3"

project.base.archivesName.set("app")

plugins {
    application
    id("java")
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
//    id("ca.cutterslade.analyze") version "1.9.0"
    id("com.github.bjornvester.wsdl2java") version "1.2"
//    id("com.github.ben-manes.versions") version "0.42.0"
}

repositories {
    mavenCentral()
    maven("https://packages.confluent.io/maven/")
    maven("https://jitpack.io")
    maven("https://plugins.gradle.org/m2/")
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.20.0")
    // Align versions of all Kotlin components
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation(kotlin("stdlib"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:1.6.3")
//    implementation("io.prometheus:simpleclient:$prometheusVersion")
//    implementation("io.prometheus:simpleclient_common:$prometheusVersion")
    implementation("com.github.navikt:rapids-and-rivers:2022061809451655538329.d6deccc62862")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.slf4j:slf4j-api:1.7.36")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.natpryce:konfig:1.6.10.0")

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
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")

    // old version because of https://github.com/bjornvester/wsdl2java-gradle-plugin#configure-binding-files
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2")
    implementation("no.nav.common:cxf:2.2022.06.30_12.48-dc5391b28f60")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:1.4.2")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-core:$cxfVersion")

    runtimeOnly("org.apache.cxf:cxf-rt-features-metrics:$cxfVersion")
//    implementation("com.sun.activation:jakarta.activation:2.0.1")
    runtimeOnly("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
//    runtimeOnly("jakarta.activation:jakarta.activation-api:1.2.2")
//    runtimeOnly("jakarta.jws:jakarta.jws-api:2.1.0")
//    implementation("jakarta.validation:jakarta.validation-api:2.0.2")
//    runtimeOnly("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    // old version because of https://issues.apache.org/jira/browse/CXF-8727
    runtimeOnly("jakarta.annotation:jakarta.annotation-api:1.3.5")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("io.mockk:mockk-dsl-jvm:1.12.4")
//    testImplementation("org.skyscreamer:jsonassert:1.5.0")
    testImplementation("org.junit-pioneer:junit-pioneer:1.7.1")
    testImplementation("io.ktor:ktor-client-mock-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-core-jvm:$ktorVersion")
    testImplementation("io.ktor:ktor-server-content-negotiation-jvm:$ktorVersion")
    testImplementation("org.xmlunit:xmlunit-matchers:2.9.0")
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

detekt {
    autoCorrect = true
    buildUponDefaultConfig = true
    allRules = false
    config = files("$projectDir/config/detekt.yml")
}

wsdl2java {
    wsdlDir.set(layout.projectDirectory.dir("src/main/resources/wsdl"))
    cxfVersion.set("3.5.2")
    bindingFile.set(layout.projectDirectory.file("src/main/resources/bindings/bindings.xml"))
}

java.sourceSets["main"].java {
    srcDir("build/generated/sources/wsdl2java/java")
}

// https://github.com/ben-manes/gradle-versions-plugin
//fun isNonStable(version: String): Boolean {
//    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
//    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
//    val isStable = stableKeyword || regex.matches(version)
//    return isStable.not()
//}

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
    // https://github.com/ben-manes/gradle-versions-plugin
//    dependencyUpdates {
//        rejectVersionIf {
//            isNonStable(candidate.version)
//        }
//    }
//    analyzeClassesDependencies {
//        warnUsedUndeclared = true
//        warnUnusedDeclared = true
//    }
//    analyzeTestClassesDependencies {
//        warnUsedUndeclared = true
//        warnUnusedDeclared = true
//    }
}
