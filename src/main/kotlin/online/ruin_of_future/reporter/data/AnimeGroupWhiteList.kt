package online.ruin_of_future.reporter.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object AnimeGroupWhiteList : AutoSavePluginConfig("bangumiGroupWhiteList"), GroupWhiteList {
    override val groupIdsPerBot: MutableMap<Long, MutableList<Long>> by value()
    override val tag: String
        get() = "Anime"
}