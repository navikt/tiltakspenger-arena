-- Tabellene bak /meldekort og /utbetalingshistorikk (jf. issue #874).
--
-- Kolonnetypene er hentet fra AAP sin Oracle-DDL-eksport av SIAMO-tabellene:
-- https://github.com/navikt/aap-arenaoppslag/blob/main/app/src/test/resources/arena_aap_oracle_ddl_export.sql
-- avgrenset til kolonnene spørringene våre faktisk bruker (samme stil som V1000__sak.sql).
--
-- Viewene vi leser fra i prod (arena_tilgang_ind.*) er definert som SELECT tabell.* med et
-- rettighetsfilter, se https://github.com/navikt/arena-db-datadeling - kolonnene er altså
-- identiske med de underliggende tabellene, så i test holder det å lage tabellene direkte.
--
-- UTBETALINGSGRUNNLAG og kodeverkstabellene mangler i AAP-eksporten; de er utledet fra
-- spørringene våre og dp-proxy sitt testoppsett:
-- https://github.com/navikt/dp-proxy/blob/main/proxy/src/test/kotlin/no/nav/dagpenger/proxy/feature/ArenaInnsendtemeldekortTest.kt

CREATE TABLE MELDEKORT
(
    MELDEKORT_ID                NUMBER      NOT NULL,
    PERSON_ID                   NUMBER      NOT NULL,
    AAR                         NUMBER(4)   NOT NULL,
    PERIODEKODE                 VARCHAR2(2) NOT NULL,
    BEREGNINGSTATUSKODE         VARCHAR2(5) NOT NULL,
    MKSKORTKODE                 VARCHAR2(2) NOT NULL,
    MELDEGRUPPEKODE             VARCHAR2(5),
    MELDEKORTKODE               VARCHAR2(5),
    DATO_INNKOMMET              DATE,
    STATUS_ARBEIDET             VARCHAR2(1),
    STATUS_KURS                 VARCHAR2(1),
    STATUS_FERIE                VARCHAR2(1),
    STATUS_SYK                  VARCHAR2(1),
    STATUS_ANNETFRAVAER         VARCHAR2(1),
    STATUS_FORTSATT_ARBEIDSOKER VARCHAR2(1),
    REG_DATO                    DATE,
    MOD_DATO                    DATE,
    PRIMARY KEY (MELDEKORT_ID)
);

CREATE TABLE MELDEKORTPERIODE
(
    AAR         NUMBER(4)   NOT NULL,
    PERIODEKODE VARCHAR2(2) NOT NULL,
    UKENR_UKE1  NUMBER(2)   NOT NULL,
    UKENR_UKE2  NUMBER(2)   NOT NULL,
    DATO_FRA    DATE        NOT NULL,
    DATO_TIL    DATE        NOT NULL,
    PRIMARY KEY (AAR, PERIODEKODE)
);

CREATE TABLE MELDEKORTDAG
(
    MELDEKORT_ID        NUMBER    NOT NULL,
    UKENR               NUMBER(2) NOT NULL,
    DAGNR               NUMBER(1) NOT NULL,
    STATUS_ARBEIDSDAG   VARCHAR2(1),
    STATUS_FERIE        VARCHAR2(1),
    STATUS_KURS         VARCHAR2(1),
    STATUS_SYK          VARCHAR2(1),
    STATUS_ANNETFRAVAER VARCHAR2(1),
    TIMER_ARBEIDET      NUMBER(3, 1),
    REG_USER            VARCHAR2(8),
    REG_DATO            DATE,
    PRIMARY KEY (MELDEKORT_ID, UKENR, DAGNR)
);

CREATE TABLE MELDELOGG
(
    MELDEKORT_ID     NUMBER NOT NULL,
    HENDELSETYPEKODE VARCHAR2(5),
    HENDELSEDATO     DATE
);

CREATE TABLE POSTERING
(
    MELDEKORT_ID     NUMBER,
    PERSON_ID        NUMBER      NOT NULL,
    VEDTAK_ID        NUMBER      NOT NULL,
    TRANSAKSJONSKODE VARCHAR2(5) NOT NULL,
    DATO_POSTERT     DATE,
    POSTERINGSATS    NUMBER(8, 2),
    BELOP            NUMBER(12, 2),
    DATO_PERIODE_FRA DATE,
    DATO_PERIODE_TIL DATE
);

CREATE TABLE UTBETALINGSGRUNNLAG
(
    MELDEKORT_ID     NUMBER,
    PERSON_ID        NUMBER      NOT NULL,
    VEDTAK_ID        NUMBER      NOT NULL,
    TRANSAKSJONSKODE VARCHAR2(5) NOT NULL,
    MOD_DATO         DATE,
    POSTERINGSATS    NUMBER(8, 2),
    BELOP            NUMBER(12, 2),
    DATO_PERIODE_FRA DATE,
    DATO_PERIODE_TIL DATE
);

CREATE TABLE BEREGNINGSLOGG
(
    OBJEKT_ID       NUMBER(10)   NOT NULL,
    PERSON_ID       NUMBER       NOT NULL,
    VEDTAK_ID       NUMBER       NOT NULL,
    TABELLNAVNALIAS VARCHAR2(10) NOT NULL,
    DATO_FRA        DATE,
    DATO_TIL        DATE,
    REG_DATO        DATE
);

CREATE TABLE ANMERKNING
(
    ANMERKNING_ID   NUMBER       NOT NULL,
    OBJEKT_ID       NUMBER       NOT NULL,
    VEDTAK_ID       NUMBER,
    TABELLNAVNALIAS VARCHAR2(10) NOT NULL,
    ANMERKNINGKODE  VARCHAR2(5)  NOT NULL,
    VERDI           NUMBER(5),
    REG_DATO        DATE,
    PRIMARY KEY (ANMERKNING_ID)
);

CREATE TABLE ANMERKNINGTYPE
(
    ANMERKNINGKODE VARCHAR2(5) NOT NULL,
    BESKRIVELSE    VARCHAR2(255),
    PRIMARY KEY (ANMERKNINGKODE)
);

CREATE TABLE RETTIGHETTYPE
(
    RETTIGHETKODE VARCHAR2(10) NOT NULL,
    RETTIGHETNAVN VARCHAR2(60),
    PRIMARY KEY (RETTIGHETKODE)
);

CREATE TABLE BEREGNINGSTATUS
(
    BEREGNINGSTATUSKODE VARCHAR2(5) NOT NULL,
    BEREGNINGSTATUSNAVN VARCHAR2(50),
    PRIMARY KEY (BEREGNINGSTATUSKODE)
);

CREATE TABLE MKSKORTTYPE
(
    MKSKORTKODE     VARCHAR2(2) NOT NULL,
    MKSKORTTYPENAVN VARCHAR2(50),
    PRIMARY KEY (MKSKORTKODE)
);

CREATE TABLE MELDEGRUPPETYPE
(
    MELDEGRUPPEKODE VARCHAR2(5) NOT NULL,
    MELDEGRUPPENAVN VARCHAR2(50),
    PRIMARY KEY (MELDEGRUPPEKODE)
);

CREATE TABLE TRANSAKSJONTYPE
(
    TRANSAKSJONSKODE     VARCHAR2(5) NOT NULL,
    TRANSAKSJONSTYPENAVN VARCHAR2(50),
    PRIMARY KEY (TRANSAKSJONSKODE)
);
