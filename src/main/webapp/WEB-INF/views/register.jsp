<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Create account &middot; QuizDesk</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>

<main class="auth-shell">
    <div class="auth-card">
        <div class="auth-brand">
            <span class="brand-mark">Q</span>
            <strong>QuizDesk</strong>
        </div>

        <h1 style="font-size:23px;">Create your account</h1>
        <p class="muted small">Register as a student to attempt quizzes and track your scores.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert error" role="alert"><c:out value="${errorMessage}" /></div>
        </c:if>

        <form method="post" action="${ctx}/register" data-lock-on-submit>
            <div class="field ${errorField eq 'name' ? 'has-error' : ''}">
                <label for="name">Full name</label>
                <input type="text" id="name" name="name" required maxlength="100" autocomplete="name"
                       value="<c:out value='${name}' />" placeholder="Aarav Sharma">
            </div>

            <div class="field ${errorField eq 'email' ? 'has-error' : ''}">
                <label for="email">Email address</label>
                <input type="email" id="email" name="email" required maxlength="150" autocomplete="email"
                       value="<c:out value='${email}' />" placeholder="you@college.edu">
            </div>

            <div class="field ${errorField eq 'password' ? 'has-error' : ''}">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required minlength="6"
                       autocomplete="new-password" placeholder="At least 6 characters">
                <p class="hint">Use at least 6 characters. Passwords are stored as BCrypt hashes.</p>
            </div>

            <div class="field ${errorField eq 'confirmPassword' ? 'has-error' : ''}">
                <label for="confirmPassword">Confirm password</label>
                <input type="password" id="confirmPassword" name="confirmPassword" required minlength="6"
                       autocomplete="new-password" placeholder="Repeat your password">
            </div>

            <button class="btn btn-primary btn-block" type="submit">Create account</button>
        </form>

        <p class="auth-foot">
            Already registered? <a href="${ctx}/login">Sign in</a>
        </p>
    </div>
</main>

<footer class="footer">
    <div class="wrap">
        <span>QuizDesk &middot; Java-based online quiz platform</span>
        <span>Jakarta Servlets &middot; JSP &middot; JDBC &middot; MySQL</span>
    </div>
</footer>

<script src="${ctx}/js/app.js" defer></script>
</body>
</html>
