package side.flab.goforawalk.app.domain.footstep.application.dto

import com.fasterxml.jackson.databind.annotation.JsonSerialize
import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import side.flab.goforawalk.app.support.serializer.UserShortNicknameSerializer
import java.time.LocalDate
import java.time.OffsetDateTime

data class FootstepDetailResponse(
    val userId: Long,

    @JsonSerialize(using = UserShortNicknameSerializer::class)
    val userNickname: String,
    val footstepId: Long,
    val date: LocalDate,
    val imageUrl: String,
    val content: String? = null,
    val createdAt: OffsetDateTime // "2025-05-10T22:00+09:00"
) {
    companion object {
        fun from(
            footstep: Footstep
        ): FootstepDetailResponse {
            val user = footstep.user

            return FootstepDetailResponse(
                userId = user.id!!,
                userNickname = user.nickname,
                footstepId = footstep.id!!,
                date = footstep.date,
                imageUrl = footstep.imageUrl,
                content = footstep.content,
                createdAt = footstep.createdAtAsSeoulOffset()
            )
        }

        fun List<Footstep>.toDetailResponses(): List<FootstepDetailResponse> {
            return map { from(it) }
        }
    }
}