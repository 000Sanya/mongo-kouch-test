interface Database<T> {
    suspend fun reset()

    suspend fun create(item: T)
    suspend fun create(range: Iterable<T>, bulk: Boolean)

    suspend fun getById(id: String): T?
}