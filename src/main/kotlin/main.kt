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
    val mongoDatabase = MongoDatabase(
        KMongo.createClient(
            MongoClientSettings
                .builder()
                .applyConnectionString(ConnectionString("mongodb://dbadmin:dbadmin@[::1]:27017"))
                .writeConcern(WriteConcern.JOURNALED)
                .build()
        ).coroutine
    )

    val executor = MeasureExecutor(
        mapOf(
            "mongo" to mongoDatabase,
            "kouch" to kouchDatabase,
        )
    )

    runBlocking {
        executor.createNAndGet(1000)
    }
}
