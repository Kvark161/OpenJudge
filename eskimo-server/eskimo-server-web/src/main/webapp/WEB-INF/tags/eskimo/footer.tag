<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:useBean id="currentTime" class="java.util.Date" scope="request"/>

<div class="container">
	<div class="row">
		<hr>
		<div class="col-lg-12">
			<div class="col-md-8">
				<a href="https://github.com/kvark161/eskimo">Eskimo</a>
			</div>
			<div class="col-md-4">
				<p class="muted pull-right">Server time: <fmt:formatDate type="time" value="${currentTime}"
				                                                         pattern="dd MMM yyyy HH:mm:ss"/></p>
			</div>
		</div>
	</div>
</div>
