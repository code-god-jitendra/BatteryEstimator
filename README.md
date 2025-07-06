# 🔋 Battery Estimator

An intelligent Android application that estimates your phone's remaining battery life based on real-time usage patterns, power consumption rate, and current battery percentage — even while running in the background.

---

## 📱 Features

- ⏳ **Real-time estimation** of remaining battery time
- 🔋 **Monitors power usage trends** in the background
- ⚠️ **Low battery alerts** when estimated time drops below 30 minutes
- 📈 **(Upcoming)** Graph view for battery drain over time
- 📲 Works with **physical devices** (ADB connected)
- 🔔 Runs as a **foreground service** with ongoing notification

---

## 🚀 How It Works

- Measures battery percentage over time
- Calculates drain rate (% per minute)
- Estimates how long your device can last if usage remains constant
- Displays and updates estimation in a persistently

---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Android Foreground Services**
- **BroadcastReceiver**
- **BatteryManager**
- **MPAndroidChart (planned)** for future graph view

---

## 📦 Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/code-god-jitendra/BatteryEstimator.git
