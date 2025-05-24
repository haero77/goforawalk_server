package side.flab.goforawalk.app.support.mock

import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.support.image.ImageUploader

class FakeImageUploader(
    private val imageUrl: String
) : ImageUploader {
    override fun uploadImage(file: MultipartFile, fileName: String): String {
        return imageUrl
    }
}
