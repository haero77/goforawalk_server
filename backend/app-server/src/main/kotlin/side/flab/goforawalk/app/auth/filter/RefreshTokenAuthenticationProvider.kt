package side.flab.goforawalk.app.auth.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import io.jsonwebtoken.ExpiredJwtException
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.auth.refreshtoken.RefreshTokenRepository
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.domain.UserRepository
import side.flab.goforawalk.app.support.util.ClockHolder

private val log = KotlinLogging.logger {}

@Component
class RefreshTokenAuthenticationProvider(
  private val tokenProvider: AppAuthTokenProvider,
  private val refreshTokenRepository: RefreshTokenRepository,
  private val userRepository: UserRepository,
  private val clockHolder: ClockHolder
) : AuthenticationProvider {

  override fun supports(authentication: Class<*>): Boolean {
    return RefreshTokenAuthenticationToken::class.java.isAssignableFrom(authentication)
  }

  override fun authenticate(authentication: Authentication): Authentication {
    val refreshTokenAuth = authentication as RefreshTokenAuthenticationToken
    val refreshToken = refreshTokenAuth.credentials

    // 1. 토큰에서 userId 추출
    val userId = try {
      tokenProvider.parseRefreshToken(refreshToken)
    } catch (e: ExpiredJwtException) {
      throw RefreshTokenExpiredException("Refresh token expired")
    }

    // 2. DB에서 refresh token 조회
    val storedRefreshToken = refreshTokenRepository.findByUserId(userId.value)
      ?: throw InvalidRefreshTokenException("Refresh token not found for user: ${userId.value}")

    // 3. 토큰 일치 여부 확인
    if (!storedRefreshToken.tokenEquals(refreshToken)) {
      log.warn { "Refresh token mismatch for user: ${userId.value}. Possible token theft." }
      throw InvalidRefreshTokenException("Refresh token mismatch")
    }

    // 4. 토큰 만료 여부 확인
    val now = clockHolder.now()
    if (storedRefreshToken.expired(now)) {
      log.info { "Refresh token expired for user: ${userId.value}" }
      throw RefreshTokenExpiredException("Refresh token expired")
    }

    // 5. 사용자 정보 조회
    val user = userRepository.findById(userId.value).orElseThrow {
      InvalidRefreshTokenException("User not found: ${userId.value}")
    }

    val userDetails = AppUserDetails(
      _userId = user.id!!,
      nickname = user.nickname,
      email = user.email
    )

    // 인증 성공
    return UsernamePasswordAuthenticationToken(userDetails, null, emptyList())
  }
}
