package side.flab.goforawalk.app.support.gcp

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("gcp")
data class GcpProperties(
  val projectId: String,
  val storage: CloudStorageProperties,
)

data class CloudStorageProperties(
  val bucket: String,
  val credentials: CloudStorageCredentials,
)

data class CloudStorageCredentials(
  val encodedKey: String,
)