<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="eskimo" tagdir="/WEB-INF/tags/eskimo" %>
<%@ taglib prefix="eskimoComp" tagdir="/WEB-INF/tags/eskimo/components" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<html>
<head>
	<eskimo:globalHead/>

	<title>Edit</title>
</head>

<body>
<eskimo:contestMenu/>

<c:url value="/contest/${contest.id}/edit" var="editUrl"/>

<div class="container">

	<form:form method="POST" action="${editUrl}"  modelAttribute="editContestForm">
		<div class="row form-group">
			<div class="col-xs-2 control-label">Name:</div>
			<form:label path="name"/>
			<div class="col-xs-4">
				<form:input path="name" id="name" class="form-control"/>
			</div>
			<div class="has-error">
				<form:errors path="name" class="help-block"/>
			</div>
		</div>
		<div class="row form-group">
			<div class="col-xs-2 control-label">Start time:</div>
			<form:label path="startTime"/>
			<div class="col-xs-4">
				<eskimoComp:dateTimePicker path="startTime" cssClass="form-control"/>
			</div>
			<div class="col-xs-4 has-error">
				<form:errors path="startTime" class="help-block"/>
			</div>
		</div>
		<div class="row form-group">
			<div class="col-xs-2 control-label">Duration:</div>
			<form:label path="duration"/>
			<div class="col-xs-4">
				<form:input type="number" path="duration" id="duration" class="form-control"
				            step="1" min="1"/>
			</div>
			<div class="col-xs-4 has-error">
				<form:errors path="duration" class="help-block"/>
			</div>
		</div>

		<div class="form-group">
			<div class="col-xs-3"></div>
			<button type="reset">Cancel</button>
			<button type="submit">Submit</button>
		</div>
	</form:form>
</div>

<eskimo:footer/>
</body>
</html>