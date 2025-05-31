package side.flab.goforawalk.app.support.gcp

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(GcpProperties::class)
class GcpConfig