<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
ResultSet rs = null;
AuthToken at = Servlets.getAuthToken(request);
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';

try {
	if (at != null)
		rs = AuthenticationService.select(at, AuthenticationService.findID(at));
	else
		out.println("User not logged in.");
		
		
	if (rs != null && rs.next()) {
%>User Information
<table>
	<tr>
		<td align = "right">Real name: </td><td align = "left"><%=rs.getString("UserFirstName")%> <%=rs.getString("UserLastName")%></td>
	</tr>
	<tr>
		<td align = "right">User name: </td><td align = "left"><%=rs.getString("UserName")%></td>
	</tr>
	<tr>
		<td align = "right">Email: </td><td align = "left"><%=rs.getString("UserEmail")%></td>
	</tr>
	<tr>
		<td align = "right">Access Level: </td><td align = "left"><%=AccessAuthenticationService.getAIDName(rs.getInt("UserAccess"))%></td>
	</tr>
	<tr>
		<td align = "right">Last Login: </td><td align = "left"><%=rs.getString("UserLastLoggedIn")%></td>
	</tr>
	<tr>
		<td align = "right">User agent: </td><td align = "left"><%=request.getHeader("USER-AGENT")%></td>
	</tr>
	<tr>
		<td align = "right">AuthToken: </td><td align = "left"><code><%=at.toString().toUpperCase()%></code></td>
	</tr>
	<tr>
		<td align = "right">Session: </td><td align = "left"><code><%=request.getRequestedSessionId()%></code></td>
	</tr>
	<tr>
		<td align = "right"><br /></td><td align = "left"><a href = "<%=pfix%>admin_authentication_edit_panel&com.labfire.fe.common.TransientUserFactory=<%=rs.getInt("UserID")%>">Edit User</a></td>
	</tr>
	<tr>
		<td align = "right"><br /></td><td align = "left"><a href = "<%=pfix%>logout">Logout</a></td>
	</tr>
	<tr>
		<td align = "right"><br /></td><td align = "left"><a href = "<%=pfix%>logout&purgeCookies=true">Logout and clear cookies</a></td>
	</tr>
</table>
<%
		rs.close();
	}
} catch (AuthenticationException ae) {
	out.println("An AuthenticationException has occurred.");
}
%>
</body></html>
