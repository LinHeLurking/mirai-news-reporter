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
import online.ruin_of_future.reporter.crawler.TouchFishCrawler
import online.ruin_of_future.reporter.data.TouchFishGroupWhiteList
import online.ruin_of_future.reporter.regexOrBuilder
import java.io.ByteArrayInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object TouchFishReply {
    private val touchfishCrawler = TouchFishCrawler.INSTANCE
    private suspend fun sendTouchFishToTarget(contact: Contact, context: CoroutineContext = Dispatchers.Default) {
        if (!touchfishCrawler.isCacheValid()) {
            contact.sendMessage(ReporterConfig.waitMessages.random())
        }
        val stream = withContext(context) {
            try {
                ByteArrayInputStream(touchfishCrawler.touchfishToday())
            } catch (e: Throwable) {
                contact.sendMessage(ReporterConfig.errorMessages.random())
                ReporterPlugin.logger.error(e)
                return@withContext null
            }
        }
        if (stream != null) {
            contact.sendMessage(ReporterConfig.touchfishReplyMessages.random())
            contact.sendImage(stream)
        }
    }

    private fun buildTrigger(): Regex {
        val dailyTrigger = regexOrBuilder(ReporterConfig.dailyTriggers)
        val separatorTrigger = regexOrBuilder(ReporterConfig.separators)
        val touchfishTrigger = regexOrBuilder(ReporterConfig.touchfishTriggers)
        return Regex("($separatorTrigger?)($touchfishTrigger)")
    }

    private val trigger = buildTrigger()

    fun registerToPlugin(plugin: KotlinPlugin) {
        plugin.globalEventChannel().subscribeGroupMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了摸鱼日历请求...")
                if (TouchFishGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendTouchFishToTarget(group, coroutineContext)
                } else {
                    sender.sendMessage(ReporterConfig.noDisturbingGroupMessages.random())
                    sendTouchFishToTarget(sender, coroutineContext)
                }
            }
        }

        plugin.globalEventChannel().subscribeFriendMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了摸鱼日历请求...")
                sendTouchFishToTarget(sender, coroutineContext)
            }
        }
    }
}