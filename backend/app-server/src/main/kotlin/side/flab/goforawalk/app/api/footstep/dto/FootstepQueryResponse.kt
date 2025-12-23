package side.flab.goforawalk.app.api.footstep.dto

import side.flab.goforawalk.app.domain.footstep.application.dto.FootstepDetailResponse

data class FootstepQueryResponse(
  val footsteps: List<FootstepDetailResponse>,
)