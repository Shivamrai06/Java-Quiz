<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="ctx" value="${pageContext.request.contextPath}" />
<c:set var="statusCode" value="${requestScope['jakarta.servlet.error.status_code']}" />
<%
    // A real HTTP failure (404/500) must be reported as such; a friendly in-app error
    // (e.g. "not authorized") keeps the original status set by the servlet.
    Integer code = (Integer) request.getAttribute("jakarta.servlet.error.status_code");
    if (code != null) {
        response.setStatus(code.intValue());
    } else if (response.getStatus() == 200) {
        response.setStatus(400);
    }
%>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Something went wrong &middot; QuizDesk</title>
    <link rel="stylesheet" href="${ctx}/css/style.css">
</head>
<body>

<main class="auth-shell">
    <div class="auth-card" style="max-width:480px;">
        <div class="auth-brand">
            <span class="brand-mark">Q</span>
            <strong>QuizDesk</strong>
        </div>

        <c:choose>
            <c:when test="${statusCode eq 404 or statusCode eq 403}">
                <h1 style="font-size:23px;">Page not available</h1>
            </c:when>
            <c:otherwise>
                <h1 style="font-size:23px;">
                    <c:out value="${empty errorTitle ? 'Something went wrong' : errorTitle}" />
                </h1>
            </c:otherwise>
        </c:choose>

        <p class="muted">
            <c:choose>
                <c:when test="${not empty errorMessage}">
                    <c:out value="${errorMessage}" />
                </c:when>
                <c:when test="${statusCode eq 404}">
                    The page you requested could not be found. It may have been moved or deleted.
                </c:when>
                <c:otherwise>
                    We could not complete that request. Please try again, or return to your dashboard.
                </c:otherwise>
            </c:choose>
        </p>

        <p class="xsmall muted">
            If this keeps happening, sign out and sign in again.
        </p>

        <div class="flex">
            <c:choose>
                <c:when test="${sessionScope.userRole eq 'ADMIN'}">
                    <a class="btn btn-primary" href="${ctx}/admin/dashboard">Back to dashboard</a>
                </c:when>
                <c:when test="${not empty sessionScope.userId}">
                    <a class="btn btn-primary" href="${ctx}/dashboard">Back to dashboard</a>
                </c:when>
                <c:otherwise>
                    <a class="btn btn-primary" href="${ctx}/login">Go to sign in</a>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</main>

</body>
</html>
