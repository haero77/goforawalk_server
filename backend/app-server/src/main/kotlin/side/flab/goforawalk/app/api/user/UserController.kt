package side.flab.goforawalk.app.api.user

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.RestController
import side.flab.goforawalk.app.domain.user.application.UserDeleteService
import side.flab.goforawalk.app.support.base.BaseApiController
import side.flab.goforawalk.app.support.web.CurrentUserId

@RestController
class UserController(
    private val userDeleteService: UserDeleteService
) : BaseApiController() {

    @DeleteMapping("/v1/users/me")
    fun deleteUser(@CurrentUserId userId: Long): ResponseEntity<Void> {
        userDeleteService.delete(userId)
        return ResponseEntity.ok().build()
    }
}