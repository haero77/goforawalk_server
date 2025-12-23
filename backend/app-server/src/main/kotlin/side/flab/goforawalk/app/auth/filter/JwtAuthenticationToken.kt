package side.flab.goforawalk.app.auth.filter

import org.springframework.security.authentication.AbstractAuthenticationToken
import side.flab.goforawalk.app.domain.user.application.UserId

class JwtAuthenticationToken(
  private val userId: UserId,
) : AbstractAuthenticationToken(emptyList()) {
  init {
    isAuthenticated = true // JWT 토큰이 파싱 성공했다면 이미 인증된 상태
  }

  override fun getCredentials(): String {
    return "" // JWT에서는 credential 불필요
  }

  override fun getPrincipal(): UserId {
    return userId
  }
}