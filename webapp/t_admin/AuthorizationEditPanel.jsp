<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="com.labfire.fe.template.*" %>
<%
AuthToken at = Servlets.getAuthToken(request);
FireEngineContext context = Servlets.getFireEngineContext(application);
String param = com.labfire.fe.template.TemplateService.getRequestParameter();
String servletName = TemplateService.getServletName();
String pfix = context.getVirtualRoot() + servletName + '?' + param + '=';
RequestAuth ra = (RequestAuth)request.getAttribute((RequestAuthFactory.class).getName());
Set constraints = ra.getConstraints();
%>
	Edit Constraint<br />
	Request: <%=ra.getURI()%><br />
	Constraints:<br />
		<table style="border: 1px solid black;">
			<tr>
				<th>User</th>
				<th>Org</th>
				<th>Access</th>
				<th>Delete</th>
			</tr>
<%
Constraint c;
Iterator i = constraints.iterator();
while (i.hasNext()) {
	c = (Constraint)i.next();
%>
			<tr>
				<td><%
					if (c.getID() != null) {
						out.print(AuthenticationService.getTransientUser(at, c.getID().intValue()).getUserName());
					} else {
						out.print("<br />");
					}
				%></td>
				<td><%
					if (c.getOID() != null) {
						out.println(OrgAuthenticationService.getOrganization(c.getOID().intValue()).getOrgName());
					} else {
						out.print("<br />");
					}
				%></td>
				<td><%
					if (c.getAID() != null) {
						out.println(AccessAuthenticationService.getAIDName(c.getAID().intValue()));
					} else {
						out.println("<br />");
					}
				%></td>
				<td><a href="servlet/com.labfire.fe.auth.ConstraintDeleteServlet?ConstraintID=<%=c.getConstraintID()%>&RequestURI=<%=ra.getURI()%>">Delete</a></td>
			</tr>
<%
}
%>
		</table>
		<a href="<%=pfix%>admin_constraint_insert_panel&com.labfire.fe.auth.RequestAuthFactory=<%=ra.getURI()%>">Additional Constraint</a>
