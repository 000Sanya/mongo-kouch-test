import org.litote.kmongo.coroutine.*

class MongoDatabase(
    private val client: CoroutineClient,
    private val database: CoroutineDatabase = client.getDatabase("speed"),
    private val collection: CoroutineCollection<TestEntity> = database.getCollection<TestEntity>(),
): Database<TestEntity> {
    override suspend fun create(item: TestEntity) {
        collection.insertOne(item)
    }

    override suspend fun create(range: Iterable<TestEntity>, bulk: Boolean) {
        if (bulk) {
            collection.insertMany(range.toList())
        } else {
            for (item in range) {
                collection.insertOne(item)
            }
        }
    }

    override suspend fun getById(id: String): TestEntity? {
        return collection.findOneById(id)
    }

    override suspend fun reset() {
        collection.drop()
    }
}