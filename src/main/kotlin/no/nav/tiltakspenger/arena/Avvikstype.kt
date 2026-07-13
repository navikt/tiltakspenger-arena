package no.nav.tiltakspenger.arena

/**
 * Prefiks for logglinjer om dataavvik, så de er lette å søke etter:
 * «AVVIK(» treffer alle avvik, «AVVIK(<type>)» treffer én bestemt type.
 */
enum class Avvikstype(private val kode: String) {
    /** Et vedtakfakta-felt vi forventer er heltall inneholdt et desimaltall og ble rundet av */
    DESIMAL_HELTALL("desimal-heltall"),

    /** Perioden for et barnetilleggvedtak overlapper bare delvis med saksperioden */
    BARNETILLEGG_PERIODE_OVERLAPP("barnetillegg-periode-overlapp"),

    /** Perioden for et barnetilleggvedtak er helt utenfor saksperioden */
    BARNETILLEGG_PERIODE_UTENFOR("barnetillegg-periode-utenfor"),

    /** Vedtaket om tiltakspenger og vedtaket om barnetillegg har ikke samme antall dager */
    ULIKT_ANTALL_DAGER("ulikt-antall-dager"),

    /** Vedtaket om tiltakspenger og vedtaket om barnetillegg har ikke samme relaterte tiltak */
    ULIKT_RELATERT_TILTAK("ulikt-relatert-tiltak"),

    /** En periode har barnetillegg uten tiltakspenger - skal egentlig ikke kunne skje */
    BARNETILLEGG_UTEN_TILTAKSPENGER("barnetillegg-uten-tiltakspenger"),
    ;

    override fun toString() = "AVVIK($kode)"
}
