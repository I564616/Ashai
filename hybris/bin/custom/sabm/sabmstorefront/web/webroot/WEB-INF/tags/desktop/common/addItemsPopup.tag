<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="isAddToCartTemplate" required="false" type="java.lang.Boolean"%>
 <div class="modal fade modal-out-of-stock" id="outOfStockPopup" role="dialog">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-body">
          <%--<h2><div id="orderHistoryPopUpError"><spring:theme code="basket.home.order.deliveryPackDate.notAvailable"/><br/><br/></div></h2> --%>
          <h2><div id="orderHistoryPopUpError"></div></h2>
          <c:if test="${isAddToCartTemplate}">
          	<p><button class="btn btn-primary submit-notification" id="addToCartForTemplateFromPopUp" data-dismiss="modal" ng-click="addAllToCart()">Proceed</button></p>
          </c:if>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default back" data-dismiss="modal"><span class="glyphicon glyphicon-menu-left"></span>back</button>
        </div>
      </div>
    </div>
  </div>
