package name.denyago.streaming.source

import io.kotlintest.matchers.string.shouldContain
import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication


class MainKtTest : DescribeSpec() {

    init {
        describe("HTTP Application") {
            context("when requesting GET /stream/{n}") {
                it("returns n strings") {
                    withTestApplication(Application::module) {
                        with(handleRequest(HttpMethod.Get, "/stream/1")) {
                            response.status() shouldBe HttpStatusCode.OK
                            response.content shouldContain "Lorem ipsum"
                        }
                    }
                }
            }
        }
    }
}
