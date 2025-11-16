package side.flab.goforawalk.app.auth.filter

import org.springframework.security.core.AuthenticationException

class InvalidRefreshTokenException(message: String) : AuthenticationException(message)