<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Attempting quiz" />
<c:set var="questionCount" value="${fn:length(questions)}" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">

        <div class="quiz-toolbar">
            <div>
                <h1 class="mb-0" style="font-size:22px;"><c:out value="${quiz.title}" /></h1>
                <p class="small muted mb-0">
                    Question <span id="current-question">1</span> of ${questionCount}
                    &middot; ${quiz.questionCount} questions in total
                </p>
            </div>
            <c:if test="${not empty quiz.durationMin}">
                <span class="timer" id="quiz-timer" data-minutes="${quiz.durationMin}">
                    ${quiz.durationMin}:00
                </span>
            </c:if>
        </div>

        <div class="quiz-layout">

            <!-- ================= Questions ================= -->
            <form id="quiz-form" method="post" action="${ctx}/quiz/submit">
                <input type="hidden" name="quizId" value="${quiz.id}">

                <c:forEach var="question" items="${questions}" varStatus="loop">
                    <div class="question-card" id="q_${question.id}">
                        <div class="q-number">Question ${loop.index + 1} of ${questionCount}</div>
                        <div class="q-text"><c:out value="${question.questionText}" /></div>

                        <div class="option-list">
                            <c:forEach var="option" items="${question.options}">
                                <label class="option">
                                    <input type="radio" name="q_${question.id}" value="${option.optionKey}">
                                    <span class="key">${option.optionKey}</span>
                                    <span><c:out value="${option.optionText}" /></span>
                                </label>
                            </c:forEach>
                        </div>
                    </div>
                </c:forEach>

                <div class="quiz-actions">
                    <button class="btn btn-secondary" type="button" id="btn-prev">&larr; Previous</button>
                    <button class="btn btn-secondary" type="button" id="btn-next">Next &rarr;</button>
                    <button class="btn btn-primary" type="submit">Submit Quiz</button>
                </div>
            </form>

            <!-- ================= Navigator ================= -->
            <aside class="q-nav-panel">
                <div class="card">
                    <h3 class="mb-2">Questions</h3>
                    <div class="q-nav-grid">
                        <c:forEach var="question" items="${questions}" varStatus="loop">
                            <button type="button" class="q-nav-btn"
                                    data-target="q_${question.id}"
                                    aria-label="Go to question ${loop.index + 1}">
                                ${loop.index + 1}
                            </button>
                        </c:forEach>
                    </div>

                    <div class="q-nav-legend">
                        <span><i class="legend-dot current"></i> Current question</span>
                        <span><i class="legend-dot answered"></i> Answered</span>
                        <span><i class="legend-dot"></i> Not answered yet</span>
                    </div>
                </div>

                <div class="card" style="margin-top:16px;">
                    <p class="xsmall muted mb-0">
                        Answers are sent to the server on submit. The score is calculated from the
                        correct answers stored in the database.
                    </p>
                </div>
            </aside>

        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
