<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.db.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.util.*" %>
	FireEngine ConnectionPools<br />
	<table border = "0">
		<tr>
			<th rowspan = "2">URL</th>
			<th rowspan = "2">UserName</th>
			<th colspan = "4">Connections</th>
			<th rowspan = "2">Wait If Busy</th>
			<th colspan = "3">Constraint</th>
		</tr>
		<tr>
			<th>Initial</th>
			<th>Max</th>
			<th>Available</th>
			<th>Busy</th>
			<th>Count</th>
			<th>User</th>
			<th>Org</th>
			<th>Access</th>
		<tr>
<%
AuthToken at = Servlets.getAuthToken(request);
Map constraints = ConnectionService.select(at);
if (constraints != null) {
	Set keySet = constraints.keySet();
	Iterator keys = keySet.iterator();
	while(keys.hasNext()) {
		ConnectionAuth ca = (ConnectionAuth)constraints.get(keys.next());
		Constraint c = ca.getConstraint();
%>
		<tr>
			<td><%=ca.getPool().getURL()%></td>
			<td><%=ca.getPool().getUserName()%></td>
			<td><%=ca.getPool().getInitialConnections()%></td>
			<td><%=ca.getPool().getMaxConnections()%></td>
			<td><%=ca.getPool().getAvailableConnections()%></td>
			<td><%=ca.getPool().getBusyConnections()%></td>
			<td><%=ca.getPool().getWaitIfBusy()%></td>
			<td><%=ca.getPool().getCount()%></td>
			<td><% if (c.getID() != null) { out.print(AuthenticationService.getTransientUser(at, c.getID().intValue()).getUserName()); } else { out.println("<i>None</i>"); }%></td>
			<td><% if (c.getOID() != null) { out.print(OrgAuthenticationService.getOrganization(c.getOID().intValue()).getOrgName()); } else { out.println("<i>None</i>"); }%></td>
			<td><% if (c.getAID() != null) { out.print(c.getAID()); } else { out.println("<i>None</i>"); }%></td>
		</tr>
<%
	}
}
%>
	</table></p>