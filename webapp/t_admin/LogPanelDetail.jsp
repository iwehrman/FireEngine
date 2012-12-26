<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.log.*, com.labfire.fe.util.*, com.labfire.fe.*" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<style type="text/css">
	td { border-bottom: 1px solid grey; border-left: 1px dotted grey; }
	th { border-bottom: 1px solid grey; }
</style>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
int lid = 0;

try {
	if (request.getParameter("lid") != null)
		lid = Integer.parseInt(request.getParameter("lid"));
} catch (NumberFormatException nfe) {

}

%>
	FireEngine Log<br />

<%
ResultSet rs = LogService.select(Servlets.getAuthToken(request), lid);


if (rs != null) {
	while (rs.next()) {
%>
	<table style="border: 1px solid black;" cellspacing="0">
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right">Id: </td><td><a href = "<%=pfix%>admin_log_panel_detail&lid=<%=rs.getInt("LogID")%>"><%=rs.getInt("LogID")%></a></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right">Level: </th><td style="border-bottom: 1px solid grey;"><%=rs.getString("LogLevel")%></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right" >Class: </th><td><%=rs.getString("LogClass")%></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right">Method: </th><td><%=rs.getString("LogMethod")%></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right">Message: </th><td><code><%=rs.getString("LogMessage")%></code></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right">Thrown: </th><td><pre><%=rs.getString("LogThrown")%></pre></td>
		</tr>
		<tr class = "<%=rs.getString("LogLevel")%>">
			<th align="right" style="border-bottom: 0px solid grey;">Time: </th><td nowrap="nowrap" style="border-bottom: 0px solid grey;"><%=new Timestamp(rs.getLong("LogMillis"))%></td>
		</tr>
	</table>
<%
	}
	rs.close();
}
%>
</p>
</body>
</html>
