<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:choose>
  <c:when test="${component.textAlignment eq 'LEFT'}">
    <c:set var="textAlignment" value=""/>
  </c:when>
  <c:when test="${component.textAlignment eq 'RIGHT'}">
    <c:set var="textAlignment" value=" text-right"/>
  </c:when>
  <c:otherwise>
    <c:set var="textAlignment" value=" text-center"/>
  </c:otherwise>
</c:choose>

<c:choose>
  <c:when test="${component.colorTreatment eq 'LIGHT'}">
    <c:set var="colorTreatment" value=" light-theme"/>
  </c:when>
  <c:when test="${component.colorTreatment eq 'LIGHT_DARK'}">
    <c:set var="colorTreatment" value=" light-dark-theme"/>
  </c:when>
  <c:otherwise>
    <c:set var="colorTreatment" value=""/>
  </c:otherwise>
</c:choose>

<c:set var="visible" value="${component.visible}" />
<c:choose>
  <c:when test="${visible eq 'TRUE'}">
    <c:set var="visible" value="true"/>
  </c:when>
  <c:otherwise>
    <c:set var="visible" value="false"/>
  </c:otherwise>
</c:choose>

<c:set var="fit" value="${component.fit}" />
<c:choose>
  <c:when test="${fit eq 'SHRINK'}">
    <c:set var="fit" value="shrink"/>
  </c:when>
  <c:when test="${fit eq 'EXPANDED'}">
    <c:set var="fit" value="expanded"/>
  </c:when>
  <c:otherwise>
    <c:set var="fit" value=""/>
  </c:otherwise>
</c:choose>

<!-- show only boundary when there's no background image -->
<c:if test="${component.desktopHeroImage.url eq null || component.desktopHeroImage.url eq ''}">
	<!-- New Attributes: Boundary / BoundaryType
		@author: lester.l.gabriel
	 -->	
	<c:set var="boundary" value="${component.boundary}" />

	<!-- conditional statement for the new attributes -->	
	<c:choose>
	  <c:when test="${boundary eq 'REGULAR'}">
	    <c:set var="boundary" value="regular"/>
		<c:set var="boundaryType" value="${component.boundaryType}" />
		<c:choose>
		  <c:when test="${boundaryType eq 'DOTTED'}">
		    <c:set var="boundaryType" value="dotted"/>
		  </c:when>
		  <c:when test="${boundaryType eq 'DASHED'}">
		    <c:set var="boundaryType" value="dashed"/>
		  </c:when>
		  <c:when test="${boundaryType eq 'SOLID'}">
		    <c:set var="boundaryType" value="solid"/>
		  </c:when>
		  <c:otherwise>
		    <c:set var="boundaryType" value=""/>
		  </c:otherwise>
		</c:choose>
	  </c:when>
	  <c:otherwise>
	    <c:set var="boundary" value=""/>
	  </c:otherwise>
	</c:choose>
</c:if>

<!-- button alignment attribute -->
<c:set var="ctaAlignment" value="${component.ctaAlignment}" />
<c:choose>
  <c:when test="${ctaAlignment eq 'LEFT'}">
    <c:set var="ctaAlignment" value="left"/>
  </c:when>
  <c:when test="${ctaAlignment eq 'CENTER'}">
    <c:set var="ctaAlignment" value="center"/>
  </c:when>
  <c:when test="${ctaAlignment eq 'RIGHT'}">
    <c:set var="ctaAlignment" value="right"/>
  </c:when>
  <c:otherwise>
    <c:set var="ctaAlignment" value=""/>
  </c:otherwise>
</c:choose>


<c:forEach items="${component.ctaLinks}" var="item">
  <c:if test="${item.renderType.code eq 'LINK'}">
    <c:set var="ctaLink" value="${item}"/>
    <c:set var="totalLinks" value="${fn:length(component.ctaLinks)}" />
  </c:if>
  <c:if test="${item.renderType.code eq 'BUTTON'}">
    <c:set var="ctaButton" value="${item}"/>
  </c:if>
</c:forEach>

<div class="title-image-text-box-component ${textAlignment} ${colorTreatment}" 
	 data-boundary="${boundary}"
	 data-boundaryType="${boundaryType}"
	 data-fit="${fit}"
	 data-visible="${visible}">
  <c:if test="${not empty component.desktopTitle}">
    <h3>
    	<c:if test="${component.titleIcon == 'BLUB' || component.titleIcon == 'BULB'}">
    		<span class="icon-bulb"></span>
    	</c:if>
    	${component.desktopTitle}
    </h3>
  </c:if>
  <c:if test="${component.desktopHeroImage.url ne null && component.desktopHeroImage.url ne ''}">
    <p class="image"><img src="${component.desktopHeroImage.url}" alt="${component.desktopHeroImage.altText}" role="presentation" /></p>
  </c:if>
  <c:forEach items="${component.textList}" var="item">
  <c:if test="${not empty item.name && item.name ne 'GenericTextContentItem'}">
    <p><strong>${item.name}</strong></p>
  </c:if>
  <c:if test="${not empty item.text}">
    <p>${item.text}</p>
  </c:if>
  <div>${item.sabmSubText}</div>
  </c:forEach>

  <c:if test="${component.titleIcon == 'BLUB' || component.titleIcon == 'BULB'}">&nbsp;</c:if>

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
	    <a href="${ctaButton.url}" role="button" class="btn btn-primary" data-ctaAlignment="${ctaAlignment}">${ctaButton.name}</a>
  </c:if>
</div>