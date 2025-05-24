package side.flab.goforawalk.app.domain.footstep.application.dto

import org.springframework.web.multipart.MultipartFile

data class FootStepCreateRequest(
    val userId: Long,
    val imageFile: MultipartFile,
    val content: String?,
)