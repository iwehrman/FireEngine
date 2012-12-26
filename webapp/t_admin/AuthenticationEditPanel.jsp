<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
boolean isNew = false;
AuthToken at = Servlets.getAuthToken(request);
TransientUser u = (TransientUser)request.getAttribute((TransientUserFactory.class).getName());
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
%>
	Edit User
	<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.AuthenticationEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">UserName</td>
				<td align = "left"><input name = "userName" type = "text" value = "<%=u.getUserName()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">UserPass</td>
				<td align = "left"><input name = "userPass" type = "password"<% if (u.getUserPass() != null) {%> value = "<%=u.getUserPass()%>"<%}%>></input></td>
			</tr>
			<tr>
				<td align = "right">(Again)</td>
				<td align = "left"><input name = "userPass2" type = "password"<% if (u.getUserPass() != null) {%> value = "<%=u.getUserPass()%>"<%}%>></input></td>
			</tr>
			<tr>
				<td align = "right">Email</td>
				<td align = "left"><input name = "userEmail" type = "text" value = "<%=u.getUserEmail()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">First Name</td>
				<td align = "left"><input name = "userFirstName" type = "text" value = "<%=u.getUserFirstName()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">Last Name</td>
				<td align = "left"><input name = "userLastName" type = "text" value = "<%=u.getUserLastName()%>"></input></td>
			</tr>
			<tr>
				<td align = "right">Organizations</td>
				<td align = "left">
					<select name = "userOrgs" multiple = "multiple" size = "10">
<%
try {
	ResultSet ors = OrgAuthenticationService.select(at);
	if (ors != null) {
		while(ors.next()) {
%>
						<option value = "<%=ors.getInt("OrgID")%>" <% if (u.getUserOIDs().contains(new Integer(ors.getInt("OrgID")))) { out.print("selected = \"selected\""); } %>><%=ors.getString("OrgName")%></option>
<%
		}
		ors.close();
	}
} catch (AuthException ae) {
	out.println("An AuthException has occurred.");
}
%>
					</select>
				</td>
			</tr>
			<tr>
				<td align = "right">Type</td>
				<td align = "left">
					<select name = "userAccess">
<%
for (int i = 1; i <= 3; i++) {
%>
						<option value = "<%=i%>" <% if (i == u.getUserAID()) { out.print("selected = \"selected\""); } %>><%=AccessAuthenticationService.getAIDName(i)%></option>
<%
}
%>
					</select>
				</td>
			</tr>
			<tr>
				<td align = "right">Login Allowed</td>
				<td align = "left">
					<select name = "userLoginAllowed">
						<option value = "true" <% if (u.isUserLoginAllowed()) { out.print("selected = \"selected\""); } %>>Yes</option>
						<option value = "false" <% if (!u.isUserLoginAllowed()) { out.print("selected = \"selected\""); } %>>No</option>
					</select>
				</td>
			</tr>
		</table>
		<input type = "hidden" name = "userLastLoggedIn" value = "<%=u.getUserLastLoggedIn().getTime()%>"></input>
		<input type = "hidden" name = "userID"  value = "<%=u.getUserID()%>"></input>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_authentication_panel"></input>
		<input type = "hidden" name = "_required" value = "userLoginAllowed, userAccess, userName, userEmail, userFirstName, userLastName"></input>
		<input type = "submit" value = "Update User">
	</form>
