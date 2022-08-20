package online.ruin_of_future.reporter.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object ReporterConfig : AutoSavePluginConfig("NewsReporterConfig") {
    val anime_triggers: List<String> by value()
    val news_triggers: List<String> by value()
}