<%@attribute name="path" required="true" description="Path to property for data binding (form:input tag)" %>
<%@attribute name="cssClass" required="false" description="css class for form:input tag" %>
<%@attribute name="id" description="id attribute for form:input tag"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<c:url value="/resources/components/datetimepicker/js/moment.min.js" var="momentJsUrl" />
<script type="text/javascript" src="${momentJsUrl}"></script>
<c:url value="/resources/components/datetimepicker/js/bootstrap-datetimepicker.min.js" var="dateTimePickerJsUrl" />
<script type="text/javascript" src="${dateTimePickerJsUrl}"></script>
<c:url value="/resources/components/datetimepicker/css/bootstrap-datetimepicker.min.css" var="dateTimePickerCssUrl" />
<link rel="stylesheet" href="${dateTimePickerCssUrl}" />

<c:set var="id" value="${(empty id) ? 'timepickerDefaultId' : id}" />

<form:input type="text" path="${path}" id="${id}" class="${cssClass}"/>

<script type="text/javascript">
	$(function () {
		$("#${id}").datetimepicker({
			format: "DD-MM-YYYY HH:mm"
		});
	});
</script>