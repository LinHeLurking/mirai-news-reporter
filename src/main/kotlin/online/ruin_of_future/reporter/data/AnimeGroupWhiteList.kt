package online.ruin_of_future.reporter.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object AnimeGroupWhiteList : AutoSavePluginConfig("bangumiGroupWhiteList") {
    val groupIdsPerBot: MutableMap<Long, MutableList<Long>> by value()
}