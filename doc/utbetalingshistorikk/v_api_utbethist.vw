CREATE OR REPLACE FORCE VIEW V_API_UTBETHIST
 (person_id,
  meldekort_id,
  dato_postert,
  transaksjonstype,
  periode,
  anvist_prosent,
  posteringsats,
  status,
  signatur,
  aar,
  saksnr,
  vedtaksnr,
  vedtak_id,
  postering_id,
  belop,
  tabellnavnalias_kilde,
  objekt_id_kilde,
  v_id,
  periode_uker,
  meldekort_aar,
  periode_kode,
  dato_periode_fra,
  dato_periode_til)
AS
SELECT
-------------------------------------------------------------------------------
-- Laget av             : $Author: r144167 $
-- Beskrivelse, logikk  : Kopi av siamo.v_utbethist til bruk for eksternt API
-- Revisjon             : $Revision: 38168 $
-- Endringshistorikk    : Se under
-------------------------------------------------------------------------------
-- $Revision: 38168 $
-- Pakke api_beregningsgrunnlag_v1 benytter viewet.
-- Viewet er laget for å vise intern utbetalingshistorikk i Arena for en person og finner frem følgende:
-- 1) Posteringer
-- 2) Utbetalingsgrunnlag
-- 3) Vedtak fra beregningslogg
-- 4) Vedtak fra anmerkning
-- 5) Meldekort
--
-- Endringslogg:
------------------------------------------------------------------------------
-- Dato        Hvem          Jira#     Hva
-- 28.01.2026  H.Solum       -         La til kolonnedefinisjonene slik at det er tydligere hvilke data som representerer hva. Denne endringen er bare gjort i dokumentasjonen sin versjon av viewet.
-- 03.11.2022  T.Richardsen  ARE-7893  Endret statustekst når forekomst hentes fra utbetalingsgrunnlag og postering
-- 22.10.2021  R.Hedgren     ARE-6463  EL11-21 Opprettet.
-------------------------------------------------------------------------------
    p.person_id                                                                             AS person_id,
	p.meldekort_id                                                                          AS meldekort_id,
	p.dato_postert                                                                          AS dato_postert,
	t.transaksjonstypenavn                                                                  AS transaksjonstype,
	TO_CHAR(p.dato_periode_fra,'DD-MM-YYYY')||'-'||TO_CHAR(p.dato_periode_til,'DD-MM-YYYY') AS periode,
    p.antall*20                                                                             AS anvist_prosent,
	p.posteringsats                                                                         AS posteringsats,
	'Overf�rt utbetaling'                                                                   AS status,
	p.bruker_id_saksbehandler || '/'|| p.aetatenhet_ansvarlig                               AS signatur,
	TO_CHAR(v.aar)                                                                          AS aar,
	LTRIM(TO_CHAR(v.lopenrsak,'0000000'))                                                   AS saksnr,
	LTRIM(TO_CHAR(v.lopenrvedtak,'000'))                                                    AS vedtaksnr,
	p.vedtak_id                                                                             AS vedtak_id,
	p.postering_id                                                                          AS postering_id,
	p.belop                                                                                 AS belop,
	p.tabellnavnalias_kilde                                                                 AS tabellnavnalias_kilde,
	p.objekt_id_kilde                                                                       AS objekt_id_kilde,
	DECODE(p.tabellnavnalias_kilde,'SPESUTB',v.vedtak_id_relatert,v.vedtak_id)              AS v_id,
	TO_CHAR(mp.ukenr_uke1,'09')||'-'||TO_CHAR(mp.ukenr_uke2,'09')                           AS periode_uker,
	m.aar                                                                                   AS meldekort_aar,
	m.periodekode                                                                           AS periode_kode,
	p.dato_periode_fra                                                                      AS dato_periode_fra,
	p.dato_periode_til                                                                      AS dato_periode_til
FROM 	postering p, transaksjontype t, vedtak v, meldekort m, meldekortperiode mp
WHERE t.transaksjonskode  = p.transaksjonskode
AND 	v.vedtak_id         = p.vedtak_id
AND   m.meldekort_id (+)  = p.meldekort_id
AND   mp.periodekode (+)  = m.periodekode
AND   mp.aar (+)          = m.aar
UNION ALL
-- 2) Finner utbetalinger fra utbetalingsgrunnlag
-- Dette er utbetalinger fra meldekort og spesialutbetalinger som ikke er overført økonomisystem
SELECT /*+ ORDERED */
  u.person_id                                                                                AS person_id,
	u.meldekort_id                                                                           AS meldekort_id,
	u.mod_dato                                                                               AS dato_postert,
	t.transaksjonstypenavn                                                                   AS transaksjonstype,
	TO_CHAR(u.dato_periode_fra,'DD-MM-YYYY')||'-'||TO_CHAR(u.dato_periode_til,'DD-MM-YYYY')  AS periode,
	u.antall*20                                                                              AS anvist_prosent,
	u.posteringsats                                                                          AS posteringsats,
	'Ikke overf�rt utbetaling'                                                               AS status,
	u.mod_user                                                                               AS signatur,
	TO_CHAR(v.aar)                                                                           AS aar,
	LTRIM(TO_CHAR(v.lopenrsak,'0000000'))                                                    AS saksnr,
	LTRIM(TO_CHAR(v.lopenrvedtak,'000'))                                                     AS vedtaksnr,
	u.vedtak_id                                                                              AS vedtak_id,
	NULL                                                                                     AS postering_id,
	u.belop                                                                                  AS belop,
	u.tabellnavnalias_kilde                                                                  AS tabellnavnalias_kilde,
	u.objekt_id_kilde                                                                        AS objekt_id_kilde,
	DECODE(u.tabellnavnalias_kilde,'SPESUTB',v.vedtak_id_relatert, v.vedtak_id)              AS v_id,
	to_char(mp.ukenr_uke1,'09')||'-'||to_char(mp.ukenr_uke2,'09')                            AS periode_uker,
	m.aar                                                                                    AS meldekort_aar,
	m.periodekode                                                                            AS periode_kode,
	u.dato_periode_fra                                                                       AS dato_periode_fra,
	u.dato_periode_til                                                                       AS dato_periode_til
FROM 	utbetalingsgrunnlag u, transaksjontype t, vedtak v, meldekort m, meldekortperiode mp
WHERE t.transaksjonskode  = u.transaksjonskode
AND 	m.meldekort_id (+)  = u.meldekort_id
AND   mp.periodekode (+)  = m.periodekode
AND   mp.aar         (+)  = m.aar
AND 	v.vedtak_id 	      = u.vedtak_id
UNION ALL
-- 3) Finner vedtak fra beregningsloggen. Tar ikke med vedtak som ble funnet i 1) og 2)
-- Dette er vedtak som er beregnet, men som ikke førte til noen utbetaling
SELECT
    b.person_id                                                                              AS person_id,
	b.objekt_id                                                                              AS meldekort_id,
	b.reg_dato                                                                               AS dato_postert,
	r.rettighetnavn                                                                          AS transaksjonstype,
	TO_CHAR(b.dato_fra,'DD-MM-YYYY')||'-'||TO_CHAR(b.dato_til,'DD-MM-YYYY')                  AS periode,
	0                                                                                        AS anvist_prosent,
	0                                                                                        AS posteringsats,
	be.beregningstatusnavn                                                                   AS status,
	b.reg_user                                                                               AS signatur,
	TO_CHAR(v.aar)                                                                           AS aar,
	LTRIM(TO_CHAR(v.lopenrsak,'0000000'))                                                    AS saksnr,
	LTRIM(TO_CHAR(v.lopenrvedtak,'000'))                                                     AS vedtaksnr,
	b.vedtak_id                                                                              AS vedtak_id,
	NULL                                                                                     AS postering_id,
	0                                                                                        AS belop,
	b.tabellnavnalias                                                                        AS tabellnavnalias_kilde,
	b.objekt_id                                                                              AS objekt_id_kilde,
	b.vedtak_id                                                                              AS v_id,
	to_char(mk.ukenr_uke1,'09')||'-'||to_char(mk.ukenr_uke2,'09')                            AS periode_uker,
	m.aar                                                                                    AS meldekort_aar,
	m.periodekode                                                                            AS periode_kode,
	b.dato_fra                                                                               AS dato_periode_fra,
	GREATEST(b.dato_til,b.dato_fra)                                                          AS dato_periode_til
FROM  beregningslogg b, vedtak v, rettighettype r,
      meldekort m, beregningstatus be, meldekortperiode mk
WHERE b.vedtak_id = v.vedtak_id
AND   v.rettighetkode = r.rettighetkode
AND   b.tabellnavnalias = 'MKORT'
AND   b.objekt_id = m.meldekort_id
AND   be.beregningstatuskode = m.beregningstatuskode
AND   m.periodekode = mk.periodekode
AND   m.aar = mk.aar
AND   NOT EXISTS (
        SELECT 1
		    FROM  utbetalingsgrunnlag u
		    WHERE u.meldekort_id  = b.objekt_id
		    AND   u.vedtak_id     = b.vedtak_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  postering p
		    WHERE p.meldekort_id  = b.objekt_id
		    AND   p.vedtak_id     = b.vedtak_id)
UNION ALL
-- 4) Finner vedtak ut fra anmerkning. Tar ikke med vedtak som ble funnet i 1), 2) og 3).
-- Dette er vedtak som er forsøkt beregnet, men som feilet og fikk laget en anmerkning på seg
-- Henter kun vedtak knyttet til anmerkningen med den laveste id'en. Dette for at vedtaket kun skal hentes en gang
SELECT
    m.person_id                                                                             AS person_id,
	a.objekt_id                                                                             AS meldekort_id,
	a.reg_dato                                                                              AS dato_postert,
	r.rettighetnavn                                                                         AS transaksjonstype,
	TO_CHAR(mk.dato_fra,'DD-MM-YYYY')||'-'||TO_CHAR(mk.dato_til,'DD-MM-YYYY')               AS periode,
	0                                                                                       AS anvist_prosent,
	0                                                                                       AS posteringsats,
	be.beregningstatusnavn                                                                  AS status,
	a.reg_user                                                                              AS signatur,
	TO_CHAR(v.aar)                                                                          AS aar,
	LTRIM(TO_CHAR(v.lopenrsak,'0000000'))                                                   AS saksnr,
	LTRIM(TO_CHAR(v.lopenrvedtak,'000'))                                                    AS vedtaksnr,
	a.vedtak_id                                                                             AS vedtak_id,
	NULL                                                                                    AS postering_id,
	0                                                                                       AS belop,
	a.tabellnavnalias                                                                       AS tabellnavnalias_kilde,
	a.objekt_id                                                                             AS objekt_id_kilde,
	a.vedtak_id                                                                             AS v_id,
	TO_CHAR(mk.ukenr_uke1,'09')||'-'||TO_CHAR(mk.ukenr_uke2,'09')                           AS periode_uker,
	m.aar                                                                                   AS meldekort_aar,
	m.periodekode                                                                           AS periode_kode,
	mk.dato_fra                                                                             AS dato_periode_fra,
	mk.dato_til                                                                             AS dato_periode_til
FROM  anmerkning a, vedtak v, rettighettype r,
      meldekort m, beregningstatus be, meldekortperiode mk
WHERE a.vedtak_id             = v.vedtak_id
AND   v.rettighetkode         = r.rettighetkode
AND   a.tabellnavnalias       = 'MKORT'
AND   a.objekt_id             = m.meldekort_id
AND   be.beregningstatuskode  = m.beregningstatuskode
AND   m.periodekode           = mk.periodekode
AND   m.aar                   = mk.aar
AND   NOT EXISTS (
        SELECT /*+ INDEX(a2 ANMERK_I) */ 1
        FROM  anmerkning a2
        WHERE a2.anmerkning_id  < a.anmerkning_id
        AND   a2.vedtak_id      = a.vedtak_id
        AND   a2.objekt_id      = a.objekt_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  utbetalingsgrunnlag u
		    WHERE u.meldekort_id  = a.objekt_id
		    AND   u.vedtak_id     = a.vedtak_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  postering p
		    WHERE p.meldekort_id  = a.objekt_id
		    AND   p.vedtak_id     = a.vedtak_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  beregningslogg b
		    WHERE b.objekt_id = m.meldekort_id)
UNION ALL
-- 5) Finner meldekort som er beregnet, men som ikke ble funnet i 1), 2) ,3) og 4)
-- Dette finner meldekort som er forsøkt beregnet, men som feilet og fikk laget en anmerkning på seg
SELECT
    m.person_id                                                                             AS person_id,
	m.meldekort_id                                                                          AS meldekort_id,
	m.mod_dato                                                                              AS dato_postert,
	mt.meldekorttypenavn                                                                    AS transaksjonstype,
	TO_CHAR(mk.dato_fra,'DD-MM-YYYY')||'-'||TO_CHAR(mk.dato_til,'DD-MM-YYYY')               AS periode,
	0                                                                                       AS anvist_prosent,
	0                                                                                       AS posteringsats,
	be.beregningstatusnavn                                                                  AS status,
	m.mod_user                                                                              AS signatur,
	NULL                                                                                    AS aar,
	NULL                                                                                    AS saksnr,
	NULL                                                                                    AS vedtaksnr,
	NULL                                                                                    AS vedtak_id,
	NULL                                                                                    AS postering_id,
	0                                                                                       AS belop,
	'MKORT'                                                                                 AS tabellnavnalias_kilde,
	m.meldekort_id                                                                          AS objekt_id_kilde,
	0                                                                                       AS v_id,
	TO_CHAR(mk.ukenr_uke1,'09')||'-'||TO_CHAR(mk.ukenr_uke2,'09')                           AS periode_uker,
	mk.aar                                                                                  AS meldekort_aar,
	mk.periodekode                                                                          AS periode_kode,
	mk.dato_fra                                                                             AS dato_periode_fra,
	mk.dato_til                                                                             AS dato_periode_til
FROM 	meldekort m, meldekortperiode mk, meldekorttype mt, beregningstatus be
WHERE m.aar                   = mk.aar
AND 	m.periodekode           = mk.periodekode
AND 	m.meldekortkode         = mt.meldekortkode
AND 	be.beregningstatuskode  = m.beregningstatuskode
AND   m.beregningstatuskode   IN ('FERDI','FEIL','VENTE','KLAR')
AND   NOT EXISTS (
        SELECT 1
		    FROM  utbetalingsgrunnlag u
		    WHERE u.meldekort_id = m.meldekort_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  postering p
		    WHERE p.meldekort_id = m.meldekort_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  beregningslogg b
		    WHERE b.objekt_id = m.meldekort_id)
AND   NOT EXISTS (
        SELECT 1
		    FROM  anmerkning a
		    WHERE a.objekt_id = m.meldekort_id
		    AND   a.vedtak_id IS NOT NULL);
