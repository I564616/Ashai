<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/responsive/action" %>

<div class="addtocart-component">
		<c:if test="${empty showAddToCart ? true : showAddToCart}">
		<div class="col-sm-6">
			<div class="summary-label"><spring:theme code="text.product.quantity"/></div>
		</div>
		
		<div class="col-sm-6 summary-value">
                <div class="row">
                    <div class="col-sm-6 trim-right-5">
                     <ul class="select-quantity">
                         <li class="down"><a href="#">
                         	<svg class="icon-minus">
                         	    <use xlink:href="#icon-minus"></use>    
                         	</svg>
                         </a></li>
                         <li><input class="qty-input" min="0" type="tel" value="1" data-minqty="1" pattern="\d*"></li>
                         <li class="up"><a href="#">
                         	<svg class="icon-plus">
                         	    <use xlink:href="#icon-plus"></use>    
                         	</svg>
                         </a></li>
                     </ul>
                     <span>10 Cases</span>
                 </div>
                 <div class="col-sm-6 trim-left-5">
                     <div class="select-list">
                         <div data-value="" class="select-btn">Case</div>
                         <ul class="select-items">
                             <li>Case</li>
                             <li>Layer</li>
                             <li>Palet</li>
                         </ul>
                    </div>
                </div>
            </div>
        </div>

		</c:if>
		<c:if test="${product.stock.stockLevel gt 0}">
			<c:set var="productStockLevel">${product.stock.stockLevel}&nbsp;
				<spring:theme code="product.variants.in.stock"/>
			</c:set>
		</c:if>
		<c:if test="${product.stock.stockLevelStatus.code eq 'lowStock'}">
			<c:set var="productStockLevel">
				<spring:theme code="product.variants.only.left" arguments="${product.stock.stockLevel}"/>
			</c:set>
		</c:if>
		<c:if test="${product.stock.stockLevelStatus.code eq 'inStock' and empty product.stock.stockLevel}">
			<c:set var="productStockLevel">
				<spring:theme code="product.variants.available"/>
			</c:set>
		</c:if>
		<div class="stock-status">
			${productStockLevel}
		</div>
		<div class="row">
			<action:actions element="div"  parentComponent="${component}"/>
		</div>
</div>