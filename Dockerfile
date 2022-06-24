FROM navikt/java:17
COPY .nais/init.sh /init-scripts/init.sh
COPY build/install/tiltakspenger-arena/libs/*.jar /

