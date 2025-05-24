package side.flab.goforawalk.app.api.footstep

import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.domain.footstep.application.FootstepCreateService
import side.flab.goforawalk.app.domain.footstep.application.dto.FootStepCreateRequest
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse
import side.flab.goforawalk.app.support.base.BaseApiController
import side.flab.goforawalk.app.support.response.ApiResponse

@RestController
class FootstepController(
    private val footstepCreateService: FootstepCreateService
) : BaseApiController() {

    @PostMapping("/v1/footsteps")
    fun createFootstep(
        userId: Long, // MethodArgumentResolver를 통해 자동으로 주입
        @RequestPart("data") data: MultipartFile, // image file
        @RequestPart("content") content: String?
    ): ApiResponse<FootstepDetailResponse> {
        val request = FootStepCreateRequest(
            userId = userId,
            imageFile = data,
            content = content,
        )
        val footstepDetailResponse = footstepCreateService.createFootstep(request)
        return ApiResponse(footstepDetailResponse)
    }
}