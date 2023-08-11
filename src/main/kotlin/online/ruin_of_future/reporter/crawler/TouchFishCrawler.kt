package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL
import javax.imageio.ImageIO


class TouchFishCrawler(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val byteArrayCache = Cached(byteArrayOf(), 1000 * 60 * 60 * 4L)


    fun isCacheValid(): Boolean {
        return byteArrayCache.isNotOutdated()
    }
    @Throws(IOException::class)
    suspend fun touchfishToday(): ByteArray {
        if (byteArrayCache.isNotOutdated()) {
            return byteArrayCache.value
        }

        val os = ByteArrayOutputStream()
        withContext(ioDispatcher) {
            var url = URL("https://api.vvhan.com/api/moyu")
            var bufferedImage = ImageIO.read(url.openConnection().getInputStream())
            ImageIO.write(bufferedImage, "png", os)
        }
        byteArrayCache.value = os.toByteArray()
        return byteArrayCache.value
    }

    companion object {
        val INSTANCE = TouchFishCrawler()
    }
}