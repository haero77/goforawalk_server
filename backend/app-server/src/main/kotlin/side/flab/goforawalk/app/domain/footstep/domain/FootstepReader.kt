package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Component
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.LocalDate

@Component
class FootstepReader(
    private val footstepRepository: FootstepRepository,
    private val clockHolder: ClockHolder
) {
    fun isUserAlreadyCreatedFootstepToday(user: User): Boolean {
        return existsByUserIdAndDate(user.id!!, user.getLocalDate(clockHolder))
    }

    private fun existsByUserIdAndDate(userId: Long, date: LocalDate): Boolean {
        return footstepRepository.existsByUserIdAndDate(userId = userId, date = date)
    }
}