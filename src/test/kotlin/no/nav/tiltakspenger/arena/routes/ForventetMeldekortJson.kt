package no.nav.tiltakspenger.arena.routes

/**
 * Forventet JSON for én `MeldekortDetaljer` fra POST /azure/tiltakspenger/meldekort.
 * Defaultene beskriver et standard meldekort; [dager] bygges med [forventetMeldekortdagJson].
 */
fun forventetMeldekortJson(
    meldekortId: String,
    periodekode: Int,
    fraOgMed: String,
    tilOgMed: String,
    år: Int = 2023,
    ukenrUke1: Int = 1,
    ukenrUke2: Int = 2,
    mottatt: String? = "2023-01-16",
    arbeidet: Boolean = false,
    kurs: Boolean = true,
    ferie: Boolean = false,
    syk: Boolean = false,
    annetFravaer: Boolean = false,
    fortsattArbeidsoker: Boolean = true,
    registrert: String = "2023-01-16T12:00:00",
    sistEndret: String = "2023-01-17T12:00:00",
    type: String = "Elektronisk meldekort",
    status: String = "Ferdig beregnet",
    statusDato: String = "2023-01-17",
    meldegruppe: String = "Individstønad",
    totaltArbeidetTimer: Int = 0,
    dager: List<String> = emptyList(),
): String =
    """
    {
      "meldekortId": "$meldekortId",
      "mottatt": ${jsonTekstEllerNull(mottatt)},
      "arbeidet": $arbeidet,
      "kurs": $kurs,
      "ferie": $ferie,
      "syk": $syk,
      "annetFravaer": $annetFravaer,
      "fortsattArbeidsoker": $fortsattArbeidsoker,
      "registrert": "$registrert",
      "sistEndret": "$sistEndret",
      "type": "$type",
      "status": "$status",
      "statusDato": "$statusDato",
      "meldegruppe": "$meldegruppe",
      "aar": $år,
      "totaltArbeidetTimer": $totaltArbeidetTimer,
      "periode": {
        "aar": $år,
        "periodekode": $periodekode,
        "ukenrUke1": $ukenrUke1,
        "ukenrUke2": $ukenrUke2,
        "fraOgMed": "$fraOgMed",
        "tilOgMed": "$tilOgMed"
      },
      "dager": [${dager.joinToString(",")}]
    }
    """.trimIndent()

/** Forventet JSON for én `MeldekortDagDetaljer` i `dager`-lista. */
fun forventetMeldekortdagJson(
    ukeNr: Int,
    dagNr: Int,
    arbeidsdag: Boolean = false,
    ferie: Boolean? = false,
    kurs: Boolean = true,
    syk: Boolean = false,
    annetFravaer: Boolean = false,
    registrertAv: String = "GRENSESN",
    registrert: String = "2023-01-16T00:00:00",
    arbeidetTimer: Int = 0,
): String =
    """
    {
      "ukeNr": $ukeNr,
      "dagNr": $dagNr,
      "arbeidsdag": $arbeidsdag,
      "ferie": ${ferie?.toString() ?: "null"},
      "kurs": $kurs,
      "syk": $syk,
      "annetFravaer": $annetFravaer,
      "registrertAv": "$registrertAv",
      "registrert": "$registrert",
      "arbeidetTimer": $arbeidetTimer
    }
    """.trimIndent()
