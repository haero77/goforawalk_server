package side.flab.goforawalk.app.auth.refreshtoken

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.Instant

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val clockHolder: ClockHolder
) {

    @Transactional
    fun saveRefreshToken(userId: Long, token: String, expiresAt: Instant) {
        // 기존 refresh token 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByUserId(userId)

        val now = clockHolder.now()
        val newRefreshToken = RefreshToken(
            userId = userId,
            token = token,
            issuedAt = now,
            expiresAt = expiresAt
        )
        refreshTokenRepository.save(newRefreshToken)
    }
}