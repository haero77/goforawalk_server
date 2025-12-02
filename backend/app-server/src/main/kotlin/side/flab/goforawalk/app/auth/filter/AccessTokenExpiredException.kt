package side.flab.goforawalk.app.auth.filter

import org.springframework.security.core.AuthenticationException

class AccessTokenExpiredException(message: String) : AuthenticationException(message)