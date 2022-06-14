package no.nav.tiltakspenger.arena.ytelser

import no.nav.tiltakspenger.arena.felles.toLocalDate
import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak
import java.time.LocalDate

class YtelseVedtak(
    val beslutningsDato: LocalDate? = null,
    val periodetypeForYtelse: String? = null,
    val vedtaksperiodeFom: LocalDate? = null,
    val vedtaksperiodeTom: LocalDate? = null,
    val vedtaksType: String? = null,
    val status: String? = null,
) {
    companion object {
        fun of(vedtakListe: List<Vedtak>): List<YtelseVedtak> =
            vedtakListe.map { vedtak ->
                YtelseVedtak(
                    beslutningsDato = vedtak.beslutningsdato.toLocalDate(),
                    periodetypeForYtelse = vedtak.periodetypeForYtelse,
                    vedtaksperiodeFom = vedtak.vedtaksperiode.fom.toLocalDate(),
                    vedtaksperiodeTom = vedtak.vedtaksperiode.tom.toLocalDate(),
                    vedtaksType = vedtak.vedtakstype,
                    status = vedtak.status
                )
            }
    }
}
