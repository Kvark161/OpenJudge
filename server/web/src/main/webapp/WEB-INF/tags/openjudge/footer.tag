<%@ tag body-content="empty" pageEncoding="UTF-8" %>
<hr>
<div style="text-align: center">
	<%
		out.print("Server time: ");
		java.text.DateFormat dateFormat = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss zzz");
		out.println(dateFormat.format(new java.util.Date()));
	%>
</div>