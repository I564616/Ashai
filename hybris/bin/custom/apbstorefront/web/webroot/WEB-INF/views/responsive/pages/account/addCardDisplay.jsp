<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>
<%@ taglib prefix="single-checkout" tagdir="/WEB-INF/tags/responsive/checkout/single"%>

<spring:url value="https://paynow-sandbox.pmnts.io/v2/TESTAsahi/Test101/AUD/10.00/fe833e46a8a55369ba2b4f022d63ba48?iframe=true&show_extras=false&show_email=false&l=v2&return_target=_self&tokenize_only=true&postmessage=true&hide_button=true" var="iFrameUrl" htmlEscape="false"/>
<spring:url value="/my-account/saved-cards" var="savedCardsUrl" htmlEscape="false"/>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-section-header user-register__headline secondary-page-title">
	<spring:theme code="saved.card.link.add.card" />
</div>

<div id="addCreditCardSection">
	<c:if test="${not empty allowAddCart and allowAddCart != true }">
		<div>
			<spring:theme code="saved.card.max.limit.reached" arguments="${maxAllowed}" />
		</div>
	</c:if>
	<c:if test="${empty allowAddCart || allowAddCart == true}">
		<p class="add-card-complete-field"><spring:theme code="saved.card.complete.fields" /></p>
		<input type="hidden" name="addCardUrl" value="${contextPath}/my-account/saved-cards/create">	
	   		<div>
	   		 <iframe id="addCardIframe" class="iframe-content" name="my_iframe" src="${addCardUrl}" frameBorder="0"></iframe>
	   		 </div> 
	   	<div class="saved-card-btn">
		  	<div class="row">
				<div class="col-xs-12 col-sm-6 col-md-3">
					<single-checkout:securityCode/>
				</div>
				<div class="col-xs-12 col-sm-12 col-md-12">
					<input id="setDefaultCard" type="checkbox"><label for="setDefaultCard"><spring:theme code="saved.card.make.default" /></label>
		  			
		  		</div>
		  		<div class="col-xs-12 col-sm-6 col-md-3">
		  			<button class="btn btn-primary btn-vd-primary btn-block" id="addCCBtn"><spring:theme code="saved.card.add.details.link" /></button>
		  		</div>
			</div>
		</div>
	</c:if>
  	<div class="accountActions-bottom">
  		<a href="${savedCardsUrl}"><spring:theme code="saved.card.saved.cards.link" /></a>
  	</div>
  	<input type="hidden" value="${themeResourcePath}" id="cssPathIframe"/>
</div>
