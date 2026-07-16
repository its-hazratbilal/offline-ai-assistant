package com.hazratbilal.offlineaiassistant.ui.features.about

data class OpenSourceLibrary(
    val name: String,
    val license: String,
    val url: String,
    val description: String
)

object OpenSourceLibraries {

    val libraries = listOf(

        OpenSourceLibrary(
            name = "Kotlin",
            license = "Apache License 2.0",
            url = "https://kotlinlang.org/",
            description = "Primary programming language used to build the application."
        ),

        OpenSourceLibrary(
            name = "Jetpack Compose",
            license = "Apache License 2.0",
            url = "https://developer.android.com/jetpack/compose",
            description = "Modern declarative UI toolkit for Android."
        ),

        OpenSourceLibrary(
            name = "Material 3",
            license = "Apache License 2.0",
            url = "https://m3.material.io/",
            description = "Material Design components used throughout the app."
        ),

        OpenSourceLibrary(
            name = "Navigation Compose",
            license = "Apache License 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/navigation",
            description = "Navigation framework for Jetpack Compose."
        ),

        OpenSourceLibrary(
            name = "Dagger Hilt",
            license = "Apache License 2.0",
            url = "https://dagger.dev/hilt/",
            description = "Dependency injection framework."
        ),

        OpenSourceLibrary(
            name = "Room",
            license = "Apache License 2.0",
            url = "https://developer.android.com/jetpack/androidx/releases/room",
            description = "SQLite persistence library for storing chats and local data."
        ),

        OpenSourceLibrary(
            name = "OkHttp",
            license = "Apache License 2.0",
            url = "https://square.github.io/okhttp/",
            description = "HTTP client used for downloading AI models."
        ),

        OpenSourceLibrary(
            name = "Kotlin Coroutines & Flow",
            license = "Apache License 2.0",
            url = "https://github.com/Kotlin/kotlinx.coroutines",
            description = "Asynchronous programming and reactive data streams."
        ),

        OpenSourceLibrary(
            name = "llama.cpp",
            license = "MIT License",
            url = "https://github.com/ggml-org/llama.cpp",
            description = "Native on-device inference engine used to run GGUF language models."
        ),

        OpenSourceLibrary(
            name = "Open-source GGUF AI Models",
            license = "Various Open-source Licenses",
            url = "https://huggingface.co/models?library=gguf",
            description = "Supports GGUF models including Gemma, Qwen, TinyLlama and SmolLM2. Each model is distributed under its own license and terms of use."
        )
    )
}