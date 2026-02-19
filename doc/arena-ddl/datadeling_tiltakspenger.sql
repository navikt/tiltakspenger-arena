--- DDLer bestilt fra Arena for å kunne lage en lokal "kopi" av viewet vårt til Arena-databasen, slik at vi kan skrive
--- tester litt enklere. Det er allerede noe datastruktur i test/resources/local-migrations/V1000__sak.sql som kan utbroderes
--- med informasjon fra denne filen.
-- https://trello.com/c/WR3F14RH/1883-skrive-tester-i-tiltakspenger-arena-for-nye-apier
--------------------------------------------------------
--  File created - onsdag-februar-11-2026
--------------------------------------------------------
spool datadeling_tiltakspenger.txt
--------------------------------------------------------
--  DDL for Type TYPE_POSTERINGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "TYPE_POSTERINGER" AS OBJECT (
    posteringid               NUMBER(11),
    korrigerttransaksjonskode VARCHAR2(5),
    belopsdifferanse          NUMBER(12,2),
    postertdato               DATE,
    periodefra                DATE,
    periodetil                DATE,
    transaksjonskode          VARCHAR2(5),
    kapittel                  VARCHAR2(4),
    post                      VARCHAR2(2),
    underpost                 VARCHAR2(3),
    artkode                   VARCHAR2(5),
    kontostedkode             VARCHAR2(5)
);
/

--------------------------------------------------------
--  DDL for Type TABTYPE_POSTERINGER
--------------------------------------------------------

  CREATE OR REPLACE EDITIONABLE TYPE "TABTYPE_POSTERINGER" IS
    VARRAY(1000) OF type_posteringer;
/

--------------------------------------------------------
--  DDL for Table ANMERKNING
--------------------------------------------------------

  CREATE TABLE "ANMERKNING"
   (	"ANMERKNINGKODE" VARCHAR2(5),
	"REG_USER" VARCHAR2(8),
	"REG_DATO" DATE,
	"VERDI" NUMBER(5,0),
	"ANMERKNING_ID" NUMBER,
	"TABELLNAVNALIAS" VARCHAR2(10),
	"OBJEKT_ID" NUMBER,
	"VEDTAK_ID" NUMBER,
	"PARTISJON" NUMBER(8,0),
	"MOD_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"VERDI2" NUMBER(5,0)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "ANMERKNING"."ANMERKNINGKODE" IS 'Referanse til ANMERKNINGTYPE';
   COMMENT ON COLUMN "ANMERKNING"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
   COMMENT ON COLUMN "ANMERKNING"."REG_DATO" IS 'Angir tidspunkt for nÃ¥r raden ble opprettet';
   COMMENT ON COLUMN "ANMERKNING"."VERDI" IS 'Flettes inn i subtitusjonsparameter 1 i beskrivelsen i anmerkningtype.';
   COMMENT ON COLUMN "ANMERKNING"."ANMERKNING_ID" IS 'Unik ID for anmerkningen';
   COMMENT ON COLUMN "ANMERKNING"."TABELLNAVNALIAS" IS 'Angir hva anmerkningen gjelder. Sammen med objekt_id er det en entydig referanse.';
   COMMENT ON COLUMN "ANMERKNING"."OBJEKT_ID" IS 'Referanse til det anmerkningen gjelder. Sammen med objekt_id er det en entydig referanse.';
   COMMENT ON COLUMN "ANMERKNING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "ANMERKNING"."PARTISJON" IS 'Angir partisjonsnÃ¸kkelen ifbm. Historiseringsbatchen';
   COMMENT ON COLUMN "ANMERKNING"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
   COMMENT ON COLUMN "ANMERKNING"."MOD_DATO" IS 'Angir tidspunkt for nÃ¥r raden sist ble endret';
   COMMENT ON COLUMN "ANMERKNING"."VERDI2" IS 'Flettes inn i subtitusjonsparameter 2 i beskrivelsen i anmerkningtype.';
   COMMENT ON TABLE "ANMERKNING"  IS 'Inneholder alle forskjellige anmerkninger som kan knyttes til (hovedsakelig) meldekort';
--------------------------------------------------------
--  DDL for Table ANMERKNINGTYPE
--------------------------------------------------------

  CREATE TABLE "ANMERKNINGTYPE"
   (	"ANMERKNINGKODE" VARCHAR2(5),
	"ANMERKNINGNAVN" VARCHAR2(100),
	"BESKRIVELSE" VARCHAR2(255),
	"HENDELSETYPEKODE" VARCHAR2(7),
	 CONSTRAINT "ANMTYP_PK" PRIMARY KEY ("ANMERKNINGKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "ANMERKNINGTYPE"."ANMERKNINGKODE" IS 'Entydig kode for anmerkningtypen';
   COMMENT ON COLUMN "ANMERKNINGTYPE"."ANMERKNINGNAVN" IS 'Navn pÃ¥ anmerkningtypen';
   COMMENT ON COLUMN "ANMERKNINGTYPE"."BESKRIVELSE" IS 'Beskrivelse av anmerkningtypen (kan inkludere flettefelt for verdien fra ANMERKNING)';
   COMMENT ON COLUMN "ANMERKNINGTYPE"."HENDELSETYPEKODE" IS 'Referanse til HENDELSETYPE';
   COMMENT ON TABLE "ANMERKNINGTYPE"  IS 'Kodetabell for anmerkningtyper';
--------------------------------------------------------
--  DDL for Table BEREGNINGSLOGG
--------------------------------------------------------

  CREATE TABLE "BEREGNINGSLOGG"
   (	"PERSON_ID" NUMBER,
	"VEDTAK_ID" NUMBER,
	"TABELLNAVNALIAS" VARCHAR2(10),
	"OBJEKT_ID" NUMBER(10,0),
	"DATO_FRA" DATE,
	"DATO_TIL" DATE,
	"REG_USER" VARCHAR2(8),
	"REG_DATO" DATE,
	"KOMMENTAR" VARCHAR2(30),
	"PARTISJON" NUMBER(8,0),
	"BEREGNINGSLOGG_ID" NUMBER INVISIBLE GENERATED ALWAYS AS IDENTITY MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 1 CACHE 20 ORDER  NOCYCLE  NOKEEP  NOSCALE
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "BEREGNINGSLOGG"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."TABELLNAVNALIAS" IS 'Angir tabell som beregningen i tillegg er knyttet til';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."OBJEKT_ID" IS 'Angir ID''en til raden for tabellen angitt i TABELLNAVNALIAS';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."DATO_FRA" IS 'Beregningsledd-perioden for beregningen (fra-dato)';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."DATO_TIL" IS 'Beregningsledd-perioden for beregningen (til-dato)';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."KOMMENTAR" IS 'Kommentar til beregningen';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "BEREGNINGSLOGG"."BEREGNINGSLOGG_ID" IS 'Generert Oracle-sekvens som entydig identifiserer raden';
   COMMENT ON TABLE "BEREGNINGSLOGG"  IS 'Logg over utfÃ¸rte beregninger';
--------------------------------------------------------
--  DDL for Table BEREGNINGSTATUS
--------------------------------------------------------

  CREATE TABLE "BEREGNINGSTATUS"
   (	"BEREGNINGSTATUSKODE" VARCHAR2(5),
	"BEREGNINGSTATUSNAVN" VARCHAR2(30),
	 CONSTRAINT "BERSTAT_PK" PRIMARY KEY ("BEREGNINGSTATUSKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "BEREGNINGSTATUS"."BEREGNINGSTATUSKODE" IS 'Entydig kode';
   COMMENT ON COLUMN "BEREGNINGSTATUS"."BEREGNINGSTATUSNAVN" IS 'Navn pÃ¥ beregningsstatusen';
   COMMENT ON TABLE "BEREGNINGSTATUS"  IS 'Beregningsstatuser';
--------------------------------------------------------
--  DDL for Table BETALINGSPLAN
--------------------------------------------------------

  CREATE TABLE "BETALINGSPLAN"
   (	"VEDTAK_ID" NUMBER,
	"UTBETALINGNR" NUMBER(10,0),
	"DATO_UTBETALING" DATE,
	"STATUS_NYDOK" VARCHAR2(1) DEFAULT 'J',
	"BELOPKODE" VARCHAR2(10),
	"BELOP" NUMBER(8,2),
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"STATUS_KLAR" VARCHAR2(1),
	"DATO_BILAGSFRIST" DATE,
	"STATUS_UTBETGRUNNLAG" VARCHAR2(1) DEFAULT 'N',
	"BETALINGSPLAN_ID" NUMBER,
	"BELOP_TIL_UTBETALING" NUMBER(8,2),
	"PARTISJON" NUMBER(8,0),
	"ETTERBETALING" VARCHAR2(1) DEFAULT 'N',
	"BETALINGSPLAN_ID_RELATERT" NUMBER
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "BETALINGSPLAN"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "BETALINGSPLAN"."UTBETALINGNR" IS 'LÃ¸penummer for vedtakets utbetalinger';
   COMMENT ON COLUMN "BETALINGSPLAN"."DATO_UTBETALING" IS 'Dato for utbetalingen';
   COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_NYDOK" IS 'Angir om dokumentasjon foreligger (J) eller mangler (N)';
   COMMENT ON COLUMN "BETALINGSPLAN"."BELOPKODE" IS 'Refererer til KONTOTYPE';
   COMMENT ON COLUMN "BETALINGSPLAN"."BELOP" IS 'BelÃ¸pet for betalingsplanen';
   COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_KLAR" IS 'Angir om betalingsplanen er klar for utbetaling';
   COMMENT ON COLUMN "BETALINGSPLAN"."DATO_BILAGSFRIST" IS 'Frist for mottak av bilag';
   COMMENT ON COLUMN "BETALINGSPLAN"."STATUS_UTBETGRUNNLAG" IS 'Angir om betalingsplanen er utbetalt (J) eller ikke (N)';
   COMMENT ON COLUMN "BETALINGSPLAN"."BETALINGSPLAN_ID" IS 'Unik ID';
   COMMENT ON COLUMN "BETALINGSPLAN"."BELOP_TIL_UTBETALING" IS 'BelÃ¸p som skal utbetales';
   COMMENT ON COLUMN "BETALINGSPLAN"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "BETALINGSPLAN"."ETTERBETALING" IS 'Angir om betalingsplanen gjelder etterbetaling. Lovlige verdier skal vÃ¦re J/N, og verdi settes default til N.';
   COMMENT ON COLUMN "BETALINGSPLAN"."BETALINGSPLAN_ID_RELATERT" IS 'Referanse til annen betalingsplan som inneholder etterbetaling for betalingsplanen';
   COMMENT ON TABLE "BETALINGSPLAN"  IS 'Skal gi mulighet for Ã¥ legge opp en betalingsplan slik at et vedtak kan gi flere utbetalinger.';
--------------------------------------------------------
--  DDL for Table FEILUTBET_OVERFORING_HIST
--------------------------------------------------------

  CREATE TABLE "FEILUTBET_OVERFORING_HIST"
   (	"FEILUTBET_OVERFORING_ID" NUMBER,
	"FODSELSNR" VARCHAR2(11),
	"AAR_SAK" NUMBER(4,0),
	"LOPENRSAK" NUMBER(7,0),
	"LOPENRVEDTAK" NUMBER,
	"VEDTAK_ID" NUMBER,
	"VEDTAK_ID_RELATERT" NUMBER,
	"VEDTAKTYPEKODE" VARCHAR2(10),
	"STATUS" VARCHAR2(20) DEFAULT 'UBEHANDLET',
	"DATO_FRA" DATE,
	"DATO_TIL" DATE,
	"RENTETILLEGG" NUMBER(8,2),
	"FORELDET_FOR_DATO" DATE,
	"FORELDETBELOP" NUMBER(12,2),
	"STONADTYPE" VARCHAR2(10),
	"REFERANSE" VARCHAR2(20),
	"SKYLDDELINGSGRAD" NUMBER,
	"TILBAKEBETALINGSBELOP" NUMBER(12,2),
	"KJORING_ID" NUMBER,
	"POSTERINGER" "TABTYPE_POSTERINGER" ,
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"GRUNNLAGSBELOP_FOR_SKYLDDELING" NUMBER(12,2)
   )
 VARRAY "POSTERINGER" STORE AS SECUREFILE LOB ;

   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FEILUTBET_OVERFORING_ID" IS 'Sekvensgenerert ID, PrimÃ¦rnÃ¸kkel';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FODSELSNR" IS 'FÃ¸dselsnummer';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."AAR_SAK" IS 'Ã…ret feilutbetalingssaken i ARENA ble opprettet.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."LOPENRSAK" IS 'LÃ¸penummer for feilutbetalingssaken. ';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."LOPENRVEDTAK" IS 'LÃ¸penummer for tilbakebetalingsvedtak.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAK_ID" IS 'Vedtak Id for feilutbetalingsvedtak';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAK_ID_RELATERT" IS 'Opprinnelig vedtaksidentifikator ved endring, gjenopptak eller stansvedtak.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."VEDTAKTYPEKODE" IS 'Mulige verdier, O - Ny rettighet, E - Endring, G - Gjenopptak, S -Stans';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."STATUS" IS 'Status';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."DATO_FRA" IS 'Fra dato for tilbakebetalingsvedtak.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."DATO_TIL" IS 'Til dato for tilbakebetalingsvedtak.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."RENTETILLEGG" IS 'Rentetillegg som legges pÃ¥ ved uaktsomhet.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FORELDET_FOR_DATO" IS 'Posteringer fÃ¸r denne dato er foreldet.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."FORELDETBELOP" IS 'Totalt foreldet belÃ¸p.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."STONADTYPE" IS 'Refererer til vedtakets rettighetkode i ARENA - skal alltid vÃ¦re TILBBET.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REFERANSE" IS 'Ã…r, lÃ¸penummer sak, lÃ¸penummer vedtak.  eks 20220080406001';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."SKYLDDELINGSGRAD" IS 'Skylddelingsgrad angis dersom tilbakebetalingsbelÃ¸pet skal reduseres pÃ¥ grunn av fordelt skyld.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."TILBAKEBETALINGSBELOP" IS 'Totalt tilbakebetalingsbelÃ¸p uten renter.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."KJORING_ID" IS 'Referer til kjÃ¸ringsid i batch-loggen gs_logg_detaljer.';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."POSTERINGER" IS 'En collection type, varray med et maks antall rader(1000) med posteringsdetaljer';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REG_DATO" IS 'Angir tidspunktet for nÃ¥r raden ble opprettet';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."MOD_DATO" IS 'Angir tidspunktet for nÃ¥r raden sist ble endret';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
   COMMENT ON COLUMN "FEILUTBET_OVERFORING_HIST"."GRUNNLAGSBELOP_FOR_SKYLDDELING" IS 'GrunnlagsbelÃ¸p for skylddeling';
   COMMENT ON TABLE "FEILUTBET_OVERFORING_HIST"  IS 'Tabell med grunnlagsdata for overfÃ¸ring av feilutbelainger til OS/UR';
--------------------------------------------------------
--  DDL for Table MELDEGRUPPETYPE
--------------------------------------------------------

  CREATE TABLE "MELDEGRUPPETYPE"
   (	"MELDEGRUPPEKODE" VARCHAR2(5),
	"MELDEGRUPPENAVN" VARCHAR2(80),
	"NIVAA" NUMBER(1,0),
	"STATUS_VERDILISTE" VARCHAR2(1),
	 CONSTRAINT "MGRPTYP_PK" PRIMARY KEY ("MELDEGRUPPEKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "MELDEGRUPPETYPE"."MELDEGRUPPEKODE" IS 'Entydlig kode som identifiserer en meldegruppetype';
   COMMENT ON COLUMN "MELDEGRUPPETYPE"."MELDEGRUPPENAVN" IS 'Navn pÃ¥ meldegruppetypen';
   COMMENT ON COLUMN "MELDEGRUPPETYPE"."NIVAA" IS 'Definerer rang for meldegruppetyper. Meldegruppe av hÃ¸yere rang skal overstyre meldegruppe av lavere rang';
   COMMENT ON COLUMN "MELDEGRUPPETYPE"."STATUS_VERDILISTE" IS 'Definerer om meldegruppetypen skal vises i lister for valg av meldegruppetype';
   COMMENT ON TABLE "MELDEGRUPPETYPE"  IS 'Liste over lovlige meldegruppetyper. Meldegruppe av hÃ¸yere rang skal overstyre meldegruppe av lavere rang, dette defineres i kolonnen NIVAA';
--------------------------------------------------------
--  DDL for Table MELDEKORT
--------------------------------------------------------

  CREATE TABLE "MELDEKORT"
   (	"MELDEKORT_ID" NUMBER,
	"PERSON_ID" NUMBER,
	"DATO_INNKOMMET" DATE,
	"DATO_UTSENDT" DATE,
	"MKSREFERANSE" VARCHAR2(21),
	"MELDEKORTKODE" VARCHAR2(5),
	"MKSKORTKODE" VARCHAR2(2),
	"STATUS_ARBEIDET" VARCHAR2(1),
	"STATUS_FERIE" VARCHAR2(1) DEFAULT 'N',
	"STATUS_KURS" VARCHAR2(1) DEFAULT NULL,
	"STATUS_NYTT_MELDEKORT" VARCHAR2(1) DEFAULT 'I',
	"STATUS_SYK" VARCHAR2(1) DEFAULT NULL,
	"STATUS_PERIODESPOERSMAAL" VARCHAR2(1) DEFAULT 'N',
	"STATUS_SOEKER_DAGPENGER" VARCHAR2(1),
	"STATUS_ANNETFRAVAER_ATTF" VARCHAR2(1) DEFAULT NULL,
	"STATUS_ATTFORINGSBISTAND" VARCHAR2(1) DEFAULT 'I',
	"STATUS_ATTFORINGSTILTAK" VARCHAR2(1) DEFAULT 'I',
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"AAR" NUMBER(4,0),
	"PERIODEKODE" VARCHAR2(2),
	"BEREGNINGSTATUSKODE" VARCHAR2(5),
	"STATUS_ANNETFRAVAER" VARCHAR2(1),
	"STATUS_FORTSATT_ARBEIDSOKER" VARCHAR2(1),
	"FEIL_PAA_KORT" VARCHAR2(1),
	"VEILEDNING" VARCHAR2(1),
	"KOMMENTAR" VARCHAR2(255),
	"MELDEGRUPPEKODE" VARCHAR2(5),
	"RETURBREVKODE" VARCHAR2(2),
	"AB_POSTKODE" VARCHAR2(1),
	"MELDEKORT_ID_RELATERT" NUMBER,
	"PARTISJON" NUMBER(8,0)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "MELDEKORT"."MELDEKORT_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "MELDEKORT"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "MELDEKORT"."DATO_INNKOMMET" IS 'Meldedato. Dato nÃ¥r meldekort har blitt mottatt.';
   COMMENT ON COLUMN "MELDEKORT"."DATO_UTSENDT" IS 'Dato for utsending av meldekort';
   COMMENT ON COLUMN "MELDEKORT"."MKSREFERANSE" IS 'Meldekortinformasjon. Referanse til et scannet meldekort hos AMELDING (arkivnÃ¸kkel) dersom papirkort';
   COMMENT ON COLUMN "MELDEKORT"."MELDEKORTKODE" IS 'Referanse til MELDEKORTKODE. Refererer til MELDEKORTPERIODEBRUK sammen med AAR og PERIODEKODE. ''DP'' eller ''AT''';
   COMMENT ON COLUMN "MELDEKORT"."MKSKORTKODE" IS 'Referanse til MKSKORTTYPE. Lovlige meldekorttyper (elektronisk, papir, manuelt osv)';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_ARBEIDET" IS 'Svar pÃ¥ om bruker har arbeidet i perioden';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_FERIE" IS 'Svar pÃ¥ om bruker har hatt ferie i perioden';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_KURS" IS 'Svar pÃ¥ om bruker har vÃ¦rt i utdanning/tiltak i perioden';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_NYTT_MELDEKORT" IS 'Ikke i bruk.';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_SYK" IS 'Svar pÃ¥ om bruker har vÃ¦rt syk i perioden';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_PERIODESPOERSMAAL" IS 'Svar pÃ¥ om bruker Ã¸nsker forskudd pÃ¥ utbetaling for neste periode';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_SOEKER_DAGPENGER" IS 'Ikke i bruk. Historisk fra omlegging av meldekortlÃ¸sning i 2005. Brukes ikke for visning av gamle meldekort';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_ANNETFRAVAER_ATTF" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra fÃ¸r omlegging av meldekortlÃ¸sning i 2005';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_ATTFORINGSBISTAND" IS 'Ikke i bruk. Brukes for visning av gamle meldekort fra fÃ¸r omlegging av meldekortlÃ¸sning i 2005';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_ATTFORINGSTILTAK" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra fÃ¸r omlegging av meldekortlÃ¸sning i 2005';
   COMMENT ON COLUMN "MELDEKORT"."AAR" IS 'Ã…r. Refererer til MELDEKORTPERIODEBRUK sammen med periodekode og meldekortkode';
   COMMENT ON COLUMN "MELDEKORT"."PERIODEKODE" IS 'Periodenummer med ledene null (01-53). Refererer til MELDEKORTPERIODEBRUK sammen med aar og meldekortkode';
   COMMENT ON COLUMN "MELDEKORT"."BEREGNINGSTATUSKODE" IS 'Referanse til BEREGNINGSTATUS. Definerer om meldekort er klar for beregning, ferdig beregnet eller feilaktig, etc.';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_ANNETFRAVAER" IS 'Svar pÃ¥ om bruker av andre grunner ikke vÃ¦rt arbeidssÃ¸ker';
   COMMENT ON COLUMN "MELDEKORT"."STATUS_FORTSATT_ARBEIDSOKER" IS 'Ã˜nsker bruker fremdeles Ã¥ fÃ¥ meldekort tilsendt og stÃ¥ som arbeidssÃ¸ker?';
   COMMENT ON COLUMN "MELDEKORT"."FEIL_PAA_KORT" IS 'Status J/N indikerer om det er funnet feil pÃ¥ meldekortet';
   COMMENT ON COLUMN "MELDEKORT"."VEILEDNING" IS 'Ikke i bruk. Historisk fra omlegging av meldekortlÃ¸sning i 2005. Brukes ikke for visning av gamle meldekort';
   COMMENT ON COLUMN "MELDEKORT"."KOMMENTAR" IS 'Saksbehandlers kommentar';
   COMMENT ON COLUMN "MELDEKORT"."MELDEGRUPPEKODE" IS 'Referanse til MELDEGRUPPETYPE';
   COMMENT ON COLUMN "MELDEKORT"."RETURBREVKODE" IS 'Returkode dersom meldekort returneres til bruker. MKS_BREVTYPE inneholder returkoder. Kan inneholde blank dersom ingen retur';
   COMMENT ON COLUMN "MELDEKORT"."AB_POSTKODE" IS 'Sende kortet som A, B eller C-post.';
   COMMENT ON COLUMN "MELDEKORT"."MELDEKORT_ID_RELATERT" IS 'Relasjon til opprinnelig meldekort';
   COMMENT ON COLUMN "MELDEKORT"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON TABLE "MELDEKORT"  IS 'Meldekort for dagpenger og AAP.  Tabellen inneholder meldekort for personer som gÃ¥r pÃ¥ Dagpenger og AAP. Meldekortet gjelder for en 2-ukers periode. Innholdet pr dag finnes i tabellen MELDEKORTDAG.Meldekort kan registrers i Arena av saksbehandler, men de fleste leveres elektronisk.';
--------------------------------------------------------
--  DDL for Table MELDEKORTDAG
--------------------------------------------------------

  CREATE TABLE "MELDEKORTDAG"
   (	"MELDEKORT_ID" NUMBER,
	"UKENR" NUMBER(2,0),
	"DAGNR" NUMBER(1,0),
	"STATUS_ARBEIDSDAG" VARCHAR2(1),
	"STATUS_FERIE" VARCHAR2(1),
	"STATUS_KURS" VARCHAR2(1),
	"STATUS_SYK" VARCHAR2(1),
	"STATUS_ANNETFRAVAER_ATTF" VARCHAR2(1),
	"TIMER_ARBEIDET" NUMBER(3,1) DEFAULT 0,
	"TIMER_ARB_MENS_PERM" NUMBER(3,1) DEFAULT 0,
	"REG_USER" VARCHAR2(8),
	"REG_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"STATUS_ANNETFRAVAER" VARCHAR2(1),
	"MELDEGRUPPEKODE" VARCHAR2(5),
	"PARTISJON" NUMBER(8,0)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "MELDEKORTDAG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT';
   COMMENT ON COLUMN "MELDEKORTDAG"."UKENR" IS 'Ukenr mellom 01 og 53';
   COMMENT ON COLUMN "MELDEKORTDAG"."DAGNR" IS 'Dagnr mellom 1 og 7';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ARBEIDSDAG" IS 'Satt hvis TIMER_ARBEIDET > 0';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_FERIE" IS 'Svar pÃ¥ om bruker har hatt ferie';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_KURS" IS 'Svar pÃ¥ om bruker har vÃ¦rt i utdanning/tiltak';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_SYK" IS 'Svar pÃ¥ om bruker har vÃ¦rt syk';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ANNETFRAVAER_ATTF" IS 'Ikke i bruk. Brukes for visning gamle meldekort fra fÃ¸r omlegging av meldekortlÃ¸sning i 2005';
   COMMENT ON COLUMN "MELDEKORTDAG"."TIMER_ARBEIDET" IS 'Totalt antall timer arbeidet pÃ¥ dagen (egne og annen arbeidsgiver).';
   COMMENT ON COLUMN "MELDEKORTDAG"."TIMER_ARB_MENS_PERM" IS 'Ikke i bruk. Antall timer en permittert person har arbeidet hos annen arbeidsgiver.';
   COMMENT ON COLUMN "MELDEKORTDAG"."STATUS_ANNETFRAVAER" IS 'Svar pÃ¥ om bruker av andre grunner ikke vÃ¦rt arbeidssÃ¸ker';
   COMMENT ON COLUMN "MELDEKORTDAG"."MELDEGRUPPEKODE" IS 'Referanse til MELDEGRUPPETYPE';
   COMMENT ON COLUMN "MELDEKORTDAG"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON TABLE "MELDEKORTDAG"  IS 'Opplysninger for et meldekort pr dag i en uke i en meldekortperiode.Det vil ligge opplysninger om hvor mange timer/dager som bruker har arbeidet, vÃ¦rt sy, har annet fravÃ¦r osv.';
--------------------------------------------------------
--  DDL for Table MELDEKORTPERIODE
--------------------------------------------------------

  CREATE TABLE "MELDEKORTPERIODE"
   (	"AAR" NUMBER(4,0),
	"PERIODEKODE" VARCHAR2(2),
	"UKENR_UKE1" NUMBER(2,0),
	"UKENR_UKE2" NUMBER(2,0),
	"DATO_FRA" DATE,
	"DATO_TIL" DATE,
	 CONSTRAINT "MKORTPER_PK" PRIMARY KEY ("AAR", "PERIODEKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "MELDEKORTPERIODE"."AAR" IS 'Ã…r';
   COMMENT ON COLUMN "MELDEKORTPERIODE"."PERIODEKODE" IS 'Periodenummer med ledene null (01-53)';
   COMMENT ON COLUMN "MELDEKORTPERIODE"."UKENR_UKE1" IS 'Ukenr for uke 1 i perioden';
   COMMENT ON COLUMN "MELDEKORTPERIODE"."UKENR_UKE2" IS 'Ukenr for uke 2 i perioden';
   COMMENT ON COLUMN "MELDEKORTPERIODE"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
   COMMENT ON COLUMN "MELDEKORTPERIODE"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
   COMMENT ON TABLE "MELDEKORTPERIODE"  IS 'Definerer periode pÃ¥ to uker som meldekort gjelder for. BÃ¥de hvilke uker og hvilke datoer som inngÃ¥r i en periode. Ytterligere egenskaper til perioden finnes i MELDEKORTPERIODEBRUK.';
--------------------------------------------------------
--  DDL for Table MELDELOGG
--------------------------------------------------------

  CREATE TABLE "MELDELOGG"
   (	"MELDEKORT_ID" NUMBER,
	"HENDELSEDATO" DATE,
	"HENDELSETYPEKODE" VARCHAR2(7),
	"LOGGTEKST" VARCHAR2(255),
	"KORRELASJONS_ID" VARCHAR2(30),
	"REG_USER" VARCHAR2(8),
	"PARTISJON" NUMBER(8,0)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "MELDELOGG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT';
   COMMENT ON COLUMN "MELDELOGG"."HENDELSEDATO" IS 'Dato for hendelse';
   COMMENT ON COLUMN "MELDELOGG"."HENDELSETYPEKODE" IS 'Referanse til hendelsetype (Opprettet, Sendt osv)';
   COMMENT ON COLUMN "MELDELOGG"."LOGGTEKST" IS 'Fritekst beskrivelse av hendelsen';
   COMMENT ON COLUMN "MELDELOGG"."KORRELASJONS_ID" IS 'Flere meldinger for samme person sendes som en SOAP-melding med samme korrelasjons_id. Referanse til AOL_SOAPKALL_LOGG.';
   COMMENT ON COLUMN "MELDELOGG"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON TABLE "MELDELOGG"  IS 'Inneholder historikk for hendelser pÃ¥ meldekort';
--------------------------------------------------------
--  DDL for Table MKSKORTTYPE
--------------------------------------------------------

  CREATE TABLE "MKSKORTTYPE"
   (	"MKSKORTKODE" VARCHAR2(2),
	"MKSKORTTYPENAVN" VARCHAR2(30),
	 CONSTRAINT "MKSKORTTYP_PK" PRIMARY KEY ("MKSKORTKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "MKSKORTTYPE"."MKSKORTKODE" IS 'Entydlig kode som identifiserer en mkskorttype';
   COMMENT ON COLUMN "MKSKORTTYPE"."MKSKORTTYPENAVN" IS 'Navn pÃ¥ meldekorttype';
   COMMENT ON TABLE "MKSKORTTYPE"  IS 'Lovlige meldekorttyper (elektronisk, papir, manuelt osv)';
--------------------------------------------------------
--  DDL for Table PERSON
--------------------------------------------------------

  CREATE TABLE "PERSON"
   (	"PERSON_ID" NUMBER,
	"FODSELSDATO" DATE,
	"STATUS_DNR" VARCHAR2(1) DEFAULT 'N',
	"PERSONNR" NUMBER(5,0),
	"FODSELSNR" VARCHAR2(11),
	"ETTERNAVN" VARCHAR2(30),
	"FORNAVN" VARCHAR2(30),
	"DATO_FRA" DATE DEFAULT sysdate,
	"STATUS_SAMTYKKE" VARCHAR2(1) DEFAULT 'N',
	"DATO_SAMTYKKE" DATE,
	"VERNEPLIKTKODE" VARCHAR2(5) DEFAULT NULL,
	"MAALFORM" VARCHAR2(2) DEFAULT 'NO',
	"LANDKODE_STATSBORGER" VARCHAR2(2),
	"KONTONUMMER" VARCHAR2(11),
	"STATUS_BILDISP" VARCHAR2(1),
	"FORMIDLINGSGRUPPEKODE" VARCHAR2(5) DEFAULT 'ISERV',
	"VIKARGRUPPEKODE" VARCHAR2(5) DEFAULT 'IVIK',
	"KVALIFISERINGSGRUPPEKODE" VARCHAR2(5) DEFAULT 'IVURD',
	"RETTIGHETSGRUPPEKODE" VARCHAR2(5) DEFAULT 'IYT',
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"AETATORGENHET" VARCHAR2(8),
	"LONNSLIPP_EPOST" VARCHAR2(1),
	"DATO_OVERFORT_AMELDING" DATE,
	"DATO_SIST_INAKTIV" DATE,
	"BEGRUNNELSE_FORMIDLINGSGRUPPE" VARCHAR2(2000),
	"HOVEDMAALKODE" VARCHAR2(10),
	"BRUKERID_NAV_KONTAKT" VARCHAR2(8),
	"FR_KODE" VARCHAR2(2),
	"ER_DOED" VARCHAR2(1),
	"PERSON_ID_STATUS" VARCHAR2(20) DEFAULT 'AKTIV',
	"SPERRET_KOMMENTAR" VARCHAR2(500),
	"SPERRET_TIL" DATE,
	"SPERRET_DATO" DATE,
	"SPERRET_AV" VARCHAR2(8)
   ) ;

   COMMENT ON COLUMN "PERSON"."PERSON_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "PERSON"."FODSELSDATO" IS 'FÃ¸dselsdato';
   COMMENT ON COLUMN "PERSON"."STATUS_DNR" IS 'Settes J hvis FODSELSNR er et DNR';
   COMMENT ON COLUMN "PERSON"."PERSONNR" IS 'Norsk personnummer eller D-nummer';
   COMMENT ON COLUMN "PERSON"."FODSELSNR" IS 'Norsk fÃ¸dselsnummer';
   COMMENT ON COLUMN "PERSON"."ETTERNAVN" IS 'Etternavn';
   COMMENT ON COLUMN "PERSON"."FORNAVN" IS 'Fornavn evt med mellomnavn';
   COMMENT ON COLUMN "PERSON"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
   COMMENT ON COLUMN "PERSON"."STATUS_SAMTYKKE" IS 'Status samtykke';
   COMMENT ON COLUMN "PERSON"."DATO_SAMTYKKE" IS 'Dato for nÃ¥r siste samtykke er gitt';
   COMMENT ON COLUMN "PERSON"."VERNEPLIKTKODE" IS 'Kode for gjennomfÃ¸rt verneplikt';
   COMMENT ON COLUMN "PERSON"."MAALFORM" IS 'Referanse til EDB_LANGUAGE';
   COMMENT ON COLUMN "PERSON"."LANDKODE_STATSBORGER" IS 'Referanse til LAND';
   COMMENT ON COLUMN "PERSON"."KONTONUMMER" IS 'Ikke i bruk. Flyttet til kommbruk type: NOKTO Kontonummer for utbetalinger fra Aetat';
   COMMENT ON COLUMN "PERSON"."STATUS_BILDISP" IS 'Disponerer bil';
   COMMENT ON COLUMN "PERSON"."FORMIDLINGSGRUPPEKODE" IS 'Referanse til FORMIDLINGSGRUPPETYPE personen tilhÃ¸rer';
   COMMENT ON COLUMN "PERSON"."VIKARGRUPPEKODE" IS 'Ikke i bruk. Referanse til VIKARGRUPPETYPE';
   COMMENT ON COLUMN "PERSON"."KVALIFISERINGSGRUPPEKODE" IS 'Referanse til KVALIFISERINGSGRUPPETYPE. Kalles nÃ¥ Servicegruppe';
   COMMENT ON COLUMN "PERSON"."RETTIGHETSGRUPPEKODE" IS 'Referanse til RETTIGHETSGRUPPETYPE';
   COMMENT ON COLUMN "PERSON"."AETATORGENHET" IS 'Referanse til ORGUNITINSTANCE';
   COMMENT ON COLUMN "PERSON"."LONNSLIPP_EPOST" IS 'Angir om en person skal motta lÃ¸nnslipp via epost';
   COMMENT ON COLUMN "PERSON"."DATO_OVERFORT_AMELDING" IS 'Dato for fÃ¸rste overfÃ¸ring til Amelding';
   COMMENT ON COLUMN "PERSON"."DATO_SIST_INAKTIV" IS 'Dato sist inaktiv';
   COMMENT ON COLUMN "PERSON"."BEGRUNNELSE_FORMIDLINGSGRUPPE" IS 'Evt saksbehandlers begrunnelse ved setting av formidlingsgruppe';
   COMMENT ON COLUMN "PERSON"."HOVEDMAALKODE" IS 'Rereranse til HOVEDMAAL';
   COMMENT ON COLUMN "PERSON"."BRUKERID_NAV_KONTAKT" IS 'Referanse til ORGUNITINSTANCE. Kontaktperson hos NAV';
   COMMENT ON COLUMN "PERSON"."FR_KODE" IS 'Koding av fortrolige adresser fra folkeregisteret';
   COMMENT ON COLUMN "PERSON"."ER_DOED" IS 'Er dÃ¸d';
   COMMENT ON COLUMN "PERSON"."PERSON_ID_STATUS" IS 'Status for denne personforekomsten/person_id, ikke generelt for person';
   COMMENT ON COLUMN "PERSON"."SPERRET_KOMMENTAR" IS 'Evt. kommentar om sperringen';
   COMMENT ON COLUMN "PERSON"."SPERRET_TIL" IS 'Evt. slutt dato for sperringen';
   COMMENT ON COLUMN "PERSON"."SPERRET_DATO" IS 'Dato sperren ble satt';
   COMMENT ON COLUMN "PERSON"."SPERRET_AV" IS 'Saksbeh. ident for den som etablerte sperren.';
   COMMENT ON TABLE "PERSON"  IS 'Tabellen omfatter alle personer som NAV har et forhold til';
--------------------------------------------------------
--  DDL for Table POSTERING
--------------------------------------------------------

  CREATE TABLE "POSTERING"
   (	"POSTERING_ID" NUMBER,
	"BELOP" NUMBER(12,2),
	"BELOPKODE" VARCHAR2(5),
	"DATO_PERIODE_FRA" DATE,
	"DATO_PERIODE_TIL" DATE,
	"DATO_POSTERT" DATE,
	"EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
	"AAR" NUMBER(4,0),
	"PERSON_ID" NUMBER,
	"POSTERINGSATS" NUMBER(8,2),
	"POSTERINGTYPEKODE" VARCHAR2(5),
	"TRANSAKSJONSKODE" VARCHAR2(5),
	"ANTALL" NUMBER(14,4),
	"MELDINGKODE" VARCHAR2(10),
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"DATO_GRUNNLAG" DATE,
	"VEDTAK_ID" NUMBER,
	"ARTKODE" VARCHAR2(5),
	"PROSJEKTNUMMER" VARCHAR2(4),
	"KAPITTEL" VARCHAR2(4),
	"POST" VARCHAR2(2),
	"UNDERPOST" VARCHAR2(3),
	"KONTOSTEDKODE" VARCHAR2(5),
	"MELDEKORT_ID" NUMBER,
	"TRANSAKSJONSTEKST" VARCHAR2(60),
	"BRUKER_ID_SAKSBEHANDLER" VARCHAR2(8),
	"AETATENHET_ANSVARLIG" VARCHAR2(8),
	"TABELLNAVNALIAS_KILDE" VARCHAR2(10),
	"OBJEKT_ID_KILDE" NUMBER(20,0),
	"PARTISJON" NUMBER(8,0)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "POSTERING"."POSTERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "POSTERING"."BELOP" IS 'BelÃ¸p';
   COMMENT ON COLUMN "POSTERING"."BELOPKODE" IS 'Referanse til BELOPTYPE';
   COMMENT ON COLUMN "POSTERING"."DATO_PERIODE_FRA" IS 'Dato periode fra';
   COMMENT ON COLUMN "POSTERING"."DATO_PERIODE_TIL" IS 'Dato periode til';
   COMMENT ON COLUMN "POSTERING"."DATO_POSTERT" IS 'Dato postert';
   COMMENT ON COLUMN "POSTERING"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til BETALINGMOTTAKER';
   COMMENT ON COLUMN "POSTERING"."AAR" IS 'Ã…r';
   COMMENT ON COLUMN "POSTERING"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "POSTERING"."POSTERINGSATS" IS 'Posteringsats';
   COMMENT ON COLUMN "POSTERING"."POSTERINGTYPEKODE" IS 'Kode som entydig identifiserer posteringstype';
   COMMENT ON COLUMN "POSTERING"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
   COMMENT ON COLUMN "POSTERING"."ANTALL" IS 'Antall dager';
   COMMENT ON COLUMN "POSTERING"."MELDINGKODE" IS 'Referanse til MELDINGTYPE';
   COMMENT ON COLUMN "POSTERING"."REG_DATO" IS 'Dato opprettet';
   COMMENT ON COLUMN "POSTERING"."REG_USER" IS 'Oracle brukerident som opprettet posten';
   COMMENT ON COLUMN "POSTERING"."MOD_DATO" IS 'Dato sist modifisert';
   COMMENT ON COLUMN "POSTERING"."MOD_USER" IS 'Oracle brukerident som sist modifiserte posten';
   COMMENT ON COLUMN "POSTERING"."DATO_GRUNNLAG" IS 'Dato grunnlag';
   COMMENT ON COLUMN "POSTERING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "POSTERING"."ARTKODE" IS 'Referanse til ART. Konteringsart';
   COMMENT ON COLUMN "POSTERING"."PROSJEKTNUMMER" IS 'Ikke i bruk';
   COMMENT ON COLUMN "POSTERING"."KAPITTEL" IS 'Kapittel i statsregnskapet';
   COMMENT ON COLUMN "POSTERING"."POST" IS 'Angir post i statsregnskapet';
   COMMENT ON COLUMN "POSTERING"."UNDERPOST" IS 'Angir underpost i statsregnskapet';
   COMMENT ON COLUMN "POSTERING"."KONTOSTEDKODE" IS 'Referanse til KONTOSTED. Kontosted for en kontering (kontostreng + sted+aar)';
   COMMENT ON COLUMN "POSTERING"."MELDEKORT_ID" IS 'Referanse til MELDEKORT. Inkludert av hensyn til utbetalings/posteringshistorikk med referanse til anmerkninger som refererer til meldekort.';
   COMMENT ON COLUMN "POSTERING"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
   COMMENT ON COLUMN "POSTERING"."BRUKER_ID_SAKSBEHANDLER" IS 'ID til ORACLE-brukerident som har lagt inn postering';
   COMMENT ON COLUMN "POSTERING"."AETATENHET_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE';
   COMMENT ON COLUMN "POSTERING"."TABELLNAVNALIAS_KILDE" IS 'Peker pÃ¥ tabellen som er opphav til utbetalingen';
   COMMENT ON COLUMN "POSTERING"."OBJEKT_ID_KILDE" IS 'Peker pÃ¥ en forekomst i tabellen angitt i Tabellnavnalias_kilde  som er opphav til utbetalingen';
   COMMENT ON COLUMN "POSTERING"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON TABLE "POSTERING"  IS 'Alle posteringer som er sendt til forsystemet';
--------------------------------------------------------
--  DDL for Table RETTIGHETTYPE
--------------------------------------------------------

  CREATE TABLE "RETTIGHETTYPE"
   (	"RETTIGHETKODE" VARCHAR2(10),
	"RETTIGHETNAVN" VARCHAR2(60),
	"DATO_GYLDIG_FRA" DATE,
	"DATO_GYLDIG_TIL" DATE,
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"SAKSKODE" VARCHAR2(10),
	"RETTIGHETSKLASSEKODE" VARCHAR2(5),
	"BELOPKODE" VARCHAR2(5),
	"RANGNR" NUMBER(3,0),
	"TRANSAKSJONSKODE" VARCHAR2(5),
	"STATUS_KONTERBAR" VARCHAR2(1) DEFAULT 'N',
	"TRANSAKSJONSKODE_FORSKUDD" VARCHAR2(5),
	"RETTIGHETNAVN_KORT" VARCHAR2(20),
	"FORSKUDD_BETPLAN" VARCHAR2(1),
	"SATSVALG" VARCHAR2(10),
	"STATUS_TILTAK" VARCHAR2(1),
	"STATUS_START_VEDTAK" VARCHAR2(1),
	"BILAG_KREVES_JN" VARCHAR2(1),
	"BETPLAN_JN" VARCHAR2(1) DEFAULT 'N',
	"GJELDERKODE" VARCHAR2(10),
	 CONSTRAINT "RETTYP_PK" PRIMARY KEY ("RETTIGHETKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETKODE" IS 'Kode som entydig identifiserer en typeverdi';
   COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETNAVN" IS 'Rettighetnavn';
   COMMENT ON COLUMN "RETTIGHETTYPE"."DATO_GYLDIG_FRA" IS 'Dato gyldig fra';
   COMMENT ON COLUMN "RETTIGHETTYPE"."DATO_GYLDIG_TIL" IS 'Dato gyldig til';
   COMMENT ON COLUMN "RETTIGHETTYPE"."SAKSKODE" IS 'Referanse til SAK';
   COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETSKLASSEKODE" IS 'Referanse til RETTIGHETSKLASSE';
   COMMENT ON COLUMN "RETTIGHETTYPE"."BELOPKODE" IS 'Referanse til BELOPTYPE';
   COMMENT ON COLUMN "RETTIGHETTYPE"."RANGNR" IS 'Rangering av rettighettyper. Et manuelt satt nr som viser  etter hvilken rang vedtak skal presenteres i samme brev.';
   COMMENT ON COLUMN "RETTIGHETTYPE"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
   COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_KONTERBAR" IS 'Status konterbar';
   COMMENT ON COLUMN "RETTIGHETTYPE"."TRANSAKSJONSKODE_FORSKUDD" IS 'Transaksjonskode forskudd. Gir transaksjonskode hvis utbetalingen er et forskudd';
   COMMENT ON COLUMN "RETTIGHETTYPE"."RETTIGHETNAVN_KORT" IS 'Kort navn pÃ¥ rettighettypen';
   COMMENT ON COLUMN "RETTIGHETTYPE"."FORSKUDD_BETPLAN" IS 'Flagg som forteller hvorvidt forskuddsutbetaling tillates i.f.m. betalingsplan';
   COMMENT ON COLUMN "RETTIGHETTYPE"."SATSVALG" IS 'Satsvalg';
   COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_TILTAK" IS 'MÃ¥ vÃ¦re pÃ¥ tiltak for Ã¥ bruke rettighettype';
   COMMENT ON COLUMN "RETTIGHETTYPE"."STATUS_START_VEDTAK" IS 'Rettighet som kan opprettes ved start vedtaksbehandling';
   COMMENT ON COLUMN "RETTIGHETTYPE"."BILAG_KREVES_JN" IS 'Bilag Kreves Jn';
   COMMENT ON COLUMN "RETTIGHETTYPE"."BETPLAN_JN" IS 'Betplan Jn';
   COMMENT ON COLUMN "RETTIGHETTYPE"."GJELDERKODE" IS 'Gjelder person, arbeidsgiver, behandler';
   COMMENT ON TABLE "RETTIGHETTYPE"  IS 'Hvilke rettighettyper som finnes i Arena.  BelÃ¸pkode finnes her eller i BelÃ¸pttypevariant. Rettighettyper uten kontering har nei i status_konterbar.';
--------------------------------------------------------
--  DDL for Table SAK
--------------------------------------------------------

  CREATE TABLE "SAK"
   (	"SAK_ID" NUMBER,
	"SAKSKODE" VARCHAR2(10) DEFAULT 'INAKT',
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"TABELLNAVNALIAS" VARCHAR2(10),
	"OBJEKT_ID" NUMBER,
	"AAR" NUMBER(4,0) DEFAULT to_number(to_char(sysdate,'YYYY')),
	"LOPENRSAK" NUMBER(7,0),
	"DATO_AVSLUTTET" DATE,
	"SAKSTATUSKODE" VARCHAR2(5),
	"ARKIVNOKKEL" VARCHAR2(7),
	"AETATENHET_ARKIV" VARCHAR2(8),
	"ARKIVHENVISNING" VARCHAR2(255),
	"BRUKERID_ANSVARLIG" VARCHAR2(8),
	"AETATENHET_ANSVARLIG" VARCHAR2(8),
	"OBJEKT_KODE" VARCHAR2(10),
	"STATUS_ENDRET" DATE,
	"PARTISJON" NUMBER(8,0),
	"ER_UTLAND" VARCHAR2(1) DEFAULT 'N'
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "SAK"."SAK_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "SAK"."SAKSKODE" IS 'Referanse til SAKSTYPE';
   COMMENT ON COLUMN "SAK"."TABELLNAVNALIAS" IS 'HovedaktÃ¸ren i saken Person eller arbeidsgiver.Kortnavn for tabellen som eier posten. Gir sammen med objekt_id eller objekt_kode eierobjektet. FK til Objekttype (UK).';
   COMMENT ON COLUMN "SAK"."OBJEKT_ID" IS 'Objekt_id angir sammen med tabellnavn eller tabellnavnalias eierobjektet';
   COMMENT ON COLUMN "SAK"."AAR" IS 'Ã…r inngÃ¥r i saksnummer';
   COMMENT ON COLUMN "SAK"."LOPENRSAK" IS 'LÃ¸penr for sak innen et Ã¥r';
   COMMENT ON COLUMN "SAK"."DATO_AVSLUTTET" IS 'Dato avsluttet';
   COMMENT ON COLUMN "SAK"."SAKSTATUSKODE" IS 'Referanse til SAKSTATUS';
   COMMENT ON COLUMN "SAK"."ARKIVNOKKEL" IS 'Angir et oppgitt arkivnummer';
   COMMENT ON COLUMN "SAK"."AETATENHET_ARKIV" IS 'Referanse til ORGUNITINSTANCE';
   COMMENT ON COLUMN "SAK"."ARKIVHENVISNING" IS 'Arkivhenvisning';
   COMMENT ON COLUMN "SAK"."BRUKERID_ANSVARLIG" IS 'Generelt ansvarlig saksbehandler. For tiltak: Signaturen til den saksbehandler som er ansvarlig for Ã¥ planlegge,';
   COMMENT ON COLUMN "SAK"."AETATENHET_ANSVARLIG" IS 'Generelt ansvarlig Aetat-enhet';
   COMMENT ON COLUMN "SAK"."OBJEKT_KODE" IS 'Objekt_kode angir sammen med tabellnavn eller tabellnavnalias eierobjektet';
   COMMENT ON COLUMN "SAK"."STATUS_ENDRET" IS 'Dato for siste endring av SAKSTATUSKODE';
   COMMENT ON COLUMN "SAK"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "SAK"."ER_UTLAND" IS 'For Ã¥ kunne merke oppgaver som UTLAND i ARENA';
   COMMENT ON TABLE "SAK"  IS 'Alle saker i Arena';
--------------------------------------------------------
--  DDL for Table SIM_UTBETALINGSGRUNNLAG
--------------------------------------------------------

  CREATE TABLE "SIM_UTBETALINGSGRUNNLAG"
   (	"SIM_POSTERING_ID" NUMBER(20,0),
	"BELOP" NUMBER(12,2),
	"BELOPKODE" VARCHAR2(5),
	"EKSTERNENHET_ID_ALTMOTTAKER" NUMBER(20,0),
	"AAR" NUMBER(4,0),
	"DATO_PERIODE_FRA" DATE,
	"PERSON_ID" NUMBER(20,0),
	"POSTERINGTYPEKODE" VARCHAR2(5),
	"TRANSAKSJONSKODE" VARCHAR2(5),
	"ANTALL" NUMBER(14,4),
	"POSTERINGSATS" NUMBER(8,2),
	"MELDINGKODE" VARCHAR2(10),
	"ARTKODE" VARCHAR2(5),
	"DATO_PERIODE_TIL" DATE,
	"REG_DATO" DATE,
	"KAPITTEL" VARCHAR2(4),
	"MOD_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_USER" VARCHAR2(8),
	"VEDTAK_ID" NUMBER(20,0),
	"DATO_GRUNNLAG" DATE,
	"POST" VARCHAR2(2),
	"PROSJEKTNUMMER" VARCHAR2(4),
	"SIM_MELDEKORT_ID" NUMBER(20,0),
	"TRANSAKSJONSTEKST" VARCHAR2(60),
	"UNDERPOST" VARCHAR2(3),
	"KONTOSTEDKODE" VARCHAR2(5),
	"STATUS_MANUELL" VARCHAR2(1),
	"KOMMENTAR" VARCHAR2(2000),
	"VEDTAK_ID_FEILUTBET" NUMBER,
	"TABELLNAVNALIAS_KILDE" VARCHAR2(10),
	"OBJEKT_ID_KILDE" NUMBER(20,0),
	"PARTISJON" NUMBER(8,0),
	"BELOP_SATT_MANUELT" VARCHAR2(1),
	"POSTERING_ID" NUMBER
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."SIM_POSTERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."BELOP" IS 'BelÃ¸p';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."BELOPKODE" IS 'Referanse til BELOPTYPE';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til EKSTERNENHET';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."AAR" IS 'Ã…r';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."DATO_PERIODE_FRA" IS 'Dato periode fra';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."ANTALL" IS 'Antall';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."POSTERINGSATS" IS 'Posteringsats';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."MELDINGKODE" IS 'Referanse til MELDINGTYPE';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."ARTKODE" IS 'Referanse til ART';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."DATO_PERIODE_TIL" IS 'Dato periode til';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."KAPITTEL" IS 'Kapittel i statsregnskapet';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."DATO_GRUNNLAG" IS 'Dato grunnlag';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."POST" IS 'Angir post i statsregnskapet';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."PROSJEKTNUMMER" IS 'Til ev. senere bruk i regnskapssammenheng';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."SIM_MELDEKORT_ID" IS 'Referanse til SIM_MELDEKORT';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."UNDERPOST" IS 'Angir underpost i statsregnskapet';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."KONTOSTEDKODE" IS 'Referanse til KONTOSTED';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."STATUS_MANUELL" IS 'Status manuell. Gjelder feilutbetalingsberegningen  spesielt';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."KOMMENTAR" IS 'Kommentar er knyttet til feilutbetalingssituasjonen';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."VEDTAK_ID_FEILUTBET" IS 'Refererer til feilutbetalingsvedtak';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."TABELLNAVNALIAS_KILDE" IS 'Tabellnavnalias for kilde til utbetalingen';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."OBJEKT_ID_KILDE" IS 'Peker pÃ¥ en forekomst i tabellen angitt i Tabellnavnalias_kilde  som er opphav til utbetalingen';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."BELOP_SATT_MANUELT" IS 'Felt for Ã¥ angi om belÃ¸pet er satt manuelt. Lovlige verdier er J eller N';
   COMMENT ON COLUMN "SIM_UTBETALINGSGRUNNLAG"."POSTERING_ID" IS 'Postering_id fra postering for Ã¥ kunne koble rad mot riktig postering';
   COMMENT ON TABLE "SIM_UTBETALINGSGRUNNLAG"  IS 'Simulerte utbetalingsgrunnlag for simulerte meldekort. Brukes ved beregning av feilutbetalingsvedtak';
--------------------------------------------------------
--  DDL for Table SPESIALUTBETALING
--------------------------------------------------------

  CREATE TABLE "SPESIALUTBETALING"
   (	"SPESUTBETALING_ID" NUMBER,
	"PERSON_ID" NUMBER,
	"VEDTAK_ID" NUMBER,
	"LOPENR" NUMBER(3,0),
	"BRUKER_ID_SAKSBEHANDLER" VARCHAR2(8),
	"BRUKER_ID_BESLUTTER" VARCHAR2(8),
	"DATO_UTBETALING" DATE,
	"BEGRUNNELSE" VARCHAR2(2000),
	"BELOP" NUMBER(12,2),
	"BELOPKODE" VARCHAR2(5),
	"RETTIGHETKODE" VARCHAR2(10),
	"AKTFASEKODE" VARCHAR2(10),
	"VEDTAKSTATUSKODE" VARCHAR2(5),
	"POSTERINGTYPEKODE" VARCHAR2(5),
	"REFERANSE_TOTAL" VARCHAR2(255),
	"DATO_FRA" DATE,
	"DATO_TIL" DATE,
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
	"FERIEGRUNNLAG" NUMBER(12,2),
	"FERIEGRUNNLAGKODE" VARCHAR2(10),
	"ORDINAER_YTELSE" VARCHAR2(1),
	"REFERANSE_BILAG" VARCHAR2(25),
	"STATUS_BILAG" VARCHAR2(1),
	"STATUS_ANVIS_BILAG" VARCHAR2(1),
	"PARTISJON" NUMBER(8,0),
	"VALGT_UTBET_TYPE" VARCHAR2(20),
	"KATEGORI" VARCHAR2(50)
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "SPESIALUTBETALING"."SPESUTBETALING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "SPESIALUTBETALING"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "SPESIALUTBETALING"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "SPESIALUTBETALING"."LOPENR" IS 'Et generert nummer for Ã¥ angi rekkefÃ¸lge pÃ¥ spesialutbealingen., skal vÃ¦re unik innenfor et vedtak';
   COMMENT ON COLUMN "SPESIALUTBETALING"."BRUKER_ID_SAKSBEHANDLER" IS 'Referanse til ORGUNITINSTANCE';
   COMMENT ON COLUMN "SPESIALUTBETALING"."BRUKER_ID_BESLUTTER" IS 'Referanse til ORGUNITINSTANCE';
   COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_UTBETALING" IS 'Dato utbetaling';
   COMMENT ON COLUMN "SPESIALUTBETALING"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
   COMMENT ON COLUMN "SPESIALUTBETALING"."BELOP" IS 'BelÃ¸p';
   COMMENT ON COLUMN "SPESIALUTBETALING"."BELOPKODE" IS 'Referanse til KONTOTYPE';
   COMMENT ON COLUMN "SPESIALUTBETALING"."RETTIGHETKODE" IS 'Referanse til LOV_RETTIGHETTYPE_AKTFAS';
   COMMENT ON COLUMN "SPESIALUTBETALING"."AKTFASEKODE" IS 'Referanse til LOV_RETTIGHETTYPE_AKTFAS';
   COMMENT ON COLUMN "SPESIALUTBETALING"."VEDTAKSTATUSKODE" IS 'Referanse til VEDTAKSTATUS';
   COMMENT ON COLUMN "SPESIALUTBETALING"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
   COMMENT ON COLUMN "SPESIALUTBETALING"."REFERANSE_TOTAL" IS 'Tekstlig referanse til tidligere vedtak iTotal som ikke er konvertert.';
   COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_FRA" IS 'Fra-dato i gyldighetsperiode';
   COMMENT ON COLUMN "SPESIALUTBETALING"."DATO_TIL" IS 'Til-dato i gyldighetsperiode';
   COMMENT ON COLUMN "SPESIALUTBETALING"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til EKSTERNENHET';
   COMMENT ON COLUMN "SPESIALUTBETALING"."FERIEGRUNNLAG" IS 'Feriegrunnlag. BelÃ¸pet som rapporteres til forsystemet som grunnlag for utbetaling av feriepenger';
   COMMENT ON COLUMN "SPESIALUTBETALING"."FERIEGRUNNLAGKODE" IS 'Referanse til FERIEGRUNNLAGTYPE';
   COMMENT ON COLUMN "SPESIALUTBETALING"."ORDINAER_YTELSE" IS 'J hvis utbetalingen gjelder en ordinÃ¦r ytelse';
   COMMENT ON COLUMN "SPESIALUTBETALING"."REFERANSE_BILAG" IS 'Referanse til eventuelle bilag';
   COMMENT ON COLUMN "SPESIALUTBETALING"."STATUS_BILAG" IS 'Status bilag J/N';
   COMMENT ON COLUMN "SPESIALUTBETALING"."STATUS_ANVIS_BILAG" IS 'Status anviste bilag J/N';
   COMMENT ON COLUMN "SPESIALUTBETALING"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "SPESIALUTBETALING"."VALGT_UTBET_TYPE" IS 'Type ventebetingelse som velges fra ny saksopplysning og attributt UTBETVENT / UTBET ( utbetaling pÃ¥ vent ). Gyldige verdier: REFKRAVSOS	Refusjonskrav sosialhjelp, REFKRAVTP	Refusjonskrav tjenestepensjonsordning, AVREGNAYT	Avregning andre folketrygdytelser';
   COMMENT ON COLUMN "SPESIALUTBETALING"."KATEGORI" IS 'Angir kategori for spesialutbetaling. Kan benyttes for Ã¥ merke utbetaling som krever spesiell behandling. ';
   COMMENT ON TABLE "SPESIALUTBETALING"  IS 'Spesialutbetalinger for attfÃ¸ringspenger, ordinÃ¦re dagpenger, samt personer med rett til ferietillegg og reisetillegg';
--------------------------------------------------------
--  DDL for Table TRANSAKSJONTYPE
--------------------------------------------------------

  CREATE TABLE "TRANSAKSJONTYPE"
   (	"TRANSAKSJONSKODE" VARCHAR2(5),
	"TRANSAKSJONSTYPENAVN" VARCHAR2(30),
	"TRANSTYPENAVN" VARCHAR2(30),
	"TRANSGRUPPEKODE" VARCHAR2(5),
	"REG_USER" VARCHAR2(8),
	"REG_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	 CONSTRAINT "TRANSTYP_PK" PRIMARY KEY ("TRANSAKSJONSKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSAKSJONSKODE" IS 'Kode som entydig identifiserer en typeverdi';
   COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSAKSJONSTYPENAVN" IS 'Transaksjonstypenavn';
   COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSTYPENAVN" IS 'Transtypenavn';
   COMMENT ON COLUMN "TRANSAKSJONTYPE"."TRANSGRUPPEKODE" IS 'Referanse til TRANSAKSJONGRUPPE';
   COMMENT ON TABLE "TRANSAKSJONTYPE"  IS 'Lovlige transaksjonstyper';
--------------------------------------------------------
--  DDL for Table UTBETALINGSGRUNNLAG
--------------------------------------------------------

  CREATE TABLE "UTBETALINGSGRUNNLAG"
   (	"POSTERING_ID" NUMBER,
	"BELOP" NUMBER(12,2),
	"BELOPKODE" VARCHAR2(5),
	"EKSTERNENHET_ID_ALTMOTTAKER" NUMBER,
	"AAR" NUMBER(4,0),
	"DATO_PERIODE_FRA" DATE,
	"PERSON_ID" NUMBER,
	"POSTERINGTYPEKODE" VARCHAR2(5),
	"TRANSAKSJONSKODE" VARCHAR2(5),
	"ANTALL" NUMBER(14,4),
	"MELDINGKODE" VARCHAR2(10),
	"POSTERINGSATS" NUMBER(8,2),
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"VEDTAK_ID" NUMBER,
	"DATO_GRUNNLAG" DATE,
	"ARTKODE" VARCHAR2(5),
	"PROSJEKTNUMMER" VARCHAR2(4),
	"DATO_PERIODE_TIL" DATE,
	"KAPITTEL" VARCHAR2(4),
	"POST" VARCHAR2(2),
	"UNDERPOST" VARCHAR2(3),
	"KONTOSTEDKODE" VARCHAR2(5),
	"MELDEKORT_ID" NUMBER,
	"TRANSAKSJONSTEKST" VARCHAR2(60),
	"TABELLNAVNALIAS_KILDE" VARCHAR2(10),
	"OBJEKT_ID_KILDE" NUMBER(20,0),
	"PARTISJON" NUMBER(8,0)
   ) ;

   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERING_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."BELOP" IS 'BelÃ¸p';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."BELOPKODE" IS 'Referanse til BELOPTYPE';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."EKSTERNENHET_ID_ALTMOTTAKER" IS 'Referanse til BETALINGMOTTAKER, alternativ mottaker';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."AAR" IS 'Ã…r';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_PERIODE_FRA" IS 'Dato periode fra';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERINGTYPEKODE" IS 'Referanse til POSTERINGTYPE';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TRANSAKSJONSKODE" IS 'Referanse til TRANSAKSJONTYPE';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."ANTALL" IS 'Antall';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."MELDINGKODE" IS 'Referanse til MELDINGTYPE';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POSTERINGSATS" IS 'Posteringsats';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_GRUNNLAG" IS 'Dato grunnlag';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."ARTKODE" IS 'Referanse til TILTAKSPROFIL';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PROSJEKTNUMMER" IS 'Prosjektnummer';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."DATO_PERIODE_TIL" IS 'Dato periode til';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."KAPITTEL" IS 'Kapittel i statsregnskapet';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."POST" IS 'Angir post i statsregnskapet';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."UNDERPOST" IS 'Angir underpost i statsregnskapet';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."KONTOSTEDKODE" IS 'Referanse til KONTOSTED. Kontosted for en kontering (kontostreng + sted+aar)';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."MELDEKORT_ID" IS 'Referanse til MELDEKORT. inkludert av hensyn til utbetalings/posteringshistorikk med referanse til anmerkninger som refererer til meldekort.';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TRANSAKSJONSTEKST" IS 'Tekst som beskriver transaksjonen';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."TABELLNAVNALIAS_KILDE" IS 'Tabellnavnalias for kilde til utbetalingen';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."OBJEKT_ID_KILDE" IS 'Peker pÃ¥ en forekomst i tabellen angitt i Tabellnavnalias_kilde  som er opphav til utbetalingen';
   COMMENT ON COLUMN "UTBETALINGSGRUNNLAG"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON TABLE "UTBETALINGSGRUNNLAG"  IS 'Alle utbetalinger som tilrettelegges. GjÃ¸res om til posteringer, nÃ¥r de blir sendt.';
--------------------------------------------------------
--  DDL for Table VEDTAK
--------------------------------------------------------

  CREATE TABLE "VEDTAK"
   (	"VEDTAK_ID" NUMBER,
	"SAK_ID" NUMBER,
	"VEDTAKSTATUSKODE" VARCHAR2(5),
	"VEDTAKTYPEKODE" VARCHAR2(10),
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"UTFALLKODE" VARCHAR2(10),
	"BEGRUNNELSE" VARCHAR2(4000),
	"BRUKERID_ANSVARLIG" VARCHAR2(8),
	"AETATENHET_BEHANDLER" VARCHAR2(8),
	"AAR" NUMBER(4,0) DEFAULT 2000,
	"LOPENRSAK" NUMBER(7,0),
	"LOPENRVEDTAK" NUMBER(3,0),
	"RETTIGHETKODE" VARCHAR2(10),
	"AKTFASEKODE" VARCHAR2(10),
	"BREV_ID" NUMBER,
	"TOTALBELOP" NUMBER(8,2),
	"DATO_MOTTATT" DATE,
	"VEDTAK_ID_RELATERT" NUMBER,
	"AVSNITTLISTEKODE_VALGT" VARCHAR2(20),
	"HANDLINGSPLAN_ID" NUMBER INVISIBLE,
	"PERSON_ID" NUMBER,
	"BRUKERID_BESLUTTER" VARCHAR2(8),
	"STATUS_SENSITIV" VARCHAR2(1),
	"VEDLEGG_BETPLAN" VARCHAR2(1),
	"PARTISJON" NUMBER(8,0),
	"OPPSUMMERING_SB2" VARCHAR2(4000),
	"DATO_UTFORT_DEL1" DATE,
	"DATO_UTFORT_DEL2" DATE,
	"OVERFORT_NAVI" VARCHAR2(1),
	"FRA_DATO" DATE,
	"TIL_DATO" DATE,
	"SF_OPPFOLGING_ID" NUMBER,
	"STATUS_SOSIALDATA" VARCHAR2(1) DEFAULT 'N',
	"KONTOR_SOSIALDATA" VARCHAR2(8),
	"TEKSTVARIANTKODE" VARCHAR2(20),
	"VALGT_BESLUTTER" VARCHAR2(8),
	"TEKNISK_VEDTAK" VARCHAR2(1),
	"DATO_INNSTILT" DATE,
	"ER_UTLAND" VARCHAR2(1) DEFAULT ON NULL 'N'
   )  ENABLE ROW MOVEMENT ;

   COMMENT ON COLUMN "VEDTAK"."VEDTAK_ID" IS 'Generert Oracle-sekvens som entydig identifiserer posten';
   COMMENT ON COLUMN "VEDTAK"."SAK_ID" IS 'Referanse til SAK';
   COMMENT ON COLUMN "VEDTAK"."VEDTAKSTATUSKODE" IS 'Referanse til VEDTAKSTATUS';
   COMMENT ON COLUMN "VEDTAK"."VEDTAKTYPEKODE" IS 'Referanse til KRAVTYPE';
   COMMENT ON COLUMN "VEDTAK"."UTFALLKODE" IS 'Referanse til UTFALLTYPE';
   COMMENT ON COLUMN "VEDTAK"."BEGRUNNELSE" IS 'Saksbehandlers begrunnelse';
   COMMENT ON COLUMN "VEDTAK"."BRUKERID_ANSVARLIG" IS 'Referanse til ORGUNITINSTANCE. Generelt ansvarlig saksbehandler';
   COMMENT ON COLUMN "VEDTAK"."AETATENHET_BEHANDLER" IS 'Referanse til ORGUNITINSTANCE. Enhet somn behandler vedtak';
   COMMENT ON COLUMN "VEDTAK"."AAR" IS 'Referanse til SAK. Angir Ã¥r i saken';
   COMMENT ON COLUMN "VEDTAK"."LOPENRSAK" IS 'Referanse til SAK. Angir lÃ¸penummer i en sak';
   COMMENT ON COLUMN "VEDTAK"."LOPENRVEDTAK" IS 'LÃ¸penrvedtak';
   COMMENT ON COLUMN "VEDTAK"."RETTIGHETKODE" IS 'Referanse til RETTIGHETTYPE';
   COMMENT ON COLUMN "VEDTAK"."AKTFASEKODE" IS 'Referanse til AKTIVITETFASE';
   COMMENT ON COLUMN "VEDTAK"."BREV_ID" IS 'Referanse til BREV';
   COMMENT ON COLUMN "VEDTAK"."TOTALBELOP" IS 'TotalbelÃ¸p';
   COMMENT ON COLUMN "VEDTAK"."DATO_MOTTATT" IS 'Dato mottatt';
   COMMENT ON COLUMN "VEDTAK"."VEDTAK_ID_RELATERT" IS 'Referanse til VEDTAK. Peker til ''opprinnelig'' vedtak ved endring, gjenopptak etc';
   COMMENT ON COLUMN "VEDTAK"."AVSNITTLISTEKODE_VALGT" IS 'Referanse til AVSNITTLISTE, ikke i bruk';
   COMMENT ON COLUMN "VEDTAK"."HANDLINGSPLAN_ID" IS 'Referanse til utgÃƒÂ¥tt tabell HANDLINGSPLAN(PK-36568)';
   COMMENT ON COLUMN "VEDTAK"."PERSON_ID" IS 'Referanse til PERSON';
   COMMENT ON COLUMN "VEDTAK"."BRUKERID_BESLUTTER" IS 'Referanse til ORGUNITINSTANCE. Brukerid for besluttende saksbehandler';
   COMMENT ON COLUMN "VEDTAK"."STATUS_SENSITIV" IS 'Status sensitiv';
   COMMENT ON COLUMN "VEDTAK"."VEDLEGG_BETPLAN" IS 'Vedlegg Betplan';
   COMMENT ON COLUMN "VEDTAK"."PARTISJON" IS 'PartisjonsnÃ¸kkel';
   COMMENT ON COLUMN "VEDTAK"."OPPSUMMERING_SB2" IS 'Oppsummering for vedtaket del 2';
   COMMENT ON COLUMN "VEDTAK"."DATO_UTFORT_DEL1" IS 'Dato for fÃ¸rste del av vilkÃ¥rssvurdering for vedtak hvor vilkÃ¥rsvurdering er delt mellom lokal og forvaltning. Brukt i eksterne vedtak';
   COMMENT ON COLUMN "VEDTAK"."DATO_UTFORT_DEL2" IS 'Dato for andre del av vilkÃ¥rssvurdering for vedtak hvor vilkÃ¥rsvurdering er delt mellom lokal og forvaltning. Brukt i eksterne vedtak';
   COMMENT ON COLUMN "VEDTAK"."OVERFORT_NAVI" IS 'OverfÃ¸rt NAVI J/N, J dersom vedtaket er overfÃ¸rt NAVI for innkreving av feilutbetaling';
   COMMENT ON COLUMN "VEDTAK"."FRA_DATO" IS 'Denormalisert vedtaksfakta FDAT';
   COMMENT ON COLUMN "VEDTAK"."TIL_DATO" IS 'Denormalisert vedtaksfakta TDAT';
   COMMENT ON COLUMN "VEDTAK"."SF_OPPFOLGING_ID" IS 'Referanse til SF_OPPFOLGING';
   COMMENT ON COLUMN "VEDTAK"."STATUS_SOSIALDATA" IS 'Status sosialdata, kontorsperret informasjon pÃ¥ vedtaket.';
   COMMENT ON COLUMN "VEDTAK"."KONTOR_SOSIALDATA" IS 'Referanse til ORGUNITINSTANSE, hvilke kontor som har kontorsperret vedtaket.';
   COMMENT ON COLUMN "VEDTAK"."TEKSTVARIANTKODE" IS 'Tekstvariant saksbehandleren valgte for vedtaket.';
   COMMENT ON COLUMN "VEDTAK"."VALGT_BESLUTTER" IS 'Beslutter saksbehandler valgte for vedtaket.';
   COMMENT ON COLUMN "VEDTAK"."TEKNISK_VEDTAK" IS 'Kolonne for Ã¥ angi om vedtaker er teknisk, og ikke skal generere noe brev.';
   COMMENT ON COLUMN "VEDTAK"."DATO_INNSTILT" IS 'Inneholder den dato som vedtakstatuskode settes til INNST. Kolonne settes i trigger VEDTAK_BUR.';
   COMMENT ON COLUMN "VEDTAK"."ER_UTLAND" IS 'For Ã¥ kunne merke oppgaver som UTLAND i ARENA';
   COMMENT ON TABLE "VEDTAK"  IS 'Alle vedtak som forberedes, behandles eller er fattet.';
--------------------------------------------------------
--  DDL for Table VEDTAKFAKTA
--------------------------------------------------------

  CREATE TABLE "VEDTAKFAKTA"
   (	"VEDTAK_ID" NUMBER,
	"VEDTAKFAKTAKODE" VARCHAR2(10),
	"VEDTAKVERDI" VARCHAR2(2000),
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	"PERSON_ID" NUMBER,
	"PARTISJON" NUMBER(8,0)
   ) ;

   COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAK_ID" IS 'Referanse til VEDTAK';
   COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAKFAKTAKODE" IS 'Referanse til VEDTAKFAKTATYPE';
   COMMENT ON COLUMN "VEDTAKFAKTA"."VEDTAKVERDI" IS 'Vedtakverdi';
   COMMENT ON COLUMN "VEDTAKFAKTA"."REG_DATO" IS 'Angir tidspunktet for nÃ¥r raden ble opprettet';
   COMMENT ON COLUMN "VEDTAKFAKTA"."REG_USER" IS 'Angir hvilken bruker som opprettet raden';
   COMMENT ON COLUMN "VEDTAKFAKTA"."MOD_DATO" IS 'Angir tidspunktet for nÃ¥r raden sist ble endret';
   COMMENT ON COLUMN "VEDTAKFAKTA"."MOD_USER" IS 'Angir hvilken bruker som sist endret raden';
   COMMENT ON COLUMN "VEDTAKFAKTA"."PERSON_ID" IS 'Ikke i bruk lenger. Brukt i hl3 for konvertering';
   COMMENT ON COLUMN "VEDTAKFAKTA"."PARTISJON" IS 'PartisjonsnÃ¸kkel. Benyttes ikke lenger!';
   COMMENT ON TABLE "VEDTAKFAKTA"  IS 'Gjelder enkeltopplysninger knyttet til et vedtak. Kan sammenlignes med attributt for saksopplysning.';
--------------------------------------------------------
--  DDL for Table VEDTAKFAKTATYPE
--------------------------------------------------------

  CREATE TABLE "VEDTAKFAKTATYPE"
   (	"VEDTAKFAKTAKODE" VARCHAR2(10),
	"SKJERMBILDETEKST" VARCHAR2(255),
	"STATUS_KVOTEBRUK" VARCHAR2(1),
	"STATUS_OVERSIKT" VARCHAR2(1),
	"VEDTAKFAKTANAVN" VARCHAR2(30),
	"BESKRIVELSE" VARCHAR2(255),
	"ORACLETYPE" VARCHAR2(10),
	"FELTLENGDE" NUMBER(3,0),
	"AVSNITT_ID_LEDETEKST" NUMBER,
	"REG_DATO" DATE,
	"REG_USER" VARCHAR2(8),
	"MOD_DATO" DATE,
	"MOD_USER" VARCHAR2(8),
	 CONSTRAINT "VEDFAKTTYP_PK" PRIMARY KEY ("VEDTAKFAKTAKODE") ENABLE
   ) ORGANIZATION INDEX NOCOMPRESS ;

   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."VEDTAKFAKTAKODE" IS 'Kode som entydig identifiserer en typeverdi';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."SKJERMBILDETEKST" IS 'Tekststreng for visning i skjermbilde';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."STATUS_KVOTEBRUK" IS 'Status kvotebruk';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."STATUS_OVERSIKT" IS 'Status oversikt. Brukes til Ã¥ sjekke om elementet skal vises i liste pÃ¥ modul VF_20_beregnstonad';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."VEDTAKFAKTANAVN" IS 'Vedtakfaktanavn';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."BESKRIVELSE" IS 'Generell beskrivelse';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."ORACLETYPE" IS 'Oracletype';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."FELTLENGDE" IS 'Feltlengde';
   COMMENT ON COLUMN "VEDTAKFAKTATYPE"."AVSNITT_ID_LEDETEKST" IS 'Referanse til AVSNITT';
   COMMENT ON TABLE "VEDTAKFAKTATYPE"  IS 'Typer av vedtakfakta som er lovlige';
--------------------------------------------------------
--  DDL for Index ANMERK_ANMTYP_FKI
--------------------------------------------------------

  CREATE INDEX "ANMERK_ANMTYP_FKI" ON "ANMERKNING" ("ANMERKNINGKODE")
  ;
--------------------------------------------------------
--  DDL for Index ANMERK_I
--------------------------------------------------------

  CREATE INDEX "ANMERK_I" ON "ANMERKNING" ("OBJEKT_ID", "TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index ANMERK_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "ANMERK_OBJTYP_FKI" ON "ANMERKNING" ("TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index ANMERK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "ANMERK_PK" ON "ANMERKNING" ("ANMERKNING_ID")
  ;
--------------------------------------------------------
--  DDL for Index ANMERK_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "ANMERK_VEDTAK_FKI" ON "ANMERKNING" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index ANMTYP_HENDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "ANMTYP_HENDTYP_FKI" ON "ANMERKNINGTYPE" ("HENDELSETYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index BEREGNINGSLOGG_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BEREGNINGSLOGG_PK" ON "BEREGNINGSLOGG" ("BEREGNINGSLOGG_ID")
  ;
--------------------------------------------------------
--  DDL for Index BERLOGG_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "BERLOGG_OBJTYP_FKI" ON "BEREGNINGSLOGG" ("OBJEKT_ID", "TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index BERLOGG_OBJTYP_FK2I
--------------------------------------------------------

  CREATE INDEX "BERLOGG_OBJTYP_FK2I" ON "BEREGNINGSLOGG" ("TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index BERLOGG_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "BERLOGG_PERS_FKI" ON "BEREGNINGSLOGG" ("PERSON_ID", "REG_DATO")
  ;
--------------------------------------------------------
--  DDL for Index BERLOGG_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "BERLOGG_VEDTAK_FKI" ON "BEREGNINGSLOGG" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index BEN_BEN_FKI
--------------------------------------------------------

  CREATE INDEX "BEN_BEN_FKI" ON "BETALINGSPLAN" ("BETALINGSPLAN_ID_RELATERT")
  ;
--------------------------------------------------------
--  DDL for Index BETPLAN_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "BETPLAN_PK" ON "BETALINGSPLAN" ("BETALINGSPLAN_ID")
  ;
--------------------------------------------------------
--  DDL for Index BETPLAN_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "BETPLAN_VEDTAK_FKI" ON "BETALINGSPLAN" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_BATCH_I
--------------------------------------------------------

  CREATE INDEX "MKORT_BATCH_I" ON "MELDEKORT" ("PERSON_ID", "MELDEKORTKODE", "BEREGNINGSTATUSKODE", "PERIODEKODE", "AAR", "MOD_DATO")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_BSTATUS_I
--------------------------------------------------------

  CREATE INDEX "MKORT_BSTATUS_I" ON "MELDEKORT" ("BEREGNINGSTATUSKODE", "MKSKORTKODE")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_DATO_I
--------------------------------------------------------

  CREATE INDEX "MKORT_DATO_I" ON "MELDEKORT" ("PERSON_ID", "DATO_INNKOMMET")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_MKORT_FKI
--------------------------------------------------------

  CREATE INDEX "MKORT_MKORT_FKI" ON "MELDEKORT" ("MELDEKORT_ID_RELATERT")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_MKORTPERBR_FKI
--------------------------------------------------------

  CREATE INDEX "MKORT_MKORTPERBR_FKI" ON "MELDEKORT" ("AAR", "PERIODEKODE", "MELDEKORTKODE")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_MKORTTYP_FKI
--------------------------------------------------------

  CREATE INDEX "MKORT_MKORTTYP_FKI" ON "MELDEKORT" ("MELDEKORTKODE")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_MKSKORTTYP_FKI
--------------------------------------------------------

  CREATE INDEX "MKORT_MKSKORTTYP_FKI" ON "MELDEKORT" ("MKSKORTKODE")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_PERS_AAR_PER_I
--------------------------------------------------------

  CREATE INDEX "MKORT_PERS_AAR_PER_I" ON "MELDEKORT" ("PERSON_ID", "AAR", "PERIODEKODE")
  ;
--------------------------------------------------------
--  DDL for Index MKORT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MKORT_PK" ON "MELDEKORT" ("MELDEKORT_ID")
  ;
--------------------------------------------------------
--  DDL for Index MKDAG_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "MKDAG_PK" ON "MELDEKORTDAG" ("MELDEKORT_ID", "UKENR", "DAGNR")
  ;
--------------------------------------------------------
--  DDL for Index MLOGG_HENDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "MLOGG_HENDTYP_FKI" ON "MELDELOGG" ("HENDELSETYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index MLOGG_I1
--------------------------------------------------------

  CREATE INDEX "MLOGG_I1" ON "MELDELOGG" ("KORRELASJONS_ID")
  ;
--------------------------------------------------------
--  DDL for Index MLOGG_I2
--------------------------------------------------------

  CREATE INDEX "MLOGG_I2" ON "MELDELOGG" ("HENDELSEDATO", "HENDELSETYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index MLOGG_MKORT_FKI
--------------------------------------------------------

  CREATE INDEX "MLOGG_MKORT_FKI" ON "MELDELOGG" ("MELDEKORT_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_EDBL_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_EDBL_FKI" ON "PERSON" ("MAALFORM", "PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_FODSELSDATO_I
--------------------------------------------------------

  CREATE INDEX "PERS_FODSELSDATO_I" ON "PERSON" ("FODSELSDATO")
  ;
--------------------------------------------------------
--  DDL for Index PERS_FORMGRTYP_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_FORMGRTYP_FKI" ON "PERSON" ("FORMIDLINGSGRUPPEKODE", "PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_HMAAL_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_HMAAL_FKI" ON "PERSON" ("HOVEDMAALKODE")
  ;
--------------------------------------------------------
--  DDL for Index PERS_KVALGRPTYP_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_KVALGRPTYP_FKI" ON "PERSON" ("KVALIFISERINGSGRUPPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index PERS_LAND_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_LAND_FKI" ON "PERSON" ("LANDKODE_STATSBORGER", "PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_NAVN_I
--------------------------------------------------------

  CREATE INDEX "PERS_NAVN_I" ON "PERSON" ("ETTERNAVN", "FORNAVN")
  ;
--------------------------------------------------------
--  DDL for Index PERS_ORGUNIT_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_ORGUNIT_FKI" ON "PERSON" ("AETATORGENHET", "PERSON_ID", "FORMIDLINGSGRUPPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index PERS_ORGUNIT_FK2I
--------------------------------------------------------

  CREATE INDEX "PERS_ORGUNIT_FK2I" ON "PERSON" ("BRUKERID_NAV_KONTAKT")
  ;
--------------------------------------------------------
--  DDL for Index PERS_ORGUNIT_KODER_I
--------------------------------------------------------

  CREATE INDEX "PERS_ORGUNIT_KODER_I" ON "PERSON" ("AETATORGENHET", "FORMIDLINGSGRUPPEKODE", "RETTIGHETSGRUPPEKODE", "KVALIFISERINGSGRUPPEKODE", "HOVEDMAALKODE", "PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERS_PK" ON "PERSON" ("PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index PERS_RETTGRTYP_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_RETTGRTYP_FKI" ON "PERSON" ("RETTIGHETSGRUPPEKODE", "AETATORGENHET", "FORMIDLINGSGRUPPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index PERS_UK2
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERS_UK2" ON "PERSON" ("FODSELSNR")
  ;
--------------------------------------------------------
--  DDL for Index PERS_UPPER_NAVN_I
--------------------------------------------------------

  CREATE INDEX "PERS_UPPER_NAVN_I" ON "PERSON" (UPPER("FORNAVN"), UPPER("ETTERNAVN"))
  ;
--------------------------------------------------------
--  DDL for Index PERS_UPPER_NAVN_I2
--------------------------------------------------------

  CREATE INDEX "PERS_UPPER_NAVN_I2" ON "PERSON" (UPPER("ETTERNAVN"), UPPER("FORNAVN"))
  ;
--------------------------------------------------------
--  DDL for Index PERS_VIKGRTYP_FKI
--------------------------------------------------------

  CREATE INDEX "PERS_VIKGRTYP_FKI" ON "PERSON" ("VIKARGRUPPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_BETMOT_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_BETMOT_FKI" ON "POSTERING" ("EKSTERNENHET_ID_ALTMOTTAKER")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_I2
--------------------------------------------------------

  CREATE INDEX "POSTER_I2" ON "POSTERING" ("TABELLNAVNALIAS_KILDE", "OBJEKT_ID_KILDE", "VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_I3
--------------------------------------------------------

  CREATE INDEX "POSTER_I3" ON "POSTERING" ("PERSON_ID", "DATO_POSTERT")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_MELDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_MELDTYP_FKI" ON "POSTERING" ("MELDINGKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_MKORT_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_MKORT_FKI" ON "POSTERING" ("MELDEKORT_ID")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_OBJTYP_FKI" ON "POSTERING" ("OBJEKT_ID_KILDE", "TABELLNAVNALIAS_KILDE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_ORGPERS_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_ORGPERS_FKI" ON "POSTERING" ("BRUKER_ID_SAKSBEHANDLER")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_ORGUNIT_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_ORGUNIT_FKI" ON "POSTERING" ("AETATENHET_ANSVARLIG")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_PERS_FKI" ON "POSTERING" ("PERSON_ID", "DATO_GRUNNLAG", "TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "POSTER_PK" ON "POSTERING" ("POSTERING_ID", "POSTERINGTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_POSTERTYP_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_POSTERTYP_FKI" ON "POSTERING" ("POSTERINGTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_TRANSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_TRANSTYP_FKI" ON "POSTERING" ("TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index POSTER_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "POSTER_VEDTAK_FKI" ON "POSTERING" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index RETTYP_GJETYP_FKI
--------------------------------------------------------

  CREATE INDEX "RETTYP_GJETYP_FKI" ON "RETTIGHETTYPE" ("GJELDERKODE")
  ;
--------------------------------------------------------
--  DDL for Index RETTYP_RETTKL_FKI
--------------------------------------------------------

  CREATE INDEX "RETTYP_RETTKL_FKI" ON "RETTIGHETTYPE" ("RETTIGHETSKLASSEKODE")
  ;
--------------------------------------------------------
--  DDL for Index RETTYP_SAKSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "RETTYP_SAKSTYP_FKI" ON "RETTIGHETTYPE" ("SAKSKODE")
  ;
--------------------------------------------------------
--  DDL for Index RETTYP_TRANSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "RETTYP_TRANSTYP_FKI" ON "RETTIGHETTYPE" ("TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index RETTYP_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "RETTYP_UK" ON "RETTIGHETTYPE" ("RETTIGHETNAVN")
  ;
--------------------------------------------------------
--  DDL for Index SAK_OBJEKT_I
--------------------------------------------------------

  CREATE INDEX "SAK_OBJEKT_I" ON "SAK" ("OBJEKT_ID", "TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index SAK_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SAK_OBJTYP_FKI" ON "SAK" ("TABELLNAVNALIAS")
  ;
--------------------------------------------------------
--  DDL for Index SAK_ORGPERS_FKI
--------------------------------------------------------

  CREATE INDEX "SAK_ORGPERS_FKI" ON "SAK" ("BRUKERID_ANSVARLIG")
  ;
--------------------------------------------------------
--  DDL for Index SAK_ORGSTED_FKI
--------------------------------------------------------

  CREATE INDEX "SAK_ORGSTED_FKI" ON "SAK" ("AETATENHET_ANSVARLIG")
  ;
--------------------------------------------------------
--  DDL for Index SAK_ORGSTED_FK2I
--------------------------------------------------------

  CREATE INDEX "SAK_ORGSTED_FK2I" ON "SAK" ("AETATENHET_ARKIV")
  ;
--------------------------------------------------------
--  DDL for Index SAK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SAK_PK" ON "SAK" ("SAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index SAK_SAKSTAT_FKI
--------------------------------------------------------

  CREATE INDEX "SAK_SAKSTAT_FKI" ON "SAK" ("SAKSTATUSKODE")
  ;
--------------------------------------------------------
--  DDL for Index SAK_SAKSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SAK_SAKSTYP_FKI" ON "SAK" ("SAKSKODE")
  ;
--------------------------------------------------------
--  DDL for Index SAK_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SAK_UK" ON "SAK" ("AAR", "LOPENRSAK")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_ART_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_ART_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("ARTKODE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_EKSTENH_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_EKSTENH_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("EKSTERNENHET_ID_ALTMOTTAKER")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_I
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_I" ON "SIM_UTBETALINGSGRUNNLAG" ("OBJEKT_ID_KILDE", "TABELLNAVNALIAS_KILDE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_MELDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_MELDTYP_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("MELDINGKODE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_OBJTYP_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("TABELLNAVNALIAS_KILDE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_PERS_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SUTBETGR_PK" ON "SIM_UTBETALINGSGRUNNLAG" ("SIM_POSTERING_ID")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_POSTERING_I
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_POSTERING_I" ON "SIM_UTBETALINGSGRUNNLAG" ("POSTERING_ID")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_POSTERTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_POSTERTYP_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("POSTERINGTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_PROSJ_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_PROSJ_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("PROSJEKTNUMMER")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_S_MKORT_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_S_MKORT_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("SIM_MELDEKORT_ID")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_TRANSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_TRANSTYP_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_VEDTAK_FKI" ON "SIM_UTBETALINGSGRUNNLAG" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index SUTBETGR_VEDTAK_FK2I
--------------------------------------------------------

  CREATE INDEX "SUTBETGR_VEDTAK_FK2I" ON "SIM_UTBETALINGSGRUNNLAG" ("VEDTAK_ID_FEILUTBET")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_BETMOT_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_BETMOT_FKI" ON "SPESIALUTBETALING" ("EKSTERNENHET_ID_ALTMOTTAKER")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_L_RETTAKTF_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_L_RETTAKTF_FKI" ON "SPESIALUTBETALING" ("RETTIGHETKODE", "AKTFASEKODE")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_ORGPERS_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_ORGPERS_FKI" ON "SPESIALUTBETALING" ("BRUKER_ID_SAKSBEHANDLER")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_ORGPERS_FK2I
--------------------------------------------------------

  CREATE INDEX "SPESBET_ORGPERS_FK2I" ON "SPESIALUTBETALING" ("BRUKER_ID_BESLUTTER")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_PERS_FKI" ON "SPESIALUTBETALING" ("PERSON_ID")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SPESBET_PK" ON "SPESIALUTBETALING" ("SPESUTBETALING_ID")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_POSTERTYP_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_POSTERTYP_FKI" ON "SPESIALUTBETALING" ("POSTERINGTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "SPESBET_UK" ON "SPESIALUTBETALING" ("LOPENR", "VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_VEDST_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_VEDST_FKI" ON "SPESIALUTBETALING" ("VEDTAKSTATUSKODE")
  ;
--------------------------------------------------------
--  DDL for Index SPESBET_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "SPESBET_VEDTAK_FKI" ON "SPESIALUTBETALING" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index TRANSTYP_TRANSGR_FKI
--------------------------------------------------------

  CREATE INDEX "TRANSTYP_TRANSGR_FKI" ON "TRANSAKSJONTYPE" ("TRANSGRUPPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_BETMOT_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_BETMOT_FKI" ON "UTBETALINGSGRUNNLAG" ("EKSTERNENHET_ID_ALTMOTTAKER")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_MELDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_MELDTYP_FKI" ON "UTBETALINGSGRUNNLAG" ("MELDINGKODE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_MKORT_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_MKORT_FKI" ON "UTBETALINGSGRUNNLAG" ("MELDEKORT_ID")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_OBJTYP_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_OBJTYP_FKI" ON "UTBETALINGSGRUNNLAG" ("OBJEKT_ID_KILDE", "TABELLNAVNALIAS_KILDE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_OBJTYP_FK2I
--------------------------------------------------------

  CREATE INDEX "UTBETGR_OBJTYP_FK2I" ON "UTBETALINGSGRUNNLAG" ("TABELLNAVNALIAS_KILDE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_PERS_FKI" ON "UTBETALINGSGRUNNLAG" ("PERSON_ID", "DATO_GRUNNLAG", "TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "UTBETGR_PK" ON "UTBETALINGSGRUNNLAG" ("POSTERING_ID")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_POSTERTYP_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_POSTERTYP_FKI" ON "UTBETALINGSGRUNNLAG" ("POSTERINGTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_TRANSTYP_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_TRANSTYP_FKI" ON "UTBETALINGSGRUNNLAG" ("TRANSAKSJONSKODE")
  ;
--------------------------------------------------------
--  DDL for Index UTBETGR_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "UTBETGR_VEDTAK_FKI" ON "UTBETALINGSGRUNNLAG" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_AKTFAS_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_AKTFAS_FKI" ON "VEDTAK" ("AKTFASEKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_AVSNLST_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_AVSNLST_FKI" ON "VEDTAK" ("AVSNITTLISTEKODE_VALGT")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_BREV_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_BREV_FKI" ON "VEDTAK" ("BREV_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_BREVVGRP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_BREVVGRP_FKI" ON "VEDTAK" ("TEKSTVARIANTKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_I
--------------------------------------------------------

  CREATE INDEX "VEDTAK_I" ON "VEDTAK" ("VEDTAK_ID", "AKTFASEKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_I2
--------------------------------------------------------

  CREATE INDEX "VEDTAK_I2" ON "VEDTAK" ("RETTIGHETKODE", "UTFALLKODE", "VEDTAKSTATUSKODE", "VEDTAKTYPEKODE", "AKTFASEKODE", "VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_KRAVTYP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_KRAVTYP_FKI" ON "VEDTAK" ("RETTIGHETKODE", "VEDTAKTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_ORGPERS_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_ORGPERS_FKI" ON "VEDTAK" ("BRUKERID_ANSVARLIG")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_ORGPERS_FK2I
--------------------------------------------------------

  CREATE INDEX "VEDTAK_ORGPERS_FK2I" ON "VEDTAK" ("BRUKERID_BESLUTTER")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_ORGPERS_FK3I
--------------------------------------------------------

  CREATE INDEX "VEDTAK_ORGPERS_FK3I" ON "VEDTAK" ("VALGT_BESLUTTER")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_ORGSTED_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_ORGSTED_FKI" ON "VEDTAK" ("AETATENHET_BEHANDLER", "RETTIGHETKODE", "VEDTAKSTATUSKODE", "UTFALLKODE", "VEDTAKTYPEKODE", "AKTFASEKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_ORGSTED_FKI2
--------------------------------------------------------

  CREATE INDEX "VEDTAK_ORGSTED_FKI2" ON "VEDTAK" ("AETATENHET_BEHANDLER", "RETTIGHETKODE", "VEDTAKSTATUSKODE", "UTFALLKODE", "VEDTAKTYPEKODE", "AKTFASEKODE", "VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_PERS_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_PERS_FKI" ON "VEDTAK" ("PERSON_ID", "RETTIGHETKODE", "VEDTAKSTATUSKODE", "AKTFASEKODE", "SAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VEDTAK_PK" ON "VEDTAK" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_SAK_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_SAK_FKI" ON "VEDTAK" ("SAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_SF_OPP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_SF_OPP_FKI" ON "VEDTAK" ("SF_OPPFOLGING_ID")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_UK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VEDTAK_UK" ON "VEDTAK" ("AAR", "LOPENRSAK", "LOPENRVEDTAK")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_UTFTYP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_UTFTYP_FKI" ON "VEDTAK" ("UTFALLKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_VEDSTAT_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_VEDSTAT_FKI" ON "VEDTAK" ("VEDTAKSTATUSKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_VEDTAK_FKI" ON "VEDTAK" ("VEDTAK_ID_RELATERT")
  ;
--------------------------------------------------------
--  DDL for Index VEDTAK_VEDTYP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDTAK_VEDTYP_FKI" ON "VEDTAK" ("VEDTAKTYPEKODE")
  ;
--------------------------------------------------------
--  DDL for Index IDX_VEDTAK_MOD_DATO
--------------------------------------------------------

  CREATE INDEX "IDX_VEDTAK_MOD_DATO" ON "VEDTAK" ("MOD_DATO")
  ;
--------------------------------------------------------
--  DDL for Index VEDFAKT_DATO_FRA_TIL_IND
--------------------------------------------------------

  CREATE INDEX "VEDFAKT_DATO_FRA_TIL_IND" ON "VEDTAKFAKTA" (CASE "VEDTAKFAKTAKODE" WHEN 'TDATO' THEN "VEDTAKFAKTAKODE" WHEN 'FDATO' THEN "VEDTAKFAKTAKODE" END , CASE "VEDTAKFAKTAKODE" WHEN 'TDATO' THEN "VEDTAK_ID" WHEN 'FDATO' THEN "VEDTAK_ID" END , CASE "VEDTAKFAKTAKODE" WHEN 'TDATO' THEN TO_DATE(NVL("VEDTAKVERDI",'31-12-9999'),'DD-MM-YYYY') WHEN 'FDATO' THEN TO_DATE(NVL("VEDTAKVERDI",'01-01-1900'),'DD-MM-YYYY') END )
  ;
--------------------------------------------------------
--  DDL for Index VEDFAKT_IND_ALLE
--------------------------------------------------------

  CREATE INDEX "VEDFAKT_IND_ALLE" ON "VEDTAKFAKTA" ("VEDTAK_ID", "VEDTAKFAKTAKODE", "VEDTAKVERDI")
  ;
--------------------------------------------------------
--  DDL for Index VEDFAKT_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "VEDFAKT_PK" ON "VEDTAKFAKTA" ("VEDTAK_ID", "VEDTAKFAKTAKODE")
  ;
--------------------------------------------------------
--  DDL for Index VEDFAKT_VEDFTYP_FKI
--------------------------------------------------------

  CREATE INDEX "VEDFAKT_VEDFTYP_FKI" ON "VEDTAKFAKTA" ("VEDTAKFAKTAKODE", "VEDTAKVERDI")
  ;
--------------------------------------------------------
--  DDL for Index VEDFAKTTYP_AVSN_FKI
--------------------------------------------------------

  CREATE INDEX "VEDFAKTTYP_AVSN_FKI" ON "VEDTAKFAKTATYPE" ("AVSNITT_ID_LEDETEKST")
  ;
--------------------------------------------------------
--  DDL for Index FEILUTBET_OVERFORING_HIST_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "FEILUTBET_OVERFORING_HIST_PK" ON "FEILUTBET_OVERFORING_HIST" ("FEILUTBET_OVERFORING_ID")
  ;
--------------------------------------------------------
--  DDL for Index FEILUTBET_OVERFORING_HIST_VEDTAK_FKI
--------------------------------------------------------

  CREATE INDEX "FEILUTBET_OVERFORING_HIST_VEDTAK_FKI" ON "FEILUTBET_OVERFORING_HIST" ("VEDTAK_ID")
  ;
--------------------------------------------------------
--  DDL for Index FEILUTBET_OVERFORING_HIST_VEDTAK_FK2I
--------------------------------------------------------

  CREATE INDEX "FEILUTBET_OVERFORING_HIST_VEDTAK_FK2I" ON "FEILUTBET_OVERFORING_HIST" ("VEDTAK_ID_RELATERT")
  ;
--------------------------------------------------------
--  Constraints for Table ANMERKNING
--------------------------------------------------------

  ALTER TABLE "ANMERKNING" ADD CONSTRAINT "ANMERK_PK" PRIMARY KEY ("ANMERKNING_ID")
  USING INDEX "ANMERK_PK"  ENABLE;
  ALTER TABLE "ANMERKNING" MODIFY ("ANMERKNINGKODE" NOT NULL ENABLE);
  ALTER TABLE "ANMERKNING" MODIFY ("ANMERKNING_ID" NOT NULL ENABLE);
  ALTER TABLE "ANMERKNING" MODIFY ("TABELLNAVNALIAS" NOT NULL ENABLE);
  ALTER TABLE "ANMERKNING" MODIFY ("OBJEKT_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table ANMERKNINGTYPE
--------------------------------------------------------

  ALTER TABLE "ANMERKNINGTYPE" MODIFY ("ANMERKNINGKODE" NOT NULL ENABLE);
  ALTER TABLE "ANMERKNINGTYPE" MODIFY ("ANMERKNINGNAVN" NOT NULL ENABLE);
  ALTER TABLE "ANMERKNINGTYPE" MODIFY ("HENDELSETYPEKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BEREGNINGSLOGG
--------------------------------------------------------

  ALTER TABLE "BEREGNINGSLOGG" ADD CONSTRAINT "BEREGNINGSLOGG_PK" PRIMARY KEY ("BEREGNINGSLOGG_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("TABELLNAVNALIAS" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("OBJEKT_ID" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("DATO_FRA" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSLOGG" MODIFY ("DATO_TIL" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BEREGNINGSTATUS
--------------------------------------------------------

  ALTER TABLE "BEREGNINGSTATUS" MODIFY ("BEREGNINGSTATUSKODE" NOT NULL ENABLE);
  ALTER TABLE "BEREGNINGSTATUS" MODIFY ("BEREGNINGSTATUSNAVN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table BETALINGSPLAN
--------------------------------------------------------

  ALTER TABLE "BETALINGSPLAN" MODIFY ("ETTERBETALING" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_STATJN_CK4" CHECK (etterbetaling in ('J','N')) ENABLE;
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_PK" PRIMARY KEY ("BETALINGSPLAN_ID")
  USING INDEX "BETPLAN_PK"  ENABLE;
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_STATJN_CK" CHECK (status_klar in ('J','N')) ENABLE;
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_STATJN_CK2" CHECK (status_nydok in ('J','N')) ENABLE;
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_STATJN_CK3" CHECK (status_utbetgrunnlag in ('J','N')) ENABLE;
  ALTER TABLE "BETALINGSPLAN" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("UTBETALINGNR" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("STATUS_NYDOK" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("BELOPKODE" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("BELOP" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("STATUS_KLAR" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("STATUS_UTBETGRUNNLAG" NOT NULL ENABLE);
  ALTER TABLE "BETALINGSPLAN" MODIFY ("BETALINGSPLAN_ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table FEILUTBET_OVERFORING_HIST
--------------------------------------------------------

  ALTER TABLE "FEILUTBET_OVERFORING_HIST" ADD CONSTRAINT "FEILUTBET_OVERFORING_HIST_PK" PRIMARY KEY ("FEILUTBET_OVERFORING_ID")
  USING INDEX  ENABLE;
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" ADD CHECK ( vedtaktypekode IN ( 'E', 'G', 'O', 'S' ) ) ENABLE;
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" ADD CHECK ( status IN ( 'BEHANDLET', 'MANUELL', 'UBEHANDLET' ) ) ENABLE;
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("FEILUTBET_OVERFORING_ID" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("FODSELSNR" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("AAR_SAK" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("LOPENRSAK" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("LOPENRVEDTAK" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("VEDTAKTYPEKODE" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("DATO_FRA" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("DATO_TIL" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("STONADTYPE" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("TILBAKEBETALINGSBELOP" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("REG_DATO" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("REG_USER" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("MOD_DATO" NOT NULL ENABLE);
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" MODIFY ("MOD_USER" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MELDEGRUPPETYPE
--------------------------------------------------------

  ALTER TABLE "MELDEGRUPPETYPE" MODIFY ("MELDEGRUPPEKODE" NOT NULL ENABLE);
  ALTER TABLE "MELDEGRUPPETYPE" MODIFY ("MELDEGRUPPENAVN" NOT NULL ENABLE);
  ALTER TABLE "MELDEGRUPPETYPE" MODIFY ("NIVAA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MELDEKORT
--------------------------------------------------------

  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK3" CHECK (status_syk in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_PK" PRIMARY KEY ("MELDEKORT_ID")
  USING INDEX "MKORT_PK"  ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK1" CHECK (status_arbeidet in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK2" CHECK (status_kurs in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK4" CHECK (status_periodespoersmaal in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK5" CHECK (status_annetfravaer in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK6" CHECK (status_fortsatt_arbeidsoker in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK7" CHECK (feil_paa_kort in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_STATJN_CK8" CHECK (veiledning in ('J','N','I','B')) ENABLE;
  ALTER TABLE "MELDEKORT" MODIFY ("MELDEKORT_ID" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORT" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORT" MODIFY ("MKSKORTKODE" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORT" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORT" MODIFY ("PERIODEKODE" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORT" MODIFY ("BEREGNINGSTATUSKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MELDEKORTDAG
--------------------------------------------------------

  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_PK" PRIMARY KEY ("MELDEKORT_ID", "UKENR", "DAGNR")
  USING INDEX "MKDAG_PK"  ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_CK1" CHECK (Ukenr BETWEEN 1 AND 53) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_CK2" CHECK (Dagnr BETWEEN 1 AND 7) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_STATJN_CK" CHECK (status_arbeidsdag in ('J','N')) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_STATJN_CK2" CHECK (status_ferie in ('J','N')) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_STATJN_CK3" CHECK (status_kurs in ('J','N')) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_STATJN_CK4" CHECK (status_syk in ('J','N')) ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_STATJN_CK5" CHECK (status_annetfravaer_attf in ('J','N')) ENABLE;
  ALTER TABLE "MELDEKORTDAG" MODIFY ("MELDEKORT_ID" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("UKENR" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("DAGNR" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("STATUS_ARBEIDSDAG" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("STATUS_KURS" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("STATUS_SYK" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTDAG" MODIFY ("TIMER_ARBEIDET" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MELDEKORTPERIODE
--------------------------------------------------------

  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("PERIODEKODE" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("UKENR_UKE1" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("UKENR_UKE2" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("DATO_FRA" NOT NULL ENABLE);
  ALTER TABLE "MELDEKORTPERIODE" MODIFY ("DATO_TIL" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MELDELOGG
--------------------------------------------------------

  ALTER TABLE "MELDELOGG" MODIFY ("MELDEKORT_ID" NOT NULL ENABLE);
  ALTER TABLE "MELDELOGG" MODIFY ("HENDELSEDATO" NOT NULL ENABLE);
  ALTER TABLE "MELDELOGG" MODIFY ("HENDELSETYPEKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table MKSKORTTYPE
--------------------------------------------------------

  ALTER TABLE "MKSKORTTYPE" MODIFY ("MKSKORTKODE" NOT NULL ENABLE);
  ALTER TABLE "MKSKORTTYPE" MODIFY ("MKSKORTTYPENAVN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERSON
--------------------------------------------------------

  ALTER TABLE "PERSON" ADD CONSTRAINT "PERSON_CK6" CHECK (PERSON_ID_STATUS
IN ('AKTIV','DUPLIKAT_TIL_BEH','DUPLIKAT','ANNULLERES','ANNULLERT','UGYLDIG','SPERRET','RESERVERT')) ENABLE;
  ALTER TABLE "PERSON" MODIFY ("PERSON_ID_STATUS" NOT NULL ENABLE);
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_DOED_CK5" CHECK (er_doed in ('J',NULL)) ENABLE;
  ALTER TABLE "PERSON" MODIFY ("FORNAVN" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("STATUS_SAMTYKKE" NOT NULL ENABLE);
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_PK" PRIMARY KEY ("PERSON_ID")
  USING INDEX "PERS_PK"  ENABLE;
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_STATJN_CK3" CHECK (STATUS_BILDISP  IN ('J','N')) ENABLE;
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_STATJN_CK" CHECK (status_dnr in ('J','N')) ENABLE;
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_UK2" UNIQUE ("FODSELSNR")
  USING INDEX "PERS_UK2"  ENABLE;
  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_STATJN_CK2" CHECK (status_samtykke in ('J','N','B','G')) ENABLE;
  ALTER TABLE "PERSON" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("STATUS_DNR" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("ETTERNAVN" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("DATO_FRA" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("MAALFORM" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("FORMIDLINGSGRUPPEKODE" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("VIKARGRUPPEKODE" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("KVALIFISERINGSGRUPPEKODE" NOT NULL ENABLE);
  ALTER TABLE "PERSON" MODIFY ("RETTIGHETSGRUPPEKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table POSTERING
--------------------------------------------------------

  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_PK" PRIMARY KEY ("POSTERING_ID", "POSTERINGTYPEKODE")
  USING INDEX "POSTER_PK"  ENABLE;
  ALTER TABLE "POSTERING" MODIFY ("POSTERING_ID" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("BELOP" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("BELOPKODE" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("DATO_PERIODE_FRA" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("DATO_PERIODE_TIL" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("DATO_POSTERT" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("POSTERINGTYPEKODE" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("TRANSAKSJONSKODE" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("DATO_GRUNNLAG" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("ARTKODE" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("KAPITTEL" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("POST" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("UNDERPOST" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("BRUKER_ID_SAKSBEHANDLER" NOT NULL ENABLE);
  ALTER TABLE "POSTERING" MODIFY ("AETATENHET_ANSVARLIG" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table RETTIGHETTYPE
--------------------------------------------------------

  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_UK" UNIQUE ("RETTIGHETNAVN")
  USING INDEX "RETTYP_UK"  ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYPE_CK" CHECK (status_konterbar IN ('J','N')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK2" CHECK ((status_konterbar = 'N'
and belopkode Is NULL) OR (status_konterbar = 'J')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK3" CHECK (forskudd_betplan IN ('J','N')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK4" CHECK (SATSVALG in ('SATS','FAKTISK','VALGBAR')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK5" CHECK (status_tiltak = 'J') ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK6" CHECK (status_start_vedtak in ('J','N')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK7" CHECK (bilag_kreves_jn  in ('J','N')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_CK8" CHECK (betplan_jn IN ('J','N')) ENABLE;
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("RETTIGHETKODE" NOT NULL ENABLE);
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("RETTIGHETNAVN" NOT NULL ENABLE);
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("DATO_GYLDIG_FRA" NOT NULL ENABLE);
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("SAKSKODE" NOT NULL ENABLE);
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("STATUS_KONTERBAR" NOT NULL ENABLE);
  ALTER TABLE "RETTIGHETTYPE" MODIFY ("BETPLAN_JN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table SAK
--------------------------------------------------------

  ALTER TABLE "SAK" MODIFY ("ER_UTLAND" NOT NULL ENABLE);
  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_CK" CHECK (ER_UTLAND IN ('N','J')) ENABLE;
  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_PK" PRIMARY KEY ("SAK_ID")
  USING INDEX "SAK_PK"  ENABLE;
  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_UK" UNIQUE ("AAR", "LOPENRSAK")
  USING INDEX "SAK_UK"  ENABLE;
  ALTER TABLE "SAK" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "SAK" MODIFY ("SAK_ID" NOT NULL ENABLE);
  ALTER TABLE "SAK" MODIFY ("SAKSKODE" NOT NULL ENABLE);
  ALTER TABLE "SAK" MODIFY ("TABELLNAVNALIAS" NOT NULL ENABLE);
  ALTER TABLE "SAK" MODIFY ("LOPENRSAK" NOT NULL ENABLE);
  ALTER TABLE "SAK" MODIFY ("SAKSTATUSKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table SIM_UTBETALINGSGRUNNLAG
--------------------------------------------------------

  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_CK2" CHECK ( BELOP_SATT_MANUELT IN ('J', 'N')) ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("KONTOSTEDKODE" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("VEDTAK_ID_FEILUTBET" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_PK" PRIMARY KEY ("SIM_POSTERING_ID")
  USING INDEX "SUTBETGR_PK"  ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("SIM_POSTERING_ID" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("BELOPKODE" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("DATO_PERIODE_FRA" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("TRANSAKSJONSKODE" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("ARTKODE" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("DATO_PERIODE_TIL" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" MODIFY ("DATO_GRUNNLAG" NOT NULL ENABLE);
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_CK" CHECK (status_manuell IN ('J','N')) ENABLE;
--------------------------------------------------------
--  Constraints for Table SPESIALUTBETALING
--------------------------------------------------------

  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_PK" PRIMARY KEY ("SPESUTBETALING_ID")
  USING INDEX "SPESBET_PK"  ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_UK" UNIQUE ("LOPENR", "VEDTAK_ID")
  USING INDEX "SPESBET_UK"  ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_CK" CHECK (ORDINAER_YTELSE in ('J')) ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_CK2" CHECK (status_bilag in ('J','N')) ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_CK3" CHECK (status_anvis_bilag in ('J','N')) ENABLE;
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("SPESUTBETALING_ID" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("BRUKER_ID_SAKSBEHANDLER" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("BELOPKODE" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("RETTIGHETKODE" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("AKTFASEKODE" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("VEDTAKSTATUSKODE" NOT NULL ENABLE);
  ALTER TABLE "SPESIALUTBETALING" MODIFY ("POSTERINGTYPEKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table TRANSAKSJONTYPE
--------------------------------------------------------

  ALTER TABLE "TRANSAKSJONTYPE" MODIFY ("TRANSAKSJONSKODE" NOT NULL ENABLE);
  ALTER TABLE "TRANSAKSJONTYPE" MODIFY ("TRANSAKSJONSTYPENAVN" NOT NULL ENABLE);
  ALTER TABLE "TRANSAKSJONTYPE" MODIFY ("TRANSTYPENAVN" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table UTBETALINGSGRUNNLAG
--------------------------------------------------------

  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("PERSON_ID" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_PK" PRIMARY KEY ("POSTERING_ID")
  USING INDEX "UTBETGR_PK"  ENABLE;
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("POSTERING_ID" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("BELOP" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("BELOPKODE" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("DATO_PERIODE_FRA" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("TRANSAKSJONSKODE" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("DATO_GRUNNLAG" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("ARTKODE" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("DATO_PERIODE_TIL" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("KAPITTEL" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("POST" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("UNDERPOST" NOT NULL ENABLE);
  ALTER TABLE "UTBETALINGSGRUNNLAG" MODIFY ("KONTOSTEDKODE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table VEDTAK
--------------------------------------------------------

  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_CK2" CHECK (vedlegg_betplan in ('J','N')) ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_CKTEK" CHECK (TEKNISK_VEDTAK IN ('J','N')) ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_CK_1" CHECK (ER_UTLAND IN ('J','N')) ENABLE;
  ALTER TABLE "VEDTAK" ADD SUPPLEMENTAL LOG DATA (FOREIGN KEY) COLUMNS;
  ALTER TABLE "VEDTAK" ADD SUPPLEMENTAL LOG DATA (ALL) COLUMNS;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_CK3" CHECK (overfort_navi in ('J')) ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_PK" PRIMARY KEY ("VEDTAK_ID")
  USING INDEX "VEDTAK_PK"  ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_UK" UNIQUE ("AAR", "LOPENRSAK", "LOPENRVEDTAK")
  USING INDEX "VEDTAK_UK"  ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_CK" CHECK (status_sensitiv in ('J','N')) ENABLE;
  ALTER TABLE "VEDTAK" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("SAK_ID" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("VEDTAKSTATUSKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("VEDTAKTYPEKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("AETATENHET_BEHANDLER" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("AAR" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("LOPENRSAK" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("LOPENRVEDTAK" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("RETTIGHETKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("AKTFASEKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("DATO_MOTTATT" NOT NULL ENABLE);
  ALTER TABLE "VEDTAK" MODIFY ("STATUS_SOSIALDATA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table VEDTAKFAKTA
--------------------------------------------------------

  ALTER TABLE "VEDTAKFAKTA" MODIFY ("VEDTAK_ID" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTA" MODIFY ("VEDTAKFAKTAKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTA" ADD CONSTRAINT "VEDFAKT_PK" PRIMARY KEY ("VEDTAK_ID", "VEDTAKFAKTAKODE")
  USING INDEX  ENABLE;
--------------------------------------------------------
--  Constraints for Table VEDTAKFAKTATYPE
--------------------------------------------------------

  ALTER TABLE "VEDTAKFAKTATYPE" ADD CONSTRAINT "VEDFAKTTYP_STATJN_CK" CHECK (status_kvotebruk in ('J','N')) ENABLE;
  ALTER TABLE "VEDTAKFAKTATYPE" ADD CONSTRAINT "VEDFAKTTYP_STATJN_CK2" CHECK (status_oversikt in ('J','N')) ENABLE;
  ALTER TABLE "VEDTAKFAKTATYPE" MODIFY ("VEDTAKFAKTAKODE" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTATYPE" MODIFY ("SKJERMBILDETEKST" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTATYPE" MODIFY ("STATUS_KVOTEBRUK" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTATYPE" MODIFY ("STATUS_OVERSIKT" NOT NULL ENABLE);
  ALTER TABLE "VEDTAKFAKTATYPE" MODIFY ("VEDTAKFAKTANAVN" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table ANMERKNING
--------------------------------------------------------

  ALTER TABLE "ANMERKNING" ADD CONSTRAINT "ANMERK_ANMTYP_FK" FOREIGN KEY ("ANMERKNINGKODE")
	  REFERENCES "ANMERKNINGTYPE" ("ANMERKNINGKODE") ENABLE;
--  ALTER TABLE "ANMERKNING" ADD CONSTRAINT "ANMERK_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
  ALTER TABLE "ANMERKNING" ADD CONSTRAINT "ANMERK_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ANMERKNINGTYPE
--------------------------------------------------------

--  ALTER TABLE "ANMERKNINGTYPE" ADD CONSTRAINT "ANMTYP_HENDTYP_FK" FOREIGN KEY ("HENDELSETYPEKODE")
--	  REFERENCES "HENDELSETYPE" ("HENDELSETYPEKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table BEREGNINGSLOGG
--------------------------------------------------------

--  ALTER TABLE "BEREGNINGSLOGG" ADD CONSTRAINT "BERLOGG_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
  ALTER TABLE "BEREGNINGSLOGG" ADD CONSTRAINT "BERLOGG_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
  ALTER TABLE "BEREGNINGSLOGG" ADD CONSTRAINT "BERLOGG_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table BETALINGSPLAN
--------------------------------------------------------

  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BETPLAN_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
  ALTER TABLE "BETALINGSPLAN" ADD CONSTRAINT "BEN_BEN_FK" FOREIGN KEY ("BETALINGSPLAN_ID_RELATERT")
	  REFERENCES "BETALINGSPLAN" ("BETALINGSPLAN_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FEILUTBET_OVERFORING_HIST
--------------------------------------------------------

  ALTER TABLE "FEILUTBET_OVERFORING_HIST" ADD CONSTRAINT "FEILUTBET_OVERFORING_HIST_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID_RELATERT")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
  ALTER TABLE "FEILUTBET_OVERFORING_HIST" ADD CONSTRAINT "FEILUTBET_OVERFORING_HIST_VEDTAK_FK2" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table MELDEKORT
--------------------------------------------------------

  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_BERSTAT_FK" FOREIGN KEY ("BEREGNINGSTATUSKODE")
	  REFERENCES "BEREGNINGSTATUS" ("BEREGNINGSTATUSKODE") ENABLE;
--  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_MKORTPERBR_FK" FOREIGN KEY ("AAR", "PERIODEKODE", "MELDEKORTKODE")
--	  REFERENCES "MELDEKORTPERIODEBRUK" ("AAR", "PERIODEKODE", "MELDEKORTKODE") ENABLE;
--  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_MKORTTYP_FK" FOREIGN KEY ("MELDEKORTKODE")
--	  REFERENCES "MELDEKORTTYPE" ("MELDEKORTKODE") ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_MKORT_FK" FOREIGN KEY ("MELDEKORT_ID_RELATERT")
	  REFERENCES "MELDEKORT" ("MELDEKORT_ID") ENABLE;
  ALTER TABLE "MELDEKORT" ADD CONSTRAINT "MKORT_MKSKORTTYP_FK" FOREIGN KEY ("MKSKORTKODE")
	  REFERENCES "MKSKORTTYPE" ("MKSKORTKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table MELDEKORTDAG
--------------------------------------------------------

  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_MGRPTYP_FK" FOREIGN KEY ("MELDEGRUPPEKODE")
	  REFERENCES "MELDEGRUPPETYPE" ("MELDEGRUPPEKODE") ENABLE;
  ALTER TABLE "MELDEKORTDAG" ADD CONSTRAINT "MKDAG_MKORT_FK" FOREIGN KEY ("MELDEKORT_ID")
	  REFERENCES "MELDEKORT" ("MELDEKORT_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table MELDELOGG
--------------------------------------------------------

--  ALTER TABLE "MELDELOGG" ADD CONSTRAINT "MLOGG_HENDTYP_FK" FOREIGN KEY ("HENDELSETYPEKODE")
--	  REFERENCES "HENDELSETYPE" ("HENDELSETYPEKODE") ENABLE;
  ALTER TABLE "MELDELOGG" ADD CONSTRAINT "MLOGG_MKORT_FK" FOREIGN KEY ("MELDEKORT_ID")
	  REFERENCES "MELDEKORT" ("MELDEKORT_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PERSON
--------------------------------------------------------

--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_ORGUNIT_FK" FOREIGN KEY ("AETATORGENHET")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_EDBL_FK" FOREIGN KEY ("MAALFORM")
--	  REFERENCES "EDB_LANGUAGE" ("LANGID") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_FORMGRTYP_FK" FOREIGN KEY ("FORMIDLINGSGRUPPEKODE")
--	  REFERENCES "FORMIDLINGSGRUPPETYPE" ("FORMIDLINGSGRUPPEKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_KVALGRPTYP_FK" FOREIGN KEY ("KVALIFISERINGSGRUPPEKODE")
--	  REFERENCES "KVALIFISERINGSGRUPPETYPE" ("KVALIFISERINGSGRUPPEKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_LAND_FK" FOREIGN KEY ("LANDKODE_STATSBORGER")
--	  REFERENCES "LAND" ("LANDKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_RETTGRTYP_FK" FOREIGN KEY ("RETTIGHETSGRUPPEKODE")
--	  REFERENCES "RETTIGHETSGRUPPETYPE" ("RETTIGHETSGRUPPEKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_VIKGRTYP_FK" FOREIGN KEY ("VIKARGRUPPEKODE")
--	  REFERENCES "VIKARGRUPPETYPE" ("VIKARGRUPPEKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_HMAAL_FK" FOREIGN KEY ("HOVEDMAALKODE")
--	  REFERENCES "HOVEDMAAL" ("HOVEDMAALKODE") ENABLE;
--  ALTER TABLE "PERSON" ADD CONSTRAINT "PERS_ORGUNIT_FK2" FOREIGN KEY ("BRUKERID_NAV_KONTAKT")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table POSTERING
--------------------------------------------------------

--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_BETMOT_FK" FOREIGN KEY ("EKSTERNENHET_ID_ALTMOTTAKER")
--	  REFERENCES "BETALINGMOTTAKER" ("BETALINGMOTTAKER_ID") ENABLE;
--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_MELDTYP_FK" FOREIGN KEY ("MELDINGKODE")
--	  REFERENCES "MELDINGTYPE" ("MELDINGKODE") ENABLE;
  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_MKORT_FK" FOREIGN KEY ("MELDEKORT_ID")
	  REFERENCES "MELDEKORT" ("MELDEKORT_ID") ENABLE;
--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS_KILDE")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_ORGPERS_FK" FOREIGN KEY ("BRUKER_ID_SAKSBEHANDLER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_ORGUNIT_FK" FOREIGN KEY ("AETATENHET_ANSVARLIG")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
--  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_POSTERTYP_FK" FOREIGN KEY ("POSTERINGTYPEKODE")
--	  REFERENCES "POSTERINGTYPE" ("POSTERINGTYPEKODE") ENABLE;
  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_TRANSTYP_FK" FOREIGN KEY ("TRANSAKSJONSKODE")
	  REFERENCES "TRANSAKSJONTYPE" ("TRANSAKSJONSKODE") ENABLE;
  ALTER TABLE "POSTERING" ADD CONSTRAINT "POSTER_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table RETTIGHETTYPE
--------------------------------------------------------

--  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_GJETYP_FK" FOREIGN KEY ("GJELDERKODE")
--	  REFERENCES "GJELDERTYPE" ("GJELDERKODE") ENABLE;
--  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_RETTKL_FK" FOREIGN KEY ("RETTIGHETSKLASSEKODE")
--	  REFERENCES "RETTIGHETSKLASSE" ("RETTIGHETSKLASSEKODE") ENABLE;
--  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_SAKSTYP_FK" FOREIGN KEY ("SAKSKODE")
--	  REFERENCES "SAKSTYPE" ("SAKSKODE") ENABLE;
  ALTER TABLE "RETTIGHETTYPE" ADD CONSTRAINT "RETTYP_TRANSTYP_FK" FOREIGN KEY ("TRANSAKSJONSKODE")
	  REFERENCES "TRANSAKSJONTYPE" ("TRANSAKSJONSKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SAK
--------------------------------------------------------

--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_ORGPERS_FK" FOREIGN KEY ("BRUKERID_ANSVARLIG")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_ORGSTED_FK" FOREIGN KEY ("AETATENHET_ANSVARLIG")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_ORGSTED_FK2" FOREIGN KEY ("AETATENHET_ARKIV")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_SAKSTAT_FK" FOREIGN KEY ("SAKSTATUSKODE")
--	  REFERENCES "SAKSTATUS" ("SAKSTATUSKODE") ENABLE;
--  ALTER TABLE "SAK" ADD CONSTRAINT "SAK_SAKSTYP_FK" FOREIGN KEY ("SAKSKODE")
--	  REFERENCES "SAKSTYPE" ("SAKSKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SIM_UTBETALINGSGRUNNLAG
--------------------------------------------------------

--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_ART_FK" FOREIGN KEY ("ARTKODE")
--	  REFERENCES "ART" ("ARTKODE") ENABLE;
--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_BETMOT_FK" FOREIGN KEY ("EKSTERNENHET_ID_ALTMOTTAKER")
--	  REFERENCES "BETALINGMOTTAKER" ("BETALINGMOTTAKER_ID") ENABLE;
--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_MELDTYP_FK" FOREIGN KEY ("MELDINGKODE")
--	  REFERENCES "MELDINGTYPE" ("MELDINGKODE") ENABLE;
--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS_KILDE")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_POSTERTYP_FK" FOREIGN KEY ("POSTERINGTYPEKODE")
--	  REFERENCES "POSTERINGTYPE" ("POSTERINGTYPEKODE") ENABLE;
--  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_S_MKORT_FK" FOREIGN KEY ("SIM_MELDEKORT_ID")
--	  REFERENCES "SIM_MELDEKORT" ("SIM_MELDEKORT_ID") ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_TRANSTYP_FK" FOREIGN KEY ("TRANSAKSJONSKODE")
	  REFERENCES "TRANSAKSJONTYPE" ("TRANSAKSJONSKODE") ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
  ALTER TABLE "SIM_UTBETALINGSGRUNNLAG" ADD CONSTRAINT "SUTBETGR_VEDTAK_FK2" FOREIGN KEY ("VEDTAK_ID_FEILUTBET")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SPESIALUTBETALING
--------------------------------------------------------

--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_ORGPERS_FK" FOREIGN KEY ("BRUKER_ID_SAKSBEHANDLER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_ORGPERS_FK2" FOREIGN KEY ("BRUKER_ID_BESLUTTER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_POSTERTYP_FK" FOREIGN KEY ("POSTERINGTYPEKODE")
--	  REFERENCES "POSTERINGTYPE" ("POSTERINGTYPEKODE") ENABLE;
--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_VEDST_FK" FOREIGN KEY ("VEDTAKSTATUSKODE")
--	  REFERENCES "VEDTAKSTATUS" ("VEDTAKSTATUSKODE") ENABLE;
  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_BETMOT_FK" FOREIGN KEY ("EKSTERNENHET_ID_ALTMOTTAKER")
--	  REFERENCES "BETALINGMOTTAKER" ("BETALINGMOTTAKER_ID") ENABLE;
--  ALTER TABLE "SPESIALUTBETALING" ADD CONSTRAINT "SPESBET_L_RETTAKTF_FK" FOREIGN KEY ("RETTIGHETKODE", "AKTFASEKODE")
--	  REFERENCES "LOV_RETTIGHETTYPE_AKTFAS" ("RETTIGHETKODE", "AKTFASEKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table TRANSAKSJONTYPE
--------------------------------------------------------

--  ALTER TABLE "TRANSAKSJONTYPE" ADD CONSTRAINT "TRANSTYP_TRANSGR_FK" FOREIGN KEY ("TRANSGRUPPEKODE")
--	  REFERENCES "TRANSAKSJONGRUPPE" ("TRANSGRUPPEKODE") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table UTBETALINGSGRUNNLAG
--------------------------------------------------------

  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
--  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_MELDTYP_FK" FOREIGN KEY ("MELDINGKODE")
--	  REFERENCES "MELDINGTYPE" ("MELDINGKODE") ENABLE;
--  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_POSTERTYP_FK" FOREIGN KEY ("POSTERINGTYPEKODE")
--	  REFERENCES "POSTERINGTYPE" ("POSTERINGTYPEKODE") ENABLE;
  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_TRANSTYP_FK" FOREIGN KEY ("TRANSAKSJONSKODE")
	  REFERENCES "TRANSAKSJONTYPE" ("TRANSAKSJONSKODE") ENABLE;
--  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_BETMOT_FK" FOREIGN KEY ("EKSTERNENHET_ID_ALTMOTTAKER")
--	  REFERENCES "BETALINGMOTTAKER" ("BETALINGMOTTAKER_ID") ENABLE;
  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_MKORT_FK" FOREIGN KEY ("MELDEKORT_ID")
	  REFERENCES "MELDEKORT" ("MELDEKORT_ID") ENABLE;
  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--  ALTER TABLE "UTBETALINGSGRUNNLAG" ADD CONSTRAINT "UTBETGR_OBJTYP_FK" FOREIGN KEY ("TABELLNAVNALIAS_KILDE")
--	  REFERENCES "OBJEKTTYPE" ("TABELLNAVNALIAS") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VEDTAK
--------------------------------------------------------

  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_SAK_FK" FOREIGN KEY ("SAK_ID")
	  REFERENCES "SAK" ("SAK_ID") ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_SAK_FK2" FOREIGN KEY ("AAR", "LOPENRSAK")
	  REFERENCES "SAK" ("AAR", "LOPENRSAK") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_SF_OPP_FK1" FOREIGN KEY ("SF_OPPFOLGING_ID")
--	  REFERENCES "SF_OPPFOLGING" ("SF_OPPFOLGING_ID") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_AKTFAS_FK" FOREIGN KEY ("AKTFASEKODE")
--	  REFERENCES "AKTIVITETFASE" ("AKTFASEKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_AVSNLST_FK" FOREIGN KEY ("AVSNITTLISTEKODE_VALGT")
--	  REFERENCES "AVSNITTLISTE" ("AVSNITTLISTEKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_BREV_FK" FOREIGN KEY ("BREV_ID")
--	  REFERENCES "BREV" ("BREV_ID") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_KRAVTYP_FK" FOREIGN KEY ("RETTIGHETKODE", "VEDTAKTYPEKODE")
--	  REFERENCES "KRAVTYPE" ("RETTIGHETKODE", "VEDTAKTYPEKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_ORGPERS_FK" FOREIGN KEY ("BRUKERID_ANSVARLIG")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_ORGPERS_FK2" FOREIGN KEY ("BRUKERID_BESLUTTER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_ORGSTED_FK" FOREIGN KEY ("AETATENHET_BEHANDLER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_PERS_FK" FOREIGN KEY ("PERSON_ID")
	  REFERENCES "PERSON" ("PERSON_ID") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_UTFTYP_FK" FOREIGN KEY ("UTFALLKODE")
--	  REFERENCES "UTFALLTYPE" ("UTFALLKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_VEDSTAT_FK" FOREIGN KEY ("VEDTAKSTATUSKODE")
--	  REFERENCES "VEDTAKSTATUS" ("VEDTAKSTATUSKODE") ENABLE;
  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID_RELATERT")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_VEDTYP_FK" FOREIGN KEY ("VEDTAKTYPEKODE")
--	  REFERENCES "VEDTAKTYPE" ("VEDTAKTYPEKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_BREVVGRP_FK" FOREIGN KEY ("TEKSTVARIANTKODE")
--	  REFERENCES "BREV_TEKSTVARIANT" ("TEKSTVARIANTKODE") ENABLE;
--  ALTER TABLE "VEDTAK" ADD CONSTRAINT "VEDTAK_ORGPERS_FK3" FOREIGN KEY ("VALGT_BESLUTTER")
--	  REFERENCES "ORGUNITINSTANCE" ("USERNAME") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VEDTAKFAKTA
--------------------------------------------------------

  ALTER TABLE "VEDTAKFAKTA" ADD CONSTRAINT "VEDFAKT_VEDFTYP_FK" FOREIGN KEY ("VEDTAKFAKTAKODE")
	  REFERENCES "VEDTAKFAKTATYPE" ("VEDTAKFAKTAKODE") ENABLE;
  ALTER TABLE "VEDTAKFAKTA" ADD CONSTRAINT "VEDFAKT_VEDTAK_FK" FOREIGN KEY ("VEDTAK_ID")
	  REFERENCES "VEDTAK" ("VEDTAK_ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table VEDTAKFAKTATYPE
--------------------------------------------------------

--  ALTER TABLE "VEDTAKFAKTATYPE" ADD CONSTRAINT "VEDFAKTTYP_AVSN_FK" FOREIGN KEY ("AVSNITT_ID_LEDETEKST")
--	  REFERENCES "AVSNITT" ("AVSNITT_ID") ENABLE;
spool off
