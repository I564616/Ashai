<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:forEach items="${component.ctaLinks}" var="item">
    <c:if test="${item.renderType.code eq 'LINK'}">
        <c:set var="ctaLink" value="${item}"/>
        <c:set var="totalLinks" value="${fn:length(component.ctaLinks)}" />
    </c:if>
    <c:if test="${item.renderType.code eq 'BUTTON'}">
        <c:set var="ctaButton" value="${item}"/>
    </c:if>
</c:forEach>

<div class="information-smallbox-component panel panel-default">
    <div class="panel-heading">
        <c:if test="${not empty component.titleIconDesktop.url}">
        <img src="${component.titleIconDesktop.url}" alt="${component.titleIconDesktop.altText}" role="presentation">
        </c:if>
        ${component.desktopTitle}
    </div>
    <div class="panel-body<c:if test="${not empty ctaButton}"> row</c:if>">
        <c:if test="${not empty ctaButton}">
        <div class="col-md-8">
        </c:if>
          <c:forEach items="${component.textList}" var="item">
          <c:if test="${not empty item.text}">
          <p>${item.text}</p>
          </c:if>
          <div>${item.sabmSubText}</div>
          </c:forEach>
          <c:if test="${not empty ctaLink}">
          <ul class="list-group with-links">
              <c:forEach var="link" items='${component.ctaLinks}' varStatus="ctaLink">
              <c:if test="${ctaLink.count > (totalLinks - 5) and (link.renderType.code ne 'BUTTON')}">
              <li class="list-group-item">
              <a href="${link.url}">${link.name}</a>
              </li>
              </c:if>
              </c:forEach>
          </ul>
          </c:if>
        <c:if test="${not empty ctaButton}">
        </div>
        <div class="col-md-4">
          <a href="${ctaButton.url}" role="button" class="btn btn-primary">${ctaButton.name}</a>
        </div>
        </c:if>
    </div>
</div>