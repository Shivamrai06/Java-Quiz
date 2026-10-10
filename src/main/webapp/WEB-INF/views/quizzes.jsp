<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Quizzes" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap stack">

        <div class="page-head">
            <div>
                <h1 class="mb-1">Available quizzes</h1>
                <p class="sub">Pick a quiz to see its rules before you begin. You have attempted ${attempted} so far.</p>
            </div>
            <a class="btn btn-secondary" href="${ctx}/dashboard">Back to dashboard</a>
        </div>

        <c:if test="${not empty flashSuccess}">
            <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
        </c:if>

        <c:choose>
            <c:when test="${empty quizzes}">
                <div class="card">
                    <div class="empty">
                        <div class="icon">&#128218;</div>
                        <h3>No quizzes available</h3>
                        <p class="mb-0">There are no published quizzes at the moment. Please check back later.</p>
                    </div>
                </div>
            </c:when>
            <c:otherwise>
                <div class="grid grid-3">
                    <c:forEach var="quiz" items="${quizzes}">
                        <div class="card quiz-card">
                            <h3><c:out value="${quiz.title}" /></h3>
                            <p class="desc">
                                <c:out value="${empty quiz.description
                                        ? 'No description provided for this quiz.' : quiz.description}" />
                            </p>
                            <div class="meta">
                                <span class="badge">${quiz.questionCount} questions</span>
                                <c:if test="${not empty quiz.category}">
                                    <span class="badge">${quiz.category}</span>
                                </c:if>
                                <c:if test="${not empty quiz.durationMin}">
                                    <span class="badge">${quiz.durationMin} min</span>
                                </c:if>
                                <span class="badge">${quiz.attemptCount} attempts</span>
                            </div>
                            <c:choose>
                                <c:when test="${quiz.attemptable}">
                                    <a class="btn btn-primary btn-block" href="${ctx}/quiz?id=${quiz.id}">Start Quiz</a>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-secondary btn-block" disabled>No questions yet</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
