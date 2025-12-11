package no.nav.tiltakspenger.arena.repository

class MeldekortDAO {

    fun findByFnrPersonId(fnr: String, personId: Long): List<ArenaMeldekortDTO> {}

}
