<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:forEach items="${component.ctaLinks}" var="item">
  <c:if test="${item.renderType.code eq 'LINK'}">
    <c:set var="ctaLink" value="${item}"/>
  </c:if>
  <c:if test="${item.renderType.code eq 'BUTTON'}">
    <c:set var="ctaButton" value="${item}"/>
  </c:if>
</c:forEach>

<div class="information-box-component panel panel-default">
  <div class="panel-heading">
    <c:if test="${not empty component.titleIconDesktop.url}">
    <img src="${component.titleIconDesktop.url}" alt="${component.titleIconDesktop.altText}" role="presentation">
    </c:if>
    ${component.desktopTitle}
    <c:if test="${not empty ctaLink}">
      <a href="${ctaLink.url}">${ctaLink.name}</a>
    </c:if>
  </div>
  <ul class="list-group">
    <c:forEach items="${component.textList}" var="item">
      <li class="list-group-item">
        <c:if test="${not empty item.media.url}">
        <img src="${item.media.url}" alt="${item.media.altText}" role="presentation">
        </c:if>
        <c:if test="${not empty item.name}">
        <h4 class="list-group-item-heading">${item.name}</h4>
        </c:if>
        <div>${item.sabmSubText}</div>
      </li>
    </c:forEach>
  </ul>
  <c:if test="${not empty ctaButton}">
    <a href="${ctaButton.url}" role="button" class="btn btn-primary">${ctaButton.name}</a>
  </c:if>
</div>
