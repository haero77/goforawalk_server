package side.flab.goforawalk.app.support.error

import org.springframework.http.HttpStatus

enum class ApiErrorCode(
  val defaultMessage: String,
) {
  A_4100("Authentication failed"),
  A_4101("Access token expired"),
  A_4102("Token expired"),
  A_4103("Invalid refresh token"),
  A_4140("Malformed JWT token"),
  A_4000("잘못된 요청입니다."),
  A_4220("요청 형식이 올바르지 않습니다."),
  A_4900(HttpStatus.CONFLICT.reasonPhrase),
  A_5000("Internal server error"),
}