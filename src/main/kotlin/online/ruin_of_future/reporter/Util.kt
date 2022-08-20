package online.ruin_of_future.reporter

fun regexOrBuilder(list: List<String>): String {
    val sb = StringBuilder()
    sb.append('(')
    list.forEachIndexed { index, s ->
        sb.append(s)
        if (index < list.size - 1) {
            sb.append('|')
        }
    }
    sb.append(')')
    return sb.toString()
}