<%@ tag trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="cart" tagdir="/WEB-INF/tags/responsive/cart" %>

<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <c:set value='' var="disabled" />
    </c:when>
    <c:otherwise>
        <c:set value='disabled="disabled"' var="disabled" />
    </c:otherwise>
</c:choose>

<div class="well-lg well well-tertiary">
    <div class="row">
        <div class="col-sm-12 item-wrapper">
            <div class="item-group">
                <div class="account-section-header user-register__headline secondary-page-title">
                    <spring:theme code="text.account.savedCartsIndividual" />: ${savedCartData.name}
                </div>
            </div>
        </div>
    </div>

    <div class="item-group">
        <ycommerce:testId code="orderDetail_overviewStatusDate_label">
            <span class="item-label"><spring:theme code="text.account.savedCart.createdBy" />: ${savedCartData.savedBy.name}</span>
        </ycommerce:testId>
    </div>
</div>
<input type="hidden" name="orderCode" value="${fn:escapeXml(savedCartData.code)}" />
<div class="row" id="save-cart-action-btns">
    <div class="col-xs-12 col-sm-4 col-md-3 col-lg-3">
		<ycommerce:testId code="savedCartDetails_reorder_link">
		<button type="submit" class="btn btn-primary btn-vd-primary btn-block re-order reorder-order-template-button" ${disabled} data-savedcart-id="${fn:escapeXml(savedCartData.code)}" <c:if test="${savedCartData.allProductExcluded != null && savedCartData.allProductExcluded.booleanValue()}"><c:out value="disabled"/></c:if> >
			<c:if test="${!isNAPGroup}">
			<img class="add-to-cart-icon white" src="/storefront/_ui/responsive/common/images/white_cart.svg"  />
			<c:if test="${cmsSite.uid eq 'sga'}">
			    <img class="add-to-cart-icon black hidden" src="/storefront/_ui/responsive/common/images/black_cart.svg"  />
			</c:if>
			</c:if>
			&nbsp;&nbsp;<spring:theme code="order.template.reorder.details.label"/>
			&nbsp;(<span class="totalQuickOrderProducts">0</span>)
		</button>
			<cart:savedCartReorderModal savedCart="${savedCartData}"/>
		</ycommerce:testId>
    </div>
    <div class="col-xs-12 col-sm-4 col-md-3 col-lg-3" id="delete-btn">
		<ycommerce:testId code="savedCartDetails_delete_link">
			<button type="submit" class="btn btn-primary btn-block js-delete-saved-order-template" data-savedcart-id="${fn:escapeXml(savedCartData.code)}">
				<spring:theme code="text.account.savedcart.delete.popuptitle"/> 
			</button>
			<%-- <cart:savedCartDeleteModal savedCart="${savedCartData}"/> --%>
		</ycommerce:testId>
    </div>
</div>

<div class="row no-margin-template">
    <br>
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="text.account.savedCarts.page.productlist" />
        
        <div class="float-right mobile-tablet-label-fix">
            <c:choose>
                <c:when test="${order.totalUnitCount} > 1}">
                    <span class="">${savedCartData.totalOrderedQty} <span> </span>
                    <spring:theme code="checkout.cart.items.total" />
                    </span>
                </c:when>
                <c:otherwise>
                    <span class="">${savedCartData.totalOrderedQty} <span> </span>
                    <spring:theme code="checkout.cart.items.total" />
                    </span>
                </c:otherwise>
            </c:choose>
        </div>
    </div>
</div>