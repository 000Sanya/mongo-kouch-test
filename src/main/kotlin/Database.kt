interface Database<T> {
    suspend fun reset()

    suspend fun create(item: T)
    suspend fun create(range: Iterable<T>, bulk: Bulk)

    suspend fun getById(id: String): T?

    enum class Bulk{
        SEQUENTIAL,
        BULK,
        PARALLEL
    }
}
