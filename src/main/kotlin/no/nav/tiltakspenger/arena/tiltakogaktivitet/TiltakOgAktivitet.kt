package no.nav.tiltakspenger.arena.tiltakogaktivitet

import no.nav.tjeneste.virksomhet.tiltakogaktivitet.v1.informasjon.Periode
import no.nav.tjeneste.virksomhet.tiltakogaktivitet.v1.informasjon.Tiltaksaktivitet

class TiltakOgAktivitet(
    val tiltaksnavn: String? = null,
    val aktivitetId: String? = null,
    val tiltakLokaltNavn: String? = null,
    val arrangoer: String? = null,
    val deltakelsePeriode: Periode? = null,
    val deltakelseProsent: Float? = null,
    // val antallDagerPerUke: Float? = null,
    // val deltakerStatus: Deltakerstatuser? = null,
    // val statusSistEndret: XMLGregorianCalendar? = null,
    // val begrunnelseInnsoeking: String? = null,
) {
    companion object {
        fun of(tiltaksaktiviteter: List<Tiltaksaktivitet>): List<TiltakOgAktivitet> =
            tiltaksaktiviteter.map { tiltaksaktivitet ->
                TiltakOgAktivitet(
                    tiltaksnavn = tiltaksaktivitet.tiltaksnavn,
                    aktivitetId = tiltaksaktivitet.getAktivitetId(),
                    tiltakLokaltNavn = tiltaksaktivitet.getTiltakLokaltNavn(),
                    arrangoer = tiltaksaktivitet.getArrangoer(),
                    deltakelsePeriode = tiltaksaktivitet.getDeltakelsePeriode(), //TODO: Må mappe periode også!
                    deltakelseProsent = tiltaksaktivitet.getDeltakelseProsent(),
                    // antallDagerPerUke = tiltaksaktivitet.getAntallDagerPerUke(),
                    // deltakerStatus = tiltaksaktivitet.deltakerStatus,
                    // statusSistEndret = tiltaksaktivitet.getStatusSistEndret(),
                    // begrunnelseInnsoeking = tiltaksaktivitet.getBegrunnelseInnsoeking()
                )
            }
    }
}
