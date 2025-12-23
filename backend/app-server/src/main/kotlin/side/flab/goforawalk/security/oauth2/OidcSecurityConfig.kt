package side.flab.goforawalk.security.oauth2

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import side.flab.goforawalk.security.oauth2.validator.OidcIdTokenValidatorFactory

@Configuration
class OidcSecurityConfig {
  @Bean
  fun oAuth2OidcAuthenticationProvider(
    idTokenValidatorFactory: OidcIdTokenValidatorFactory,
    userService: OidcUserService,
  ): OidcAuthenticationProvider {
    return OidcAuthenticationProvider(idTokenValidatorFactory, userService)
  }
}