<%--suppress ELValidationInJSP --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<html>
<head>
	<eskimo:globalHead/>
	<c:url value="/resources/css/submit-style.css" var="customSubmitStyle" />
	<link href="${customSubmitStyle}" rel="stylesheet" />

	<title>Submit</title>
</head>

<body>
<eskimo:contestMenu/>

<div class="container">
	<form method="post" action="/contest/${contest.id}/submit?${_csrf.parameterName}=${_csrf.token}" enctype="multipart/form-data">
		<table class="submit-form">
			<tr>
			<td>
				<label for="problemId"> Choose problem: </label>
			</td>
			<td>
				<select name="problemId" id="problemId">
					<option></option>
					<c:forEach var="problem" items="${contest.problems}">
						<option value="${problem.id}">${problem.getName(pageContext.response.locale)}</option>
					</c:forEach>
				</select>
			</td>
			</tr>
			<tr>
			<td>
				<label for="sourceCode">Source code:</label>
			</td>
			<td>
				<textarea name="sourceCode" id="sourceCode" rows="20" style="width: 500px" ></textarea>
			</td>
			</tr>
			<tr>
			<td colspan="2" style="text-align: center">
				<button type="submit">Submit</button>
			</td>
			</tr>
		</table>
	</form>
</div>
<eskimo:footer/>
</body>
</html>