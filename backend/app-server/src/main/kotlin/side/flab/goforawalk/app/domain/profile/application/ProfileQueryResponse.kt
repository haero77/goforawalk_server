package side.flab.goforawalk.app.domain.profile.application

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import side.flab.goforawalk.app.support.serializer.UserShortNicknameSerializer
import side.flab.goforawalk.security.oauth2.OAuth2Provider

data class ProfileQueryResponse(
    val userId: Long,

    @JsonSerialize(using = UserShortNicknameSerializer::class)
    val userNickname: String,

    val userProvider: OAuth2Provider,
    val totalFootstepCount: Long,
    val footstepStreakDays: Long,
)