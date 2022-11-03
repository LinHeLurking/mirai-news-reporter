package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException


class HTTPGetter {
    private var client = OkHttpClient()

    @Throws(IOException::class)
    suspend fun get(url: String): String = runBlocking {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val future = async {
            client.newCall(request).execute().use { response -> return@async response.body!!.string() }
        }
        return@runBlocking future.await()
    }
}