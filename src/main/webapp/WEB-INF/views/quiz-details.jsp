<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Quiz overview" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">
        <div style="max-width:720px;margin:0 auto;">

            <p class="small"><a href="${ctx}/quizzes">&larr; All quizzes</a></p>

            <div class="card">
                <div class="flex-between mb-2">
                    <div>
                        <h1 class="mb-1" style="font-size:26px;"><c:out value="${quiz.title}" /></h1>
                        <p class="muted mb-0">
                            <c:out value="${empty quiz.description
                                    ? 'No description provided for this quiz.' : quiz.description}" />
                        </p>
                    </div>
                    <c:if test="${not empty quiz.category}">
                        <span class="badge accent">${quiz.category}</span>
                    </c:if>
                </div>

                <hr class="hr">

                <div class="grid grid-3 mb-3">
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Questions</div>
                        <div class="value" style="font-size:22px;">${quiz.questionCount}</div>
                    </div>
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Time limit</div>
                        <div class="value" style="font-size:22px;">
                            <c:choose>
                                <c:when test="${not empty quiz.durationMin}">${quiz.durationMin} min</c:when>
                                <c:otherwise>None</c:otherwise>
                            </c:choose>
                        </div>
                    </div>
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Attempts so far</div>
                        <div class="value" style="font-size:22px;">${quiz.attemptCount}</div>
                    </div>
                </div>

                <h3>Before you start</h3>
                <ul class="muted small" style="padding-left:20px;line-height:1.9;">
                    <li>Select exactly one answer for each question.</li>
                    <li>You can move between questions and change your answers before submitting.</li>
                    <li>Unanswered questions are stored as unanswered and score zero.</li>
                    <li>Your score is calculated on the server after submission.</li>
                    <c:if test="${not empty quiz.durationMin}">
                        <li>The quiz is limited to ${quiz.durationMin} minutes and submits automatically when time runs out.</li>
                    </c:if>
                </ul>

                <c:choose>
                    <c:when test="${quiz.attemptable}">
                        <a class="btn btn-primary" href="${ctx}/quiz?id=${quiz.id}&amp;mode=start">Start Quiz</a>
                        <a class="btn btn-ghost" href="${ctx}/quizzes">Cancel</a>
                    </c:when>
                    <c:otherwise>
                        <div class="alert error mb-0" role="alert">
                            This quiz does not have any questions yet, so it cannot be attempted.
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
