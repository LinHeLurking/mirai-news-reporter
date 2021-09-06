package online.ruin_of_future.reporter

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.utils.MiraiLogger
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

object ReporterPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "online.ruin_of_future.reporter",
        version = "1.2.6",
    ) {
        name("Reporter")
        author("LinHeLurking")
    }
) {
    val newsCrawler = NewsCrawler()
    val animeCrawler = AnimeCrawler()
    override fun onEnable() {
        NewsGroupWhiteList.reload()
        AnimeGroupWhiteList.reload()

        CommandManager.registerCommand(NewsGroupCommand)
        CommandManager.registerCommand(AnimeGroupCommand)

        this.launch {
            while (true) {
                val dateTime = LocalDateTime.now()
                if (dateTime.hour in 7..7 && dateTime.minute in 0..31) {
                    logger.info("Daily pushing")
                    Bot.instances.forEach {
                        if (it.id in NewsGroupWhiteList.groupIdsPerBot) {
                            for (groupId in NewsGroupWhiteList.groupIdsPerBot[it.id]!!) {
                                try {
                                    val group = it.getGroup(groupId)
                                    group?.sendMessage("早上好呀, 这是今天的新闻速报 q(≧▽≦q)")
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
                        if (it.id in NewsGroupWhiteList.groupIdsPerBot) {
                            for (groupId in NewsGroupWhiteList.groupIdsPerBot[it.id]!!) {
                                try {
                                    val group = it.getGroup(groupId)
                                    group?.sendMessage("早上好呀, 这是今天的 B 站番剧 ( •̀ ω •́ )✧")
                                    group?.sendImage(ByteArrayInputStream(animeCrawler.animeToday()))
                                    logger.info(
                                        "Daily anime push to group " +
                                                (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                                    )
                                } catch (e: Exception) {
                                    logger.error(e)
                                }
                            }
                        }
                    }
                }
                delay(1000 * 60 * 30L)
            }
        }

        val sendNewsToTarget: suspend (Contact) -> Unit = {
            try {
                it.sendMessage("这是今天的新闻速报 q(≧▽≦q)")
                it.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
            } catch (e: Exception) {
                it.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
                logger.error(e)
            }
        }

        this.globalEventChannel().subscribeGroupMessages {
            matching(Regex("(每日|今日)?(新闻|速报|速递)")) {
                logger.info("$senderName 发起了新闻请求...")
                if (NewsGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendNewsToTarget(group)
                } else {
                    sender.sendMessage("为了防止打扰到网友，这个群不在日报白名单呢 QwQ")
                }

            }
        }

        this.globalEventChannel().subscribeFriendMessages {
            matching(Regex("(每日|今日)?(新闻|速报|速递)")) {
                logger.info("$senderName 发起了新闻请求...")
                sendNewsToTarget(sender)
            }
        }

        val sendAnimeToTarget: suspend (Contact) -> Unit = {
            try {
                it.sendMessage("这是今天的 B 站番剧 ( •̀ ω •́ )✧")
                it.sendImage(ByteArrayInputStream(animeCrawler.animeToday()))
            } catch (e: Exception) {
                when (e) {
                    is NoAnimeException -> {
                        it.sendMessage("好像今天没有放送呢 >_<")
                        logger.info(e)
                    }
                    else -> {
                        it.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
                        logger.error(e)
                    }
                }
            }
        }


        this.globalEventChannel().subscribeGroupMessages {
            matching(Regex("(每日|今日)?(新番|番剧|动画)")) {
                logger.info("$senderName 发起了动画请求...")
                if (AnimeGroupWhiteList.groupIdsPerBot[bot.id]?.contains(group.id) == true) {
                    sendAnimeToTarget(group)
                } else {
                    sender.sendMessage("为了防止打扰到网友，这个群不在日报白名单呢 QwQ")
                }
            }
        }

        this.globalEventChannel().subscribeFriendMessages {
            matching(Regex("(每日|今日)?(新番|番剧|动画)")) {
                logger.info("$senderName 发起了动画请求...")
                sendAnimeToTarget(sender)
            }
        }
    }
}