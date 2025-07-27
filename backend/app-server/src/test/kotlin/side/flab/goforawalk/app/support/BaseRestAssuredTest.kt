package side.flab.goforawalk.app.support

import com.github.tomakehurst.wiremock.client.WireMock.*
import io.github.oshai.kotlinlogging.KotlinLogging
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.context.ApplicationContext
import org.springframework.test.web.servlet.MockMvc
import side.flab.goforawalk.app.auth.OidcLoginTest

private val log = KotlinLogging.logger {}

/**
 * @AutoConfigureWireMock: wiremock.server.port 프로퍼티 자동으로 등록
 */
@AutoConfigureMockMvc // Spring Security가 클래스패스에 있으면 자동으로 Security 필터를 포함.
@SpringBootTest(webEnvironment = WebEnvironment.MOCK) // webEnvironment = WebEnvironment.MOCK (기본값)
@AutoConfigureWireMock(port = 0) // port = 0: 동적 포트 사용
abstract class BaseRestAssuredTest : BaseIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @BeforeEach
    fun setup() {
        log.info { "[Context ID] ${applicationContext.id}" }
        log.info { "[Context Hash] ${applicationContext.hashCode()}" }

        RestAssuredMockMvc.mockMvc(mockMvc)
        RestAssuredMockMvc.enableLoggingOfRequestAndResponseIfValidationFails()

        setUpOidc()
    }

    fun setUpOidc() {
        stubFor(
            get(urlEqualTo("/kakao/.well-known/jwks.json"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OidcLoginTest.kakaoJwkSetJson)
                )
        )
        stubFor(
            get(urlEqualTo("/auth/keys"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(OidcLoginTest.appleJwkSetJson)
                )
        )
    }
}