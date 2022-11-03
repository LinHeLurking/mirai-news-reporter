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
import online.ruin_of_future.reporter.crawler.AnimeCrawler
import online.ruin_of_future.reporter.crawler.NoAnimeException
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.regexOrBuilder
import java.io.ByteArrayInputStream
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

object AnimeChatReply {
    private val animeCrawler = AnimeCrawler.INSTANCE

    private suspend fun sendAnimeToTarget(contact: Contact, context: CoroutineContext = Dispatchers.Default) {
        try {
            if (!animeCrawler.isCacheValid()) {
                contact.sendMessage(ReporterConfig.waitMessages.random())
            }
            val stream = withContext(context) {
                ByteArrayInputStream(animeCrawler.animeToday())
            }
            contact.sendMessage(ReporterConfig.animeReplyMessages.random())
            contact.sendImage(stream)
        } catch (e: Exception) {
            when (e) {
                is NoAnimeException -> {
                    contact.sendMessage(ReporterConfig.noAnimeMessages.random())
                    ReporterPlugin.logger.info(e)
                }

                else -> {
                    contact.sendMessage(ReporterConfig.errorMessages.random())
                    ReporterPlugin.logger.error(e)
                }
            }
        }
    }

    private fun buildTrigger(): Regex {
        val dailyTrigger = regexOrBuilder(ReporterConfig.dailyTriggers)
        val separatorTrigger = regexOrBuilder(ReporterConfig.separators)
        val animeTrigger = regexOrBuilder(ReporterConfig.animeTriggers)
        return Regex("($dailyTrigger)($separatorTrigger?)($animeTrigger)")
    }

    private val trigger = buildTrigger()

    fun registerToPlugin(plugin: KotlinPlugin) {
        plugin.globalEventChannel().subscribeGroupMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了动画请求...")
                if (AnimeGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendAnimeToTarget(group, coroutineContext)
                } else {
                    sender.sendMessage(ReporterConfig.noDisturbingGroupMessages.random())
                }
            }
        }

        plugin.globalEventChannel().subscribeFriendMessages {
            matching(trigger) {
                plugin.logger.info("$senderName 发起了动画请求...")
                sendAnimeToTarget(sender, coroutineContext)
            }
        }
    }
}