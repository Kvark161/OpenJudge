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
		<div class="row">
			<div class="col-xs-2"><form:label path="problemId">Choose problem:</form:label></div>
			<div class="col-xs-10">
				<form:select id="problemId" path="problemId">
					<form:option value=""/>
					<c:forEach var="problem" items="${contest.problems}">
						<form:option value="${problem.id}" label="${problem.getName(pageContext.response.locale)}"/>
					</c:forEach>
				</form:select>
				<div class="has-error">
					<form:errors path="problemId" class="help-block"/>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-2"><form:label path="problemId">Source code:</form:label></div>
			<div class="col-xs-10">
				<form:textarea path="sourceCode" cols="30" rows="20"/>
				<div class="has-error">
					<form:errors path="sourceCode" class="help-block"/>
				</div>
			</div>
		</div>
		<div class="row">
			<div class="col-xs-3"></div>
			<div class="col-xs-9">
				<button type="submit">Submit</button>
			</div>
		</div>
		<c:if test="${success}">
			<div class="row">
				<div class="col-xs-3"></div>
				<div class="col-xs-9">
					<div class="alert alert-success">
						Submitted successfully
					</div>
				</div>
			</div>
		</c:if>
	</form:form>
</div>
<eskimo:footer/>
</body>
</html>