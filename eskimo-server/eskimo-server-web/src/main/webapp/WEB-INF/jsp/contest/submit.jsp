<%--suppress ELValidationInJSP --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
	<eskimo:globalHead/>
	<title>Submit</title>
</head>

<body>
<eskimo:contestMenu/>


<c:url value="/contest/${contest.id}/submit" var="submitUrl"/>

<div class="container">
	<form:form method="POST" action="${submitUrl}" modelAttribute="submissionForm">
		<form:select path="problemId">
			<form:option value=""/>
			<c:forEach var="problem" items="${contest.problems}">
				<form:option value="${problem.id}" label="${problem.getName(pageContext.response.locale)}"/>
			</c:forEach>
		</form:select>
		<form:textarea path="sourceCode" cols="30" rows="20"/>
		<button type="submit">Submit</button>
	</form:form>
</div>
<eskimo:footer/>
</body>
</html>