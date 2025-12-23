package side.flab.goforawalk.security.oauth2.validator

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.jwt.JwtDecoder
import side.flab.goforawalk.security.oauth2.OidcIdTokenDecoderConfig

@Configuration
class OidcIdTokenValidatorConfig {
  @Bean
  fun oidcIdTokenValidatorFactory(
    validators: Set<OidcIdTokenValidator>,
  ): OidcIdTokenValidatorFactory {
    return OidcIdTokenValidatorFactory(validators)
  }

  @Bean
  fun kakaoOidcIdTokenValidator(
    @Qualifier(OidcIdTokenDecoderConfig.KAKAO_ID_TOKEN_DECODER_BEAN_NAME)
    kakaoIdTokenDecoder: JwtDecoder,
  ): OidcIdTokenValidator {
    return KakaoIdTokenValidator(kakaoIdTokenDecoder)
  }

  @Bean
  fun appleOidcIdTokenValidator(
    @Qualifier(OidcIdTokenDecoderConfig.APPLE_ID_TOKEN_DECODER_BEAN_NAME)
    appleIdTokenDecoder: JwtDecoder,
  ): OidcIdTokenValidator {
    return AppleIdTokenValidator(appleIdTokenDecoder)
  }
}