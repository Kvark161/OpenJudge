<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<hr>
<div class="footer">
    <jsp:useBean id="ololo" class="java.util.Date"/>
    Server time: <fmt:formatDate type="time" value="${ololo}" pattern="dd.MM.yyyy HH:mm:ss"/>
</div>