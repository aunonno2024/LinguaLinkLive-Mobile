# 🌍 LinguaLinkLive

<div align="center">

**Real-Time Multilingual Call Translation — Breaking Language Barriers, Connecting the World**

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![Language](https://img.shields.io/badge/Language-Kotlin-purple.svg)](https://kotlinlang.org/)
[![Backend](https://img.shields.io/badge/Backend-Python%20FastAPI-blue.svg)](https://fastapi.tiangolo.com/)
[![Status](https://img.shields.io/badge/Status-In%20Development-orange.svg)]()
[![Contributions Welcome](https://img.shields.io/badge/Contributions-Welcome-brightgreen.svg)](CONTRIBUTING.md)

*Developed & Maintained by **OSA Studio***

</div>

---

## 📖 About

**LinguaLinkLive** is a free, open-source Android application that enables real-time voice and video call translation across multiple languages. The app is designed to eliminate language barriers in everyday communication — so that two people who speak completely different languages can have a natural, fluid conversation without either person needing to learn a new language.

Whether you are a business professional connecting with international clients, a family member calling a loved one abroad, or simply two people from different corners of the world wanting to talk — **LinguaLinkLive makes it possible**.

> *"You speak your language. They hear theirs. Instantly."*

---

## 🎯 The Problem We Are Solving

Today, over **7,000 languages** are spoken across the world. Yet most communication technology assumes everyone speaks English. Millions of people are excluded from global conversations simply because of the language they were born into.

Existing solutions like Google Translate require manual copy-paste. Professional interpreters are expensive. Learning a new language takes years.

**LinguaLinkLive solves this in real time — during a live phone or video call.**

---

## ✨ Key Features

| Feature | Description |
|---|---|
| 🎙️ **Live Audio Call Translation** | Speak in your language, your contact hears it in theirs — in real time |
| 📹 **Video Call Translation** | Full WebRTC-powered video calls with translated speech overlay |
| 💬 **Translated Messaging** | Send messages in your language; they arrive translated |
| 🌐 **100+ Languages Supported** | Bengali, Arabic, Chinese, Japanese, Korean, Spanish, French, and many more |
| 🔒 **Privacy Focused** | Translation happens on your own self-hosted server — your conversations stay yours |
| 📴 **No Subscription** | Completely free, forever |
| 🌍 **Open Source** | Full source code available under MIT License |

---

## 🏗️ How It Works

LinguaLinkLive uses a three-step pipeline to deliver real-time translation during calls:

```
Your Voice  ──►  Speech-to-Text  ──►  Translation  ──►  Text-to-Speech  ──►  Friend Hears
(Bengali)        (Whisper AI)          (Google         (gTTS Engine)          (Arabic)
                                        Translate)
```

Both users install the app. Each selects their preferred language. When either person speaks, the app:

1. **Captures** the audio in real time
2. **Transcribes** it using OpenAI Whisper (runs on the backend server — no GPU required)
3. **Translates** the text using Google Translate (via deep-translator, free)
4. **Converts** the translated text back to speech using gTTS
5. **Delivers** the translated audio to the other person — all within seconds

---

## 🛠️ Tech Stack

### Android App
| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | XML Layouts |
| IDE | JetBrains Rider / Android Studio |
| Architecture | Fragment-based Navigation |
| Real-time Calls | WebRTC (stream-webrtc-android) |
| Networking | Retrofit2 + OkHttp3 |
| WebSocket | Java-WebSocket |
| Local Database | Room (SQLite) |
| Async | Kotlin Coroutines |

### Backend Server
| Layer | Technology |
|---|---|
| Language | Python 3.11 |
| Framework | FastAPI |
| WebSocket | Uvicorn + WebSockets |
| Speech-to-Text | OpenAI Whisper (base model, CPU) |
| Translation | deep-translator (Google Translate, free) |
| Text-to-Speech | gTTS (Google TTS, free) |
| Deployment | Docker / Render.com / Railway.app |

---

## 📁 Project Structure

```
LinguaLinkLive/
├── app/
│   └── src/main/
│       ├── java/com/osastudio/lingualinklive/
│       │   ├── MainActivity.kt
│       │   ├── ui/
│       │   │   ├── contacts/          # Call contact list & language selection
│       │   │   ├── call/              # Audio & video call screens
│       │   │   ├── messaging/         # Translated messaging
│       │   │   ├── video/             # Video contact list
│       │   │   └── profile/           # User settings & language preference
│       │   ├── model/                 # Data models (Contact, Message, Language)
│       │   ├── network/               # API service, Retrofit, WebSocket
│       │   ├── webrtc/                # WebRTC & signaling
│       │   ├── translation/           # STT & speech processing
│       │   ├── database/              # Room database, DAOs
│       │   └── utils/                 # Constants, permissions, language utils
│       └── res/
│           ├── layout/                # All XML UI layouts
│           ├── drawable/              # Shapes, backgrounds, icons
│           └── values/                # Colors, strings, themes, dimensions
├── backend/
│   ├── main.py                        # FastAPI server entry point
│   ├── translation/
│   │   ├── stt.py                     # Speech-to-Text (Whisper)
│   │   ├── translate.py               # Text translation
│   │   └── tts.py                     # Text-to-Speech (gTTS)
│   ├── signaling/
│   │   └── server.py                  # WebSocket signaling & translation relay
│   ├── requirements.txt
│   └── Dockerfile
├── LICENSE
└── README.md
```

---

## 🚀 Getting Started

### Prerequisites

- Ubuntu 24.04 LTS (or any Linux / Windows / macOS)
- Java 17
- Android SDK (API 35)
- Gradle 8.7
- Python 3.11+
- An Android device or emulator (Android 8.0+)

### 1. Clone the Repository

```bash
git clone https://github.com/OSAStudio/LinguaLinkLive.git
cd LinguaLinkLive
```

### 2. Set Up the Backend Server

```bash
cd backend
pip install -r requirements.txt
pip install gTTS pydub
python main.py
```

The server will start at `http://0.0.0.0:8000`

> For production deployment, use the included `Dockerfile` and deploy to [Render.com](https://render.com) or [Railway.app](https://railway.app) — both offer free tiers.

### 3. Configure the Android App

Open the app, go to the **Profile** tab and enter:
- Your name
- Your native language
- Your server URL (e.g. `ws://YOUR_SERVER_IP:8000/ws`)

### 4. Build the APK

```bash
cd ..
gradle assembleDebug
```

The APK will be generated at:
```
app/build/outputs/apk/debug/app-debug.apk
```

Install it on your Android device and start calling.

---

## 📱 Screenshots

| Contacts & Call | Audio Call Screen | Messaging |
|---|---|---|
| Select contact & language | Live call with translation controls | Real-time translated messages |

| Message Contacts | Video Call |
|---|---|
| Choose language per contact | WebRTC video with speech overlay |

---

## 🗺️ Roadmap

- [x] Android app UI/UX design
- [x] WebSocket signaling server
- [x] Audio call with real-time translation pipeline
- [x] Video call with WebRTC
- [x] Translated messaging
- [ ] iOS version (Swift)
- [ ] Desktop version (Windows / macOS / Linux)
- [ ] Offline translation mode (on-device AI)
- [ ] Contact sync with phone contacts
- [ ] Push notifications for incoming calls
- [ ] End-to-end encryption
- [ ] Conference calls with multi-language translation
- [ ] Browser extension for Google Meet / Zoom integration

---

## 🤝 Contributing

We warmly welcome contributions from developers around the world. Whether it is fixing a bug, adding a new language, improving the UI, or writing documentation — every contribution matters.

1. Fork the repository
2. Create your feature branch: `git checkout -b feature/YourFeature`
3. Commit your changes: `git commit -m 'Add YourFeature'`
4. Push to the branch: `git push origin feature/YourFeature`
5. Open a Pull Request

Please read our [Contributing Guidelines](CONTRIBUTING.md) before submitting.

---

## 🐛 Reporting Issues

Found a bug or have a feature request? Please open an issue on GitHub:

- Describe the problem clearly
- Include steps to reproduce
- Attach logs or screenshots if possible

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for full details.

You are free to use, copy, modify, merge, publish, distribute, and build upon this project for any purpose, including commercial use, as long as the original copyright notice is included.

---

## 👥 Team

**OSA Studio**
- Building technology that connects people across language barriers
- Based in Bangladesh & Nepal 🇧🇩 🇳🇵
- Open to collaboration from developers and organizations worldwide

---

## 🌟 Support the Project

If you find LinguaLinkLive useful, please consider:

- ⭐ Starring the repository on GitHub
- 🍴 Forking and contributing
- 📢 Sharing with your network
- 🐛 Reporting bugs and suggesting features

---

## 📬 Contact

For business inquiries, partnerships, or collaboration opportunities:

- **GitHub:** [ ]( )
- **Email:** open-source-applications@tutamail.com

---

<div align="center">

**Made with ❤️ by OSA Studio — For a world without language barriers**

*"Every language is a different vision of life." — Federico Fellini*

</div>
