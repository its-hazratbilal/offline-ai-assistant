package com.hazratbilal.offlineaiassistant.data.model

object AvailableModels {

    val SMOLLM2_360M = LlmModel(
        id = "smollm2_360m",
        name = "SmolLM2",
        description = "Fastest model for older phones and basic conversations.",
        downloadSize = "258 MB",
        ramRequirement = "2 GB+",
        downloadUrl = "https://huggingface.co/bartowski/SmolLM2-360M-Instruct-GGUF/resolve/main/SmolLM2-360M-Instruct-Q4_K_M.gguf",
        fileName = "SmolLM2-360M-Instruct-Q4_K_M.gguf",
        isLightweight = true
    )

    val QWEN2_5_0_5B = LlmModel(
        id = "qwen2_5_0_5b",
        name = "Qwen 2.5 Mini",
        description = "Fast and efficient for everyday tasks.",
        downloadSize = "468 MB",
        ramRequirement = "3 GB+",
        downloadUrl = "https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/main/qwen2.5-0.5b-instruct-q4_k_m.gguf",
        fileName = "qwen2.5-0.5b-instruct-q4_k_m.gguf",
        isLightweight = true
    )

    val TINYLLAMA_1_1B = LlmModel(
        id = "tinyllama_1_1b",
        name = "TinyLlama",
        description = "Balanced speed and quality for everyday use.",
        downloadSize = "637 MB",
        ramRequirement = "4 GB+",
        downloadUrl = "https://huggingface.co/TheBloke/TinyLlama-1.1B-Chat-v1.0-GGUF/resolve/main/tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf",
        fileName = "tinyllama-1.1b-chat-v1.0.Q4_K_M.gguf",
        isLightweight = true
    )

    val GEMMA_3_1B = LlmModel(
        id = "gemma3_1b",
        name = "Gemma 3",
        description = "Recommended for most devices with the best balance of speed and quality.",
        downloadSize = "768 MB",
        ramRequirement = "6 GB+",
        downloadUrl = "https://huggingface.co/bartowski/google_gemma-3-1b-it-GGUF/resolve/main/google_gemma-3-1b-it-Q4_K_M.gguf",
        fileName = "google_gemma-3-1b-it-Q4_K_M.gguf",
        recommended = true
    )

    val GEMMA_3_4B = LlmModel(
        id = "gemma3_4b",
        name = "Gemma 3 Advanced",
        description = "Highest quality model for flagship devices.",
        downloadSize = "2.37 GB",
        ramRequirement = "8 GB+",
        downloadUrl = "https://huggingface.co/bartowski/google_gemma-3-4b-it-GGUF/resolve/main/google_gemma-3-4b-it-Q4_K_M.gguf",
        fileName = "google_gemma-3-4b-it-Q4_K_M.gguf"
    )

    val models = listOf(
        SMOLLM2_360M,
        QWEN2_5_0_5B,
        TINYLLAMA_1_1B,
        GEMMA_3_1B,
        GEMMA_3_4B
    )

    val default: LlmModel
        get() = GEMMA_3_1B
}