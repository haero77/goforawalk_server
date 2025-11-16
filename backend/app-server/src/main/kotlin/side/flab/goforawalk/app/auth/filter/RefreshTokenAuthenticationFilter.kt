package side.flab.goforawalk.app.auth.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import side.flab.goforawalk.app.support.util.AuthUtil

private val log = KotlinLogging.logger {}

class RefreshTokenAuthenticationFilter(
  private val authenticationManager: AuthenticationManager,
  private val successHandler: AuthenticationSuccessHandler,
  private val failureHandler: AuthenticationFailureHandler,
) : OncePerRequestFilter() {

  companion object {
    const val REFRESH_TOKEN_PATH = "/api/v1/auth/token/refresh"
  }

  private val requestMatcher: RequestMatcher =
    AntPathRequestMatcher(REFRESH_TOKEN_PATH, HttpMethod.POST.name())

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain,
  ) {
    if (!requestMatcher.matches(request)) {
      filterChain.doFilter(request, response)
      return
    }

    try {
      val authResult = attemptAuthentication(request)
      if (authResult.isAuthenticated) {
        successHandler.onAuthenticationSuccess(request, response, authResult)
      }
    } catch (e: AuthenticationException) {
      SecurityContextHolder.clearContext()
      failureHandler.onAuthenticationFailure(request, response, e)
    }
  }

  private fun attemptAuthentication(request: HttpServletRequest): Authentication {
    val refreshToken =
      AuthUtil.extractBearerToken(request) ?: throw MalformedJwtException(
        "Refresh token is missing or malformed"
      )

    log.info { "Attempting refresh token authentication for token: $refreshToken" }

    val authentication = RefreshTokenAuthenticationToken(refreshToken)
    return authenticationManager.authenticate(authentication)
  }
}