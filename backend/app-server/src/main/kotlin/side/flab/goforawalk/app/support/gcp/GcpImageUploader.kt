package side.flab.goforawalk.app.support.gcp

import com.google.cloud.storage.BlobId
import com.google.cloud.storage.BlobInfo
import com.google.cloud.storage.Storage
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.support.image.ImageUploader

private val log = KotlinLogging.logger {}

@Component
class GcpImageUploader(
  private val gcpProperties: GcpProperties,
  private val storage: Storage
) : ImageUploader {
  /**
   * 업로드된 이미지를 GCP Cloud Storage에 저장하고, 이미지 URL을 반환한다.
   * 같은 이름의 파일이 이미 존재하는 경우 덮어쓴다.
   */
  override fun uploadImage(file: MultipartFile, fileName: String): String {
    if (file.isEmpty) {
      throw IllegalArgumentException("File is empty $file")
    }

    val originalFilename = file.originalFilename ?: "untitled"
    val fileExtension = originalFilename.substringAfterLast('.', "")
    val fileNameToBeUploaded = "${fileName}${if (fileExtension.isNotEmpty()) ".$fileExtension" else ""}"

    val blobId = BlobId.of(gcpProperties.storage.bucket, fileNameToBeUploaded)
    val blobInfo = BlobInfo.newBuilder(blobId)
      .setContentType(file.contentType)
      .build()

    storage.create(blobInfo, file.bytes)
    val imageUrl = "https://storage.googleapis.com/${gcpProperties.storage.bucket}/$fileNameToBeUploaded"
    log.info { "Successfully uploaded image '${originalFilename}' to GCS. URL: $imageUrl" }
    return imageUrl
  }
}