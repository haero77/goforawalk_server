package side.flab.goforawalk.app.api.footstep.dto

import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import java.time.OffsetDateTime

data class TodayAvailabilityResponse(
  val canCreateToday: Boolean,
  val todayDate: String,
  val existingFootstep: ExistingFootstepResponse?
)

data class ExistingFootstepResponse(
  val footstepId: Long,
  val imageUrl: String,
  val content: String,
  val createdAt: OffsetDateTime
) {
  companion object {
    fun from(footstep: Footstep): ExistingFootstepResponse {
      return ExistingFootstepResponse(
        footstepId = footstep.id!!,
        imageUrl = footstep.imageUrl,
        content = footstep.content ?: "",
        createdAt = footstep.createdAtAsSeoulOffset()
      )
    }
  }
}