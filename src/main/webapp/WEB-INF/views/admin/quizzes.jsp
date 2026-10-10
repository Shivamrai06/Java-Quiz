<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Manage quizzes" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div class="page-head" style="margin-bottom:0;">
                    <div>
                        <h1 class="mb-1">Quizzes</h1>
                        <p class="sub">Create, edit and publish the quizzes students can attempt.</p>
                    </div>
                    <a class="btn btn-primary" href="${ctx}/admin/quiz/create">Create Quiz</a>
                </div>

                <c:if test="${not empty flashSuccess}">
                    <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert error" role="alert"><c:out value="${flashError}" /></div>
                </c:if>

                <div class="card card-tight">
                    <c:choose>
                        <c:when test="${empty quizzes}">
                            <div class="empty">
                                <div class="icon">&#128218;</div>
                                <h3>No quizzes yet</h3>
                                <p>Create your first quiz, then add its questions.</p>
                                <a class="btn btn-primary" href="${ctx}/admin/quiz/create">Create Quiz</a>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-scroll">
                                <table class="data">
                                    <thead>
                                    <tr>
                                        <th>Quiz</th>
                                        <th>Category</th>
                                        <th>Questions</th>
                                        <th>Attempts</th>
                                        <th>Status</th>
                                        <th>Updated</th>
                                        <th class="text-right">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="quiz" items="${quizzes}">
                                        <tr>
                                            <td>
                                                <div class="cell-strong"><c:out value="${quiz.title}" /></div>
                                                <c:if test="${not empty quiz.durationMin}">
                                                    <div class="cell-muted">${quiz.durationMin} min limit</div>
                                                </c:if>
                                            </td>
                                            <td class="cell-muted"><c:out value="${quiz.category}" /></td>
                                            <td>${quiz.questionCount}</td>
                                            <td>${quiz.attemptCount}</td>
                                            <td>
                                                <span class="badge ${quiz.published ? 'good' : 'fair'}">
                                                    ${quiz.status.label}
                                                </span>
                                            </td>
                                            <td class="cell-muted nowrap">
                                                <fmt:formatDate value="${quiz.updatedAt}" pattern="dd MMM yyyy" />
                                            </td>
                                            <td>
                                                <div class="actions">
                                                    <a class="btn btn-secondary btn-sm"
                                                       href="${ctx}/admin/questions?quizId=${quiz.id}">Questions</a>
                                                    <a class="btn btn-secondary btn-sm"
                                                       href="${ctx}/admin/quiz/edit?id=${quiz.id}">Edit</a>
                                                    <form method="post" action="${ctx}/admin/quiz/delete" class="mb-0">
                                                        <input type="hidden" name="id" value="${quiz.id}">
                                                        <button class="btn btn-danger btn-sm" type="submit"
                                                                data-confirm="Delete &quot;${fn:escapeXml(quiz.title)}&quot;? Its questions, attempts and results will be permanently removed.">
                                                            Delete
                                                        </button>
                                                    </form>
                                                </div>
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
                    A quiz can only be published once it contains at least one question.
                </p>

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
