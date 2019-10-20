package name.denyago.streaming.source.db

interface Readable {
    fun selectUsers(ids: IntRange): Iterator<User>
}

interface User {
    val id: Int
    val name: String
}
