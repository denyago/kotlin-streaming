package name.denyago.streaming.source.db

import java.util.stream.Stream

interface Readable {
    fun selectUsers(ids: IntRange): Stream<User>
}

interface User {
    val id: Int
    val name: String
}
