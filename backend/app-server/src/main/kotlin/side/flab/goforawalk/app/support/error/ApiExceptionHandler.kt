package side.flab.goforawalk.app.support.error

import io.sentry.Sentry
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import side.flab.goforawalk.app.support.response.ErrorResponse
import side.flab.goforawalk.app.support.response.ErrorResponse.ErrorDetail

@RestControllerAdvice
class ApiExceptionHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ExceptionHandler(RuntimeException::class)
  fun handleRuntimeException(e: RuntimeException): ErrorResponse<Nothing> {
    log.error("Error occurred: ${e.message}", e)
    Sentry.captureException(e)
    return ErrorResponse.internalServerError(e.message ?: "Internal server error")
  }

  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  @ExceptionHandler(IllegalArgumentException::class)
  fun handleIllegalArgumentException(e: IllegalArgumentException): ErrorResponse<Nothing> {
    log.error("IllegalArgumentException occurred: ${e.message}", e)
    return ErrorResponse.badRequest(e.message)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleMethodArgumentNotValidException(e: MethodArgumentNotValidException): ErrorResponse<List<ErrorDetail>> {
    log.warn("MethodArgumentNotValidException occurred: ${e.message}")
    return ErrorResponse.badRequest(e)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(HttpMessageNotReadableException::class)
  fun handleMethodArgumentNotValidException(e: HttpMessageNotReadableException): ErrorResponse<Nothing> {
    log.warn("HttpMessageNotReadableException occurred: ${e.message}")
    return ErrorResponse.badRequest(e.message)
  }

  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ExceptionHandler(BadRequestException::class)
  fun handleBadRequestException(e: BadRequestException): ErrorResponse<Nothing> {
    log.error("BadRequestException occurred: ${e.message}", e)
    return ErrorResponse.badRequest(e.message)
  }

  @ResponseStatus(HttpStatus.CONFLICT)
  @ExceptionHandler(IllegalStateException::class)
  fun handleIllegalStateException(e: IllegalStateException): ErrorResponse<Nothing> {
    log.error("IllegalStateException occurred: ${e.message}", e)
    return ErrorResponse.conflict(e.message)
  }
}