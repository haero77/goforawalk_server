package side.flab.goforawalk.app.domain.footstep.domain.dto

import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.util.ClockHolder

data class FootstepCreateDto constructor(
    val user: User,
    val imageUrl: String,
    val content: String?,
) {
    fun toEntity(clockHolder: ClockHolder): Footstep {
        return Footstep.of(
            user = this.user,
            imageUrl = this.imageUrl,
            clockHolder = clockHolder,
            content = this.content,
        )
    }
}