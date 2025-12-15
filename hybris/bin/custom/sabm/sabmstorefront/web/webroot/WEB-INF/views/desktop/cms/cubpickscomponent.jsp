<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>

<!-- CUB Picks Start-->
<c:if test="${not empty productData}">
									
<section class="product-picks row" id="cubPickSection" data-cupRefreshInProgress="${cupRefreshInProgress}">

		
        <div class="col-xs-12 clearfix">
        	<c:choose>
				<c:when test="${smartOrderPage}">
					 <h2 class="product-picks-title"><spring:theme code="text.smartorders.youmayalsolike" text="text.smartorders.youmayalsolike"/></h2>
				</c:when>
				<c:otherwise>
					<h2 class="product-picks-title"><spring:theme code="text.homepage.cubpicks" text="text.homepage.cubpicks"/></h2>
				</c:otherwise>
			</c:choose>

         </div>
		<div class="clearfix"></div>
		<div class="product-picks-items">
<!-- 			<div class="loading-message">
				<div class="loading-content">
					<span>loading...</span><br>
					<img src="/_ui/desktop/SABMiller/img/spinner.gif" alt="Loading Image">
				</div>
			</div> -->
	    	<c:forEach items="${productData}" var="productMapping" varStatus="status">
		        <div class="col-xs-12 col-sm-6 col-md-3 addtocart-qty productImpressionTag">	        
		    		<home:homePick title="${productMapping.key }" product="${productMapping.value}" count="${status.count}" productListName="Home/CUB Picks"/>
		        </div>
	        </c:forEach>
	        <div class="clearfix"></div>
		</div>


    <!-- <hr/> -->
</section>
<!-- CUB Picks Ends-->
	</c:if>
<hr>
<product:productPricePopup/>
