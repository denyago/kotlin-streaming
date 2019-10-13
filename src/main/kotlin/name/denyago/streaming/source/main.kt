package name.denyago.streaming.source

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.response.respondTextWriter
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.delay
import java.time.LocalDateTime

const val DELAY = 1000L // 10 sec
const val HTTP_PORT = 8080
const val LOREM_IPSUM = """Lorem ipsum dolor sit amet, consectetur adipiscing elit, 
sed do eiusmod tempor incididunt ut labore et dolore magna aliqua."""

fun Application.module() {
    install(DefaultHeaders)
    install(CallLogging)
    install(Routing) {
        // curl --no-buffer "http://127.0.0.1:8080/stream/200"
        get("/stream/{n}") {
            val lorenIpsum = LOREM_IPSUM.replace("\n".toRegex(), "")
            val times = call.parameters["n"]!!.toInt()

            call.respondTextWriter {
                repeat(times) {
                    val time = LocalDateTime.now()
                    write("$time -> $lorenIpsum")
                    flush()
                    delay(DELAY)
                }
            }
        }
    }
}

fun main(args: Array<String>) {
    embeddedServer(
        Netty,
        HTTP_PORT,
        watchPaths = listOf("main.kt"),
        module = Application::module
    ).start() // $COVERAGE-IGNORE$
}
