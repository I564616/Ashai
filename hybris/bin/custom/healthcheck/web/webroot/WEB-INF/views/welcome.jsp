<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>Welcome to healthcheck</title>
    <link rel="stylesheet" href="<c:url value="/static/healthcheck-webapp.css"/>" type="text/css"
          media="screen, projection"/>
</head>
<div class="container" style="text-align: center">
    <img src="<c:url value="${logoUrl}" />" alt="Hybris platform logo"/>
    <h2>Welcome to "healthcheck" Page</h2>
</div>
</html>