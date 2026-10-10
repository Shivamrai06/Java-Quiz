<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="My results" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap stack">

        <div class="page-head">
            <div>
                <h1 class="mb-1">My results</h1>
                <p class="sub">Every quiz you have submitted, most recent first.</p>
            </div>
            <a class="btn btn-primary" href="${ctx}/quizzes">Attempt a quiz</a>
        </div>

        <c:if test="${not empty flashSuccess}">
            <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
        </c:if>

        <div class="grid grid-3">
            <div class="stat">
                <div class="label">Attempts</div>
                <div class="value">${summary.attempted}</div>
            </div>
            <div class="stat">
                <div class="label">Average</div>
                <div class="value">${summary.average}<span class="small muted">%</span></div>
            </div>
            <div class="stat">
                <div class="label">Best</div>
                <div class="value">${summary.best}<span class="small muted">%</span></div>
            </div>
        </div>

        <div class="card card-tight">
            <c:choose>
                <c:when test="${empty attempts}">
                    <div class="empty">
                        <div class="icon">&#128202;</div>
                        <h3>You have not attempted any quizzes yet</h3>
                        <p>Once you submit a quiz, your score and full answer review will appear here.</p>
                        <a class="btn btn-primary" href="${ctx}/quizzes">Browse quizzes</a>
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
                            <c:forEach var="attempt" items="${attempts}">
                                <tr>
                                    <td class="cell-strong"><c:out value="${attempt.quizTitle}" /></td>
                                    <td class="nowrap">${attempt.score} / ${attempt.totalQuestions}</td>
                                    <td>
                                        <span class="badge ${attempt.gradeBand}">
                                            <fmt:formatNumber value="${attempt.percentage}" maxFractionDigits="2" />%
                                        </span>
                                    </td>
                                    <td class="cell-muted nowrap">
                                        <fmt:formatDate value="${attempt.submittedAt}" pattern="dd MMM yyyy, HH:mm" />
                                    </td>
                                    <td class="text-right">
                                        <a class="btn btn-secondary btn-sm" href="${ctx}/result?id=${attempt.id}">View</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
