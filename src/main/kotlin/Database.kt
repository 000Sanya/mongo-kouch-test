interface Database<T> {
    suspend fun reset()

    suspend fun create(item: T)
    suspend fun create(range: Iterable<T>, bulk: Bulk)

    suspend fun getById(id: String): T?
    suspend fun get(range: Iterable<T>, bulk: Bulk)

    enum class Bulk{
        SEQUENTIAL,
        BULK,
        PARALLEL
    }
}
