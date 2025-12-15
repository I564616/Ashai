<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

${fn:length(heroBanners)}


<b style="color:red"> sabmherobannercomponent.jsp </b> </br>
<!--
Desktop title : ${banner.desktopTitle} </br>
mobile title : ${banner.mobileTitle} </br>
<c:forEach items="${banner.textList}" var="item">
    Text List : ${item.name} </br>
</c:forEach>
Background image desktop : <img src="${banner.desktopHeroImage.url}" height="150" width="300" /> </br>

Background image mobile : <img src="${banner.mobileHeroImage.url}" height="150" width="300" /> </br>

-->

<div class="home-slider">
	<div class="visible-lg-block visible-md-block visible-sm-block">
		<!--
		#original div
		#<div id="homepage_slider_desktop" class="slick-slider clearfix">
		-->
		<div id="homepage_slider_desktop" class="clearfix">

		        <img
                        data-altText="${fn:escapeXml(component.desktopTitle)}"
                        data-position="3"
                        data-type="Banner"
                        class="rotatingBannerTag" src="${component.desktopHeroImage.url}" alt="${component.desktopTitle}" title="${component.desktopTitle}" />

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
