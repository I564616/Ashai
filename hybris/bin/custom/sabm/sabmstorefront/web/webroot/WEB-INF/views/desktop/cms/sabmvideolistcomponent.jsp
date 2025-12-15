<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="frameVisible" value="${component.frameVisible}" />

<c:if test="${empty frameVisible}">
	<c:set var="frameVisible" value="true" />
</c:if>

<div id="videoListComponent" data-frame-visible="${frameVisible}" class="<c:if test="${component.videoList.size() eq 2}">two-column</c:if>">	
	<c:if test="${not empty item.videoListTitle}">	     
		<header class="h1 title text-center">${component.videoListTitle}</header>
	</c:if>
	
	 <c:forEach items="${component.videoList}" var="item" >   
		<div class="videoContainer">
			<h3 class="text-center">${item.videoTitle}</h3>
   	     	<div class="videoEmbedded"> 		     
			     ${item.videoEmbeddedCode}
	     	</div>
		     <p class="lead">${item.videoDescription}</p>
	     </div>
	 </c:forEach>
</div>

