package side.flab.goforawalk.app.domain.profile.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import side.flab.goforawalk.app.domain.user.domain.User

data class UserNicknameUpdateRequest(
  @field:NotBlank(message = "닉네임은 비어있을 수 없습니다.")
  @field:Size(
    max = User.NICKNAME_UPDATE_MAX_LENGTH,
    message = "닉네임은 최대 ${User.NICKNAME_UPDATE_MAX_LENGTH} 글자까지 입력할 수 있습니다."
  )
  val nickname: String,
)

