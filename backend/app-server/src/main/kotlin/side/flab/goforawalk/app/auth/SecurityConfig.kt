package side.flab.goforawalk.app.auth

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import side.flab.goforawalk.app.auth.filter.JwtAuthenticationFailureHandler
import side.flab.goforawalk.app.auth.filter.JwtAuthenticationFilter
import side.flab.goforawalk.app.auth.filter.RefreshTokenAuthenticationFilter
import side.flab.goforawalk.app.auth.filter.RefreshTokenAuthenticationProvider
import side.flab.goforawalk.security.oauth2.EnableOAuth2OidcConfiguration
import side.flab.goforawalk.security.oauth2.OidcAuthenticationProvider
import side.flab.goforawalk.security.oauth2.OidcLoginAuthenticationFilter

@Configuration
@EnableOAuth2OidcConfiguration
@EnableConfigurationProperties(JwtProperties::class)
class SecurityConfig {
  @Bean
  fun securityFilterChain(
    http: HttpSecurity,
    oidcLoginAuthenticationFilter: OidcLoginAuthenticationFilter,
    oidcAuthenticationProvider: OidcAuthenticationProvider,
    refreshTokenAuthenticationFilter: RefreshTokenAuthenticationFilter,
    refreshTokenAuthenticationProvider: RefreshTokenAuthenticationProvider,
    jwtAuthenticationFilter: JwtAuthenticationFilter,
  ): SecurityFilterChain {
    http
      .csrf { csrf -> csrf.disable() }
      .authorizeHttpRequests { auth ->
        auth
          .requestMatchers("/docs/**", "/sample/**", "/error").permitAll()
          .requestMatchers("/api/v1/auth/login/oauth2/**").permitAll()  // OIDC 로그인 허용
          .requestMatchers("/api/v1/auth/token/refresh").permitAll()  // 토큰 갱신 허용
          .requestMatchers("/api/**").authenticated()
          .anyRequest().authenticated()
      }
      .addFilterBefore(
        oidcLoginAuthenticationFilter,
        UsernamePasswordAuthenticationFilter::class.java
      ) // todo 필터 위치 조정(usernamePasswordAuthenticationFilter 는 미사용.)
      .addFilterBefore(refreshTokenAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
      .authenticationProvider(oidcAuthenticationProvider)
      .authenticationProvider(refreshTokenAuthenticationProvider)

    return http.build()
  }

  @Bean
  fun oauth2OidcLoginAuthenticationFilter(
    objectMapper: ObjectMapper,
    oidcAuthenticationProvider: OidcAuthenticationProvider,
    userLoginSuccessHandler: UserLoginSuccessHandler,
    userLoginFailureHandler: UserLoginFailureHandler
  ): OidcLoginAuthenticationFilter {
    val authenticationManager = ProviderManager(listOf(oidcAuthenticationProvider))
    return OidcLoginAuthenticationFilter(
      objectMapper,
      authenticationManager,
      userLoginSuccessHandler,
      userLoginFailureHandler
    )
  }

  @Bean
  fun refreshTokenAuthenticationFilter(
    refreshTokenAuthenticationProvider: RefreshTokenAuthenticationProvider,
    refreshTokenSuccessHandler: RefreshTokenSuccessHandler,
    refreshTokenFailureHandler: RefreshTokenFailureHandler
  ): RefreshTokenAuthenticationFilter {
    val authenticationManager = ProviderManager(listOf(refreshTokenAuthenticationProvider))
    return RefreshTokenAuthenticationFilter(
      authenticationManager,
      refreshTokenSuccessHandler,
      refreshTokenFailureHandler
    )
  }

  @Bean
  fun jwtAuthenticationFilter(
    authTokenProvider: AppAuthTokenProvider,
    failureHandler: JwtAuthenticationFailureHandler
  ): JwtAuthenticationFilter {
    return JwtAuthenticationFilter(authTokenProvider, failureHandler)
  }
}