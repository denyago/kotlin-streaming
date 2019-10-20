package name.denyago.streaming.source

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import name.denyago.streaming.source.db.ConnectionDetails
import name.denyago.streaming.source.web.module
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.singleton


const val HTTP_PORT = 8080

fun main() {
    fun Application.apiWithDi() = module(Kodein {
        bind<ConnectionDetails>() with singleton {
            ConnectionDetails(
                "jdbc:postgresql://127.0.0.1:5432/streaming_source",
                "streaming_source",
                "1d0n0tc@Re"
            )
        }
    })

    embeddedServer(Netty, HTTP_PORT) { apiWithDi() }.start()
}
