package side.flab.goforawalk.app.api.v1.auth.login.oauth2

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.LoginFixture.sampleKakaoIdToken
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import side.flab.goforawalk.security.oauth2.OidcLoginRequest
import kotlin.test.Test

@DisplayName("POST /api/v1/auth/login/oauth2")
class POST_specs : BaseRestAssuredTest() {
    @Test
    fun `회원탈퇴 후 재 로그인 시 200 OK를 응답한다`() {
        // Arrange
        // 회원가입
        val loginRequest = OidcLoginRequest(sampleKakaoIdToken())
        val provider = OAuth2Provider.KAKAO

        val userJoinResponse = given()
            .body(loginRequest)
            .contentType("application/json")
            .`when`()
            .post("/api/v1/auth/login/oauth2/{provider}", provider)
        val originUserId: Int = userJoinResponse.jsonPath().getInt("data.userId")

        val user = userRepository.findById(originUserId.toLong()).get()

        given()
            .header("Authorization", "Bearer ${generateAccessToken(user)}")
            .delete("/api/v1/users/me")

        val userReJoinResponse = given()
            .body(loginRequest)
            .contentType("application/json")
            .`when`()
            .post("/api/v1/auth/login/oauth2/{provider}", provider)
        val newUserId: Int = userReJoinResponse.jsonPath().getInt("data.userId")

        assertThat(newUserId).isNotEqualTo(originUserId)
    }
}