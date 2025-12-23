package side.flab.goforawalk.app.auth

data class RefreshTokenResponse(
  val userId: Long,
  val credentials: AppAuthToken,
  val userInfo: UserInfo
)