package side.flab.goforawalk.app.auth.filter

import org.springframework.security.core.AuthenticationException

class MalformedJwtException(
    message: String = "Malformed JWT token"
) : AuthenticationException(message)