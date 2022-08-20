//package online.ruin_of_future.reporter
//
//import net.mamoe.mirai.Bot
//import net.mamoe.mirai.console.command.CommandSender
//import net.mamoe.mirai.console.command.CommandSender.Companion.asCommandSender
//import net.mamoe.mirai.console.command.CompositeCommand
//import net.mamoe.mirai.console.command.ConsoleCommandSender
//import net.mamoe.mirai.console.command.UserCommandSender
//import net.mamoe.mirai.console.data.AutoSavePluginConfig
//import net.mamoe.mirai.console.data.value
//import net.mamoe.mirai.console.util.ConsoleExperimentalApi
//import net.mamoe.mirai.contact.Group
//import net.mamoe.mirai.contact.User
//
//
//private suspend fun addWhiteList(
//    bot: Bot,
//    sender: CommandSender,
//    target: Group,
//    whiteList: MutableMap<Long, MutableList<Long>>
//) {
//    try {
//        if (whiteList.containsKey(bot.id)) {
//            whiteList[bot.id]!!.add(target.id)
//        } else {
//            whiteList[bot.id] = mutableListOf(target.id)
//        }
//        sender.sendMessage("添加 ${target.name} 成功")
//    } catch (e: Exception) {
//        sender.sendMessage("添加 ${target.name} 失败 QAQ")
//    }
//}
//
//private suspend fun removeWhiteList(
//    bot: Bot,
//    sender: CommandSender,
//    target: Group,
//    whiteList: MutableMap<Long, MutableList<Long>>
//) {
//    try {
//        if (whiteList.containsKey(bot.id)) {
//            whiteList[bot.id]!!.remove(target.id)
//        }
//        sender.sendMessage("移除 ${target.name} 成功")
//    } catch (e: Exception) {
//        sender.sendMessage("移除 ${target.name} 失败 QAQ")
//    }
//}
//
//private suspend fun listWhiteList(
//    bot: Bot,
//    sender: CommandSender,
//    whiteList: MutableMap<Long, MutableList<Long>>
//) {
//    try {
//        val resStrBuilder = StringBuilder()
//        resStrBuilder.append("来自 ${bot.nick}(${bot.id}) 的配置，该机器人的白名单如下\n")
//        if (whiteList[bot.id]?.isEmpty() != false) {
//            resStrBuilder.append("白名单为空呢 >_<")
//        } else {
//            for (groupId in whiteList[bot.id]!!) {
//                resStrBuilder.append("$groupId, ${bot.getGroup(groupId)?.name}")
//                resStrBuilder.append('\n')
//            }
//        }
//        sender.sendMessage(resStrBuilder.toString())
//    } catch (e: Exception) {
//        sender.sendMessage("出错啦 QAQ")
//    }
//}
//
//@Deprecated("Use consistent commands to control both anime and news.")
//object NewsGroupCommand : CompositeCommand(
//    ReporterPlugin,
//    "news_group", "新闻群组", // "manage" 是主指令名
//    description = "每日新闻播报的群组白名单管理"
//) {
//
//
//    @SubCommand("list", "显示", "展示", "show")
//    suspend fun CommandSender.list() {
//        if (this is UserCommandSender) {
//            listWhiteList(bot, user.asCommandSender(true), NewsGroupWhiteList.groupIdsPerBot)
//        } else if (this is ConsoleCommandSender) {
//            for (bot in Bot.instances) {
//                listWhiteList(bot, this, NewsGroupWhiteList.groupIdsPerBot)
//            }
//        }
//    }
//
//    @SubCommand("add", "添加")
//    suspend fun CommandSender.add(target: Group) {
//        if (this is UserCommandSender) {
//            addWhiteList(
//                bot,
//                user.asCommandSender(true),
//                target,
//                NewsGroupWhiteList.groupIdsPerBot
//            )
//        } else if (this is ConsoleCommandSender) {
//            for (bot in Bot.instances) {
//                addWhiteList(
//                    bot,
//                    this,
//                    target,
//                    NewsGroupWhiteList.groupIdsPerBot
//                )
//            }
//        }
//    }
//
//    @SubCommand("delete", "remove", "删除", "移除")
//    suspend fun CommandSender.remove(target: Group) {
//        if (this is UserCommandSender) {
//            removeWhiteList(bot, user.asCommandSender(true), target, NewsGroupWhiteList.groupIdsPerBot)
//        } else {
//            for (bot in Bot.instances) {
//                removeWhiteList(bot, this, target, NewsGroupWhiteList.groupIdsPerBot)
//            }
//        }
//    }
//}
//
//@Deprecated("Use consistent commands to control both anime and news.")
//object AnimeGroupCommand : CompositeCommand(
//    ReporterPlugin,
//    "anime_group", "动画群组", // "manage" 是主指令名
//    description = "每日动画播报的群组白名单管理"
//) {
//
//
//    @SubCommand("list", "显示", "展示", "show")
//    suspend fun CommandSender.list() {
//        if (this is UserCommandSender) {
//            listWhiteList(bot, user.asCommandSender(true), AnimeGroupWhiteList.groupIdsPerBot)
//        } else if (this is ConsoleCommandSender) {
//            for (bot in Bot.instances) {
//                listWhiteList(bot, this, AnimeGroupWhiteList.groupIdsPerBot)
//            }
//        }
//    }
//
//    @SubCommand("add", "添加")
//    suspend fun CommandSender.add(target: Group) {
//        if (this is UserCommandSender) {
//            addWhiteList(
//                bot,
//                user.asCommandSender(true),
//                target,
//                AnimeGroupWhiteList.groupIdsPerBot
//            )
//        } else if (this is ConsoleCommandSender) {
//            for (bot in Bot.instances) {
//                addWhiteList(
//                    bot,
//                    this,
//                    target,
//                    AnimeGroupWhiteList.groupIdsPerBot
//                )
//            }
//        }
//    }
//
//    @SubCommand("delete", "remove", "删除", "移除")
//    suspend fun CommandSender.remove(target: Group) {
//        if (this is UserCommandSender) {
//            removeWhiteList(bot, user.asCommandSender(true), target, AnimeGroupWhiteList.groupIdsPerBot)
//        } else {
//            for (bot in Bot.instances) {
//                removeWhiteList(bot, this, target, AnimeGroupWhiteList.groupIdsPerBot)
//            }
//        }
//    }
//}
