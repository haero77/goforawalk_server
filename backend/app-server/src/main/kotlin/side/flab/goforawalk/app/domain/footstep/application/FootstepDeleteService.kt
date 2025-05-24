package side.flab.goforawalk.app.domain.footstep.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.domain.footstep.domain.FootstepReader

@Service
class FootstepDeleteService(
    private val footstepReader: FootstepReader,
) {
    @Transactional
    fun delete(footstepId: Long) {
        val footstep = footstepReader.getById(footstepId)
        footstep.delete()
    }
}