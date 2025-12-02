package side.flab.goforawalk.app.support.util

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpHeaders

object AuthUtil {
  private const val BEARER_PREFIX = "Bearer "

  fun extractBearerToken(request: HttpServletRequest): String? {
    return request.getHeader(HttpHeaders.AUTHORIZATION)
      ?.takeIf { it.startsWith(BEARER_PREFIX) }
      ?.substring(BEARER_PREFIX.length)
      ?.trim()
      ?.takeIf { it.isNotEmpty() }
  }
}