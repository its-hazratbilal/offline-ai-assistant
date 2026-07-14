# 🤖 Offline AI Assistant

<p align="center">
  <img src="./screenshots/logo.png" width="120" alt="Offline AI Assistant Logo"/>
</p>

<p align="center">
  <b>A privacy-first Android AI assistant that runs entirely on-device using local LLMs powered by llama.cpp.</b><br/>
  No cloud • No subscriptions • No tracking • Fully Offline
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin"/>
  <img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=jetpackcompose"/>
  <img src="https://img.shields.io/badge/MVVM-Architecture-FF6F00?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/Clean-Architecture-00C853?style=for-the-badge"/>
  <img src="https://img.shields.io/badge/llama.cpp-JNI-000000?style=for-the-badge"/>
</p>

---

## 📱 Overview

**Offline AI Assistant** is a fully on-device AI chat application for Android.

Unlike cloud-based AI assistants, every conversation runs locally on your device using GGUF language models powered by **llama.cpp**. Once a model is downloaded, no internet connection is required for chatting.

The app focuses on:

- 🔒 Privacy
- ⚡ Speed
- 📱 Offline AI
- 🧠 Local LLM inference
- 🎨 Modern Android UI

Users can download models, switch between them, chat offline, generate content, summarize text, translate languages, create emails and resumes, and much more.

---

## ✨ Features

- 🧠 Fully offline AI conversations
- 📥 Download and manage local GGUF models
- 🔄 Switch AI models without restarting the app
- 💬 Chat history with conversation grouping
- 🕵️ Private Chat mode (not stored locally)
- 📝 Email writing assistant
- 📄 Resume & CV generation
- ✍️ Writing & rewriting assistant
- 📚 Text summarization
- 🌍 Language translation
- 💻 Programming help
- 🎙️ Voice-to-text input
- 🔊 Text-to-speech playback
- 📋 Copy responses
- 📤 Share messages & conversations
- 📥 Export chats
- 🌗 Light / Dark / System theme
- 🔒 No analytics, no tracking, no cloud backend

---

## 🤖 Supported Models

Current built-in model catalog:

| Model | Size |
|---------|---------|
| SmolLM2 360M | ~220 MB |
| Qwen 2.5 0.5B | ~320 MB |
| TinyLlama 1.1B | ~420 MB |
| Gemma 3 1B | ~806 MB |
| Gemma 3 4B | ~2.2 GB |

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|----------|
| Kotlin | Programming Language |
| Jetpack Compose | UI Toolkit |
| Material 3 | Design System |
| MVVM | Presentation Architecture |
| Clean Architecture | App Structure |
| Hilt | Dependency Injection |
| Room | Local Database |
| SharedPreferences | App Preferences |
| Navigation Compose | Navigation |
| Coroutines & Flow | Async Programming |
| OkHttp | Model Downloads |
| llama.cpp | Local LLM Inference |
| JNI / C++ | Native Integration |
| Android SpeechRecognizer | Voice Input |
| Android TextToSpeech | Voice Output |

---

## 🧠 Architecture

```text
Presentation Layer
├── Compose UI
├── ViewModels
└── UiState

        ↓

Domain Layer
├── Use Cases
├── Repository Interfaces
└── Business Logic

        ↓

Data Layer
├── Repository Implementations
├── Room Database
├── Preferences
└── Download Manager

        ↓

AI Layer
├── ModelSessionManager
├── ModelManager
├── LlmEngine
└── llama.cpp (JNI)
```

### Model Lifecycle

```text
Select Model
      ↓
RAM Validation
      ↓
Download Model
      ↓
Load via JNI
      ↓
Model Ready
      ↓
Chat Available
```

---

## 📸 Screenshots

### Chat

<p align="center">
  <img src="./screenshots/chat-1.png" width="180"/>
  <img src="./screenshots/chat-2.png" width="180"/>
  <img src="./screenshots/chat-3.png" width="180"/>
</p>

### Models

<p align="center">
  <img src="./screenshots/models-1.png" width="180"/>
  <img src="./screenshots/models-2.png" width="180"/>
</p>

### Settings

<p align="center">
  <img src="./screenshots/settings.png" width="180"/>
</p>

---

## 🔒 Privacy First

Offline AI Assistant is designed with privacy in mind.

- No cloud processing
- No user accounts
- No analytics
- No tracking
- No conversation uploads

All AI inference runs entirely on-device.

---

## 🚀 Getting Started

### Prerequisites

- Android Studio
- Android NDK
- CMake

### Clone

```bash
git clone https://github.com/its-hazratbilal/offline-ai-assistant.git
```

### Build

Open the project in Android Studio and run the app.

The native llama.cpp library will be compiled automatically through CMake and the Android NDK.

---

## 📦 Download

APK releases are available on GitHub:

👉 https://github.com/its-hazratbilal/offline-ai-assistant/releases

---

## 🎯 What This Project Demonstrates

- Modern Android development
- Jetpack Compose UI
- MVVM Architecture
- Clean Architecture
- Native C++ integration
- llama.cpp integration
- JNI communication
- Local AI inference
- Room database usage
- Offline-first application design

---

## 👨‍💻 Author

**Hazrat Bilal**

Senior Android Engineer

Kotlin • Jetpack Compose • MVVM • Clean Architecture • KMP • Flutter

🌐 Portfolio: https://hazratbilal.com

🔗 LinkedIn: https://linkedin.com/in/its-hazratbilal

💻 GitHub: https://github.com/its-hazratbilal

---

## 🙏 Acknowledgements

Special thanks to:

- llama.cpp
- GGUF
- Google Gemma
- Qwen Team
- TinyLlama
- Hugging Face
- Android Jetpack

for making local AI applications possible.

---

## ⭐ Support

If you find this project useful:

- Star the repository
- Fork the project
- Open issues
- Share with others

---

## 📄 License

MIT License — feel free to use, modify, and distribute.
