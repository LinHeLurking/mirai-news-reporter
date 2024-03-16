package online.ruin_of_future.reporter

import online.ruin_of_future.reporter.crawler.AnimeCrawler
import java.io.File

suspend fun main() {
    val animeCrawler = AnimeCrawler()
    val bytes = animeCrawler.animeToday()
    val file = File("test.png")
    file.writeBytes(bytes)
}