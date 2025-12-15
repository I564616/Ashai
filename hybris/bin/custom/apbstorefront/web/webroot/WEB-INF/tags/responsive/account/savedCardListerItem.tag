<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="card" required="true" type="de.hybris.platform.commercefacades.order.data.CCPaymentInfoData" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<c:url var="removeCardUrl" value="/my-account/saved-cards/remove" scope="page"/>
<c:url var="makeDefaultCardUrl" value="/my-account/saved-cards/default" scope="page"/>
<c:if test="${not empty card}">
	<li class="item__list--item">
			<div class="item__info card_info">
				<span class="hidden-sm hidden-md hidden-lg saved-cards-heading"><spring:theme code="saved.card.number.mobile"/></span> 
				  <span class="item__name">${card.cardNumber}</span>

			</div>
			<div class="item__info">
				<span class="hidden-sm hidden-md hidden-lg saved-cards-heading" ><spring:theme code="saved.card.expiry.date.mobile"/></span>
				<span class="item__name">${card.expiryMonth}/${card.expiryYear}</span>
			</div>
			<div class="item__info">
				<span class="hidden-sm hidden-md hidden-lg saved-cards-heading"><spring:theme code="saved.card.type.mobile"/></span>
				<span class="item__name">${card.cardTypeData.name}</span>
			</div>
			<div class="item__info">
				<span class="hidden-sm hidden-md hidden-lg saved-cards-heading"><spring:theme code="saved.card.name.on.card.mobile"/></span>
				<span class="item__name">${card.accountHolderName}</span>
			</div>
			<div class="item__info">
				<span class="item__name">
				<div class="row">
					<div class="col-xs-12 col-md-12 saved-make-default">
					<c:choose>
						<c:when test="${card.defaultPaymentInfo}">
							<spring:theme code="saved.card.text.default" />
						</c:when>
						<c:otherwise>
							<form:form action="${makeDefaultCardUrl}" class="defaultCardForm" modelAttribute="cardInfo">
								<input type="hidden" name="id" value="${card.id}" />
								
									<button type="submit" class="btn btn-primary btn-block"><spring:theme code="saved.card.button.make.default" /></button>
								
							</form:form>
						</c:otherwise>
					</c:choose>
					</div>
					</div>
				</span>
				
				
			</div>
			<div class="item__info">
				<form:form action="${removeCardUrl}" class="removeCardForm" modelAttribute="removeCardForm">
					<input type="hidden" name="id" value="${card.id}" />
						<div class="saved-cards-remove"><button type="submit" class="btn btn-link">
							<spring:theme code="saved.card.link.remove" /></button>
						</div>
						
				</form:form>
				<div class="clearfix"></div>
				
			</div>
		
	</li>
</c:if>