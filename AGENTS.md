# AGENTS.md — tiltakspenger-arena

Dette repoet følger monorepo-konvensjonene i [`../AGENTS.md`](../AGENTS.md) og Kotlin/JVM-backendkonvensjonene i [`../AGENTS-backend.md`](../AGENTS-backend.md). Les disse først.

## Testing mot Arena-databasen (Oracle)

- Repository-tester kjører mot **ekte Oracle i testcontainers** (`gvenzl/oracle-free`), ikke H2.
- Tabellene bygges av flyway-filene i `src/test/resources/local-migrations/` — se `SakRepositoryTest` for oppsettet. Nye tabeller legges som nye `V<n>__*.sql`-filer der.
- I prod leser appen fra views (`arena_tilgang_ind.*`), men de er `SELECT tabell.*` med rettighetsfilter — kolonnene er identiske med `SIAMO`-tabellene. **I test lager vi tabeller med samme navn som viewene; ikke lag view-laget.**
- Trenger du kolonnedefinisjoner for nye tabeller, IKKE reverse-engineer fra spørringene alene. Kildene er allerede kartlagt (analyse i [issue #874](https://github.com/navikt/tiltakspenger-arena/issues/874)):
    - View-definisjonene våre (`v_ind_*`): [navikt/arena-db-datadeling](https://github.com/navikt/arena-db-datadeling) under `src/main/resources/db/migration/views/`.
    - Kolonnetyper for `SIAMO`-tabellene: AAP sin Oracle-DDL-eksport i [navikt/aap-arenaoppslag](https://github.com/navikt/aap-arenaoppslag/blob/main/app/src/test/resources/arena_aap_oracle_ddl_export.sql).
    - Referanse-testoppsett mot Oracle-container med testdata: [dp-proxy `ArenaInnsendtemeldekortTest`](https://github.com/navikt/dp-proxy/blob/main/proxy/src/test/kotlin/no/nav/dagpenger/proxy/feature/ArenaInnsendtemeldekortTest.kt).
    - Tabelldokumentasjon: [tilleggsstonader-docs/arena-database.md](https://github.com/navikt/tilleggsstonader-docs/blob/main/docs/Arena/arena-database.md).
- Stil for test-DDL: kolonnene spørringene bruker (ikke full Arena-bredde), `NOT NULL` kun på nøkkelkolonner, `PRIMARY KEY` på naturlige nøkler — se `src/test/resources/local-migrations/V1000__arena_skjema.sql`.
- Nullability-fasit for viewene (hvilke kolonner som er nullbare i Arena, og hvilke som faktisk er null for tiltakspenger-data i Q2) ligger i [`doc/arena-ddl/nullability_arena_tilgang_ind.md`](doc/arena-ddl/nullability_arena_tilgang_ind.md). Bruk den, ikke `datadeling_tiltakspenger.sql` (den har strippet constraints). Trenger du å verifisere noe nytt mot den faktiske databasen: se «Kjøre queries mot arena-viewet» i [README.md](README.md) — kun nåbar via VDI.

