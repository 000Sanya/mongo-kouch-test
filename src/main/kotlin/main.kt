import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.WriteConcern
import kotlinx.coroutines.runBlocking
import kouch.Context
import kouch.DatabaseName
import kouch.Settings
import kouch.client.KouchClientImpl
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    try {
        val kouchDatabase = KouchDatabase(
            KouchClientImpl(
                Context(
                    Settings(
                        host = "::1",
                        adminName = "dbadmin",
                        adminPassword = "dbadmin",
                        databaseNaming = Settings.DatabaseNaming.Predefined(DatabaseName("defaultdb"))
                    )
                )
            )
        )
        val kouchClusterDatabase = KouchDatabase(
            KouchClientImpl(
                Context(
                    Settings(
                        host = "::1",
                        adminName = "dbadmin",
                        adminPassword = "dbadmin",
                        port = 10010,
                        databaseNaming = Settings.DatabaseNaming.Predefined(DatabaseName("defaultdb"))
                    )
                )
            )
        )
        val mongoDatabase = MongoDatabase(
            KMongo.createClient(
                MongoClientSettings
                    .builder()
                    .applyConnectionString(ConnectionString("mongodb://dbadmin:dbadmin@localhost:27017"))
                    .writeConcern(WriteConcern.JOURNALED)
                    .build()
            ).coroutine
        )
        val mongoCluserDatabase = MongoDatabase(
            KMongo.createClient(
                MongoClientSettings
                    .builder()
                    .applyConnectionString(ConnectionString("mongodb://192.168.112.1:27011,192.168.112.1:27012,192.168.112.1:27013/?replicaSet=rs0"))
                    .writeConcern(WriteConcern.W2.withJournal(true))
                    .build()
            ).coroutine
        )

        val executor = MeasureExecutor(
            mapOf(
                "mongo(single)" to mongoDatabase,
                "mongo(3 nodes, W2)" to mongoCluserDatabase,
                "kouch(single)" to kouchDatabase,
                "kouch(3 nodes, default)" to kouchClusterDatabase,
            )
        )

        runBlocking {
            executor.createNAndGet(10000)
        }
    } catch (e: Exception) {
        println(e)
    }
}