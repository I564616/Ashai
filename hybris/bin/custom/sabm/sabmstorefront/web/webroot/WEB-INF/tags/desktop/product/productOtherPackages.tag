<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/desktop/storepickup"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>

<div class="other-packages">
	<div id="otherPack">
		<div class="slider-nav-wrap pull-right">
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
		<div class="slick-slider clearfix">
			<c:forEach items="${product.baseOptions[0].options}" var="variant" varStatus="status">
				<c:if test="${not empty product.baseOptions[0].selected and product.baseOptions[0].selected.code ne variant.code}">
		            <div class="col-xs-12 col-sm-6 col-md-3 addtocart-qty slider-height">
                        <home:homePick title="" product="${product}" variant="${variant}" productListName="Home/ProductPage/OtherProduct" count="${status.count}"/>
                    </div>
				</c:if>
			</c:forEach>
		</div>
	</div>
</div>