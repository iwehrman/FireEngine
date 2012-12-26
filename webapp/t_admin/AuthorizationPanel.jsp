<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
%>
	FireEngine Request Constraints<br />
	<style type="text/css">
		td { border-top: 1px solid grey; padding-right: 5px; }
	</style>
	<table cellspacing = "0">
		<tr>
			<th>URI</th>
			<th>edit</th>
			<th>delete</th>
		</tr>
<%
ResultSet rs = AuthorizationService.selectRequests(Servlets.getAuthToken(request));
if (rs != null) {
	while (rs.next()) {
%>
		<tr>
			<td><%=rs.getString("RequestURI")%></td>
			<td><a href = "<%=pfix%>admin_authorization_edit_panel&com.labfire.fe.auth.RequestAuthFactory=<%=rs.getString("RequestURI")%>">edit</a></td>
			<td><a href = "servlet/com.labfire.fe.auth.AuthorizationDeleteServlet?RequestURI=<%=rs.getString("RequestURI")%>">delete</a></td>
		</tr>
<%
	}
	rs.close();
}
%>
	</table><a href = "<%=pfix%>admin_authorization_insert_panel">Add New Constraint</a></p>
