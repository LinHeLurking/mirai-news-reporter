package online.ruin_of_future.reporter.tasks

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.config.ReporterConfig
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.data.NewsGroupWhiteList
import online.ruin_of_future.reporter.crawler.AnimeCrawler
import online.ruin_of_future.reporter.crawler.NewsCrawler
import java.io.ByteArrayInputStream
import java.util.*

class MorningReportTask : TimerTask() {

    override fun run() = runBlocking {
        Bot.instances.forEach {
            if (it.id in NewsGroupWhiteList.groupIdsPerBot) {
                launch {
                    for (groupId in NewsGroupWhiteList.groupIdsPerBot[it.id]!!) {
                        try {
                            val group = it.getGroup(groupId)
                            group?.sendMessage(ReporterConfig.newsDailyMessages.random())
                            group?.sendImage(ByteArrayInputStream(NewsCrawler.newsToday()))
                            ReporterPlugin.logger.info(
                                "Daily news push to group " +
                                        (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                            )
                        } catch (e: Exception) {
                            ReporterPlugin.logger.error(e)
                        }
                        delay(100)
                    }
                }
            }
            if (it.id in AnimeGroupWhiteList.groupIdsPerBot) {
                launch {
                    for (groupId in AnimeGroupWhiteList.groupIdsPerBot[it.id]!!) {
                        try {
                            val group = it.getGroup(groupId)
                            group?.sendMessage(ReporterConfig.animeDailyMessages.random())
                            group?.sendImage(ByteArrayInputStream(AnimeCrawler.animeToday()))
                            ReporterPlugin.logger.info(
                                "Daily anime push to group " +
                                        (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                            )
                        } catch (e: Exception) {
                            ReporterPlugin.logger.error(e)
                        }
                        delay(100)
                    }
                }
            }
        }
    }
}