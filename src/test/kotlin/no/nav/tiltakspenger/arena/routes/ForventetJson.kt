package no.nav.tiltakspenger.arena.routes

/**
 * Forventet JSON for endepunktenes responser, skrevet ut som **tekst** (ikke via DTO-ene). Dette er
 * kontrakten mot konsumentene (tiltakspenger-datadeling → NKS m.fl.). Endre kun her ved en bevisst
 * kontraktsendring — da oppdateres alle testene som bruker byggerne. Fordi feltnavn/struktur står
 * eksplisitt som tekst, brekker en DTO-refaktorering (omdøpt/fjernet/lagt-til felt) testene.
 */

private fun jsonTekstEllerNull(verdi: String?): String = verdi?.let { "\"$it\"" } ?: "null"

/**
 * Én `ArenaTiltakspengerVedtakPeriode` fra POST /azure/tiltakspenger/vedtaksperioder (og /vedtak).
 *
 * Defaultene beskriver et standard tiltakspengevedtak (samsvarer med [ArenaTestdata]s defaults +
 * [no.nav.tiltakspenger.arena.repository.ArenaTestdata.leggTilTiltakspengevedtakMedFakta]). Identiteten
 * ([vedtakId]/[sakId]/[saksnummer]) er påkrevd; hver test overstyrer i tillegg kun det den tester.
 */
fun forventetVedtaksperiodeJson(
    vedtakId: Long,
    sakId: Long,
    saksnummer: String,
    fraOgMed: String = "2023-01-01",
    tilOgMed: String? = "2023-03-31",
    antallDager: Double = 10.0,
    dagsatsTiltakspenger: Int = 285,
    dagsatsBarnetillegg: Int = 0,
    antallBarn: Int = 0,
    relaterteTiltak: String = "133924438",
    rettighet: String = "TILTAKSPENGER",
    beslutningsdato: String? = null,
    sakOpprettetDato: String = "2023-01-01",
    sakStatus: String = "Aktiv",
): String =
    """
    {
      "fraOgMed": "$fraOgMed",
      "tilOgMed": ${jsonTekstEllerNull(tilOgMed)},
      "antallDager": $antallDager,
      "dagsatsTiltakspenger": $dagsatsTiltakspenger,
      "dagsatsBarnetillegg": $dagsatsBarnetillegg,
      "antallBarn": $antallBarn,
      "relaterteTiltak": "$relaterteTiltak",
      "rettighet": "$rettighet",
      "vedtakId": $vedtakId,
      "sakId": $sakId,
      "beslutningsdato": ${jsonTekstEllerNull(beslutningsdato)},
      "sak": {
        "saksnummer": "$saksnummer",
        "opprettetDato": "$sakOpprettetDato",
        "status": "$sakStatus"
      }
    }
    """.trimIndent()

/** JSON-array av perioder (rekkefølgen betyr ikke noe for [io.kotest.assertions.json.shouldEqualJson]). */
fun forventetPerioderJson(vararg perioder: String): String = "[${perioder.joinToString(",")}]"
