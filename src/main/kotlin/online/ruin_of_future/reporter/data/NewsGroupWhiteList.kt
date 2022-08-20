package online.ruin_of_future.reporter.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object NewsGroupWhiteList : AutoSavePluginConfig("newsGroupWhiteList") {
    val groupIdsPerBot: MutableMap<Long, MutableList<Long>> by value()
}