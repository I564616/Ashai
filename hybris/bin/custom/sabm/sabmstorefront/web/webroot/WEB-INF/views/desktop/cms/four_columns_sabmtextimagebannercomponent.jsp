<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<div class="image-text-card-component panel panel-default row">
  <div class="col-xs-12">
    <h2 class="h1">${component.textTitle}</h2>
  </div>
  <div class="row">
  <c:forEach items="${component.textAndImages}" var="item">
    <div class="card col-xs-12 col-sm-6 col-md-3">
      <div class="col-xs-4 col-sm-12">
        <div class="panel-heading">
          <img src="${item.desktopImage.url}" alt="${item.desktopImage.altText}">
        </div>
      </div>
      <div class="col-xs-8 col-sm-12">
        <div class="panel-body">
          <p class="caption">${item.imageTitle}</p>
          <h3>${item.textTitle}</h3>
          <p class="description">${item.textDescription}</p>
        </div>
      </div>
    </div>
  </c:forEach>
  </div>
</div>
