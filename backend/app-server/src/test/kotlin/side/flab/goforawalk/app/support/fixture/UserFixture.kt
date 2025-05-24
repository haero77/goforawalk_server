package side.flab.goforawalk.app.support.fixture

import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.domain.user.domain.UserRepository
import side.flab.goforawalk.security.oauth2.OAuth2Provider

object UserFixture {
    private const val DEFAULT_EMAIL = "test@example.com"
    private const val DEFAULT_PROVIDER_USERNAME = "test-provider-username"
    private const val DEFAULT_NICKNAME = "테스트유저"

    fun createSeoulUser(
        email: String? = DEFAULT_EMAIL,
        provider: OAuth2Provider = OAuth2Provider.KAKAO,
        providerUsername: String = DEFAULT_PROVIDER_USERNAME,
        nickname: String
    ): User {
        return createUser(
            email = email,
            provider = provider,
            providerUsername = providerUsername,
            nickname = nickname,
            timeZone = "Asia/Seoul"
        )
    }

    private fun createUser(
        email: String? = DEFAULT_EMAIL,
        provider: OAuth2Provider = OAuth2Provider.KAKAO,
        providerUsername: String = DEFAULT_PROVIDER_USERNAME,
        nickname: String = DEFAULT_NICKNAME,
        timeZone: String
    ): User {
        return User(
            email = email,
            provider = provider,
            providerUsername = providerUsername,
            nickname = nickname,
            timeZone = timeZone
        )
    }

    fun User.save(repo: UserRepository): User {
        return repo.save(this)
    }
}