import com.mongodb.client.model.Filters
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.CoroutineDatabase

class MongoDatabase(
    private val client: CoroutineClient,
    private val database: CoroutineDatabase = client.getDatabase("speed"),
    private val collection: CoroutineCollection<TestEntity> = database.getCollection<TestEntity>(),
) : Database<TestEntity> {
    override suspend fun create(item: TestEntity) {
        collection.insertOne(item)
    }

    override suspend fun create(range: Iterable<TestEntity>, bulk: Database.Bulk) {
        when (bulk) {
            Database.Bulk.SEQUENTIAL -> range.forEach { create(it) }
            Database.Bulk.BULK -> collection.insertMany(range.toList())
            Database.Bulk.PARALLEL -> range
                .map { item ->
                    GlobalScope.launch {
                        create(item)
                    }
                }
                .forEach { it.join() }
        }
    }

    override suspend fun getById(id: String): TestEntity? {
        return collection.findOneById(id)
    }

    override suspend fun reset() {
        collection.drop()
    }

    override suspend fun get(range: Iterable<TestEntity>, bulk: Database.Bulk) {
        when (bulk) {
            Database.Bulk.SEQUENTIAL -> range.forEach { getById(it.id) }
            Database.Bulk.BULK -> collection.find(Filters.`in`("_id", range.map { it.id })).toList()
            Database.Bulk.PARALLEL -> range
                .map { item ->
                    GlobalScope.launch {
                        getById(item.id)
                    }
                }
                .forEach { it.join() }
        }
    }
}
