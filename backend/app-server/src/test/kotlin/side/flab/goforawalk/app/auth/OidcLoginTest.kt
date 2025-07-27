package side.flab.goforawalk.app.auth

import io.restassured.module.mockmvc.RestAssuredMockMvc.given
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.`is`
import org.springframework.core.io.ClassPathResource
import side.flab.goforawalk.app.support.BaseRestAssuredTest
import side.flab.goforawalk.app.support.error.ApiErrorCode
import side.flab.goforawalk.security.oauth2.OAuth2Provider
import side.flab.goforawalk.security.oauth2.OidcLoginRequest
import java.nio.charset.StandardCharsets
import kotlin.test.Test

@Suppress("NonAsciiCharacters")
class OidcLoginTest : BaseRestAssuredTest() {
    @Test
    fun `OIDC 로그인 - 카카오 성공`() {
        val provider = OAuth2Provider.KAKAO
        val loginRequest = OidcLoginRequest(sampleKaKaoIdToken())

        val response = given()
            .log().all()
            .body(loginRequest)
            .contentType("application/json")
            .`when`()
            .post("/api/v1/auth/login/oauth2/{provider}", provider)

        response
            .then()
            .log().all()
            .statusCode(200)
            .body("data", notNullValue())
            .body("data.userId", isA(Int::class.java))
            .body("data.credentials", notNullValue())
            .body("data.credentials.accessToken", not(emptyString()))
            .body("data.credentials.refreshToken", not(emptyString()))
            .body("data.userInfo", notNullValue())
            .body("data.userInfo.email", anyOf(notNullValue(), nullValue()))
            .body("data.userInfo.nickname", not(emptyString()))
    }

    @Test
    fun `OIDC 로그인 - 카카오 실패`() {
        val provider = OAuth2Provider.KAKAO
        val loginRequest = OidcLoginRequest("invalid_id_token")

        val response = given()
            .log().all()
            .body(loginRequest)
            .contentType("application/json")
            .`when`()
            .post("/api/v1/auth/login/oauth2/{provider}", provider)

        response
            .then()
            .log().all()
            .statusCode(401)
            .body("code", `is`(ApiErrorCode.A_4100.name))
            .body("message", not(emptyString()))
    }

    @Test
    fun `OIDC 로그인 - 애플 성공`() {
        val provider = OAuth2Provider.APPLE
        val loginRequest = OidcLoginRequest(sampleAppleIdToken())

        val response = given()
            .log().all()
            .body(loginRequest)
            .contentType("application/json")
            .`when`()
            .post("/api/v1/auth/login/oauth2/{provider}", provider)

        response
            .then()
            .log().all()
            .statusCode(200)
            .body("data", notNullValue())
            .body("data.userId", isA(Int::class.java))
            .body("data.credentials", notNullValue())
            .body("data.credentials.accessToken", not(emptyString()))
            .body("data.credentials.refreshToken", not(emptyString()))
            .body("data.userInfo", notNullValue())
            .body("data.userInfo.email", equalTo("test_email@privaterelay.appleid.com"))
            .body("data.userInfo.nickname", not(emptyString()))
    }

    companion object {
        private const val KAKAO_JWK_SET_RESPONSE = "stub/kakao-jwks.json"
        private const val APPLE_JWK_SET_RESPONSE = "stub/apple-jwks.json"

        val kakaoJwkSetJson = readJsonFile(KAKAO_JWK_SET_RESPONSE)
        val appleJwkSetJson = readJsonFile(APPLE_JWK_SET_RESPONSE)

        fun sampleKaKaoIdToken(): String =
            "eyJraWQiOiJDVVNUT01fS0lEIiwidHlwIjoiSldUIiwiYWxnIjoiUlMyNTYifQ.eyJhdWQiOiJLQUtBT19DTElFTlRfSUQiLCJzdWIiOiJLQUtBT19TVUIiLCJhdXRoX3RpbWUiOjE3NDYxMTQzMTYsImlzcyI6Imh0dHBzOi8va2F1dGgua2FrYW8uY29tIiwiZXhwIjoxODQ2MTU3NTE2LCJpYXQiOjE3NDYxMTQzMTZ9.ppv_TsHVL8Go1M7_TR7JlfwcSqNSltAE15K8ulJOPtqhbTZbejSLGmubY3Ow_lxXA9cH28XaOa_fcBdkmL9SZFChK639aOUbnJyOF8Mn98bF5gP9kmn6GLQoYOBXhXcqo10cYHmWhsHhR0z7I-5NEV57_YEIlIqmzvdXjF-lCrM-3HQtN-0eGzCC6NkPx0Sra1sM9-jLtLw0dX0EVoOpCZgUvzlNWvyd7DqXLe3CNTBtG6uQ54SvKeRVK72S3GxEHCjhpVbTEHHdMMNW3DCQoSB9lbelGn_iEwA3vTqoLnGn6WAkcquXl6cB2EvfJ3VyfR9q31xgk4SMoQGfdysIEQ"

        fun sampleAppleIdToken(): String =
            "eyJhbGciOiJSUzI1NiIsImtpZCI6IkFQUExFX1RFU1RfS0lEIn0.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiQVBQTEVfQ0xJRU5UX0lEIiwiZXhwIjoxODQ2MTU3NTE2LCJpYXQiOjE3NTM2MTM5OTEsInN1YiI6IjAwMDAwMC5hYmNkZWZnaGlqa2xtbm9wcXJzdHV2d3h5emFiY2RlZi4wMDAwIiwiY19oYXNoIjoiT1gxZjRDTGlMMGt5MjI1LVNwQ0dyQSIsImVtYWlsIjoidGVzdF9lbWFpbEBwcml2YXRlcmVsYXkuYXBwbGVpZC5jb20iLCJlbWFpbF92ZXJpZmllZCI6dHJ1ZSwiaXNfcHJpdmF0ZV9lbWFpbCI6dHJ1ZSwiYXV0aF90aW1lIjoxNzUzNjEzOTkxLCJub25jZV9zdXBwb3J0ZWQiOnRydWV9.RoSinJGQr-xPtuFGfppuD_t464lccG-Lci1SmlxCwC56W2s4y1u9KUQJMyPeC1xc9W4SNQK-4uKtayqCzAzWrmee7Xop7lmV_NGFcJ6c8_-_qCdfIKbTYA-o08gSKaRw1elTIYSqXOhFMrDxBNRsdYE6-M0lKQj8yOCpx3tdAOedsft77O_9yFwg6LjE0xPY9VNB0wYRVbC239BxSiceAQepSSahxuhk0LSamGldZInBoE7TdRh1P7lujwrNvy6AK-93zI53-U-Y7dKLvltdBfKUFfV1h2Qtgvu9VEesw_nJqXhOn60naymDsFHAayJjBDkHyNmp_uxae7AiDvlPbA"

        private fun readJsonFile(filePath: String): String {
            return try {
                val resource = ClassPathResource(filePath)
                resource.inputStream.readBytes().toString(StandardCharsets.UTF_8)
            } catch (e: Exception) {
                throw RuntimeException("Failed to read stub file: $filePath", e)
            }
        }
    }
}