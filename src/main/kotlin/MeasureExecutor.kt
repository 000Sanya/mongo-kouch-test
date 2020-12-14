import kotlin.system.measureTimeMillis

class MeasureExecutor(
    private val dbs: Map<String, Database<TestEntity>>
) {
    suspend fun createNAndGet(n: Long) {
        val entities = (0..n).map { TestEntity.randomEntity() }

        println("Inserting $n entities")

        Database.Bulk.values().forEach { bulkMode ->
            dbs.forEach { (dbName, db) ->
                db.reset()
                val time = measureTimeMillis {
                    db.create(entities, bulkMode)
                }
                println("$dbName: Insert mode: $bulkMode; With: ${time}ms")
            }
        }


    }
}
