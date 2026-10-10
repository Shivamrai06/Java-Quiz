<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Profile" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap stack">

        <div class="page-head">
            <div>
                <h1 class="mb-1">Your profile</h1>
                <p class="sub">Account details and performance summary.</p>
            </div>
            <a class="btn btn-secondary" href="${ctx}/dashboard">Back to dashboard</a>
        </div>

        <div class="grid grid-2">

            <div class="card">
                <h2 class="mb-2">Account</h2>

                <div class="flex mb-2">
                    <span class="avatar" style="width:52px;height:52px;font-size:18px;">
                        <c:out value="${user.initials}" />
                    </span>
                    <div>
                        <div class="cell-strong" style="font-size:17px;"><c:out value="${user.name}" /></div>
                        <div class="small muted"><c:out value="${user.email}" /></div>
                    </div>
                </div>

                <hr class="hr">

                <table class="data">
                    <tbody>
                    <tr>
                        <td class="cell-muted">Role</td>
                        <td class="text-right"><span class="badge accent">${user.role}</span></td>
                    </tr>
                    <tr>
                        <td class="cell-muted">Registered on</td>
                        <td class="text-right">
                            <fmt:formatDate value="${user.createdAt}" pattern="dd MMM yyyy" />
                        </td>
                    </tr>
                    <tr>
                        <td class="cell-muted">Password</td>
                        <td class="text-right small muted">Stored as a BCrypt hash</td>
                    </tr>
                    </tbody>
                </table>

                <p class="xsmall muted" style="margin-top:14px;margin-bottom:0;">
                    Account editing is out of scope for this version &mdash; contact your administrator
                    to change your details.
                </p>
            </div>

            <div class="card">
                <h2 class="mb-2">Performance</h2>

                <div class="grid grid-3 mb-2">
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Attempts</div>
                        <div class="value" style="font-size:24px;">${summary.attempted}</div>
                    </div>
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Average</div>
                        <div class="value" style="font-size:24px;">${summary.average}%</div>
                    </div>
                    <div class="stat" style="border:0;padding:0;">
                        <div class="label">Best</div>
                        <div class="value" style="font-size:24px;">${summary.best}%</div>
                    </div>
                </div>

                <c:choose>
                    <c:when test="${empty attempts}">
                        <p class="small muted mb-0">You have not attempted any quizzes yet.</p>
                    </c:when>
                    <c:otherwise>
                        <h3 style="margin-top:20px;">Recent attempts</h3>
                        <ul class="inline-list" style="flex-direction:column;gap:10px;">
                            <c:forEach var="attempt" items="${attempts}" varStatus="loop">
                                <c:if test="${loop.index lt 5}">
                                    <li class="flex-between" style="width:100%;border-bottom:1px solid var(--border);padding-bottom:9px;">
                                        <span class="small"><c:out value="${attempt.quizTitle}" /></span>
                                        <span class="badge ${attempt.gradeBand}">
                                            ${attempt.score}/${attempt.totalQuestions}
                                        </span>
                                    </li>
                                </c:if>
                            </c:forEach>
                        </ul>
                    </c:otherwise>
                </c:choose>
            </div>

        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
