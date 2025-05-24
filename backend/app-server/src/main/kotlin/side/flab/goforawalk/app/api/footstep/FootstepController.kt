package side.flab.goforawalk.app.api.footstep

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.api.footstep.dto.FootstepQueryResponse
import side.flab.goforawalk.app.domain.footstep.application.FootstepCreateService
import side.flab.goforawalk.app.domain.footstep.application.FootstepDeleteService
import side.flab.goforawalk.app.domain.footstep.application.FootstepQueryService
import side.flab.goforawalk.app.domain.footstep.application.dto.FootStepCreateRequest
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse
import side.flab.goforawalk.app.support.base.BaseApiController
import side.flab.goforawalk.app.support.response.ApiResponse

@RestController
class FootstepController(
    private val footstepCreateService: FootstepCreateService,
    private val footstepQueryService: FootstepQueryService,
    private val deleteService: FootstepDeleteService,
) : BaseApiController() {

    @GetMapping("/v1/footsteps")
    fun getFootsteps(
        userId: Long, // todo MethodArgumentResolver를 통해 자동으로 주입
    ): ApiResponse<FootstepQueryResponse> {
        val footsteps = footstepQueryService.findAllFootStepsOfUser(userId)
        return ApiResponse(FootstepQueryResponse(footsteps))
    }

    @PostMapping("/v1/footsteps")
    fun createFootstep(
        userId: Long, // todo MethodArgumentResolver를 통해 자동으로 주입
        @RequestPart("data") data: MultipartFile, // image file
        @RequestPart("content") content: String?,
    ): ApiResponse<FootstepDetailResponse> {
        val request = FootStepCreateRequest(
            userId = userId,
            imageFile = data,
            content = content,
        )
        val footstepDetailResponse = footstepCreateService.createFootstep(request)
        return ApiResponse(footstepDetailResponse)
    }

    @DeleteMapping("/v1/footsteps/{footstepId}")
    fun deleteFootstep(
        @PathVariable footstepId: Long,
    ) {
        deleteService.delete(footstepId)
    }
}