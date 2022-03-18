package online.ruin_of_future.reporter

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import java.io.ByteArrayInputStream
import java.time.ZoneId
import java.util.*

object ReporterPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "online.ruin_of_future.reporter",
        version = "1.3.1",
    ) {
        name("Reporter")
        author("LinHeLurking")
    }
) {
    private val newsCrawler = NewsCrawler()
    private val animeCrawler = AnimeCrawler()
    private val scheduler = Timer()
    private val dailyTask = object: TimerTask() {
        override fun run() {
            Bot.instances.forEach {
                if (it.id in NewsGroupWhiteList.groupIdsPerBot) {
                    launch {
                        for (groupId in NewsGroupWhiteList.groupIdsPerBot[it.id]!!) {
                            try {
                                val group = it.getGroup(groupId)
                                group?.sendMessage("早上好呀, 这是今天的新闻速报 \nq(≧▽≦q)")
                                group?.sendImage(ByteArrayInputStream(newsCrawler.newsToday()))
                                logger.info(
                                    "Daily news push to group " +
                                            (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                                )
                            } catch (e: Exception) {
                                logger.error(e)
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
                                group?.sendMessage("早上好呀, 这是今天的 B 站番剧 \n( •̀ ω •́ )✧")
                                group?.sendImage(ByteArrayInputStream(animeCrawler.animeToday()))
                                logger.info(
                                    "Daily anime push to group " +
                                            (group?.name ?: "<No group of ${groupId}> from ${it.id}")
                                )
                            } catch (e: Exception) {
                                logger.error(e)
                            }
                            delay(100)
                        }
                    }
                }
            }
        }
    }

    override fun onEnable() {
        NewsGroupWhiteList.reload()
        AnimeGroupWhiteList.reload()

        CommandManager.registerCommand(NewsGroupCommand)
        CommandManager.registerCommand(AnimeGroupCommand)

        val date = Date.from(
            Date().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
        ) // midnight today
        date.time += 7 * 60 * 60 * 1000

        if (date.before(Date())) {
            date.time += 24 * 60 * 60 * 1000
            scheduler.schedule(dailyTask, date, 24 * 60 * 60 * 1000)
        } else {
            scheduler.schedule(dailyTask, date, 24 * 60 * 60 * 1000)
        }

        val sendNewsToTarget: suspend (Contact) -> Unit = {
            try {
                it.sendMessage("这是今天的新闻速报 \nq(≧▽≦q)")
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
                it.sendMessage("这是今天的 B 站番剧 \n( •̀ ω •́ )✧")
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

    override fun onDisable() {
        dailyTask.cancel()
        scheduler.cancel()
        CommandManager.unregisterCommand(NewsGroupCommand)
        CommandManager.unregisterCommand(AnimeGroupCommand)
    }
}
