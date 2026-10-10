<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="Manage questions" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<c:set var="editing" value="${requestScope.editing}" />
<c:set var="isEditMode" value="${not empty editing}" />

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div class="page-head" style="margin-bottom:0;">
                    <div>
                        <p class="small mb-1"><a href="${ctx}/admin/quizzes">&larr; All quizzes</a></p>
                        <h1 class="mb-1"><c:out value="${quiz.title}" /></h1>
                        <p class="sub">
                            ${fn:length(questions)} question<c:if test="${fn:length(questions) ne 1}">s</c:if>
                            &middot;
                            <span class="badge ${quiz.published ? 'good' : 'fair'}">${quiz.status.label}</span>
                        </p>
                    </div>
                    <a class="btn btn-secondary" href="${ctx}/admin/quiz/edit?id=${quiz.id}">Edit quiz details</a>
                </div>

                <c:if test="${not empty flashSuccess}">
                    <div class="alert success" role="status"><c:out value="${flashSuccess}" /></div>
                </c:if>
                <c:if test="${not empty flashError}">
                    <div class="alert error" role="alert"><c:out value="${flashError}" /></div>
                </c:if>

                <!-- ================= Question list ================= -->
                <div class="card card-tight">
                    <c:choose>
                        <c:when test="${empty questions}">
                            <div class="empty">
                                <div class="icon">&#10067;</div>
                                <h3>No questions in this quiz yet</h3>
                                <p class="mb-0">Add the first question using the form below. A quiz cannot be
                                    published until it has at least one question.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-scroll">
                                <table class="data">
                                    <thead>
                                    <tr>
                                        <th style="width:52px;">#</th>
                                        <th>Question</th>
                                        <th>Correct</th>
                                        <th class="text-right">Actions</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="question" items="${questions}" varStatus="loop">
                                        <tr>
                                            <td class="cell-muted">${loop.index + 1}</td>
                                            <td>
                                                <div class="cell-strong"><c:out value="${question.shortText(90)}" /></div>
                                                <div class="cell-muted">
                                                    A. <c:out value="${question.options[0].optionText}" />
                                                </div>
                                            </td>
                                            <td><span class="badge good">${question.correctOption}</span></td>
                                            <td>
                                                <div class="actions">
                                                    <a class="btn btn-secondary btn-sm"
                                                       href="${ctx}/admin/questions?quizId=${quiz.id}&amp;edit=${question.id}">Edit</a>
                                                    <form method="post" action="${ctx}/admin/question/delete" class="mb-0">
                                                        <input type="hidden" name="id" value="${question.id}">
                                                        <input type="hidden" name="quizId" value="${quiz.id}">
                                                        <button class="btn btn-danger btn-sm" type="submit"
                                                                data-confirm="Delete this question? If it is the last one, the quiz returns to draft.">
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

                <!-- ================= Add / edit form ================= -->
                <div class="card">
                    <div class="card-head">
                        <h2>${isEditMode ? 'Edit question' : 'Add a question'}</h2>
                        <c:if test="${isEditMode}">
                            <a class="small" href="${ctx}/admin/questions?quizId=${quiz.id}">Cancel edit</a>
                        </c:if>
                    </div>

                    <form method="post"
                          action="${ctx}/admin/question/${isEditMode ? 'edit' : 'create'}"
                          data-lock-on-submit>

                        <input type="hidden" name="quizId" value="${quiz.id}">
                        <c:if test="${isEditMode}">
                            <input type="hidden" name="id" value="${editing.id}">
                        </c:if>

                        <div class="field">
                            <label for="questionText">Question text</label>
                            <textarea id="questionText" name="questionText" required maxlength="1000"
                                      placeholder="Which keyword is used to create an object in Java?"><c:out value="${editing.questionText}" /></textarea>
                        </div>

                        <div class="field-row">
                            <div class="field">
                                <label for="optionA">Option A</label>
                                <input type="text" id="optionA" name="optionA" required maxlength="500"
                                       value="<c:out value='${editing.options[0].optionText}' />" placeholder="class">
                            </div>
                            <div class="field">
                                <label for="optionB">Option B</label>
                                <input type="text" id="optionB" name="optionB" required maxlength="500"
                                       value="<c:out value='${editing.options[1].optionText}' />" placeholder="new">
                            </div>
                        </div>

                        <div class="field-row">
                            <div class="field">
                                <label for="optionC">Option C</label>
                                <input type="text" id="optionC" name="optionC" required maxlength="500"
                                       value="<c:out value='${editing.options[2].optionText}' />" placeholder="object">
                            </div>
                            <div class="field">
                                <label for="optionD">Option D</label>
                                <input type="text" id="optionD" name="optionD" required maxlength="500"
                                       value="<c:out value='${editing.options[3].optionText}' />" placeholder="instance">
                            </div>
                        </div>

                        <div class="field">
                            <label>Correct option</label>
                            <input type="hidden" id="correctOption" name="correctOption"
                                   value="${isEditMode ? editing.correctOption : 'A'}">
                            <div class="flex">
                                <c:forEach var="key" items="A,B,C,D">
                                    <c:set var="selectedKey" value="${isEditMode ? editing.correctOption : 'A'}" />
                                    <button type="button"
                                            class="btn ${selectedKey eq key ? 'btn-primary' : 'btn-secondary'} option-pick"
                                            data-key="${key}"
                                            aria-pressed="${selectedKey eq key ? 'true' : 'false'}">
                                        ${key}
                                    </button>
                                </c:forEach>
                            </div>
                            <p class="hint">Click the letter of the correct answer. The student sees only the letters.</p>
                        </div>

                        <div class="field">
                            <label for="explanation">Explanation (optional)</label>
                            <textarea id="explanation" name="explanation" maxlength="1000"
                                      placeholder="Shown on the result page to explain the answer."><c:out value="${editing.explanation}" /></textarea>
                        </div>

                        <div class="flex">
                            <button class="btn btn-primary" type="submit">
                                ${isEditMode ? 'Save question' : 'Add question'}
                            </button>
                            <a class="btn btn-secondary" href="${ctx}/admin/questions?quizId=${quiz.id}">Cancel</a>
                        </div>
                    </form>
                </div>

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
