package online.ruin_of_future.reporter

import online.ruin_of_future.reporter.crawler.NewsCrawler
import java.io.File

suspend fun main() {
    val newsCrawler = NewsCrawler()
    val bytes = newsCrawler.newsToday()
    val file = File("test.png")
    file.writeBytes(bytes)
}