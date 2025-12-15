<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="component" tagdir="/WEB-INF/tags/shared/component"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<div class="smart-order-banner">
			<div class="bg-image">
				<div class="container-main clearfix">
					<div class="col-md-10 col-sm-12 col-xs-12">
						<h2 class="break-word margin-bottom-0">
							<svg class="icon-glasses">
								<use xlink:href="#icon-glasses"></use>
							</svg>
							<!--${linkParagraphComponent.title}-->
							<spring:theme code="text.headline.imagebanner"/>
						</h2>
					</div>
					<!--<p class="offset-bottom-small">${linkParagraphComponent.content}</p>-->
					<div class="col-md-2 col-sm-12 col-xs-12">
						<h3>
							<c:url value="${linkParagraphComponent.url}" var="linkUrl"/>
							<a id="imagelinkTag" data-url="${linkUrl}" href="${linkUrl}">${linkParagraphComponent.linkText}</a>
						</h3>
					</div>
				</div>
			</div>
		</div>


