<div align="center">

# Aetheris Android

**Billing & Virtualization Management Client for Android**

Full-featured Android client for the Aetheris platform. Manage servers, monitor resources, handle billing, and receive real-time alerts -- all from your mobile device.

![License](https://img.shields.io/badge/License-AGPL--3.0-blue)
![API](https://img.shields.io/badge/API-35-brightgreen)
![Min SDK](https://img.shields.io/badge/Min%20SDK-26-orange)
![Status](https://img.shields.io/badge/Status-Active-brightgreen)

</div>

---

## Features

- **Server Discovery** -- Automatic LAN discovery via UDP broadcast and manual WAN connection
- **Dashboard** -- Real-time stats, revenue, uptime, system health at a glance
- **Server Management** -- Start, stop, restart, suspend servers with resource monitoring
- **Console** -- Built-in terminal, VNC viewer, and file manager via WebView
- **Billing** -- Invoices, services, payment methods, and transaction history
- **Alerts** -- Severity-based alerts with acknowledgment and filtering
- **Settings** -- Dark/light theme, notifications, LAN discovery, language preferences
- **Offline Support** -- Room database for caching server data
- **Responsive Design** -- Material3 dynamic color and responsive layouts

## Architecture

```
com.aetheris.android/
  AetherisApp.kt           -- Application class (Hilt, notification channels)
  MainActivity.kt          -- Entry point with edge-to-edge theming
  navigation/
    NavGraph.kt             -- Navigation routes and bottom nav
  ui/
    theme/
      Theme.kt              -- Material3 dark/light theme
      Type.kt               -- Typography system
    screens/
      auth/
        ConnectScreen.kt    -- Server discovery (LAN + manual)
        LoginScreen.kt      -- Email/password + social auth
      dashboard/
        DashboardScreen.kt  -- Stats, health, quick actions
      servers/
        ServerListScreen.kt -- Server list with search/filter
        ServerDetailScreen.kt -- Server detail with actions
      console/
        ConsoleScreen.kx    -- Terminal, VNC, file manager
      billing/
        BillingScreen.kt    -- Invoices, services overview
        InvoiceDetailScreen.kt -- Invoice with line items
      alerts/
        AlertsScreen.kt     -- Alert list with severity filtering
      settings/
        SettingsScreen.kt   -- App configuration
  data/
    api/
      AetherisApi.kt        -- Retrofit API interface
    model/
      Server.kt             -- Server data models
      Billing.kt            -- Invoice, Service, Payment models
      Models.kt             -- Node, Alert, Auth, Dashboard models
    repository/
      AetherisRepository.kt -- API wrapper with error handling
    local/
      PreferencesManager.kt -- DataStore preferences
      AetherisDatabase.kt   -- Room offline cache
      dao/
        CachedServerDao.kt  -- Server cache DAO
  di/
    NetworkModule.kt        -- Hilt DI for networking
  service/
    DiscoveryService.kt     -- LAN discovery background service
    NotificationService.kt  -- Server status monitoring
  util/
    LanDiscovery.kt         -- UDP broadcast LAN discovery
```

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Kotlin 2.1 |
| UI | Jetpack Compose + Material3 |
| DI | Hilt (Dagger) |
| Networking | Retrofit + OkHttp + Kotlinx Serialization |
| Storage | DataStore + Room |
| Navigation | Navigation Compose |
| Image | Coil |
| WebView | AndroidX WebKit |
| Background | WorkManager + Foreground Services |
| Min SDK | 26 (Android 8.0) |
| Target SDK | 35 (Android 15) |

## Building

### Prerequisites

- Android Studio Hedgehog (2023.1) or later
- JDK 17
- Android SDK 35

### Build from source

```bash
git clone https://github.com/aetheris-project/aetheris-android.git
cd aetheris-android

# Debug build
./gradlew assembleDebug

# Release build (requires signing config)
./gradlew assembleRelease
```

### Install on device

```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## Configuration

The app connects to any Aetheris panel instance. Configure the server URL:

1. Open the app
2. Tap **Connect Manually**
3. Enter your panel URL (e.g., `https://panel.example.com`)
4. Sign in with your credentials

### LAN Discovery

The app can automatically discover Aetheris panels on your local network:

1. Ensure your device is on the same network as the panel
2. Tap **Discover on LAN**
3. Select a discovered panel from the list

## Connecting to the Panel

The Android client communicates with the Aetheris panel via its REST API. Ensure your panel has:

- API access enabled
- CORS configured for mobile clients
- Authentication endpoints active

## Screenshots

| Connect | Dashboard | Servers | Console |
|---|---|---|---|
| Server discovery with LAN scan | Stats, health, quick actions | Server list with resource bars | Terminal and VNC viewer |

## Related Projects

| Repository | Description | URL |
|---|---|---|
| aetheris-app | Main web panel | [GitHub](https://github.com/aetheris-project/aetheris-app) |
| aetheris-website | Marketing site | [GitHub](https://github.com/aetheris-project/aetheris-website) |
| aetheris-docs | Documentation wiki | [GitHub](https://github.com/aetheris-project/aetheris-docs) |
| aetheris-addons | Panel extensions | [GitHub](https://github.com/aetheris-project/aetheris-addons) |
| aetheris-themes | Theme presets | [GitHub](https://github.com/aetheris-project/aetheris-themes) |
| aetheris-game-eggs | Pterodactyl game eggs | [GitHub](https://github.com/aetheris-project/aetheris-game-eggs) |
| aetheris-windows-installer | Windows installer | [GitHub](https://github.com/aetheris-project/aetheris-windows-installer) |
| aetheris-installer | Linux installer | [GitHub](https://github.com/aetheris-project/aetheris-installer) |
| aetheris-ops | Infrastructure | [GitHub](https://github.com/aetheris-project/aetheris-ops) |
| aetheris-status | Status page | [GitHub](https://github.com/aetheris-project/aetheris-status) |
| aetheris-community | Community & workflows | [GitHub](https://github.com/aetheris-project/aetheris-community) |

## Links

| Resource | URL |
|---|---|
| Website | [aetheris-web.vercel.app](https://aetheris-web.vercel.app/) |
| Panel | [aetheris-panel.vercel.app](https://aetheris-panel.vercel.app/) |
| Documentation | [aetheris-docs.vercel.app](https://aetheris-docs.vercel.app/) |
| Status | [aetheris-status.vercel.app](https://aetheris-status.vercel.app/) |
| Discord | [discord.gg/6GcfebuT2A](https://discord.gg/6GcfebuT2A) |
| Contact | [hello@another-horizon.eu](mailto:hello@another-horizon.eu) |

## Contributing

All contributions require a Pull Request with automated CI checks (lint, typecheck, build) before manual review. See [CONTRIBUTING.md](https://github.com/aetheris-project/aetheris-community/blob/main/docs/CONTRIBUTING.md) for guidelines.

Contact: hello@another-horizon.eu

---

<div align="center">

Copyright (C) 2026 Leonardo Galli (Leo-Galli), Aetheris Project -- AGPL-3.0

</div>
