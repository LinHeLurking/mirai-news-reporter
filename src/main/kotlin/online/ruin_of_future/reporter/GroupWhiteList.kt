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
    description = "管理日报群组白名单"
) {
    @SubCommand("list", "显示", "展示", "show")
    suspend fun CommandSender.list() {
        val resStrBuilder = StringBuilder()
        if (groupWhiteList.isEmpty()) {
            resStrBuilder.append("白名单为空呢 >_<")
        } else {
            for (groupId in groupWhiteList) {
                resStrBuilder.append(groupId)
            }
        }
        sendMessage(resStrBuilder.toString())
    }

    @SubCommand("add", "添加")
    suspend fun CommandSender.add(target: Group) {
        groupWhiteList.add(target.id)
        sendMessage("添加 ${target.name} 成功")
    }
}


object groupWhiteList : AutoSavePluginConfig("groupWhiteList") {
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

    fun add(elem: Long) {
        groupIds.add(elem)
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