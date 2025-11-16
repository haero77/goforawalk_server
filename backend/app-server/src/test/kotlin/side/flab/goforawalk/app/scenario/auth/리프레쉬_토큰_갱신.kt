package side.flab.goforawalk.app.scenario.auth

import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.equalTo
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.auth.JwtProperties
import side.flab.goforawalk.app.auth.refreshtoken.RefreshToken
import side.flab.goforawalk.app.auth.refreshtoken.RefreshTokenRepository
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.fixture.AuthFixture.Companion.sampleKaKaoIdToken
import side.flab.goforawalk.app.support.util.ClockHolder
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import side.flab.goforawalk.security.oauth2.OidcLoginRequest
import java.time.Instant
import kotlin.test.Test

@Transactional
class 리프레쉬_토큰_갱신 : BaseRestAssuredTest() {

  @Autowired
  private lateinit var refreshTokenRepository: RefreshTokenRepository

  @Autowired
  private lateinit var jwtProperties: JwtProperties

  @MockitoSpyBean
  private lateinit var clockHolder: ClockHolder

  @Test
  fun `로그인 후 리프레쉬 토큰으로 액세스 토큰 갱신`() {
    val loginInstant = Instant.parse("2025-11-16T00:00:00Z")
    val refreshInstant = loginInstant.plusSeconds(1)

    // 로그인 흐름 전체에 고정 시각 적용
    given(clockHolder.now()).willReturn(loginInstant)

    // 로그인
    val provider = OAuth2Provider.KAKAO
    val loginRequest = OidcLoginRequest(sampleKaKaoIdToken())

    val loginResponse = RestAssuredMockMvc.given()
      .log().all()
      .body(loginRequest)
      .contentType("application/json")
      .`when`()
      .post("/api/v1/auth/login/oauth2/{provider}", provider)
    loginResponse
      .then()
      .log().all()
      .statusCode(200)

    // 리프레쉬 토큰 추출
    val originRefreshToken = loginResponse.jsonPath().getString("data.credentials.refreshToken")

    // 리프레시 요청부터는 다른 시각을 재주입해 토큰이 새로 서명되도록 보장
    given(clockHolder.now()).willReturn(refreshInstant)

    // 리프레쉬 토큰으로 액세스 토큰 갱신
    val refreshTokenRefreshResponse = authFixture.givenAuthorizationBearerHeader(originRefreshToken)
      .log().all()
      .`when`()
      .post("/api/v1/auth/token/refresh")

    refreshTokenRefreshResponse
      .then()
      .log().all()
      .statusCode(200)
      .body("data", notNullValue())
      .body("data.credentials", notNullValue())
      .body("data.credentials.accessToken", not(emptyString()))
      .body("data.credentials.refreshToken", not(emptyString()))

    // 리프레쉬 토큰 정상 저장 확인
    val refreshedRefreshToken = refreshTokenRefreshResponse.jsonPath().getString("data.credentials.refreshToken")
    val newAccessToken = refreshTokenRefreshResponse.jsonPath().getString("data.credentials.accessToken")

    // 새로 발급된 토큰들이 기존과 다른지 확인
    assertThat(refreshedRefreshToken).isNotEqualTo(originRefreshToken)

    val originAccessToken = loginResponse.jsonPath().getString("data.credentials.accessToken")
    assertThat(newAccessToken).isNotEqualTo(originAccessToken)

    // 새 액세스 토큰으로 API 요청 가능한지 확인
    authFixture.givenAuthorizationBearerHeader(newAccessToken)
      .`when`()
      .get("/api/v1/footsteps")
      .then()
      .statusCode(200)
  }

  @Test
  fun `만료된 리프레쉬 토큰으로 갱신 시도 시 401 에러`() {
    val loginInstant = Instant.parse("2025-11-16T00:10:00Z")
    given(clockHolder.now()).willReturn(loginInstant)

    val provider = OAuth2Provider.KAKAO
    val loginRequest = OidcLoginRequest(sampleKaKaoIdToken())
    val response = RestAssuredMockMvc.given()
      .body(loginRequest)
      .contentType("application/json")
      .`when`()
      .post("/api/v1/auth/login/oauth2/{provider}", provider)
      .then()
      .statusCode(200)
      .extract()
      .response()

    val refreshToken = response.jsonPath().getString("data.credentials.refreshToken")

    // 토큰 만료 시각 이후로 now() 세팅
    given(clockHolder.now()).willReturn(loginInstant.plusSeconds(jwtProperties.rtExpirationSeconds + 1))

    authFixture.givenAuthorizationBearerHeader(refreshToken)
      .log().all()
      .`when`()
      .post("/api/v1/auth/token/refresh")
      .then()
      .log().all()
      .statusCode(401)
      .body("code", equalTo(ApiErrorCode.A_4102.name))
  }

  @Test
  fun `존재하지 않는 리프레쉬 토큰으로 갱신 시도 시 401 에러`() {
    RestAssuredMockMvc.given()
      .log().all()
      .`when`()
      .post("/api/v1/auth/token/refresh")
      .then()
      .log().all()
      .statusCode(401)
      .body("code", equalTo(ApiErrorCode.A_4104.name))
  }

  @Test
  fun `저장된 토큰과 불일치하는 리프레쉬 토큰으로 갱신 시 401 에러`() {
    val loginInstant = Instant.parse("2025-11-16T00:20:00Z")
    given(clockHolder.now()).willReturn(loginInstant)

    val provider = OAuth2Provider.KAKAO
    val loginRequest = OidcLoginRequest(sampleKaKaoIdToken())
    val response = RestAssuredMockMvc.given()
      .body(loginRequest)
      .contentType("application/json")
      .`when`()
      .post("/api/v1/auth/login/oauth2/{provider}", provider)
      .then()
      .statusCode(200)
      .extract()
      .response()

    val userId = response.jsonPath().getLong("data.userId")
    val refreshToken = response.jsonPath().getString("data.credentials.refreshToken")

    // DB에 다른 토큰을 저장시켜서 비교 시 불일치 발생하게 함
    refreshTokenRepository.deleteByUserId(userId)
    refreshTokenRepository.save(
      RefreshToken(
        userId = userId,
        token = "stored-but-different-token",
        issuedAt = loginInstant,
        expiresAt = loginInstant.plusSeconds(jwtProperties.rtExpirationSeconds)
      )
    )

    authFixture.givenAuthorizationBearerHeader(refreshToken)
      .log().all()
      .`when`()
      .post("/api/v1/auth/token/refresh")
      .then()
      .log().all()
      .statusCode(401)
      .body("code", equalTo(ApiErrorCode.A_4103.name))
  }
}
