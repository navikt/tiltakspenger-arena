import kotlinx.kover.gradle.plugin.dsl.AggregationType
import kotlinx.kover.gradle.plugin.dsl.CoverageUnit
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

val ktorVersion = "3.4.3"
val jacksonVersion = "3.2.0"
val jacksonAnnotationsVersion = "2.22"
val mockkVersion = "1.14.11"
val kotlinxCoroutinesVersion = "1.11.0"
val testContainersVersion = "2.0.5"
val felleslibVersion = "0.0.20260718174757"
val kotestVersion = "6.2.2"

plugins {
    application
    id("java")
    kotlin("jvm") version "2.4.0"
    id("com.diffplug.spotless") version "8.8.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
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

    // Lås alle io.netty:* til samme versjon som forsikring mot fremtidig 4.1/4.2-drift.
    // ktor-server-netty drar inn netty 4.2.x; en BOM hindrer at en transitiv avhengighet
    // senere blander inn 4.1.x og legger duplikate baseklasser på classpath (jf. `-cp lib/*`).
    implementation(platform("io.netty:netty-bom:4.2.16.Final"))
    implementation("com.github.navikt.tiltakspenger-libs:common:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:periodisering:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:logging:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:texas:$felleslibVersion")
    implementation("com.github.navikt.tiltakspenger-libs:json:${felleslibVersion}")
    implementation("com.github.navikt.tiltakspenger-libs:ktor-common:$felleslibVersion")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core-jvm:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-slf4j:$kotlinxCoroutinesVersion")
    implementation("org.jetbrains:annotations:26.1.0")
    implementation("com.natpryce:konfig:1.6.10.0")
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("ch.qos.logback:logback-classic:1.5.38")
    implementation("io.github.oshai:kotlin-logging-jvm:8.0.4")

    implementation("io.ktor:ktor-server:$ktorVersion")
    implementation("io.ktor:ktor-server-auth:$ktorVersion")
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-serialization-jackson3:$ktorVersion")

    implementation("tools.jackson.core:jackson-core:$jacksonVersion")
    implementation("com.fasterxml.jackson.core:jackson-annotations:$jacksonAnnotationsVersion")
    implementation("tools.jackson.core:jackson-databind:$jacksonVersion")
    implementation("tools.jackson.module:jackson-module-kotlin:$jacksonVersion")

    implementation("com.zaxxer:HikariCP:7.1.0")
    implementation("com.oracle.database.jdbc:ojdbc11:23.26.2.0.0")
    implementation("com.github.seratch:kotliquery:1.9.1")

    implementation("org.slf4j:jul-to-slf4j:2.0.18")
    implementation("org.slf4j:log4j-over-slf4j:2.0.18")
    implementation("org.slf4j:jcl-over-slf4j:2.0.18")

    testImplementation(platform("org.junit:junit-bom:6.1.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("com.github.navikt.tiltakspenger-libs:konsist-regler:$felleslibVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.mockk:mockk-dsl-jvm:$mockkVersion")
    testImplementation("io.ktor:ktor-server-test-host-jvm:$ktorVersion")
    testImplementation("org.xmlunit:xmlunit-matchers:2.12.0")
    testImplementation("org.hamcrest:hamcrest-core:3.0")
    testImplementation("org.flywaydb:flyway-database-oracle:12.10.0")
    testImplementation("org.testcontainers:testcontainers:$testContainersVersion")
    testImplementation("org.testcontainers:testcontainers-junit-jupiter:$testContainersVersion")
    testImplementation("org.testcontainers:testcontainers-oracle-free:$testContainersVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
    testImplementation("io.kotest:kotest-assertions-json:$kotestVersion")
    testImplementation("io.kotest:kotest-extensions:$kotestVersion")
}

configurations.all {
    // ekskluder JUnit 4
    exclude(group = "junit", module = "junit")
}

application {
    mainClass.set("no.nav.tiltakspenger.arena.ApplicationKt")
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
    kotlin {
        jvmToolchain(25)
        compilerOptions {
            freeCompilerArgs.add("-Xconsistent-data-class-copy-visibility")
        }
    }
    test {
        // JUnit 5-støtte
        useJUnitPlatform()
        // https://phauer.com/2018/best-practices-unit-testing-kotlin/
        systemProperty("junit.jupiter.testinstance.lifecycle.default", "per_class")
        testLogging {
            // Vi logger bare feilede og hoppede tester når Gradle kjører.
            events("skipped", "failed")
            exceptionFormat = TestExceptionFormat.FULL
        }
    }
    register<Copy>("gitHooks") {
        group = "git hooks"
        description = "Installerer git-hooks fra .gitHooks/ til .git/hooks/."
        from(file(".gitHooks"))
        into(file(".git/hooks"))
        filePermissions { unix("rwxr-xr-x") }
    }
}

kover {
    reports {
        total {
            filters {
                includes {
                    classes("no.nav.tiltakspenger.arena.*")
                }
                excludes {
                    // Ren oppstartskode som bare kan kjøres mot et ekte miljø - alt annet krever 100 % linjedekning:
                    //  - Application/ApplicationBuilder: main + serveroppstart (Texas-klient og Hikari-pool mot Nais).
                    //  - Configuration: leser NAIS_CLUSTER_NAME og secrets-filer montert av Nais (/secrets/...).
                    classes(
                        "no.nav.tiltakspenger.arena.ApplicationKt",
                        "no.nav.tiltakspenger.arena.ApplicationBuilderKt",
                        "no.nav.tiltakspenger.arena.Configuration",
                    )
                }
            }
            html {
                onCheck = true
            }
            xml {
                onCheck = true
            }
            verify {
                onCheck = true
                rule("all kode utenom oppstartskoden har full linjedekning") {
                    bound {
                        minValue = 100
                        coverageUnits = CoverageUnit.LINE
                        aggregationForGroup = AggregationType.COVERED_PERCENTAGE
                    }
                }
            }
        }
    }
}

tasks.named("koverXmlReport") {
    val xmlReport = layout.buildDirectory.file("reports/kover/report.xml")
    doLast {
        val xml = xmlReport.get().asFile
        val classCount = xml.readText().split("<class ").size - 1
        if (classCount == 0) throw GradleException("Kover report contains no classes — include filters likely stale")
    }
}
