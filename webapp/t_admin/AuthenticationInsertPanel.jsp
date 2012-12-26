<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = vRoot + servletName + '?' + param + '=';
AuthToken at = Servlets.getAuthToken(request);
%>

	Add User
	<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.AuthenticationEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">UserName</td>
				<td align = "left"><input name = "userName" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">Password</td>
				<td align = "left"><input name = "userPass" type = "password"></input></td>
			</tr>
			<tr>
				<td align = "right">(Again)</td>
				<td align = "left"><input name = "userPass2" type = "password"></input></td>
			</tr>
			<tr>
				<td align = "right">Email</td>
				<td align = "left"><input name = "userEmail" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">First Name</td>
				<td align = "left"><input name = "userFirstName" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">Last Name</td>
				<td align = "left"><input name = "userLastName" type = "text"></input></td>
			</tr>
			<tr>
				<td align = "right">Organizations</td>
				<td align = "left">
					<select name = "userOrgs" multiple = "multiple" size = "10">
<%
	ResultSet ors = OrgAuthenticationService.select(at);
	if (ors != null) {
		while(ors.next()) {
%>
						<option value = "<%=ors.getInt("OrgID")%>"><%=ors.getString("OrgName")%></option>
<%
		}
		ors.close();
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
						<option value = "<%=i%>"><%=AccessAuthenticationService.getAIDName(i)%></option>
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
						<option value = "true">Yes</option>
						<option value = "false">No</option>
					</select>
				</td>
			</tr>
			<tr>
				<td align="right"><br /></td>
				<td align="left">
					<em>If both password fields are left blank, the 
					system will automatically create one, and notify 
					the new user via email.</em>
				</td>
			</tr>
		</table>
		<input type = "hidden" name = "userID"  value = "new"></input>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_authentication_panel"></input>
		<input type = "submit" value = "Add User">
	</form>
