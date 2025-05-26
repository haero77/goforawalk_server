package side.flab.goforawalk.app.auth.filter

import org.springframework.security.core.AuthenticationException

class JwtAuthenticationException(
    msg: String,
    cause: Throwable? = null,
) : AuthenticationException(msg, cause) {
    companion object {
        fun expiredToken(cause: Throwable? = null): JwtAuthenticationException {
            return JwtAuthenticationException("Token has expired", cause)
        }

        fun malformedToken(cause: Throwable? = null): JwtAuthenticationException {
            return JwtAuthenticationException("Malformed token", cause)
        }

        fun invalidToken(msg: String): JwtAuthenticationException {
            return JwtAuthenticationException("Invalid token: $msg")
        }
    }
}