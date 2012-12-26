<%@ page import="com.labfire.fe.auth.*" %>
<%@ page import="com.labfire.fe.cache.*" %>
<%@ page import="com.labfire.fe.util.*" %>
<%@ page import="java.util.*" %>
	<table border = "0">
		<tr>
			<th>Id</th>
			<th>Description</th>
			<th>Expires</th>
			<th><br /></th>
		<tr>
<%
Map cache = null;
AuthToken at = Servlets.getAuthToken(request);
long l;
try {
	if (at != null)
		cache = CacheService.select(at);
	else
		out.println("User not logged in.");
} catch (AuthException ae) {
	out.println("An AuthException has occurred.");
}
if (cache != null) {
	Set keySet = cache.keySet();
	Iterator keys = keySet.iterator();
	Object k = null;
	while(keys.hasNext()) {
		k = keys.next();
		Cacheable o = (Cacheable)cache.get(k);
%>
		<tr>
			<td><%=o.getIdentifier().toString()%></td>
			<td><%=Strings.replaceString(Strings.replaceString(o.toString().substring(0,Math.min(50,o.toString().length())), "<", "&lt;"), ">", "&gt;")%><br /></td>
			<td><% if (o instanceof CachedObject) { if (((CachedObject)o).getExpiration() == null) { out.println("<i>Never</i>"); } else { out.print(((CachedObject)o).getExpiration()); }} else { out.print("Unknown"); }%></td>
			<td><a href = "javascript:cache_del('<%=o.getIdentifier()%>');">Delete</a></td>
		</tr>
<%
	}
}
%>
	</table></p>