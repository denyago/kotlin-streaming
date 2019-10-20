package name.denyago.streaming.source

import io.kotlintest.shouldBe
import io.kotlintest.specs.DescribeSpec
import io.ktor.application.Application
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.server.testing.handleRequest
import io.ktor.server.testing.withTestApplication
import name.denyago.streaming.source.db.ConnectionDetails
import name.denyago.streaming.source.db.GeneratedReader
import name.denyago.streaming.source.db.populate.ScriptGenerator
import name.denyago.streaming.source.web.module
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.io.ByteArrayOutputStream
import java.io.File
import java.sql.DriverManager


class MainKtTest : DescribeSpec() {

    init {
        val container = Kodein {
            bind<ConnectionDetails>() with singleton {
                ConnectionDetails("jdbc:h2:mem:test", "", "")
            }
        }

        fun Application.apiWithDi() = module(container)

        describe("HTTP Application") {
            context("when requesting GET /source/users/stream") {
                it("returns JSON with the only 3 Users from a generator source") {
                    withTestApplication(Application::apiWithDi) {
                        with(handleRequest(HttpMethod.Get, "/source/users/stream?start=1&number=2&source=generator")) {
                            response.status() shouldBe HttpStatusCode.OK
                            response.content?.replace("},", "},\n") shouldBe """
                                [{"id":1,"name":"Jane"},
                                {"id":2,"name":"Fred"}]
                            """.trimIndent()
                        }
                    }
                }

                context("when requesting from an unsupported source") {
                    it("returns 400 with a validation error") {
                        withTestApplication(Application::apiWithDi) {
                            with(handleRequest(HttpMethod.Get, "/source/users/stream?start=1&number=2&source=random")) {
                                response.status() shouldBe HttpStatusCode.BadRequest
                                response.content?.replace("},", "},\n") shouldBe """
                                {"errors":[{"message": "Source 'random' is unsupported. 
                                Use 'jooq', 'generator' instead.","code": "invalid_source"}]}
                            """.trimIndent().replace("\n","")
                            }
                        }
                    }
                }

                context("when requesting from a jooq source") {
                    it("returns JSON with Users from the DB") {
                        val conn = DriverManager.getConnection(container.direct.instance<ConnectionDetails>().uri)

                        val createTableScript = File("./db/scripts/001_create_db.sql").readText()
                        val createTableResult = conn.createStatement().executeUpdate(createTableScript)
                        createTableResult shouldBe 0

                        val populateTableScript = ByteArrayOutputStream(200000)
                        val usersGenerator = GeneratedReader()
                            .selectUsers(1..3)
                            .asSequence()
                        ScriptGenerator(usersGenerator, populateTableScript).generate(3, 3, 3)
                        val populateTableResult = conn.createStatement().executeUpdate(populateTableScript.toString())
                        populateTableResult shouldBe 3

                        withTestApplication(Application::apiWithDi) {
                            with(handleRequest(HttpMethod.Get, "/source/users/stream?start=1&number=2&source=jooq")) {
                                response.status() shouldBe HttpStatusCode.OK
                                response.content?.replace("},", "},\n") shouldBe """
                                [{"id":1,"name":"Jane"},
                                {"id":2,"name":"Fred"}]
                            """.trimIndent()

                                conn.close()
                            }
                        }
                    }
                }
            }
        }
    }
}
