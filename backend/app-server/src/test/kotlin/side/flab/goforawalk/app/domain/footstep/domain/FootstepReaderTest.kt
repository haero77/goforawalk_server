package side.flab.goforawalk.app.domain.footstep.domain

import org.assertj.core.api.Assertions.assertThat
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import side.flab.goforawalk.app.support.mock.FakeClockHolder
import java.time.LocalDate
import kotlin.test.Test

class FootstepReaderTest : BaseIntegrationTest() {
    @Test
    fun isUserAlreadyCreatedFootstepToday() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val footstep = Footstep(
            user = user,
            date = LocalDate.of(2025, 5, 24),
            imageUrl = "https://example.com/image.jpg",
        )
        footstepRepository.save(footstep)

        val clockHolder = FakeClockHolder(LocalDate.of(2025, 5, 24))
        val SUT = FootstepReader(footstepRepository, clockHolder)

        // when
        val actual = SUT.isUserAlreadyCreatedFootstepToday(user)

        // then
        assertThat(actual).isTrue()
    }
}