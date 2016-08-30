<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<%@ taglib uri="/WEB-INF/tld/functions" prefix="func" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Submissions</title>
</head>
<body>
<eskimo:contestMenu/>
<div class="container">
	<table class="table table-striped table-bordered">
		<thead>
		<tr>
			<th>ID</th>
			<th>Problem</th>
			<th>Verdict</th>
		</tr>
		</thead>
		<tbody>
		<c:forEach var="submission" items="${submissions}">
			<tr>
				<td>${submission.id}</td>
				<td>${func:getName(submission.problem.names, locale)}</td>
				<td>${submission.verdict}</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
</div>
<eskimo:footer/>
</body>
</html>
