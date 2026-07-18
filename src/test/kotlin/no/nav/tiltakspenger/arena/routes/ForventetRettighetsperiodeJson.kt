package no.nav.tiltakspenger.arena.routes

/** Forventet JSON for én `ArenaTiltakspengerRettighetPeriode` fra POST /azure/tiltakspenger/rettighetsperioder. */
fun forventetRettighetsperiodeJson(fraOgMed: String = "2023-01-01", tilOgMed: String? = "2023-03-31"): String =
    """{ "fraOgMed": "$fraOgMed", "tilOgMed": ${jsonTekstEllerNull(tilOgMed)} }"""
