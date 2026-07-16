# 🤖 Offline AI Assistant

<p align="center">
  <img src="./screenshots/logo.png" width="140" alt="Offline AI Assistant Logo"/>
</p>

<p align="center">
  <img src="https://img.shields.io/github/stars/its-hazratbilal/offline-ai-assistant?style=for-the-badge"/>
  <img src="https://img.shields.io/github/license/its-hazratbilal/offline-ai-assistant?style=for-the-badge"/>
  <img src="https://img.shields.io/github/last-commit/its-hazratbilal/offline-ai-assistant?style=for-the-badge"/>
  <img src="https://img.shields.io/github/issues/its-hazratbilal/offline-ai-assistant?style=for-the-badge"/>
</p>

<p align="center">
  <b>A privacy-first Android AI assistant that runs entirely on-device using GGUF language models powered by llama.cpp.</b><br/>
  No cloud • No subscriptions • No tracking • Fully Offline
</p>

<p align="center">
<img src="https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white"/>
<img src="https://img.shields.io/badge/Jetpack%20Compose-4285F4?style=for-the-badge&logo=android&logoColor=white"/>
<img src="https://img.shields.io/badge/MVVM-Architecture-FF6F00?style=for-the-badge"/>
<img src="https://img.shields.io/badge/Clean-Architecture-00C853?style=for-the-badge"/>
<img src="https://img.shields.io/badge/llama.cpp-LLM-000000?style=for-the-badge"/>
<img src="https://img.shields.io/badge/C%2B%2B-JNI-00599C?style=for-the-badge&logo=cplusplus&logoColor=white"/>
</p>

---

## 📑 Table of Contents

- [📱 Overview](#-overview)
- [✨ Features](#-features)
- [🤖 Supported Models](#-supported-models)
- [🛠 Tech Stack](#-tech-stack)
- [🧠 Architecture](#-architecture)
- [📂 Project Structure](#-project-structure)
- [🚀 Getting Started](#-getting-started)
- [📱 Device Requirements](#-device-requirements)
- [📦 Download](#-download)
- [🤝 Contributing](#-contributing)
- [🛣️ Roadmap](#️-roadmap)
- [📄 License](#-license)

---

## 📱 Overview

**Offline AI Assistant** is a modern Android application that brings powerful AI directly to your device.

Unlike cloud-based AI assistants, every conversation runs locally using **GGUF language models** powered by **llama.cpp**, ensuring your chats remain private and accessible even without an internet connection.

Once a model is downloaded, all AI inference happens completely on-device.

Designed with a privacy-first philosophy, Offline AI Assistant delivers a fast, modern, and completely offline AI experience while supporting multiple open-source language models.

---

## ✨ Features

- 🧠 Fully offline AI conversations
- 📥 Download and manage GGUF language models
- 🔄 Switch AI models without restarting the app
- 💬 Session-based chat history
- 📂 Conversation grouping (Today, Yesterday, This Week, Older)
- 🕵️ Private Chat mode (chat never stored)
- ✍️ AI writing assistant (emails, resumes, rewriting)
- 📚 Text summarization
- 🌍 Language translation
- 💻 Programming assistance
- 🎙️ Voice-to-text input
- 🔊 Text-to-speech playback
- ⏹️ Stop AI response generation
- 📋 Copy AI responses
- 📤 Share messages and conversations
- 📥 Export conversations
- 🌗 Light / Dark / System theme
- 🔒 No cloud processing

---

## 🤖 Supported Models

The application currently supports the following GGUF models:

| Model | Quantization | Download Size | Recommended RAM |
|--------|--------------|---------------|-----------------|
| SmolLM2 360M | Q4_K_M | **258 MB** | 2 GB+ |
| Qwen 2.5 Mini (0.5B) | Q4_K_M | **468 MB** | 3 GB+ |
| TinyLlama 1.1B | Q4_K_M | **637 MB** | 4 GB+ |
| **Gemma 3 1B ⭐ Recommended** | Q4_K_M | **768 MB** | 6 GB+ |
| Gemma 3 4B | Q4_K_M | **2.37 GB** | 8 GB+ |

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|----------|
| Kotlin | Programming Language |
| Jetpack Compose | Declarative UI Toolkit |
| Material 3 | Design System |
| MVVM | Presentation Architecture |
| Clean Architecture | Project Structure |
| Hilt | Dependency Injection |
| Room | Chat history & session persistence |
| DataStore | Theme, onboarding & selected model preferences |
| Navigation Compose | Navigation |
| Kotlin Coroutines | Background operations |
| Kotlin Flow | Reactive UI state |
| OkHttp | GGUF model downloading |
| llama.cpp | Local LLM inference engine |
| C++ / JNI | Native AI integration |
| Android SpeechRecognizer | Voice input |
| Android TextToSpeech | Voice output |

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
├── DataStore
├── Download Manager
└── Local AI Data Source

        ↓

AI Layer
├── ModelSessionManager
├── ModelManager
├── LlmEngine
└── llama.cpp (JNI)
```

---

## 📂 Project Structure

```text
offline-ai-assistant
│
├── ai
│   ├── engine
│   ├── gguf
│   │   └── internal
│   │
│   ├── manager
│   └── model
│
├── data
│   ├── download
│   ├── llm
│   ├── local
│   │   ├── dao
│   │   ├── database
│   │   ├── datasource
│   │   ├── entity
│   │   └── preferences
│   │
│   ├── mapper
│   ├── model
│   └── repository
│
├── di
├── domain
│   ├── model
│   ├── repository
│   └── usecase
│
├── ui
│   ├── common
│   ├── features
│   │   ├── about
│   │   ├── chat
│   │   ├── model_selection
│   │   ├── settings
│   │   └── welcome
│   │
│   ├── navigation
│   └── theme
│
├── utils
│
├── BaseApp.kt
└── MainActivity.kt
```

---

## 📦 Package Overview

| Package | Responsibility |
|----------|----------------|
| **ai** | Local LLM inference, JNI bridge, GGUF support, model loading & generation |
| **data** | Repository implementations, Room database, DataStore, downloads |
| **domain** | Business logic, repository contracts and use cases |
| **ui** | Jetpack Compose screens, ViewModels, navigation and theming |
| **di** | Dependency injection using Hilt |
| **utils** | Helper classes including RAM checks, exporters and download utilities |

---

## 🏗️ Architecture Principles

This project follows modern Android development best practices:

- **MVVM Architecture**
- **Clean Architecture**
- **Repository Pattern**
- **Single Source of Truth**
- **Dependency Injection with Hilt**
- **StateFlow + Coroutines**
- **Offline-first Design**
- **Native AI Inference using JNI & llama.cpp**

---

## 🔄 AI Model Lifecycle

```text
User selects a model
        │
        ▼
Check available RAM
        │
        ▼
Download GGUF model
        │
        ▼
Load model through JNI
        │
        ▼
ModelManager initializes llama.cpp
        │
        ▼
Model ready
        │
        ▼
Start chatting
```

---

## ⚙️ How AI Works

```text
User Prompt
      │
      ▼
ChatViewModel
      │
      ▼
Chat Use Case
      │
      ▼
Repository
      │
      ▼
ModelSessionManager
      │
      ▼
ModelManager
      │
      ▼
LlamaCppEngine
      │
      ▼
JNI Bridge
      │
      ▼
llama.cpp
      │
      ▼
GGUF Model
      │
      ▼
AI Response
```

---

## 📸 Screenshots

<p align="center">
  <img src="./screenshots/screenshot-1.jpg" width="180" alt="screenshot-1"/>
  <img src="./screenshots/screenshot-2.jpg" width="180" alt="screenshot-2"/>
  <img src="./screenshots/screenshot-3.jpg" width="180" alt="screenshot-3"/>
  <img src="./screenshots/screenshot-4.jpg" width="180" alt="screenshot-4"/>
</p>

<p align="center">
  <img src="./screenshots/screenshot-5.jpg" width="180" alt="screenshot-5"/>
  <img src="./screenshots/screenshot-6.jpg" width="180" alt="screenshot-6"/>
  <img src="./screenshots/screenshot-7.jpg" width="180" alt="screenshot-7"/>
  <img src="./screenshots/screenshot-8.jpg" width="180" alt="screenshot-8"/>
</p>

<p align="center">
  <img src="./screenshots/screenshot-9.jpg" width="180" alt="screenshot-9"/>
  <img src="./screenshots/screenshot-10.jpg" width="180" alt="screenshot-10"/>
  <img src="./screenshots/screenshot-11.jpg" width="180" alt="screenshot-11"/>
  <img src="./screenshots/screenshot-12.jpg" width="180" alt="screenshot-12"/>
</p>

---

## 🎥 Demo

👉 [Watch on YouTube](https://www.youtube.com/watch?v=v6mUB1AA4QM)

---

## 🚀 Getting Started

### Prerequisites

Before building the project, install:

- Android Studio Narwhal or newer
- Android SDK
- Android NDK
- CMake
- JDK 17+

---

### Clone the Repository

```bash
git clone https://github.com/its-hazratbilal/offline-ai-assistant.git
```

Open the project in Android Studio.

---

### Native Build Requirements

This project compiles **llama.cpp** from source using the Android NDK.

Ensure the following SDK components are installed:

- Android NDK
- CMake

The first build may take a few minutes while the native library is compiled.

---

### Build & Run

Run the app module from Android Studio on a physical Android device.

For the best performance, a device with at least **8 GB RAM** is recommended when using larger models.

Minimum SDK: **30**

Target SDK: **37**

After launching the app:

1. Select an AI model.
2. Download the model.
3. Wait for the model to finish loading.
4. Start chatting completely offline.

---

## 📱 Device Requirements

| Requirement | Recommended |
|-------------|-------------|
| Android Version | Android 11 (API 30)+ |
| RAM | 4 GB minimum (8 GB recommended) |
| Storage | Up to 3 GB free for larger models |
| Internet | Required only for downloading models |

---

## 📦 Download

Prebuilt APKs are available from the GitHub Releases page.

👉 **[Download the latest APK](https://github.com/its-hazratbilal/offline-ai-assistant/releases)**

---

## 🧪 Tested On

The application has been tested on Android devices running Android 11 and above.

Performance depends on:

- Available RAM
- CPU performance
- Selected GGUF model

Larger models provide better responses but require more memory.

---

## 🤝 Contributing

Contributions are always welcome!

If you'd like to improve the project:

1. Fork the repository
2. Create a feature branch

```bash
git checkout -b feature/my-feature
```

3. Commit your changes

```bash
git commit -m "Add amazing feature"
```

4. Push the branch

```bash
git push origin feature/my-feature
```

5. Open a Pull Request

---

## 🛣️ Roadmap

Planned features include:

- [ ] Chat search
- [ ] Chat pinning
- [ ] Code syntax highlighting
- [ ] Document summarization
- [ ] Image understanding (Vision models)
- [ ] RAG support
- [ ] Function calling
- [ ] Streaming response improvements
- [ ] More GGUF models
- [ ] Better GPU acceleration
- [ ] Tablet UI optimization
- [ ] Kotlin Multiplatform (KMP) support

---

## 🎯 What This Project Demonstrates

This repository demonstrates how to build a production-style Android application around modern on-device AI technologies.

This project demonstrates:

- 🤖 On-device LLM inference using **llama.cpp**
- 📦 Integration of native C++ libraries through **JNI**
- 🏗️ MVVM with **Clean Architecture**
- 💉 Dependency Injection using **Hilt**
- ⚡ Reactive UI with **Jetpack Compose**, **StateFlow**, and **Coroutines**
- 💾 Local persistence using **Room**
- ⚙️ User preferences with **DataStore**
- 📥 Background model downloading with **OkHttp**
- 🧠 GGUF model management and lifecycle handling
- 🎙️ Android Speech Recognition integration
- 🔊 Android Text-to-Speech integration
- 🔒 Offline-first and privacy-first application design
- 🎨 Material 3 UI following modern Android design guidelines

The project is intended to serve as both a real-world AI application and a reference implementation for developers interested in integrating local language models into Android apps.

---

## 📚 Open Source Models

Offline AI Assistant supports GGUF-quantized models from the open-source AI community, including:

- **Google Gemma 3**
- **Qwen 2.5**
- **TinyLlama**
- **SmolLM2**

New models can easily be added by extending the built-in model catalog.

---

## 🙏 Acknowledgements

A huge thank you to the amazing open-source community and projects that made this application possible.

Special thanks to:

- **llama.cpp** — Fast and efficient on-device LLM inference
- **GGUF** — Standard format for quantized language models
- **Google** — Gemma
- **Alibaba Cloud** — Qwen
- **TinyLlama** contributors
- **SmolLM2** contributors
- **Hugging Face** — Model hosting and distribution
- **Android Jetpack** — Modern Android development libraries
- **Material Design** — Google's design system

Without these projects, building local AI applications would be far more difficult.

---

## 👨‍💻 Author

**Hazrat Bilal**  
Senior Android Engineer  
Kotlin • Jetpack Compose • MVVM • Clean Architecture • Kotlin Multiplatform (KMP) • Flutter

[![Portfolio](https://img.shields.io/badge/Portfolio-Visit%20Website-0A66C2?style=flat&logo=googlechrome&logoColor=white)](https://hazratbilal.com)
[![GitHub](https://img.shields.io/badge/GitHub-its--hazratbilal-181717?style=flat&logo=github)](https://github.com/its-hazratbilal)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Hazrat%20Bilal-0077B5?style=flat&logo=linkedin)](https://linkedin.com/in/its-hazratbilal)

---

## ⭐ Support

If you found this project useful, please consider supporting it by:

- ⭐ Starring the repository
- 🍴 Forking the project
- 🐛 Reporting bugs
- 💡 Suggesting new features
- 🔀 Opening Pull Requests
- 📢 Sharing the project with other Android developers

Every contribution and star helps the project grow.

---

## 📄 License

This project is licensed under the **MIT License**.

See the [LICENSE](LICENSE) file for details.

---

<p align="center">

Built with ❤️ using Kotlin, Jetpack Compose, and llama.cpp

</p>
