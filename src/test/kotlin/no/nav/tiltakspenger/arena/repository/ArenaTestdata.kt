package no.nav.tiltakspenger.arena.repository

import kotliquery.queryOf
import kotliquery.sessionOf
import no.nav.tiltakspenger.arena.db.Datasource
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Insert-hjelpere for testene mot Oracle-testcontaineren.
 * Kun kolonnene spørringene bruker har parametre; øvrige NOT NULL-kolonner fylles med faste defaults.
 * Testene eier sine egne data - bruk unike id-er/fnr per test siden databasen deles.
 */
object ArenaTestdata {

    /**
     * Fluent inngang: seeder en person og lar deg kjede på sak/vedtak uten å gjenta id-ene.
     * ```
     * ArenaTestdata.person(personId = 900, fnr = "90000000000")
     *     .medSak(sakId = 9001)
     *     .medTiltakspengevedtak(vedtakId = 90011)
     *     .medBarnetilleggvedtak(vedtakId = 90012, antallBarn = "0.96...")
     * ```
     * De flate `leggTil...`-funksjonene finnes fortsatt for enkelttilfeller.
     */
    fun person(personId: Long, fnr: String): PersonBuilder {
        leggTilPerson(personId = personId, fnr = fnr)
        return PersonBuilder(personId)
    }

    class PersonBuilder(private val personId: Long) {
        fun medSak(
            sakId: Long,
            år: Int = 2023,
            lopenrSak: Long = sakId % 1_000_000,
            sakstatuskode: String = "AKTIV",
            regDato: LocalDate = LocalDate.of(2023, 1, 1),
        ): SakBuilder {
            ArenaTestdata.leggTilSak(sakId, personId, år, lopenrSak, sakstatuskode, regDato)
            return SakBuilder(personId, sakId)
        }

        /** Standard meldekort for personen; kjed på [MeldekortBuilder.medDag] for dager. */
        fun medMeldekort(
            meldekortId: Long,
            periodekode: String,
            datoFra: LocalDate,
            datoTil: LocalDate,
            år: Int = 2023,
        ): MeldekortBuilder {
            ArenaTestdata.leggTilStandardMeldekort(meldekortId, personId, periodekode, datoFra, datoTil, år)
            return MeldekortBuilder(meldekortId)
        }
    }

    class MeldekortBuilder(private val meldekortId: Long) {
        fun medDag(
            ukenr: Int,
            dagnr: Int,
            statusArbeidsdag: String = "N",
            statusFerie: String? = "N",
            statusKurs: String = "J",
            statusSyk: String = "N",
            statusAnnetFravaer: String = "N",
            timerArbeidet: Double = 0.0,
            regUser: String = "GRENSESN",
            regDato: LocalDate = LocalDate.of(2023, 1, 16),
        ): MeldekortBuilder {
            ArenaTestdata.leggTilMeldekortdag(
                meldekortId, ukenr, dagnr, statusArbeidsdag, statusFerie, statusKurs,
                statusSyk, statusAnnetFravaer, timerArbeidet, regUser, regDato,
            )
            return this
        }
    }

    class SakBuilder(private val personId: Long, private val sakId: Long) {
        /** Standard tiltakspengevedtak med de vanlige vedtakfaktaene (DAGS/DAGUTBTILT/KODETILTAK). */
        fun medTiltakspengevedtak(
            vedtakId: Long,
            fraDato: LocalDate = LocalDate.of(2023, 1, 1),
            tilDato: LocalDate? = LocalDate.of(2023, 3, 31),
            dags: String = "285",
            dagutbtilt: String = "10",
            kodetiltak: String = "133924438",
        ): SakBuilder {
            ArenaTestdata.leggTilTiltakspengevedtakMedFakta(vedtakId, sakId, personId, fraDato, tilDato, dags, dagutbtilt, kodetiltak)
            return this
        }

        /** Barnetilleggvedtak (BTIL) med antall barn og dagsats. */
        fun medBarnetilleggvedtak(
            vedtakId: Long,
            antallBarn: String,
            dagsats: String = "53",
            fraDato: LocalDate = LocalDate.of(2023, 1, 1),
            tilDato: LocalDate? = LocalDate.of(2023, 3, 31),
        ): SakBuilder {
            ArenaTestdata.leggTilVedtak(vedtakId = vedtakId, sakId = sakId, personId = personId, rettighetkode = "BTIL", fraDato = fraDato, tilDato = tilDato)
            ArenaTestdata.leggTilVedtakfakta(vedtakId = vedtakId, kode = "BARNMSTON", verdi = antallBarn)
            ArenaTestdata.leggTilVedtakfakta(vedtakId = vedtakId, kode = "DAGS", verdi = dagsats)
            return this
        }
    }

    /**
     * Kodeverk-rader spørringene joiner mot.
     * Kjøres én gang fra [no.nav.tiltakspenger.arena.db.OracleTestbase].
     */
    fun seedKodeverk() {
        exec("INSERT INTO BEREGNINGSTATUS (BEREGNINGSTATUSKODE, BEREGNINGSTATUSNAVN) VALUES ('FERDI', 'Ferdig beregnet')")
        exec("INSERT INTO BEREGNINGSTATUS (BEREGNINGSTATUSKODE, BEREGNINGSTATUSNAVN) VALUES ('KLAR', 'Klar til beregning')")
        exec("INSERT INTO BEREGNINGSTATUS (BEREGNINGSTATUSKODE, BEREGNINGSTATUSNAVN) VALUES ('VENTE', 'Venter på beregning')")
        exec("INSERT INTO BEREGNINGSTATUS (BEREGNINGSTATUSKODE, BEREGNINGSTATUSNAVN) VALUES ('FEIL', 'Feil i beregning')")
        exec("INSERT INTO BEREGNINGSTATUS (BEREGNINGSTATUSKODE, BEREGNINGSTATUSNAVN) VALUES ('OPPRE', 'Opprettet')")
        exec("INSERT INTO MKSKORTTYPE (MKSKORTKODE, MKSKORTTYPENAVN) VALUES ('05', 'Elektronisk meldekort')")
        exec("INSERT INTO MKSKORTTYPE (MKSKORTKODE, MKSKORTTYPENAVN) VALUES ('10', 'Manuelt - Korrigering')")
        exec("INSERT INTO MELDEGRUPPETYPE (MELDEGRUPPEKODE, MELDEGRUPPENAVN) VALUES ('INDIV', 'Individstønad')")
        exec("INSERT INTO TRANSAKSJONTYPE (TRANSAKSJONSKODE, TRANSAKSJONSTYPENAVN) VALUES ('UTBET', 'Utbetaling')")
        exec("INSERT INTO RETTIGHETTYPE (RETTIGHETKODE, RETTIGHETNAVN) VALUES ('BASI', 'Tiltakspenger')")
        exec("INSERT INTO RETTIGHETTYPE (RETTIGHETKODE, RETTIGHETNAVN) VALUES ('BTIL', 'Barnetillegg')")
        exec("INSERT INTO ANMERKNINGTYPE (ANMERKNINGKODE, BESKRIVELSE) VALUES ('X123', 'Meldekort avvist, mangler &1 dager')")
    }

    fun leggTilPerson(personId: Long, fnr: String) {
        exec(
            "INSERT INTO PERSON (PERSON_ID, FODSELSNR) VALUES (:personId, :fnr)",
            mapOf("personId" to personId, "fnr" to fnr),
        )
    }

    fun leggTilSak(
        sakId: Long,
        personId: Long,
        år: Int = 2023,
        lopenrSak: Long = sakId % 1_000_000,
        sakstatuskode: String = "AKTIV",
        regDato: LocalDate = LocalDate.of(2023, 1, 1),
    ) {
        exec(
            """
            INSERT INTO SAK (SAK_ID, SAKSKODE, REG_DATO, TABELLNAVNALIAS, OBJEKT_ID, AAR, LOPENRSAK, SAKSTATUSKODE, ER_UTLAND)
            VALUES (:sakId, 'INDIV', :regDato, 'PERS', :personId, :aar, :lopenrSak, :sakstatuskode, 'N')
            """.trimIndent(),
            mapOf(
                "sakId" to sakId,
                "regDato" to regDato,
                "personId" to personId,
                "aar" to år,
                "lopenrSak" to lopenrSak,
                "sakstatuskode" to sakstatuskode,
            ),
        )
    }

    fun leggTilVedtak(
        vedtakId: Long,
        sakId: Long,
        personId: Long,
        rettighetkode: String = "BASI",
        vedtaktypekode: String = "O",
        vedtakstatuskode: String = "IVERK",
        utfallkode: String = "JA",
        fraDato: LocalDate = LocalDate.of(2023, 1, 1),
        tilDato: LocalDate? = LocalDate.of(2023, 3, 31),
        datoMottatt: LocalDate = LocalDate.of(2022, 12, 1),
        regDato: LocalDate = LocalDate.of(2022, 12, 1),
        lopenrVedtak: Long = 1,
    ) {
        // Oracle godtar ikke en utypet null-binding (ORA-17004), så åpen sluttdato settes som NULL-literal i SQL-en i stedet for et bind-param.
        val tilDatoBind = if (tilDato == null) "NULL" else ":tilDato"
        exec(
            """
            INSERT INTO VEDTAK (VEDTAK_ID, SAK_ID, VEDTAKSTATUSKODE, VEDTAKTYPEKODE, REG_DATO, UTFALLKODE,
                                AETATENHET_BEHANDLER, AAR, LOPENRSAK, LOPENRVEDTAK, RETTIGHETKODE, AKTFASEKODE,
                                DATO_MOTTATT, PERSON_ID, FRA_DATO, TIL_DATO, STATUS_SOSIALDATA, ER_UTLAND)
            VALUES (:vedtakId, :sakId, :vedtakstatuskode, :vedtaktypekode, :regDato, :utfallkode,
                    '0219', 2023, :sakId, :lopenrVedtak, :rettighetkode, 'UGJEN',
                    :datoMottatt, :personId, :fraDato, $tilDatoBind, 'N', 'N')
            """.trimIndent(),
            mapOf(
                "vedtakId" to vedtakId,
                "sakId" to sakId,
                "vedtakstatuskode" to vedtakstatuskode,
                "vedtaktypekode" to vedtaktypekode,
                "regDato" to regDato,
                "utfallkode" to utfallkode,
                "lopenrVedtak" to lopenrVedtak,
                "rettighetkode" to rettighetkode,
                "datoMottatt" to datoMottatt,
                "personId" to personId,
                "fraDato" to fraDato,
                "tilDato" to tilDato,
            ),
        )
    }

    fun leggTilVedtakfakta(vedtakId: Long, kode: String, verdi: String?) {
        exec(
            "INSERT INTO VEDTAKFAKTA (VEDTAK_ID, VEDTAKFAKTAKODE, VEDTAKVERDI) VALUES (:vedtakId, :kode, :verdi)",
            mapOf("vedtakId" to vedtakId, "kode" to kode, "verdi" to verdi),
        )
    }

    /**
     * Standard tiltakspengevedtak (BASI, [leggTilVedtak]s defaults) med de vanlige vedtakfaktaene.
     * Gir dagsats 285, 10 dager og relatert tiltak 133924438 - samsvarer med defaultene i `routes/forventetVedtaksperiodeJson(...)`.
     */
    fun leggTilTiltakspengevedtakMedFakta(
        vedtakId: Long,
        sakId: Long,
        personId: Long,
        fraDato: LocalDate = LocalDate.of(2023, 1, 1),
        tilDato: LocalDate? = LocalDate.of(2023, 3, 31),
        dags: String = "285",
        dagutbtilt: String = "10",
        kodetiltak: String = "133924438",
    ) {
        leggTilVedtak(vedtakId = vedtakId, sakId = sakId, personId = personId, fraDato = fraDato, tilDato = tilDato)
        leggTilVedtakfakta(vedtakId = vedtakId, kode = "DAGS", verdi = dags)
        leggTilVedtakfakta(vedtakId = vedtakId, kode = "DAGUTBTILT", verdi = dagutbtilt)
        leggTilVedtakfakta(vedtakId = vedtakId, kode = "KODETILTAK", verdi = kodetiltak)
    }

    fun leggTilMeldekortperiode(
        år: Int,
        periodekode: String,
        datoFra: LocalDate,
        datoTil: LocalDate,
        ukenrUke1: Int = 1,
        ukenrUke2: Int = 2,
    ) {
        exec(
            """
            INSERT INTO MELDEKORTPERIODE (AAR, PERIODEKODE, UKENR_UKE1, UKENR_UKE2, DATO_FRA, DATO_TIL)
            VALUES (:aar, :periodekode, :ukenrUke1, :ukenrUke2, :datoFra, :datoTil)
            """.trimIndent(),
            mapOf(
                "aar" to år,
                "periodekode" to periodekode,
                "ukenrUke1" to ukenrUke1,
                "ukenrUke2" to ukenrUke2,
                "datoFra" to datoFra,
                "datoTil" to datoTil,
            ),
        )
    }

    fun leggTilMeldekort(
        meldekortId: Long,
        personId: Long,
        år: Int,
        periodekode: String,
        beregningstatuskode: String = "FERDI",
        mkskortkode: String = "05",
        meldegruppekode: String = "INDIV",
        meldekortkode: String = "MK",
        datoInnkommet: LocalDate? = LocalDate.of(2023, 1, 16),
        statusArbeidet: String = "N",
        statusKurs: String = "J",
        statusFerie: String = "N",
        statusSyk: String = "N",
        statusAnnetFravaer: String = "N",
        statusFortsattArbeidsoker: String = "J",
        regDato: LocalDateTime = LocalDateTime.of(2023, 1, 16, 12, 0),
        modDato: LocalDateTime = LocalDateTime.of(2023, 1, 17, 12, 0),
    ) {
        exec(
            """
            INSERT INTO MELDEKORT (MELDEKORT_ID, PERSON_ID, AAR, PERIODEKODE, BEREGNINGSTATUSKODE, MKSKORTKODE,
                                   MELDEGRUPPEKODE, MELDEKORTKODE, DATO_INNKOMMET, STATUS_ARBEIDET, STATUS_KURS,
                                   STATUS_FERIE, STATUS_SYK, STATUS_ANNETFRAVAER, STATUS_FORTSATT_ARBEIDSOKER,
                                   REG_DATO, MOD_DATO)
            VALUES (:meldekortId, :personId, :aar, :periodekode, :beregningstatuskode, :mkskortkode,
                    :meldegruppekode, :meldekortkode, :datoInnkommet, :statusArbeidet, :statusKurs,
                    :statusFerie, :statusSyk, :statusAnnetFravaer, :statusFortsattArbeidsoker,
                    :regDato, :modDato)
            """.trimIndent(),
            mapOf(
                "meldekortId" to meldekortId,
                "personId" to personId,
                "aar" to år,
                "periodekode" to periodekode,
                "beregningstatuskode" to beregningstatuskode,
                "mkskortkode" to mkskortkode,
                "meldegruppekode" to meldegruppekode,
                "meldekortkode" to meldekortkode,
                "datoInnkommet" to datoInnkommet,
                "statusArbeidet" to statusArbeidet,
                "statusKurs" to statusKurs,
                "statusFerie" to statusFerie,
                "statusSyk" to statusSyk,
                "statusAnnetFravaer" to statusAnnetFravaer,
                "statusFortsattArbeidsoker" to statusFortsattArbeidsoker,
                "regDato" to regDato,
                "modDato" to modDato,
            ),
        )
    }

    /**
     * Standard meldekort med tilhørende periode og meldelogg-treff (så statusDato blir satt).
     * Dager legges til separat.
     */
    fun leggTilStandardMeldekort(
        meldekortId: Long,
        personId: Long,
        periodekode: String,
        datoFra: LocalDate,
        datoTil: LocalDate,
        år: Int = 2023,
    ) {
        leggTilMeldekortperiode(år = år, periodekode = periodekode, datoFra = datoFra, datoTil = datoTil)
        leggTilMeldekort(meldekortId = meldekortId, personId = personId, år = år, periodekode = periodekode)
        leggTilMeldelogg(meldekortId = meldekortId)
    }

    fun leggTilMeldelogg(meldekortId: Long, hendelsetypekode: String = "FERDI", hendelsedato: LocalDate = LocalDate.of(2023, 1, 17)) {
        exec(
            "INSERT INTO MELDELOGG (MELDEKORT_ID, HENDELSETYPEKODE, HENDELSEDATO) VALUES (:meldekortId, :hendelsetypekode, :hendelsedato)",
            mapOf("meldekortId" to meldekortId, "hendelsetypekode" to hendelsetypekode, "hendelsedato" to hendelsedato),
        )
    }

    fun leggTilMeldekortdag(
        meldekortId: Long,
        ukenr: Int,
        dagnr: Int,
        statusArbeidsdag: String = "N",
        statusFerie: String? = "N",
        statusKurs: String = "J",
        statusSyk: String = "N",
        statusAnnetFravaer: String = "N",
        timerArbeidet: Double = 0.0,
        regUser: String = "GRENSESN",
        regDato: LocalDate = LocalDate.of(2023, 1, 16),
    ) {
        exec(
            """
            INSERT INTO MELDEKORTDAG (MELDEKORT_ID, UKENR, DAGNR, STATUS_ARBEIDSDAG, STATUS_FERIE, STATUS_KURS,
                                      STATUS_SYK, STATUS_ANNETFRAVAER, TIMER_ARBEIDET, REG_USER, REG_DATO)
            VALUES (:meldekortId, :ukenr, :dagnr, :statusArbeidsdag, :statusFerie, :statusKurs,
                    :statusSyk, :statusAnnetFravaer, :timerArbeidet, :regUser, :regDato)
            """.trimIndent(),
            mapOf(
                "meldekortId" to meldekortId,
                "ukenr" to ukenr,
                "dagnr" to dagnr,
                "statusArbeidsdag" to statusArbeidsdag,
                "statusFerie" to statusFerie,
                "statusKurs" to statusKurs,
                "statusSyk" to statusSyk,
                "statusAnnetFravaer" to statusAnnetFravaer,
                "timerArbeidet" to timerArbeidet,
                "regUser" to regUser,
                "regDato" to regDato,
            ),
        )
    }

    fun leggTilPostering(
        personId: Long,
        vedtakId: Long,
        meldekortId: Long?,
        belop: Double = 2850.0,
        posteringsats: Double = 285.0,
        datoPostert: LocalDate = LocalDate.of(2023, 1, 20),
        datoPeriodeFra: LocalDate = LocalDate.of(2023, 1, 2),
        datoPeriodeTil: LocalDate = LocalDate.of(2023, 1, 15),
    ) {
        exec(
            """
            INSERT INTO POSTERING (MELDEKORT_ID, PERSON_ID, VEDTAK_ID, TRANSAKSJONSKODE, DATO_POSTERT,
                                   POSTERINGSATS, BELOP, DATO_PERIODE_FRA, DATO_PERIODE_TIL)
            VALUES (:meldekortId, :personId, :vedtakId, 'UTBET', :datoPostert,
                    :posteringsats, :belop, :datoPeriodeFra, :datoPeriodeTil)
            """.trimIndent(),
            mapOf(
                "meldekortId" to meldekortId,
                "personId" to personId,
                "vedtakId" to vedtakId,
                "datoPostert" to datoPostert,
                "posteringsats" to posteringsats,
                "belop" to belop,
                "datoPeriodeFra" to datoPeriodeFra,
                "datoPeriodeTil" to datoPeriodeTil,
            ),
        )
    }

    fun leggTilUtbetalingsgrunnlag(
        personId: Long,
        vedtakId: Long,
        meldekortId: Long?,
        belop: Double = 1425.0,
        posteringsats: Double = 285.0,
        modDato: LocalDate = LocalDate.of(2023, 1, 18),
        datoPeriodeFra: LocalDate = LocalDate.of(2023, 1, 2),
        datoPeriodeTil: LocalDate = LocalDate.of(2023, 1, 15),
    ) {
        exec(
            """
            INSERT INTO UTBETALINGSGRUNNLAG (MELDEKORT_ID, PERSON_ID, VEDTAK_ID, TRANSAKSJONSKODE, MOD_DATO,
                                             POSTERINGSATS, BELOP, DATO_PERIODE_FRA, DATO_PERIODE_TIL)
            VALUES (:meldekortId, :personId, :vedtakId, 'UTBET', :modDato,
                    :posteringsats, :belop, :datoPeriodeFra, :datoPeriodeTil)
            """.trimIndent(),
            mapOf(
                "meldekortId" to meldekortId,
                "personId" to personId,
                "vedtakId" to vedtakId,
                "modDato" to modDato,
                "posteringsats" to posteringsats,
                "belop" to belop,
                "datoPeriodeFra" to datoPeriodeFra,
                "datoPeriodeTil" to datoPeriodeTil,
            ),
        )
    }

    fun leggTilBeregningslogg(
        objektId: Long,
        personId: Long,
        vedtakId: Long,
        datoFra: LocalDate = LocalDate.of(2023, 1, 2),
        datoTil: LocalDate = LocalDate.of(2023, 1, 15),
        regDato: LocalDate = LocalDate.of(2023, 1, 18),
    ) {
        exec(
            """
            INSERT INTO BEREGNINGSLOGG (OBJEKT_ID, PERSON_ID, VEDTAK_ID, TABELLNAVNALIAS, DATO_FRA, DATO_TIL, REG_DATO)
            VALUES (:objektId, :personId, :vedtakId, 'MKORT', :datoFra, :datoTil, :regDato)
            """.trimIndent(),
            mapOf(
                "objektId" to objektId,
                "personId" to personId,
                "vedtakId" to vedtakId,
                "datoFra" to datoFra,
                "datoTil" to datoTil,
                "regDato" to regDato,
            ),
        )
    }

    fun leggTilAnmerkning(
        anmerkningId: Long,
        objektId: Long,
        vedtakId: Long?,
        anmerkningkode: String = "X123",
        verdi: Int? = 5,
        regDato: LocalDate = LocalDate.of(2023, 1, 18),
    ) {
        exec(
            """
            INSERT INTO ANMERKNING (ANMERKNING_ID, OBJEKT_ID, VEDTAK_ID, TABELLNAVNALIAS, ANMERKNINGKODE, VERDI, REG_DATO)
            VALUES (:anmerkningId, :objektId, :vedtakId, 'MKORT', :anmerkningkode, :verdi, :regDato)
            """.trimIndent(),
            mapOf(
                "anmerkningId" to anmerkningId,
                "objektId" to objektId,
                "vedtakId" to vedtakId,
                "anmerkningkode" to anmerkningkode,
                "verdi" to verdi,
                "regDato" to regDato,
            ),
        )
    }

    private fun exec(sql: String, params: Map<String, Any?> = emptyMap()) {
        sessionOf(Datasource.hikariDataSource).use { session ->
            session.run(queryOf(sql, params).asUpdate)
        }
    }
}
