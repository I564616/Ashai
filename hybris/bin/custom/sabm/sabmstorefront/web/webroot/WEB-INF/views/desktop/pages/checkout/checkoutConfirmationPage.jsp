<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/desktop/user"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<%@ taglib prefix="confirmation" tagdir="/WEB-INF/tags/desktop/checkout/confirmation"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>  

<c:url value="/Beer/c/10" var="beerPageURL" />
<c:url value="/your-business/orders" var="orderHistoryUrl" />
<c:url value="/businessEnquiry" var="BusinessEnquiryURL" />
<input id="order-detail-inputID" type="hidden" value="${orderCode}">
<input type="hidden" class="checkoutConfirmation">

<template:page pageTitle="${pageTitle}">
	<div id="globalMessages">
		<common:globalMessages />
	</div>
	<div class="row">
		<div class="col-md-12">
			<h1>
				<spring:theme code="checkout.orderConfirmation.title" />
			</h1>
		</div>
	</div>
    <confirmation:recommendationData/>

	<div class="row">
		<div class="col-md-12">
			<cms:pageSlot position="TopContent" var="feature" element="div"
				class="span-24 top-content-slot cms_disp-img_slot">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<div>
				<ycommerce:testId code="orderConfirmation_yourOrderResults_text">
					<div>
						<spring:theme code="checkout.orderConfirmation.thankYouForOrder" />
					</div>
					<div>
					<c:choose>
				
						<c:when test="${orderData.bdeOrder eq true }">
						<spring:theme code="checkout.orderConfirmation.bdeOrder.copySentTo"/>
						
						<c:forEach items="${orderData.bdeOrderEmails}" var="email" varStatus="loop">
						   <c:choose>
						   <c:when test="${loop.last}"> and ${email}</c:when>
						  <c:when test="${loop.count == orderData.bdeOrderEmails.size()-1}">${email} </c:when>
						   <c:otherwise>${email}, </c:otherwise>
						   
						   </c:choose>
						
						</c:forEach>

						
						</c:when>
						 <c:otherwise>
							<spring:theme code="checkout.orderConfirmation.copySentTo" arguments="${email}" />
						 </c:otherwise>
					 </c:choose>
					
					</div>
					<div>
                    			   <confirmation:confirmationDetails/>
                    </div>
				</ycommerce:testId>
            </div>
			<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
				<div class="span-24 delivery_stages-guest last">
					<user:guestRegister actionNameKey="guest.register.submit" />
				</div>
			</sec:authorize>
			<cms:pageSlot position="SideContent" var="feature" element="div"
				class="span-24 side-content-slot cms_disp-img_slot">
				<cms:component component="${feature}" />
			</cms:pageSlot>
			<div class="row">
                <div class="col-xs-12 col-md-7 action-box margin-top-30">
                    <a href="${beerPageURL}" class="btn btn-primary"><spring:theme
                            code="checkout.orderConfirmation.continueShopping" /></a>
                    <div class="magnific-template-order" style="margin-left:1em">
                        <a class="inline" href="#save-as-template"><spring:theme code="cart.page.save.as.template"/></a>
                    </div>
                     <ycommerce:testId code="orderConfirmation_yourOrderResults_text">
                        <span style="margin-left:1em">
                            <a class="inline" href="${orderHistoryUrl}"><spring:theme code="checkout.orderConfirmation.viewOrder" text="View Order"/></a>
                        </span>
                    </ycommerce:testId>
                </div>
            </div>
              <div>
            <br/>
            <ycommerce:testId code="orderConfirmation_yourOrderResults_text">
					<div>
						<spring:theme code="checkout.orderConfirmation.EmptyKegPickup.describe" />
					</div>
				</ycommerce:testId>
            </div>
            <div>
            	<br/>
                    <a href="${BusinessEnquiryURL}" class="btn btn-primary" ><spring:theme
                            code="checkout.orderConfirmation.EmptyKegPickup" /></a>
            </div>
		</div>
	</div>
	<templatesOrder:templateOrderPopup/>
</template:page>
