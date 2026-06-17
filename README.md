<div align="center">

# DDDLock

**Bloqueio inteligente de chamadas por DDD**

[![GitHub Release](https://img.shields.io/github/v/release/eduardokryon/DDD-shield?style=flat-square&color=E94560)](https://github.com/eduardokryon/DDD-shield/releases)
[![License](https://img.shields.io/github/license/eduardokryon/DDD-shield?style=flat-square)](LICENSE)
[![API](https://img.shields.io/badge/API-26%2B-brightgreen?style=flat-square)](https://developer.android.com/about/versions/oreo)
[![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-7F52FF?style=flat-square&logo=kotlin)](https://kotlinlang.org)

[**Download Latest Release**](https://github.com/eduardokryon/DDD-shield/releases/latest)

</div>

---

## About

DDDLock is an Android application that automatically blocks incoming calls from selected Brazilian area codes (DDDs). Built exclusively on the official Android `CallScreeningService` API — no foreground services, no accessibility hacks, no battery drain.

### Why?

Brazil receives millions of unwanted calls daily — telemarketing, scams, debt collection. DDDLock gives you control over which regions can reach you.

### Key Features

- Block calls from any combination of Brazil's 67 area codes
- Persistent blocking that survives reboots without background services
- Contact protection — numbers in your contacts are never blocked
- Material You design with dynamic colors and dark mode
- Zero background battery usage

---

## How It Works

DDDLock uses Android's `CallScreeningService`, a system-level API that intercepts incoming calls before they ring. When a call arrives:

1. The service extracts the DDD from the phone number
2. Checks if the number exists in your contacts (if yes, allow)
3. Checks if the DDD is in your blocked list
4. Rejects the call if blocked, allows if not

This approach is:
- **Battery-efficient** — no persistent services
- **Reliable** — works after reboots without reconfiguration
- **Non-invasive** — uses only official Android APIs

---

## Requirements

- Android 8.0 (API 26) or higher
- Call Screening permission (granted on first launch)
- Contact permission (optional, for contact protection)

---

## Installation

### Download

Grab the latest APK from [Releases](https://github.com/eduardokryon/DDD-shield/releases/latest).

### Setup

1. Enable "Install from unknown sources" for your file manager/browser
2. Install the APK
3. Open DDDLock — it will request necessary permissions
4. Set DDDLock as your default Call Screening app when prompted

If the prompt doesn't appear:

```
Settings → Apps → Default Apps → Call Screening app → DDDLock
```

### Usage

1. Open DDDLock
2. Browse or search for DDDs by code, city, or state
3. Select the DDDs you want to block
4. Toggle the blocker ON
5. Done — blocked calls will be silently rejected

---

## Architecture

Clean Architecture with MVVM pattern.

```
┌─────────────────────────────────────────────┐
│                 UI Layer                     │
│   Screens ─ Components ─ ViewModels (Flow)   │
├─────────────────────────────────────────────┤
│               Domain Layer                   │
│        Use Cases ─ Repository Interface       │
├─────────────────────────────────────────────┤
│                Data Layer                    │
│      Repository Impl ─ DataStore             │
├─────────────────────────────────────────────┤
│              Service Layer                   │
│           CallScreeningService               │
└─────────────────────────────────────────────┘
```

### Design Principles

- **SOLID** — single responsibility, dependency inversion
- **DRY** — reusable components, centralized DDD repository
- **KISS** — minimal abstractions, straightforward logic

---

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin 1.9.22 |
| UI | Jetpack Compose + Material 3 |
| Navigation | Navigation Compose |
| Persistence | DataStore Preferences |
| Async | Coroutines + StateFlow |
| Architecture | MVVM + Clean Architecture |
| Build | Gradle 8.4 + Version Catalog |

---

## Project Structure

```
app/src/main/java/com/dddlock/
├── AppContainer.kt            # Dependency injection
├── DDDLockApplication.kt      # Application entry point
├── MainActivity.kt            # Main activity + navigation
├── model/                     # Data models
├── data/                      # Repository + DataStore
├── domain/                    # Use cases + interfaces
├── service/                   # CallScreeningService
├── navigation/                # Navigation graph
└── ui/                        # Screens, components, theme
```

---

## Building from Source

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17
- Android SDK 34

### Build

```bash
git clone https://github.com/eduardokryon/DDD-shield.git
cd DDD-shield
./gradlew assembleDebug
```

APK output: `app/build/outputs/apk/debug/app-debug.apk`

### Release Build

```bash
./gradlew assembleRelease
```

> Release builds require a signing key. Configure in `app/build.gradle.kts`.

---

## Permissions

| Permission | Purpose |
|------------|---------|
| `CALL_PHONE` | Required for CallScreeningService |
| `READ_CONTACTS` | Protects contacts from being blocked |
| `POST_NOTIFICATIONS` | Notifications (Android 13+) |

---

## Known Limitations

- **VoIP calls** (WhatsApp, Telegram) are not affected — only cellular calls
- **Android 8.0+** required — CallScreeningService unavailable on older versions
- **Manufacturer variations** — some OEMs (Xiaomi, Samsung) may modify call screening behavior
- **First ring** — the call may ring once before being rejected, depending on device

---

## Roadmap

- [ ] Block statistics and history
- [ ] Export/import settings
- [ ] Cloud backup
- [ ] Multi-language support (PT-BR, EN)

---

## Contributing

Contributions are welcome. Please open an issue first to discuss what you'd like to change.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## License

This project is licensed under the MIT License — see the [LICENSE](LICENSE) file for details.

---

## Author

**Eduardo Brito** — [@eduardokryon](https://github.com/eduardokryon)

---

<div align="center">
  <sub>Built with Kotlin and Jetpack Compose</sub>
</div>
