package ru.skillbranch.devintensive.extensions

fun String.truncate(countTruncation: Int = 16) : String {
    val truncateString = this.take(countTruncation).dropLastWhile { it.isWhitespace() }
    return if (truncateString.filter { !it.isWhitespace() }.count() == this.filter { !it.isWhitespace() }.count()) truncateString else "$truncateString..."
}

fun String.stripHtml() = this.replace("<.*?>".toRegex(), "").replace("\\s\\s+".toRegex(), " ")