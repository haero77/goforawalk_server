package side.flab.goforawalk.app.auth.filter

import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.stereotype.Component
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.response.ErrorResponse
import java.nio.charset.StandardCharsets

private val log = KotlinLogging.logger {}

@Component
class JwtAuthenticationFailureHandler(
  private val objectMapper: ObjectMapper,
) : AuthenticationFailureHandler {
  override fun onAuthenticationFailure(
    request: HttpServletRequest,
    response: HttpServletResponse,
    exception: AuthenticationException,
  ) {
    log.warn(exception) { "Jwt Authentication failed: ${exception.message}" }

    val errorResponse = when (exception) {
      is AccessTokenExpiredException -> ErrorResponse(
        code = ApiErrorCode.A_4101,
        message = exception.message ?: ApiErrorCode.A_4102.defaultMessage
      )

      else -> ErrorResponse.authenticationFailed("Jwt Authentication failed: ${exception.message}")
    }

    response.status = HttpServletResponse.SC_UNAUTHORIZED
    response.contentType = MediaType.APPLICATION_JSON_VALUE
    response.characterEncoding = StandardCharsets.UTF_8.name()

    response.writer.use { writer ->
      writer.write(objectMapper.writeValueAsString(errorResponse))
      writer.flush()
    }
  }
}