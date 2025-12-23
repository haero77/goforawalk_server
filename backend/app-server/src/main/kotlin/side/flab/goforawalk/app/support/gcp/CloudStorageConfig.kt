package side.flab.goforawalk.app.support.gcp

import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.storage.Storage
import com.google.cloud.storage.StorageOptions
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.ByteArrayInputStream
import java.util.*

private val log = KotlinLogging.logger {}

@Configuration
class CloudStorageConfig(
  private val properties: GcpProperties,
) {

  @Bean
  fun storage(): Storage {
    val storageBuilder = StorageOptions.newBuilder()

    storageBuilder.setProjectId(properties.projectId)
    val encodedKey = properties.storage.credentials.encodedKey
    val decodedKey = decodeCredentials(encodedKey)
    ByteArrayInputStream(decodedKey).use { inputStream ->
      storageBuilder.setCredentials(
        GoogleCredentials.fromStream(inputStream)
      )
    }

    val storageService = storageBuilder.build().service
    log.info { "GCP Cloud Storage initialized with project ID: ${properties.projectId}" }
    return storageService
  }

  private fun decodeCredentials(encodedKey: String): ByteArray {
    return Base64.getDecoder().decode(encodedKey)
  }
}