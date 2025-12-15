<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>


<div class="home-slider">
	<div class="visible-lg-block visible-md-block visible-sm-block">
		<div id="homepage_slider_desktop" class="slick-slider clearfix">

				<c:forEach items="${banners}" var="banner" varStatus="status">
					 <c:if test="${ycommerce:evaluateRestrictions(banner)}">
						<c:url value="${banner.urlLink}" var="encodedUrl" />
                        <div <c:catch var="exception">class="btn-${fn:length(banner.buttons)}"</c:catch><c:if test="${not empty exception}">class="btn-0"</c:if>>
                            <c:choose>
                                <c:when test="${not empty encodedUrl}"> 
                                    <a class="rotatingBannerTag"
                                     data-altText="${fn:escapeXml(not empty banner.headline ? banner.headline : banner.media.altText)}"
                                     data-url="${encodedUrl}"
                                     data-position="${status.count}"
                                     data-type="Banner"
                                    href="${encodedUrl}"<c:if test="${banner.external}"> target="_blank"</c:if> >
                                        <img src="${banner.media.url}" alt="${not empty banner.headline ? banner.headline : banner.media.altText}" title="${not empty banner.headline ? banner.headline : banner.media.altText}"/>
                                    </a>
                                </c:when>
                                <c:otherwise>
                                    <img
                                    data-altText="${fn:escapeXml(not empty banner.headline ? banner.headline : banner.media.altText)}"
                                    data-url="${encodedUrl}"
                                    data-position="${status.count}"
                                    data-type="Banner"
                                    class="rotatingBannerTag" src="${banner.media.url}" alt="${not empty banner.headline ? banner.headline : banner.media.altText}" title="${not empty banner.headline ? banner.headline : banner.media.altText}" />
                                </c:otherwise>
                            </c:choose> 


							<%--Button Offset Top -- ${button.offsetTop}
							Button Offset Left -- ${button.offsetLeft} 
							banner-buttons--is-left-${banner.isLeft}" --%>
                            <div class="banner-buttons">
                            	<c:catch var="exception">
                                <c:forEach items="${banner.buttons}" var="button" varStatus="status">                               
                                    <a class="banner-button<c:if test="${button.isVideo}"> popup-video</c:if>" href="${button.urlLink}" style="background-color: #${button.color};color:#${button.textColor};position:relative;left:${button.offsetLeft}px;top:${button.offsetTop}px">${button.text}</a>
                                </c:forEach>
                                </c:catch>
                            </div>
                        </div>

					</c:if>
				</c:forEach>
		</div>
	</div>

	<div class="visible-xs-block">
		<div id="homepage_slider_mobile" class="slick-slider video-carousel clearfix">
				<c:forEach items="${banners}" var="banner" varStatus="status">
					 <c:if test="${ycommerce:evaluateRestrictions(banner)}">
                        <c:url value="${banner.urlLink}" var="encodedUrl" />
                        <div <c:catch var="exception">class="btn-${fn:length(banner.buttons)}"</c:catch><c:if test="${not empty exception}">class="btn-0"</c:if>>
                            <a class="rotatingBannerTag"
                            data-altText="${fn:escapeXml(not empty banner.headline ? banner.headline : banner.media.altText)}"
                            data-url="${encodedUrl}"
                            data-position="${status.count}"
                            data-type="Banner"
                            tabindex="-1" href="${encodedUrl}"<c:if test="${banner.external}"> target="_blank"</c:if>>
                                <img src="${banner.media.url}" alt="${not empty banner.headline ? banner.headline : banner.media.altText}" title="${not empty banner.headline ? banner.headline : banner.media.altText}"/>
                            </a>

                            <div class="banner-buttons">
                            	<c:catch var="exception">
                                <c:forEach items="${banner.buttons}" var="button" varStatus="status">
                                    <c:choose>
                                        <c:when test="${button.isVideo}"> 
                                            <a class="banner-button slick-video-thumb regular-popup" href="#${button.pk}" data-url="${button.urlLink}" style="background-color: #${button.color};color:#${button.textColor};">${button.text}</a>
                                            <div id="${button.pk}" class="slick-video-mobile"></div>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="banner-button" href="${button.urlLink}" style="background-color: #${button.color};color:#${button.textColor};">${button.text}</a>
                                        </c:otherwise>
                                    </c:choose>                                     
                                </c:forEach>
                               	</c:catch>
                            </div>
                        </div>
                    </c:if>
				</c:forEach>
		</div>
	</div>

	<div class="slider-nav-wrap pull-right">
        <ul class="slider-nav visible-lg-block visible-md-block visible-sm-block">
            <li class="slider-prev">
                <svg class="icon-arrow-left">
                    <use xlink:href="#icon-arrow-left"></use>
                </svg>
            </li>
            <li class="slider-next">
                <svg class="icon-arrow-right">
                    <use xlink:href="#icon-arrow-right"></use>
                </svg>
            </li>
        </ul>
        <ul class="slider-nav visible-xs-block">
            <li class="slider-prev-mob">
                <svg class="icon-arrow-left">
                    <use xlink:href="#icon-arrow-left"></use>
                </svg>
            </li>
            <li class="slider-next-mob">
                <svg class="icon-arrow-right">
                    <use xlink:href="#icon-arrow-right"></use>
                </svg>
            </li>
        </ul>
    </div>
    <input type="text" style="display: none" class="homepage_slider_timeout" value="${timeout }">
</div>