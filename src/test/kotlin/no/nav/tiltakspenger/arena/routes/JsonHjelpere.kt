package no.nav.tiltakspenger.arena.routes

/**
 * JSON-array av elementer.
 * Rekkefølgen betyr ikke noe for [io.kotest.assertions.json.shouldEqualJson].
 */
fun jsonArray(vararg elementer: String): String = "[${elementer.joinToString(",")}]"

/** Kvoterer verdien som JSON-streng, eller `null` når den mangler. */
internal fun jsonTekstEllerNull(verdi: String?): String = verdi?.let { "\"$it\"" } ?: "null"
