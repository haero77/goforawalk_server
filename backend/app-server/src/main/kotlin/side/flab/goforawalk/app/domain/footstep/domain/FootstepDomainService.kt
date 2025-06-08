package side.flab.goforawalk.app.domain.footstep.domain

import org.springframework.stereotype.Service
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.LocalDate

@Service
class FootstepDomainService(
    private val footstepRepository: FootstepRepository,
    private val clockHolder: ClockHolder,
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

    fun countTotalFoostepsOfUser(userId: Long): Long {
        val footsteps = footstepRepository.findAllByUserIdFetchJoinUser(userId)
        return footsteps.size.toLong()
    }

    fun countFootstepStreakDays(userId: Long): Long {
        val footsteps = footstepRepository.findAllByUserIdFetchJoinUser(userId)
        if (footsteps.isEmpty()) {
            return 0L
        }

        val ascendingDates = footsteps.map { it.date }.sorted()

        var maxStreakDays = 1L
        var currentStreakDays = 1L

        for (i in 1 until ascendingDates.size) {
            val previousDate = ascendingDates[i - 1]
            val currentDate = ascendingDates[i]

            // 날짜 차이가 1일이면 연속된 날
            if (currentDate == previousDate.plusDays(1)) {
                currentStreakDays++
            } else {
                currentStreakDays = 1L // 연속이 끊겼으므로 초기화
            }

            maxStreakDays = maxOf(maxStreakDays, currentStreakDays)
        }

        return maxStreakDays
    }
}