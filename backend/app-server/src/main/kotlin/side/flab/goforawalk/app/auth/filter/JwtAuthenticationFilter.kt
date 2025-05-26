package side.flab.goforawalk.app.auth.filter

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.security.web.util.matcher.RequestMatcher
import org.springframework.web.filter.OncePerRequestFilter
import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.domain.user.application.UserId

private val log = KotlinLogging.logger {}

/**
 * "/api" path로 JWT 인증 필터를 적용하기 위한 필터
 */
class JwtAuthenticationFilter(
    private val authTokenProvider: AppAuthTokenProvider,
    private val failureHandler: JwtAuthenticationFailureHandler,
) : OncePerRequestFilter() {
    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    private val jwtPathMatcher: RequestMatcher = AntPathRequestMatcher("/api/**")
    private val excludedPathMatchers: List<RequestMatcher> = listOf(
        AntPathRequestMatcher("/api/v1/auth/**")
    )

    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        // 제외 경로에 매치되면 필터 적용 안함
        if (excludedPathMatchers.any { it.matches(request) }) {
            return true
        }

        // /api/** 경로가 아니면 필터 적용 안함
        return !jwtPathMatcher.matches(request)
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        try {
            val userId = validateAccessToken(request)

            val authenticationToken = JwtAuthenticationToken(userId)
            SecurityContextHolder.getContext().authentication = authenticationToken

            filterChain.doFilter(request, response)
        } catch (e: JwtAuthenticationException) {
            failureHandler.onAuthenticationFailure(request, response, e)
            return
        }
    }

    private fun validateAccessToken(request: HttpServletRequest): UserId {
        val accessToken = extractAccessToken(request) ?: throw JwtAuthenticationException.emptyToken()

        try {
            return authTokenProvider.parseAccessToken(accessToken)
        } catch (e: Exception) {
            log.warn(e) { "Failed to parse JWT token: ${e.message}" }
            throw JwtAuthenticationException.invalidToken(e.message ?: "Invalid JWT token")
        }
    }

    private fun extractAccessToken(request: HttpServletRequest): String? {
        return request.getHeader(HttpHeaders.AUTHORIZATION)
            ?.takeIf { it.startsWith(BEARER_PREFIX) }
            ?.substring(BEARER_PREFIX.length)
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
    }
}