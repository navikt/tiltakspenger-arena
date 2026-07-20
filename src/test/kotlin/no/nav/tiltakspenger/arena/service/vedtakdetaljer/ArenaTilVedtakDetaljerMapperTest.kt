package no.nav.tiltakspenger.arena.service.vedtakdetaljer

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.repository.arenaBarnetilleggVedtakDTO
import no.nav.tiltakspenger.arena.repository.arenaSakDTO
import no.nav.tiltakspenger.arena.repository.arenaTiltakspengerVedtakDTO
import no.nav.tiltakspenger.arena.repository.vedtak.ArenaSakMedMinstEttVedtakDTO
import no.nav.tiltakspenger.libs.periode.Periode
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaTilVedtakDetaljerMapperTest {

    // Enhetstest fordi: barnetillegget droppes helt, så route-responsen er identisk med «ingen barnetillegg» — dette er et ekskluderende filter der kun sikkerloggen skiller.
    // Overlapp- og gap-variantene er route-testet (VedtaksperioderRouteTest), siden de er observerbare i JSON-en.
    @Test
    fun `barnetillegg helt utenfor saksperioden droppes`() {
        val sak = ArenaSakMedMinstEttVedtakDTO(
            arenaSakDTO(
                sakId = 2000,
                tiltakspengerVedtak = listOf(
                    arenaTiltakspengerVedtakDTO(
                        vedtakId = 201,
                        tilhørendeSakId = 2000,
                        fom = LocalDate.of(2023, 11, 7),
                        tom = LocalDate.of(2023, 11, 15),
                    ),
                ),
                barnetilleggVedtak = listOf(
                    arenaBarnetilleggVedtakDTO(
                        vedtakId = 202,
                        tilhørendeSakId = 2000,
                        fom = LocalDate.of(2023, 12, 1),
                        tom = LocalDate.of(2023, 12, 31),
                    ),
                ),
            ),
        )

        val resultat = ArenaTilVedtakDetaljerMapper.mapTiltakspengerFraArenaTilVedtaksperioder(listOf(sak), fnr = "12345678910")!!

        resultat.perioderMedVerdi.map { it.periode } shouldBe listOf(Periode(LocalDate.of(2023, 11, 7), LocalDate.of(2023, 11, 15)))
        resultat.verdier.single() shouldBe VedtakDetaljer(
            antallDager = 5.0,
            dagsatsTiltakspenger = 255,
            dagsatsBarnetillegg = 0,
            antallBarn = 0,
            tiltakGjennomføringsId = "Tiltak",
            rettighet = Rettighet.TILTAKSPENGER,
            vedtakId = 201,
            sakId = 2000,
            beslutningsdato = LocalDate.of(2023, 11, 10),
            sak = VedtakDetaljer.Sak(
                saksnummer = "202211",
                opprettetDato = LocalDate.of(2022, 1, 8),
                status = "Aktiv",
            ),
        )
    }
}
