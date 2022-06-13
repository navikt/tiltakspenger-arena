val javaVersion = JavaVersion.VERSION_17
val prometheusVersion = "0.15.0"
val cxfVersion = "3.5.2"

plugins {
    application
    id("java")
    kotlin("jvm") version "1.7.0"
    kotlin("plugin.serialization") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("ca.cutterslade.analyze") version "1.9.0"
    id("com.github.bjornvester.wsdl2java") version "1.2"
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
    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("no.nav.common:rest:2.2022.05.31_07.13-5812471780dc")
    //implementation("no.nav.common:xml:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:sts:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:util:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:client:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:auth:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:log:2.2022.05.31_07.13-5812471780dc")
    implementation("no.nav.common:health:2.2022.05.31_07.13-5812471780dc")

    // https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/jackson-dataformat-xml
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-xml:2.13.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.13.3")

    // Not quite sure if I need all of these
    implementation("no.nav.common:cxf:2.2022.05.31_07.13-5812471780dc")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")
    implementation("org.apache.cxf:cxf-rt-features-metrics:$cxfVersion")
    implementation("com.sun.activation:jakarta.activation:1.2.2")
    implementation("com.sun.xml.messaging.saaj:saaj-impl:1.5.3")
    implementation("jakarta.activation:jakarta.activation-api:1.2.2")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")
    implementation("jakarta.jws:jakarta.jws-api:2.1.0")
    implementation("jakarta.validation:jakarta.validation-api:2.0.2")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:1.4.2")
    implementation("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")

    testImplementation(platform("org.junit:junit-bom:5.8.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("io.mockk:mockk:1.12.4")
    testImplementation("io.mockk:mockk-dsl-jvm:1.12.4")
    testImplementation("org.skyscreamer:jsonassert:1.5.0")
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
    }
    shadowJar {
        dependsOn("test")
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer::class.java)
        // https://github.com/johnrengelman/shadow/issues/309
        transform(com.github.jengelman.gradle.plugins.shadow.transformers.AppendingTransformer::class.java) {
            resource = "META-INF/cxf/bus-extensions.txt"
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
