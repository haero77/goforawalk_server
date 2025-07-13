package side.flab.goforawalk.app.support.oci

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "oci")
data class OciProperties(
    val region: String,
    val namespace: String,
    val bucketName: String,
    val configPath: String, // ~/.oci/config 파일의 경로
    val configProfile: String
)