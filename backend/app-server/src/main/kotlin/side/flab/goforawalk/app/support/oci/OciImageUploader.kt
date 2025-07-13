package side.flab.goforawalk.app.support.oci

import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.requests.PutObjectRequest
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.stereotype.Component
import org.springframework.web.multipart.MultipartFile
import side.flab.goforawalk.app.support.image.ImageUploader
import java.io.IOException

private val log = KotlinLogging.logger {}

@Component
class OciImageUploader(
    private val objectStorageClient: ObjectStorage, // 1. Bean으로 등록된 OCI 오브젝트 스토리지 클라이언트 주입
    private val ociProperties: OciProperties        // 2. OCI 설정 정보 주입
) : ImageUploader {

    override fun uploadImage(
        file: MultipartFile,
        fileName: String
    ): String {
        try {
            // PutObjectRequest 생성
            val request = PutObjectRequest.builder()
                .namespaceName(ociProperties.namespace)
                .bucketName(ociProperties.bucketName)
                .objectName(fileName)
                .contentType(file.contentType)
                .contentLength(file.size)
                .putObjectBody(file.inputStream)
                .build()

            // 파일 업로드 실행
            objectStorageClient.putObject(request)
            log.info { "Successfully uploaded file: $fileName to bucket: ${ociProperties.bucketName}" }

            // 업로드된 파일의 URL 생성하여 반환
            return buildObjectUrl(fileName)
        } catch (e: IOException) {
            log.error(e) { "File upload failed for $fileName" }
            throw IllegalStateException("파일 업로드에 실패했습니다.", e)
        }
    }

    private fun buildObjectUrl(objectName: String): String {
        return "https://objectstorage.${ociProperties.region}.oraclecloud.com/n/${ociProperties.namespace}/b/${ociProperties.bucketName}/o/${objectName}"
    }
}