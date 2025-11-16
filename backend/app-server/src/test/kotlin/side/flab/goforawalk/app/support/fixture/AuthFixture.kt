package side.flab.goforawalk.app.support.fixture

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import io.restassured.module.mockmvc.specification.MockMvcRequestSpecification
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import side.flab.goforawalk.app.auth.AppAuthTokenProvider
import side.flab.goforawalk.app.domain.user.application.AppUserDetails
import side.flab.goforawalk.app.domain.user.domain.User

@Component
class AuthFixture(
  private val authTokenProvider: AppAuthTokenProvider
) {
  companion object {
    const val SAMPLE_KAKAO_ID_TOKEN =
      "eyJraWQiOiJDVVNUT01fS0lEIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJLQUtBT19DTElFTlRfSUQiLCJzdWIiOiJLQUtBT19TVUIiLCJhdXRoX3RpbWUiOjE3NDYxMTQzMTYsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwiZXhwIjoxODQ2MTU3NTE2LCJpYXQiOjE3NDYxMTQzMTZ9.ppv_TsHVL8Go1M7_TR7JlfwcSqNSltAE15K8ulJOPtqhbTZbejSLGmubY3Ow_lxXA9cH28XaOa_fcBdkmL9SZFChK639aOUbnJyOF8Mn98bF5gP9kmn6GLQoYOBXhXcqo10cYHmWhsHhR0z7I-5NEV57_YEIlIqmzvdXjF-lCrM-3HQtN-0eGzCC6NkPx0Sra1sM9-jLtLw0dX0EVoOpCZgUvzlNWvyd7DqXLe3CNTBtG6uQ54SvKeRVK72S3GxEHCjhpVbTEHHdMMNW3DCQoSB9lbelGn_iEwA3vTqoLnGn6WAkcquXl6cB2EvfJ3VyfR9q31xgk4SMoQGfdysIEQ"
    const val SAMPLE_APPLE_ID_TOKEN =
      "eyJhbGciOiJSUzI1NiIsImtpZCI6IkFQUExFX1RFU1RfS0lEIn0.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiQVBQTEVfQ0xJRU5UX0lEIiwiZXhwIjoxODQ2MTU3NTE2LCJpYXQiOjE3NTM2MTM5OTEsInN1YiI6IjAwMDAwMC5hYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFiY2RlZi4wMDAwIiwiY19oYXNoIjoiT1gxZjRDTGlMMGt5MjI1LVNwQ0dyQSIsImVtYWlsIjoidGVzdF9lbWFpbEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNfcHJpdmF0ZV9lbWFpbCI6dHJ1ZSwiYXV0aF90aW1lIjoxNzUzNjEzOTkxLCJub25jZV9zdXBwb3J0ZWQiOnRydWV9.RoSinJGQr-xPtuFGfppuD_t464lccG-Lci1SmlxCwC56W2s4y1u9KUQJMyPeC1xc9W4SNQK-4uKtayqCzAzWrmee7Xop7lmV_NGFcJ6c8_-_qCdfIKbTYA-o08gSKaRw1elTIYSqXOhFMrDxBNRsdYE6-M0lKQj8yOCpx3tdAOedsft77O_9yFwg6LjE0xPY9VNB0wYRVbC239BxSiceAQepSSahxuhk0LSamGldZInBoE7TdRh1P7lujwrNvy6AK-93zI53-U-Y7dKLvltdBfKUFfV1h2Qtgvu9VEesw_nJqXhOn60naymDsFHAayJjBDkHyNmp_uxae7AiDvlPbA"

    fun sampleKaKaoIdToken(): String = SAMPLE_KAKAO_ID_TOKEN
    fun sampleAppleIdToken(): String = SAMPLE_APPLE_ID_TOKEN
  }

  fun givenAuthenticatedUser(user: User): MockMvcRequestSpecification {
    return givenAuthorizationBearerHeader(
      generateAT(user)
    )
  }

  fun givenAuthorizationBearerHeader(token: String): MockMvcRequestSpecification {
    return given()
      .header(AUTHORIZATION, "Bearer $token")
  }

  private fun generateAT(
    user: User,
  ): String {
    val appAuthToken = authTokenProvider.generate(AppUserDetails(user.id!!, user.nickname))
    return appAuthToken.accessToken
  }
}