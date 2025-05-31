package side.flab.goforawalk.app.domain.footstep.application

import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertAll
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mock.web.MockMultipartFile
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.domain.footstep.application.dto.FootStepCreateRequest
import side.flab.goforawalk.app.domain.footstep.domain.Footstep
import side.flab.goforawalk.app.domain.footstep.domain.FootstepCreator
import side.flab.goforawalk.app.domain.footstep.domain.FootstepDomainService
import side.flab.goforawalk.app.domain.footstep.domain.FootstepImageNameGenerator
import side.flab.goforawalk.app.domain.user.domain.UserReader
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.deleted
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.app.support.image.ImageUploader
import side.flab.goforawalk.app.support.mock.FakeClockHolder
import side.flab.goforawalk.app.support.mock.FakeImageUploader
import side.flab.goforawalk.app.support.util.ClockHolder
import java.time.LocalDate
import kotlin.test.Test

class FootstepCreateServiceTest : BaseIntegrationTest() {
    @Autowired
    lateinit var userReader: UserReader

    @Autowired
    lateinit var footstepCreator: FootstepCreator

    @Autowired
    lateinit var imageNameGenerator: FootstepImageNameGenerator

    @Test
    fun `유저가 오늘 이미 발자취를 생성한 경우, 발자취를 생성할 수 없다(하루 한 개 제한)`() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val footstepDate = LocalDate.of(2025, 5, 24)
        Footstep(user, footstepDate, "imageUrl").save(footstepRepository)

        val request = FootStepCreateRequest(user.id!!, mockMultipartFile(), "test-content")
        val clockHolder = FakeClockHolder(footstepDate)
        val SUT = footstepCreateService(clockHolder = clockHolder)

        // when, then
        Assertions.assertThatThrownBy { SUT.createFootstep(request) }
            .isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `발자취가 삭제된 경우, 발자취를 생성할 수 있다(하루 한 개 제한)`() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val footstepDate = LocalDate.of(2025, 5, 24)
        Footstep(user, footstepDate, "imageUrl").deleted().save(footstepRepository)

        val request = FootStepCreateRequest(user.id!!, mockMultipartFile(), "test-content")

        val clockHolder = FakeClockHolder(footstepDate)

        val SUT = footstepCreateService(
            imageUploader = FakeImageUploader("https://example.com/image.jpg"),
            clockHolder = clockHolder
        )

        // when
        val actual = SUT.createFootstep(request)

        // then
        assertAll(
            { assertThat(actual.userId).isEqualTo(user.id) },
            { assertThat(actual.userNickname).isEqualTo("산책왕") },
            { assertThat(actual.footstepId).isPositive() },
            { assertThat(actual.date).isEqualTo(LocalDate.of(2025, 5, 24)) },
            { assertThat(actual.imageUrl).isEqualTo("https://example.com/image.jpg") },
            { assertThat(actual.content).isEqualTo("test-content") },
            { assertThat(actual.createdAt).isNotNull() }
        )
    }

    @Test
    fun `유저와 이미지 파일로 발자취를 생성할 수 있다`() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val request = FootStepCreateRequest(
            userId = user.id!!,
            imageFile = multipartFile(),
            content = "test-content"
        )

        val imageUploader = FakeImageUploader(imageUrl = "https://example.com/image.jpg")
        val clockHolder = FakeClockHolder(localDate = LocalDate.of(2025, 5, 24))

        val SUT = footstepCreateService(imageUploader = imageUploader, clockHolder = clockHolder)

        // when
        val actual = SUT.createFootstep(request)

        // then
        assertAll(
            { assertThat(actual.userId).isEqualTo(user.id) },
            { assertThat(actual.userNickname).isEqualTo("산책왕") },
            { assertThat(actual.footstepId).isPositive() },
            { assertThat(actual.date).isEqualTo(LocalDate.of(2025, 5, 24)) },
            { assertThat(actual.imageUrl).isEqualTo("https://example.com/image.jpg") },
            { assertThat(actual.content).isEqualTo("test-content") },
            { assertThat(actual.createdAt).isNotNull() }
        )
    }

    private fun multipartFile(): MultipartFile {
        val imageFile: MultipartFile = mockMultipartFile()
        return imageFile
    }

    private fun mockMultipartFile() = MockMultipartFile(
        "image",
        "image.jpg",
        "image/jpeg",
        "fake image content".toByteArray()
    )

    private fun footstepCreateService(
        clockHolder: ClockHolder,
        footstepDomainService: FootstepDomainService = FootstepDomainService(footstepRepository, clockHolder),
        footstepCreator: FootstepCreator = FootstepCreator(footstepDomainService, footstepRepository, clockHolder),
        imageUploader: ImageUploader = FakeImageUploader("fake-image-url"),
    ): FootstepCreateService {
        return FootstepCreateService(
            footstepDomainService = footstepDomainService,
            footstepCreator = footstepCreator,
            imageNameGenerator = imageNameGenerator,
            imageUploader = imageUploader,
            userReader = userReader,
            footstepRepository = footstepRepository
        )
    }

    private fun footstepDomainService(clockHolder: ClockHolder): FootstepDomainService {
        return FootstepDomainService(footstepRepository, clockHolder)
    }
}