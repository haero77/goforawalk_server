package side.flab.goforawalk.app.domain.user.application

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import side.flab.goforawalk.app.domain.user.domain.UserReader

@Service
class UserDeleteService(
    private val userReader: UserReader
) {

    @Transactional
    fun delete(userId: Long) {
        val user = userReader.getById(userId)
        user.delete()
    }
}