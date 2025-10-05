# RefCheck – JavaFX Football Referee Identity Verifier

**RefCheck** is a JavaFX application designed to assist football referees in verifying players’ identities before a match.  
Built as part of the **4PRJ1D** course at **Haute École Bruxelles-Brabant (HE2B ESI)**, the project combines OCR technology, database management, and an intuitive graphical interface to ensure a fast and reliable identity-checking process.

![Java](https://img.shields.io/badge/Java-ED8B00?style=flat&logo=openjdk&logoColor=white) 
![SQLite](https://img.shields.io/badge/SQLite-003B57?style=flat&logo=sqlite&logoColor=white)
![JavaFX](https://img.shields.io/badge/JavaFX-1B6AC6?style=flat&logo=java&logoColor=white)
![Tesseract](https://img.shields.io/badge/Tesseract-5A4FCF?style=flat&logo=tesseract&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=flat&logo=apachemaven&logoColor=white)

---

## Project Overview
- **Course:** 4PRJ1D – Integrated Development Project  
- **Institution:** HE2B ESI (Haute École Bruxelles-Brabant)  
- **Team:** Group D121  
- **Authors:** Wissam Abu Nasser (g58540), Mohamed Daouani (g62275)  
- **Language:** Java 17+  
- **Frameworks:** JavaFX, Tesseract OCR, SQLite  
- **Build System:** Maven  

---

## Objectives
This project was designed to:
- Assist referees in verifying player identities efficiently before matches.  
- Automate **text extraction** from ID cards using **OCR (Tesseract)**.  
- Provide a **user-friendly JavaFX interface** for image import, cropping, and data validation.  
- Implement **MVC architecture** and **SOLID principles** for maintainable code.  
- Use **multithreading** to ensure smooth user interaction during OCR and database operations.  

---

## Application Description

**RefCheck** streamlines pre-match identity verification:  
- Import and crop ID card photos.  
- Extract player details (name, surname, date of birth) via **Tesseract OCR**.  
- Compare extracted data with an **authorized player database**.  
- Display match details and verification history.  
- Detect mismatches and possible identity frauds automatically.  

---

## Architecture
The project follows a **modular MVC structure** with `module-info.java` for dependency management.

```
src/
├── controller/
│   ├── LoginController.java
│   ├── MatchController.java
│   └── OcrController.java
├── model/
│   ├── Player.java
│   ├── Match.java
│   ├── Referee.java
│   ├── DatabaseManager.java
│   └── OcrService.java
├── view/
│   ├── login.fxml
│   ├── match_details.fxml
│   └── ocr_view.fxml
└── Main.java
```

The design adheres to **SOLID** principles and applies the **Repository pattern** for database access.

---

## Functional Test Plan

| ID | Test | Input | Expected Result |
|----|------|--------|-----------------|
| T1 | Invalid login | Incorrect or empty credentials | Error message displayed |
| T2 | Valid login | Correct referee credentials | Redirected to match list |
| T3 | Display match details | Data from database | Match details displayed |
| T4 | Import photos | Files selected via FileChooser | Image previews displayed |
| T5 | Crop photos | User-defined crop regions | Cropped images confirmed |
| T6 | Extract data | ID card photo | Text fields filled automatically |
| T7 | Check data in DB | Extracted data | Match found and validated |
| T8 | Incorrect data | Invalid ID | Error message displayed |
| T9 | Update match sheet | Match data | Stats saved and displayed |

---

## Installation & Usage

### Requirements
- **Java 17+**  
- **Maven 3.6+**  
- **Git**  
- **Tesseract OCR** (must be installed and accessible from system PATH)

### Run the application

1. Clone the repository:
   ```bash
   git clone https://github.com/mohameddaouani/refcheck-javafx.git
   cd refcheck-javafx
   ```

2. Clean the project:
   ```bash
   mvn clean
   ```

3. Run the application:
   ```bash
   mvn javafx:run
   ```

4. Log in with one of the referees listed below, then select a match.

---

## Referee Login Credentials

| First Name | Last Name | Login | Password |
|-------------|------------|--------|-----------|
| Yasin | Alageyik | yalage | yalage01 |
| Jiri | Bergs | jbergs | jbergs01 |
| Juan | Boelen | jboele | jboele01 |
| Stefan | Vrijens | svrije | svrije01 |

---

## Known Issues
- When rescanning ID cards, newly extracted data **overwrites** previous results in the FXML display.  
- Match statistics were **not implemented** in the final version.

---

## Retrospective
- Initial design used **MVVM**, later switched to **MVC** for simplicity.  
- Class diagram structure evolved as new requirements emerged.  
- OCR integration introduced concurrency challenges, solved via threading.

---

## Key Features
- OCR text extraction using **Tesseract**  
- Modular **MVC architecture**  
- **JavaFX** interface with image cropping  
- **SQLite** database for player and match data  
- **Multithreading** for smooth execution  
- Follows **SOLID** and **Repository pattern** principles  

---

## Authors
**Wissam Abu Nasser** (g58540) & **Mohamed Daouani** (g62275)  
🎓 Haute École Bruxelles-Brabant – ESI (Group D121)

---

> © 2025 – RefCheck, developed for the 4PRJ1D course at HE2B ESI.
