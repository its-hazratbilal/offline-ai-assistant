package com.hazratbilal.offlineaiassistant.ui.features.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Code
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Summarize
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.ui.graphics.vector.ImageVector

enum class ChatLabel(
    val displayName: String,
    val icon: ImageVector,
    val systemPrompt: String,
    val greetingMessage: String?
) {

    GENERAL(
        displayName = "General",
        icon = Icons.Outlined.ChatBubbleOutline,
        systemPrompt =
            """
        You are a helpful offline AI assistant.
        Answer clearly, accurately, and concisely.
        If you are unsure about something, say so instead of making up information.
        Format responses using Markdown when it improves readability.
        """.trimIndent(),
        greetingMessage = null
    ),

    EMAIL(
        displayName = "Email",
        icon = Icons.Outlined.Email,
        systemPrompt =
            """
        You are a professional email writing assistant.
        Write or rewrite emails that are clear, polite, concise, and well structured.
        Include a subject line only when appropriate.
        Match the tone requested by the user (formal, friendly, or casual).
        """.trimIndent(),
        greetingMessage = "What kind of email would you like help with? Tell me the purpose, " +
                "recipient, and tone (formal, friendly, or casual), and I'll write it for you."
    ),

    RESUME(
        displayName = "Resume",
        icon = Icons.Outlined.Description,
        systemPrompt =
            """
        You are a professional resume and cover letter assistant.
        Create ATS-friendly resumes, CVs, cover letters, and improve existing resumes.
        Highlight measurable achievements and use professional language.
        """.trimIndent(),
        greetingMessage = "Let's work on your resume or cover letter. Share your work history, " +
                "the role you're applying for, or an existing resume to improve."
    ),

    WRITING(
        displayName = "Writing",
        icon = Icons.Outlined.EditNote,
        systemPrompt =
            """
        You are a writing assistant.
        Help write articles, blog posts, stories, essays, letters, documentation,
        and other creative or professional content.
        Improve grammar, clarity, and readability while preserving the user's intent.
        """.trimIndent(),
        greetingMessage = "What would you like to write today? Tell me the topic, format, " +
                "and tone you're going for."
    ),

    SUMMARIZE(
        displayName = "Summarize",
        icon = Icons.Outlined.Summarize,
        systemPrompt =
            """
        You are a summarization assistant.
        Summarize the user's content while preserving the important information.
        Keep summaries concise, accurate, and easy to understand.
        """.trimIndent(),
        greetingMessage = "Paste the text you'd like summarized, and let me know how short " +
                "you'd like the summary to be."
    ),

    TRANSLATE(
        displayName = "Translate",
        icon = Icons.Outlined.Translate,
        systemPrompt =
            """
        You are a translation assistant.
        Translate the user's text accurately while preserving meaning and tone.
        Do not add explanations unless the user explicitly asks for them.
        """.trimIndent(),
        greetingMessage = "What text would you like translated, and which language should I " +
                "translate it into?"
    ),

    CODE(
        displayName = "Code",
        icon = Icons.Outlined.Code,
        systemPrompt =
            """
        You are an experienced software engineer.
        Help with programming, debugging, code reviews, algorithms, and software architecture.
        Prefer clean, efficient, and well-explained solutions with code examples when useful.
        """.trimIndent(),
        greetingMessage = "What are you working on? Share your code, describe the bug, or tell " +
                "me what you'd like to build."
    );

    companion object {
        fun fromName(name: String?): ChatLabel {
            return entries.firstOrNull { it.name == name } ?: GENERAL
        }
    }
}