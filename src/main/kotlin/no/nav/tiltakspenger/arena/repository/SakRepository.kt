package no.nav.tiltakspenger.arena.repository

import io.github.oshai.kotlinlogging.KotlinLogging
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import no.nav.tiltakspenger.libs.logging.Sikkerlogg
import java.time.LocalDate

/*
Denne klassen er basert på tjenesten hos Arena som er dokumentert her:
https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+Ytelseskontrakt_v3

Men endret så den kun returnerer tiltakspenger-data, som er litt enklere enn AAP og DP.
Vi har også endret så vi bruker mer enums, og så utfall inkluderes vedtakene.

Saker som returneres i liste:

Ytelsen må inneholde vedtak med fra_dato <= tom-dato i input. Dersom tom-dato er blank vil 31.12.2299 benyttes.
Ytelsen må inneholde vedtak med til_dato >= fom-dato i input. Dersom fom-dato er blank vil 01.01.1900 benyttes.
Ytelsen må inneholde vedtak hvor:
- typen må være Ny rettighet (O), Endring (E) eller Gjenopptak (G)
- utfallskode må ikke være Avbrutt (AVBRUTT) eller Nei (NEI)
Ytelsen må ha minst ett vedtak som oppfyller kravene for å returneres,
og bare vedtakene som oppfyller kravene inkluderes
Ytelsen returneres ikke dersom sakstatus er Historisert (HIST).
 */
class SakRepository(
    private val personDAO: PersonDAO = PersonDAO(),
    private val sakDAO: SakDAO = SakDAO(),
) {
    companion object {
        private val LOG = KotlinLogging.logger {}
    }

    fun hentSakerForFnr(
        fnr: String,
        fom: LocalDate = LocalDate.of(1900, 1, 1),
        tom: LocalDate = LocalDate.of(2999, 12, 31),
    ): List<ArenaSakMedMinstEttVedtakDTO> {
        val saker = hentAlleSakerForFnr(fnr)
            .logVedtakMedUgyldigeVerdier()
            .kunSakerMedVedtakInnenforPeriode(fom, tom)
            .map { ArenaSakMedMinstEttVedtakDTO(it) }
        LOG.info { "Antall filtrerte saker er ${saker.size}" }
        return saker
    }

    private fun hentAlleSakerForFnr(
        fnr: String,
    ): List<ArenaSakDTO> {
        sessionOf(Datasource.hikariDataSource).use {
            it.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession)
                if (person == null) {
                    LOG.info { "Fant ikke person" }
                    Sikkerlogg.info { "Fant ikke person med ident $fnr" }
                    return emptyList()
                }
                val saker = sakDAO.findByPersonId(
                    personId = person.personId,
                    txSession = txSession,
                )
                LOG.info { "Antall saker er ${saker.size}" }
                return saker
            }
        }
    }
}
