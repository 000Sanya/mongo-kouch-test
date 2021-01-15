import de.m3y.kformat.Table
import de.m3y.kformat.table
import kotlinx.coroutines.delay
import java.lang.StringBuilder
import kotlin.system.measureTimeMillis

class MeasureExecutor(
    private val dbs: Map<String, Database<TestEntity>>
) {
    suspend fun createNAndGet(n: Long) {
        val entities = (0..n).map { TestEntity.randomEntity() }

        println("Inserting $n entities")

        val writeTable = table {
            header("Name", "SEQUENTIAL", "BULK", "PARALLEL")
            hints {
                postfix("SEQUENTIAL", "ms")
                postfix("BULK", "ms")
                postfix("PARALLEL", "ms")
                borderStyle = Table.BorderStyle.SINGLE_LINE
            }
        }

        val readTable = table {
            header("Name", "SEQUENTIAL", "BULK", "PARALLEL")
            hints {
                postfix("SEQUENTIAL", "ms")
                postfix("BULK", "ms")
                postfix("PARALLEL", "ms")
                borderStyle = Table.BorderStyle.SINGLE_LINE
            }
        }

        dbs.forEach { (dbName, db) ->
            val readTimes = mutableListOf<Int>()
            val writeTimes = mutableListOf<Int>()
            Database.Bulk.values().forEach { bulkMode ->
                db.reset()
                val time = measureTimeMillis {
                    db.create(entities, bulkMode)
                }
                val readTime = measureTimeMillis {
                    entities.forEach { db.getById(it.id) }
                }
                readTimes += readTime.toInt()
                writeTimes += time.toInt()
                println("$dbName: Insert mode: $bulkMode; With: ${time}ms; Read: $readTime")
            }

            writeTable.row(dbName, *writeTimes.toTypedArray())
            readTable.row(dbName, *readTimes.toTypedArray())

            println("Waiting")
            delay(30000)
        }

        println("WRITE")
        println(writeTable.render(StringBuilder()).toString())
        println()
        println()
        println("READ")
        println(readTable.render(StringBuilder()).toString())
    }
}
