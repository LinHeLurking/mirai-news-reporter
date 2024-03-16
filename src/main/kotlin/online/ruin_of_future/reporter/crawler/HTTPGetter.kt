package online.ruin_of_future.reporter.crawler

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request


class HTTPGetter {
    private var client = OkHttpClient()

    suspend fun get(url: String): String = runBlocking {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val future = async {
            client.newCall(request).execute().use { response -> return@async response.body!!.string() }
        }
        return@runBlocking future.await()
    }

    suspend fun getRaw(url: String): ByteArray = runBlocking {
        val request: Request = Request.Builder()
            .url(url)
            .build()
        val future: Deferred<ByteArray> = async {
            val response = client.newCall(request).execute()
            val bytes = ByteArray(response.headers.get("Content-Length")?.toInt() ?: 0)
            response.body?.bytes()?.copyInto(bytes)
            return@async bytes
        }
        return@runBlocking future.await()
    }
}