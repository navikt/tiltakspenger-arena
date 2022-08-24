package no.nav.tiltakspenger.arena.ytelser

import java.time.LocalDate
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak

data class YtelseVedtakDTO(
    val beslutningsDato: LocalDate? = null,
    val periodetypeForYtelse: YtelseVedtakPeriodeTypeForYtelse? = null,
    val vedtaksperiodeFom: LocalDate? = null,
    val vedtaksperiodeTom: LocalDate? = null,
    val vedtaksType: YtelseVedtakVedtakstype? = null,
    val status: YtelseVedtakStatus? = null,
) {
    companion object {
        fun of(vedtakListe: List<Vedtak>): List<YtelseVedtakDTO> =
            vedtakListe.map { vedtak ->
                YtelseVedtakDTO(
                    beslutningsDato = vedtak.beslutningsdato,
                    periodetypeForYtelse = vedtak.periodetypeForYtelse?.let {
                        YtelseVedtakPeriodeTypeForYtelse.valueOf(
                            it
                        )
                    },
                    vedtaksperiodeFom = vedtak.vedtaksperiode.fom,
                    vedtaksperiodeTom = vedtak.vedtaksperiode.tom,
                    vedtaksType = vedtak.vedtakstype?.let { YtelseVedtakVedtakstype.valueOf(it) },
                    status = vedtak.status?.let { YtelseVedtakStatus.valueOf(it) }
                )
            }
    }

    enum class YtelseVedtakPeriodeTypeForYtelse(val navn: String) {
        E("Endring"),
        F("Forlenget ventetid"), // Gjelder ikke tiltakspenger
        G("Gjenopptak"),
        N("Annuller sanksjon"), // Gjelder ikke tiltakspenger
        O("Ny rettighet"),
        S("Stans"),
        T("Tidsbegrenset bortfall") // Gjelder ikke tiltakspenger
    }

    enum class YtelseVedtakVedtakstype(val navn: String, val ytelseSakDTOYtelsetype: YtelseSakDTO.YtelseSakYtelsetype) {
        AAP("Arbeidsavklaringspenger", YtelseSakDTO.YtelseSakYtelsetype.AA),
        DAGO("Ordinære dagpenger", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        PERM("Dagpenger under permitteringer", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        FISK("Dagp. v/perm fra fiskeindustri", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        LONN("Lønnsgarantimidler - dagpenger", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        BASI("Tiltakspenger (basisytelse før 2014)", YtelseSakDTO.YtelseSakYtelsetype.INDIV)
    }

    enum class YtelseVedtakStatus(val navn: String) {
        AVSLU("Avsluttet"),
        GODKJ("Godkjent"),
        INNST("Innstilt"),
        IVERK("Iverksatt"),
        MOTAT("Mottatt"),
        OPPRE("Opprettet"),
        REGIS("Registrert")
    }
}
