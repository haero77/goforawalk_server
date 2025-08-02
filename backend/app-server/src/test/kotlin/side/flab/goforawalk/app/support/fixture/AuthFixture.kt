package side.flab.goforawalk.app.support.fixture

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.domain.User

@Component
class AuthFixture(
    private val authTokenProvider: AppAuthTokenProvider
) {
    fun givenAuthenticatedUser(user: User): MockMvcRequestSpecification {
        return given()
            .header(AUTHORIZATION, "Bearer ${generateAT(user)}")
    }

    private fun generateAT(
        user: User,
    ): String {
        val appAuthToken = authTokenProvider.generate(AppUserDetails(user.id!!, user.nickname))
        return appAuthToken.accessToken
    }
}