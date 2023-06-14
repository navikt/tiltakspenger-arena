package no.nav.tiltakspenger.arena.repository

import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import java.time.LocalDate

/*
Denne klassen er basert på tjenesten hos Arena som er dokumentert her:
https://confluence.adeo.no/display/ARENA/Arena+-+Tjeneste+Webservice+-+Ytelseskontrakt_v3

Men endret så den kun returnerer tiltakspenger-data, som er litt enklere enn AAP og DP.
Vi har også endret så vi bruker mer enums, og så utfall inkluderes vedtakene.
Vi dropper også saker som kun har vedtak som oppfyller det andre kriteriet:
(Vedtakstype er Ny rettighet (O), samt at utfallskode er Nei (NEI).)

Saker som returneres i liste:

Sakstype er AAP (AA), Dagpenger (DAGP) eller Individstønad (INDIV).

Ytelsen må inneholde vedtak med fra_dato <= tom-dato i input. Dersom tom-dato er blank vil 31.12.2099 benyttes.
Ytelsen må inneholde vedtak med til_dato >= fom-dato i input. Dersom fom-dato er blank vil 01.01.1900 benyttes.
Ytelsen må inneholde vedtak som oppfyller et av følgende to alternativer:
Vedtakstype er Ny rettighet (O), Endring (E) eller Gjenopptak (G), samt at utfallskode ikke er Avbrutt (AVBRUTT) eller Nei (NEI).
Vedtakstype er Ny rettighet (O), samt at utfallskode er Nei (NEI).
Ytelsen returneres ikke dersom sakstatus er Historisert (HIST).
Alle vedtak i de returnerte sakene listes, med unntak av ytelsesvedtak av type "§11-5 Nedsatt arbeidsevne"(AA115) og avbrutte vedtak.
 */
class SakRepository(
    private val personDAO: PersonDAO = PersonDAO(),
    private val sakDAO: SakDAO = SakDAO(),
) {

    fun hentSakerForFnr(
        fnr: String,
        fom: LocalDate = LocalDate.of(1900, 1, 1),
        tom: LocalDate = LocalDate.of(2099, 12, 31),
    ): List<ArenaSakDTO> {
        sessionOf(Datasource.hikariDataSource).use {
            return it.transaction { txSession ->
                val person = personDAO.findByFnr(fnr, txSession) ?: return emptyList()
                sakDAO.findByPersonIdAndPeriode(
                    personId = person.personId,
                    fom = fom,
                    tom = tom,
                    txSession = txSession,
                )
            }
        }
    }
}
