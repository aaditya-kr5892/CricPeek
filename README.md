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
cd backend
```
## ✅ 2. Build & Run Backend
```
mvnw.cmd clean package
```
then
```
mvnw.cmd spring-boot:run
```

By default, the backend runs on http://localhost:8080.

if port 8080 is busy running something them use this run command
```
mvnw.cmd spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```
this will run backend on port 8081, change it if you want

## ✅ 3. Load the Extension
- Open chrome://extensions
- Enable Developer Mode
- Click Load Unpacked
- Select the extension folder in CricPeek 

## 📫 Contact
```
If you like this project or want to discuss features:
Aaditya Kumar - aadityakumar5892@gmail.com
```

## Use NSSM to run your backend as a background service.

- Download NSSM

👉 https://nssm.cc/download

- Extract the zip.

- Open Command Prompt as Administrator

- Install your backend as a service

Run:
```
C:\location\of\your\nssm.exe install CricketScorecardBackend
```
- A window will pop up:

   - Path: Application path: Path to your Java executable: `C:\Program Files\Java\jdk-22\bin\java.exe`

    - Arguments: Go inside the target folder in the backend and right click and copy as path your .jar file.
    ```-jar path\to\your\jar\cricket-scorecard-extension-0.0.1-SNAPSHOT.jar```
    - Startup directory: location of your target folder.

    Click Install Service.

- Set to Automatic

    - Open Services (press windows button and search services)

    - Find CricketScorecardBackend

    - Right-click → Properties → set Startup type to Automatic

Now your backend will start automatically every time your PC boots.

- Start the service

You can start it right away:
```right-click it in services.msc → Start.```

✅ That’s it!
Your backend is now always on when your PC is on — your extension can fetch scores anytime without you having to open your IDE or running the backend manually.

