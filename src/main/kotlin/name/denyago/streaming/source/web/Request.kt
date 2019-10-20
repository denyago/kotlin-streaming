package name.denyago.streaming.source.web

import io.ktor.http.Parameters

sealed class Request {
    data class Valid(val start: Int, val number: Int, val source: DataSource) : Request()
    data class Invalid(val errors: List<Error>) : Request()

    companion object {
        fun validateParams(parameters: Parameters): Request {
            val dataSource = when (parameters["source"]) {
                "jooq" -> DataSource.Jooq
                "generator" -> DataSource.Generator
                null -> DataSource.Generator
                else -> return Invalid(
                    listOf(
                        Error(
                            "invalid_source",
                            "Source '${parameters["source"]}' is unsupported. " +
                                    "Use ${DataSource.sourcesAvailable()} instead."
                        )
                    )
                )
            }

            return Valid(
                start = (parameters["start"] ?: "1").toInt(),
                number = (parameters["number"] ?: "10000000").toInt(),
                source = dataSource
            )
        }
    }
}

sealed class DataSource {
    object Jooq : DataSource()
    object Generator : DataSource()

    override fun toString() =
        this.javaClass.simpleName.toLowerCase()

    companion object {
        fun sourcesAvailable(): String =
            listOf(Jooq, Generator).joinToString(prefix = "'", separator = "', '", postfix = "'") { it.toString() }
    }
}

data class Error(val code: String, val message: String)
