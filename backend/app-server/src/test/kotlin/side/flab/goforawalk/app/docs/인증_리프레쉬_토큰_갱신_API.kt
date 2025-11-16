package side.flab.goforawalk.app.docs

import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.auth.JwtProperties
import side.flab.goforawalk.app.auth.refreshtoken.RefreshToken
import side.flab.goforawalk.app.auth.refreshtoken.RefreshTokenRepository
import side.flab.goforawalk.app.support.fixture.AuthFixture.Companion.sampleKaKaoIdToken
import side.flab.goforawalk.app.support.util.ClockHolder
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import side.flab.goforawalk.security.oauth2.OidcLoginRequest
import java.time.Instant

@Transactional
class 인증_리프레쉬_토큰_갱신_API : DocsTestSupport() {

  @Autowired
  private lateinit var refreshTokenRepository: RefreshTokenRepository

  @Autowired
  private lateinit var jwtProperties: JwtProperties

  @MockitoSpyBean
  private lateinit var clockHolder: ClockHolder

  @Test
  fun `token-refresh-success`() {
    val loginInstant = Instant.parse("2025-11-16T00:00:00Z")
    given(clockHolder.now()).willReturn(loginInstant)

    val loginResponse = loginAndExtractRefreshToken()
    val refreshToken = loginResponse.refreshToken

    // 리프레시 시점에는 시간 차이를 둬 새 토큰 생성
    given(clockHolder.now()).willReturn(loginInstant.plusSeconds(1))

    mockMvc.perform(
      post("/api/v1/auth/token/refresh")
        .header("Authorization", "Bearer $refreshToken")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isOk)
      .andDo(
        docs.document(
          requestHeaders(
            headerWithName("Authorization").description("Bearer {RefreshToken}")
          ),
          responseFields(
            fieldWithPath("data.userId").description("유저 ID").type(NUMBER),
            fieldWithPath("data.credentials.accessToken").description("새 Access Token").type(STRING),
            fieldWithPath("data.credentials.refreshToken").description("새 Refresh Token").type(STRING),
            fieldWithPath("data.userInfo.email").description("이메일").type(STRING).optional(),
            fieldWithPath("data.userInfo.nickname").description("닉네임").type(STRING)
          )
        )
      )
  }

  @Test
  fun `token-refresh-expired`() {
    val loginInstant = Instant.parse("2025-11-16T00:10:00Z")
    given(clockHolder.now()).willReturn(loginInstant)

    val loginResponse = loginAndExtractRefreshToken()
    val refreshToken = loginResponse.refreshToken

    // 만료 시각 이후로 now 설정해 Expired 흐름 유도
    given(clockHolder.now()).willReturn(loginInstant.plusSeconds(jwtProperties.rtExpirationSeconds + 1))

    mockMvc.perform(
      post("/api/v1/auth/token/refresh")
        .header("Authorization", "Bearer $refreshToken")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isUnauthorized)
      .andDo(
        docs.document(
          responseFields(
            fieldWithPath("code").description("에러 코드").type(STRING),
            fieldWithPath("message").description("에러 메시지").type(STRING),
            fieldWithPath("detailMessage").description("상세 메시지").type(OBJECT).optional()
          )
        )
      )
  }

  @Test
  fun `token-refresh-mismatch`() {
    val loginInstant = Instant.parse("2025-11-16T00:20:00Z")
    given(clockHolder.now()).willReturn(loginInstant)

    val loginResponse = loginAndExtractRefreshToken()
    val refreshToken = loginResponse.refreshToken

    // 저장된 토큰을 다른 값으로 덮어써 불일치 유도
    refreshTokenRepository.deleteByUserId(loginResponse.userId)
    refreshTokenRepository.save(
      RefreshToken(
        userId = loginResponse.userId,
        token = "stored-but-different-token",
        issuedAt = loginInstant,
        expiresAt = loginInstant.plusSeconds(jwtProperties.rtExpirationSeconds)
      )
    )

    mockMvc.perform(
      post("/api/v1/auth/token/refresh")
        .header("Authorization", "Bearer $refreshToken")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isUnauthorized)
      .andDo(
        docs.document(
          responseFields(
            fieldWithPath("code").description("에러 코드").type(STRING),
            fieldWithPath("message").description("에러 메시지").type(STRING),
            fieldWithPath("detailMessage").description("상세 메시지").type(OBJECT).optional()
          )
        )
      )
  }

  @Test
  fun 인증_리프레쉬_토큰_갱신_실패_토큰_형식_오류() {
    mockMvc.perform(
      post("/api/v1/auth/token/refresh")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isUnauthorized)
      .andDo(
        docs.document(
          responseFields(
            fieldWithPath("code").description("에러 코드").type(STRING),
            fieldWithPath("message").description("에러 메시지").type(STRING),
            fieldWithPath("detailMessage").description("상세 메시지").type(OBJECT).optional()
          )
        )
      )
  }

  private fun loginAndExtractRefreshToken(): LoginResponse {
    val provider = OAuth2Provider.KAKAO
    val loginRequest = OidcLoginRequest(sampleKaKaoIdToken())

    val result = mockMvc.perform(
      post("/api/v1/auth/login/oauth2/{provider}", provider)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest))
        .accept(MediaType.APPLICATION_JSON)
    ).andExpect(status().isOk)
      .andReturn()

    val json = objectMapper.readTree(result.response.contentAsByteArray)
    val data = json.path("data")
    val credentials = data.path("credentials")
    return LoginResponse(
      userId = data.path("userId").asLong(),
      refreshToken = credentials.path("refreshToken").asText()
    )
  }

  private data class LoginResponse(
    val userId: Long,
    val refreshToken: String
  )
}
