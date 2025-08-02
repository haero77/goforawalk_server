package side.flab.goforawalk.app.docs

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType.NUMBER
import org.springframework.restdocs.payload.JsonFieldType.STRING
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import side.flab.goforawalk.app.domain.profile.application.dto.UserNicknameUpdateRequest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.security.oauth2.OAuth2Provider.APPLE

class ProfileApiDocsTest : DocsTestSupport() {

    @Test
    fun `profile-success`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕",
            provider = APPLE,
            email = "test@test.com"
        ).save(userRepository)

        createFootstep(user, dateOf("2025-02-27")).save(footstepRepository)
        createFootstep(user, dateOf("2025-02-28")).save(footstepRepository)
        createFootstep(user, dateOf("2025-03-02")).save(footstepRepository)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/profile")
                .header("Authorization", "Bearer ${generateAccessToken(user)}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    responseFields(
                        fieldWithPath("data.userId").description("유저 ID").type(NUMBER),
                        fieldWithPath("data.userNickname").description("유저 닉네임").type(STRING),
                        fieldWithPath("data.userEmail").description("유저 이메일").type(STRING).optional(),
                        fieldWithPath("data.userProvider").description("유저 가입 경로 (KAKAO/APPLE)").type(STRING),
                        fieldWithPath("data.totalFootstepCount").description("전체 발자취 개수").type(NUMBER),
                        fieldWithPath("data.footstepStreakDays").description("연속 발자취 일수").type(NUMBER),
                    )
                )
            )
    }

    @Test
    fun `profile-update-nickname-success`() {
        // Arrange
        val user = createSeoulUser("닉네임_변경_전").save(userRepository)
        val requestBody = UserNicknameUpdateRequest(
            nickname = "새로운닉네임"
        )
        
        val accessToken = generateAccessToken(user)

        // Act & Assert
        mockMvc.perform(
            patch("/api/v1/users/me/nickname")
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isNoContent)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    requestFields(
                        fieldWithPath("nickname").description("변경 요청 닉네임(최대 8글자)").type(STRING),
                    ),
                )
            )
    }

}