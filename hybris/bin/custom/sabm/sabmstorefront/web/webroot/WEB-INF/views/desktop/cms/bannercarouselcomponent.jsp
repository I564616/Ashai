<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<div class="carousel-wraper">
	<div class="general-carousel col-md-12">
		<c:forEach items="${carouselItems}" var="component">
			<cms:component component="${component}" />
		</c:forEach>


	</div>
	<div class="slider-nav-wrap">
		<ul class="slider-nav">
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
	</div>
</div>

