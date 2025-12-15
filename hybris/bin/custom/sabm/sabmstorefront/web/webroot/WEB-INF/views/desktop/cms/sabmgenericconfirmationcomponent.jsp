<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:forEach items="${component.ctaLinks}" var="item">
  <c:if test="${item.name}">
    <c:set var="ctaText" value="${item}"/>
  </c:if>
  <c:if test="${item.renderType.code eq 'LINK'}">
    <c:set var="ctaLink" value="${item}"/>
  </c:if>
  <c:if test="${item.renderType.code eq 'BUTTON'}">
    <c:set var="ctaButton" value="${item}"/>
  </c:if>
</c:forEach>

<div class="confirmation-page-component">
  <div class="center-block text-center">
    <c:if test="${not empty component.desktopImage.url}">
    <p><img src="${component.desktopImage.url}" alt="${component.desktopImage.altText}" role="presentation" /></p>
    </c:if>
    <c:if test="${not empty component.textTitle}">
    <h2>${component.textTitle}</h2>
    </c:if>
    <c:if test="${not empty component.textDescription}">
    <p>${component.textDescription}</p>
    </c:if>
    <c:if test="${not empty ctaButton}">
    <p><a href="${ctaButton.url}" role="button" class="btn btn-primary">${ctaButton.name}</a></p>
    </c:if>
    <c:if test="${not empty ctaLink}">
    <p><a href="${ctaLink.url}" class="underline">${ctaLink.name}</a></p>
    </c:if>
  </div>
</div>