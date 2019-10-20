package name.denyago.streaming.source.web

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.response.respond
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import name.denyago.streaming.source.db.ConnectionDetails
import name.denyago.streaming.source.db.DbReader
import name.denyago.streaming.source.db.GeneratedReader
import org.kodein.di.Kodein
import org.kodein.di.direct
import org.kodein.di.generic.instance


fun Application.module(container: Kodein) {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        // curl --no-buffer "http://127.0.0.1:8080/source/users/stream"
        get("/source/users/stream") {
            when (val validatedRequest = Request.validateParams(call.parameters)) {
                is Request.Valid -> {
                    val start = validatedRequest.start
                    val end = start + (validatedRequest.number - 1)
                    val dbConnectionDetails: ConnectionDetails = container.direct.instance()
                    val stream = when (validatedRequest.source) {
                        DataSource.Jooq -> DbReader(
                            dbUri = dbConnectionDetails.uri,
                            user = dbConnectionDetails.username,
                            password = dbConnectionDetails.password
                        ).selectUsers(start..end)
                        DataSource.Generator -> GeneratedReader().selectUsers(start..end)
                    }

                    call.respondTextWriter(writer = JsonStreamWriter.writer(stream))
                }
                is Request.Invalid -> {
                    val response = """
                        {"errors":[{
                        "message": "${validatedRequest.errors.first().message}",
                        "code": "${validatedRequest.errors.first().code}"
                        }]}
                    """.trimIndent().replace("\n", "")
                    call.respond(HttpStatusCode.BadRequest, response)
                }
            }
        }
    }
}
