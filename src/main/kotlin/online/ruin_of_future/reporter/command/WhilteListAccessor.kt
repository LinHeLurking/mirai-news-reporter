package online.ruin_of_future.reporter.command

import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.contact.Group

private fun wrapTag(tag: String?): String {
    if (tag == null) {
        return ""
    }
    return "($tag)"
}

suspend fun addWhiteList(
    bot: Bot,
    sender: CommandSender,
    target: Group,
    whiteList: MutableMap<Long, MutableList<Long>>,
    tag: String? = null,
) {
    try {
        if (whiteList.containsKey(bot.id)) {
            whiteList[bot.id]!!.add(target.id)
        } else {
            whiteList[bot.id] = mutableListOf(target.id)
        }
        sender.sendMessage("${wrapTag(tag)} 添加 ${target.name} 成功")
    } catch (e: Exception) {
        sender.sendMessage("${wrapTag(tag)} 添加 ${target.name} 失败 QAQ")
    }
}

suspend fun removeWhiteList(
    bot: Bot,
    sender: CommandSender,
    target: Group,
    whiteList: MutableMap<Long, MutableList<Long>>,
    tag: String? = null,
) {
    try {
        if (whiteList.containsKey(bot.id)) {
            whiteList[bot.id]!!.remove(target.id)
        }
        sender.sendMessage("${wrapTag(tag)} 移除 ${target.name} 成功")
    } catch (e: Exception) {
        sender.sendMessage("${wrapTag(tag)} 移除 ${target.name} 失败 QAQ")
    }
}

suspend fun listWhiteList(
    bot: Bot,
    sender: CommandSender,
    whiteList: MutableMap<Long, MutableList<Long>>,
    tag: String? = null,
) {
    try {
        val resStrBuilder = StringBuilder()
        resStrBuilder.append("${wrapTag(tag)} 来自 ${bot.nick}(${bot.id}) 的配置，该机器人的白名单如下\n")
        if (whiteList[bot.id]?.isEmpty() != false) {
            resStrBuilder.append("    白名单为空呢 >_<")
        } else {
            for (groupId in whiteList[bot.id]!!) {
                resStrBuilder.append("$groupId, ${bot.getGroup(groupId)?.name}")
                resStrBuilder.append('\n')
            }
        }
        sender.sendMessage(resStrBuilder.toString())
    } catch (e: Exception) {
        sender.sendMessage("${wrapTag(tag)} 出错啦 QAQ")
    }
}
