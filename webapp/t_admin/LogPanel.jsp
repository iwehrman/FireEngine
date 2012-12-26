<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.log.*" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
int offset = 0;
int limit = 25;
DateFormat df = DateFormat.getInstance();

try {
	if (request.getParameter("offset") != null)
		offset = Integer.parseInt(request.getParameter("offset"));
	
	if (request.getParameter("limit") != null)
		limit = Integer.parseInt(request.getParameter("limit"));
} catch (NumberFormatException nfe) {
	limit = 25;
	offset = 0;
}

%>
<style type="text/css">
	td { border-top: 1px solid grey; border-right: 1px dotted grey; }
</style>
	FireEngine Log<br />
	<table style="border: 1px solid black;" cellspacing="0">
		<tr>
			<th>Id</th>
			<th>Level</th>
			<th>Message</th>
			<th>Time</th>
		<tr>
<%
ResultSet rs = null;
AuthToken at = Servlets.getAuthToken(request);
java.util.Date timer = new java.util.Date();
long l;
rs = LogService.select(at, limit, offset);

l = (new java.util.Date()).getTime() - timer.getTime();
if (rs != null) {
	while (rs.next()) {
%>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<td><a href = "<%=pfix%>admin_log_panel_detail&lid=<%=rs.getInt("LogID")%>"><%=rs.getInt("LogID")%></a></td>
			<td><%=rs.getString("LogLevel")%></td>
			<td><% if (rs.getString("LogMessage").length() > 75) { out.println(rs.getString("LogMessage").substring(0,75) + "..."); } else { out.println(rs.getString("LogMessage")); }%></td>
			<td style="border-right: 0px solid grey;" nowrap="nowrap"><%=df.format(new java.util.Date(rs.getLong("LogMillis")))%></td>
		</tr>
<%
	}
	rs.close();
}
%>
	</table></p>
	<a href = "<%=pfix + "admin_log_panel&offset=" + Math.max(0,(offset - limit)) + "&limit=" + limit%>">&lt; Previous</a> |
	<a href = "<%=pfix + "admin_log_panel&offset=" + (offset + limit) + "&limit=" + limit%>">Next &gt;</a><p /><font color = "grey">Time: <%=l%>ms.</font>
