package no.nav.tiltakspenger.arena.tiltakogaktivitet

sealed class ArenaOrdsException : RuntimeException {
    constructor(message: String) : super(message)
    constructor(message: String, cause: Exception) : super(message, cause)

    class PersonNotFoundException(message: String) : ArenaOrdsException(message)
    class UnauthorizedException(message: String, cause: Exception) : ArenaOrdsException(message, cause)
    class OtherException(message: String, cause: Exception) : ArenaOrdsException(message, cause)
}
