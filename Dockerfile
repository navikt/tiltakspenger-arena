FROM ghcr.io/navikt/baseimages/temurin:21

COPY .nais/init.sh /init-scripts/init.sh
COPY build/install/tiltakspenger-arena/lib/*.jar .
