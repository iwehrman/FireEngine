<%@ page import="com.labfire.fe.*, com.labfire.fe.util.*, java.util.Iterator" contentType="text/html" %>
<%
FireEngineContext context = Servlets.getFireEngineContext(application);
FireEngineRepository repository;
FireEngineComponent service;
Iterator i,j;
out.println("Context: " + context.getVirtualRoot() + "<br />");
i = context.getRepositories().values().iterator();
out.println("<ul>");
while (i.hasNext()) {
	repository = (FireEngineRepository)i.next();
	out.println("<li>Repository: " + repository.getFilename());
	j = repository.getRegisteredServices().iterator();
	out.println("<ul>");
	while (j.hasNext()) {
		service = (FireEngineComponent)j.next();
		out.println("<li>" + service.getStatus() + "</li>");
	}
	out.println("</ul>");
	out.println("</li>");
}
out.println("</ul>");
%>
