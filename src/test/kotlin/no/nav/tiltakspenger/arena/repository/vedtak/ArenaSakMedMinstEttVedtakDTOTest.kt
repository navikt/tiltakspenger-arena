package no.nav.tiltakspenger.arena.repository.vedtak

import io.kotest.assertions.throwables.shouldThrow
import no.nav.tiltakspenger.arena.repository.arenaSakDTO
import org.junit.jupiter.api.Test

class ArenaSakMedMinstEttVedtakDTOTest {

    // Enhetstest fordi: konstruktør-vernet er defensivt og unåbart via route — SakRepository filtrerer bort saker uten vedtak (kunSakerMedVedtakInnenforPeriode) før typen opprettes.
    @Test
    fun `sak uten tiltakspengervedtak avvises`() {
        shouldThrow<IllegalStateException> {
            ArenaSakMedMinstEttVedtakDTO(arenaSakDTO(sakId = 3000))
        }
    }
}
