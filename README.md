# 🤖 Offline AI Assistant

<p align="center">
  <b>A fully offline, on-device AI chat assistant for Android — powered by local LLM inference via llama.cpp, built with Jetpack Compose, MVI-inspired Architecture, Hilt, Room, and Material 3.</b>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-Material%203-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white"/>
  <img src="https://img.shields.io/badge/llama.cpp-JNI-000000?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Hilt-DI-0F9D58?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Room-Database-FF6F00?style=for-the-badge"/>
</p>

---

## 📱 Overview

Offline AI Assistant is a privacy-first Android app that runs large language models entirely **on-device** — no internet connection required after downloading a model, no data ever leaves the phone.

Built on top of **llama.cpp** via a native JNI bridge, the app lets users download and switch between multiple GGUF-quantized models (from ultra-lightweight to flagship-tier), chat with grouped session history, apply task-specific prompt presets, and export or share conversations — all without a single network call to a backend server.

This project demonstrates production-style Android architecture: Clean Architecture layering, reactive state management, native library integration, and thoughtful UX around a genuinely resource-constrained domain (on-device inference).

---

# ✨ Features

- 🧠 Fully offline LLM inference via llama.cpp (JNI)
- 📥 Multi-model download manager with live progress, RAM-requirement checks, and resumable UI state
- 🔄 Seamless model switching mid-app, with safe native memory unload/reload
- 💬 Session-based chat history, grouped by recency (Today / Yesterday / This Week / Older)
- 🕵️ Private chat mode (fully ephemeral, never persisted)
- 🏷️ Task-specific prompt presets (General, Email, Resume, Writing, Summarize, Translate, Code) with hidden system prompts
- ⏹️ Stop/cancel generation mid-response
- 🎙️ Voice-to-text input
- 🔊 Text-to-speech playback per message
- 📋 Copy, download, and share individual messages or full conversations
- 🌗 Light / Dark / System theme support
- 🎨 Custom navigation drawer, no `Scaffold` — fully custom toolbar and layout
- 🔐 Zero network calls beyond model downloads — no analytics, no tracking, no backend

---

# 🛠 Tech Stack

| Technology | Purpose |
|------------|---------|
| Kotlin | Programming Language |
| Jetpack Compose | Declarative UI |
| Material 3 | UI Components |
| llama.cpp (JNI) | On-device LLM inference engine |
| Hilt | Dependency Injection |
| Room | Local chat & session persistence |
| DataStore | Preferences (theme, active model) |
| OkHttp | Model file downloads |
| Navigation Compose | Screen Navigation |
| Coroutines & Flow | Asynchronous & reactive state |
| Android SpeechRecognizer / TextToSpeech | Voice input & playback |

---

# 🧠 Architecture
Presentation (Compose UI)
│
├── Screen
├── ViewModel
└── UiState
│
▼
Domain
│
UseCase ──▶ Repository (interface)
│
▼
Data
│
RepositoryImpl ──▶ Room (chat/session persistence)
│
▼
AI
│
ModelManager ──▶ ModelSessionManager ──▶ LlmEngine (JNI) ──▶ llama.cpp (native)

## Model Lifecycle Flow
User selects a model
│
▼
RAM check (RamChecker)
│
▼
Download (OkHttp, resumable UI, one-at-a-time)
│
▼
ModelSessionManager.switchModel()
│
▼
ModelManager unloads old model (if any) → loads new model (JNI)
│
▼
ChatViewModel observes ModelSessionState → Ready
│
▼
Chat enabled

---

# 📂 Project Structure
offlineaiassistant
│   BaseApp.kt
│   MainActivity.kt
│
├───ai
│   ├───engine              → LlmEngine interface + LlamaCppEngine (JNI bridge)
│   ├───gguf                → GGUF file metadata reading
│   ├───manager             → ModelManager (load/unload/generate orchestration)
│   └───model                → LlmRequest / LlmResponse
│
├───data
│   ├───download             → Model download manager & state
│   ├───llm                  → ModelSessionManager (reactive session state)
│   ├───local
│   │   ├───dao              → Room DAOs (chat messages, sessions)
│   │   ├───database         → AppDatabase
│   │   ├───datasource       → LLM local data source
│   │   ├───entity           → Room entities
│   │   └───preferences      → DataStore-backed preferences
│   ├───mapper               → Entity ↔ Domain mappers
│   ├───model                → Available models catalog
│   └───repository           → ChatRepositoryImpl
│
├───di                       → Hilt modules
│
├───domain
│   ├───model                 → ChatMessage, ChatSession
│   ├───repository            → ChatRepository interface
│   └───usecase               → ChatUseCase, ConversationContextBuilder
│
├───ui
│   ├───common                → Shared composables (buttons, toolbar, fade edge)
│   ├───features
│   │   ├───about             → About & Open Source Licenses screens
│   │   ├───chat              → Chat screen, labels, ViewModel
│   │   ├───model_selection   → Model picker, download UI
│   │   ├───settings          → Settings screen
│   │   └───welcome           → Onboarding welcome screen
│   ├───navigation            → Nav graph & routes
│   └───theme                 → Colors, typography, shapes, theming
│
└───utils                     → App info, chat export, file download, RAM checks

---

# 🚀 Getting Started

## Clone

```bash
git clone https://github.com/its-hazratbilal/offline-ai-assistant.git
```

Open the project in Android Studio (with NDK & CMake support installed, required for building the native llama.cpp library).

---

## Native Build Requirements

This project builds `llama.cpp` from source via CMake/NDK as part of the Gradle build.

Ensure you have installed via SDK Manager:
- **NDK** (side by side)
- **CMake**

First sync/build may take longer than usual while the native library compiles.

---

## Run

Simply run the `app` module on a **physical device** (recommended over emulator, given native inference performance and RAM requirements).

Minimum SDK: **30**

Target SDK: **36**

On first launch, choose and download a model from the built-in model picker before chatting.

---

# 🎯 Learning Goals

This project demonstrates:

- Integrating native C++ inference (llama.cpp) into Android via JNI
- Managing native memory lifecycle safely from Kotlin coroutines
- Clean Architecture with a clear AI/data/domain/UI separation
- Reactive state management across ViewModel, StateFlow, and Room Flows
- Building a resumable, cancellable file download system with OkHttp
- Designing UX around real hardware constraints (RAM checks, model tiering)
- Custom Compose layouts without `Scaffold` (custom toolbars, insets, drawers)
- DataStore migration from SharedPreferences
- Production-style project organization for a non-trivial, resource-intensive Android app

---

## 👨‍💻 Author

**Hazrat Bilal**  
Senior Android Engineer  
Kotlin • Jetpack Compose • MVVM • Clean Architecture • Kotlin Multiplatform (KMP) • Flutter

[![GitHub](https://img.shields.io/badge/GitHub-View%20Profile-181717?style=flat&logo=github)](https://github.com/its-hazratbilal)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0077B5?style=flat&logo=linkedin)](https://linkedin.com/in/its-hazratbilal)

---

## ⭐ Support

If you find this project useful:

- ⭐ **Star** this repository
- 🍴 **Fork** it and build your own version
- 🐛 **Report issues** or suggest features
- 💬 **Share** it with the community

---

## 🙏 Acknowledgments

This project is built on top of [llama.cpp](https://github.com/ggml-org/llama.cpp) (MIT License) for on-device inference, and supports GGUF-quantized models including Google's Gemma 3, Qwen 2.5, TinyLlama, and SmolLM2. See the in-app **Open Source Licenses** screen for full attribution.

---

## 📄 License  
MIT License — feel free to use, modify, and distribute.
