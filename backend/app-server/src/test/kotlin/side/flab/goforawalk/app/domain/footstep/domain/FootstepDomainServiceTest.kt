package side.flab.goforawalk.app.domain.footstep.domain

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.FootstepFixture.createFootstep
import side.flab.goforawalk.app.support.fixture.FootstepFixture.deleted
import side.flab.goforawalk.app.support.fixture.FootstepFixture.save
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import java.time.LocalDate
import java.util.stream.Stream
import kotlin.test.Test

class FootstepDomainServiceTest : BaseIntegrationTest() {
    @Autowired
    lateinit var SUT: FootstepDomainService

    @Test
    fun `전체 발자취 개수 조회`() {
        // Arrange
        val user = createSeoulUser("산책왕").save(userRepository)
        createFootstep(user = user, date = LocalDate.of(2024, 1, 1)).save(footstepRepository)
        createFootstep(user = user, date = LocalDate.of(2024, 1, 2)).deleted().save(footstepRepository)
        createFootstep(user = user, date = LocalDate.of(2024, 1, 3)).save(footstepRepository)

        // Act
        val totalFootstepCount = SUT.countTotalFoostepsOfUser(user.id!!)

        // Assert
        assertThat(totalFootstepCount).isEqualTo(2);
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("streakDaysTestData")
    fun `연속 발자취 일수 조회`(
        testName: String,
        footstepDates: List<LocalDate>,
        expectedStreakDays: Long,
    ) {
        // Arrange
        val user = createSeoulUser("산책왕").save(userRepository)
        footstepDates.forEach { date ->
            createFootstep(user = user, date = date).save(footstepRepository)
        }

        // Act
        val footstepStreakDays = SUT.countFootstepStreakDays(user.id!!)

        // Assert
        assertThat(footstepStreakDays).isEqualTo(expectedStreakDays);
    }

    companion object {
        @JvmStatic
        fun streakDaysTestData(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(
                    "2일 연속 발자국",
                    listOf(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                    ),
                    2
                ),
                Arguments.of(
                    "3일 연속 발자국",
                    listOf(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                        LocalDate.of(2024, 1, 3)
                    ),
                    3
                ),
                Arguments.of(
                    "하루 걸러서 발자국 (연속 1일)",
                    listOf(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 3),
                        LocalDate.of(2024, 1, 5)
                    ),
                    1
                ),
                Arguments.of(
                    "2일 연속 후 공백, 다시 2일 연속 (최대 2일)",
                    listOf(
                        LocalDate.of(2024, 1, 1),
                        LocalDate.of(2024, 1, 2),
                        LocalDate.of(2024, 1, 4),
                        LocalDate.of(2024, 1, 5)
                    ),
                    2
                ),
                Arguments.of(
                    "발자국 없음",
                    emptyList<LocalDate>(),
                    0
                ),
                Arguments.of(
                    "하루만 발자국",
                    listOf(LocalDate.of(2024, 1, 1)),
                    1
                ),
                Arguments.of(
                    "월경계 연속 발자국",
                    listOf(
                        LocalDate.of(2024, 1, 30),
                        LocalDate.of(2024, 1, 31),
                        LocalDate.of(2024, 2, 1),
                        LocalDate.of(2024, 2, 2)
                    ),
                    4
                )
            )
        }
    }
}