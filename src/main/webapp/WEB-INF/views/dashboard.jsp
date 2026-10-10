<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Dashboard" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap stack">

        <div class="page-head">
            <div>
                <h1 class="mb-1"><c:out value="${greeting}" />, <c:out value="${sessionScope.userName}" /></h1>
                <p class="sub">
                    <c:choose>
                        <c:when test="${attemptableCount gt 0}">
                            You have ${attemptableCount} quiz<c:if test="${attemptableCount ne 1}">zes</c:if> ready to attempt.
                        </c:when>
                        <c:otherwise>
                            No published quizzes yet &mdash; check back after your instructor publishes one.
                        </c:otherwise>
                    </c:choose>
                </p>
            </div>
            <a class="btn btn-primary" href="${ctx}/quizzes">Browse all quizzes</a>
        </div>

        <c:if test="${not empty flashSuccess}">
            <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
        </c:if>
        <c:if test="${not empty flashError}">
            <div class="alert error" role="alert"><c:out value="${flashError}" /></div>
        </c:if>

        <!-- ================= Summary ================= -->
        <div class="grid grid-4">
            <div class="stat">
                <div class="label">Available quizzes</div>
                <div class="value">${attemptableCount}</div>
                <div class="hint">Published and ready</div>
            </div>
            <div class="stat">
                <div class="label">Quizzes attempted</div>
                <div class="value">${summary.attempted}</div>
                <div class="hint">Total submissions</div>
            </div>
            <div class="stat">
                <div class="label">Average score</div>
                <div class="value">${summary.average}<span class="small muted">%</span></div>
                <div class="hint">Across all attempts</div>
            </div>
            <div class="stat">
                <div class="label">Best score</div>
                <div class="value">${summary.best}<span class="small muted">%</span></div>
                <div class="hint">Your highest result</div>
            </div>
        </div>

        <!-- ================= Available quizzes ================= -->
        <div class="card">
            <div class="card-head">
                <h2>Available quizzes</h2>
                <a class="small" href="${ctx}/quizzes">View all</a>
            </div>

            <c:choose>
                <c:when test="${empty quizzes}">
                    <div class="empty">
                        <div class="icon">&#128218;</div>
                        <h3>No quizzes available yet</h3>
                        <p class="mb-0">Published quizzes will appear here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="grid grid-3">
                        <c:forEach var="quiz" items="${quizzes}" varStatus="loop">
                            <c:if test="${loop.index lt 6}">
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
                                    </div>
                                    <a class="btn btn-primary btn-block"
                                       href="${ctx}/quiz?id=${quiz.id}">Start Quiz</a>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- ================= Recent results ================= -->
        <div class="card card-tight">
            <div style="padding:20px 22px 0;">
                <div class="card-head">
                    <h2>Recent results</h2>
                    <a class="small" href="${ctx}/results">View all results</a>
                </div>
            </div>

            <c:choose>
                <c:when test="${empty recentAttempts}">
                    <div class="empty">
                        <div class="icon">&#128202;</div>
                        <h3>No attempts yet</h3>
                        <p class="mb-0">Start your first quiz and your score will appear here.</p>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-scroll">
                        <table class="data">
                            <thead>
                            <tr>
                                <th>Quiz</th>
                                <th>Score</th>
                                <th>Percentage</th>
                                <th>Attempted on</th>
                                <th class="text-right">Action</th>
                            </tr>
                            </thead>
                            <tbody>
                            <c:forEach var="attempt" items="${recentAttempts}">
                                <tr>
                                    <td class="cell-strong"><c:out value="${attempt.quizTitle}" /></td>
                                    <td>${attempt.score} / ${attempt.totalQuestions}</td>
                                    <td>
                                        <span class="badge ${attempt.gradeBand}">
                                            <fmt:formatNumber value="${attempt.percentage}" maxFractionDigits="2" />%
                                        </span>
                                    </td>
                                    <td class="cell-muted">
                                        <fmt:formatDate value="${attempt.submittedAt}" pattern="dd MMM yyyy, HH:mm" />
                                    </td>
                                    <td class="text-right">
                                        <a class="btn btn-secondary btn-sm"
                                           href="${ctx}/result?id=${attempt.id}">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <p class="xsmall muted mb-0">
            Server diagnostics &mdash; requests served: ${requestsServed}; JDBC pool: ${connectionPool}
        </p>

    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
