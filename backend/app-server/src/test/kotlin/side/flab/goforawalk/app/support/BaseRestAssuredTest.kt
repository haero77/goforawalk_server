package side.flab.goforawalk.app.support

import com.github.tomakehurst.wiremock.client.WireMock.*
import io.restassured.module.mockmvc.RestAssuredMockMvc
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.test.web.servlet.MockMvc
import side.flab.goforawalk.app.auth.OidcLoginTest
import kotlin.test.BeforeTest

@AutoConfigureMockMvc // Spring Security가 클래스패스에 있으면 자동으로 Security 필터를 포함.
@SpringBootTest
@AutoConfigureWireMock(port = 8089) // wiremock 서버를 8089로 실제로 띄움.
abstract class BaseRestAssuredTest : BaseIntegrationTest() {
    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeTest
    fun setup() {
        RestAssuredMockMvc.mockMvc(mockMvc)
    }

    @BeforeTest
    fun setUpOidc() {
        stubFor(
            get(urlEqualTo("/kakao/.well-known/jwks.json"))
                .willReturn(
                    aResponse()
                        .withStatus(200)
                        .withBody(OidcLoginTest.kakaoJwkSetJson)
                )
        )
    }
}