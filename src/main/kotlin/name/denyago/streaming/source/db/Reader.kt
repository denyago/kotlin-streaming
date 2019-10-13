package name.denyago.streaming.source.db

import java.util.stream.Stream
import kotlin.streams.asStream

data class User(val id: Int) {
    val name: String
        get() = if (this.id % 2 == 0) "Fred" else "Jane"
}

class Reader {
    fun selectUsers(ids: IntRange): Stream<User> {
        return generateSequence(
            User(ids.first),
            {
                if ((it.id + 1) < ids.last)
                    User(it.id + 1)
                else null
            }
        ).asStream()
    }
}
