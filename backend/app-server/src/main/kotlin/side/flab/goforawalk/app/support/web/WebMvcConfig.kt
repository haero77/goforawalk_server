package side.flab.goforawalk.app.support.web

import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebMvcConfig(
  private val currentUserIdArgumentResolver: CurrentUserIdArgumentResolver,
) : WebMvcConfigurer {

  override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
    resolvers.add(currentUserIdArgumentResolver)
  }
}