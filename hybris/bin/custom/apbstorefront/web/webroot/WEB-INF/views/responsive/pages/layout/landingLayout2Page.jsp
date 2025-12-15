<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>



<c:set value = "false" var="isAnonymous" />
<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')">
    <c:set value="true" var="isAnonymous" />
</sec:authorize>

<input id="isAnonymousUser" type="hidden" value="${isAnonymous}"/>
<input id="showAlbCCPopup" type="hidden" value="${showAlbCCPopup}" />
<input id="disableCCInfoPopupUrl" type="hidden" value = '<c:url value="/cc-info-popup/disable" />' />

<template:page pageTitle="${pageTitle}">
		<c:if test="${accessMessege ne null}">
			<div class="asahi-logout-msg">
				<spring:theme code="sga.homepage.pay.access.${accessMessege}"/>
			</div>
		</c:if>
    <sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
        <div class="white_space"> 
<!--            Do not delete this div. -->
        </div>
    </sec:authorize>
    
    <c:url value="/quickOrder" var="quickOrderUrl"/>
    <c:url value="my-account/orders" var="orderHistoryUrl"/>
    <c:url value="my-account/saved-carts" var="orderTemplateUrl"/>
	<c:url value="/invoice" var="invoicesAndPaymentsUrl"/>
	<c:url value="contactus" var="contactusURL"/>
    <div class="homepage_cms_slot_1">
        <cms:pageSlot position="Section1" var="feature">
                <c:choose>
                    <c:when test="${feature.uid eq  'CustomerCreditLimitExceedParagraphComponent' && creditLimit eq true}">
                        <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
                            <div class="white_space_credit"></div>
                            <cms:component component="${feature}" />
                        </sec:authorize>
                    </c:when>
                    <c:otherwise>
                        <c:if test="${feature.uid ne  'CustomerCreditLimitExceedParagraphComponent'}">
                            <cms:component component="${feature}" />
                        </c:if>
                    </c:otherwise>
                </c:choose>
         </cms:pageSlot>
        <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')" >
			<c:choose>
				<c:when test="${cmsSite.uid eq 'sga'}">

					<div class="row" id="sga-homepage-top-link">

						<div class="col-xs-12 col-sm-12 col-md-8 col-lg-8">
							<cms:pageSlot position="Section2B" var="feature" element="div" class="recommendation-component no-margin">
								<cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
							</cms:pageSlot>
						</div>

						<div class="col-xs-12 col-sm-12 col-md-4 col-lg-4">
							<!--    These are the three Order Buttons for SGA .-->
							<c:if test="${accessType eq 'ORDER_ONLY' || accessType eq 'PAY_AND_ORDER'}">
									<c:if test="${!isNAPGroup}">
									<div class="row">
										
										<div class="">
											<div class="sga-logged-in-user-options">
												<a href="${quickOrderUrl}" class="btn sga-homepage-loggedin-btn"><spring:theme code="sga.homepage.loggedin.btn.quickorder" /></a>
											</div>
										</div>
									</div>
									<div class="row">
										
										<div class="">
											<div class="sga-logged-in-user-options">
												<a href="${orderTemplateUrl}" class="btn sga-homepage-loggedin-btn"><spring:theme code="sga.homepage.loggedin.btn.orderfromtemplate" /></a>
											</div>
										</div>
									</div>
									</c:if>
									<div class="row">
										
										<div class="">
											<div class="sga-logged-in-user-options">
												<a href="${orderHistoryUrl}" class="btn sga-homepage-loggedin-btn"><spring:theme code="sga.homepage.loggedin.btn.orderfromhistory" /></a>
											</div>
										</div>
									</div>
							</c:if>
							<c:if test="${accessType eq 'PAY_ONLY' || accessType eq 'PAY_AND_ORDER'}">
								<c:if test="${!isApprovalPending && !isAccessDenied}">
									<div class="row">
										
										<div class="">
											<div class="sga-logged-in-user-options">
												<a href="${invoicesAndPaymentsUrl}" class="btn sga-homepage-loggedin-btn"><spring:theme code="sga.homepage.loggedin.btn.invoicesandpayments" /></a>
											</div>
										</div>
									</div>
								</c:if>
							</c:if>
							<c:if test="${accessType eq 'ORDER_ONLY'}">
								<div class="row">
									
									<div class="">
										<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
											<div class="transition-message-heading"><spring:theme code="sga.homepage.order.only.payoff.message" /></div>
											<c:choose>
												<c:when test="${isApprovalPending && !isAccessDenied}">
													<spring:theme code="sga.homepage.pay.access.pending.message" arguments="${approvalEmailId}" />
												</c:when>
												<c:otherwise>
													<c:choose>
														<c:when test="${approvalEmailId eq null}">
															<a href="" action="/validateForPayerAccess?code=" accessRequestType="PAY_ONLY" method="GET" class="transition-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.pay.only.order.link"/>
														</c:when>
														<c:otherwise>
															<a href="" action="/validateForPayerAccess?code=" accessRequestType="PAY_ONLY" method="GET" class="transition-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.order.only.payoff.link" arguments="${approvalEmailId}"/>
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${accessType eq 'PAY_ONLY'}">
								<div class="row">
									
									<div class="">
										
										<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
											<div class="transition-message-heading"><spring:theme code="sga.homepage.pay.only.order.message" /></div>
											<a href="" action="/validateForPayerAccess?code=" accessRequestType="ORDER_ONLY" method="GET" class="transition-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.pay.only.order.link"/>
										</div>
										
										<c:if test="${isApprovalPending && !isAccessDenied}">
											<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
												<div class="transition-message-heading"><spring:theme code="sga.homepage.order.only.payoff.message" /></div>
												<spring:theme code="sga.homepage.pay.access.pending.message" arguments="${approvalEmailId}" />
											</div>
										</c:if>
										<c:if test="${isAccessDenied}">
											<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
												<div class="transition-message-heading"><spring:theme code="sga.homepage.order.only.payoff.message" /></div>
												<a href="" action="/validateForPayerAccess?code=" accessRequestType="PAY_ONLY" method="GET" class="transition-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.order.only.payoff.link" arguments="${approvalEmailId}"/>
											</div>
										</c:if>
									</div>
								</div>
							</c:if>
							<c:if test="${accessType eq 'PAY_AND_ORDER' && isApprovalPending && !isAccessDenied}">
								<div class="row">
									
									<div class="">
										<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
											<div class="transition-message-heading"><spring:theme code="sga.homepage.order.only.payoff.message" /></div>
											<spring:theme code="sga.homepage.pay.access.pending.message" arguments="${approvalEmailId}" />
										</div>
									</div>
								</div>
							</c:if>
							<c:if test="${accessType eq 'PAY_AND_ORDER' && isAccessDenied}">
								<div class="row">
									
									<div class="">
										<div class="sga-logged-in-user-options homepage-transition-message homepage-inovices-payments-link">
											<div class="transition-message-heading"><spring:theme code="sga.homepage.order.only.payoff.message" /></div>
											<a href="" action="/validateForPayerAccess?code=" accessRequestType="PAY_ONLY" method="GET" class="transition-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.order.only.payoff.link" arguments="${approvalEmailId}"/>
										</div>
									</div>
								</div>
							</c:if>
						</div>
					</div>
					
				</c:when>
				<c:otherwise>
					<!--    These are the three Order Buttons for APB.-->
					<div class="row" id="logged-in-user-row">
						<div class="col-xs-12 col-sm-8 col-md-4 col-lg-4">
							<div class="logged-in-user-options">
								<a href="${orderTemplateUrl}" class="btn homepage-loggedin-btn"><spring:theme code="homepage.loggedin.btn.orderfromtemplate" /></a>
							</div>
						</div>
						<div class="col-xs-12 col-sm-8 col-md-4 col-lg-4">
							<div class="logged-in-user-options">
								<a href="${orderHistoryUrl}" class="btn homepage-loggedin-btn"><spring:theme code="homepage.loggedin.btn.orderfromhistory" /></a>
							</div>
						</div>
						<div class="col-xs-12 col-sm-8 col-md-4 col-lg-4">
							<div class="logged-in-user-options">
								<a href="https://online.cub.com.au" target="_blank" class="btn homepage-loggedin-btn"><spring:theme code="homepage.loggedin.btn.kegorders" /></a>
							</div>
						</div>
					</div>
				</c:otherwise>
			</c:choose>
			<!-- SGA Homepage Deals Component -->
			<c:if test="${fn:length(asahiDeals) > 0 && !isNAPGroup}" >
				<div class="sga-homepage-deal">
					<cms:pageSlot position="Section2A" var="feature" element="div" class="row no-margin">
						<cms:component component="${feature}" element="div" class="sga-homepage-deal-component yComponentWrapper" />
					</cms:pageSlot>
				</div>
			</c:if>
	            <div class="content_slots">
	                <div class="promo-slots-content"> 
	                	<c:if test="${cmsPage.uid eq 'homepage'}">
	                    <div class="cs-padding">
	                        <div id="slot-margin1" class="slot-margin">
	                            <div class="row">
	                                <div class="col-xs-12 col-sm-12 col-md-12 col-lg-12 promo-slot-image">
	                                    <cms:pageSlot position="Section7" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                            </div>
	                        </div>
	
	                        <div id="slot-margin2" class="slot-margin">
	                            <div class="row">
	                                <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 promo-slot-image">
	                                    <cms:pageSlot position="Section8" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                                <div class="col-xs-6 col-sm-6 col-md-6 col-lg-6 promo-slot-image">
	                                    <cms:pageSlot position="Section9" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                            </div>
	                        </div>
	
	                        <div id="slot-margin3" class="slot-margin">
	                            <div class="row">
	                                <div class="col-xs-3 col-sm-3 col-md-3 col-lg-3 promo-slot-image">
	                                    <cms:pageSlot position="Section10" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                                <div class="col-xs-9 col-sm-9 col-md-9 col-lg-9 promo-slot-image">
	                                    <cms:pageSlot position="Section11" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                            </div>
	                        </div>
	
	                        <div id="slot-margin4" class="slot-margin">
	                            <div class="row">
	                                <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 promo-slot-image">
	                                    <cms:pageSlot position="Section12" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	
	                                <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 promo-slot-image">
	                                    <cms:pageSlot position="Section13" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                                <div class="col-xs-4 col-sm-4 col-md-4 col-lg-4 promo-slot-image">
	                                    <cms:pageSlot position="Section14" var="feature" element="div">
	                                        <cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
	                                    </cms:pageSlot>
	                                </div>
	                            </div>
	                        </div>
	                    </div> 
	                  </c:if>
	                </div>
	            </div>
           
        </sec:authorize>
    </div>
	
	<c:choose>
		<c:when test="${cmsSite.uid eq 'sga'}">
<!--			This is for SGA Logged Out Homepage			-->
			<sec:authorize access="hasAnyRole('ROLE_ANONYMOUS')" >
				<div id="homepage-info-buttons-desktop">
					<div class="row sga-homepage-info-btns">
						<div class="hidden-xs hidden-sm col-md-7 col-lg-7">
						</div>
						<div class="col-xs-12 col-sm-12 col-md-4 col-lg-4">
							<cms:pageSlot position="TransitionContentSlot" var="feature" element="div">
								<cms:component component="${feature}" element="div" class="no-space yComponentWrapper" />
							</cms:pageSlot>
						</div>
						<div class="hidden-xs hidden-sm col-md-1 col-lg-1">
						</div>
					</div>
				</div>
				<div id="homepage-info-buttons-desktop">
					<div class="row sga-homepage-info-btns">
						<div class="hidden-xs hidden-sm col-md-7 col-lg-7">
						</div>
						<div class="col-xs-12 col-sm-12 col-md-4 col-lg-4">
							<a href="<c:url value='/login'/>" class="btn homepage-info-btn"><spring:theme code="header.link.login.apb" /></a>
						</div>
						<div class="hidden-xs hidden-sm col-md-1 col-lg-1">
						</div>
					</div>
					<div class="row sga-homepage-info-btns">
						<div class="hidden-xs hidden-sm col-md-7 col-lg-7">
						</div>
						<div class="col-xs-12 col-sm-12 col-md-4 col-lg-4">
							<a id="sgaHomepageRegisterLink" href="<c:url value='/register'/>" class="btn homepage-info-btn"><spring:theme code="header.link.registration" /></a>
						</div>
						<div class="hidden-xs hidden-sm col-md-1 col-lg-1">
						</div>
					</div>
				</div>
			</sec:authorize>
		</c:when>
		<c:otherwise>
<!--			This is for APB Logged Out Homepage			-->
			<div id="homepage-info-buttons-desktop" class="js-info-buttons-scroll">
				<div class="mobile-tablet-margin">
				</div>
    		</div>
		</c:otherwise>
	</c:choose>

		<div class="row no-margin">
			<div class="col-xs-12 col-md-6 no-space">
				<%--<cms:pageSlot position="Section2A" var="feature" element="div" class="row no-margin">
					<cms:component component="${feature}" element="div" class="col-xs-12 col-sm-6 no-space yComponentWrapper" />
				</cms:pageSlot>--%>
			</div>
			<div class="col-xs-12 col-md-6 no-space">
				<%--<cms:pageSlot position="Section2B" var="feature" element="div" class="row no-margin">
					<cms:component component="${feature}" element="div" class="col-xs-12 col-sm-6 no-space yComponentWrapper" />
				</cms:pageSlot>--%>
			</div>
			<div class="col-xs-12">
				<cms:pageSlot position="Section2C" var="feature" element="div" class="landingLayout2PageSection2C">
					<cms:component component="${feature}" element="div" class="yComponentWrapper" />
				</cms:pageSlot>
			</div>
		</div>
		
</template:page>