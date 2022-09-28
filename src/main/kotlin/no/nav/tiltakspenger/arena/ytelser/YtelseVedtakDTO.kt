package no.nav.tiltakspenger.arena.ytelser

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak
import java.time.LocalDate

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
                        YtelseVedtakPeriodeTypeForYtelse.fromNavn(it)
                    },
                    vedtaksperiodeFom = vedtak.vedtaksperiode.fom,
                    vedtaksperiodeTom = vedtak.vedtaksperiode.tom,
                    vedtaksType = vedtak.vedtakstype?.let { YtelseVedtakVedtakstype.fromNavn(it) },
                    status = vedtak.status?.let { YtelseVedtakStatus.fromNavn(it) }
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
        T("Tidsbegrenset bortfall"); // Gjelder ikke tiltakspenger

        companion object {
            fun fromNavn(n: String): YtelseVedtakPeriodeTypeForYtelse =
                YtelseVedtakPeriodeTypeForYtelse.values().firstOrNull { it.navn == n }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakPeriodeTypeForYtelse $n")

        }
    }

    enum class YtelseVedtakVedtakstype(val navn: String, val ytelseSakDTOYtelsetype: YtelseSakDTO.YtelseSakYtelsetype) {
        AAP("Arbeidsavklaringspenger", YtelseSakDTO.YtelseSakYtelsetype.AA),
        DAGO("Ordinære dagpenger", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        PERM("Dagpenger under permitteringer", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        FISK("Dagp. v/perm fra fiskeindustri", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        LONN("Lønnsgarantimidler - dagpenger", YtelseSakDTO.YtelseSakYtelsetype.DAGP),
        BASI("Tiltakspenger (basisytelse før 2014)", YtelseSakDTO.YtelseSakYtelsetype.INDIV);

        companion object {
            fun fromNavn(n: String): YtelseVedtakVedtakstype =
                YtelseVedtakVedtakstype.values().firstOrNull { n.contains(it.navn) }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakVedtakstype $n")

        }
    }

    enum class YtelseVedtakStatus(val navn: String) {
        AVSLU("Avsluttet"),
        GODKJ("Godkjent"),
        INNST("Innstilt"),
        IVERK("Iverksatt"),
        MOTAT("Mottatt"),
        OPPRE("Opprettet"),
        REGIS("Registrert");

        companion object {
            fun fromNavn(n: String): YtelseVedtakStatus =
                YtelseVedtakStatus.values().firstOrNull { it.navn == n }
                    ?: throw IllegalArgumentException("Ukjent YtelseVedtakStatus $n")

        }
    }
}
