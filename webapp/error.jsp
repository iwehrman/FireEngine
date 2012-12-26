<%@ page import="com.labfire.fe.common.*" %>
<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.mail.*" %>
<%@ page import="com.labfire.fe.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="javax.servlet.*" %>
<% 
FireEngineContext context = (FireEngineContext)application.getAttribute("FireEngineContext");
String vRoot = context.getVirtualRoot();
%>
<h1>Error</h1><p />
<form action = "<%=vRoot%>servlet/com.labfire.fe.mail.MailServlet" method = "post">
<%
String subject = "Error: ";
String body = "Error: \n\n";
Throwable e = (Throwable)session.getAttribute("lastException");
boolean detail = false;
if (request.getParameter("detail") != null && request.getParameter("detail").equalsIgnoreCase("true"))
	detail = true;
if (e != null) {
	while (e != null) {
		subject += e.getMessage() + ": ";
		body += e.getMessage() + "\n";
		StackTraceElement ste[] = e.getStackTrace();
		for (int i = 0; i < ste.length; i++) {
			body += "- " + Strings.escapeHTML(ste[i].toString()) + "\n";
		}
		body += "\n";
		out.println("<p><b><pre>" + Strings.escapeHTML(e.toString()) + "</pre></b>");
		if (detail) {
			out.println("<pre>");
			StackTraceElement[] elements = e.getStackTrace();
			for (int i = 0; i < elements.length; i++) {
				out.println(Strings.escapeHTML(elements[i].toString()));
			}
			out.println("</pre>");
		}
		out.println("</p>");
		
		if (e instanceof ServletException) {
			e = ((ServletException)e).getRootCause();
		} else {
			e = e.getCause();
		}
	}
} else {
	out.println("An unknown error has occurred.");
}
%>
<input type = "hidden" name = "MailSubject" value = "<%=subject%>" />
<input type = "hidden" name = "MailBody" value = "<%=body%>" />
<input type = "hidden" name = "MailTo" value = "<%=context.getAdmin()%>" />
<% if (detail) { %>
<a href = "<%=Strings.addRequestParameter(vRoot + '.' + context.getErrorPage(), "detail", new Boolean(false))%>">Hide Details</a>
<% } else { %>
<a href = "<%=Strings.addRequestParameter(vRoot + '.' + context.getErrorPage(), "detail", new Boolean(true))%>">Show Details</a>
<% } %>
| <a href = "javascript:history.go(-1)">Back</a> | <a href = "javascript:document.forms[0].submit();">Email Administrator</a> | <a href = "/">Home</a>
