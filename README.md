# 🛒 Namma-Santhe Ledger

**A lightweight digital khata for India's village market vendors — replacing pocket diaries with a 5-second transaction app.**

Namma-Santhe Ledger is an Android application designed for small Santhe (weekly village market) vendors who extend informal credit ("Udari") and struggle with tracking dues using memory or handwritten notes. The app enables vendors to record transactions quickly, monitor outstanding balances, and send reminders — all while working fully offline. :contentReference[oaicite:1]{index=1}

---

## 🚀 Problem Statement

Small village vendors often:

- Record credit on paper or memory
- Lose track of customer dues
- Struggle with manual reconciliation
- Avoid existing fintech apps because they feel too complex

This results in **5–15% revenue loss from forgotten credit**. Namma-Santhe aims to solve this through **speed, simplicity, and offline reliability**. :contentReference[oaicite:2]{index=2}

---

## ✨ Features

### MVP Features

✅ Add and search customers  
✅ Record credit (Udari) in 2 steps  
✅ Record cash sales  
✅ Track repayments against dues  
✅ Daily summary dashboard  
✅ Customer-wise ledger view  
✅ WhatsApp payment reminders  
✅ Offline-first local storage  
✅ Large keypad for faster amount entry  
✅ Undo/edit recent transactions  :contentReference[oaicite:3]{index=3}

---

## 📱 User Flow

### Add Credit

Customer → Enter Amount → Save

⏱ Target time: **≤ 5 seconds**

### Record Payment

Open Customer → Paid → Enter Amount

⏱ Target time: **≤ 5 seconds**

### Send Reminder

Open Customer → WhatsApp Reminder

⏱ Target time: **≤ 3 seconds** :contentReference[oaicite:4]{index=4}

---

## 🛠 Tech Stack

| Technology | Purpose |
|------------|----------|
| Kotlin | Android development |
| MVVM Architecture | Clean architecture |
| Room DB | Offline local storage |
| Coroutines + Flow | Async operations |
| Hilt | Dependency Injection |
| Jetpack Compose / XML | UI |
| JUnit + Espresso | Testing |  :contentReference[oaicite:5]{index=5}

---

## 🎯 Design Principles

The app is built around one core metric:

> **Any transaction should be recordable within 5 seconds in 2 steps maximum.** :contentReference[oaicite:6]{index=6}

Every UI and architecture decision prioritizes:

- Speed
- Simplicity
- Offline usability
- Accessibility
- Low-end Android support :contentReference[oaicite:7]{index=7}

---

## 📊 Target Users

Primary users:

👨‍🌾 Santhe vendors (vegetables, snacks, bangles, etc.)  
📱 Entry-level Android users  
💬 Comfortable with WhatsApp & UPI but not complex apps :contentReference[oaicite:8]{index=8}

---

## 🔐 Offline & Privacy

- 100% offline functionality
- No cloud dependency (v1.0)
- Data stored locally using Room DB
- No third-party analytics in MVP :contentReference[oaicite:9]{index=9}

---

## 📦 Installation

Clone repository:

```bash
git clone https://github.com/Sai014/Nammasanthe.git
```

Open in Android Studio:

```bash
File → Open → Sync Gradle → Run
```

Minimum supported Android:

```txt
Android 7.0 (API 24)+
``` 
:contentReference[oaicite:10]{index=10}

---

## 🔮 Planned Features

- Multi-language support
- PDF / CSV export
- PIN lock
- Voice-based transaction entry
- OCR for handwritten entries
- Cloud backup
- Weekly/monthly analytics :contentReference[oaicite:11]{index=11}

---

## 🌍 Vision

> Make every Santhe vendor walk home knowing exactly how much they sold, how much is owed, and by whom — in the same time it takes to fold up their stall. :contentReference[oaicite:12]{index=12}

---

## 👨‍💻 Author

**Sai Sandeep R**  
B.Tech CSE — Sir M Visvesvaraya Institute of Technology

GitHub: https://github.com/Sai014

---
