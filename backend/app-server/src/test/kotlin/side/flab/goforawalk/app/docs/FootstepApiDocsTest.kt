package side.flab.goforawalk.app.docs

import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.headers.HeaderDocumentation.headerWithName
import org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders
import org.springframework.restdocs.payload.JsonFieldType.*
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.RequestDocumentation.*
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.TestDateUtil.dateOf
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate

class FootstepApiDocsTest : DocsTestSupport() {
    @Test
    fun `footstep-list-success`() {
        // Arrange
        val user = createSeoulUser("산책왕").save(userRepository)
        val accessToken = generateAccessToken(user)

        createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)
        createFootstep(user, dateOf("2025-05-27")).save(footstepRepository)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/footsteps")
                .header("Authorization", "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    responseFields(
                        fieldWithPath("data.footsteps").description("발자취 리스트").type(ARRAY),
                        fieldWithPath("data.footsteps[].userId").description("유저 ID").type(NUMBER),
                        fieldWithPath("data.footsteps[].userNickname").description("유저 닉네임").type(STRING),
                        fieldWithPath("data.footsteps[].footstepId").description("발자취 ID").type(NUMBER),
                        fieldWithPath("data.footsteps[].date").description("발자취 날짜").type(STRING),
                        fieldWithPath("data.footsteps[].imageUrl").description("이미지 URL").type(STRING),
                        fieldWithPath("data.footsteps[].content").description("오늘의 한 마디").type(STRING).optional(),
                        fieldWithPath("data.footsteps[].createdAt").description("생성 일시").type(STRING)
                    )
                )
            )
    }

    @Test
    fun `footstep-create-success`() {
        // Arrange
        val user = createSeoulUser("산책왕").save(userRepository)
        val accessToken = generateAccessToken(user)

        val imageFile = MockMultipartFile(
            "data",
            "test-image.jpg",
            "image/jpeg",
            "test image file".toByteArray()
        )

        val content = MockMultipartFile(
            "content",
            "",
            "text/plain",
            "오늘 한강공원에서 산책했어요. 날씨가 정말 좋았습니다!".toByteArray()
        )

        // Act & Assert
        mockMvc.perform(
            multipart("/api/v1/footsteps")
                .file(imageFile)
                .file(content)
                .header("Authorization", "Bearer $accessToken")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    requestParts(
                        partWithName("data").description("업로드할 이미지 파일 (JPG 포맷)"),
                        partWithName("content").description("오늘의 한 마디")
                    ),
                    responseFields(
                        fieldWithPath("data.userId").description("유저 ID").type(NUMBER),
                        fieldWithPath("data.userNickname").description("유저 닉네임").type(STRING),
                        fieldWithPath("data.footstepId").description("생성된 발자취 ID").type(NUMBER),
                        fieldWithPath("data.date").description("산책 날짜 (YYYY-MM-DD 형식)").type(STRING),
                        fieldWithPath("data.imageUrl").description("업로드된 이미지 URL").type(STRING),
                        fieldWithPath("data.content").description("오늘의 한 마디").type(STRING).optional(),
                        fieldWithPath("data.createdAt").description("생성 일시 (ISO-8601 형식)").type(STRING)
                    )
                )
            )
    }

    @Test
    fun `footstep-create-unauthorized`() {
        // Arrange
        // Act & Assert
        mockMvc.perform(
            multipart("/api/v1/footsteps")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
            .andDo(
                docs.document(
                    responseFields(
                        fieldWithPath("code").description("에러 코드").type(STRING),
                        fieldWithPath("message").description("에러 메시지").type(STRING),
                        fieldWithPath("detailMessage").description("상세 에러 메시지").type(OBJECT).optional()
                    )
                )
            )
    }

    //    @Test
    fun `footstep-list-unauthorized`() {
        // Act & Assert
        mockMvc.perform(
            get("/api/v1/footsteps")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isUnauthorized)
            .andDo(
                docs.document(
                    responseFields(
                        fieldWithPath("code").description("에러 코드").type(STRING),
                        fieldWithPath("message").description("에러 메시지").type(STRING),
                        fieldWithPath("detailMessage").description("상세 에러 메시지").type(OBJECT).optional()
                    )
                )
            )
    }

    @Test
    fun `footstep-delete-success`() {
        // Arrange
        val user = createSeoulUser("산책왕").save(userRepository)
        val accessToken = generateAccessToken(user)
        val footstep = createFootstep(user, dateOf("2025-05-25")).save(footstepRepository)

        // Act & Assert
        mockMvc.perform(
            delete("/api/v1/footsteps/{footstepId}", footstep.id)
                .header("Authorization", "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    pathParameters(
                        parameterWithName("footstepId").description("삭제할 발자취 ID")
                    )
                )
            )
    }

    @Test
    fun `footstep-today-availability-can-create`() {
        // Arrange - 오늘 발자취가 없는 경우
        val user = createSeoulUser(
            nickname = "산책러버",
            providerUsername = "docs-test-user-1"
        ).save(userRepository)
        val accessToken = generateAccessToken(user)

        // 어제 발자취만 생성
        createFootstep(user, LocalDate.now().minusDays(1)).save(footstepRepository)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/footsteps/today/availability")
                .header("Authorization", "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    responseFields(
                        fieldWithPath("data.canCreateToday").description("오늘 발자취 생성 가능 여부").type(BOOLEAN),
                        fieldWithPath("data.todayDate").description("오늘 날짜 (YYYY-MM-DD 형식)").type(STRING),
                        fieldWithPath("data.existingFootstep").description("기존 발자취 정보 (없으면 null)").type(NULL).optional()
                    )
                )
            )
    }

    @Test
    fun `footstep-today-availability-cannot-create`() {
        // Arrange - 오늘 발자취가 있는 경우
        val user = createSeoulUser(
            nickname = "매일산책",
            providerUsername = "docs-test-user-2"
        ).save(userRepository)
        val accessToken = generateAccessToken(user)

        // 오늘 발자취 생성
        val todayFootstep = createFootstep(user, LocalDate.now()).save(footstepRepository)

        // Act & Assert
        mockMvc.perform(
            get("/api/v1/footsteps/today/availability")
                .header("Authorization", "Bearer $accessToken")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andDo(
                docs.document(
                    requestHeaders(
                        headerWithName("Authorization").description("AccessToken")
                    ),
                    responseFields(
                        fieldWithPath("data.canCreateToday").description("오늘 발자취 생성 가능 여부").type(BOOLEAN),
                        fieldWithPath("data.todayDate").description("오늘 날짜 (YYYY-MM-DD 형식)").type(STRING),
                        fieldWithPath("data.existingFootstep").description("기존 발자취 정보").type(OBJECT),
                        fieldWithPath("data.existingFootstep.footstepId").description("기존 발자취 ID").type(NUMBER),
                        fieldWithPath("data.existingFootstep.imageUrl").description("기존 발자취 이미지 URL").type(STRING),
                        fieldWithPath("data.existingFootstep.content").description("기존 발자취 내용").type(STRING),
                        fieldWithPath("data.existingFootstep.createdAt").description("기존 발자취 생성 일시 (ISO-8601 형식)").type(STRING)
                    )
                )
            )
    }

}