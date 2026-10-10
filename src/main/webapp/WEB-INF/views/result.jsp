<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Result" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap stack">

        <div class="result-hero">
            <p class="badge accent mb-2">Quiz Complete</p>
            <div class="score">${attempt.score} / ${attempt.totalQuestions}</div>
            <div class="pct">
                <fmt:formatNumber value="${attempt.percentage}" maxFractionDigits="2" />% correct
            </div>

            <div class="grid grid-3" style="margin-top:24px;">
                <div class="stat" style="border:0;background:var(--success-soft);">
                    <div class="label" style="color:var(--success);">Correct</div>
                    <div class="value" style="color:var(--success);">${result.correctCount}</div>
                </div>
                <div class="stat" style="border:0;background:var(--error-soft);">
                    <div class="label" style="color:var(--error);">Incorrect</div>
                    <div class="value" style="color:var(--error);">${result.incorrectCount}</div>
                </div>
                <div class="stat" style="border:0;background:var(--warning-soft);">
                    <div class="label" style="color:var(--warning);">Not answered</div>
                    <div class="value" style="color:var(--warning);">${result.unansweredCount}</div>
                </div>
            </div>

            <p class="small muted" style="margin-top:18px;margin-bottom:0;">
                <c:out value="${quiz.title}" /> &middot;
                Submitted <fmt:formatDate value="${attempt.submittedAt}" pattern="dd MMM yyyy, HH:mm" />
            </p>
        </div>

        <div class="flex">
            <a class="btn btn-primary" href="${ctx}/dashboard">Back to Dashboard</a>
            <a class="btn btn-secondary" href="${ctx}/results">View All Results</a>
            <c:if test="${quiz.attemptable}">
                <a class="btn btn-secondary" href="${ctx}/quiz?id=${quiz.id}&amp;mode=start">Retake Quiz</a>
            </c:if>
        </div>

        <div>
            <h2 class="mb-2">Question review</h2>

            <c:forEach var="answer" items="${answers}" varStatus="loop">
                <div class="review-item ${answer.statusClass}">
                    <div class="flex-between mb-1">
                        <span class="xsmall muted">Question ${loop.index + 1}</span>
                        <span class="badge ${answer.statusClass}">${answer.statusLabel}</span>
                    </div>

                    <div class="q-text"><c:out value="${answer.questionText}" /></div>

                    <div class="answer-line">
                        <span class="answer-label">Your answer</span>
                        <span>
                            <c:choose>
                                <c:when test="${answer.unanswered}">
                                    <span class="muted">Not answered</span>
                                </c:when>
                                <c:otherwise>
                                    <strong>${answer.selectedOption}.</strong>
                                    <c:out value="${answer.selectedText}" />
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <div class="answer-line">
                        <span class="answer-label">Correct answer</span>
                        <span>
                            <strong>${answer.correctOption}.</strong>
                            <c:out value="${answer.correctText}" />
                        </span>
                    </div>

                    <c:if test="${not empty answer.explanation}">
                        <div class="explanation">
                            <strong style="color:var(--text);">Why:</strong>
                            <c:out value="${answer.explanation}" />
                        </div>
                    </c:if>
                </div>
            </c:forEach>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
