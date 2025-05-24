package side.flab.goforawalk.app.domain.footstep.application.dto

import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import java.time.LocalDate
import java.time.ZonedDateTime

data class FootstepDetailResponse(
    val userId: Long,
    val userNickname: String,
    val footStepId: Long,
    val date: LocalDate,
    val imageUrl: String,
    val content: String? = null,
    val createdAt: ZonedDateTime // "2025-05-10T22:00+09:00"
) {
    companion object {
        fun from(
            footstep: Footstep
        ): FootstepDetailResponse {
            val user = footstep.user

            return FootstepDetailResponse(
                userId = user.id!!,
                userNickname = user.nickname,
                footStepId = footstep.id!!,
                date = footstep.date,
                imageUrl = footstep.imageUrl,
                content = footstep.content,
                createdAt = footstep.createdAtAsZonedDateTime()
            )
        }
    }
}