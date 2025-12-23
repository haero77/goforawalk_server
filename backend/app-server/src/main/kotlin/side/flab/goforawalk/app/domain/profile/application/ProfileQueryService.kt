package side.flab.goforawalk.app.domain.profile.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.domain.footstep.domain.FootstepDomainService
import side.flab.goforawalk.app.domain.user.domain.UserReader

@Service
class ProfileQueryService(
  private val footstepDomainService: FootstepDomainService,
  private val userReader: UserReader,
) {
  @Transactional(readOnly = true)
  fun queryProfileByUserId(userId: Long): ProfileQueryResponse {
    val user = userReader.getById(userId)

    return ProfileQueryResponse(
      userId = user.id!!,
      userNickname = user.nickname,
      userEmail = user.email,
      userProvider = user.provider,
      totalFootstepCount = footstepDomainService.countTotalFoostepsOfUser(userId),
      footstepStreakDays = footstepDomainService.countFootstepStreakDays(userId)
    )
  }
}