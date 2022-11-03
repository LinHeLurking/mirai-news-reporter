package online.ruin_of_future.reporter.chat_reply

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.config.ReporterConfig
import online.ruin_of_future.reporter.crawler.NewsCrawler
import online.ruin_of_future.reporter.data.NewsGroupWhiteList
import online.ruin_of_future.reporter.regexOrBuilder
import java.io.ByteArrayInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object NewsChatReply {
    private val newsCrawler = NewsCrawler.INSTANCE
    private suspend fun sendNewsToTarget(contact: Contact, context: CoroutineContext = Dispatchers.Default) {
        try {
            if (!newsCrawler.isCacheValid()) {
                contact.sendMessage(ReporterConfig.waitMessages.random())
            }
            val stream = withContext(context) {
                ByteArrayInputStream(newsCrawler.newsToday())
            }
            contact.sendMessage(ReporterConfig.newsReplyMessages.random())
            contact.sendImage(stream)
        } catch (e: Exception) {
            contact.sendMessage(ReporterConfig.errorMessages.random())
            ReporterPlugin.logger.error(e)
        }
    }

    private fun buildTrigger(): Regex {
        val dailyTrigger = regexOrBuilder(ReporterConfig.dailyTriggers)
        val separatorTrigger = regexOrBuilder(ReporterConfig.separators)
        val newsTrigger = regexOrBuilder(ReporterConfig.newsTriggers)
        return Regex("($dailyTrigger)($separatorTrigger?)($newsTrigger)")
    }

    private val trigger = buildTrigger()

    fun registerToPlugin(plugin: KotlinPlugin) {
        plugin.globalEventChannel().subscribeGroupMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了新闻请求...")
                if (NewsGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendNewsToTarget(group, coroutineContext)
                } else {
                    sender.sendMessage(ReporterConfig.noDisturbingGroupMessages.random())
                }
            }
        }

        plugin.globalEventChannel().subscribeFriendMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了新闻请求...")
                sendNewsToTarget(sender, coroutineContext)
            }
        }
    }
}