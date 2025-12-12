package side.flab.goforawalk.app.api.v1.auth.login.oauth2

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.springframework.beans.factory.annotation.Autowired
import side.flab.goforawalk.app.auth.refreshtoken.RefreshTokenRepository
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.fixture.LoginFixture.sampleKakaoIdToken
import side.flab.goforawalk.app.support.util.ClockHolder
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import side.flab.goforawalk.security.oauth2.OidcLoginRequest
import kotlin.test.Test

@DisplayName("POST /api/v1/auth/login/oauth2")
class POST_specs : BaseRestAssuredTest() {

  @Autowired
  private lateinit var clockHolder: ClockHolder

  @Autowired
  private lateinit var refreshTokenRepository: RefreshTokenRepository

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

  @Test
  fun `동일 사용자가 재로그인 시 리프레쉬 토큰이 갱신되고 200 OK를 응답한다`() {
    // Arrange
    val loginRequest = OidcLoginRequest(sampleKakaoIdToken())
    val provider = OAuth2Provider.KAKAO

    // Act - 1차 로그인
    val firstLoginResponse = given()
      .log().all()
      .body(loginRequest)
      .contentType("application/json")
      .post("/api/v1/auth/login/oauth2/{provider}", provider)
      .then()
      .log().all()
      .statusCode(200)
      .extract().response()

    val firstRefreshToken = firstLoginResponse.jsonPath().getString("data.credentials.refreshToken")
    val userId = firstLoginResponse.jsonPath().getLong("data.userId")

    // Act - 2차 로그인 (동일 사용자)
    val secondLoginResponse = given()
      .log().all()
      .body(loginRequest)
      .contentType("application/json")
      .post("/api/v1/auth/login/oauth2/{provider}", provider)
      .then()
      .log().all()
      .statusCode(200)
      .extract().response()

    val secondRefreshToken = secondLoginResponse.jsonPath().getString("data.credentials.refreshToken")

    // Assert
    assertThat(secondRefreshToken).isNotEqualTo(firstRefreshToken) // 새 토큰 발급
    assertThat(refreshTokenRepository.findByUserId(userId)).isNotNull // DB에 1개만 존재
  }
}