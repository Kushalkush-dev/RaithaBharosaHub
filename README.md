# RaithaBharosaHub

**RaithaBharosaHub** is a modern, AI-integrated Android application designed to digitize agricultural management and empower farmers with data-driven insights. Built using **Jetpack Compose** and **Clean Architecture**, it combines local persistence, real-time analytics, and Generative AI to provide a comprehensive toolkit for the modern agriculturist.

---

## 🚀 Key Features

*   **AI Agricultural Consultant:** Integration with **Google Gemini AI** to provide real-time, expert-level advice on crop health, pest control, and farming techniques.
*   **Dynamic Market Analytics:** Interactive data visualization of market trends and crop metrics using the **Vico Charting Engine**.
*   **Offline-First Activity Logging:** Robust CRUD-based activity tracking powered by **Room Database**, ensuring data availability even in remote areas without internet.
*   **Geospatial Intelligence:** Location-aware insights using **Google Play Services** to provide region-specific agricultural data.
*   **Reactive UI:** A fluid, state-driven user interface built entirely with **Jetpack Compose** and **Material Design 3**.

---

## 🛠 Tech Stack

### Core
- **Language:** [Kotlin](https://kotlinlang.org/)
- **UI Framework:** [Jetpack Compose](https://developer.android.com/jetpack/compose)
- **Architecture:** MVVM (Model-View-ViewModel) + Repository Pattern
- **Asynchronous Flow:** Kotlin Coroutines & StateFlow

### Data & Networking
- **Local Persistence:** [Room Database](https://developer.android.com/training/data-storage/room)
- **API Communication:** [Retrofit 2](https://square.github.io/retrofit/) & [OkHttp 4](https://square.github.io/okhttp/)
- **Serialization:** [GSON](https://github.com/google/gson)
- **Dependency Management:** Gradle Kotlin DSL (KTS)

### Intelligence & Visualization
- **AI Engine:** [Google Gemini AI SDK](https://ai.google.dev/android/sdk)
- **Charts:** [Vico](https://github.com/patrykandpatrick/vico)
- **Dependency Injection Support:** [KSP (Kotlin Symbol Processing)](https://kotlinlang.org/docs/ksp-overview.html)

---

## 🏗 Project Structure

com.raithabharosa.hub/
├── data/
│   ├── api/          # Retrofit interfaces & Gemini AI service
│   ├── local/        # Room Database, DAOs, and Entities
│   └── repository/   # Single source of truth for data
├── ui/
│   ├── screens/      # Compose UI screens (Trends, History, AI Chat)
│   ├── theme/        # Material 3 Design Tokens (Color, Type, Shape)
│   └── components/   # Reusable UI widgets
├── viewmodel/        # UI State management & Business logic
└── util/             # Helpers & Extensions

---

## ⚙️ Installation & Setup

### Prerequisites
- Android Studio Iguana (2023.2.1) or newer.
- Android SDK Level 34 (Target).
- A **Gemini API Key** from [Google AI Studio](https://aistudio.google.com/).

### Setup Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/RaithaBharosaHub.git
   ```
   
2. **Configure API Keys:**
   Open your `local.properties` file in the root directory and add your Gemini API Key:
   ```properties
   GEMINI_API_KEY=your_actual_api_key_here
   ```
   
3. **Build & Run:**
   Sync the project with Gradle files and run it on an emulator or physical device (Min SDK 24).

---

## 🛡 Security & Configuration
- **API Key Management:** The project uses `buildConfigField` to inject the Gemini API key from `local.properties` at compile time, ensuring sensitive keys are never committed to version control.
- **ProGuard:** Initial ProGuard rules are configured to optimize and obfuscate the release build.

---

## 📈 Future Roadmap
- [ ] Multilingual support (Kannada, Telugu, Hindi).
- [ ] Push notifications for urgent weather alerts.
- [ ] Image-based pest diagnosis using Gemini Vision.
- [ ] Exportable reports (PDF/CSV) for farm activities.

---

## 🤝 Contributing
Contributions are welcome! Please fork the repository and use a feature branch. Pull requests should follow the existing code style and include relevant tests.

---

## 📜 License
Distributed under the MIT License. See `LICENSE` for more information.

---
**Developed by [Kushal J]**
*Empowering Agriculture through Technology.*
