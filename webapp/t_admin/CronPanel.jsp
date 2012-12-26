<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.cron.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.util.*, java.text.*" %>
<style type="text/css">
	td { border-top: 1px solid grey; padding-right: 5px; }
</style>
	<table cellspacing = "0">
		<tr>
			<th>Description</th>
			<th>Start</th>
			<th>Stop</th>
			<th>Repeat</th>
		<tr>
<%
DateFormat df = DateFormat.getInstance();
AuthToken at = Servlets.getAuthToken(request);
List cron = CronService.select(at);
if (cron != null) {
	Iterator itr = cron.iterator();
	while(itr.hasNext()) {
		Chronological c = (Chronological)itr.next();
%>
		<tr>
			<td><%=c.toString()%></td>
			<td><% if (c instanceof SimpleCronEntry) { out.print(df.format(((SimpleCronEntry)c).getStartDate())); } else { out.print("Unknown"); }%></td>
			<td><% if (c instanceof SimpleCronEntry) { out.print(df.format(((SimpleCronEntry)c).getStopDate())); } else { out.print("Unknown"); }%></td>
			<td><% if (c instanceof SimpleCronEntry) { out.print(((SimpleCronEntry)c).getRepeatName()); } else { out.print("Unknown"); }%></td>
		</tr>
<%
	}
}
%>
	</table></p>
	<!-- a href = "javascript:cron_del();">Clear cron entries</a-->
