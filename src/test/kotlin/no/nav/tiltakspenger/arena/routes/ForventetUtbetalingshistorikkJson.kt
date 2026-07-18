package no.nav.tiltakspenger.arena.routes

/**
 * Forventet JSON for ett `UtbetalingshistorikkDetaljer`-innslag fra POST /azure/tiltakspenger/utbetalingshistorikk.
 * Defaultene beskriver et postering-innslag («Overført utbetaling»); [meldekortId]/[vedtakId] er identitet.
 */
fun forventetUtbetalingshistorikkJson(
    meldekortId: Long?,
    vedtakId: Long?,
    dato: String = "2023-01-20",
    transaksjonstype: String = "Utbetaling",
    sats: Double = 285.0,
    status: String = "Overført utbetaling",
    belop: Double = 2850.0,
    fraOgMedDato: String = "2023-01-02",
    tilOgMedDato: String = "2023-01-15",
): String =
    """
    {
      "meldekortId": ${meldekortId ?: "null"},
      "dato": "$dato",
      "transaksjonstype": "$transaksjonstype",
      "sats": $sats,
      "status": "$status",
      "vedtakId": ${vedtakId ?: "null"},
      "belop": $belop,
      "fraOgMedDato": "$fraOgMedDato",
      "tilOgMedDato": "$tilOgMedDato"
    }
    """.trimIndent()
