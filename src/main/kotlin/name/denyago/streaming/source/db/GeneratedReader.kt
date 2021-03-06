package name.denyago.streaming.source.db

data class GeneratedUser(override val id: Int) : User {
    override val name: String
        get() = if (this.id % 2 == 0) "Fred" else "Jane"
}

class GeneratedReader : Readable {
    override fun selectUsers(ids: IntRange): Iterator<User> {
        return generateSequence(
            GeneratedUser(ids.first),
            {
                if ((it.id + 1) <= ids.last)
                    GeneratedUser(it.id + 1)
                else null
            }
        ).iterator()
    }
}
