<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="user" tagdir="/WEB-INF/tags/responsive/user"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>


<template:page pageTitle="${pageTitle}">
    <c:url value="/contactus/send" var="contactusSendUrl" />
    <form:form modelAttribute="apbContactUsForm" method="post" enctype="multipart/form-data" action="${contactusSendUrl}">
	<div class="form-container">

<!-- ######################################## CONTACT US HEADING ############################################# -->
		<div id = "globalErrorMessage" class="alert alert-danger" style="display:none">
			<spring:theme code="contactus.loginerror.message" />
		</div>


		<div class="login-section">
			<div class="login-page__headline">
				<spring:theme code="contactus.heading" />
			</div>
		</div>

<!-- ######################################## VARIABLES ############################################# -->
		<c:set value="${false}" var="contactUsIsLoggedIn"/>
		<c:set value="" var="deliveryNumber"/>
		<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
			<c:set value="${true}" var="contactUsIsLoggedIn"/>
		</sec:authorize>

<!-- ######################################## SCRIPT ############################################# -->
		<script>
			var contactUsIsLoggedIn = ${contactUsIsLoggedIn};
		</script>

<!-- ######################################## UPDATED PAGE ############################################# -->

		<c:if test="${contactusUpdateAvailable && cmsSite.uid eq 'sga'}">
			<div class="row contact-us-details no-margin">				
					<!---- 'Send us a message' header ---->
						<!---- Contact Us page landing information ---->
							
							<div class="contact-us-padding-bottom">
								<spring:theme code="contactus.landing.message" />
							</div>

							<!---- What can we help you with? ---->

							<div class="contact-us-padding-bottom">
								<spring:theme code="contactus.helptype.header" />
							</div>

							<!---- Menu option row 1 ---->
								<div id="menuoption1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options">
									<button id="menuoption1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button" >
										<span id="website-menu-icon" class="input-group-addon">
											<img class="login-inputs website-support-icon" src="/storefront/_ui/responsive/common/images/website.png"  />
										</span>
										<span class="website-support-wrapper">
											<spring:theme code="contactus.websitesupport.button" /></span>
									</button>	
									<div class="log-in-error">
										<span class="login-error-text"  style="display:none"><spring:theme code="contactus.loginprompt.message"/></span>
									</div>
								</div>
								<div id="menuoption2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option">
									<button id="menuoption2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="delivery-menu-icon" class="input-group-addon">
											<img class="login-inputs delivery-support-icon" src="/storefront/_ui/responsive/common/images/delivery.png"  />
										</span>
										<span class="delivery-support-wrapper">
											<spring:theme code="contactus.deliveryissue.button" />
										</span>
									</button>
								</div>
								<div id="menuoption3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right">
									<button id="menuoption3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="account-menu-icon" class="input-group-addon">
											<img class="login-inputs account-support-icon" src="/storefront/_ui/responsive/common/images/account.png"  /></span>
										<span class="account-support-wrapper">
											<spring:theme code="contactus.manageaccount.button"/>
										</span>
									</button>	
								</div>

							<!---- Menu option row 2 ---->

								<div id="menuoption4" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options">
									<button id="menuoption4button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="registration-menu-icon" class="input-group-addon">
											<img class="login-inputs registration-support-icon" src="/storefront/_ui/responsive/common/images/addUser.png"  /></span>
										<span class="registration-support-wrapper">
											<spring:theme code="contactus.registrationsupport.button" />
										</span>

									</button>
								</div>

								<div id="menuoption5" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option">
									<button id="menuoption5button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="amend-menu-icon" class="input-group-addon">
											<img class="login-inputs amend-support-icon" src="/storefront/_ui/responsive/common/images/amend.png"  /></span>
										<span class="amend-support-wrapper">
											<spring:theme code="contactus.amendorder.button" />

										</span>
									</button>	
								</div>
								<div id="menuoption6" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right">
									<button id="menuoption6button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="incorrect-menu-icon" class="input-group-addon">
											<img class="login-inputs incorrect-support-icon" src="/storefront/_ui/responsive/common/images/invoice.png"  /></span>
										<span class="incorrect-support-wrapper">
											<spring:theme code="contactus.incorrectcharge.button" />
										</span>
									</button>
									
									<span id="menuoption6login" class="error-possibility" style="display:none">
										<spring:theme code="contactus.menuerror.message" />
									</span>
								</div>

							<!---- Menu option row 3 ---->

								<div id="menuoption7" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options">
									<button id="menuoption7button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button" style="text-align:left">
										<span id="assistance-menu-icon" class="input-group-addon">
											<img class="login-inputs assistance-support-icon" src="/storefront/_ui/responsive/common/images/assistancelogo.png"  /></span>
										<span class="assistance-support-wrapper">
											<spring:theme code="contactus.assistancewithproducts.button" />
										</span>
									</button>	
								</div>
								<div id="menuoption8" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option">
									<button id="menuoption8button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-button" type="button">
										<span id="other-menu-icon" class="input-group-addon">
											<img class="login-inputs other-support-icon" src="/storefront/_ui/responsive/common/images/other.png"  /></span>
										<span class="other-support-wrapper">
											<spring:theme code="contactus.other.button" />
										</span>
									</button>	
								</div>

							<!---- Change menu option ---->
							<div class="menu-option-padding" id="menureselect" style="display:none">
								<button type="button" id="menureselectbutton" class="change-menu-button">
									<spring:theme code="contactus.changeselection.button" />
								</button>	
							</div>

							<!---- Select submenu option ---->
							<div class="contact-us-padding-bottom" id="submenuselectmessage" style="display:none">
								<spring:theme code="contactus.subtype.header" />
							</div>

							<!---- Submenu 1: sub-menu option row 1 ---->

								<div id="menu1sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu1sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.loginissue.button" />
									</button>	
								</div>

								<div id="menu1sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
									<button id="menu1sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.orderingissue.button" />
									</button>	
								</div>

								<div id="menu1sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
									<button id="menu1sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.paymentissue.button" />
									</button>	
								</div>

							<!---- Submenu 1: sub-menu option row 2 ---->

								<div id="menu1sub4" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu1sub4button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.suggestionsfeedback.button" />
									</button>	
								</div>

								<div id="menu1sub5" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
									<button id="menu1sub5button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.other.button" />
									</button>	
								</div>

							<!---- Submenu 2: sub-menu option row 1 ---->
								<div id="menu2sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu2sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.damagedproducts.button" />
									</button>	
									
									<span id="menu2sub1login" class="error-possibility" style="display:none">
										<spring:theme code="contactus.menuerror.message" />
									</span>

								</div>

								<div id="menu2sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
									<button id="menu2sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.incorrectproducts.button" />
									</button>	
									
									<span id="menu2sub2login" class="error-possibility" style="display:none">
										<spring:theme code="contactus.menuerror.message" />
									</span>

								</div>

								<div id="menu2sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
									<button id="menu2sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.wrongquantity.button" />
									</button>	
									
									<span id="menu2sub3login" class="error-possibility" style="display:none">
										<spring:theme code="contactus.menuerror.message" />
									</span>

								</div>
							<!---- Submenu 2: sub-menu option row 2 ---->
							<div id="menu2submenu2">

								<div id="menu2sub4" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu2sub4button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.qualityissue.button" />
									</button>	
								</div>

								<div id="menu2sub5" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
									<button id="menu2sub5button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.ordereta.button" />
									</button>	
								</div>

								<div id="menu2sub6" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
									<button id="menu2sub6button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.other.button" />
									</button>	
								</div>

							</div>
							<!---- Submenu 3: sub-menu option row 1 ---->
							
							<div id="menu3sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu3sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.accountenquiry.button" />
									</button>	
							</div>

							<div id="menu3sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
								<button id="menu3sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.accountdetails.button" />
								</button>	
							</div>
							
							<div id="menu3sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
								<button id="menu3sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.deactivateaccount.button" />
								</button>	
							</div>


							<!---- Submenu 3: sub-menu option row 2 ---->
							<!-- <div id="menu3submenu2"> -->
								<div id="menu3sub4" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
									<button id="menu3sub4button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.requestpayment.button" />
									</button>	

									<span id="menu3sub4login" class="error-possibility" style="display:none">
										<spring:theme code="contactus.menuerror.message" />
									</span>

								</div>

								<div id="menu3sub5" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
									<button id="menu3sub5button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
										<spring:theme code="contactus.other.button" />
									</button>	
								</div>
							<!-- </div> -->

							<!---- Submenu 4: sub-menu option row 1 ---->

							<div id="menu4sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
								<button id="menu4sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.lifestylebeverages.button" />
								</button>	
							</div>

							<div id="menu4sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
								<button id="menu4sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.createaccount.button" />
								</button>	
							</div>
							<div id="menu4sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
								<button id="menu4sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.other.button" />
								</button>	
							</div>

							<!---- Submenu 5: sub-menu option row 1 ---->

							<div id="menu5sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
								<button id="menu5sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.changeorder.button" />
								</button>	
							</div>

							<div id="menu5sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
								<button id="menu5sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.cancelorder.button" />
								</button>	
							</div>
							<div id="menu5sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
								<button id="menu5sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.other.button" />
								</button>	
							</div>


							<!---- Submenu 7: sub-menu option row 1 ---->

							<div id="menu7sub1" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options" style="display:none">
								<button id="menu7sub1button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.productinformation.button" />
								</button>	
							</div>

							<div id="menu7sub2" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options mid-option" style="display:none">
								<button id="menu7sub2button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.stockavailability.button" />
								</button>	
							</div>
							<div id="menu7sub3" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-menu-options menu-button-right" style="display:none">
								<button id="menu7sub3button" class="col-lg-3 col-md-3 col-sm-6 col-xs-12 contact-us-sub-menu-button" type="button" >
									<spring:theme code="contactus.other.button" />
								</button>	
							</div>

							<!---- Change menu subtype option ---->
							<div class="menu-option-padding" id="subtypereselect" style="display:none">
								<button type="button" id="subtypereselectbutton" class="change-menu-button">
									<spring:theme code="contactus.changeselection.button" />
								</button>	
							</div>
						
						<div id="sendMessageHeader" class="discrepancy_sendus_subheading row-margin-fix" style="display:none">
							<spring:theme code="contactus.discrepancy.header" />
						</div>
			</div>
			
			<div id = "defaultcontactform" style="display:none">
				<user:contactus actionNameKey="" action="${contactUsURL}"/>
			</div>
			</div>  
			</div>
		</c:if>


<!-- ######################################## DEFAULT PAGE ############################################# -->

		<c:if test="${!contactusUpdateAvailable || cmsSite.uid ne 'sga'}">

			<div class="row contact-us-details no-margin">
				<div class="col-sm-12 col-md-12 no-padding customer-service">
					<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
						<c:choose>
							<c:when test="${cmsSite.uid eq 'sga' && not empty salesRepName && not empty salesRepEmailID}">
								<div class="col-sm-4 col-md-4 padding-fix-left-right-20">
									<div class="customer-service-content-heading">
										<spring:theme code="sga.contactus.your.sales.rep.headline" />
									</div>
									<div class="customer-service-details">

										<c:if test="${not empty salesRepName}">
											<form:hidden path="asahiSalesRepName" value="${salesRepName}"/>
											<span class="customer-service-content-subheading"><spring:theme  code="sga.contactus.area.sales.manager.headline"/></span>
											<span class="customer-service-content textword-wrap">${salesRepName}</span> <br>
										</c:if>

										<c:if test="${not empty salesRepEmailID}">
												<form:hidden path="asahiSalesRepEmail" value="${salesRepEmailID}"/>
											<span class="customer-service-content-subheading"><spring:theme  code="contactus.area.sales.email.headline"/></span>
											<span class="customer-service-content textword-wrap">${salesRepEmailID}</span><br>
										</c:if>

										 <c:if test="${not empty salesRepPhone}">
											<span class="customer-service-content-subheading"><spring:theme  code="contactus.area.sales.phone.headline"/></span>
											<span class="customer-service-content textword-wrap">${salesRepPhone}</span>
										</c:if>
									</div>
								</div>
							</c:when>
							<c:otherwise>
								<c:if test="${asahiSalesRepData.showSalesRep && asahiSalesRepData.activeSalesRep}">
									<form:hidden path="showSalesRep" value="${asahiSalesRepData.showSalesRep}"/>
									<form:hidden path="activeSalesRep" value="${asahiSalesRepData.activeSalesRep}"/>
									<div class="col-sm-4 col-md-4 padding-fix-left-right-20">
										<div class="customer-service-content-heading">
											<spring:theme code="contactus.your.sales.rep.headline" />
										</div>
										<div class="customer-service-details">

											<c:if test="${not empty asahiSalesRepData.name}">
													<form:hidden path="asahiSalesRepName" value="${asahiSalesRepData.name}"/>
												<span class="customer-service-content-subheading"><spring:theme  code="contactus.area.sales.manager.headline"/></span>
												<span class="customer-service-content textword-wrap">${asahiSalesRepData.name}</span> <br>
											</c:if>

											<c:if test="${not empty asahiSalesRepData.contactNumber}">
												<span class="customer-service-content-subheading"><spring:theme  code="contactus.area.sales.phone.headline"/></span>
												<span class="customer-service-content">${asahiSalesRepData.contactNumber}</span> <br>
											</c:if>

											<c:if test="${not empty asahiSalesRepData.emailAddress}">
													<form:hidden path="asahiSalesRepEmail" value="${asahiSalesRepData.emailAddress}"/>
												<span class="customer-service-content-subheading"><spring:theme  code="contactus.area.sales.email.headline"/></span>
												<span class="customer-service-content textword-wrap">${asahiSalesRepData.emailAddress}</span>
											</c:if>

										</div>
									</div>
								</c:if>
							</c:otherwise>
						</c:choose>
					</sec:authorize>

					<div class="col-sm-4 col-md-4 padding-fix-left-right-20">
						<cms:pageSlot position="TopContent" var="feature" element="div" class="">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
					<div class="col-sm-4 col-md-4 padding-fix-left-right-20">
						 <cms:pageSlot position="BodyContent" var="feature" element="div" class="">
							<cms:component component="${feature}" />
						</cms:pageSlot>
					</div>
				</div>
			</div>
			<div id = "defaultcontactform">
					<user:contactusDefault actionNameKey="" action="${contactUsURL}"/>
			</div>
			</div>


		</c:if>

<!-- ######################################## END OF FORM SECTION ############################################# -->


</form:form>

<c:if test="${contactusUpdateAvailable && cmsSite.uid eq 'sga'}">

	<div class="container" id="accFooter" style="display:none">
		<div class="row contact_container">
			<div class="contact_row col-xs-12 col-sm-12 col-md-12 customer-service">
				<div class="col-xs-12 col-sm-6 col-md-6 footer-align">
					<div class="">
						<div class="content">
							<div class="customer-service-content-heading callback-header-footer">Asahi Contact Centre
							</div>
								<div class="customer-service-details first-message-footer">
									<span class="customer-service-content-subheading">Phone:</span>
										<span class="customer-service-content"><a href="tel:1300127244" class="inline-block">1300 127 244</a> <span class="inline-block">(Mon - Fri, 9:00am to 6:00pm AEST)</span></span>
										<br>
										<span class="customer-service-content-subheading">Email:</span>
										<span class="customer-service-content"><a href="mailto:accconnect@asahi.com.au">ACCConnect@asahi.com.au</a></span>
										<br>
									</div>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-6 col-md-6" id="equipmentContactUs">
							<div class="">
								<div class="content">
									<div class="customer-service-content-heading callback-">Equipment &amp; Technical Services
									</div>
									<div class="customer-service-details second-message-footer">									<span class="customer-service-content-subheading">Phone:</span>
											<span class="customer-service-content"><a href="tel:1300364246" class="inline-block">1300 364 246</a> <span class="inline-block">(Open 24/7)</span></span>
											<br>
										</div>
									</div>
								</div>
							</div>
						</div>
	</div>
	</div>

	<div class="container" id="cicFooter" style="display:none">
		<div class="row contact_container">
			<div class="contact_row col-xs-12 col-sm-12 col-md-12 customer-service">
				<div class="col-xs-12 col-sm-6 col-md-6 footer-align">
					<div class="">
						<div class="content">
							<div class="customer-service-content-heading callback-header-footer">Asahi Contact Centre
							</div>
								<div class="customer-service-details first-message-footer">
									<span class="customer-service-content-subheading">Phone:</span>
										<span class="customer-service-content"><a href="tel:1300127244" class="inline-block">1300 127 244</a> <span class="inline-block">(Mon - Fri, 9:00am to 6:00pm AEST)</span></span>
										<br>
										<span class="customer-service-content-subheading">Email:</span>
										<span class="customer-service-content"><a href="mailto:CICAdmin@asahi.com.au">CICAdmin@asahi.com.au</a>
										</span>
								</div>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-6 col-md-6">
							<div class="">
								<div class="content">
									<div class="customer-service-content-heading">Equipment &amp; Technical Services
									</div>
									<div class="customer-service-details second-message-footer">
										<span class="customer-service-content-subheading">Phone:</span>
											<span class="customer-service-content"><a href="tel:1300364246" class="inline-block">1300 364 246</a> <span class="inline-block">(Open 24/7)</span></span>
											<br>
										</div>
									</div>
								</div>
							</div>
						</div>
	</div>
	</div>

    <div class="container" id="productFooter" style="display:none">
        <div class="row contact_container">
            <div class="contact_row col-xs-12 col-sm-12 col-md-12 customer-service">
                <div class="col-xs-12 col-sm-6 col-md-6 footer-align">
                    <div class="">
                        <div class="content">
                            <div class="customer-service-content-heading callback-header-footer">Asahi Contact Centre
                            </div>
                                <div class="customer-service-details first-message-footer">
                                    <span class="customer-service-content-subheading">Phone:</span>
                                        <span class="customer-service-content"><a href="tel:1300127244" class="inline-block">1300 127 244</a> <span class="inline-block">(Mon - Fri, 9:00am to 6:00pm AEST)</span></span>
                                        <br>
                                        <span class="customer-service-content-subheading">Email:</span>
                                        <span class="customer-service-content"><a href="mailto:ACCConnect@asahi.com.au">ACCConnect@asahi.com.au</a>
                                        </span>
                                </div>
                                </div>
                            </div>
                        </div>
                        <div class="col-xs-12 col-sm-6 col-md-6">
                            <div class="">
                                <div class="content">
                                    <div class="customer-service-content-heading">Equipment &amp; Technical Services
                                    </div>
                                    <div class="customer-service-details second-message-footer">
                                        <span class="customer-service-content-subheading">Phone:</span>
                                            <span class="customer-service-content"><a href="tel:1300364246" class="inline-block">1300 364 246</a> <span class="inline-block">(Open 24/7)</span></span>
                                            <br>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
    </div>
    </div>

	<div class="container" id="consumerFooter" style="display:none">
		<div class="row contact_container">
			<div class="contact_row col-xs-12 col-sm-12 col-md-12 customer-service">
				<div class="col-xs-12 col-sm-6 col-md-6 footer-align">
					<div class="">
						<div class="content">
							<div class="customer-service-content-heading callback-header-footer">Consumer Relations
							</div>
								<div class="customer-service-details first-message-footer">
									<span class="customer-service-content-subheading">Phone:</span>
										<span class="customer-service-content"><a href="tel:1800244054" class="inline-block">1800 244 054</a> <span class="inline-block">(Mon - Fri, 9:00am to 6:00pm AEST)</span></span>
										<br>
										<span class="customer-service-content-subheading">Email:</span>
										<span class="customer-service-content"><a href="mailto:consumerRelations@asahi.com.au">consumerRelations@asahi.com.au</a>
										</span>
								</div>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-6 col-md-6">
							<div class="">
								<div class="content">
									<div class="customer-service-content-heading">Equipment &amp; Technical Services
									</div>
									<div class="customer-service-details second-message-footer">
										<span class="customer-service-content-subheading">Phone:</span>
											<span class="customer-service-content"><a href="tel:1300364246" class="inline-block">1300 364 246</a> <span class="inline-block">(Open 24/7)</span></span>
											<br>
										</div>
									</div>
								</div>
							</div>
						</div>
	</div>
	</div>

	<div class="container" id="adminFooter" style="display:none">
		<div class="row contact_container">
			<div class="contact_row col-xs-12 col-sm-12 col-md-12 customer-service">
				<div class="col-xs-12 col-sm-6 col-md-6 footer-align">
					<div class="">
						<div class="content">
							<div class="customer-service-content-heading callback-header-footer">Asahi Credit Team
							</div>
								<div class="customer-service-details first-message-footer">
									<span class="customer-service-content-subheading">Phone:</span>
										<span class="customer-service-content"><a href="tel:1300127244" class="inline-block">1300 127 244</a> <span class="inline-block">(Mon - Fri, 9:00am to 6:00pm AEST )</span></span>
										<br>
										<span class="customer-service-content-subheading">Email:</span>
										<span class="customer-service-content"><a href="mailto:adminfinance@asahi.com.au">adminfinance@asahi.com.au</a>
										</span>
								</div>
								</div>
							</div>
						</div>
						<div class="col-xs-12 col-sm-6 col-md-6">
							<div class="">
								<div class="content">
									<div class="customer-service-content-heading">Equipment &amp; Technical Services
									</div>
									<div class="customer-service-details second-message-footer">
										<span class="customer-service-content-subheading">Phone:</span>
											<span class="customer-service-content"><a href="tel:1300364246" class="inline-block">1300 364 246</a> <span class="inline-block">(Open 24/7)</span></span>
											<br>
										</div>
									</div>
								</div>
							</div>
						</div>
	</div>
	</div>

</c:if>
</template:page>