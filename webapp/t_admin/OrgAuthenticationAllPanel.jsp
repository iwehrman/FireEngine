<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
%>
	FireEngine Organizations<br />
	<form method="get" action="<%=context.getVirtualRoot()%>FireEngine">
		<input type="hidden" name="<%=param%>" value="admin_orgauthentication_edit_panel"></input>
		<input type="text" size="10" name="<%=OrganizationFactory.class.getName()%>"></input>
		<input type="submit" value="Go to ShortName"></input>
	</form>
	<table cellspacing="0">
		<tr>
			<th>Full Name</th>
			<th>ShortName</th>
			<th>Website</th>
			<th>Default Type</th>
			<th>edit</th>
			<th>delete</th>
		</tr>
<%
ResultSet rs = null;
AuthToken at = Servlets.getAuthToken(request);
try {
	if (at != null)
		rs = OrgAuthenticationService.select(at);
	else
		out.println("User not logged in.");
} catch (AuthException ae) {
	out.println("An AuthException has occurred.");
}
if (rs != null) {
	while (rs.next()) {
%>
		<tr>
			<td><%=rs.getString("OrgName")%></td>
			<td><code><%=rs.getString("OrgShortName")%></code></td>
			<td><a href = "<%=rs.getString("OrgURL")%>"><%=rs.getString("OrgURL")%></a></td>
			<td><%=AccessAuthenticationService.getAIDName(rs.getInt("OrgDefaultAID"))%>
			<td><a href = "<%=pfix%>admin_orgauthentication_edit_panel&com.labfire.fe.common.OrganizationFactory=<%=rs.getInt("OrgID")%>">edit</a></td>
			<td><a href = "javascript:org_del(<%=rs.getInt("OrgID")%>,'<%=rs.getString("OrgName")%>');">delete</a></td>
		</tr>
<%
	}
	rs.close();
}
%>
	</table><a href = "<%=pfix%>admin_orgauthentication_insert_panel">Add New Organization</a></p>
