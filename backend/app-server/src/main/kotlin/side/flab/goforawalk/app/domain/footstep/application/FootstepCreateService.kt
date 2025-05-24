package side.flab.goforawalk.app.domain.footstep.application

import org.springframework.stereotype.Service
import side.flab.goforawalk.app.domain.footstep.application.dto.FootStepCreateRequest
import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse
import side.flab.goforawalk.app.domain.footstep.domain.FootstepCreator
import side.flab.goforawalk.app.domain.footstep.domain.FootstepImageNameGenerator
import side.flab.goforawalk.app.domain.footstep.domain.FootstepReader
import side.flab.goforawalk.app.domain.footstep.domain.FootstepRepository
import side.flab.goforawalk.app.domain.footstep.domain.dto.FootstepCreateDto
import side.flab.goforawalk.app.domain.user.domain.User
import side.flab.goforawalk.app.domain.user.domain.UserReader
import side.flab.goforawalk.app.support.image.ImageUploader
import side.flab.goforawalk.app.support.util.ClockHolder

@Service
class FootstepCreateService(
    private val footstepCreator: FootstepCreator,
    private val footstepReader: FootstepReader,
    private val imageNameGenerator: FootstepImageNameGenerator,
    private val imageUploader: ImageUploader,
    private val clockHolder: ClockHolder,
    private val userReader: UserReader,
    private val footstepRepository: FootstepRepository
) {
    fun createFootstep(request: FootStepCreateRequest): FootstepDetailResponse {
        val user = userReader.getById(request.userId)
        validateAbleToCreateFootstep(user)

        val newFootstepId = createFootstep(user, request)

        val footstep = footstepRepository.findByIdFetchJoinUser(newFootstepId)!!
        return FootstepDetailResponse.from(footstep)
    }

    private fun validateAbleToCreateFootstep(user: User) {
        if (footstepReader.isUserAlreadyCreatedFootstepToday(user)) {
            throw IllegalStateException(
                "User already created a footstep for today: userId=${user.id}, date=${user.getLocalDate(clockHolder)}"
            )
        }
    }

    private fun createFootstep(
        user: User,
        request: FootStepCreateRequest
    ): Long {
        val imageName = imageNameGenerator.generate(user.id!!)
        val imageUrl = imageUploader.uploadImage(request.imageFile, imageName)

        val createDto = FootstepCreateDto(user, imageUrl, request.content)
        return footstepCreator.create(createDto)
    }
}