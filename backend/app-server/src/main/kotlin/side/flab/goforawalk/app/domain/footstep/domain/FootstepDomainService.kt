package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Service
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.LocalDate

@Service
class FootstepDomainService(
    private val footstepRepository: FootstepRepository,
    private val clockHolder: ClockHolder
) {
    fun validateDailyFootstepLimit(user: User) {
        val today = user.getLocalDate(clockHolder)
        if (hasUserCreatedFootstepToday(user.id!!, today)) {
            throw IllegalStateException("발자취는 하루에 한 개만 생성 가능합니다: date=${today} user=${user} ")
        }
    }

    private fun hasUserCreatedFootstepToday(userId: Long, today: LocalDate): Boolean {
        return footstepRepository.existsByUserIdAndDate(userId, today)
    }
}