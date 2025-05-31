package side.flab.goforawalk.app.domain.footstep.domain

import org.assertj.core.api.Assertions
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate
import kotlin.test.BeforeTest
import kotlin.test.Test

class FootstepRepositoryTest : BaseIntegrationTest() {
    lateinit var SUT: FootstepRepository

    @BeforeTest
    fun setUp() {
        SUT = footstepRepository
    }

    @Test
    fun existsByUserIdAndDate_false() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val footstep = Footstep(
            user = user,
            date = LocalDate.of(2025, 5, 23),
            imageUrl = "https://example.com/image.jpg",
        )
        footstepRepository.save(footstep)

        // when
        val actual = SUT.existsByUserIdAndDate(
            userId = user.id!!,
            date = LocalDate.of(2025, 5, 24)
        )

        // then
        Assertions.assertThat(actual).isFalse()
    }

    @Test
    fun existsByUserIdAndDate_true() {
        val user = createSeoulUser(nickname = "산책왕").save(userRepository)
        val footstep = Footstep(
            user = user,
            date = LocalDate.of(2025, 5, 24),
            imageUrl = "https://example.com/image.jpg",
        )
        footstepRepository.save(footstep)

        // when
        val actual = SUT.existsByUserIdAndDate(
            userId = user.id!!,
            date = LocalDate.of(2025, 5, 24)
        )

        // then
        Assertions.assertThat(actual).isTrue()
    }
}