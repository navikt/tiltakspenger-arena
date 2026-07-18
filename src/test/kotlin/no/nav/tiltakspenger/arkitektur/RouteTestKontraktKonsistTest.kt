package no.nav.tiltakspenger.arkitektur

import com.lemonappdev.konsist.api.Konsist
import io.kotest.assertions.withClue
import io.kotest.matchers.collections.shouldBeEmpty
import org.junit.jupiter.api.Test

class RouteTestKontraktKonsistTest {
    private val deserialiseringsImporter =
        setOf(
            "no.nav.tiltakspenger.libs.json.deserialize",
            "no.nav.tiltakspenger.libs.json.deserializeList",
            "no.nav.tiltakspenger.libs.json.deserializeListNullable",
            "io.ktor.client.call.body",
        )

    @Test
    fun `route-tester asserter på rå JSON, ikke via respons-DTO`() {
        val violations =
            Konsist
                .scopeFromTest()
                .files
                .filter { file -> file.path.endsWith("RouteTest.kt") }
                .flatMap { file ->
                    file.imports
                        .filter { import -> import.name in deserialiseringsImporter }
                        .map { import -> "${file.path}: ${import.name}" }
                }

        withClue(
            "Route-tester skal asserte på rå JSON via skalHaOkMedJson(...), ikke deserialisere respons-DTO-en. " +
                "Da brekker testen når DTO-en refaktoreres. " +
                "Følgende deserialiserings-importer er ikke tillat i *RouteTest:\n" +
                violations.joinToString("\n"),
        ) {
            violations.shouldBeEmpty()
        }
    }
}
