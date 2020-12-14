import kotlin.system.measureTimeMillis

class MeasureExecutor (
    private val dbs: Map<String, Database<TestEntity>>
) {
    suspend fun createNAndGet(n: Long) {
        val entities = (0..n).map { TestEntity.randomEntity() }
        for ((name, db) in dbs) {
            db.reset()
            val woBulk = measureTimeMillis {
                db.create(entities, false)
            }

            db.reset()
            val wBulk = measureTimeMillis {
                db.create(entities, true)
            }

            println("$name Insert w/o bulk: $woBulk; With: $wBulk")
        }
    }
}