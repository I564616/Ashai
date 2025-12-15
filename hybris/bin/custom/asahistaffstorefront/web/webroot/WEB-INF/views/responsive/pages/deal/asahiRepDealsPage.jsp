<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

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

<spring:url value="/deals/update" var="updateUrl" />
<spring:url value="/doCustomerSearch" var="searchUrl" />

<spring:theme code="img.missingProductImage.responsive.product" text="/" var="missingImagePath" />
<c:set value="${fn:split(fn:replace(request.getRequestURL(), 'https://', ''), '/')}" var="url" />
<c:set value="https://${url[0]}/${url[1]}${missingImagePath}" var="imagePath" />

<spring:theme code="staff.portal.deals.customer" arguments="${customerNumber}, ${customerName}" var="customerAccount"/>
<div class="row deals mb-50">
    <div class="col-xs-12">
        <h1 class="h2 bold login-page__headline">
            <spring:theme code="staff.portal.deals.title" arguments="${customerNumber}, ${customerName}"/>
            <span class="deal-customer">${customerAccount}</span>
        </h1>
        <c:choose>
            <c:when test="${empty asahiDeals}">
                <h3><spring:theme code="staff.portal.deals.no.products" /></h3>
            </c:when>
            <c:otherwise>
                <p class="h5 mt-25 mb-30">
                    <spring:theme code="staff.portal.deals.text" /> (<strong>${customerAccount}</strong>).
                </p>
                <form
                    id="dealsForm"
                    class="multi-checkboxes"
                    action="${updateUrl}"
                    method="POST"
                    data-deals-amount="${fn:length(asahiDeals)}">
                    <div class="form-group">
                        <label for="selectAll" class="text-init-transform text-underline cursor">
                            <input id="selectAll" class="hidden" type="checkbox" onclick="ACC.deals.selectAll(this);" />
                            <span class="checkmark pull-left hidden"></span>
                            <span id="select-text" class="font-normal"><spring:theme code="staff.portal.deals.select.all" /></span>
                        </label>
                    </div>
                    <div class="mt-25 mb-35 mb-xs-20">
                        <c:forEach items="${asahiDeals}" var="deal" varStatus="loop">
                            <c:set value="${loop.index}" var="itemIndex" />
                            <c:choose>
                                <c:when test="${not empty deal.conditionProduct.images[0].url}">
                                    <c:set value="${deal.conditionProduct.images[0].url}" var="imageUrl" />
                                </c:when>
                                <c:otherwise>
                                    <c:url value="${imagePath}" var="imageUrl" />
                                </c:otherwise>
                            </c:choose>

                            <div class="form-group">
                                <div class="checkbox">
                                    <label for="deal-input-${itemIndex}" class="display-flex pl-0">
                                        <input id="deal-input-${itemIndex}" class="deal-select" type="checkbox" value="${deal.code}" <c:if test="${deal.active}">checked</c:if> />
                                        <span class="checkmark"></span>
                                        <div class="product-image">
                                            <img src="${imageUrl}" alt="${deal.code}"/>
                                        </div>
                                        <span class="label-text">
                                            ${deal.title} <br />
                                            <span class="grey-white text-italic"> Valid from <fmt:formatDate value="${deal.validFrom}" pattern="dd/MM/yyyy" /> - <fmt:formatDate value="${deal.validTo}" pattern="dd/MM/yyyy" /></span>
                                        </span>
                                    </label>
                                </div>
                            </div>
                        </c:forEach>
                    </div>

                    <div class="row">
                        <div class="col-xs-6 col-sm-5 col-md-3">
                            <button type="button" id="update-btn" class="btn btn-vd-primary text-uppercase js-update-btn" onclick="ACC.deals.openModal(this)"  disabled data-modal-target="deals-template"><spring:theme code="staff.portal.deals.update" /></button>
                        </div>
                    </div>

                    <!-- Modal -->
                    <div id="deals-template" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="uploaderTemplateLabel"  aria-hidden="false" data-backdrop="false">
                        <div class="modal-dialog">
                            <div class="modal-content">
                                <div class="modal-body uploader">
                                    <div class="alert-danger required" style="display: none;"><spring:theme code="form.global.error" /></div>
                                    <div class="alert-danger failed hidden"></div> <!-- If email exists error message -->
                                    <h2 class="h3"><strong><spring:theme code="staff.portal.deals.popup.title" /></strong></h2>
                                    <p class="mt-25"><spring:theme code="staff.portal.deals.popup.text" /></p>
                                    <div class="form-group mt-10">
                                        <label for="send-confirm" class="display-flex position-relative pl-0">
                                            <input id="send-confirm" type="checkbox" value="sendConfirm" checked onchange="ACC.deals.onChange(this)" />
                                            <span class="checkmark"></span>
                                            <span class="label-text"><spring:theme code="staff.portal.deals.popup.send.confirm" /></span>
                                        </label>

                                        <div id="additional-section">
                                            <div class="row mt-20">
                                                <div class="col-xs-12">
                                                    <p><spring:theme code="staff.portal.deals.popup.subtitle" /></p>
                                                    <p><spring:theme code="staff.portal.deals.popup.text1" /></p>
                                                    <textarea class="detail-area form-control" rows="5" name="dealsDetails"></textarea>
                                                </div>
                                            </div>

                                            <div class="row mt-20">
                                                <div class="col-xs-12">
                                                    <p><strong><spring:theme code="staff.portal.deals.popup.subtitle1" /></strong></p>
                                                    <p><spring:theme code="staff.portal.deals.popup.text2" /></p>
                                                    <div id="customer-emails" class="customer-emails mt-10" data-emails="${customerEmailIds}"></div>
                                                </div>
                                            </div>

                                            <div class="row mt-20">
                                                <div class="col-xs-12">
                                                    <p><spring:theme code="staff.portal.deals.popup.subtitle2" /></p>
                                                    <div class="col-xs-7 pl-0 pr-xs-5">
                                                        <input id="addEmail" type="text" class="form-control" onfocus="ACC.deals.removeError()" onblur="ACC.deals.removeError()"/>
                                                        <span class="alert alert-danger required inline-block"></span>
                                                    </div>
                                                    <div class="col-xs-5 pr-xs-5 pl-xs-5">
                                                        <button type="button" class="btn btn-primary" onclick="ACC.deals.addItem('addEmail');"/><spring:theme code="staff.portal.deals.popup.add.email" /></button>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>

                                         <div class="row mt-30">
                                             <div class="col-xs-6">
                                                <!-- Handle inputs for submission -->
                                                <button type="button" class="btn btn-vd-primary" style="width: 100%;" onclick="ACC.deals.onSubmit('${customerNumber}');"><spring:theme code="staff.portal.deals.popup.confirm" /></button>
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
                </form>
            </c:otherwise>
        </c:choose>
        <!-- Back to search button -->
        <div class="row">

            <div class="col-xs-12">
                <form:form id="customerSearchForm" name="customerSearchForm" modelAttribute="asahiCustomerSearchForm" action="${searchUrl}">
                    <input type="hidden" name="page" value=""/>
                    <input type="hidden" name="sort" value=""/>
                    <input type="hidden" name="accountPayerNumber" value=""/>
                    <input type="hidden" name="customerName" value=""/>
                    <input type="hidden" name="email" value=""/>
                    <input type="hidden" name="address" value=""/>
                    <input type="hidden" name="postcode" value=""/>
                    <input type="hidden" name="suburb" value=""/>
                    <input type="hidden" name="expiryDateMonth" value=""/>
                    <button type="submit" class="textButton text-underline inline-block mt-20" onclick="ACC.deals.search()"><b><spring:theme code="staff.portal.deals.back.url" /></b></button>
                </form:form>
            </div>
        </div>
    </div>
</div>