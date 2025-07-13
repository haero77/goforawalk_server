package side.flab.goforawalk.app.support.oci

import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider
import com.oracle.bmc.objectstorage.ObjectStorage
import com.oracle.bmc.objectstorage.ObjectStorageClient
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.IOException

private val log = KotlinLogging.logger {}

@Configuration
@EnableConfigurationProperties(OciProperties::class)
class OciConfig {
    @Bean
    @Throws(IOException::class)
    fun objectStorageClient(ociProperties: OciProperties): ObjectStorage {
        // OCI 설정 파일 경로를 OS가 이해하는 절대 경로로 변환.
        // `~` (물결표시)는 사용자의 홈 디렉토리를 의미하는 단축 경로이지만, OCI SDK는 이를 직접 해석 불가.
        // 따라서 `System.getProperty("user.home")`을 통해 실제 홈 디렉토리의 전체 경로(예: /home/username)로 치환.
        val configPath = ociProperties.configPath.replace("~", System.getProperty("user.home"))
        log.info { ">>> Using $configPath" }

        // OCI 설정 파일(~/.oci/config)을 사용하여 인증 정보를 생성합니다.
        val provider = ConfigFileAuthenticationDetailsProvider(
            configPath,
            ociProperties.configProfile
        )

        // 인증 정보로 오라클 Object Storage 클라이언트를 생성하여 Spring Bean으로 등록
        return ObjectStorageClient.builder()
            .region(ociProperties.region)
            .build(provider)
    }
}