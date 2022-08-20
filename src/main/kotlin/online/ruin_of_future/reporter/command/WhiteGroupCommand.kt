package online.ruin_of_future.reporter.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CommandSender.Companion.asCommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.command.ConsoleCommandSender
import net.mamoe.mirai.console.command.UserCommandSender
import net.mamoe.mirai.contact.Group
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.data.GroupWhiteList
import online.ruin_of_future.reporter.data.NewsGroupWhiteList

object WhiteGroupCommand : CompositeCommand(
    ReporterPlugin, "white_group", description = "统一的白名单管理"
) {
    private fun getWhiteList(category: String?): List<GroupWhiteList> {
        val animeRegex = Regex("anime|Anime|动画")
        val newsRegex = Regex("news|News|新闻|速报")
        return if (category == null) {
            listOf(AnimeGroupWhiteList, NewsGroupWhiteList)
        } else if (category.matches(animeRegex)) {
            listOf(AnimeGroupWhiteList)
        } else if (category.matches(newsRegex)) {
            listOf(NewsGroupWhiteList)
        } else {
            emptyList()
        }
    }

    @SubCommand("list", "show", "显示", "展示")
    suspend fun list(sender: CommandSender, category: String? = null) {
        getWhiteList(category).forEach {
            if (sender is UserCommandSender) {
                listWhiteList(sender.bot, sender.user.asCommandSender(true), it.groupIdsPerBot, it.tag)
            } else if (sender is ConsoleCommandSender) {
                for (bot in Bot.instances) {
                    listWhiteList(bot, sender, it.groupIdsPerBot, it.tag)
                }
            }
        }
    }

    @SubCommand("add", "添加")
    suspend fun add(sender: CommandSender, target: Group, category: String? = null) {
        getWhiteList(category).forEach {
            if (sender is UserCommandSender) {
                addWhiteList(sender.bot, sender.user.asCommandSender(true), target, it.groupIdsPerBot, it.tag)
            } else if (sender is ConsoleCommandSender) {
                for (bot in Bot.instances) {
                    addWhiteList(
                        bot, sender, target, it.groupIdsPerBot, it.tag
                    )
                }
            }
        }

    }

    @SubCommand("delete", "remove", "删除", "移除")
    suspend fun remove(sender: CommandSender, target: Group, category: String? = null) {
        getWhiteList(category).forEach {
            if (sender is UserCommandSender) {
                removeWhiteList(sender.bot, sender.user.asCommandSender(true), target, it.groupIdsPerBot, it.tag)
            } else {
                for (bot in Bot.instances) {
                    removeWhiteList(bot, sender, target, it.groupIdsPerBot, it.tag)
                }
            }
        }

    }
}