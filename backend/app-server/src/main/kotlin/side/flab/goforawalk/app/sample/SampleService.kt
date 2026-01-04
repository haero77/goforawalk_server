package side.flab.goforawalk.app.sample

import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val log = KotlinLogging.logger {}

@Service
class SampleService(
  private val sampleRepository: SampleRepository
) {
  @Transactional
  fun createSample(request: SampleCreateRequest): SampleEntity {
    val sampleEntity = SampleEntity(
      name = request.name
    )

    return sampleRepository.save(sampleEntity)
  }

  fun throwSampleError(message: String?) {
    throw RuntimeException(message ?: "샘플 에러 발생")
  }
}

data class SampleCreateRequest(
  val name: String
)