package no.nav.tiltakspenger.arena

import io.mockk.coEvery
import io.mockk.mockk
import no.nav.helse.rapids_rivers.testsupport.TestRapid
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaAktiviteterDTO
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsClient
import no.nav.tiltakspenger.arena.tiltakogaktivitet.ArenaOrdsException.PersonNotFoundException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.skyscreamer.jsonassert.JSONCompareMode

internal class ArenaTiltakServiceTest {
    private val testRapid = TestRapid()
    private val ident = "04927799109"

    private val arenaOrdsService = mockk<ArenaOrdsClient>()

    val service = ArenaTiltakService(
        rapidsConnection = testRapid,
        arenaOrdsService = arenaOrdsService,
    )

    @BeforeEach
    fun reset() {
        testRapid.reset()
    }

    @Test
    fun `Sjekk løsning hvis vi ikke finner person i arena`() {
        coEvery { arenaOrdsService.hentArenaAktiviteter(any()) } throws PersonNotFoundException("feil")

        testRapid.sendTestMessage(behovMelding)

        with(testRapid.inspektør) {
            assertEquals(1, size)
            assertEquals(ident, field(0, "ident").asText())
            JSONAssert.assertEquals(
                tomLøsning, field(0, "@løsning").toPrettyString(),
                JSONCompareMode.STRICT
            )

            assertNull(
                field(0, "@løsning")
                    .get("arenatiltak")
                    .get("feil")
                    .asText(null)
            )
        }
    }

    @Test
    fun `Sjekk løsning hvis alt går ok`() {
        coEvery { arenaOrdsService.hentArenaAktiviteter(any()) } returns ArenaAktiviteterDTO(
            response = ArenaAktiviteterDTO.Response(
                tiltaksaktivitetListe = listOf(
                    ArenaAktiviteterDTO.Tiltaksaktivitet(
                        tiltaksnavn = ArenaAktiviteterDTO.Tiltaksaktivitet.Tiltaksnavn.ABIST,
                        aktivitetId = "aktivitetId",
                        tiltakLokaltNavn = null,
                        arrangoer = null,
                        bedriftsnummer = null,
                        deltakelsePeriode = null,
                        deltakelseProsent = null,
                        deltakerStatus = ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakerStatus(
                            statusNavn = "",
                            status = ArenaAktiviteterDTO.Tiltaksaktivitet.DeltakerStatusType.DELAVB,
                        ),
                        statusSistEndret = null,
                        begrunnelseInnsoeking = null,
                        antallDagerPerUke = null
                    )
                ),
                gruppeaktivitetListe = listOf(),
                utdanningsaktivitetListe = listOf()
            )
        )

        testRapid.sendTestMessage(behovMelding)

        with(testRapid.inspektør) {
            assertEquals(1, size)
            assertEquals(ident, field(0, "ident").asText())
            JSONAssert.assertEquals(
                løsning, field(0, "@løsning").toPrettyString().also { println(it) },
                JSONCompareMode.STRICT
            )

            assertNull(
                field(0, "@løsning")
                    .get("arenatiltak")
                    .get("feil")
                    .asText(null)
            )
        }
    }

    private val tomLøsning = """
            {
                "arenatiltak": {
                  "tiltaksaktiviteter": [],
                  "feil": null
                }
              }
        """.trimIndent()

    private val løsning = """
            {
                "arenatiltak": {
                  "tiltaksaktiviteter": [
                    {
                      "tiltakType": "ABIST",
                      "aktivitetId": "aktivitetId",
                      "tiltakLokaltNavn": null,
                      "arrangoer": null,
                      "bedriftsnummer": null,
                      "deltakelsePeriode": null,
                      "deltakelseProsent": null,
                      "deltakerStatusType": "DELAVB",
                      "statusSistEndret": null,
                      "begrunnelseInnsoeking": null,
                      "antallDagerPerUke": null
                    }
                  ],
                  "feil": null
                }
              }
        """.trimIndent()

    private val behovMelding = """
        {
          "@behov": [
            "arenatiltak"
          ],
          "@id": "test",
          "@behovId": "behovId",
          "ident": "$ident",
          "@opprettet": "2022-08-18T17:44:24.723046748",
          "system_read_count": 0,
          "system_participating_services": [
            {
              "id": "test",
              "time": "2022-08-18T17:44:24.723046748",
              "service": "tiltakspenger-fakta-person",
              "instance": "tiltakspenger-fakta-person-56bffc459b-4rm9s",
              "image": "ghcr.io/navikt/tiltakspenger-fakta-person:fea0180a813ea2a49c4200906cdb844ada797109"
            }
          ]
        }
    """.trimIndent()
}
