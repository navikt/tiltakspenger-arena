FROM ghcr.io/navikt/baseimages/temurin:17

COPY .nais/init.sh /init-scripts/init.sh
COPY build/install/tiltakspenger-arena/lib/*.jar .
