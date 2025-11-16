package side.flab.goforawalk.app.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.auth.refreshtoken.RefreshToken
import side.flab.goforawalk.app.auth.refreshtoken.RefreshTokenRepository
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.application.UserId
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class AppAuthTokenProvider(
    private val properties: JwtProperties,
    private val clockHolder: ClockHolder,
    private val refreshTokenRepository: RefreshTokenRepository
) {
    @Transactional
    fun generate(userDetails: AppUserDetails): AppAuthToken {
        val now = clockHolder.now()

        val accessToken = generateJwt(
            userDetails,
            properties.atSecretKey,
            properties.atExpirationSeconds,
            now
        )
        val refreshToken = generateJwt(
            userDetails,
            properties.rtSecretKey,
            properties.rtExpirationSeconds,
            now
        )

        // RefreshToken DB에 저장
        refreshTokenRepository.deleteByUserId(userDetails.getUserId())
        val newRefreshToken = RefreshToken(
            userId = userDetails.getUserId(),
            token = refreshToken,
            issuedAt = now,
            expiredAt = getRefreshTokenExpiresAt(now)
        )
        refreshTokenRepository.save(newRefreshToken)

        return AppAuthToken(
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }

    private fun generateJwt(
        userDetails: AppUserDetails,
        secretKey: String,
        expirationSeconds: Long,
        now: Instant
    ): String {
        return Jwts.builder()
            .subject(userDetails.getUserId().toString())
            .issuer(properties.issuer)
            .claim("nickname", userDetails.nickname)
            .issuedAt(Date.from(now))
            .expiration(toExpirationSeconds(now, expirationSeconds))
            .signWith(toSigningKey(secretKey))
            .compact()
    }

    fun parseAccessToken(token: String) : UserId {
        val claims = Jwts.parser()
            .verifyWith(toSigningKey(properties.atSecretKey))
            .requireIssuer(properties.issuer)
            .clock { Date.from(clockHolder.now()) }
            .build()
            .parseSignedClaims(token)
            .payload

        return UserId(claims.subject.toLong())
    }

    fun parseRefreshToken(token: String) : UserId {
        val claims = Jwts.parser()
            .verifyWith(toSigningKey(properties.rtSecretKey))
            .requireIssuer(properties.issuer)
            .clock { Date.from(clockHolder.now()) }
            .build()
            .parseSignedClaims(token)
            .payload

        return UserId(claims.subject.toLong())
    }

    fun getRefreshTokenExpiresAt(issuedAt: Instant): Instant {
        return issuedAt.plusSeconds(properties.rtExpirationSeconds)
    }

    private fun toSigningKey(secretKey: String): SecretKey =
        Keys.hmacShaKeyFor(secretKey.toByteArray())

    private fun toExpirationSeconds(now: Instant, expirationSeconds: Long): Date =
        Date.from(now.plusSeconds(expirationSeconds))
}