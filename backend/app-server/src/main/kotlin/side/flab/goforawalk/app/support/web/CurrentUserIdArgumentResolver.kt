package side.flab.goforawalk.app.support.web

import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import side.flab.goforawalk.app.auth.filter.JwtAuthenticationToken

@Component
class CurrentUserIdArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.hasParameterAnnotation(CurrentUserId::class.java) &&
                parameter.parameterType == Long::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Long {
        return when (val authentication = SecurityContextHolder.getContext().authentication) {
            is JwtAuthenticationToken -> authentication.principal.value
            else -> throw IllegalArgumentException("Current user ID is not available in the security context(authentication is not JwtAuthenticationToken)")
        }
    }
}