package online.ruin_of_future.reporter.tasks

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.config.ReporterConfig
import online.ruin_of_future.reporter.crawler.AnimeCrawler
import online.ruin_of_future.reporter.crawler.NewsCrawler
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.data.NewsGroupWhiteList
import java.io.ByteArrayInputStream
import java.util.*

class MorningReportTask : TimerTask() {
    private val animeCrawler = AnimeCrawler.INSTANCE
    private val newsCrawler = NewsCrawler.INSTANCE

    private suspend fun sendNewsFrom(bot: Bot) {
        for (groupId in NewsGroupWhiteList.groupIdsPerBot[bot.id]!!) {
            try {
                val group = bot.getGroup(groupId)
                group?.sendMessage(ReporterConfig.newsDailyMessages.random())
                group?.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
                ReporterPlugin.logger.info(
                    "Daily news push to group " +
                            (group?.name ?: "<No group of ${groupId}> from ${bot.id}")
                )
            } catch (e: Exception) {
                ReporterPlugin.logger.error(e)
            }
            delay(100)
        }
    }

    private suspend fun sendAnimeFrom(bot: Bot) {
        for (groupId in AnimeGroupWhiteList.groupIdsPerBot[bot.id]!!) {
            try {
                val group = bot.getGroup(groupId)
                group?.sendMessage(ReporterConfig.animeDailyMessages.random())
                group?.sendImage(ByteArrayInputStream(animeCrawler.animeToday()))
                ReporterPlugin.logger.info(
                    "Daily anime push to group " +
                            (group?.name ?: "<No group of ${groupId}> from ${bot.id}")
                )
            } catch (e: Exception) {
                ReporterPlugin.logger.error(e)
            }
            delay(100)
        }
    }

    override fun run() = runBlocking {
        Bot.instances.forEach {
            if (it.id in NewsGroupWhiteList.groupIdsPerBot) {
                launch {
                    sendNewsFrom(it)
                }
            }
            if (it.id in AnimeGroupWhiteList.groupIdsPerBot) {
                launch {
                    sendAnimeFrom(it)
                }
            }
        }
    }
}