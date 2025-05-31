package side.flab.goforawalk.app.support.error

import org.springframework.http.HttpStatus

enum class ApiErrorCode(
    val defaultMessage: String,
) {
    A_4100("Authentication failed"),
    A_4000(HttpStatus.BAD_REQUEST.reasonPhrase),
    A_4900(HttpStatus.CONFLICT.reasonPhrase),
    A_5000("Internal server error"),
}