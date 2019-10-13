package name.denyago.streaming.source

import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import name.denyago.streaming.source.web.module


class MainKtTest : DescribeSpec() {

    init {
        describe("HTTP Application") {
            context("when requesting GET /source/users/stream") {
                it("returns JSON with the only 3 Users") {
                    withTestApplication(Application::module) {
                        with(handleRequest(HttpMethod.Get, "/source/users/stream?start=1&number=2")) {
                            response.status() shouldBe HttpStatusCode.OK
                            response.content?.replace("},", "},\n") shouldBe """
                                [{"id":1,"name":"Jane"},
                                {"id":2,"name":"Fred"}]
                            """.trimIndent()
                        }
                    }
                }
            }
        }
    }
}
