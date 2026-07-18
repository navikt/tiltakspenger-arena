package no.nav.tiltakspenger.arena.repository.vedtak

import no.nav.tiltakspenger.arena.repository.ArenaAktivitetFase
import no.nav.tiltakspenger.arena.repository.ArenaRettighet
import no.nav.tiltakspenger.arena.repository.ArenaUtfall
import no.nav.tiltakspenger.arena.repository.ArenaVedtakStatus
import no.nav.tiltakspenger.arena.repository.ArenaVedtakType
import no.nav.tiltakspenger.libs.periode.Periode
import java.time.LocalDate
import java.time.LocalDateTime

// Har ikke lagt til alle vedtaksfakta her ennå
//
// Slik tolkes et gjeldende tiltakspenger-vedtak fra Arena-kodene (se ArenaKodeverk):
// - rettighet BASI = tiltakspenger (basisytelse før 2014, ytelse INDIV).
// - status IVERK = iverksatt/effektuert vedtak.
// - vedtakType O/G/E (ny rettighet/gjenopptak/endring) gir eller endrer en løpende rettighet; S=stans, T=bortfall m.fl. gjør ikke.
// - utfall NEI (avslag) og AVBRUTT regnes ikke som gjeldende; JA gjør.
// - tomVedtaksperiode == null betyr åpen/løpende periode.
//
// TODO: Navn og plassering bør revurderes.
// Dette er en intern representasjon av Arena-data (mapping fra Arena-tabellene), ikke et transfer-objekt mot konsumentene — wire-kontrakten er response-DTO-ene i routes/ — så «DTO»-suffikset er misvisende.
// Skal noe her deles som kontrakt mot tiltakspenger-*, hører det hjemme i en fellesmodul i libs; ellers bør typen få et navn som reflekterer at den er Arena-intern.
data class ArenaTiltakspengerVedtakDTO(
    val vedtakId: Long,
    val tilhørendeSakId: Long,
    /** Vedtakstype i Arena; O (ny rettighet), G (gjenopptak) og E (endring) gir/endrer en løpende rettighet, øvrige (S=stans m.fl.) gjør ikke. */
    val vedtakType: ArenaVedtakType,
    val uttaksgrad: Int,
    val fomVedtaksperiode: LocalDate,
    /** Til-dato for vedtaksperioden; null betyr åpen/løpende periode (ingen sluttdato). */
    val tomVedtaksperiode: LocalDate?,
    /** Vedtaksstatus i Arena; IVERK (iverksatt) markerer et gjeldende, effektuert vedtak. */
    val status: ArenaVedtakStatus,
    /** Rettighetstype i Arena; et tiltakspenger-vedtak har BASI («Tiltakspenger (basisytelse før 2014)», ytelse INDIV). */
    val rettighettype: ArenaRettighet,
    val aktivitetsfase: ArenaAktivitetFase,
    val dagsats: Int?,
    val beslutningsdato: LocalDate?,
    val mottattDato: LocalDate,
    val registrertDato: LocalDate?,
    /** Utfall i Arena; NEI (avslag) og AVBRUTT regnes ikke som gjeldende vedtak, JA gjør. */
    val utfall: ArenaUtfall,
    val antallDager: Double?,
    val opprinneligTomVedtaksperiode: LocalDate?,
    val relatertTiltak: String?,
) {
    fun fomGyldighetstidspunkt(): LocalDateTime = fomVedtaksperiode.atStartOfDay()
    fun tomGyldighetstidspunkt(): LocalDateTime? = tomVedtaksperiode?.atStartOfDay()

    fun vedtaksperiode(): Periode = Periode(fomVedtaksperiode, tomVedtaksperiode ?: LocalDate.MAX)
}
