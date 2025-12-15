<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="sec"
	uri="http://www.springframework.org/security/tags"%>

<c:set var="context" value="${pageContext.request.contextPath}" />

<template:page pageTitle="${pageTitle}">

<c:url value=""/>
	<c:choose>
		<c:when test="${selfRegistration}">
			<div class="keg-returns-section">
				<div class="login-section">
					<div class="login-page__headline">
						<spring:theme code="register.confirmation.success.message" />
					</div>
				</div>
				<div class="user-register__body">
					<spring:theme code="register.confirmation.dear.message" arguments="${firstName}" />
				</div>
				<br>
				<div class="user-register__body">
					<c:choose>
						<c:when test="${cmsSite.uid eq 'sga'}">
							<c:if test="${accessType eq 'ORDER_ONLY'}">
								<p>Welcome to ALB Connect - the new home of Schweppes Connect.
									<br><br> 
									Now that you're registered, you can: 
									<br>
									<ul class="registerContent">
										<li>Quickly and conveniently place orders</li>
										<li>View your order history</li>
										<li>Manage your account and</li>
										<li>Update your details</li>
									</ul>
									<br>                              
									<span> Should you need further support call us on <strong>1300 127 244</strong>, (9am - 6pm, Monday - Friday). Alternatively, you can call your Territory Manager.</span>
									<br><br>
									<span>We hope you enjoy using ALB Connect.</span>
									<br><br>
									<span>Cheers,
									<br><br>Asahi Lifestyle Beverages (formerly Schweppes Australia)</span>                  
									<br><br>              
								</p>
							</c:if>
							<c:if test="${accessType eq 'PAY_ONLY'}">
								<p>Welcome to ALB Connect - the new home of Schweppes Connect.
									<br><br> 
									<span>An email has been sent to <b>${approvalEmailId}</b> (the email address we have on our records) to get your statements and invoices access up and running.
									<br>Once this has been approved, you will be able to make payments for ${tradingName}.</span>
									<br><br>                              
									<span> If this mailbox is unattended, please let us know by using our <a class="site-anchor-link" href="${context}/contactus">contact form</a> so we can help you get set up.</span>
									<br><br>
									<span>Should you need further support call us on 1300 127 244, (9am - 6pm Monday - Friday). Alternatively, you can call your Territory Manager.</span>
									<br><br>
									<span>We hope you enjoy using ALB Connect.</span>
									<br><br>
									<span> Cheers,
									<br> <br>Asahi Lifestyle Beverages (formerly Schweppes Australia)
									</span>                  
									<br><br>                
								</p>
							</c:if>
							<c:if test="${accessType eq 'PAY_AND_ORDER'}">
								<p>Welcome to ALB Connect - the new home of Schweppes Connect.
									<br><br> 
									Now that you're registered, you can: 
									<br>
									<ul class="registerContent">
										<li>Quickly and conveniently place orders</li>
										<li>View your order history</li>
										<li>Manage your account and</li>
										<li>Update your details</li>
									</ul>
									<br>
									<span>An email has been sent to <b>${approvalEmailId}</b> (the email address we have on our records) to get your statements and invoices access up and running.
									<br>Once this has been approved, you will be able to make payments for ${tradingName}.</span>
									<br><br>                              
									<span> If this mailbox is unattended, please let us know by using our <a class="site-anchor-link" href="${context}/contactus">contact form</a> so we can help you get set up.</span>
									<br><br>
									<span>Should you need further support call us on 1300 127 244, (9am - 6pm Monday - Friday). Alternatively, you can call your Territory Manager.</span>
									<br><br>
									<span>We hope you enjoy the ALB Connect experience.</span>
									<br><br>
									<span> Cheers,
									<br> <br>Asahi Lifestyle Beverages (formerly Schweppes Australia)
									</span>                  
									<br><br>                
								</p>
							</c:if>
						</c:when>
						<c:otherwise>
							<cms:pageSlot position="Section1" var="feature">
			                <cms:component component="${feature}"/>
			       	 	</cms:pageSlot>
						</c:otherwise>
					</c:choose>
					
		       </div>
			</div>
		</c:when>
		<c:otherwise>
			<div class="keg-returns-section">
				<div class="login-section">
					<div class="login-page__headline">
						<spring:theme code="request.registration.reference.number.message1" />
					</div>
				</div>
				<div class="user-register__body">
					<spring:theme code="request.registration.reference.number.message2" />
				</div>
				<br>
				<div class="user-register__body">
					<spring:theme code="request.registration.reference.number.message3" />
				</div>
				
				<div class="accountActions-bottom">
					<a href="${request.contextPath}" class="button positive right"><spring:theme code="checkout.orderConfirmation.continueShopping" /></a>
				</div>
			</div>
		</c:otherwise>
	</c:choose>
</template:page>
