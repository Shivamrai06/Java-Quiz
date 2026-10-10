-- =====================================================================
--  QuizDesk :: seed.sql
--  Demo data for the college presentation.
--  Run AFTER schema.sql:   mysql -u root -p < sql/seed.sql
-- =====================================================================
USE quiz_platform;

-- ---------------------------------------------------------------------
-- Users
-- Passwords are BCrypt hashes (cost 10). The plain text values are only
-- documented here so the demo can be signed into - the database never
-- stores them.
--
--   admin@quizdesk.com   / Admin@123     (ADMIN)
--   aarav@student.com    / Student@123   (STUDENT)
--   priya@student.com    / Java@1234     (STUDENT)
-- ---------------------------------------------------------------------
INSERT INTO users (name, email, password_hash, role) VALUES
('QuizDesk Admin', 'admin@quizdesk.com',
 '$2a$10$kjzT46TkVsYKkJapxFpmgOghJ/CMkb.jYzS.pkBPBG3zmg.5Ed0R.', 'ADMIN'),

('Aarav Sharma', 'aarav@student.com',
 '$2a$10$5PjuXNYdvmVJs3mDjomou.50SQ9mx.t3m9Fwz3l/JQsjyUGDNewTq', 'STUDENT'),

('Priya Nair', 'priya@student.com',
 '$2a$10$EcPf6zdQpM8DfhQwnFmcs.0Ind4Zb0TrEwroh76.TyF5CgN4r8m..', 'STUDENT');

-- ---------------------------------------------------------------------
-- Quiz 1 : Java Fundamentals  (10 questions, PUBLISHED)
-- ---------------------------------------------------------------------
INSERT INTO quizzes (title, description, category, duration_min, status) VALUES
('Java Fundamentals',
 'A quick check of core Java: data types, operators, strings and the basics of the language.',
 'Core Java', 10, 'PUBLISHED');

SET @quiz = LAST_INSERT_ID();

INSERT INTO questions (quiz_id, question_text, correct_option, explanation) VALUES
(@quiz, 'Which keyword is used to create an object in Java?', 'B',
 'The new operator allocates memory and invokes a constructor.'),
(@quiz, 'What is the size of an int in Java?', 'C',
 'An int is a 32-bit signed two''s-complement integer in every JVM.'),
(@quiz, 'Which of these is NOT a primitive type in Java?', 'D',
 'String is a class in java.lang, not a primitive.'),
(@quiz, 'What does the == operator compare for two object references?', 'A',
 'For references, == compares identity (same object), not content.'),
(@quiz, 'Which method is the entry point of a Java application?', 'B',
 'public static void main(String[] args) is the JVM entry point.'),
(@quiz, 'What is the default value of a boolean instance field?', 'A',
 'Uninitialised boolean fields default to false.'),
(@quiz, 'Which collection does not allow duplicate elements?', 'C',
 'A Set is defined by uniqueness of its elements.'),
(@quiz, 'What is the result of 7 / 2 in Java (both int)?', 'B',
 'Integer division truncates toward zero, so 7 / 2 is 3.'),
(@quiz, 'Which keyword prevents a class from being inherited?', 'C',
 'A final class cannot be extended.'),
(@quiz, 'What is the parent class of every Java class that does not extend anything else?', 'D',
 'java.lang.Object is the root of the class hierarchy.');

INSERT INTO question_options (question_id, option_key, option_text)
SELECT id, 'A', 'class' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword is used to create an object%'
UNION ALL SELECT id, 'B', 'new' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword is used to create an object%'
UNION ALL SELECT id, 'C', 'object' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword is used to create an object%'
UNION ALL SELECT id, 'D', 'instance' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword is used to create an object%'
UNION ALL SELECT id, 'A', '16 bits' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the size of an int%'
UNION ALL SELECT id, 'B', '8 bits' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the size of an int%'
UNION ALL SELECT id, 'C', '32 bits' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the size of an int%'
UNION ALL SELECT id, 'D', '64 bits' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the size of an int%'
UNION ALL SELECT id, 'A', 'boolean' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which of these is NOT a primitive type%'
UNION ALL SELECT id, 'B', 'char' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which of these is NOT a primitive type%'
UNION ALL SELECT id, 'C', 'double' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which of these is NOT a primitive type%'
UNION ALL SELECT id, 'D', 'String' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which of these is NOT a primitive type%'
UNION ALL SELECT id, 'A', 'Object identity (same object)' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the == operator compare%'
UNION ALL SELECT id, 'B', 'Object content' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the == operator compare%'
UNION ALL SELECT id, 'C', 'Hash codes' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the == operator compare%'
UNION ALL SELECT id, 'D', 'Class names' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the == operator compare%'
UNION ALL SELECT id, 'A', 'start()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method is the entry point%'
UNION ALL SELECT id, 'B', 'main()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method is the entry point%'
UNION ALL SELECT id, 'C', 'run()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method is the entry point%'
UNION ALL SELECT id, 'D', 'init()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method is the entry point%'
UNION ALL SELECT id, 'A', 'false' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the default value of a boolean%'
UNION ALL SELECT id, 'B', 'true' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the default value of a boolean%'
UNION ALL SELECT id, 'C', 'null' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the default value of a boolean%'
UNION ALL SELECT id, 'D', '0' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the default value of a boolean%'
UNION ALL SELECT id, 'A', 'List' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which collection does not allow duplicate%'
UNION ALL SELECT id, 'B', 'Queue' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which collection does not allow duplicate%'
UNION ALL SELECT id, 'C', 'Set' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which collection does not allow duplicate%'
UNION ALL SELECT id, 'D', 'ArrayList' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which collection does not allow duplicate%'
UNION ALL SELECT id, 'A', '3.5' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the result of 7 / 2%'
UNION ALL SELECT id, 'B', '3' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the result of 7 / 2%'
UNION ALL SELECT id, 'C', '4' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the result of 7 / 2%'
UNION ALL SELECT id, 'D', 'Compilation error' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the result of 7 / 2%'
UNION ALL SELECT id, 'A', 'static' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword prevents a class from being inherited%'
UNION ALL SELECT id, 'B', 'private' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword prevents a class from being inherited%'
UNION ALL SELECT id, 'C', 'final' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword prevents a class from being inherited%'
UNION ALL SELECT id, 'D', 'abstract' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which keyword prevents a class from being inherited%'
UNION ALL SELECT id, 'A', 'Class' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the parent class of every Java class%'
UNION ALL SELECT id, 'B', 'Base' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the parent class of every Java class%'
UNION ALL SELECT id, 'C', 'Main' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the parent class of every Java class%'
UNION ALL SELECT id, 'D', 'Object' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the parent class of every Java class%';

-- ---------------------------------------------------------------------
-- Quiz 2 : OOP Concepts  (10 questions, PUBLISHED)
-- ---------------------------------------------------------------------
INSERT INTO quizzes (title, description, category, duration_min, status) VALUES
('OOP Concepts',
 'Encapsulation, inheritance, polymorphism and abstraction - the four pillars in practice.',
 'Object Oriented Programming', 12, 'PUBLISHED');

SET @quiz = LAST_INSERT_ID();

INSERT INTO questions (quiz_id, question_text, correct_option, explanation) VALUES
(@quiz, 'Which OOP principle hides internal state behind methods?', 'A',
 'Encapsulation exposes behaviour and keeps fields private.'),
(@quiz, 'What does the extends keyword express?', 'B',
 'extends creates an inheritance relationship (an "is-a" link).'),
(@quiz, 'Which type of polymorphism is resolved at runtime?', 'C',
 'Method overriding is dynamic dispatch, resolved at runtime.'),
(@quiz, 'What is an abstract class?', 'B',
 'It cannot be instantiated and may declare abstract methods.'),
(@quiz, 'How many classes can a Java class extend directly?', 'A',
 'Java supports single inheritance of implementation.'),
(@quiz, 'What does an interface define?', 'C',
 'An interface defines a contract; implementers supply the behaviour.'),
(@quiz, 'What is method overloading?', 'B',
 'Same method name, different parameter lists, resolved at compile time.'),
(@quiz, 'Which access modifier makes a member visible only inside its own class?', 'D',
 'private is the most restrictive modifier.'),
(@quiz, 'What is the purpose of the super keyword?', 'A',
 'super refers to the immediate parent class members.'),
(@quiz, 'What does the toString() method of Object return by default?', 'B',
 'It returns the class name followed by the hash code in hex.');

INSERT INTO question_options (question_id, option_key, option_text)
SELECT id, 'A', 'Encapsulation' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which OOP principle hides internal state%'
UNION ALL SELECT id, 'B', 'Compilation' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which OOP principle hides internal state%'
UNION ALL SELECT id, 'C', 'Iteration' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which OOP principle hides internal state%'
UNION ALL SELECT id, 'D', 'Serialization' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which OOP principle hides internal state%'
UNION ALL SELECT id, 'A', 'A method signature' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the extends keyword express%'
UNION ALL SELECT id, 'B', 'An inheritance relationship' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the extends keyword express%'
UNION ALL SELECT id, 'C', 'A package import' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the extends keyword express%'
UNION ALL SELECT id, 'D', 'A constructor call' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the extends keyword express%'
UNION ALL SELECT id, 'A', 'Method overloading' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which type of polymorphism is resolved at runtime%'
UNION ALL SELECT id, 'B', 'Variable shadowing' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which type of polymorphism is resolved at runtime%'
UNION ALL SELECT id, 'C', 'Method overriding' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which type of polymorphism is resolved at runtime%'
UNION ALL SELECT id, 'D', 'Type casting' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which type of polymorphism is resolved at runtime%'
UNION ALL SELECT id, 'A', 'A class with no methods' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is an abstract class%'
UNION ALL SELECT id, 'B', 'A class that cannot be instantiated' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is an abstract class%'
UNION ALL SELECT id, 'C', 'A class with only static methods' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is an abstract class%'
UNION ALL SELECT id, 'D', 'A final class' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is an abstract class%'
UNION ALL SELECT id, 'A', 'One' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'How many classes can a Java class extend directly%'
UNION ALL SELECT id, 'B', 'Two' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'How many classes can a Java class extend directly%'
UNION ALL SELECT id, 'C', 'Many' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'How many classes can a Java class extend directly%'
UNION ALL SELECT id, 'D', 'None' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'How many classes can a Java class extend directly%'
UNION ALL SELECT id, 'A', 'Fields only' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does an interface define%'
UNION ALL SELECT id, 'B', 'A database schema' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does an interface define%'
UNION ALL SELECT id, 'C', 'A contract of behaviour' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does an interface define%'
UNION ALL SELECT id, 'D', 'A constructor' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does an interface define%'
UNION ALL SELECT id, 'A', 'Same name, same parameters' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is method overloading%'
UNION ALL SELECT id, 'B', 'Same name, different parameters' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is method overloading%'
UNION ALL SELECT id, 'C', 'Different name, same parameters' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is method overloading%'
UNION ALL SELECT id, 'D', 'Rewriting a parent method' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is method overloading%'
UNION ALL SELECT id, 'A', 'public' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which access modifier makes a member visible only inside its own class%'
UNION ALL SELECT id, 'B', 'protected' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which access modifier makes a member visible only inside its own class%'
UNION ALL SELECT id, 'C', 'default' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which access modifier makes a member visible only inside its own class%'
UNION ALL SELECT id, 'D', 'private' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which access modifier makes a member visible only inside its own class%'
UNION ALL SELECT id, 'A', 'To refer to the immediate parent class' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of the super keyword%'
UNION ALL SELECT id, 'B', 'To create a static method' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of the super keyword%'
UNION ALL SELECT id, 'C', 'To stop inheritance' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of the super keyword%'
UNION ALL SELECT id, 'D', 'To declare a constant' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of the super keyword%'
UNION ALL SELECT id, 'A', 'An empty string' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the toString() method of Object return%'
UNION ALL SELECT id, 'B', 'Class name plus hash code' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the toString() method of Object return%'
UNION ALL SELECT id, 'C', 'The field values' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the toString() method of Object return%'
UNION ALL SELECT id, 'D', 'null' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does the toString() method of Object return%';

-- ---------------------------------------------------------------------
-- Quiz 3 : JDBC Basics  (8 questions, PUBLISHED)
-- ---------------------------------------------------------------------
INSERT INTO quizzes (title, description, category, duration_min, status) VALUES
('JDBC Basics',
 'Connecting Java to MySQL: drivers, statements, result sets and transactions.',
 'Database Integration', 8, 'PUBLISHED');

SET @quiz = LAST_INSERT_ID();

INSERT INTO questions (quiz_id, question_text, correct_option, explanation) VALUES
(@quiz, 'Which interface represents an open connection to a database?', 'B',
 'java.sql.Connection is the handle to the database session.'),
(@quiz, 'Which class family prevents SQL injection?', 'A',
 'PreparedStatement sends SQL and values separately, so a value is never parsed as SQL.'),
(@quiz, 'Which method executes a SELECT query?', 'C',
 'executeQuery() returns a ResultSet; executeUpdate() is for modifying statements.'),
(@quiz, 'What does ResultSet.next() do?', 'B',
 'It moves the cursor one row forward and reports whether a row is available.'),
(@quiz, 'Which method on Connection starts a transaction?', 'A',
 'setAutoCommit(false) starts an explicit transaction.'),
(@quiz, 'What should you do when a transaction fails?', 'C',
 'Rolling back discards the partial work so the data stays consistent.'),
(@quiz, 'Which try-with-resources behaviour is correct?', 'B',
 'Resources declared in the try header are closed automatically, in reverse order.'),
(@quiz, 'What is the purpose of Class.forName("com.mysql.cj.jdbc.Driver")?', 'A',
 'It loads the JDBC driver class so the DriverManager can find it.');

INSERT INTO question_options (question_id, option_key, option_text)
SELECT id, 'A', 'Statement' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which interface represents an open connection%'
UNION ALL SELECT id, 'B', 'Connection' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which interface represents an open connection%'
UNION ALL SELECT id, 'C', 'ResultSet' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which interface represents an open connection%'
UNION ALL SELECT id, 'D', 'DriverManager' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which interface represents an open connection%'
UNION ALL SELECT id, 'A', 'PreparedStatement' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which class family prevents SQL injection%'
UNION ALL SELECT id, 'B', 'Statement' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which class family prevents SQL injection%'
UNION ALL SELECT id, 'C', 'CallableStatement' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which class family prevents SQL injection%'
UNION ALL SELECT id, 'D', 'ResultSetMetaData' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which class family prevents SQL injection%'
UNION ALL SELECT id, 'A', 'executeUpdate()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method executes a SELECT query%'
UNION ALL SELECT id, 'B', 'execute()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method executes a SELECT query%'
UNION ALL SELECT id, 'C', 'executeQuery()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method executes a SELECT query%'
UNION ALL SELECT id, 'D', 'commit()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method executes a SELECT query%'
UNION ALL SELECT id, 'A', 'Closes the ResultSet' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does ResultSet.next() do%'
UNION ALL SELECT id, 'B', 'Moves to the next row and returns true if one exists' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does ResultSet.next() do%'
UNION ALL SELECT id, 'C', 'Returns the current row as a String' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does ResultSet.next() do%'
UNION ALL SELECT id, 'D', 'Counts the rows' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What does ResultSet.next() do%'
UNION ALL SELECT id, 'A', 'setAutoCommit(false)' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method on Connection starts a transaction%'
UNION ALL SELECT id, 'B', 'startTransaction()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method on Connection starts a transaction%'
UNION ALL SELECT id, 'C', 'beginWork()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method on Connection starts a transaction%'
UNION ALL SELECT id, 'D', 'commit()' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which method on Connection starts a transaction%'
UNION ALL SELECT id, 'A', 'Ignore it' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What should you do when a transaction fails%'
UNION ALL SELECT id, 'B', 'Commit the partial work' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What should you do when a transaction fails%'
UNION ALL SELECT id, 'C', 'Roll back' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What should you do when a transaction fails%'
UNION ALL SELECT id, 'D', 'Restart the database' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What should you do when a transaction fails%'
UNION ALL SELECT id, 'A', 'Resources must be closed manually' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which try-with-resources behaviour%'
UNION ALL SELECT id, 'B', 'Resources are closed automatically' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which try-with-resources behaviour%'
UNION ALL SELECT id, 'C', 'Only the first resource is closed' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which try-with-resources behaviour%'
UNION ALL SELECT id, 'D', 'It only works with Connection' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'Which try-with-resources behaviour%'
UNION ALL SELECT id, 'A', 'Loads the JDBC driver class' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of Class.forName%'
UNION ALL SELECT id, 'B', 'Opens a connection' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of Class.forName%'
UNION ALL SELECT id, 'C', 'Creates the database' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of Class.forName%'
UNION ALL SELECT id, 'D', 'Validates the SQL' FROM questions WHERE quiz_id = @quiz AND question_text LIKE 'What is the purpose of Class.forName%';

-- ---------------------------------------------------------------------
-- Quiz 4 : Exception Handling (DRAFT - shows the unpublished state)
-- ---------------------------------------------------------------------
INSERT INTO quizzes (title, description, category, duration_min, status) VALUES
('Exception Handling',
 'try / catch / finally, checked versus unchecked exceptions, and custom exceptions.',
 'Core Java', 8, 'DRAFT');

-- ---------------------------------------------------------------------
-- Demo attempts so the dashboard and the admin results page are not empty
-- ---------------------------------------------------------------------
SET @aarav  = (SELECT id FROM users WHERE email = 'aarav@student.com');
SET @priya  = (SELECT id FROM users WHERE email = 'priya@student.com');
SET @java   = (SELECT id FROM quizzes WHERE title = 'Java Fundamentals');
SET @oop    = (SELECT id FROM quizzes WHERE title = 'OOP Concepts');
SET @jdbc   = (SELECT id FROM quizzes WHERE title = 'JDBC Basics');

-- Aarav - Java Fundamentals: 8 / 10  = 80%
INSERT INTO attempts (user_id, quiz_id, score, total_questions, percentage, started_at, submitted_at)
VALUES (@aarav, @java, 8, 10, 80.00, NOW() - INTERVAL 4 DAY, NOW() - INTERVAL 4 DAY + INTERVAL 7 MINUTE);
SET @attempt1 = LAST_INSERT_ID();

-- Answers for attempt 1: the first two questions are answered wrongly on purpose.
INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt1, q.id,
       CASE WHEN q.correct_option = 'A' THEN 'B' ELSE q.correct_option END,
       FALSE
  FROM questions q
 WHERE q.quiz_id = @java
 ORDER BY q.id
 LIMIT 2;

INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt1, q.id, q.correct_option, TRUE
  FROM questions q
 WHERE q.quiz_id = @java
   AND q.id NOT IN (SELECT question_id FROM answers WHERE attempt_id = @attempt1);

-- Priya - OOP Concepts: 14 / 20 is impossible here (10 questions), so 9 / 10 = 90%
INSERT INTO attempts (user_id, quiz_id, score, total_questions, percentage, started_at, submitted_at)
VALUES (@priya, @oop, 9, 10, 90.00, NOW() - INTERVAL 2 DAY, NOW() - INTERVAL 2 DAY + INTERVAL 9 MINUTE);
SET @attempt2 = LAST_INSERT_ID();

INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt2, q.id,
       CASE WHEN q.correct_option = 'A' THEN 'C' ELSE q.correct_option END,
       FALSE
  FROM questions q
 WHERE q.quiz_id = @oop
 ORDER BY q.id
 LIMIT 1;

INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt2, q.id, q.correct_option, TRUE
  FROM questions q
 WHERE q.quiz_id = @oop
   AND q.id NOT IN (SELECT question_id FROM answers WHERE attempt_id = @attempt2);

-- Aarav - JDBC Basics: 6 / 8 = 75%
INSERT INTO attempts (user_id, quiz_id, score, total_questions, percentage, started_at, submitted_at)
VALUES (@aarav, @jdbc, 6, 8, 75.00, NOW() - INTERVAL 1 DAY, NOW() - INTERVAL 1 DAY + INTERVAL 5 MINUTE);
SET @attempt3 = LAST_INSERT_ID();

INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt3, q.id,
       CASE WHEN q.correct_option = 'A' THEN 'D' ELSE q.correct_option END,
       FALSE
  FROM questions q
 WHERE q.quiz_id = @jdbc
 ORDER BY q.id
 LIMIT 2;

INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt3, q.id, q.correct_option, TRUE
  FROM questions q
 WHERE q.quiz_id = @jdbc
   AND q.id NOT IN (SELECT question_id FROM answers WHERE attempt_id = @attempt3);

-- One unanswered question, to prove the "Not answered" state is stored and rendered.
INSERT INTO answers (attempt_id, question_id, selected_option, is_correct)
SELECT @attempt3, q.id, NULL, FALSE
  FROM questions q
 WHERE q.quiz_id = @jdbc
 ORDER BY q.id DESC
 LIMIT 1
ON DUPLICATE KEY UPDATE selected_option = NULL, is_correct = FALSE;

-- ---------------------------------------------------------------------
-- Verification
-- ---------------------------------------------------------------------
SELECT 'Seed data loaded' AS status;
SELECT (SELECT COUNT(*) FROM users)     AS users,
       (SELECT COUNT(*) FROM quizzes)   AS quizzes,
       (SELECT COUNT(*) FROM questions) AS questions,
       (SELECT COUNT(*) FROM question_options) AS options,
       (SELECT COUNT(*) FROM attempts)  AS attempts,
       (SELECT COUNT(*) FROM answers)   AS answers;
