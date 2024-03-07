package no.nav.tiltakspenger.arena.service

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.Periode
import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.repository.ArenaBarnetilleggVedtakDTO
import no.nav.tiltakspenger.arena.repository.ArenaSakDTO
import no.nav.tiltakspenger.arena.repository.ArenaTiltakspengerVedtakDTO

private val SECURELOG = KotlinLogging.logger("tjenestekall")

fun mapTiltakspengerFraArenaTilVedtaksperioder(saker: List<ArenaSakDTO>): PeriodeMedVerdier<VedtakDetaljer>? {
    val totalePeriodeFra = saker.minOfOrNull { it.sakPeriode()!!.fra }
    val totalePeriodeTil = saker.maxOfOrNull { it.sakPeriode()!!.til }
    if (totalePeriodeFra == null) {
        SECURELOG.info { "Returnerer null pga ingen saksperiode" }
        return null
    }
    val totalePeriode = Periode(totalePeriodeFra, totalePeriodeTil!!)
    val periodeMedTiltakspengerInit = PeriodeMedVerdier(
        totalePeriode = totalePeriode,
        defaultVerdi = VedtakDetaljerUtenBarnetillegg(
            antallDager = 0.0,
            dagsats = 0,
            relaterteTiltak = "",
            rettighet = Rettighet.INGENTING,
        ),
    )
    val periodeMedBarnetilleggInit = PeriodeMedVerdier(
        totalePeriode = totalePeriode,
        defaultVerdi = VedtakDetaljerBarnetillegg(
            antallDager = 0.0,
            dagsats = 0,
            antallBarn = 0,
            relaterteTiltak = "",
            rettighet = Rettighet.INGENTING,
        ),
    )
    val periodeMedTiltakspenger =
        saker
            .flatMap { it.tiltakspengerVedtak }
            .fold(periodeMedTiltakspengerInit) { periodeMedVerdier: PeriodeMedVerdier<VedtakDetaljerUtenBarnetillegg>, arenaTiltakspengerVedtakDTO: ArenaTiltakspengerVedtakDTO ->
                periodeMedVerdier.setVerdiForDelPeriode(
                    VedtakDetaljerUtenBarnetillegg(
                        antallDager = arenaTiltakspengerVedtakDTO.antallDager!!,
                        dagsats = arenaTiltakspengerVedtakDTO.dagsats!!,
                        relaterteTiltak = arenaTiltakspengerVedtakDTO.relatertTiltak!!,
                        rettighet = Rettighet.TILTAKSPENGER,
                    ),
                    arenaTiltakspengerVedtakDTO.vedtaksperiode(),
                )
            }

    val periodeMedBarnetillegg =
        saker
            .flatMap { it.barnetilleggVedtak }
            .fold(periodeMedBarnetilleggInit) { periodeMedVerdier: PeriodeMedVerdier<VedtakDetaljerBarnetillegg>, arenaBarnetilleggVedtakDTO: ArenaBarnetilleggVedtakDTO ->
                if (totalePeriode.inneholderHele(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    SECURELOG.info { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) er innenfor saksperioden ($totalePeriode) " }
                    // Legger til hele perioden
                    periodeMedVerdier.setVerdiForDelPeriode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager!!,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats!!,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn!!,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.relatertTiltak!!,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode(),
                    )
                } else if (totalePeriode.overlapperMed(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    SECURELOG.info { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) overlapper med saksperioden ($totalePeriode) " }
                    periodeMedVerdier.setVerdiForDelPeriode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager!!,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats!!,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn!!,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.relatertTiltak!!,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode().overlappendePeriode(totalePeriode)!!,
                    )
                } else {
                    SECURELOG.info { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) er utenfor saksperioden ($totalePeriode) " }
                    // Legger ikke til noe
                    periodeMedVerdier
                }
            }
    SECURELOG.info { "Periode med tiltakspenger: $periodeMedTiltakspenger" }
    SECURELOG.info { "Periode med barnetillegg: $periodeMedBarnetillegg" }

    return periodeMedTiltakspenger.kombiner(periodeMedBarnetillegg) { vt, vb ->
        if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG && vt.antallDager != vb.antallDager) {
            SECURELOG.info { "Vedtaket om tiltakspenger (${vt.antallDager}) og vedtaket om barnetillegg (${vb.antallDager}) har ikke samme antall dager" }
        }
        if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG && vt.relaterteTiltak != vb.relaterteTiltak) {
            SECURELOG.info { "Vedtaket om tiltakspenger (${vt.relaterteTiltak}) og vedtaket om barnetillegg (${vb.relaterteTiltak}) har ikke samme relaterte tiltak" }
        }
        VedtakDetaljer(
            antallDager = vt.antallDager,
            dagsatsTiltakspenger = vt.dagsats,
            dagsatsBarnetillegg = vb.dagsats,
            antallBarn = vb.antallBarn,
            relaterteTiltak = vt.relaterteTiltak,
            rettighet = if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG) {
                Rettighet.TILTAKSPENGER_OG_BARNETILLEGG
            } else if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.INGENTING) {
                Rettighet.TILTAKSPENGER
            } else if (vt.rettighet == Rettighet.INGENTING && vb.rettighet == Rettighet.BARNETILLEGG) {
                SECURELOG.info { "Her har vi en periode med barnetillegg men ikke tiltakspenger - det skal ikke egentlig kunne skje!" }
                Rettighet.BARNETILLEGG
            } else {
                Rettighet.INGENTING
            },
        )
    }
}
