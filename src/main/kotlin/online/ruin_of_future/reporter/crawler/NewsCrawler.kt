package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.awt.Font

@Serializable
data class NewsMetaData(
    val code: Int,
    val msg: String,
    val imageUrl: String,
    val datatime: String,
)

class NewsException(msg: String) : Exception(msg)

class NewsCrawler(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val httpGetter = HTTPGetter()
    private val entryUrl: String = "https://api.2xb.cn/zaob"

    private val byteArrayCache = Cached(byteArrayOf(), 1000 * 60 * 60 * 4L)

    private val font = Font
        .createFont(Font.TRUETYPE_FONT, this.javaClass.getResourceAsStream("/chinese_font.ttf"))
        .deriveFont(25f)

    fun isCacheValid(): Boolean {
        return byteArrayCache.isNotOutdated()
    }

    private val json = Json { ignoreUnknownKeys = true }
    private suspend fun getMetaData(): NewsMetaData {
        val jsonStr = withContext(ioDispatcher) {
            httpGetter.get(entryUrl)
        }
        val meta = json.decodeFromString<NewsMetaData>(jsonStr)
        // verify data
        if (meta.code != 200 || meta.msg.lowercase() != "success") {
            throw NewsException("Wrong news metadata!")
        }
        return meta
    }

    private suspend fun getNewsImage(url:String) {
        val bytes = httpGetter.getRaw(url)
        byteArrayCache.value = bytes
    }

    suspend fun newsToday(): ByteArray {
        if (byteArrayCache.isNotOutdated()) {
            return byteArrayCache.value
        }
        val meta = getMetaData()
        getNewsImage(meta.imageUrl)
        return byteArrayCache.value
    }

    companion object {
        val INSTANCE = NewsCrawler()
    }
}