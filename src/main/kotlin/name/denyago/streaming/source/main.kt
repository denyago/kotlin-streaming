package name.denyago.streaming.source

import io.ktor.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import name.denyago.streaming.source.web.module

const val HTTP_PORT = 8080

fun main() {
    embeddedServer(
        Netty,
        HTTP_PORT,
        watchPaths = listOf("main.kt"),
        module = Application::module
    ).start()
}
