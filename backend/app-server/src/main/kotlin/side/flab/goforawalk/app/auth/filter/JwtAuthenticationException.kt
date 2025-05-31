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

        fun emptyToken(cause: Throwable? = null): JwtAuthenticationException {
            return JwtAuthenticationException("No AccessToken exists", cause)
        }

        fun invalidToken(msg: String): JwtAuthenticationException {
            return JwtAuthenticationException("Invalid token: $msg")
        }
    }
}