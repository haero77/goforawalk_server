package side.flab.goforawalk.app.domain.user.domain

import org.springframework.stereotype.Component

@Component
class UserReader(
    private val userRepository: UserRepository
) {
    fun getById(userId: Long): User {
        return getByIdInternal(userId)
    }

    private fun getByIdInternal(userId: Long): User {
        return userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("Cannot find user for id=$userId") }
    }
}