package side.flab.goforawalk.app.domain.profile.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.domain.profile.application.dto.UserNicknameUpdateRequest
import side.flab.goforawalk.app.domain.user.domain.UserReader

@Service
class ProfileUpdateService(
    private val userReader: UserReader,
) {
    @Transactional
    fun updateNickname(userId: Long, request: UserNicknameUpdateRequest) {
        val user = userReader.getById(userId)
        user.updateNickname(request.nickname)
    }
}