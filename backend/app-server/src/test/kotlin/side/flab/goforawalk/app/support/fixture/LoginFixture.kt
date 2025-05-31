package side.flab.goforawalk.app.support.fixture

import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.domain.User

object LoginFixture {
    fun generateAT(
        provider: AppAuthTokenProvider,
        user: User,
    ): String {
        val appAuthToken = provider.generate(AppUserDetails(user.id!!, user.nickname))
        return appAuthToken.accessToken
    }
}