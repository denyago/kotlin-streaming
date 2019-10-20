package name.denyago.streaming.source.db

import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.table
import java.sql.ResultSet

const val MAX_RECORDS_PER_FETCH = 20

data class DbUser constructor(
    override val id: Int,
    override val name: String
) : User

class DbReader(
    private val dbUri: String,
    private val user: String,
    private val password: String
) : Readable {

    private val dsl by lazy {
        DSL.using(dbUri, user, password).also {
                dsl -> dsl.connection { it.autoCommit = false }
        }
    }

    override fun selectUsers(ids: IntRange): Iterator<User> {
        return dsl.select(field("id"), field("name")).from(table("users")).where(
            field("id").betweenSymmetric(ids.first, ids.last)
        ).resultSetType(ResultSet.TYPE_FORWARD_ONLY).fetchSize(MAX_RECORDS_PER_FETCH).fetchLazy()
            .stream()
            .map { DbUser(it.value1().toString().toInt(), it.value2().toString()) }.iterator()
    }
}
