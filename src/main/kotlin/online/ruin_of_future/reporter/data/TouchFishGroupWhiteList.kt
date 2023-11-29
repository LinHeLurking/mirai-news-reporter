package online.ruin_of_future.reporter.data

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object TouchFishGroupWhiteList : AutoSavePluginConfig("touchFishGroupWhiteList"), GroupWhiteList {
    override val groupIdsPerBot: MutableMap<Long, MutableList<Long>> by value()
    override val tag: String
        get() = "TouchFish"
}