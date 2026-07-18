# Nullability i `arena_tilgang_ind`-viewene

Fasit for hvilke kolonner som er nullbare i Arena, og hvilke som *faktisk* er null for tiltakspenger-data i Q2.
Verifisert mot Q2 via VDI **2026-07-16** (bruker `arena_q2-tiltakspenger`).

Bakgrunn: `doc/arena-ddl/datadeling_tiltakspenger.sql` har **strippet constraints** og kan ikke brukes som nullability-fasit.
Denne fila er kilden.
Kjør spørringene under på nytt for å oppdatere.

## Skjema-nullability (per kolonne)

```sql
SELECT table_name, column_name, nullable, data_type
FROM   all_tab_columns
WHERE  owner = 'ARENA_TILGANG_IND'
ORDER  BY table_name, column_id;
```

## Faktisk forekomst av null (kolonner vi mapper som non-null)

Kjørt mot viewene (allerede tiltakspenger-scopet).
`nullable=Y` betyr «tillatt i skjemaet», `null i Q2` er reell forekomst.

| Kolonne | Mappes til | nullable | Null i Q2 | Total | Konklusjon |
|---|---|---|---|---|---|
| `MELDEKORT.REG_DATO` | `registrert` | Y | 0 | 112 969 | Non-null OK — forventes aldri null |
| `MELDEKORT.MELDEKORTKODE` | `meldekortkode` | Y | 0 | 112 969 | Non-null OK |
| `MELDEKORT.STATUS_FERIE` | `ferie` | Y | 0 | 112 969 | Non-null OK (`DEFAULT 'N'` i basen) |
| `MELDEKORT.STATUS_ARBEIDET` | `arbeidet` | Y | 1 386 | 112 969 | Nullbare, men filtreres bort — se note 1 |
| `MELDEKORT.STATUS_KURS` | `kurs` | Y | 1 386 | 112 969 | Se note 1 |
| `MELDEKORT.STATUS_SYK` | `syk` | Y | 1 386 | 112 969 | Se note 1 |
| `MELDEKORT.STATUS_ANNETFRAVAER` | `annetFravaer` | Y | 1 386 | 112 969 | Se note 1 |
| `MELDEKORT.STATUS_FORTSATT_ARBEIDSOKER` | `fortsattArbeidsoker` | Y | 1 386 | 112 969 | Se note 1 |
| `MELDEKORT.MELDEGRUPPEKODE` | (join-nøkkel) | Y | 1 387 | 112 969 | INNER JOIN-filter — se note 1 og 2 |
| `MELDEKORT.MOD_DATO` | `sistEndret` | Y | 0 | 112 969 | Non-null OK — se note 3 |
| statusDato via MELDELOGG (LEFT JOIN-miss) | `statusDato` | (N i tabell) | 0 | 112 969 | Non-null OK — se note 3 |
| `POSTERING.POSTERINGSATS` | `sats` | Y | 0 | 118 270 | Non-null OK |
| `UTBETALINGSGRUNNLAG.MOD_DATO` | `dato` | Y | 0 | 16 578 | Non-null OK |
| `UTBETALINGSGRUNNLAG.POSTERINGSATS` | `sats` | Y | 0 | 16 578 | Non-null OK |
| `BEREGNINGSLOGG.REG_DATO` | `dato` | Y | 0 | 138 934 | Non-null OK |
| `ANMERKNING.REG_DATO` | `dato` | Y | 0 | 35 691 | Non-null OK |
| `SAK.REG_DATO` | `opprettetDato` | Y | 0 | 5 639 | Non-null OK |

### Note 1: status-flaggene er trygge kun pga. `MELDEGRUPPETYPE`-joinen
De ~1386 meldekortene med null status-flagg er (nesten) de samme ~1387 som har null `MELDEGRUPPEKODE`.
`MeldekortDAO` INNER JOIN-er mot `MELDEGRUPPETYPE`, så disse radene filtreres bort **før** mappingen.
Bekreftet: antall rader som overlever alle inner join-ene *og* har et null status-flagg er **0**:

```sql
SELECT COUNT(*)
FROM   arena_tilgang_ind.meldekort m
JOIN   arena_tilgang_ind.meldekortperiode  mp ON m.aar = mp.aar AND m.periodekode = mp.periodekode
JOIN   arena_tilgang_ind.beregningstatus   be ON be.beregningstatuskode = m.beregningstatuskode
JOIN   arena_tilgang_ind.mkskorttype       mt ON m.mkskortkode = mt.mkskortkode
JOIN   arena_tilgang_ind.meldegruppetype   mg ON m.meldegruppekode = mg.meldegruppekode
WHERE  m.status_arbeidet IS NULL OR m.status_kurs IS NULL OR m.status_syk IS NULL
    OR m.status_annetfravaer IS NULL OR m.status_fortsatt_arbeidsoker IS NULL;
-- => 0
```

**Konsekvens:** status-flaggene mappes trygt som non-null i dag, men *kun* så lenge `MELDEGRUPPETYPE`-joinen står.
Fjernes eller endres den joinen, må status-flaggene gjøres nullbare gjennom hele kjeden samtidig (arena → datadeling → OpenAPI).
Se kommentar i `MeldekortDAO`.

### Note 2: ~1387 meldekort forsvinner stille (1,2 %)
Meldekort med null `MELDEGRUPPEKODE` filtreres bort av INNER JOIN-en og kommer aldri med i `/meldekort`-responsen.
Antatt tekniske/placeholder-rader, men bør bekreftes med Arena-teamet om det er tilsiktet.

### Note 3: statusDato/sistEndret er strukturelt nullbare, men mappes non-null
- `sistEndret` (`MELDEKORT.MOD_DATO`) er `nullable=Y` i skjemaet, men 0 av 112 969 er null i Q2.
- `statusDato` kommer fra `MELDELOGG.HENDELSEDATO` (`NOT NULL` i tabellen) via en **LEFT JOIN** — et meldekort uten matchende logg-rad ville gitt NULL, men 0 av 112 969 mangler treff i Q2.

Begge mappes derfor **non-null**.
Beslutning (2026-07-16): siden det er 0 forekomster i Q2, ingen innmeldte feil, og ingenting i prod/dev-loggene, håndterer vi ikke null-tilfellet.
Skulle et meldekort uten logg-treff (eller med null MOD_DATO) dukke opp, feiler `/meldekort` med NPE — da gjøres feltene nullbare gjennom hele kjeden (arena `ArenaMeldekortDTO`/`MeldekortDetaljer` → datadeling `ArenaMeldekort`/`ArenaMeldekortResponse` → `ArenaMeldekort.yaml`).
`MeldekortRepositoryTest` pinner NPE-oppførselen.
