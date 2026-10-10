<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="pageTitle" value="${mode eq 'create' ? 'Create quiz' : 'Edit quiz'}" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>
<c:set var="isEdit" value="${mode eq 'edit'}" />

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div>
                    <p class="small"><a href="${ctx}/admin/quizzes">&larr; Back to quizzes</a></p>
                    <h1 class="mb-1">${isEdit ? 'Edit quiz' : 'Create quiz'}</h1>
                    <p class="sub muted">
                        ${isEdit ? 'Update the quiz details or change its publication status.'
                                 : 'A new quiz starts as a draft so you can add its questions first.'}
                    </p>
                </div>

                <c:if test="${not empty errorMessage}">
                    <div class="alert error" role="alert"><c:out value="${errorMessage}" /></div>
                </c:if>

                <div class="card">
                    <form method="post" action="${ctx}/admin/quiz/${isEdit ? 'edit' : 'create'}" data-lock-on-submit>
                        <c:if test="${isEdit}">
                            <input type="hidden" name="id" value="${quiz.id}">
                        </c:if>

                        <div class="field ${errorField eq 'title' ? 'has-error' : ''}">
                            <label for="title">Quiz title</label>
                            <input type="text" id="title" name="title" required maxlength="150"
                                   value="<c:out value='${quiz.title}' />"
                                   placeholder="Java Fundamentals">
                            <p class="hint">Shown to students on the quiz list and dashboard.</p>
                        </div>

                        <div class="field">
                            <label for="description">Description</label>
                            <textarea id="description" name="description" maxlength="2000"
                                      placeholder="A quick check of core Java: data types, operators and strings."><c:out value="${quiz.description}" /></textarea>
                        </div>

                        <div class="field-row">
                            <div class="field ${errorField eq 'category' ? 'has-error' : ''}">
                                <label for="category">Category</label>
                                <input type="text" id="category" name="category" required maxlength="100"
                                       value="<c:out value='${quiz.category}' />"
                                       placeholder="Core Java">
                            </div>

                            <div class="field ${errorField eq 'durationMin' ? 'has-error' : ''}">
                                <label for="durationMin">Duration (minutes)</label>
                                <input type="number" id="durationMin" name="durationMin" min="1" max="600"
                                       value="${quiz.durationMin}" placeholder="Leave empty for no limit">
                                <p class="hint">Optional. Between 1 and 600 minutes.</p>
                            </div>

                            <div class="field">
                                <label for="status">Status</label>
                                <select id="status" name="status">
                                    <option value="DRAFT" ${quiz.status eq 'DRAFT' ? 'selected' : ''}>Draft (hidden from students)</option>
                                    <option value="PUBLISHED" ${quiz.status eq 'PUBLISHED' ? 'selected' : ''}>Published (visible to students)</option>
                                </select>
                                <p class="hint">Publishing requires at least one question.</p>
                            </div>
                        </div>

                        <hr class="hr">

                        <div class="flex">
                            <button class="btn btn-primary" type="submit">
                                ${isEdit ? 'Save changes' : 'Create quiz'}
                            </button>
                            <a class="btn btn-secondary" href="${ctx}/admin/quizzes">Cancel</a>
                            <c:if test="${isEdit}">
                                <a class="btn btn-ghost" href="${ctx}/admin/questions?quizId=${quiz.id}">
                                    Manage questions
                                </a>
                            </c:if>
                        </div>
                    </form>
                </div>

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
