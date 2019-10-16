package name.denyago.streaming.source.web

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import name.denyago.streaming.source.db.DbReader
import name.denyago.streaming.source.db.GeneratedReader
import name.denyago.streaming.source.db.User

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        // curl --no-buffer "http://127.0.0.1:8080/source/users/stream"
        get("/source/users/stream") {
            val start = (call.parameters["start"] ?: "1").toInt()
            val number = (call.parameters["number"] ?: "10000000").toInt()
            val source = call.parameters["source"] ?: "generator"
            val end = start + number

            val stream = when (source) {
                "jooq" -> DbReader(
                    user = "streaming_source",
                    password = "1d0n0tc@Re"
                ).selectUsers(start..end).iterator()
                "generator" -> GeneratedReader().selectUsers(start..end).iterator()
                else -> emptyList<User>().stream().iterator()
            }

            call.respondTextWriter {
                write("[")
                stream.forEach {
                    val json = """{"id":${it.id},"name":"${it.name}"}"""
                    write(json)
                    if (stream.hasNext()) write(",")
                    flush()
                }
                write("]")
                flush()
            }
        }
    }
}
