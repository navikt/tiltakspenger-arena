package no.nav.tiltakspenger.arena.routes

/**
 * Forventet JSON for `UtbetalingshistorikkVedtaksfaktaOgAnmerkninger` fra GET /utbetalingshistorikk/detaljer.
 * Defaultene beskriver standard vedtakfakta; [anmerkninger] bygges med [forventetAnmerkningJson].
 */
fun forventetUtbetalingshistorikkDetaljerJson(
    dagsats: Int? = 285,
    gjelderFra: String? = "2021-03-01",
    gjelderTil: String? = "2021-03-14",
    antallUtbetalinger: Int? = 2,
    belopPerUtbetalinger: Int? = 1995,
    alternativBetalingsmottaker: String? = null,
    anmerkninger: List<String> = emptyList(),
): String =
    """
    {
      "vedtakfakta": {
        "dagsats": ${dagsats ?: "null"},
        "gjelderFra": ${jsonTekstEllerNull(gjelderFra)},
        "gjelderTil": ${jsonTekstEllerNull(gjelderTil)},
        "antallUtbetalinger": ${antallUtbetalinger ?: "null"},
        "belopPerUtbetalinger": ${belopPerUtbetalinger ?: "null"},
        "alternativBetalingsmottaker": ${jsonTekstEllerNull(alternativBetalingsmottaker)}
      },
      "anmerkninger": [${anmerkninger.joinToString(",")}]
    }
    """.trimIndent()

/** Forventet JSON for én `AnmerkningDetaljer` i `anmerkninger`-lista. */
fun forventetAnmerkningJson(
    kilde: String? = "Vedtak",
    registrert: String? = "2023-01-18T00:00:00",
    beskrivelse: String? = "Meldekort avvist, mangler 5 dager",
): String =
    """
    {
      "kilde": ${jsonTekstEllerNull(kilde)},
      "registrert": ${jsonTekstEllerNull(registrert)},
      "beskrivelse": ${jsonTekstEllerNull(beskrivelse)}
    }
    """.trimIndent()
