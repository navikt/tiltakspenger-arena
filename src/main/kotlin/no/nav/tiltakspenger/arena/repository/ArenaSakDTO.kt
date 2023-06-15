package no.nav.tiltakspenger.arena.repository

import java.time.LocalDate
import java.time.LocalDateTime

data class ArenaSakDTO(
    val aar: Int,
    val lopenrSak: Long,
    val status: ArenaSakStatus,
    val ytelsestype: ArenaYtelse,
    val ihtVedtak: List<ArenaVedtakDTO>,
) {

    /*
    TODO: Blir det det samme? Må kanskje filtrere litt mer..

          (SELECT to_char(min(nvl(va.fra_dato,va.reg_dato)),'rrrr-mm-dd')||'T00:00:00'
          FROM vedtak va
              ,rettighettype rht
          WHERE va.sak_id=sak.sak_id
          AND va.rettighetkode = rht.rettighetkode
          AND va.vedtaktypekode NOT IN ('S')
          AND rht.sakskode IN ('AA','INDIV')
          AND rht.rettighetkode NOT IN ('AA115')
          AND va.utfallkode != 'AVBRUTT'
         ) AS "fomGyldighetsperiode"

         (SELECT DISTINCT first_value(to_char(va.til_dato,'rrrr-mm-dd')||'T00:00:00')
                   OVER (ORDER BY fra_dato DESC, til_dato DESC)
          FROM vedtak va
          WHERE va.sak_id=sak.sak_id
          AND va.vedtaktypekode IN ('O','E','G')
         ) AS "tomGyldighetsperiode"
     */
    val fomGyldighetsperiode: LocalDateTime
        get() = ihtVedtak.mapNotNull { it.fomGyldighetsdato() }.min()
    val tomGyldighetsperiode: LocalDateTime?
        get() = ihtVedtak.mapNotNull { it.tomGyldighetsdato() }.maxOrNull()
    val datoKravMottatt: LocalDate
        get() = ihtVedtak.first { it.periodetypeForYtelse == ArenaVedtakType.O }.mottattDato
    val fagsystemSakId: String
        get() = aar.toString() + lopenrSak
}
/*
ArenaDagpengeSakDTO må evt også ha feltene
    protected int antallDagerIgjen;
    protected int antallUkerIgjen;
    protected Integer antallDagerIgjenUnderPermittering;
    protected Integer antallUkerIgjenUnderPermittering;

Dagpenger og AAP må ha feltene:
    val bortfallsprosentDagerIgjen: Int?,
    val bortfallsprosentUkerIgjen: Int?,
*/
