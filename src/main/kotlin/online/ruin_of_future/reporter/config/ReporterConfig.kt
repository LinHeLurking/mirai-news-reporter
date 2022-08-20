package online.ruin_of_future.reporter.config


import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value

object ReporterConfig : AutoSavePluginConfig("ReporterConfig") {
    val dailyTriggers by value(mutableListOf("今日", "每日", "日常", "daily", "Daily"))
    val animeTriggers by value(mutableListOf("番剧", "动画", "B 站番剧", "B站番剧"))
    val newsTriggers by value(mutableListOf("新闻", "速报", "新闻速报"))
    val separators by value(mutableListOf(" ", "-"))
    val waitMessages by value(mutableListOf("稍等哦 QwQ"))
    val animeDailyMessages by value(mutableListOf("早上好呀, 这是今天的 B 站番剧 \n( •̀ ω •́ )✧"))
    val animeReplyMessages by value(mutableListOf("这是今天的 B 站番剧 \n( •̀ ω •́ )✧"))
    val noAnimeMessages by value(mutableListOf("好像今天没有放送呢 >_<"))
    val newsDailyMessages by value(mutableListOf("早上好呀, 这是今天的新闻速报 \nq(≧▽≦q)"))
    val newsReplyMessages by value(mutableListOf("这是今天的新闻速报 \nq(≧▽≦q)"))
    val noDisturbingGroupMessages by value(mutableListOf("为了防止打扰到网友，这个群不在日报白名单呢 QwQ"))
    val errorMessages by value(mutableListOf("出错啦, 等会再试试吧 ￣へ￣"))
}
