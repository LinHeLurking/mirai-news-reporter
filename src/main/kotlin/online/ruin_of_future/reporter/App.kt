package online.ruin_of_future.reporter

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

fun main(args: Array<String>) {
    val crawler = NewsCrawler()
    runBlocking {
        crawler.newsToday()
    }
}

object ReporterPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "online.ruin_of_future.reporter",
        version = "1.0.0",
    ) {
        name("Reporter")
        author("LinHeLurking")
    }
) {
    val newsCrawler = NewsCrawler()
    override fun onEnable() {
        GroupWhiteList.reload()

        CommandManager.registerCommand(ReporterGroupCommand)

        this.launch {
            while (true) {
                val dateTime = LocalDateTime.now()
                if (dateTime.hour in 7..7 && dateTime.minute in 0..31) {
                    logger.info("Daily pushing")
                    for (groupId in GroupWhiteList) {
                        Bot.instances.forEach {
                            try {
                                val group = it.getGroup(groupId)
                                group?.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
                                logger.info(
                                    "Daily news push to group " +
                                            (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                                )
                            } catch (e: Exception) {
                                logger.error(e)
                            }
                        }
                    }
                }
                delay(1000 * 60 * 30L)
            }
        }

        this.globalEventChannel().subscribeGroupMessages {
            matching(Regex("(每日|今日)?(新闻|速报)")) {
                logger.info("$senderName 发起了请求...")
                launch {
                    if (GroupWhiteList.contains(group.id)) {
                        try {
                            group.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
                        } catch (e: Exception) {
                            group.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
                            logger.error(e)
                        }
                    } else {
                        sender.sendMessage("为了防止打扰到网友，这个群不在日报白名单呢 QwQ")
                    }
                }
            }
        }

        this.globalEventChannel().subscribeFriendMessages {
            matching(Regex("(每日|今日)?(新闻|速报)")) {
                logger.info("$senderName 发起了请求...")
                launch {
                    try {
                        sender.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
                    } catch (e: Exception) {
                        sender.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
                        logger.error(e)
                    }
                }
            }
        }
    }
}