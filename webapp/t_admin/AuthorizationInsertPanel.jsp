<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*, com.labfire.fe.template.*" %>
<%@ page import="java.sql.*" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
String vRoot = context.getVirtualRoot();
String param = TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
%>
	Add Constraint
	<form action = "<%=vRoot%>servlet/com.labfire.fe.auth.AuthorizationEditServlet" method = "post">
		<table border = "0">
			<tr>
				<td align = "right">URI</td>
				<td align = "left"><input name = "RequestURI" type = "text" value="/"></input></td>
			</tr>
			<tr>
				<td align = "right">User</td>
				<td align = "left">
					<select name = "cID">
						<option value = ""></option>
<%
	ResultSet urs = AuthenticationService.select(Servlets.getAuthToken(request));
	if (urs != null) {
		while(urs.next()) {
%>
						<option value = "<%=urs.getInt("UserID")%>"><%=urs.getString("UserName")%></option>
<%
		}
		urs.close();
	}
%>
					</select>
				</td>
			</tr>
			<tr>
				<td align = "right">Organization</td>
				<td align = "left">
					<select name = "cOID">
						<option value = ""></option>
<%
	ResultSet ors = OrgAuthenticationService.select(Servlets.getAuthToken(request));
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
					<select name = "cAID">
						<option value = ""></option>
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
		</table>
		<input type = "hidden" name = "ConstraintID"  value = "new"></input>
		<input type = "hidden" name = "_redirect" value = "<%=pfix%>admin_authorization_panel"></input>
		<input type = "submit" value = "Add Constraint">
	</form>
