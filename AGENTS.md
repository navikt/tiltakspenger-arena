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

## Route-tester (målbilde)

Route-testene (`src/test/kotlin/.../routes/*RouteTest.kt`) er kontrakten mot konsumentene våre. Målbildet under skal holde dem konsistente over tid — hold nye tester og hjelpere i samme stil.

- **Full-vertikal:** HTTP inn → prod ktor-pipeline (`tiltakApi(...)`) → ekte Oracle-testcontainer → JSON ut. Ingen mocking av tjeneste-/repo-laget.
- **Assert på rå JSON, aldri via respons-DTO:** bruk `skalHaOkMedJson(...)` (sjekker `200 OK` + `shouldEqualJson`). Da brekker testen når DTO-en refaktoreres — poenget, siden JSON-en er kontrakten. Håndhevet av `RouteTestKontraktKonsistTest` (ingen `deserialize*`/`body()` i route-tester).
- **Send rå tekst inn** (`vedtakRequestBody("<fnr>")`), ikke serialiser en request-DTO.
- **Forventet JSON bygges av `Forventet*Json`-hjelpere med gode defaults** for standardtilfellet. Én forventet-JSON-type per fil. En test overstyrer kun identiteten (fnr/id/saksnummer) og det den faktisk verifiserer.
- **Del mest mulig testdata** mellom testene — kun identitet og det som testes skal være unikt.
- **Bygg testdata fluent:** `ArenaTestdata.person(...).medSak(...).medTiltakspengevedtak(...)` / `.medMeldekort(...).medDag(...)`. Flate `leggTil*`-hjelpere finnes for kant-tilfeller.
- **Auth-avvisning (401) dekkes ett sted** (`TiltakspengerRoutesAuthTest`), ikke i hver route-test.
- **Unike id-serier per test-fil:** `MELDEKORTPERIODE` har nøkkel `(aar, periodekode)` som deles på tvers av *alle* tester (repository, service og route), så `periodekode` må være globalt unik per år (ellers PK-kollisjon mellom klasser).

Id-/periodekode-kart (år 2023 der ikke annet er nevnt):

| Serie | Test | periodekode |
| --- | --- | --- |
| `1xx` | repository (vedtak/sak) | – |
| `2xx` | `MeldekortRepositoryTest` | `51`–`53` |
| `3xx` | `UtbetalingshistorikkServiceTest` | `61`–`65` |
| `90xx` | `VedtaksperioderRouteTest` | – |
| `91xx` | `VedtakRouteTest` | – |
| `92xx` | `RettighetsperioderRouteTest` | – |
| `93xx` | `MeldekortRouteTest` | `71` |
| `94xx` | `UtbetalingshistorikkRouteTest` | – |
| `95xx` | `UtbetalingshistorikkDetaljerRouteTest` | – |

