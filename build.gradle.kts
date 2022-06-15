val javaVersion = JavaVersion.VERSION_17
val prometheusVersion = "0.15.0"
val cxfVersion = "3.5.2"
val ktorVersion = "2.0.2"
val jacksonVersion = "2.13.3"

plugins {
    application
    id("java")
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("ca.cutterslade.analyze") version "1.9.0"
    id("com.github.bjornvester.wsdl2java") version "1.2"
    id("com.github.ben-manes.versions") version "0.42.0"
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
    implementation("io.prometheus:simpleclient:$prometheusVersion")
    implementation("io.prometheus:simpleclient_common:$prometheusVersion")
    implementation("com.github.navikt:rapids-and-rivers:2022060808531654671206.908d671b7ae0")
    implementation("io.github.microutils:kotlin-logging-jvm:2.1.23")
    implementation("org.jetbrains:annotations:23.0.0")
    implementation("com.natpryce:konfig:1.6.10.0")

//    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-core-jvm:$ktorVersion")
//    implementation("io.ktor:ktor-client-cio:$ktorVersion")
    implementation("io.ktor:ktor-client-cio-jvm:$ktorVersion")
//    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation-jvm:$ktorVersion")
//    implementation("io.ktor:ktor-serialization-jackson:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jvm:$ktorVersion")
    implementation("io.ktor:ktor-utils-jvm:$ktorVersion")
    implementation("io.ktor:ktor-http-jvm:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-xml:$ktorVersion")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")

    // Not quite sure if I need all of these
    implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2")
    implementation("no.nav.common:cxf:2.2022.05.31_07.13-5812471780dc")
    implementation("org.apache.cxf:cxf-core:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-metrics:$cxfVersion")
    implementation("com.sun.activation:jakarta.activation:1.2.2")
    implementation("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
    implementation("jakarta.activation:jakarta.activation-api:1.2.2")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")
    implementation("jakarta.jws:jakarta.jws-api:2.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:1.4.2")
    implementation("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("io.mockk:mockk-dsl-jvm:1.12.4")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
    testImplementation("org.junit-pioneer:junit-pioneer:1.7.1")
}

configurations.all {
    // exclude JUnit 4
    exclude(group = "junit", module = "junit")
}

application {
    mainClass.set("no.nav.tiltakspenger.arena.ApplicationKt")
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
fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.toUpperCase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
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
    }
    shadowJar {
        dependsOn("test")
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
        // https://github.com/johnrengelman/shadow/issues/309
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer::class.java) {
            resource = "META-INF/cxf/bus-extensions.txt"
        }
    }
    // https://github.com/ben-manes/gradle-versions-plugin
    dependencyUpdates {
        rejectVersionIf {
            isNonStable(candidate.version)
        }
    }
    analyzeClassesDependencies {
        warnUsedUndeclared = true
        warnUnusedDeclared = true
    }
    analyzeTestClassesDependencies {
        warnUsedUndeclared = true
        warnUnusedDeclared = true
    }
}
