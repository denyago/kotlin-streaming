package name.denyago.streaming.source.web

import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.http.parseUrlEncodedParameters
import io.ktor.util.KtorExperimentalAPI

@KtorExperimentalAPI
class RequestTest : DescribeSpec() {

    init {
        describe("validateParams") {
            context("when source is unsupported") {
                it("returns Invalid result with errors") {
                    Request.validateParams("start=1&number=2&source=random".parseUrlEncodedParameters()) shouldBe
                            Request.Invalid(
                                listOf(
                                    Error(
                                        "invalid_source",
                                        "Source 'random' is unsupported. Use 'jooq', 'generator' instead."
                                    )
                                )
                            )
                }

            }

            context("when the source is supported") {
                it("returns a Valid result") {
                    Request.validateParams("start=1&number=2&source=jooq".parseUrlEncodedParameters()) shouldBe
                            Request.Valid(1, 2, DataSource.Jooq)

                    Request.validateParams("start=1&number=2&source=generator".parseUrlEncodedParameters()) shouldBe
                            Request.Valid(1, 2, DataSource.Generator)
                }
            }

            context("when no query attributes provided") {
                it("returns a Valid result with default choices") {
                    Request.validateParams("".parseUrlEncodedParameters()) shouldBe
                            Request.Valid(1, 10000000, DataSource.Generator)
                }
            }
        }
    }
}
