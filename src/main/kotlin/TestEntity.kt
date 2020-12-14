import kotlinx.serialization.Serializable
import kouch.KouchEntity
import kouch.KouchEntityMetadata
import java.util.concurrent.ThreadLocalRandom
import kotlin.streams.asSequence

@Serializable
@KouchEntityMetadata
data class TestEntity(
    val f1: String,
    val f2: String,
    val f3: String,
    val f4: String,
    val f5: String,
    val f6: String,
    val f7: String,
    val f8: String,
    val f9: String,
    val f10: String,
    override val id: String,
    override val revision: String?,
): KouchEntity {
    companion object {
        private val charPool = ('a'..'z') + ('A'..'Z') + ('0'..'9')

        fun randomEntity(): TestEntity {
            return TestEntity(
                randomString(2),
                randomString(4),
                randomString(6),
                randomString(8),
                randomString(10),
                randomString(12),
                randomString(14),
                randomString(16),
                randomString(18),
                randomString(20),
                randomString(22),
                null,
            )
        }

        fun randomString(length: Long = 10): String {
            return ThreadLocalRandom.current()
                .ints(length, 0, charPool.size)
                .asSequence()
                .map(charPool::get)
                .joinToString("")
        }
    }
}
