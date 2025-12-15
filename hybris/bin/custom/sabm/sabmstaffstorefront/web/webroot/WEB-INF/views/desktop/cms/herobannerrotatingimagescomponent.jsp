<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<ul class="hero-banner-rotating-images" data-timeout="${component.timeout}">
    <c:forEach items="${component.heroBanners}" var="banner" varStatus="status">
        <c:if test="${banner.visible and ycommerce:evaluateRestrictions(banner)}">
    <li data-colortreatment="${banner.colorTreatment}" data-textposition="${banner.textAlignment.code}">
            <div class="image-wrapper">
                <c:if test="${not empty banner.urlLink}"><a href="${banner.urlLink}?&userpk=${user.gaUid}"<c:if test="${banner.external}"> target="_blank"</c:if>></c:if>
                   <c:if test="${not empty banner.desktopHeroImage.url}">
                    <img src="${banner.desktopHeroImage.url}" class="hidden-xs" alt="${banner.desktopHeroImage.altText}" />
                    </c:if>
                    <c:if test="${not empty banner.mobileHeroImage.url}">
                    <img src="${banner.mobileHeroImage.url}" class="visible-xs-block" alt="${banner.mobileHeroImage.altText}" />
                    </c:if>
                <c:if test="${not empty banner.urlLink}"></a></c:if>
            </div>
            <c:if test="${empty banner.urlLink}">
            <div class="content-wrapper">
                <c:if test="${not empty banner.desktopTitle}">
                <h3 class="title hidden-xs">${banner.desktopTitle}</h3>
                </c:if>
                <c:if test="${not empty banner.mobileTitle}">
                <h3 class="title visible-xs-block">${banner.mobileTitle}</h3>
                </c:if>
                <c:if test="${not empty banner.textList}">
                <c:forEach items="${banner.textList}" var="item">
                <p class="desc">${item.name}</p>
                </c:forEach>
                </c:if>
                <div class="hero-banner-buttons">
                    <c:if test="${not empty banner.ctaLinks}">
                    <c:forEach items="${banner.ctaLinks}" var="item" varStatus="i">
                    <c:choose>
                    <c:when test="${item.renderType.code eq 'LINK'}">
                    <div class="d-block">
                        <a href="${item.url}">${item.name}</a>
                    </div>
                    </c:when>
                    <c:otherwise>
                    <div class="d-block">
                        <a href="${item.url}" class="btn btn-primary" role="button">${item.name}</a>
                    </div>
                    </c:otherwise>
                    </c:choose>
                    </c:forEach>
                    </c:if>
                </div>
            </div>
            </c:if>
    </li>
    </c:if>
    </c:forEach>
</ul>