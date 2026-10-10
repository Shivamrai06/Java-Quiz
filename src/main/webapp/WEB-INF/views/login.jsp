<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Sign in &middot; QuizDesk</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>

<main class="auth-shell">
    <div class="auth-card">
        <div class="auth-brand">
            <span class="brand-mark">Q</span>
            <strong>QuizDesk</strong>
        </div>

        <h1 style="font-size:23px;">Sign in to continue</h1>
        <p class="muted small">Access your quizzes, results, and profile.</p>

        <c:if test="${not empty errorMessage}">
            <div class="alert error" role="alert"><c:out value="${errorMessage}" /></div>
        </c:if>
        <c:if test="${not empty infoMessage}">
            <div class="alert info"><c:out value="${infoMessage}" /></div>
        </c:if>
        <c:if test="${not empty sessionScope.flashSuccess}">
            <div class="alert success" role="status"><c:out value="${sessionScope.flashSuccess}" /></div>
            <c:remove var="flashSuccess" scope="session" />
        </c:if>

        <form method="post" action="${ctx}/login" data-lock-on-submit>
            <div class="field">
                <label for="email">Email address</label>
                <input type="email" id="email" name="email" required autocomplete="username"
                       value="<c:out value='${email}' />" placeholder="you@college.edu">
            </div>

            <div class="field">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required
                       autocomplete="current-password" placeholder="Your password">
            </div>

            <button class="btn btn-primary btn-block" type="submit">Sign in</button>
        </form>

        <p class="auth-foot">
            New to QuizDesk? <a href="${ctx}/register">Create an account</a>
        </p>

        <div class="demo-creds">
            <strong style="color:var(--text);">Demo credentials</strong><br>
            Student &mdash; <code>aarav@student.com</code> / <code>Student@123</code><br>
            Admin &mdash; <code>admin@quizdesk.com</code> / <code>Admin@123</code>
        </div>
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
