package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import no.nav.tiltakspenger.arena.repository.sak.SakRepository
import no.nav.tiltakspenger.libs.periodisering.Periodisering
import java.time.LocalDate

class VedtakDetaljerServiceImpl(
    private val arenaSakRepository: SakRepository,
) : VedtakDetaljerService {
    override fun hentVedtakDetaljerPerioder(
        ident: String,
        fom: LocalDate,
        tom: LocalDate,
    ): Periodisering<VedtakDetaljer>? {
        val sakerFraDb = arenaSakRepository.hentSakerForFnr(fnr = ident, fom = fom, tom = tom)
        return ArenaTilVedtakDetaljerMapper.mapTiltakspengerFraArenaTilVedtaksperioder(sakerFraDb)
    }
}
