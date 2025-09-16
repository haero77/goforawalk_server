package side.flab.goforawalk.app.api.user

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import side.flab.goforawalk.app.domain.profile.application.ProfileUpdateService
import side.flab.goforawalk.app.domain.profile.application.dto.UserNicknameUpdateRequest
import side.flab.goforawalk.app.domain.user.application.UserDeleteService
import side.flab.goforawalk.app.support.base.BaseApiController
import side.flab.goforawalk.app.support.web.CurrentUserId

@RestController
class UserController(
    private val userDeleteService: UserDeleteService,
    private val profileUpdateService: ProfileUpdateService
) : BaseApiController() {

    @DeleteMapping("/v1/users/me")
    fun deleteUser(@CurrentUserId userId: Long): ResponseEntity<Void> {
        userDeleteService.delete(userId)
        return ResponseEntity.ok().build()
    }

    @PatchMapping("/v1/users/me/nickname")
    fun updateProfile(
        @CurrentUserId userId: Long,
        @RequestBody @Valid request: UserNicknameUpdateRequest
    ): ResponseEntity<Void> {
        profileUpdateService.updateNickname(userId, request)
        return ResponseEntity.noContent().build()
    }
}