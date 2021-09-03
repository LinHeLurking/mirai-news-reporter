package online.ruin_of_future.reporter

import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Contact.Companion.sendImage
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.event.subscribeFriendMessages
import net.mamoe.mirai.event.subscribeGroupMessages
import net.mamoe.mirai.event.subscribeMessages
import net.mamoe.mirai.message.data.Image
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO

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
        groupWhiteList.reload()

        CommandManager.registerCommand(ReporterGroupCommand)

        this.globalEventChannel().subscribeGroupMessages {
            matching(Regex("(每日|今日)?(新闻|速报)")) {
                logger.info("$senderName 发起了请求...")
                launch {
                    if (groupWhiteList.contains(group.id)) {
                        try {
                            val os = ByteArrayOutputStream()
                            ImageIO.write(newsCrawler.newsToday(), "png", os)
                            val inStream = ByteArrayInputStream(os.toByteArray())
                            group.sendImage(inStream)
                        } catch (e: Exception) {
                            group.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
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
                        val os = ByteArrayOutputStream()
                        ImageIO.write(newsCrawler.newsToday(), "png", os)
                        val inStream = ByteArrayInputStream(os.toByteArray())
                        sender.sendImage(inStream)
                    } catch (e: Exception) {
                        sender.sendMessage("出错啦, 等会再试试吧 ￣へ￣")
                    }
                }
            }
        }
    }
}