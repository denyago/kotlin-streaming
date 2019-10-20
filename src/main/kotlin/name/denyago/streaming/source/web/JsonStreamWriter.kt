package name.denyago.streaming.source.web

import name.denyago.streaming.source.db.User
import java.io.Writer

object JsonStreamWriter {
    fun writer(stream: Iterator<User>): suspend Writer.() -> Unit = {
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
