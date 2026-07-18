tiltakspenger-arena
================

Håndterer Arena-koblinger for tiltakspenger

En del av satsningen ["Flere i arbeid – P4"](https://memu.no/artikler/stor-satsing-skal-fornye-navs-utdaterte-it-losninger-og-digitale-verktoy/)

# Komme i gang
## Forutsetninger
- [JDK](https://jdk.java.net/)
- [Kotlin](https://kotlinlang.org/)
- [Gradle](https://gradle.org/) brukes som byggeverktøy og er inkludert i oppsettet

For hvilke versjoner som brukes, [se byggefilen](build.gradle.kts)

## Bygging og denslags
For å bygge artifaktene:

```sh
./gradlew build
```

---

## Kjøre opp appen lokalt

For lokal kjøring ligger det et docker-compose oppsett i `./docker-compose` som mocker ut Kafka og auth-server.
Gå inn i den folderen og kjør `docker-compose up -d --build`.

**Nødvendig ved førstegangsoppsett --** Legg inn følgende innslag i /etc/hosts:

```0.0.0.0 kafka```


# Testing mot Arena-databasen

Repository-testene kjører mot en **ekte Oracle-database i testcontainers** (`gvenzl/oracle-free`),
med tabellene bygget opp av flyway-migrasjoner i [`src/test/resources/local-migrations`](src/test/resources/local-migrations)
(se `SakRepositoryTest` for oppsettet). I prod leser appen fra views (`arena_tilgang_ind.*`), men
viewene er definert som `SELECT tabell.*` med et rettighetsfilter — kolonnene er identiske med de
underliggende `SIAMO`-tabellene, så i test lager vi bare tabeller med samme navn.

Kilder for tabell- og view-definisjoner når testoppsettet skal utvides:

- [navikt/arena-db-datadeling](https://github.com/navikt/arena-db-datadeling) — Arena-teamets
  flyway-repo for datadelings-viewene. Våre views heter `v_ind_*` (individstønad) og viser nøyaktig
  hvilke kolonner og rettighetsfiltre vi får.
- [navikt/aap-arenaoppslag](https://github.com/navikt/aap-arenaoppslag) — har en ekte Oracle-DDL-eksport
  av `SIAMO`-tabellene med fulle kolonnetyper i
  [`arena_aap_oracle_ddl_export.sql`](https://github.com/navikt/aap-arenaoppslag/blob/main/app/src/test/resources/arena_aap_oracle_ddl_export.sql).
  Beste kilde for kolonnetyper.
- [navikt/dp-proxy](https://github.com/navikt/dp-proxy) — dagpenger tester også mot Oracle-testcontainer,
  med minimale tabell-DDL-er og realistiske testdata for meldekort/posteringer i
  [`ArenaInnsendtemeldekortTest`](https://github.com/navikt/dp-proxy/blob/main/proxy/src/test/kotlin/no/nav/dagpenger/proxy/feature/ArenaInnsendtemeldekortTest.kt).
- [navikt/tilleggsstonader-arena](https://github.com/navikt/tilleggsstonader-arena) og
  [arena-database.md](https://github.com/navikt/tilleggsstonader-docs/blob/main/docs/Arena/arena-database.md) —
  H2-basert variant og god dokumentasjon av tabellene; tipser bl.a. om at Q2-brukeren ser
  tabellstrukturen under viewene, så DDL-er kan genereres derfra (se avsnittet under om databasetilgang).

## Utvide test-skjemaet med en ny tabell

1. Finn kolonnene den nye spørringen bruker (ikke hele Arena-tabellen).
2. Hent kolonnetypene fra AAP sin DDL-eksport (lenke over). Mangler tabellen der, generer DDL fra
   Q2 via VDI (se «Kjøre queries mot arena-viewet» under) eller sjekk view-definisjonen i
   `arena-db-datadeling`.
3. Legg tabellen i `src/test/resources/local-migrations/V1000__arena_skjema.sql`, i samme stil:
   `NOT NULL`/`PRIMARY KEY` kun på nøkler, `DATE` framfor `to_date(...)` for å unngå
   locale-forskjeller mellom maskiner.
4. Er tabellen kodeverk (statiske rader spørringene joiner mot), seed radene i
   `ArenaTestdata.seedKodeverk()`; ellers lag en `leggTil…`-hjelper der.

# Kjøre queries mot arena-viewet

Arena-databasen er **ikke** nåbar fra utviklermaskin, heller ikke med naisdevice (det finnes ingen naisdevice-gateway for Oracle — kun for postgres).
Bruk avd-vdi.

For å kunne kjøre queries mot viewet må man kunne koble seg på arena sin database. Dette er tilgjengelig på flere mulige måter, men det enkelse er nok via en avd-vdi.
1. Bestille RA-bruker på "Mine tilganger" https://nav.omada.cloud/requestaccess, hvor du må søke om "RA_UTEN_TILGANG". 
2. Be om brukernavn og passord til Arena databasen i q2, spør i #arena på slack; eventuelt bruk: https://vault.adeo.no/ui/vault/secrets/oracle/kv/dev%2Fcreds%2Farena_q2-tiltakspenger/details?version=1. Egentlig finnes verdien i [vault](https://vault.adeo.no/ui/vault/secrets/oracle/kv/dev%2Fconfig%2Farena_q2/details?version=3), men vi har tidligere heller bare fått den tilsendt på e-post.
3. Be om tilganger til å installere programvare i avd-vdien i #avd-vdi på Slack.
4. Gå til https://windows.cloud.microsoft og åpne VDIen "vdi-utvikler-tiltakspenger"
5. Med RA brukeren skal du kunne installere et fornuftig utviklingsverktøy til å kunne gjøre spørringer mot databasen (f.eks IntelliJ eller SQL Developer)

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #tiltakspenger-værsågod.
