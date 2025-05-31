package side.flab.goforawalk.app.support

import io.restassured.RestAssured
import io.restassured.builder.RequestSpecBuilder
import io.restassured.builder.ResponseSpecBuilder
import io.restassured.config.LogConfig
import io.restassured.config.RestAssuredConfig
import io.restassured.filter.log.LogDetail
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Import
import kotlin.test.BeforeTest

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestConfig::class)
abstract class BaseE2ETest : BaseIntegrationTest() {

    @LocalServerPort
    private var port: Int = 0

    @Autowired
    private lateinit var applicationContext: ApplicationContext

    @BeforeTest
    fun setup() {
        println("🔧 Context ID: ${applicationContext.id}")        // 같으면 재사용
        println("🔧 Context Hash: ${applicationContext.hashCode()}")  // 같으면 재사용
        println("🔧 Port: $port")

        RestAssured.port = port
        RestAssured.baseURI = "http://localhost"

        //  로깅 설정
        RestAssured.config = RestAssuredConfig.config()
            .logConfig(
                LogConfig.logConfig()
                    .enablePrettyPrinting(true)  // JSON pretty-printing
//                    .defaultStream(System.out) // 콘솔 출력
            )

        // request, response 항상 로깅.
        RestAssured.requestSpecification = RequestSpecBuilder()
            .setConfig(RestAssured.config)
            .log(LogDetail.ALL)
            .build()

        RestAssured.responseSpecification = ResponseSpecBuilder()
            .log(LogDetail.ALL)
            .build()
    }
}