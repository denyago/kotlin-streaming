package name.denyago.streaming.source.db

import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.util.stream.Stream

data class DbUser constructor(
    override var id: Int,
    override var name: String
) : User

class DbReader(
    private val dbUri: String = "jdbc:postgresql://127.0.0.1:5432/streaming_source",
    private val user: String,
    private val password: String
) : Readable {

    private val dsl by lazy { DSL.using(dbUri, user, password) }

    override fun selectUsers(ids: IntRange): Stream<User> {
        return dsl.select(field("id"), field("name")).from(table("users")).where(
            field("id").betweenSymmetric(ids.first, ids.last)
        ).fetch().stream().map { DbUser(it.value1().toString().toInt(), it.value2().toString()) }
    }
}