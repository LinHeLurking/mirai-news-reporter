package online.ruin_of_future.reporter.config


import kotlinx.serialization.Serializable
import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

@Serializable
data class ReporterChatMessageModel(
    val dailyTriggers: List<String>,
    val animeTriggers: List<String>,
    val newsTriggers: List<String>,
    val separators: List<String>,
    val waitMessage: String,
    val animeDailyMessage:String,
    val animeReplayMessage: String,
    val noAnimeMessage: String,
    val newsDailyMessage:String,
    val newsReplayMessage: String,
    val noDisturbingGroupMessage:String,
    val errorMessage: String,
) {
    companion object {
        val DEFAULT = ReporterChatMessageModel(
            dailyTriggers = listOf("今日", "每日", "日常", "daily", "Daily"),
            animeTriggers = listOf("番剧", "动画", "B 站番剧", "B站番剧"),
            newsTriggers = listOf("新闻", "速报", "新闻速报"),
            separators = listOf(" ", "-"),
            waitMessage = "稍等哦 QwQ",
            animeDailyMessage = "早上好呀, 这是今天的 B 站番剧 \n( •̀ ω •́ )✧",
            animeReplayMessage = "这是今天的 B 站番剧 \n( •̀ ω •́ )✧",
            noAnimeMessage = "好像今天没有放送呢 >_<",
            newsDailyMessage = "早上好呀, 这是今天的新闻速报 \nq(≧▽≦q)",
            newsReplayMessage = "这是今天的新闻速报 \nq(≧▽≦q)",
            noDisturbingGroupMessage = "为了防止打扰到网友，这个群不在日报白名单呢 QwQ",
            errorMessage = "出错啦, 等会再试试吧 ￣へ￣",
        )
    }
}

object ReporterConfig : AutoSavePluginConfig("ReporterConfig") {
    val chatMessage by value(ReporterChatMessageModel.DEFAULT)
}
