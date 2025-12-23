package side.flab.goforawalk.app.support.response

import org.springframework.web.bind.MethodArgumentNotValidException
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.error.ApiErrorCode.*

data class ErrorResponse<T>(
  val code: ApiErrorCode,
  val message: String,
  val detailMessage: T? = null,
) {
  companion object {
    fun authenticationFailed(
      message: String = A_4100.defaultMessage,
    ): ErrorResponse<Nothing> {
      return ErrorResponse(A_4100, message)
    }

    fun internalServerError(
      message: String = A_5000.defaultMessage,
    ): ErrorResponse<Nothing> {
      return ErrorResponse(A_5000, message)
    }

    fun badRequest(message: String?): ErrorResponse<Nothing> {
      val errorCode = A_4000
      return ErrorResponse(errorCode, message ?: errorCode.defaultMessage)
    }

    fun badRequest(e: MethodArgumentNotValidException): ErrorResponse<List<ErrorDetail>> {
      val errorCode = A_4000
      val fieldErrors = e.bindingResult.fieldErrors

      val errorDetails = fieldErrors.map { fieldError ->
        ErrorDetail(
          field = fieldError.field,
          rejectValue = fieldError.rejectedValue,
          message = fieldError.defaultMessage
        )
      }

      return ErrorResponse(errorCode, errorCode.defaultMessage, errorDetails)
    }

    fun conflict(message: String?): ErrorResponse<Nothing> {
      val errorCode = A_4900
      return ErrorResponse(errorCode, message ?: errorCode.defaultMessage)
    }
  }

  data class ErrorDetail(
    val field: String,
    val rejectValue: Any?,
    val message: String?,
  )
}