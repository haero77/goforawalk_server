package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Component

@Component
class FootstepReader(
    private val footstepRepository: FootstepRepository,
) {
    fun getById(footstepId: Long): Footstep {
        return getByIdInternal(footstepId)
    }

    private fun getByIdInternal(footstepId: Long): Footstep {
        return footstepRepository.findById(footstepId)
            .orElseThrow { IllegalArgumentException("Cannot find Footstep for id=$footstepId") }
    }
}