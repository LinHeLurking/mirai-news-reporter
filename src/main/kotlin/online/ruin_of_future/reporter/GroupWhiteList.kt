package online.ruin_of_future.reporter

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value
import net.mamoe.mirai.console.util.ConsoleExperimentalApi
import net.mamoe.mirai.contact.Group


@OptIn(ConsoleExperimentalApi::class)
object ReporterGroupCommand : CompositeCommand(
    ReporterPlugin,
    "reporter_group", "日报群组", // "manage" 是主指令名
    description = "每日新闻播报的群组白名单管理"
) {
    @SubCommand("list", "显示", "展示", "show")
    suspend fun CommandSender.list() {
        val resStrBuilder = StringBuilder()
        if (GroupWhiteList.isEmpty()) {
            resStrBuilder.append("白名单为空呢 >_<")
        } else {
            for (groupId in GroupWhiteList) {
                resStrBuilder.append(groupId)
                resStrBuilder.append('\n')
            }
        }
        sendMessage(resStrBuilder.toString())
    }

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(target: Group) {
        GroupWhiteList.add(target.id)
        sendMessage("添加 ${target.name} 成功")
    }

    @SubCommand("delete", "remove", "删除", "移除")
    suspend fun CommandSender.remove(target: Group) {
        GroupWhiteList.remove(target.id)
        sendMessage("移除 ${target.name} 成功")
    }
}


object GroupWhiteList : AutoSavePluginConfig("groupWhiteList") {
    private val groupIds: MutableList<Long> by value()
    fun get(i: Int): Long {
        return groupIds[i]
    }

    fun set(i: Int, groupId: Long) {
        groupIds[i] = groupId
    }

    val size: Int
        get() = groupIds.size

    fun contains(element: Long): Boolean {
        return groupIds.contains(element)
    }

    fun isEmpty(): Boolean {
        return groupIds.isEmpty()
    }

    fun add(elem: Long): Boolean {
        return groupIds.add(elem)
    }

    fun remove(elem: Long): Boolean {
        return groupIds.remove(elem)
    }


    operator fun iterator(): Iterator<Long> {
        return object : Iterator<Long> {
            var i = 0;
            override fun hasNext(): Boolean {
                return i < groupIds.size
            }

            override fun next(): Long {
                return groupIds[i++]
            }
        }
    }
}