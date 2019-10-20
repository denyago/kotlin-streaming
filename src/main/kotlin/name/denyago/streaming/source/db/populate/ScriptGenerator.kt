package name.denyago.streaming.source.db.populate

import kotlinx.coroutines.runBlocking
import name.denyago.streaming.source.db.User
import java.io.OutputStream

const val HEADER = """--- Auto-generated by db_populator.kt
insert into users (id, name)
  values"""

class ScriptGenerator(private val usersGenerator: Sequence<User>, private val out: OutputStream) {
    fun generate(size: Int, chunk: Int = 10, perStatement: Int = 1000) {
        runBlocking {
            out.use { fs ->
                fs.write(HEADER.toByteArray())

                val separateStatements = usersGenerator.chunked(perStatement)

                separateStatements.withIndex().forEach { (i, statementValues) ->
                    statementValues.chunked(chunk).withIndex().forEach { (i, value) ->
                        val padding = if (i == 0) " " else "         "

                        val fields = value
                            .joinToString(separator = ", ", postfix = ",") { "(${it.id}, '${it.name}')" }
                            .let {
                                if ((i + 1) * chunk >= perStatement)
                                    it.trimEnd(',').plus(";")
                                else
                                    it
                            }

                        fs.write("$padding$fields\n".toByteArray())
                        fs.flush()
                    }

                    val nextStatementStart = if ((i + 1) * perStatement >= size) "" else "\n\n$HEADER"
                    fs.write(nextStatementStart.toByteArray())
                    fs.flush()
                }
            }
        }
    }
}