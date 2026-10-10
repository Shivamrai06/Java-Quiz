# QuizDesk — Java-Based Online Quiz Platform

QuizDesk is a Java-based online quiz platform built using Jakarta Servlets, JSP, JDBC, and MySQL. Students can register, attempt MCQ quizzes, and review their results, while administrators can manage quizzes, questions, users and results through a separate admin interface.

## 1. Project overview

The application is a classic layered Java web application (Servlet → Service → DAO → JDBC → MySQL). It was written for a Java web programming course, so the Java work is deliberately visible in the code rather than hidden behind a framework: no Spring Boot, no ORM.

## 2. Features

**Student**
- Register and log in with BCrypt-hashed passwords
- Browse published quizzes
- Read quiz instructions before starting
- Attempt MCQ quizzes with a question navigator and answered/unanswered states
- Confirmation prompt before submitting (for example, "You have answered 8 of 10 questions")
- Server-calculated score, percentage, correct / incorrect / unanswered breakdown and per-question review
- Results history and a profile page

**Administrator**
- Dashboard with totals for users, quizzes, questions and attempts, plus recent activity
- Create, edit and delete quizzes with DRAFT and PUBLISHED states
- Publishing is blocked until the quiz has at least one question
- Add, edit and delete questions with options A–D, the correct option and an optional explanation
- View registered users with their attempt counts
- View every quiz attempt across all students

## 3. Technology stack

| Layer | Technology |
| --- | --- |
| Language | Java 17 |
| Build | Apache Maven |
| Web server | Apache Tomcat 10.1 (Jakarta EE 10 — `jakarta.servlet.*`) |
| Web layer | Jakarta Servlets + JSP (JSTL) |
| Frontend | HTML5, CSS3, vanilla JavaScript |
| Database | MySQL 8 |
| DB access | JDBC with `PreparedStatement` |
| Security | BCrypt password hashing (jBCrypt) |
| Testing | JUnit 5 |

## 4. Architecture

```
Browser  (HTML5 / CSS3 / JavaScript)
   |
   v
JSP Views            render only, EL like ${quiz.title}
   |
   v
Servlet Controllers  HTTP, session, routing, no SQL
   |
   v
Service Layer        AuthService / QuizService / ResultService
   |
   v
DAO Layer            JDBC, PreparedStatement, ResultSet mapping
   |
   v
MySQL 8              quiz_platform schema
```

Each layer talks only to the layer directly beneath it. Servlets never build SQL, JSPs never contain business logic, and scoring is never trusted from the browser.

| Package | Responsibility |
| --- | --- |
| `com.quizdesk.controller` | Servlets, HTTP concerns, `BaseServlet` helpers |
| `com.quizdesk.controller.admin` | Admin-only servlets (`/admin/*`) |
| `com.quizdesk.service` | Business rules, validation, scoring, transactions |
| `com.quizdesk.dao` | Interfaces + JDBC implementations |
| `com.quizdesk.model` | `User`, `Quiz`, `Question`, `QuizOption`, `Attempt`, `Answer`, `Role` |
| `com.quizdesk.filter` | `AuthFilter`, `CharacterEncodingFilter` |
| `com.quizdesk.util` | `DBConnection`, `PasswordUtil`, `ValidationUtil`, `SessionUtil`, `StatsCounter` |

## 5. Database schema

Database: `quiz_platform`

| Table | Purpose | Key columns |
| --- | --- | --- |
| `users` | Registered students and admins | `id`, `name`, `email` UNIQUE, `password_hash`, `role` ENUM, `created_at` |
| `quizzes` | Quiz metadata and publishing state | `id`, `title`, `description`, `category`, `duration_min`, `status` ENUM, timestamps |
| `questions` | MCQ questions belonging to a quiz | `id`, `quiz_id` FK, `question_text`, `correct_option` CHAR(1), `explanation` |
| `question_options` | Normalised options A–D | `id`, `question_id` FK, `option_key` CHAR(1), `option_text` |
| `attempts` | One submitted attempt | `id`, `user_id` FK, `quiz_id` FK, `score`, `total_questions`, `percentage`, `started_at`, `submitted_at` |
| `answers` | Answer given per question in an attempt | `id`, `attempt_id` FK, `question_id` FK, `selected_option` CHAR(1) NULL, `is_correct` BOOLEAN |

Relationships:

```
USER 1 ------< ATTEMPT >------ 1 QUIZ
                  |
                  | 1
                  v
               ANSWER
                  |
                  | >------ 1 QUESTION 1 ------< QUESTION_OPTION
```

## 6. Prerequisites

- JDK 17 or newer
- Apache Maven 3.8 or newer
- MySQL 8 server running locally
- Apache Tomcat 10.1 (must be Tomcat 10+, because the project uses the `jakarta.*` namespace)

## 7. Database setup

```sql
CREATE DATABASE quiz_platform CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Then load the schema and the demo data (from the repository root):

```bash
mysql -u root -p quiz_platform < sql/schema.sql
mysql -u root -p quiz_platform < sql/seed.sql
```

`schema.sql` creates the six tables and their foreign keys. `seed.sql` inserts the demo quiz content, the demo accounts below, and a few completed attempts so the dashboards are not empty during the presentation.

## 8. Configuration

Copy the example file and enter your own MySQL credentials:

```bash
cp src/main/resources/db.properties.example src/main/resources/db.properties
```

```properties
db.url=jdbc:mysql://localhost:3306/quiz_platform
db.username=root
db.password=YOUR_PASSWORD
```

`db.properties` is listed in `.gitignore` and must never be committed. Only `db.properties.example` (with placeholders) belongs in the repository.

## 9. How to run

```bash
mvn clean package
```

This produces `target/online-quiz-platform-1.0.0.war`. Then either:

1. Copy that WAR into Tomcat's `webapps/` directory and start Tomcat (`bin/startup.sh` or `bin/startup.bat`), or
2. Deploy it from your IDE (IntelliJ IDEA / Eclipse) as an exploded web application on a Tomcat 10.1 run configuration.

Open:

```
http://localhost:8080/online-quiz-platform/
```

The landing page links to the login screen. The first request warms up the JDBC connection pool.

## 10. Demo credentials

| Name | Email | Password | Role |
| --- | --- | --- | --- |
| QuizDesk Admin | `admin@quizdesk.com` | `Admin@123` | ADMIN |
| Aarav Sharma | `aarav@student.com` | `Student@123` | STUDENT |
| Priya Nair | `priya@student.com` | `Java@123` | STUDENT |

These accounts come from `sql/seed.sql`; the passwords are stored only as BCrypt hashes.

## 11. Project structure

```
online-quiz-platform/
├── pom.xml
├── README.md
├── .gitignore
├── sql/
│   ├── schema.sql
│   └── seed.sql
├── screenshots/
└── src/
    ├── main/
    │   ├── java/com/quizdesk/
    │   │   ├── controller/        (student + shared servlets, admin/ subpackage)
    │   │   ├── model/             (User, Quiz, Question, QuizOption, Attempt, Answer, Role)
    │   │   ├── dao/               (DAO interfaces + JDBC implementations)
    │   │   ├── service/           (AuthService, QuizService, ResultService)
    │   │   ├── filter/            (AuthFilter, CharacterEncodingFilter)
    │   │   └── util/              (DBConnection, PasswordUtil, ValidationUtil, ...)
    │   ├── resources/
    │   │   ├── db.properties.example
    │   │   └── db.properties      (ignored by git)
    │   └── webapp/
    │       ├── index.html         (standalone landing page)
    │       ├── css/  js/  images/
    │       ├── error.jsp
    │       └── WEB-INF/
    │           ├── web.xml
    │           └── views/         (JSP views, including admin/)
    └── test/java/com/quizdesk/    (JUnit 5 tests)
```

## 12. Screenshots

Add your own screenshots to the `screenshots/` folder before submitting, for example:

- `login.png`, `dashboard.png`, `quiz.png`, `result.png`, `admin-dashboard.png`, `admin-questions.png`

A landing-page preview is available directly from the WAR at `index.html`.

## 13. Security notes

- Passwords are hashed with BCrypt (work factor 10); plain-text passwords are never stored or logged.
- All SQL is executed through `PreparedStatement` with bound parameters — no string concatenation of user input, so the classic SQL injection vector is closed.
- The session holds only `userId`, `userName` and `userRole`. No password or hash is ever placed in the session.
- `AuthFilter` protects every route: anonymous users are redirected to `/login?expired=1`, and any non-admin request to `/admin/*` is rejected with HTTP 403.
- All validation is repeated server-side (`ValidationUtil`, service layer) even though the JavaScript also validates.
- Users see generic messages ("Invalid email or password."). SQL errors, stack traces and file paths are only written to the server log.
- Quiz submission runs inside a JDBC transaction: the attempt row and its answer rows are committed together, or rolled back together.
- Logout invalidates the session.
- Servlets keep no request-specific data in instance fields, so a single servlet instance is safe to share across concurrent requests (`StatsCounter` uses `AtomicLong`).

## 14. Testing

```bash
mvn test
```

Included JUnit 5 tests:

| Test class | Covers |
| --- | --- |
| `PasswordUtilTest` | hashing, verification, wrong password, null/empty input |
| `ValidationUtilTest` | email format, required text, id parsing, HTML escaping |
| `QuizServiceTest` | quiz/question validation rules, publish requirements |
| `ResultServiceTest` | scoring, percentage rounding, unanswered questions, divide-by-zero guard |

Also worth rehearsing manually before the viva: valid and invalid login, duplicate registration, logout, an attempt with unanswered questions, an invalid quiz id, a student typing an `/admin/*` URL, and MySQL persistence after a restart.

## 15. Known limitations

- No email verification or password reset flow.
- The stored quiz duration is displayed but there is no server-side auto-submit timer.
- Admin result filtering is intentionally simple (no date-range queries).
- Retaking a quiz is always allowed; there is no attempt limit.

## 16. Future improvements

- Server-enforced timed quizzes with auto-submit
- Question bank import/export (CSV)
- Per-topic performance analytics for students
- Email notification when a new quiz is published
- Pagination on the admin user and result tables

## 17. Pushing to GitHub

```bash
git init
git add .
git commit -m "QuizDesk: Java-based online quiz platform (Servlets, JSP, JDBC, MySQL)"
git branch -M main
git remote add origin https://github.com/<your-username>/online-quiz-platform.git
git push -u origin main
```

Before pushing, confirm that `src/main/resources/db.properties` is not staged (`git status`), and that the repository contains `README.md`, `pom.xml`, `.gitignore`, `sql/` and `screenshots/`.
