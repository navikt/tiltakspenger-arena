val javaVersion = JavaVersion.VERSION_17
val prometheusVersion = "0.15.0"
val cxfVersion = "3.5.2"

plugins {
    application
    id("java")
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.gitlab.arturbosch.detekt") version "1.20.0"
    id("ca.cutterslade.analyze") version "1.9.0"
    id("com.github.bjornvester.wsdl2java") version "1.2"
    //id("io.mateo.cxf-codegen") version "1.0.1"
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

    // cxfCodegen("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    // cxfCodegen("jakarta.annotation:jakarta.annotation-api:1.3.5")
    // cxfCodegen("ch.qos.logback:logback-classic:1.2.10")

    // implementation("org.apache.cxf:cxf-bom:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-frontend-jaxws:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-frontend-jaxrs:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-transports-http:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-rs-client:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-rs-service-description:$cxfVersion")
    // implementation("com.sun.activation:jakarta.activation:2.0.1")
    // implementation("org.apache.cxf:cxf-rt-ws-policy:$cxfVersion")
    // implementation("org.apache.cxf:cxf-rt-ws-security:$cxfVersion")
    // implementation("javax.activation:activation:1.1.1")
    // implementation("no.nav.helse:cxf-prometheus-metrics:dd7d125")
    implementation("org.apache.cxf:cxf-rt-features-logging:$cxfVersion")

    implementation("no.nav.common:cxf:2.2022.05.31_07.13-5812471780dc")
    implementation("jakarta.activation:jakarta.activation-api:1.2.2")
    implementation("jakarta.annotation:jakarta.annotation-api:1.3.5")
    implementation("jakarta.jws:jakarta.jws-api:2.1.0")
    implementation("jakarta.validation:jakarta.validation-api:2.0.2")
    implementation("jakarta.xml.bind:jakarta.xml.bind-api:2.3.3")
    implementation("jakarta.xml.soap:jakarta.xml.soap-api:1.4.2")
    implementation("jakarta.xml.ws:jakarta.xml.ws-api:2.3.3")
    // implementation("io.github.threeten-jaxb:threeten-jaxb-core:1.2")
    // implementation("javax.annotation:javax.annotation-api:1.3.2")
    // implementation("jakarta.annotation:jakarta.annotation-api:2.1.0")

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
    // bindingFile.set(layout.projectDirectory.file("src/main/resources/bindings/bindings.xml"))
    cxfVersion.set("3.5.2")
}

/*
tasks.register<Wsdl2Java>("ytelser") {
    toolOptions {
        wsdl.set(file("src/main/resources/wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/ytelseskontrakt/v3/Binding.wsdl"))
        outputDir.set(file("$buildDir/generated/java"))
        markGenerated.set(true)
    }
}

tasks.register<Wsdl2Java>("tiltak") {
    toolOptions {
        wsdl.set(file("src/main/resources/wsdl/tjenestespesifikasjon/no/nav/tjeneste/virksomhet/tiltakogaktivitet/v1/Binding.wsdl"))
        outputDir.set(file("$buildDir/generated/java"))
        markGenerated.set(true)
    }
}
 */

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
