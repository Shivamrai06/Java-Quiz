<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<c:set var="pageTitle" value="Users" />
<%@ include file="/WEB-INF/views/common/head.jspf" %>

<main class="page">
    <div class="wrap">
        <div class="admin-layout">

            <%@ include file="/WEB-INF/views/admin/side.jspf" %>

            <div class="stack">

                <div>
                    <h1 class="mb-1">Registered users</h1>
                    <p class="sub muted">
                        Accounts that can sign in to the platform. Passwords are stored as BCrypt hashes and
                        are never shown here.
                    </p>
                </div>

                <div class="card card-tight">
                    <c:choose>
                        <c:when test="${empty users}">
                            <div class="empty">
                                <div class="icon">&#128100;</div>
                                <h3>No users found</h3>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="table-scroll">
                                <table class="data">
                                    <thead>
                                    <tr>
                                        <th>Name</th>
                                        <th>Email</th>
                                        <th>Role</th>
                                        <th>Registered</th>
                                        <th>Attempts</th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach var="user" items="${users}">
                                        <tr>
                                            <td>
                                                <div class="flex">
                                                    <span class="avatar"><c:out value="${user.initials}" /></span>
                                                    <span class="cell-strong"><c:out value="${user.name}" /></span>
                                                </div>
                                            </td>
                                            <td class="cell-muted"><c:out value="${user.email}" /></td>
                                            <td>
                                                <span class="badge ${user.admin ? 'accent' : ''}">${user.role}</span>
                                            </td>
                                            <td class="cell-muted nowrap">
                                                <fmt:formatDate value="${user.createdAt}" pattern="dd MMM yyyy" />
                                            </td>
                                            <td>${user.attemptCount}</td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>

                <div class="card">
                    <h2 class="mb-2">Password storage</h2>
                    <p class="small muted mb-0">
                        The password column contains a BCrypt hash such as
                        <code>$2a$10$&hellip;</code> &mdash; a salted, one-way value. Even an administrator
                        cannot read a user's password from the database.
                    </p>
                </div>

            </div>
        </div>
    </div>
</main>

<%@ include file="/WEB-INF/views/common/foot.jspf" %>
