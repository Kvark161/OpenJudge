<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Problems</title>
</head>
<body>
<eskimo:contestMenu/>
<div class="container">
<div class="well">
	<a href="<c:url value="/contest/${contest.id}/statements"/>" target="_blank">Statements</a>
</div>
<table class="table table-striped table-bordered">
	<thead>
	<tr>
		<th class="col-xs-2">Index</th>
		<th>Name</th>
	</tr>
	</thead>
	<tbody>
	<c:forEach items="${problems}" var="problem">
		<tr>
			<td>${problem.index}</td>
			<td>${problem.name}</td>
		</tr>
	</c:forEach>
	</tbody>
</table>
</div>
<eskimo:footer/>
</body>
</html>