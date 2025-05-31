package side.flab.goforawalk.app.auth

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.application.UserId
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.Instant
import java.util.*
import javax.crypto.SecretKey

@Component
class AppAuthTokenProvider(
    private val properties: JwtProperties,
    private val clockHolder: ClockHolder
) {
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

    private fun toSigningKey(secretKey: String): SecretKey =
        Keys.hmacShaKeyFor(secretKey.toByteArray())

    private fun toExpirationSeconds(now: Instant, expirationSeconds: Long): Date =
        Date.from(now.plusSeconds(expirationSeconds))
}