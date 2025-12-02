package side.flab.goforawalk.app.auth.filter

import org.springframework.security.core.AuthenticationException

class RefreshTokenExpiredException(message: String) : AuthenticationException(message)