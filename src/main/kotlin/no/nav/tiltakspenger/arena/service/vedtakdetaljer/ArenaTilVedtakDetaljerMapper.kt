package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import io.github.oshai.kotlinlogging.KotlinLogging
import no.nav.tiltakspenger.arena.Avvikstype
import no.nav.tiltakspenger.arena.SE_SIKKERLOGG
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaBarnetilleggVedtakDTO
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaSakMedMinstEttVedtakDTO
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaTiltakspengerVedtakDTO
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import no.nav.tiltakspenger.libs.periode.Periode
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

private val LOG = KotlinLogging.logger {}

// TODO post-mvp jah: Dette føles ikke riktig.
//  Hvorfor kan vi ikke behandle den som null istedet?
private const val DEFAULT_TILTAK_GJENNOMFØRINGS_ID: String = ""
private const val DEFAULT_ANTALL_BARN: Int = 0
private const val DEFAULT_DAGSATS: Int = 0
private const val DEFAULT_ANTALL_DAGER: Double = 0.0

object ArenaTilVedtakDetaljerMapper {
    fun mapTiltakspengerFraArenaTilVedtaksperioder(
        saker: List<ArenaSakMedMinstEttVedtakDTO>,
        fnr: String,
    ): Periodisering<VedtakDetaljer>? {
        if (saker.isEmpty()) {
            return null
        }
        val totalePeriodeFra = saker.minOf { it.sakPeriode().fraOgMed }
        val totalePeriodeTil = saker.maxOf { it.sakPeriode().tilOgMed }
        val totalePeriode = Periode(totalePeriodeFra, totalePeriodeTil)

        val periodeMedTiltakspengerInit = periodeMedDefaultVerdierForTiltakspenger(totalePeriode)
        val periodeMedBarnetilleggInit = periodeMedDefaultVerdierForBarnetillegg(totalePeriode)

        val periodeMedTiltakspenger = fyllTiltakspengerPeriodenMedReelleVerdier(saker, periodeMedTiltakspengerInit)
        val periodeMedBarnetillegg =
            fyllBarnetilleggPeriodenMedReelleVerdier(saker, periodeMedBarnetilleggInit, totalePeriode, fnr)

        return kombinerTiltakspengerMedBarnetillegg(periodeMedTiltakspenger, periodeMedBarnetillegg, fnr)
    }

    private fun periodeMedDefaultVerdierForTiltakspenger(totalePeriode: Periode) =
        Periodisering(
            initiellVerdi = VedtakDetaljerKunTiltakspenger(
                antallDager = DEFAULT_ANTALL_DAGER,
                dagsats = DEFAULT_DAGSATS,
                tiltakGjennomføringsId = DEFAULT_TILTAK_GJENNOMFØRINGS_ID,
                rettighet = Rettighet.INGENTING,
                vedtakId = 0L,
                sakId = 0L,
                beslutningsdato = null,
                saksnummer = "",
                sakOpprettetDato = LocalDate.MIN,
                sakStatus = "",
            ),
            totalPeriode = totalePeriode,
        )

    private fun periodeMedDefaultVerdierForBarnetillegg(totalePeriode: Periode) =
        Periodisering(
            initiellVerdi = VedtakDetaljerBarnetillegg(
                antallDager = DEFAULT_ANTALL_DAGER,
                dagsats = DEFAULT_DAGSATS,
                antallBarn = DEFAULT_ANTALL_BARN,
                relaterteTiltak = DEFAULT_TILTAK_GJENNOMFØRINGS_ID,
                rettighet = Rettighet.INGENTING,
            ),
            totalPeriode = totalePeriode,
        )

    private fun fyllTiltakspengerPeriodenMedReelleVerdier(
        saker: List<ArenaSakMedMinstEttVedtakDTO>,
        periodeMedTiltakspengerInit: Periodisering<VedtakDetaljerKunTiltakspenger>,
    ) =
        saker
            .flatMap { it.tiltakspengerVedtak }
            .fold(periodeMedTiltakspengerInit) { periodeMedVerdier: Periodisering<VedtakDetaljerKunTiltakspenger>, arenaTiltakspengerVedtakDTO: ArenaTiltakspengerVedtakDTO ->
                val sak = saker.first { it.sakId == arenaTiltakspengerVedtakDTO.tilhørendeSakId }
                periodeMedVerdier.setVerdiForDelperiode(
                    VedtakDetaljerKunTiltakspenger(
                        antallDager = arenaTiltakspengerVedtakDTO.antallDager ?: DEFAULT_ANTALL_DAGER,
                        dagsats = arenaTiltakspengerVedtakDTO.dagsats ?: DEFAULT_DAGSATS,
                        tiltakGjennomføringsId = arenaTiltakspengerVedtakDTO.relatertTiltak ?: DEFAULT_TILTAK_GJENNOMFØRINGS_ID,
                        rettighet = Rettighet.TILTAKSPENGER,
                        vedtakId = arenaTiltakspengerVedtakDTO.vedtakId,
                        sakId = arenaTiltakspengerVedtakDTO.tilhørendeSakId,
                        beslutningsdato = arenaTiltakspengerVedtakDTO.beslutningsdato,
                        saksnummer = sak.fagsystemSakId,
                        sakOpprettetDato = sak.opprettetDato,
                        sakStatus = sak.status.navn,
                    ),
                    arenaTiltakspengerVedtakDTO.vedtaksperiode(),
                )
            }

    private fun fyllBarnetilleggPeriodenMedReelleVerdier(
        saker: List<ArenaSakMedMinstEttVedtakDTO>,
        periodeMedBarnetilleggInit: Periodisering<VedtakDetaljerBarnetillegg>,
        totalePeriode: Periode,
        fnr: String,
    ) =
        saker
            .flatMap { it.barnetilleggVedtak }
            .fold(periodeMedBarnetilleggInit) { periodeMedVerdier: Periodisering<VedtakDetaljerBarnetillegg>, arenaBarnetilleggVedtakDTO: ArenaBarnetilleggVedtakDTO ->
                if (totalePeriode.inneholderHele(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    // Legger til hele perioden
                    periodeMedVerdier.setVerdiForDelperiode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager ?: DEFAULT_ANTALL_DAGER,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats ?: DEFAULT_DAGSATS,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn ?: DEFAULT_ANTALL_BARN,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.tiltakGjennomføringsId ?: DEFAULT_TILTAK_GJENNOMFØRINGS_ID,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode(),
                    )
                } else if (totalePeriode.overlapperMed(arenaBarnetilleggVedtakDTO.vedtaksperiode())) {
                    loggAvvik(
                        Avvikstype.BARNETILLEGG_PERIODE_OVERLAPP,
                        "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) overlapper med saksperioden ($totalePeriode) " +
                            "for vedtakId ${arenaBarnetilleggVedtakDTO.vedtakId}, sakId ${arenaBarnetilleggVedtakDTO.tilhørendeSakId}",
                        fnr,
                    )
                    periodeMedVerdier.setVerdiForDelperiode(
                        VedtakDetaljerBarnetillegg(
                            antallDager = arenaBarnetilleggVedtakDTO.antallDager ?: DEFAULT_ANTALL_DAGER,
                            dagsats = arenaBarnetilleggVedtakDTO.dagsats ?: DEFAULT_DAGSATS,
                            antallBarn = arenaBarnetilleggVedtakDTO.antallBarn ?: DEFAULT_ANTALL_BARN,
                            relaterteTiltak = arenaBarnetilleggVedtakDTO.tiltakGjennomføringsId ?: DEFAULT_TILTAK_GJENNOMFØRINGS_ID,
                            rettighet = Rettighet.BARNETILLEGG,
                        ),
                        arenaBarnetilleggVedtakDTO.vedtaksperiode().overlappendePeriode(totalePeriode)!!,
                    )
                } else {
                    loggAlvorligAvvik(
                        Avvikstype.BARNETILLEGG_PERIODE_UTENFOR,
                        "Perioden for barnetillegg (${arenaBarnetilleggVedtakDTO.vedtaksperiode()}) er utenfor saksperioden ($totalePeriode) " +
                            "for vedtakId ${arenaBarnetilleggVedtakDTO.vedtakId}, sakId ${arenaBarnetilleggVedtakDTO.tilhørendeSakId}",
                        fnr,
                    )
                    // Legger ikke til noe
                    periodeMedVerdier
                }
            }

    private fun kombinerTiltakspengerMedBarnetillegg(
        periodeMedTiltakspenger: Periodisering<VedtakDetaljerKunTiltakspenger>,
        periodeMedBarnetillegg: Periodisering<VedtakDetaljerBarnetillegg>,
        fnr: String,
    ) =
        periodeMedTiltakspenger.kombiner(periodeMedBarnetillegg) { vt, vb ->
            if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG && vt.antallDager != vb.antallDager) {
                loggAvvik(
                    Avvikstype.ULIKT_ANTALL_DAGER,
                    "Vedtaket om tiltakspenger (${vt.antallDager}) og vedtaket om barnetillegg (${vb.antallDager}) har ikke samme antall dager " +
                        "for ${vt.loggKontekst()}",
                    fnr,
                )
            }
            if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG && vt.tiltakGjennomføringsId != vb.relaterteTiltak) {
                loggAvvik(
                    Avvikstype.ULIKT_RELATERT_TILTAK,
                    "Vedtaket om tiltakspenger (${vt.tiltakGjennomføringsId}) og vedtaket om barnetillegg (${vb.relaterteTiltak}) har ikke samme relaterte tiltak " +
                        "for ${vt.loggKontekst()}",
                    fnr,
                )
            }
            VedtakDetaljer(
                antallDager = vt.antallDager,
                dagsatsTiltakspenger = vt.dagsats,
                dagsatsBarnetillegg = vb.dagsats,
                antallBarn = vb.antallBarn,
                tiltakGjennomføringsId = vt.tiltakGjennomføringsId,
                rettighet = kombinerRettighet(vt, vb, fnr),
                vedtakId = vt.vedtakId,
                sakId = vt.sakId,
                beslutningsdato = vt.beslutningsdato,
                sak = VedtakDetaljer.Sak(
                    saksnummer = vt.saksnummer,
                    opprettetDato = vt.sakOpprettetDato,
                    status = vt.sakStatus,
                ),
            )
        }

    private fun kombinerRettighet(vt: VedtakDetaljerKunTiltakspenger, vb: VedtakDetaljerBarnetillegg, fnr: String) =
        if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.BARNETILLEGG) {
            Rettighet.TILTAKSPENGER_OG_BARNETILLEGG
        } else if (vt.rettighet == Rettighet.TILTAKSPENGER && vb.rettighet == Rettighet.INGENTING) {
            Rettighet.TILTAKSPENGER
        } else if (vt.rettighet == Rettighet.INGENTING && vb.rettighet == Rettighet.BARNETILLEGG) {
            // Her finnes det ikke noe tiltakspengervedtak å knytte til, så relatert tiltak fra barnetillegget er nærmeste spor i vanlig logg - identen ligger i sikkerlogg.
            loggAvvik(
                Avvikstype.BARNETILLEGG_UTEN_TILTAKSPENGER,
                "Her har vi en periode med barnetillegg men ikke tiltakspenger - det skal ikke egentlig kunne skje! " +
                    "Barnetillegget har relatert tiltak ${vb.relaterteTiltak}",
                fnr,
            )
            Rettighet.BARNETILLEGG
        } else {
            Rettighet.INGENTING
        }

    private fun VedtakDetaljerKunTiltakspenger.loggKontekst(): String =
        "vedtakId $vedtakId, sakId $sakId, saksnummer $saksnummer"

    /** Delt linje til vanlig logg og sikkerlogg - identen er PII og legges kun på sikkerlogg-linjen. */
    private fun loggAvvik(type: Avvikstype, melding: String, fnr: String) {
        LOG.warn { "$type: $melding. $SE_SIKKERLOGG" }
        Sikkerlogg.warn { "$type: $melding. Ident: $fnr" }
    }

    private fun loggAlvorligAvvik(type: Avvikstype, melding: String, fnr: String) {
        LOG.error { "$type: $melding. $SE_SIKKERLOGG" }
        Sikkerlogg.error { "$type: $melding. Ident: $fnr" }
    }
}
