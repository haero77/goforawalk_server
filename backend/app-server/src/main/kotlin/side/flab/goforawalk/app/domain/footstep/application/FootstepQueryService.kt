package side.flab.goforawalk.app.domain.footstep.application

import org.springframework.stereotype.Service
import side.flab.goforawalk.app.api.footstep.dto.ExistingFootstepResponse
import side.flab.goforawalk.app.api.footstep.dto.FootstepQueryResponse
import side.flab.goforawalk.app.api.footstep.dto.TodayAvailabilityResponse
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse.Companion.toDetailResponses
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository
import java.time.LocalDate

@Service
class FootstepQueryService(
  private val footstepRepository: FootstepRepository,
) {
  fun findAllFootStepsOfUser(userId: Long): List<FootstepDetailResponse> {
    val footsteps = footstepRepository.findAllByUserIdFetchJoinUser(userId)

    // sort by date desc
    val sortedFootsteps = footsteps.sortedByDescending { it.date }

    return sortedFootsteps.toDetailResponses()
  }

  fun findAllFootstepsBy(
    userId: Long,
    startDate: LocalDate,
    endDate: LocalDate
  ): FootstepQueryResponse {
    val foundFootsteps = footstepRepository.findAllByUserIdAndDateBetween(
      userId = userId,
      startDate = startDate,
      endDate = endDate
    )

    // sort by date asc
    val sortedFootsteps = foundFootsteps.sortedBy { it.date }

    return FootstepQueryResponse(
      footsteps = sortedFootsteps.toDetailResponses()
    )
  }

  fun getTodayAvailability(userId: Long): TodayAvailabilityResponse {
    val today = LocalDate.now()
    val existingFootstep = footstepRepository.findByUserIdAndDate(userId, today)

    return if (existingFootstep != null) {
      TodayAvailabilityResponse(
        canCreateToday = false,
        todayDate = today.toString(),
        existingFootstep = ExistingFootstepResponse.from(existingFootstep)
      )
    } else {
      TodayAvailabilityResponse(
        canCreateToday = true,
        todayDate = today.toString(),
        existingFootstep = null
      )
    }
  }
}