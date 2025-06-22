package side.flab.goforawalk.app.domain.footstep.application

import org.springframework.stereotype.Service
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse.Companion.toDetailResponses
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository

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
}