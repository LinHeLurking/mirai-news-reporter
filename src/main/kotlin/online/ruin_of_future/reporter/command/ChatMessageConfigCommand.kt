package online.ruin_of_future.reporter.command

import net.mamoe.mirai.console.command.CommandSender
import net.mamoe.mirai.console.command.CompositeCommand
import online.ruin_of_future.reporter.ReporterPlugin
import online.ruin_of_future.reporter.config.ReporterConfig

object ChatMessageConfigCommand : CompositeCommand(
    ReporterPlugin, "reporter_msg", description = "配置 Bot 交互消息"
) {
    private fun splitList(triggers: String): List<String> {
        return triggers.split(',', '，', ';', '；').toList()
    }

    @SubCommand("dailyTriggers", "dailyTrigger")
    suspend fun setDailyTriggers(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.dailyTriggers.clear()
        list.forEach {
            ReporterConfig.dailyTriggers.add(it)
        }

        sender.sendMessage("设置 dailyTriggers 为 $list")
    }

    @SubCommand("animeTriggers", "animeTrigger")
    suspend fun setAnimeTriggers(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.animeTriggers.clear()
        list.forEach {
            ReporterConfig.animeTriggers.add(it)
        }

        sender.sendMessage("设置 animeTriggers 为 $list")
    }

    @SubCommand("newsTriggers", "newsTrigger")
    suspend fun setNewsTriggers(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.newsTriggers.clear()
        list.forEach {
            ReporterConfig.newsTriggers.add(it)
        }

        sender.sendMessage("设置 newsTriggers 为 $list")
    }

    @SubCommand("separators", "separator")
    suspend fun setSeparators(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.separators.clear()
        list.forEach {
            ReporterConfig.separators.add(it)
        }

        sender.sendMessage("设置 separators 为 $list")
    }

    @SubCommand("waitMessages", "waitMessage")
    suspend fun setWaitMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.waitMessages.clear()
        list.forEach {
            ReporterConfig.waitMessages.add(it)
        }

        sender.sendMessage("设置 waitMessages 为 $list")
    }

    @SubCommand("animeDailyMessages", "animeDailyMessage")
    suspend fun setAnimeDailyMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.animeDailyMessages.clear()
        list.forEach {
            ReporterConfig.animeDailyMessages.add(it)
        }

        sender.sendMessage("设置 animeDailyMessages 为 $list")
    }

    @SubCommand("animeReplyMessages", "animeReplyMessage")
    suspend fun setAnimeReplyMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.animeReplyMessages.clear()
        list.forEach {
            ReporterConfig.animeReplyMessages.add(it)
        }

        sender.sendMessage("设置 animeReplyMessages 为 $list")
    }

    @SubCommand("noAnimeMessages", "noAnimeMessage")
    suspend fun setNoAnimeMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.noAnimeMessages.clear()
        list.forEach {
            ReporterConfig.noAnimeMessages.add(it)
        }

        sender.sendMessage("设置 noAnimeMessages 为 $list")
    }

    @SubCommand("newsDailyMessages", "newsDailyMessage")
    suspend fun setNewsDailyMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.newsDailyMessages.clear()
        list.forEach {
            ReporterConfig.newsDailyMessages.add(it)
        }

        sender.sendMessage("设置 newsDailyMessages 为 $list")
    }

    @SubCommand("newsReplyMessages", "newsReplyMessage")
    suspend fun setNewsReplyMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.newsReplyMessages.clear()
        list.forEach {
            ReporterConfig.newsReplyMessages.add(it)
        }

        sender.sendMessage("设置 newsReplyMessages 为 $list")
    }

    @SubCommand("noDisturbingGroupMessages", "noDisturbingGroupMessage")
    suspend fun setNoDisturbingGroupMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.noDisturbingGroupMessages.clear()
        list.forEach {
            ReporterConfig.noDisturbingGroupMessages.add(it)
        }

        sender.sendMessage("设置 noDisturbingGroupMessages 为 $list")
    }

    @SubCommand("errorMessages", "errorMessage")
    suspend fun setErrorMessages(sender: CommandSender, args: String) {
        val list = splitList(args)
        ReporterConfig.errorMessages.clear()
        list.forEach {
            ReporterConfig.errorMessages.add(it)
        }

        sender.sendMessage("设置 errorMessages 为 $list")
    }
}