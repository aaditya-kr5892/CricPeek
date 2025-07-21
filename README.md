# 🏏 CricPeek — Live Cricket Scorecard Chrome Extension

**CricPeek** is a simple yet powerful Chrome Extension that displays live cricket scores and detailed scorecards directly in your browser — no need to switch tabs!

---

## 🚀 Features

- 📊 **Live Matches:** Get a list of ongoing matches with team scores and match status.
- 📋 **Detailed Scorecard:** Click on any match to see full batting and bowling details.
- 🔄 **Manual & Auto Refresh:** Refresh data easily or let it auto-update.
- 📌 **Lightweight & Fast:** Runs directly in your Chrome popup.
- 🔒 **Privacy-Friendly:** No unnecessary tracking or cookies.

---

## 📂 Project Structure

- **Backend:** Spring Boot REST API that fetches live cricket scores and serves JSON endpoints.
- **Frontend:** HTML, CSS, JavaScript popup that fetches data from the backend and renders it nicely.

---

## ⚙️ How It Works

1️⃣ The backend fetches live cricket scores by parsing publicly available live scorecard data from cricket websites using Jsoup.  
2️⃣ The extension’s popup (`popup.html` + `popup.js`) makes API calls to the backend.  
3️⃣ The user sees live scores and can open full scorecards with batting and bowling stats.

---

## 📦 Tech Stack
- Backend: Java, Spring Boot, Jsoup (HTML parsing)
- Frontend: HTML, CSS, Vanilla JavaScript (runs as a Chrome extension popup)
---

## 🏗️ Getting Started

### ✅ 1. Clone the Repo

```bash
git clone https://github.com/aaditya-kr5892/CricPeek.git
cd CricPeek
```
## ✅ 2. Build & Run Backend
```
./mvnw spring-boot:run
```

By default, the backend runs on http://localhost:8080.

## ✅ 3. Load the Extension
- Open chrome://extensions
- Enable Developer Mode
- Click Load Unpacked
- Select the folder containing your manifest.json and popup files

## 📫 Contact
```
If you like this project or want to discuss features:
Aaditya Kumar - aadityakumar5892@gmail.com
```

