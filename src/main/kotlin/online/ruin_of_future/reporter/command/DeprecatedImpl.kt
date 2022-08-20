package online.ruin_of_future.reporter.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.*
import net.mamoe.mirai.console.command.CommandSender.Companion.asCommandSender
import net.mamoe.mirai.console.command.ConsoleCommandSender.sendMessage
import net.mamoe.mirai.contact.Group
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.data.AnimeGroupWhiteList
import online.ruin_of_future.reporter.data.NewsGroupWhiteList

private val deprecationMessage =
    "本命令将被废弃，推荐使用统一的白名单管理命令 ${CommandManager.INSTANCE.commandPrefix}${WhiteListCommand.primaryName} 来帮助管理白名单"

@Deprecated("Use consistent commands to control both anime and news.")
object NewsGroupCommand : CompositeCommand(
    ReporterPlugin,
    "news_group", "新闻群组", // "manage" 是主指令名
    description = "每日新闻播报的群组白名单管理"
) {

    @SubCommand("list", "显示", "展示", "show")
    suspend fun CommandSender.list() {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            listWhiteList(bot, user.asCommandSender(true), NewsGroupWhiteList.groupIdsPerBot)
        } else if (this is ConsoleCommandSender) {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                listWhiteList(bot, this, NewsGroupWhiteList.groupIdsPerBot)
            }
        }
    }

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(target: Group) {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            addWhiteList(
                bot,
                user.asCommandSender(true),
                target,
                NewsGroupWhiteList.groupIdsPerBot
            )
        } else if (this is ConsoleCommandSender) {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                addWhiteList(
                    bot,
                    this,
                    target,
                    NewsGroupWhiteList.groupIdsPerBot
                )
            }
        }
    }

    @SubCommand("delete", "remove", "删除", "移除")
    suspend fun CommandSender.remove(target: Group) {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            removeWhiteList(bot, user.asCommandSender(true), target, NewsGroupWhiteList.groupIdsPerBot)
        } else {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                removeWhiteList(bot, this, target, NewsGroupWhiteList.groupIdsPerBot)
            }
        }
    }
}

@Deprecated("Use consistent commands to control both anime and news.")
object AnimeGroupCommand : CompositeCommand(
    ReporterPlugin,
    "anime_group", "动画群组", // "manage" 是主指令名
    description = "每日动画播报的群组白名单管理"
) {

    @SubCommand("list", "显示", "展示", "show")
    suspend fun CommandSender.list() {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            listWhiteList(bot, user.asCommandSender(true), AnimeGroupWhiteList.groupIdsPerBot)
        } else if (this is ConsoleCommandSender) {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                listWhiteList(bot, this, AnimeGroupWhiteList.groupIdsPerBot)
            }
        }
    }

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(target: Group) {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            addWhiteList(
                bot,
                user.asCommandSender(true),
                target,
                AnimeGroupWhiteList.groupIdsPerBot
            )
        } else if (this is ConsoleCommandSender) {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                addWhiteList(
                    bot,
                    this,
                    target,
                    AnimeGroupWhiteList.groupIdsPerBot
                )
            }
        }
    }

    @SubCommand("delete", "remove", "删除", "移除")
    suspend fun CommandSender.remove(target: Group) {
        if (this is UserCommandSender) {
            user.sendMessage(deprecationMessage)
            removeWhiteList(bot, user.asCommandSender(true), target, AnimeGroupWhiteList.groupIdsPerBot)
        } else {
            sendMessage(deprecationMessage)
            for (bot in Bot.instances) {
                removeWhiteList(bot, this, target, AnimeGroupWhiteList.groupIdsPerBot)
            }
        }
    }
}
