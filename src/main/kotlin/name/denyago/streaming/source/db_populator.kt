package name.denyago.streaming.source

import name.denyago.streaming.source.db.GeneratedReader
import name.denyago.streaming.source.db.populate.ScriptGenerator
import java.io.File

const val SIZE = 2000000

fun main() {
    val seq = GeneratedReader()
        .selectUsers(1..SIZE)
        .asSequence()
    val file = File("./db/scripts/010_seed.sql")

    ScriptGenerator(seq, file.outputStream()).generate(SIZE)
}
