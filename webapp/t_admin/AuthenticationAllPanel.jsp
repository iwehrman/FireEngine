<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
%>
<style type="text/css">
	td { border-top: 1px solid grey; padding-right: 5px; }
</style>
	FireEngine Users<br />
	<form method="get" action="<%=context.getVirtualRoot()%>FireEngine">
		<input type="hidden" name="<%=param%>" value="admin_authentication_edit_panel"></input>
		<input type="text" size="10" name="<%=TransientUserFactory.class.getName()%>"></input>
		<input type="submit" value="Go to UserName"></input>
	</form>
	<table cellspacing="0">
		<tr>
			<th>Full Name</th>
			<th>UserName</th>
			<th>Email</th>
			<th>Last Login</th>
			<th>Type</th>
			<th>edit</td>
			<th>delete</th>
		<tr>
<%
ResultSet rs = null;
boolean yep;
AuthToken at = Servlets.getAuthToken(request);
try {
	if (at != null)
		rs = AuthenticationService.select(at);
	else
		out.println("User not logged in.");
} catch (AuthException ae) {
	out.println("An AuthException has occurred.");
}
int lastUserId = -1;
if (rs != null) {
	while (rs.next()) {
%>
		<tr>
			<td><%=rs.getString("UserFirstName")%> <%=rs.getString("UserLastName")%></td>
			<td><code><%=rs.getString("UserName")%></code></td>
			<td><a href = "mailto:<%=rs.getString("UserEmail")%>"><%=rs.getString("UserEmail")%></a></td>
			<td><%=DateFormat.getInstance().format(new Date(rs.getTimestamp("UserLastLoggedIn").getTime()))%></td>
			<td><%=AccessAuthenticationService.getAIDName(rs.getInt("UserAccess"))%></td>
			<td><a href = "<%=pfix%>admin_authentication_edit_panel&com.labfire.fe.common.TransientUserFactory=<%=rs.getInt("UserID")%>">edit</a></td>
			<td><a href = "javascript:user_del(<%=rs.getInt("UserID")%>,'<%=rs.getString("UserName")%>');">delete</a></td>
		</tr>
<%
	}
	rs.close();
}
%>
	</table><a href = "<%=pfix%>admin_authentication_insert_panel">Add New User</a></p>
