import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kouch.KouchClient
import kouch.getMetadata

class KouchDatabase(
    val kouch: KouchClient
) : Database<TestEntity> {
    override suspend fun create(item: TestEntity) {
        kouch.doc.insert(item, TestEntity::class)
    }

    override suspend fun create(range: Iterable<TestEntity>, bulk: Database.Bulk) {
        when (bulk) {
            Database.Bulk.SEQUENTIAL -> range.forEach { create(it) }
            Database.Bulk.BULK -> kouch.db.bulkUpsert(range)
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
        return kouch.doc.get<TestEntity>(id, TestEntity::class)
    }

    override suspend fun reset() {
        val dbName = kouch.context.getMetadata(TestEntity::class).databaseName
        if (kouch.db.isExist(dbName)) {
            kouch.db.delete(dbName)
        }
        kouch.db.create(dbName)
    }

}
