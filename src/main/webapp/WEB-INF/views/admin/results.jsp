<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Results" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div>
                    <h1 class="mb-1">Results</h1>
                    <p class="sub muted">
                        Showing ${fn:length(attempts)} of ${totalCount} submitted attempt(s).
                    </p>
                </div>

                <div class="card">
                    <form method="get" action="${ctx}/admin/results">
                        <div class="field-row">
                            <div class="field mb-0">
                                <label for="quizId">Filter by quiz</label>
                                <select id="quizId" name="quizId">
                                    <option value="">All quizzes</option>
                                    <c:forEach var="quiz" items="${quizzes}">
                                        <option value="${quiz.id}" ${quizIdFilter eq quiz.id ? 'selected' : ''}>
                                            <c:out value="${quiz.title}" />
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>

                            <div class="field mb-0">
                                <label for="email">Filter by student email</label>
                                <input type="text" id="email" name="email"
                                       value="<c:out value='${emailFilter}' />" placeholder="aarav@student.com">
                            </div>

                            <div class="field mb-0" style="align-self:end;">
                                <div class="flex">
                                    <button class="btn btn-primary" type="submit">Apply</button>
                                    <a class="btn btn-secondary" href="${ctx}/admin/results">Clear</a>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>

                <div class="card card-tight">
                    <c:choose>
                        <c:when test="${empty attempts}">
                            <div class="empty">
                                <div class="icon">&#128202;</div>
                                <h3>No results match</h3>
                                <p class="mb-0">Try clearing the filters, or wait for students to submit a quiz.</p>
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
                                        <th>Attempt date</th>
                                        <th class="text-right">Action</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="attempt" items="${attempts}">
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

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
