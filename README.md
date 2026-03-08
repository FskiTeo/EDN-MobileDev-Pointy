# 🎯 Pointy

**Pointy** is an Android attendance tracking application for teachers, using NFC cards to mark students as present in real time.

Built as part of a Mobile Development course project — M1 Cyber.

---

## 👥 Team

| Name | Role |
|------|------|
| Téo Fiminski | Backend & API integration |
| Hugo Hochart | Login, NFC module & UI |
| Julie Vandenberghe | Dashboard, Attendance screen & UX |

---

## 📱 Features

- **Teacher login** — secure authentication with JWT token
- **Secure session persistence** — token stored with DataStore + Tink encryption
- **Auto session restore on app start** — token is validated via API before entering the app
- **Course dashboard** — view all your courses with dynamic greeting
- **Attendance sheet** — list students per course with presence status (present / absent / excused)
- **NFC scan** — tap a student card to automatically mark them present
- **Manual attendance** — rotate attendance status by tapping a student card in the list
- **Profile screen** — view teacher information
- **Logout** — clear persisted session and return to login

---

## 🔧 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Architecture | MVVM |
| State management | StateFlow |
| HTTP client | Retrofit + OkHttp |
| JSON parsing | Gson |
| Secure storage | DataStore Preferences + Tink (AES-GCM) |
| NFC | Android NfcAdapter |
| Backend | Node.js + Drizzle ORM + PostgreSQL (Vercel) |

---

## 🏗️ Project Structure

```
app/
└── src/main/java/com/jht/pointy/
    ├── data/
    │   ├── model/          # Data classes (Course, Student, Teacher...)
    │   ├── network/        # Retrofit, ApiService, SessionManager
    │   └── session/        # SecureSessionStorage (DataStore + Tink)
    ├── state/              # Sealed classes (LoginState, CourseState...)
    └── ui/
      ├── viewModel/      # ViewModels (AuthState, Login, Dashboard, Course, Scan, Profile)
        ├── AttendanceScreen.kt
        ├── DashboardScreen.kt
        ├── LoginScreen.kt
        ├── ProfileScreen.kt
        └── ScanScreen.kt
```

---

## 🔄 Main User Flow

```
Login
  ↓
Session bootstrap on startup (restore token + validate with /courses/mycourses + get /teachers/me)
  ↓
Dashboard (list of courses)
  ↓ tap a course
Attendance Screen (student list)
  ↓ tap "Start scan"
Scan Screen (NFC mode active)
  ↓ student taps card
Attendance updated automatically
```

---

## 🌐 API

Base URL: `https://edn-mobile-dev-pointy-backend.vercel.app`

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/teachers/login` | Login and get JWT token |
| `GET` | `/teachers/me` | Get current teacher profile |
| `GET` | `/courses/mycourses` | Get courses for logged-in teacher |
| `GET` | `/courses/{id}` | Get course details with students |
| `PATCH` | `/courses/attendance` | Update student attendance |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio Hedgehog or later
- Android device or emulator with API 29+
- NFC-enabled device for full functionality

### Setup

1. Clone the repository
```bash
git clone https://github.com/your-repo/pointy.git
```

2. Open the project in Android Studio

3. Sync Gradle dependencies

4. Run on a physical device (NFC requires real hardware)

---

## 📋 Requirements

- `INTERNET` permission — for API calls
- `NFC` permission — for card scanning
- NFC hardware required (`android.hardware.nfc`)

---

## 📦 Dependencies

### Networking
| Library | Version | Purpose |
|---------|---------|---------|
| Retrofit | 2.11.0 | HTTP client |
| converter-gson | 2.11.0 | JSON to Kotlin object conversion |
| Gson | 2.10.1 | JSON parsing |
| OkHttp logging-interceptor | 4.12.0 | HTTP request/response logging |

### Security & Persistence
| Library | Version | Purpose |
|---------|---------|---------|
| DataStore Preferences | 1.1.1 | Persist session data |
| security-crypto | 1.1.0-alpha06 | Android Keystore integration |
| Tink Android | 1.13.0 | Encrypt/decrypt JWT token (AES-GCM) |

### Android & Jetpack
| Library | Version | Purpose |
|---------|---------|---------|
| androidx.core-ktx | 1.17.0 | Kotlin extensions for Android |
| androidx.lifecycle-runtime-ktx | 2.10.0 | Lifecycle-aware components |
| androidx.activity-compose | 1.12.3 | Compose integration with Activity |
| lifecycle-viewmodel-compose | 2.8.4 | ViewModel in Compose |

### Jetpack Compose
| Library | Version | Purpose |
|---------|---------|---------|
| Compose BOM | 2024.09.00 | Compose version management |
| material3 | via BOM | Material Design 3 components |
| material3-adaptive-navigation-suite | via BOM | Bottom navigation bar |
| material-icons-core | via BOM | Material icons |
| ui, ui-graphics | via BOM | Core Compose UI |
