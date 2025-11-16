package side.flab.goforawalk.app.auth.filter

import org.springframework.security.authentication.AbstractAuthenticationToken

class RefreshTokenAuthenticationToken(
    private val refreshToken: String
) : AbstractAuthenticationToken(emptyList()) {

    init {
        isAuthenticated = false
    }

    override fun getCredentials(): String = refreshToken

    override fun getPrincipal(): String = refreshToken
}