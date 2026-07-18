package com.hazratbilal.offlineaiassistant.utils

object MarkdownStripper {

    fun toPlainText(markdown: String): String {
        var text = markdown

        text = text.replace(Regex("^#{1,6}\\s+", RegexOption.MULTILINE), "")
        text = text.replace(Regex("\\*\\*\\*(.+?)\\*\\*\\*"), "$1")
        text = text.replace(Regex("\\*\\*(.+?)\\*\\*"), "$1")
        text = text.replace(Regex("__(.+?)__"), "$1")
        text = text.replace(Regex("\\*(.+?)\\*"), "$1")
        text = text.replace(Regex("_(.+?)_"), "$1")
        text = text.replace(Regex("`{1,3}([^`]+)`{1,3}"), "$1")
        text = text.replace(Regex("~~(.+?)~~"), "$1")
        text = text.replace(Regex("\\[([^\\]]+)]\\(([^)]+)\\)"), "$1 ($2)")
        text = text.replace(Regex("^[-*+]\\s+", RegexOption.MULTILINE), "• ")
        text = text.replace(Regex("^>\\s?", RegexOption.MULTILINE), "")
        text = text.replace(Regex("^(-{3,}|\\*{3,}|_{3,})$", RegexOption.MULTILINE), "")
        text = text.replace(Regex("\n{3,}"), "\n\n")

        return text.trim()
    }
}