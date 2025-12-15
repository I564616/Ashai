<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format"%>

<c:set value="${false}" var="isLoggedIn" />
<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
    <c:set value="${true}" var="isLoggedIn" />
</sec:authorize>
<c:set value="${false}" var="isBDECUSTOMERGROUP" />
<sec:authorize access="hasAnyRole('ROLE_BDECUSTOMERGROUP')" >
    <c:set value="${true}" var="isBDECUSTOMERGROUP" />
</sec:authorize>

<input type="hidden" name="pdpPriceError" value="${priceError}" />
<c:set var="productUnavailable" value="${disablePdp !=null && disablePdp.booleanValue()}"/>
<c:set value="${cmsSite.uid eq 'sga'}" var="isSga" />
<c:set value="${[{'name': 'Test order template', 'code': 'Test order template'},{'name': 'Copy of Test order template', 'code': 'Copy of Test order template'},{'name': 'Moday Template', 'code': 'jjjuuu'},{'name': 'Tuesday Template', 'code': 'abcde'},{'name': 'Tuesday Template', 'code': 'abcde'}]}" var="templateList" />
<c:set value="${fn:length(templateList) > 4}" var="isScrollable" />
<c:set value="" var="scrollerClass" />
<c:if test="${isScrollable}">
    <c:set value="scrollable" var="scrollerClass" />
</c:if>

<input type="hidden" name="product" value="${product.code}" />

<!-- Get close icon url-->
<spring:theme code="img.closeIcon" text="/" var="closeIconPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${closeIconPath}" context="${originalContextPath}" var="closeIconUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${closeIconPath}" var="closeIconUrl" />
	</c:otherwise>
</c:choose>

<div class="row">
       <div class="col-xs-10 col-xs-push-1 col-sm-8 col-sm-push-0 col-md-5 col-lg-5">
             <product:productImagePanel galleryImages="${galleryImages}" productUnavailable="${productUnavailable}"/>
       </div>
       <div class="clearfix hidden-sm hidden-md hidden-lg"></div>
       <div class="col-sm-4 col-md-3 col-md-3 paddingForPDP">
             <div class="product-main-info mb-xs-30">

                    <div class="row">
                           <div class="col-sm-12">
                                 <div class="product-details page-title">
                                 		<c:if test="${cmsSite.uid eq 'sga'}">
	                                 		<div class = "sga-product-code">
												${product.code}&nbsp;

												
												<c:if test= "${productUnavailable}">
													<span class="product-unavailable-text">&mdash;&nbsp;<spring:theme code="sga.product.unavailable" /></span>
												</c:if>
											</div>
                                 		</c:if>
                                        <div><span class="name">${product.apbBrand.name}</span><span class="pdp-name-and-details"> &nbsp;${fn:escapeXml(product.name)}</span></div>
                                        <div class = "product-details pdp-name-and-details">${product.unitVolume.name}</div> 
                                        <div class = "product-details pdp-name-and-details">${product.packageSize.name}</div> 
                                     
                                 </div>
                           </div>
                    </div>

					<c:choose>
						<c:when test="${product.stock.stockLevelStatus eq 'lowStock'}">
                    		<div class="col-sm-12">
								<div class="stock-details">
									<span class="price low_stock_status_name"> ${product.stock.stockLevelStatusName} </span>
								</div>
							</div>
						</c:when>
						<c:when test="${product.stock.stockLevelStatus eq 'outOfStock'}">
                    		<div class="col-sm-12">
								<div class="stock-details">
									<span class="price no_stock_status_name"> ${product.stock.stockLevelStatusName} </span>
								</div>
							</div>						
						</c:when>
					</c:choose>

                    <div class="col-sm-12">
                            <div class="product-details">
                                 <product:productPromotionSection product="${product}" />
                                 <c:if test="${!isNAPGroup}">
								 <c:choose>
								  <c:when test="${not empty product.price}">
								    <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" > 
	                                	<ycommerce:testId
	                                        code="productDetails_productNamePrice_label_${product.code}">
	                                        <product:productPricePanel product="${product}" />
	                                        
	                                 	</ycommerce:testId>
										<c:choose>
								  			<c:when test="${cmsSite.uid eq 'sga'}">
												<c:if test="${product.isPromotionActive && not empty product.promotionMsg}">
													<p class="pdp-promotion-message"><spring:theme code="sga.product.promotion.description.text"/>&nbsp;${product.promotionMsg }</p>
												</c:if>
												<div class="price_exclude"><spring:theme code="sga.product.details.page.price.exclude"/></div>
											</c:when>
											<c:otherwise>
												<div class="price_exclude"><spring:theme code="product.details.page.price.exclude"/></div>
											</c:otherwise>
										</c:choose>
                                 	</sec:authorize> 
								 </c:when>
								 <c:otherwise>
								 	<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" > 
	                                	<ycommerce:testId
	                                        code="productDetails_productNamePrice_label_${product.code}">
	                                        
											<p class="price">
												${textForNoPrice}
											</p>
	                                 	</ycommerce:testId>
										<c:choose>
								  			<c:when test="${cmsSite.uid eq 'sga'}">
												<div class="price_exclude"><spring:theme code="sga.product.details.page.price.exclude"/></div>
											</c:when>
											<c:otherwise>
												<div class="price_exclude"><spring:theme code="product.details.page.price.exclude"/></div>
											</c:otherwise>
										</c:choose>
                                 	</sec:authorize> 
								 
								 </c:otherwise>
								 </c:choose>
								 </c:if>

                           </div>

                           <cms:pageSlot position="VariantSelector" var="component"
                                 element="div" class="page-details-variants-select">
                                 <cms:component component="${component}" element="div"
                                        class="yComponentWrapper page-details-variants-select-component" />
                           </cms:pageSlot>
                           <c:if test="${!isNAPGroup}">
                           <cms:pageSlot position="AddToCart" var="component" element="div"
                                 class="page-details-variants-select PDP-add-to-cart">
                                 <cms:component component="${component}" element="div"
                                        class="yComponentWrapper page-details-add-to-cart-component" />
                           </cms:pageSlot>
                           </c:if>

                            <c:if test="${isSga && isBDECUSTOMERGROUP}">
                                <div class="row">
                                    <c:url value="/cart/add?action=addBonus" var="addBonusToCartUrl" />
                                    <div class="bonus-stock-plp bonus-stock-pdp mt-15" id="${fn:escapeXml(product.code)}">
                                        <button
                                            class="btn btn-vd-primary btn-primary"
                                            data-product-code="${fn:escapeXml(product.code)}"
                                            data-add-bonus-url="${addBonusToCartUrl}"
                                            data-product-name="${fn:escapeXml(product.name)}"
                                            data-csrf-token="${CSRFToken.token}"
                                            onclick="ACC.product.addBonus(this)"
                                            <c:if test="${product.allowedBonusQty lt 1}"><c:out value="disabled='disabled'"/></c:if>>
                                            <spring:theme code="order.entry.bonus.text"/>
                                        </button>
                                    </div>
                                </div>
                            </c:if>

                           <c:if test="${isSga && isLoggedIn && !isNAPGroup && !productUnavailable}">
                               <div class="row">
                                    <a href="#"
                                        data-modal-target="save-to-template"
                                        class="btn-link inline-block text-underline mt-10 disable-spinner"
                                        onclick="ACC.productDetail.openModal(this);"><b>Add to Template</b></a>
                               </div>
                           </c:if>
                    </div>
             </div>
       </div>
       <div class="hidden-xs hidden-sm col-md-4 col-lg-4 paddingForPDP">
             <div id="productTabs">
                    <cms:pageSlot position="Tabs" var="tabs">
                           <cms:component component="${tabs}" />
                    </cms:pageSlot>
             </div>

       </div>
    <div class="col-xs-12 col-sm-12 hidden-md hidden-lg onTabletTabs">
             <div id="productTabs" class="mt-sm-30">
                    <cms:pageSlot position="Tabs" var="tabs">
                           <cms:component component="${tabs}" />
                    </cms:pageSlot>
             </div>

        </div>
</div>

<c:if test="${cmsSite.uid eq 'sga' && not empty productRecommendation}">
 	<product:recommendedProduct/>	 
</c:if>

<c:if test="${isSga && isLoggedIn}">
    <div id="save-to-template" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="saveToTemplateLabel"  aria-hidden="true" data-backdrop="false">
        <div class="modal-dialog recommendation-popup-container">
            <div class="modal-content">
                <div class="modal-body">
                    <div class="alert-danger required hidden"><spring:theme code="form.global.error" /></div>
                    <div class="alert-danger failed hidden"></div> <!-- If template exists error message -->
                    <h2 class="h3"><strong><spring:theme code="sga.product.details.page.popup.header" /></strong></h2>
                    <div>
                        <div class="templates-order-table">
                            <c:url value="/cart/addProductToTemplate" var="addProductTemplate"/>
                            <div class="scroller"> <!-- replace with html template with jquery --></div>
                        </div>
                        <div class="form-group mt-35">
                            <label for="template-name text-capitalize"><strong><spring:theme code="sga.product.details.page.popup.title" /></label>
                            <div class="row">
                                <form id="ProductOrderTemplateForm" class="productOrderTemplateForm" method="post" modelAttribute="ProductOrderTemplateForm">
                                    <div class="col-xs-6 col-sm-8 offset-bottom-xsmall margin-top-3">
                                        <input type="text" class="form-control" id="templateCode"  name="templateCode" onfocus="ACC.productDetail.onFocus()"  />
                                        <input type="hidden" name="product" value="${product.code}" tabindex="-1" />
                                        <input type="hidden" name="quantity" tabindex="-1" />
                                        <input type="hidden" name="existingTemplate" value="false" tabindex="-1" />
                                        <span class="font-normal alert-danger required mt-10 hidden"><spring:theme code="sga.product.details.page.popup.field.required" /></span>
                                    </div>
                                    <div class="col-xs-6 col-sm-4 scroll-right-0">
                                        <button
                                            id="create-template-button"
                                            type="button"
                                            class="create-template-button btn btn-primary btn-vd-primary text-uppercase disable-spinner"
                                            onclick="ACC.productDetail.saveOrderTemplate('ProductOrderTemplateForm')"><spring:theme code="sga.product.details.page.popup.save" /></button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="recommendation-popup-close cursor" data-dismiss="modal">
                    <img src="${closeIconUrl}">
                </div>
            </div>
        </div>
    </div>
</c:if>
