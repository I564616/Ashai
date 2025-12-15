<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>


<b style="color:red">five_columns_sabmtextimagebannercomponent.jsp</b> </br>
<div class="text-image-banner-component row">
<c:forEach items="${component.textAndImages}" var="item">
    <div class="col-md-4 media">
      <div class="media-left">
        <img src="${item.desktopImage.url}" class="media-object" alt="${item.desktopImage.altText}">
      </div>
      <div class="media-body">
        <p class="media-heading h3">${item.textTitle}</p>
        <p>${item.textDescription}</p>
      </div>
    </div>
</c:forEach>
</div>