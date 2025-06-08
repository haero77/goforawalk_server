package side.flab.goforawalk.app.api.profile

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import side.flab.goforawalk.app.domain.profile.application.ProfileQueryResponse
import side.flab.goforawalk.app.domain.profile.application.ProfileQueryService
import side.flab.goforawalk.app.support.base.BaseApiController
import side.flab.goforawalk.app.support.response.ApiResponse
import side.flab.goforawalk.app.support.web.CurrentUserId

@RestController
class ProfileController(
    private val profileQueryService: ProfileQueryService
) : BaseApiController() {
    @GetMapping("/v1/profile")
    fun getProfile(
        @CurrentUserId userId: Long,
    ): ApiResponse<ProfileQueryResponse> {
        val response = profileQueryService.queryProfileByUserId(userId)
        return ApiResponse(response)
    }
}