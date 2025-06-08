package side.flab.goforawalk.app.domain.profile.application

import side.flab.goforawalk.security.oauth2.OAuth2Provider

data class ProfileQueryResponse(
    val userId: Long,
    val userNickname: String,
    val userProvider: OAuth2Provider,
    val totalFootstepCount: Long,
    val footstepStreakDays: Long,
)