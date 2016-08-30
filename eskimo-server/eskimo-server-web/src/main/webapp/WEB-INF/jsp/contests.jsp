
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib uri="/WEB-INF/tld/functions" prefix="func" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Eskimo</title>
</head>

<body>

<eskimo:mainMenu/>

	<div class="container">
		<sec:authorize access="isAuthenticated() and hasRole('ROLE_ADMIN')">
		<div class="well">
<%--suppress JspAbsolutePathInspection --%>
			<a href="<c:url value="/contests/new"/>">New contest</a>
		</div>
		</sec:authorize>
		<table class="table table-striped table-bordered">
			<thead>
			<tr>
				<th>ID</th>
				<th>Name</th>
			</tr>
			</thead>
			<tbody>
			<c:forEach var="contest" items="${contests}">
				<tr>
					<td>${contest.id}</td>
					<td><a href="<c:url value="/contest/${contest.id}"/>">
						${func:getName(contest.names, currentLocale)}
					</a></td>
				</tr>
			</c:forEach>
			</tbody>
		</table>
	</div>

	<eskimo:footer/>
</body>
</html>