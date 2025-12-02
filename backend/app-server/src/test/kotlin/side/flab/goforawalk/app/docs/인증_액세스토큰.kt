package side.flab.goforawalk.app.docs

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType.OBJECT
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.app.support.fixture.UserFixture
import side.flab.goforawalk.app.support.fixture.UserFixture.save

class 인증_액세스토큰 : DocsTestSupport() {
  @Test
  fun 인증_액세스토큰_만료() {
    val user = UserFixture.createSeoulUser().save(userRepository)
    val expiredAccessToken = authFixture.generateExpiredAccessToken(user)

    mockMvc.perform(
      get("/api/v1/profile")
        .header("Authorization", "Bearer $expiredAccessToken")
        .accept(MediaType.APPLICATION_JSON)
    )
      .andExpect(status().isUnauthorized)
      .andExpect(jsonPath("$.code").value(ApiErrorCode.A_4101.name))
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
}
