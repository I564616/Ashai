<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<c:set value="1" var="numberOfBrand" />
<c:set value="true" var="isClose" />

<c:set var="count" value="0" scope="page" />

<div class="brand-grid" data-category="${brandLink.linkName}">
	<%-- <c:if test="${not empty brandLink.linkName}">
		<h1><a href="${brandLink.url}">${brandLink.linkName}</a></h1>
	</c:if>--%>
	<%-- <c:forEach items="${brands}" var="brand" varStatus="status"> --%>
	<c:forEach items="${filterBrands}" var="brand" varStatus="status">
	
<c:set var="count" value="${count + 1}" scope="page"/>

		<c:if test="${(numberOfBrand-1) % 4 == 0 and brand.visible}">
			<c:set value="false" var="isClose" />
			<div class="row">
		</c:if>
		<c:if test="${brand.visible}">
			<c:set value="${numberOfBrand+1}" var="numberOfBrand" />
			<div class="col-xs-6 col-sm-3 brand-grid-item text-center">
				<a class="rotatingBannerTag"
                   data-altText="${fn:escapeXml(brand.media.altText)}"
                   data-url="${brand.urlLink}"
                   data-name="${brand.name}"
                   data-category="${brandLink.linkName}"
                   data-position="${status.count}"
                   data-type="BrandLogo" href="${brand.urlLink}"> <img alt="${brand.media.altText}"
					src="${brand.media.url}">
				</a>
			</div>
		</c:if>
		<c:if test="${(numberOfBrand-1) % 4 == 0 and brand.visible}">
			<c:set value="true" var="isClose" />
			</div>
		</c:if>

	</c:forEach>

	<c:if test="${isClose eq 'false'}">
		</div>
	</c:if> 
</div>