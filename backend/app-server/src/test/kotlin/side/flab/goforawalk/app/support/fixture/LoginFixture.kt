package side.flab.goforawalk.app.support.fixture

import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.domain.User

object LoginFixture {
    fun generateAT(
        provider: AppAuthTokenProvider,
        user: User,
    ): String {
        val appAuthToken = provider.generate(AppUserDetails(user.id!!, user.nickname))
        return appAuthToken.accessToken
    }

    fun sampleKakaoIdToken(): String {
        return "eyJraWQiOiJDVVNUT01fS0lEIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJLQUtBT19DTElFTlRfSUQiLCJzdWIiOiJLQUtBT19TVUIiLCJhdXRoX3RpbWUiOjE3NDYxMTQzMTYsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwiZXhwIjoxODQ2MTU3NTE2LCJpYXQiOjE3NDYxMTQzMTZ9.ppv_TsHVL8Go1M7_TR7JlfwcSqNSltAE15K8ulJOPtqhbTZbejSLGmubY3Ow_lxXA9cH28XaOa_fcBdkmL9SZFChK639aOUbnJyOF8Mn98bF5gP9kmn6GLQoYOBXhXcqo10cYHmWhsHhR0z7I-5NEV57_YEIlIqmzvdXjF-lCrM-3HQtN-0eGzCC6NkPx0Sra1sM9-jLtLw0dX0EVoOpCZgUvzlNWvyd7DqXLe3CNTBtG6uQ54SvKeRVK72S3GxEHCjhpVbTEHHdMMNW3DCQoSB9lbelGn_iEwA3vTqoLnGn6WAkcquXl6cB2EvfJ3VyfR9q31xgk4SMoQGfdysIEQ"
    }
}