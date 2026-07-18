package no.nav.tiltakspenger.arena.routes

/**
 * Forventet JSON for én `ArenaTiltakspengerVedtakPeriode` fra POST /azure/tiltakspenger/vedtaksperioder (og /vedtak).
 *
 * Kontrakten skrives ut som tekst (ikke via DTO-en), slik at en DTO-refaktorering brekker testene.
 * Endre kun her ved en bevisst kontraktsendring.
 * Defaultene beskriver et standard tiltakspengevedtak (samsvarer med [no.nav.tiltakspenger.arena.repository.ArenaTestdata] sine defaults).
 * Identiteten ([vedtakId]/[sakId]/[saksnummer]) er påkrevd; hver test overstyrer i tillegg kun det den tester.
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
