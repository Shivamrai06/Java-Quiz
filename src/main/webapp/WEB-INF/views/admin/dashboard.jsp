<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:set var="pageTitle" value="Admin dashboard" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div class="page-head" style="margin-bottom:0;">
                    <div>
                        <h1 class="mb-1">Admin dashboard</h1>
                        <p class="sub">Overview of quizzes, questions and student activity.</p>
                    </div>
                    <div class="flex">
                        <a class="btn btn-secondary" href="${ctx}/admin/quiz/create">Create Quiz</a>
                        <a class="btn btn-primary" href="${ctx}/admin/quizzes">Add Question</a>
                    </div>
                </div>

                <c:if test="${not empty flashSuccess}">
                    <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert error" role="alert"><c:out value="${flashError}" /></div>
                </c:if>

                <div class="grid grid-4">
                    <div class="stat">
                        <div class="label">Total users</div>
                        <div class="value">${userCount}</div>
                        <div class="hint">Registered accounts</div>
                    </div>
                    <div class="stat">
                        <div class="label">Quizzes</div>
                        <div class="value">${counters.quizzes}</div>
                        <div class="hint">${counters.published} published</div>
                    </div>
                    <div class="stat">
                        <div class="label">Questions</div>
                        <div class="value">${counters.questions}</div>
                        <div class="hint">Across all quizzes</div>
                    </div>
                    <div class="stat">
                        <div class="label">Attempts</div>
                        <div class="value">${counters.attempts}</div>
                        <div class="hint">Submissions received</div>
                    </div>
                </div>

                <div class="card card-tight">
                    <div style="padding:20px 22px 0;">
                        <div class="card-head">
                            <h2>Recent activity</h2>
                            <a class="small" href="${ctx}/admin/results">All results</a>
                        </div>
                    </div>

                    <c:choose>
                        <c:when test="${empty recentAttempts}">
                            <div class="empty">
                                <div class="icon">&#128202;</div>
                                <h3>No quiz attempts yet</h3>
                                <p class="mb-0">Student submissions will be listed here.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-scroll">
                                <table class="data">
                                    <thead>
                                    <tr>
                                        <th>Student</th>
                                        <th>Quiz</th>
                                        <th>Score</th>
                                        <th>Percentage</th>
                                        <th>Submitted</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="attempt" items="${recentAttempts}">
                                        <tr>
                                            <td>
                                                <div class="cell-strong"><c:out value="${attempt.studentName}" /></div>
                                                <div class="cell-muted"><c:out value="${attempt.studentEmail}" /></div>
                                            </td>
                                            <td><c:out value="${attempt.quizTitle}" /></td>
                                            <td class="nowrap">${attempt.score} / ${attempt.totalQuestions}</td>
                                            <td>
                                                <span class="badge ${attempt.gradeBand}">
                                                    <fmt:formatNumber value="${attempt.percentage}" maxFractionDigits="2" />%
                                                </span>
                                            </td>
                                            <td class="cell-muted nowrap">
                                                <fmt:formatDate value="${attempt.submittedAt}" pattern="dd MMM yyyy, HH:mm" />
                                            </td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="card">
                    <h2 class="mb-2">Runtime diagnostics</h2>
                    <p class="small muted">
                        These figures come from the running Java process, not the database, so they
                        show that requests are being handled by the servlet container.
                    </p>
                    <div class="mono-box">
                        requests served &nbsp;: ${requestsServed}<br>
                        JDBC pool &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;: ${poolSummary}<br>
                        successful logins: ${successfulLogins}<br>
                        quiz submissions : ${quizSubmissions}<br>
                        uptime (minutes) : ${uptimeMinutes}
                    </div>
                </div>

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
