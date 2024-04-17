package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import mu.KotlinLogging
import no.nav.tiltakspenger.arena.felles.Periode
import no.nav.tiltakspenger.arena.felles.PeriodeMedVerdier
import no.nav.tiltakspenger.arena.repository.ArenaBarnetilleggVedtakDTO
import no.nav.tiltakspenger.arena.repository.ArenaSakMedMinstEttVedtakDTO
import no.nav.tiltakspenger.arena.repository.ArenaTiltakspengerVedtakDTO

private val LOG = KotlinLogging.logger {}
private val SECURELOG = KotlinLogging.logger("tjenestekall")

private const val defaultRelaterteTiltak: String = ""
private const val defaultAntallBarn: Int = 0
private const val defaultDagsats: Int = 0
private const val defaultAntallDager: Double = 0.0

object ArenaTilVedtakDetaljerMapper {
    fun mapTiltakspengerFraArenaTilVedtaksperioder(saker: List<ArenaSakMedMinstEttVedtakDTO>): PeriodeMedVerdier<VedtakDetaljer>? {
        if (saker.isEmpty()) {
            SECURELOG.info { "Returnerer null pga ingen saker" }
            return null
        }
        val totalePeriodeFra = saker.minOf { it.sakPeriode().fra }
        val totalePeriodeTil = saker.maxOf { it.sakPeriode().til }
        val totalePeriode = Periode(totalePeriodeFra, totalePeriodeTil)

        val periodeMedTiltakspengerInit = periodeMedDefaultVerdierForTiltakspenger(totalePeriode)
        val periodeMedBarnetilleggInit = periodeMedDefaultVerdierForBarnetillegg(totalePeriode)

        val periodeMedTiltakspenger = fyllTiltakspengerPeriodenMedReelleVerdier(saker, periodeMedTiltakspengerInit)
        val periodeMedBarnetillegg =
            fyllBarnetilleggPeriodenMedReelleVerdier(saker, periodeMedBarnetilleggInit, totalePeriode)

        SECURELOG.info { "Periode med tiltakspenger: $periodeMedTiltakspenger" }
        SECURELOG.info { "Periode med barnetillegg: $periodeMedBarnetillegg" }

        return kombinerTiltakspengerMedBarnetillegg(periodeMedTiltakspenger, periodeMedBarnetillegg)
    }

    private fun periodeMedDefaultVerdierForTiltakspenger(totalePeriode: Periode) =
        PeriodeMedVerdier(
            totalePeriode = totalePeriode,
            defaultVerdi = VedtakDetaljerKunTiltakspenger(
                antallDager = defaultAntallDager,
                dagsats = defaultDagsats,
                relaterteTiltak = defaultRelaterteTiltak,
                rettighet = Rettighet.INGENTING,
                vedtakId = 0L,
                sakId = 0L,
            ),
        )

    private fun periodeMedDefaultVerdierForBarnetillegg(totalePeriode: Periode) =
        PeriodeMedVerdier(
            totalePeriode = totalePeriode,
            defaultVerdi = VedtakDetaljerBarnetillegg(
                antallDager = defaultAntallDager,
                dagsats = defaultDagsats,
                antallBarn = defaultAntallBarn,
                relaterteTiltak = defaultRelaterteTiltak,
                rettighet = Rettighet.INGENTING,
            ),
        )

    private fun fyllTiltakspengerPeriodenMedReelleVerdier(
        saker: List<ArenaSakMedMinstEttVedtakDTO>,
        periodeMedTiltakspengerInit: PeriodeMedVerdier<VedtakDetaljerKunTiltakspenger>,
    ) =
        saker
            .flatMap { it.tiltakspengerVedtak }
            .fold(periodeMedTiltakspengerInit) { periodeMedVerdier: PeriodeMedVerdier<VedtakDetaljerKunTiltakspenger>, arenaTiltakspengerVedtakDTO: ArenaTiltakspengerVedtakDTO ->
                periodeMedVerdier.setVerdiForDelPeriode(
                    VedtakDetaljerKunTiltakspenger(
                        antallDager = arenaTiltakspengerVedtakDTO.antallDager ?: defaultAntallDager,
                        dagsats = arenaTiltakspengerVedtakDTO.dagsats ?: defaultDagsats,
                        relaterteTiltak = arenaTiltakspengerVedtakDTO.relatertTiltak ?: defaultRelaterteTiltak,
                        rettighet = Rettighet.TILTAKSPENGER,
                        vedtakId = arenaTiltakspengerVedtakDTO.vedtakId,
                        sakId = arenaTiltakspengerVedtakDTO.tilhørendeSakId,
                    ),
                    arenaTiltakspengerVedtakDTO.vedtaksperiode(),
                )
            }

    private fun fyllBarnetilleggPeriodenMedReelleVerdier(
        saker: List<ArenaSakMedMinstEttVedtakDTO>,
        periodeMedBarnetilleggInit: PeriodeMedVerdier<VedtakDetaljerBarnetillegg>,
        totalePeriode: Periode,
    ) =
        saker
            .flatMap { it.barnetilleggVedtak }
            .fold(periodeMedBarnetilleggInit) { periodeMedVerdier: PeriodeMedVerdier<VedtakDetaljerBarnetillegg>, arenaBarnetilleggVedtakDTO: ArenaBarnetilleggVedtakDTO ->
                if (totalePeriode.inneholderHele(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    LOG.info { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) er innenfor saksperioden ($totalePeriode) " }
                    // Legger til hele perioden
                    periodeMedVerdier.setVerdiForDelPeriode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager ?: defaultAntallDager,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats ?: defaultDagsats,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn ?: defaultAntallBarn,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.relatertTiltak ?: defaultRelaterteTiltak,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode(),
                    )
                } else if (totalePeriode.overlapperMed(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    LOG.warn { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) overlapper med saksperioden ($totalePeriode) " }
                    periodeMedVerdier.setVerdiForDelPeriode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager ?: defaultAntallDager,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats ?: defaultDagsats,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn ?: defaultAntallBarn,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.relatertTiltak ?: defaultRelaterteTiltak,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode().overlappendePeriode(totalePeriode)!!,
                    )
                } else {
                    LOG.error { "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) er utenfor saksperioden ($totalePeriode) " }
                    // Legger ikke til noe
                    periodeMedVerdier
                }
            }

    private fun kombinerTiltakspengerMedBarnetillegg(
        periodeMedTiltakspenger: PeriodeMedVerdier<VedtakDetaljerKunTiltakspenger>,
        periodeMedBarnetillegg: PeriodeMedVerdier<VedtakDetaljerBarnetillegg>,
    ) =
        periodeMedTiltakspenger.kombiner(periodeMedBarnetillegg) { vt, vb ->
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
                rettighet = kombinerRettighet(vt, vb),
                vedtakId = vt.vedtakId,
                sakId = vt.sakId,
            )
        }

    private fun kombinerRettighet(vt: VedtakDetaljerKunTiltakspenger, vb: VedtakDetaljerBarnetillegg) =
        if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG) {
            Rettighet.TILTAKSPENGER_OG_BARNETILLEGG
        } else if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.INGENTING) {
            Rettighet.TILTAKSPENGER
        } else if (vt.rettighet == Rettighet.INGENTING && vb.rettighet == Rettighet.BARNETILLEGG) {
            SECURELOG.info { "Her har vi en periode med barnetillegg men ikke tiltakspenger - det skal ikke egentlig kunne skje!" }
            Rettighet.BARNETILLEGG
        } else {
            Rettighet.INGENTING
        }
}
