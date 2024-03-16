package online.ruin_of_future.reporter

import net.mamoe.mirai.console.command.CommandManager
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import online.ruin_of_future.reporter.chat_reply.AnimeChatReply
import online.ruin_of_future.reporter.chat_reply.NewsChatReply
import online.ruin_of_future.reporter.chat_reply.TouchFishReply

import online.ruin_of_future.reporter.command.ChatMessageConfigCommand

import online.ruin_of_future.reporter.command.WhiteListCommand
import online.ruin_of_future.reporter.config.ReporterConfig
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.data.NewsGroupWhiteList
import online.ruin_of_future.reporter.data.TouchFishGroupWhiteList
import online.ruin_of_future.reporter.tasks.MorningReportTask
import java.time.ZoneId
import java.util.*

object ReporterPlugin : KotlinPlugin(
    JvmPluginDescription(
        id = "online.ruin_of_future.reporter",
        version = "1.6.0",
    ) {
        name("Reporter")
        author("LinHeLurking")
    }
) {
    private val scheduler = Timer()
    private val dailyTask = MorningReportTask()

    // Convenient un-registration
    private val commands: MutableList<CompositeCommand> = mutableListOf()

    override fun onEnable() {
        ReporterConfig.reload()

        NewsGroupWhiteList.reload()
        AnimeGroupWhiteList.reload()
        TouchFishGroupWhiteList.reload()

//        commands.add(AnimeGroupCommand)
//        commands.add(NewsGroupCommand)
//        commands.add(TouchFishGroupCommand)
        commands.add(WhiteListCommand)
        commands.add(ChatMessageConfigCommand)

        commands.forEach {
            CommandManager.registerCommand(it)
        }

        val date = Date.from(
            Date().toInstant().atZone(ZoneId.systemDefault())
                .toLocalDate().atStartOfDay(ZoneId.systemDefault()).toInstant()
        ) // midnight today
        date.time += 10 * 60 * 60 * 1000 //更新时间改为10点

        if (date.before(Date())) {
            date.time += 24 * 60 * 60 * 1000
            scheduler.schedule(dailyTask, date, 24 * 60 * 60 * 1000)
        } else {
            scheduler.schedule(dailyTask, date, 24 * 60 * 60 * 1000)
        }

        NewsChatReply.registerToPlugin(this)
        AnimeChatReply.registerToPlugin(this)
        TouchFishReply.registerToPlugin(this)
    }

    override fun onDisable() {
        dailyTask.cancel()
        scheduler.cancel()
        commands.forEach {
            CommandManager.unregisterCommand(it)
        }
    }
}
