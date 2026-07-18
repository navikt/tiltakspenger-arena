package no.nav.tiltakspenger.arena.repository.sak

import io.kotest.matchers.shouldBe
import no.nav.tiltakspenger.arena.repository.arenaSakDTO
import no.nav.tiltakspenger.arena.repository.arenaTiltakspengerVedtakDTO
import org.junit.jupiter.api.Test
import java.time.LocalDate

class ArenaSakDTOTest {

    // Repo-test fordi: kunSakerMedVedtakInnenforPeriode er en ren overlapp-predikat over en liste saker,
    // og grensetilfellene (tom akkurat før/på/etter periodestart) testes uttømmende i én test.
    // Å reprodusere den samme grensematrisen gjennom route ville krevd et eget flersaks-oppsett + kall per rad.
    @Test
    fun `filtrering på liste av saker skal fungere`() {
        // Vedtakene er bevisst i ulik rekkefølge; sakPeriode() bruker min/maks og skal være uavhengig av rekkefølgen.
        val sakMedVedtakIUlikRekkefølge = arenaSakDTO(
            sakId = 1000,
            tiltakspengerVedtak = listOf(
                arenaTiltakspengerVedtakDTO(vedtakId = 1, fom = LocalDate.of(2023, 11, 11), tom = LocalDate.of(2023, 11, 13)),
                arenaTiltakspengerVedtakDTO(vedtakId = 2, fom = LocalDate.of(2023, 11, 14), tom = LocalDate.of(2023, 11, 15)),
                arenaTiltakspengerVedtakDTO(vedtakId = 3, fom = LocalDate.of(2023, 11, 7), tom = LocalDate.of(2023, 11, 10)),
            ),
        )
        val sakMedVedtakSomAlleHarSluttDato = arenaSakDTO(
            sakId = 1001,
            tiltakspengerVedtak = listOf(
                arenaTiltakspengerVedtakDTO(vedtakId = 11, fom = LocalDate.of(2023, 11, 11), tom = LocalDate.of(2023, 11, 13)),
                arenaTiltakspengerVedtakDTO(vedtakId = 12, fom = LocalDate.of(2023, 11, 14), tom = LocalDate.of(2023, 11, 15)),
                arenaTiltakspengerVedtakDTO(vedtakId = 13, fom = LocalDate.of(2023, 11, 7), tom = LocalDate.of(2023, 11, 10)),
            ),
        )
        val sakMedVedtakHvorEttErÅpent = arenaSakDTO(
            sakId = 1002,
            tiltakspengerVedtak = listOf(
                arenaTiltakspengerVedtakDTO(vedtakId = 20, fom = LocalDate.of(2023, 11, 11), tom = LocalDate.of(2023, 11, 13)),
                arenaTiltakspengerVedtakDTO(vedtakId = 21, fom = LocalDate.of(2023, 11, 14), tom = LocalDate.of(2023, 11, 15)),
                arenaTiltakspengerVedtakDTO(vedtakId = 22, fom = LocalDate.of(2023, 11, 7), tom = null),
            ),
        )
        val sakUtenVedtak = arenaSakDTO(sakId = 1004)

        val liste = listOf(
            sakUtenVedtak,
            sakMedVedtakIUlikRekkefølge,
            sakMedVedtakHvorEttErÅpent,
            sakMedVedtakSomAlleHarSluttDato,
        )

        // Perioden ligger foran alle vedtakene (som tidligst starter 7. nov) → ingen saker.
        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 2),
            tom = LocalDate.of(2023, 11, 6),
        ).size shouldBe 0

        // Perioden tar med 7. nov → de tre sakene med vedtak treffer; sakUtenVedtak filtreres av harTiltakspengerVedtak().
        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 2),
            tom = LocalDate.of(2023, 11, 7),
        ).size shouldBe 3

        // Sent i november er kun den åpne saken (vedtak uten sluttdato) fortsatt løpende.
        liste.kunSakerMedVedtakInnenforPeriode(
            fom = LocalDate.of(2023, 11, 20),
            tom = LocalDate.of(2023, 11, 29),
        ).size shouldBe 1
    }
}
