package side.flab.goforawalk.app.docs

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.security.oauth2.OAuth2Provider.APPLE

class UserApiDocsTest : DocsTestSupport() {

    @Test
    fun `user-delete-success`() {
        // Arrange
        val user = createSeoulUser(
            nickname = "산책왕",
            provider = APPLE,
            email = "test@test.com"
        ).save(userRepository)

        // Act & Assert
        mockMvc.perform(
            delete("/api/v1/users/me")
                .header("Authorization", "Bearer ${generateAccessToken(user)}")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    )
                )
            )
    }
}