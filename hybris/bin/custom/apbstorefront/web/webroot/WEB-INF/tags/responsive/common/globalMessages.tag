<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring"  uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<script type="text/javascript">
  var zeroQtyErr="<spring:theme code="basket.page.message.zero.quantity.not.allowed"/>";
</script>
<input type="hidden" name="fetchPriceUrl" value="${request.contextPath}/integration/price" >
<input type="hidden" name="showPriceOnPLP" value="${isPriceFetch}" >
<input type="hidden" name="showHomepageCreditBlock" value="<spring:theme code="homepage.credit.block"/>" >
<input type="hidden" name="showErrorHomepageCreditBlock" value="<spring:theme code="homepage.error.credit.block"/>" >
<input type="hidden" name="textForNoPrice" value="${textForNoPrice}" />
<input type="hidden" name="accessType" value="${sgaAccessType}" />
<input type="hidden" name="isApprovalPending" value="${isApprovalPending}" />
<input type="hidden" name="isAccessDenied" value="${isAccessDenied}" />
<input type="hidden" name="isNAPGroup" value="${isNAPGroup}">

<sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
    <div id="order-only-pending-block" class="hidden"><spring:theme code="sga.order.only.user.pending.credit.block.message" arguments="${approvalEmailId}" /></div>
    <div id="order-and-pay-credit-block" class="hidden"><spring:theme code="sga.order.and.pay.user.credit.block.message" arguments="${approvalEmailId}" /></div>
	<div id="close-to-block-errors" class="hide">
		<div id="order-and-pay">
			<spring:theme code="sga.order.and.pay.user.close.to.block.message" />
		</div>
		<div id="order-only">
			<spring:theme code="sga.order.only.user.close.to.block.message" />
		</div>
		<div id="order-only-pending">
			<spring:theme code="sga.order.only.user.pending.close.to.block.message" arguments="${approvalEmailId}" />
		</div>
		<div id="nap-user">
			<spring:theme code="sga.close.to.block.is.national.account" />
		</div>
	</div>
</sec:authorize>


<div class="global-alerts">
<c:if test="${(not empty accConfMsgs) || (not empty accInfoMsgs) || (not empty accErrorMsgs) || (not empty accDeliveryMsgs)}">
		<%-- Information (confirmation) messages --%>
		<c:if test="${not empty accConfMsgs}">
			<c:forEach items="${accConfMsgs}" var="msg">
				<div class="save-cart-success">
                  <!--<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>  Don't need this button as per the requirements. -->
					<c:choose>
						<c:when test="${fn:contains(msg.code, 'request.registration.reference.number.message')}">
							<c:set var = "referenceNo" value = "${fn:split(msg.code, '_')}" />
						   <spring:theme code="${referenceNo[0]}" arguments="${msg.attributes}"/>
						   <%-- <strong><c:out value="${referenceNo[1]}"/></strong> --%>
						</c:when>
						<c:otherwise>
							<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
						</c:otherwise>
					</c:choose>
				</div>
			</c:forEach>
		</c:if>

		<%-- Warning messages --%>

		<c:if test="${not empty accDeliveryMsgs}">
			<c:forEach items="${accDeliveryMsgs}" var="msg">
				<div class="alert alert-warning alert-dismissable cart-page-alert">
                 <!--<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>    Don't need this button as per the requirements. -->
		 			<img src="${commonResourcePath}/images/deliverySurcharge.png"  />  &nbsp; <spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
		 			
				</div>
			</c:forEach>
		</c:if>
		
		<c:if test="${not empty accInfoMsgs}">
			<c:forEach items="${accInfoMsgs}" var="msg">
		 			<c:choose>
						<c:when test="${fn:contains(msg.code, 'account.confirmation.signout.title')}">
							<div class="asahi-logout-msg">
								<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
							</div>
						</c:when>
						<c:when test="${fn:contains(msg.code, 'sga.checkout.exclusion.error.message')}">
							<div id="unavailProducts" class="alert alert-warning alert-dismissable">
								<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
							</div>
						</c:when>
						<c:when test="${fn:contains(msg.code, 'sga.checkout.outofstock.error.message')}">
							<div id="unavailProducts" class="alert alert-warning alert-dismissable">
								<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
							</div>
						</c:when>
						<c:otherwise>
						<div class="alert alert-warning alert-dismissable">
							<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
								</div>
						</c:otherwise>
					</c:choose>
			</c:forEach>
		</c:if>
		
		<%-- Error messages (includes spring validation messages)--%>
		<c:if test="${not empty accErrorMsgs}">
			<c:forEach items="${accErrorMsgs}" var="msg">
				<c:choose>
                 <c:when test="${cmsPage.uid eq 'paymentdetail' || cmsPage.uid eq 'checkoutPage' || cmsPage.uid eq 'cartPage' || cmsPage.uid eq 'productDetails' || cmsPage.uid eq 'emptyCart' || cmsPage.uid eq 'productGrid' || cmsPage.uid eq 'searchGrid' || cmsPage.uid eq 'searchEmpty' || cmsPage.uid eq 'order'|| cmsPage.uid eq 'savedCartDetailsPage' || cmsPage.uid eq 'quickOrderPage'}">
					<c:choose>
						<c:when test="${msg.code eq 'sga.user.credit.block.message' || msg.code eq 'sga.order.and.pay.user.credit.block.message' || msg.code eq 'sga.pay.only.user.credit.block.message' || msg.code eq 'sga.order.only.user.credit.block.message'}">
							<div class="alert alert-warning alert-dismissable alert-interface-error" id="creditBlockError">
								<div class="credit-block-error-container">
									<div class="glyphicon glyphicon-alert pull-left credit-block-error-glyphicon"></div>
									<spring:theme code="${msg.code}"/>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<div <c:if test="${msg.code eq 'sga.allunavailable.error.message' || msg.code eq 'sga.allunavailable.error.message' || msg.code eq 'sga.user.credit.block.message' || msg.code eq 'sga.pdp.unavailable.text' || msg.code eq 'inclusion.product.not.found' || msg.code eq 'sga.cart.outofstock.error.message'}">id="unavailProducts"</c:if> class="alert alert-warning alert-dismissable <c:if test="${msg.code eq 'asahi.payment.failed.message'}">alert-card-error</c:if><c:if test="${cmsPage.uid eq 'checkoutPage'}"><c:out value=" alert-checkout"/></c:if>">
								<div class="row">
									<div class="col-xs-12 col-sm-12 col-md-12 col-lg-12">
										<div class="glyphicon glyphicon-alert">&nbsp;</div>
										<spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
									</div>
			                      <!--<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>   Don't need this button as per the requirements. -->
								</div>
							</div>
						</c:otherwise>
					</c:choose>
                </c:when>
                <c:otherwise>
                    <div class="alert alert-danger alert-dismissable">
                           <!--<button class="close" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>    Don't need this button as per the requirements. -->
                        <c:choose>
                        <c:when test="${isForgetPassword && errorMessage ne 'true'}">
                        	<c:if test="${cmsSite.uid eq 'sga'}">
                        		<spring:theme code="sga.forgottenPwd.error.message"/>
                        	</c:if>
                        	<c:if test="${cmsSite.uid ne 'sga'}">
                        		 <spring:theme code="forgottenPwd.error.message"/>
                        	</c:if>
                        </c:when>
                        <c:when test="${cmsPage.uid eq 'contactusPage'}">
                        		 <spring:theme code="contactus.error.message"/>
                        </c:when>
                        <c:otherwise>
                        		 <spring:theme code="${msg.code}" arguments="${msg.attributes}"/>
                        </c:otherwise>
                        </c:choose>
                    </div>
                </c:otherwise>
                </c:choose>
                
			</c:forEach>
		</c:if>
</c:if>

</div>


<div id="stockErrorMsg" class="hide alert alert-danger alert-dismissable">
	<c:set var="allowedQty"><text id="allowedQuantity" value='' ></text></c:set>
<!--<button class="close stockErrorCloseBtn" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>  Don't need this button as per the requirements. -->
	<c:choose>
		<c:when test="${cmsSite.uid ne 'sga'}">
			<spring:theme code="basket.page.message.stock.add.not.allowed" arguments="${allowedQty}"/>
		</c:when>
		<c:otherwise>
			<spring:theme code="sga.basket.page.message.stock.add.not.allowed" arguments="${allowedQty}"/>
		</c:otherwise>
	</c:choose>
</div>

<c:if test="${cmsSite.uid ne 'sga'}">
	<div id="priceUpdateFailedDisclaimer" class="hide alert alert-danger alert-dismissable">
		<spring:theme code="apb.price.not.fetched.services.msg" />
	</div>
	<div id="priceUpdateFailedErr" class="hide alert alert-danger alert-dismissable">
		<spring:theme code="apb.price.not.fetched.services.msg" />
	</div>
</c:if>

<div id="multiAccountErrorMsg" class="hide alert alert-danger alert-dismissable">
	<spring:theme code="error.multi.account.select" />
</div>
<c:if test="${cmsPage.uid eq 'updatePassword'}">
	<div id="tokenInvalid" class="alert alert-danger alert-dismissable<c:if test="${not tokenInvalid eq true}"><c:out value=" hide"/></c:if>">
		<spring:theme code="updatePwd.token.invalid" />
	</div>
</c:if>

<div id="bonusStockErrorMsg" class="hide alert alert-danger alert-dismissable">
	<c:set var="allowedBonusQty"><text id="allowedBonusQty" value='' ></text></c:set>
<!--<button class="close stockErrorCloseBtn" aria-hidden="true" data-dismiss="alert" type="button">&times;</button>  Don't need this button as per the requirements. -->
    <spring:theme code="apb.bonus.stock.not.valid" arguments="${allowedBonusQty}"/>
</div>

<div id="generalErrorMsg" class="hide alert alert-danger alert-dismissable">
<!-- div content -->
</div>
<input id="checkPage" type="hidden" value="${cmsPage.uid}"/>
<div id="creditCheckErrorMsg"></div>
	
<c:if test="${cmsSite.uid eq 'sga'}">
	<c:if test="${not empty invoiceDownloadError}">
		<div class="alert alert-danger alert-dismissable">
			<spring:theme code="sam.invoice.download.error" arguments="${invoiceDownloadError}" /> 
		</div> 
	</c:if>
	<c:if test="${not empty statementDownloadError}">
		<div class="alert alert-danger alert-dismissable">
			<spring:theme code="sam.statement.download.error" arguments="${statementDownloadError}" /> 
		</div> 
	</c:if>
</c:if>

<c:if test="${cmsSite.uid eq 'sga'}">
    <div class="add-order-template-msg alert save-cart-success hidden">
        <spring:theme code="sga.product.details.page.popup.success" />
    </div>
</c:if>