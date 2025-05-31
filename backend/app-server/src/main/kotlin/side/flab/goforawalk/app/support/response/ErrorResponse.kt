package side.flab.goforawalk.app.support.response

import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.error.ApiErrorCode.*

data class ErrorResponse(
    val code: ApiErrorCode,
    val message: String,
    val detailMessage: String? = null, // TODO 유효하지 않은 파라미터를 명세
) {
    companion object {
        fun authenticationFailed(
            message: String = A_4100.defaultMessage,
        ): ErrorResponse {
            return ErrorResponse(A_4100, message)
        }

        fun internalServerError(
            message: String = A_5000.defaultMessage,
        ): ErrorResponse {
            return ErrorResponse(A_5000, message)
        }

        fun badRequest(message: String?): ErrorResponse {
            val errorCode = A_4000
            return ErrorResponse(errorCode, message ?: errorCode.defaultMessage)
        }

        fun conflict(message: String?): ErrorResponse {
            val errorCode = A_4900
            return ErrorResponse(errorCode, message ?: errorCode.defaultMessage)
        }
    }
}