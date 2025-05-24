package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.domain.footstep.domain.dto.FootstepCreateDto
import side.flab.goforawalk.app.support.util.ClockHolder

@Component
class FootstepCreator(
    private val footstepRepository: FootstepRepository,
    private val clockHolder: ClockHolder
) {
    @Transactional
    fun create(create: FootstepCreateDto): Long {
        val newFootStep = footstepRepository.save(create.toEntity(clockHolder))
        return newFootStep.id!!
    }
}