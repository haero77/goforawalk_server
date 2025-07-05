package side.flab.goforawalk.app.domain.user.domain

import org.assertj.core.api.Assertions.assertThat
import side.flab.goforawalk.app.support.BaseIntegrationTest
import side.flab.goforawalk.app.support.fixture.UserFixture.createSeoulUser
import side.flab.goforawalk.app.support.fixture.UserFixture.save
import kotlin.test.Test

class UserIntegrationTest : BaseIntegrationTest() {
    @Test
    fun `유저 삭제시 providerUsername delete_user_{userId}로 변경된다`() {
        // Arrange
        val user = createSeoulUser("삭제될 유저").save(userRepository) // 식별자가 필요하여 통합 테스트 사용

        // Act
        user.delete()

        // Assert
        assertThat(user.providerUsername).isEqualTo("deleted_user_${user.id}")
    }
}