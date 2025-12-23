package side.flab.goforawalk.app.sample

import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.support.image.ImageUploader

@RestController
@RequestMapping("/sample")
class SampleApi(
  private val sampleService: SampleService,
  private val imageUploader: ImageUploader,
) {
  @GetMapping("/hello")
  fun helloWorld(): String {
    return "Hello World!"
  }

  @PostMapping("/hello")
  fun helloWorld(@RequestBody request: HelloRequest): HelloResponse {
    return HelloResponse("Hello ${request.name}!")
  }

  @PostMapping("/")
  fun createSample(@RequestBody request: SampleCreateRequest): SampleEntity {
    return sampleService.createSample(request)
  }

  //    @PostMapping("/upload-image")
  fun uploadSampleImage(
    @RequestPart data: MultipartFile,
  ): ImageResponse {
    val uploadedImageUrl = imageUploader.uploadImage(data, "sample-image")
    return ImageResponse(uploadedImageUrl)
  }

  data class HelloRequest(
    val name: String
  )

  data class HelloResponse(
    val message: String
  )

  data class ImageResponse(
    val imageUrl: String
  )
}
