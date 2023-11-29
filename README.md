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

For lokal kjøring ligger det et docker-compose oppsett i `./docker-compose` som mocker ut Kafka og auth-server. Gå inn
i den folderen og kjør `docker-compose up -d --build`.

**Nødvendig ved førstegangsoppsett --** Legg inn følgende innslag i /etc/hosts:

```0.0.0.0 kafka```

# Henvendelser

Spørsmål knyttet til koden eller prosjektet kan stilles som issues her på GitHub.

## For NAV-ansatte

Interne henvendelser kan sendes via Slack i kanalen #tiltakspenger-værsågod.
