<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>

<spring:url value="https://paynow.sandbox.fatzebra.com.au/v2/TESTAsahi/5496/AUD/100.00/dd18e9570f9362ebd2ec54326cd0a786?iframe=true&show_extras=false&show_email=false&l=v2&return_target=_self&tokenize_only=true&postmessage=true&hide_button=true" var="iFrameUrl" htmlEscape="false"/>
<spring:url value="/my-account/saved-cards/add" var="addCardUrl" htmlEscape="false"/>

<spring:htmlEscape defaultHtmlEscape="true" />
<div class="account-section-header user-register__headline secondary-page-title">
	<spring:theme code="text.account.savedcards" />
</div>
<c:if test="${not empty paymentInfo.paymentInfos}">
	<div class="saved-cards-section">
		<ul class="item__list">
		    <li class="hidden-xs">
		        <ul class="item__list--header">
		            <li class="item__number checkout__page__li__item" id="card_item_number"><spring:theme code="saved.card.number"/></li>
		            <li class="item__exp_date checkout__page__li__item" id="card_item_exp_date"><spring:theme code="saved.card.expiry.date"/></li>
		            <li class="item__type checkout__page__li__item" id="card_item_type"><spring:theme code="saved.card.type"/></li>
		            <li class="item__name checkout__page__li__item" id="card_item_name"><spring:theme code="saved.card.name.on.card"/></li>
		        	<li class="checkout__page__li__item"></li>
		        	<li class="checkout__page__li__item"></li>
		        </ul> 
			</li>
			<table id="checkout_table">
				<c:forEach items="${paymentInfo.paymentInfos}" var="card" varStatus="loop">
					<account:savedCardListerItem card="${card}"/>
			    </c:forEach>
		    </table>
		</ul>
	</div>
</c:if>
<c:if test="${empty paymentInfo.paymentInfos}">
<div class="saved-card-empty">
	<spring:theme code="saved.card.not.available.text"/>
	</div>
	
</c:if>
<div class="row">
	<div class="col-xs-12 col-sm-6 col-md-3">
	<a href="${addCardUrl}" class="btn btn-primary addCreditCardbtn btn-block btn-vd-primary" <c:if test="${allowAddCart == false}"><c:out value="disabled=disabled"/></c:if> ><spring:theme code="saved.card.link.add.card" /></a>
	</div>
</div>
<div class="row">
	<c:if test="${addCardWarning == true}">
		<div id="addCardErrorMsg" class="col-xs-12 col-sm-6 col-md-6">
			<spring:theme code="saved.card.max.limit.reached" arguments="${maxAllowed}"/>
		</div>
	</c:if>
</div>


