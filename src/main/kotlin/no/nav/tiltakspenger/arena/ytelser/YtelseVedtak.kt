package no.nav.tiltakspenger.arena.ytelser

import no.nav.tjeneste.virksomhet.ytelseskontrakt.v3.informasjon.ytelseskontrakt.Vedtak
import javax.xml.datatype.XMLGregorianCalendar

class YtelseVedtak(
    val beslutningsDato: XMLGregorianCalendar? = null,
    val periodetypeForYtelse: String? = null,
    val vedtaksperiodeFom: XMLGregorianCalendar? = null,
    val vedtaksperiodeTom: XMLGregorianCalendar? = null,
    val vedtaksType: String? = null,
    val status: String? = null,
) {
    companion object {
        fun of(vedtakListe: List<Vedtak>): List<YtelseVedtak> =
            vedtakListe.map { vedtak ->
                YtelseVedtak(
                    beslutningsDato = vedtak.beslutningsdato,
                    periodetypeForYtelse = vedtak.periodetypeForYtelse,
                    vedtaksperiodeFom = vedtak.vedtaksperiode.fom,
                    vedtaksperiodeTom = vedtak.vedtaksperiode.tom,
                    vedtaksType = vedtak.vedtakstype,
                    status = vedtak.status
                )
            }
    }
}