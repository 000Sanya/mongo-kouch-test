import kotlinx.coroutines.runBlocking
import kouch.Context
import kouch.DatabaseName
import kouch.Settings
import kouch.client.KouchClientImpl
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

fun main(args: Array<String>) {
    val kouchDatabase = KouchDatabase(
        KouchClientImpl(Context(
            Settings(
                host = "::1",
                adminName = "dbadmin",
                adminPassword = "dbadmin",
                databaseNaming = Settings.DatabaseNaming.Predefined(DatabaseName("defaultdb"))
            )
        ))
    )
    val mongoDatabase = MongoDatabase(
        KMongo.createClient("mongodb://dbadmin:dbadmin@[::1]:27017").coroutine
    )

    val executor = MeasureExecutor(
        mapOf(
            "kouch" to kouchDatabase,
            "mongo" to mongoDatabase,
        )
    )

    runBlocking {
        executor.createNAndGet(1000)
    }
}