package online.ruin_of_future.reporter.data

interface GroupWhiteList {
    val groupIdsPerBot: MutableMap<Long, MutableList<Long>>
    val tag: String
}