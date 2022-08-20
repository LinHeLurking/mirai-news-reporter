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
import online.ruin_of_future.reporter.data.NewsGroupWhiteList
import online.ruin_of_future.reporter.crawler.NewsCrawler
import online.ruin_of_future.reporter.regexOrBuilder
import java.io.ByteArrayInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object NewsChatReply {
    private suspend fun sendNewsToTarget(contact: Contact, context: CoroutineContext = Dispatchers.Default) {
        try {
            if (!NewsCrawler.isCacheValid()) {
                contact.sendMessage(ReporterConfig.chatMessage.waitMessage)
            }
            val stream = withContext(context) {
                ByteArrayInputStream(NewsCrawler.newsToday())
            }
            contact.sendMessage(ReporterConfig.chatMessage.newsReplayMessage)
            contact.sendImage(stream)
        } catch (e: Exception) {
            contact.sendMessage(ReporterConfig.chatMessage.errorMessage)
            ReporterPlugin.logger.error(e)
        }
    }

    fun buildTrigger(): Regex {
        val dailyTrigger = regexOrBuilder(ReporterConfig.chatMessage.dailyTriggers)
        val separatorTrigger = regexOrBuilder(ReporterConfig.chatMessage.separators)
        val newsTrigger = regexOrBuilder(ReporterConfig.chatMessage.newsTriggers)
        return Regex("$dailyTrigger$separatorTrigger?$newsTrigger")
    }

    val trigger = buildTrigger()

    fun registerToPlugin(plugin: KotlinPlugin) {
        plugin.globalEventChannel().subscribeGroupMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了新闻请求...")
                if (NewsGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendNewsToTarget(group, coroutineContext)
                } else {
                    sender.sendMessage(ReporterConfig.chatMessage.noDisturbingGroupMessage)
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