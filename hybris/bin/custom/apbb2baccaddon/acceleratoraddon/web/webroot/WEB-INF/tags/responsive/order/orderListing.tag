
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" type="String" %>
<%@ attribute name="messageKey" required="true" type="String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>


<!-- FILE FROM ORDER LISTING APBB2BAC ADDON  -->
<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/my-account/order/" var="orderDetailsUrl" htmlEscape="false"/>
<c:url var="reorderUrl" value="/history/reorder" scope="page"/>

<c:if test="${empty searchPageData.results} && request.getParameter('startDate') == null">
	    <b2b-order:noOrderHistory/>
</c:if>

<c:set value="" var="textLeft" />
<c:set value="" var="width3" />
<c:if test="${isNAPGroup}">
    <c:set value="text-left" var="textLeft" />
    <c:set value="width-3" var="width3" />
</c:if>

<c:if test="${not empty searchPageData.results || request.getParameter('startDate') != null}">
	<div class="account-orderhistory-label"><spring:theme code="text.account.orderHistory.details.text" /></div>
	<div class="account-orderhistory-label"><spring:theme code="text.account.orderHistory.details.text.part.two" /></div>
	<div class="account-orderhistory-label">
		<c:choose>
			<c:when test="${cmsSite.uid eq 'sga'}">
				<spring:theme code="sga.text.account.orderHistory.details.text.part.three" />
			</c:when>
			<c:otherwise>
				<spring:theme code="text.account.orderHistory.details.text.part.three" />
			</c:otherwise>
		</c:choose>
	</div>
    <div class="account-section-content">
        <div class="account-orderhistory">
            <c:if test="${cmsSite.uid eq 'sga' && orderExportAvailable eq 'true'}">
                <div class="order-history-filter container">
                    <div class="order-history-max-order-range row">
                        <span class="pull-left">
                            <spring:theme code="text.account.orderHistory.dateRange" />
                        </span>
                    </div>
                    <div class="order-history-calendars-export row">
                        <div class="order-history-calendar-group col-md-3 col-sm-4 col-xs-12">
                            <div class="account-orderhistory-label calendar-orderhistory-label-start col-xs-3 col-md-2 text-left">
                                <spring:theme code="text.account.orderHistory.startCalendar.label" />

                            </div>
                            <div class="deffered-delivery-dates order-history-range-calendars col-xs-9">
                                <div class="input-group">
                                    <input type="text" id="startDateCalendar" class="form-control" name="deliveryMethod.deferredDeliveryDate" readonly="readonly">
                                    <span class="input-group-addon showStartCal"><i class="glyphicon glyphicon-calendar"></i></span>
                                </div>
                            </div>
                        </div>
                        <div class="order-history-calendar-group col-md-3 col-sm-4 col-xs-12">
                        <div class="account-orderhistory-label calendar-orderhistory-label-end col-xs-3 col-md-2 text-left">
                            <spring:theme code="text.account.orderHistory.endCalendar.label" />
                        </div>
                            <div class="deffered-delivery-dates order-history-range-calendars col-xs-9">
                                <div class="input-group">
                                    <input type="text" id="endDateCalendar" class="form-control" name="deliveryMethod.deferredDeliveryDate" readonly="readonly">
                                    <span class="input-group-addon showEndCal"><i class="glyphicon glyphicon-calendar"></i></span>
                                </div>
                            </div>
                        </div>

                        <btn id="orderRangeUpdateBtn" class="btn btn-primary btn-order-range-update col-md-1 col-sm-2 col-xs-12">
                            <spring:theme code="text.account.orderHistory.rangeUpdateButton.label" />
                        </btn>

                        <div class="order-history-csv-export pull-right">
                            <a class="site-anchor-link" id="exportOrderHistoryCsvBtn">
                                <spring:theme code="text.account.orderHistory.exportHistoryLink.label" />
                            </a>
                        </div>

                    </div>
                    <div id="dateRangeError" class="hide alert alert-danger alert-dismissable orderhistory-alert">
                            <spring:theme code="text.account.orderHistory.range.error.message" />
                    </div>
                    <script>var maxOrderRange = ${orderRange}</script>
                </div>
            </c:if>
            <div class="account-orderhistory-pagination">
                <nav:paginationwithdisplay top="true" msgKey="${messageKey}" showCurrentPageInfo="true" hideRefineButton="true"
                                supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
                                searchPageData="${searchPageData}" searchUrl="${searchUrl}"
                                numberPagesShown="${numberPagesShown}"/>
            </div>


            <div class="responsive-table order-history-table">
                <table class="responsive-table">
                    <thead>
                    <tr class="responsive-table-head hidden-xs">
                        <c:if test="${cmsSite.uid ne 'sga'}"><th id="header1"><spring:theme code="text.account.orderHistoryListing.orderNumber"/></th></c:if>
                        <c:choose>
	                        <c:when test="${cmsSite.uid eq 'sga'}">
	                        	<th id="header6" class="${width3}"><spring:theme code="text.account.orderHistoryListing.orderNumber"/></th>
	                        </c:when>
	                        <c:otherwise>
	                        	<th id="header6"><spring:theme code="text.account.orderHistory.your.portal.id"/></th>
	                        </c:otherwise>
                        </c:choose>
                        <th id="header2" class="${width3}"><spring:theme code="text.account.orderHistoryListing.datePlaced"/></th>
                        <th id="header3" class="${width3}"><spring:theme code="text.account.orderHistoryListing.type"/></th>
                        <th id="header4" class="${width3}"><spring:theme code="text.account.orderHistoryListing.orderStatus"/></th>
                        <c:if test="${cmsSite.uid ne 'sga'}">
                            <th id="header5"><spring:theme code="text.account.orderHistoryListing.deliveryDate"/></th>
                        </c:if>
                        <c:if test = "${!isNAPGroup}">
                        <th id="header6"><spring:theme code="text.account.orderHistory.total"/></th>
                        </c:if>
                        <th></th>
                    </tr>
                    </thead>
                    <tbody>
                     <form:form action="${reorderUrl}" class="reorderForm" modelAttribute="reorderForm">


                    <c:forEach items="${searchPageData.results}" var="order">
                        <tr class="responsive-table-item kjkhk" title="<spring:theme code='order.confirmation.our.portal.order.id'/>&nbsp;${order.code}">
                        	<c:if test="${cmsSite.uid ne 'sga'}">
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistoryListing.orderNumber"/></td>
                            <td headers="header1" class="responsive-table-cell">
                                <ycommerce:testId code="orderHistoryItem_orderDetails_link">
                                    <a href="${orderDetailsUrl}${ycommerce:encodeUrl(order.code)}" class="responsive-table-link">
                                    	${fn:escapeXml(order.salesOrderId)}
                                    </a>
                                </ycommerce:testId>
                            </td>
                            </c:if>

	                          <c:choose>
		                        <c:when test="${cmsSite.uid eq 'sga'}">
		                        	<td class="hidden-sm hidden-md hidden-lg"><spring:theme
                                    code="text.account.orderHistoryListing.orderNumber"/></td>
		                            <td headers="header6" class="responsive-table-cell">
		                             <ycommerce:testId code="orderHistoryItem_orderDetails_link">
		                              <a href="${orderDetailsUrl}${ycommerce:encodeUrl(order.code)}" class="responsive-table-link">
	                                    	${order.code}
	                                 </a>
		                              </ycommerce:testId>
		                            </td>
		                        </c:when>
		                        <c:otherwise>
		                        	 <td class="hidden-sm hidden-md hidden-lg"><spring:theme
                                    code="text.account.orderHistory.your.portal.id"/>:</td>
		                            <td headers="header6" class="responsive-table-cell">
		                                 ${order.code}
		                            </td>
		                        </c:otherwise>
	                        </c:choose>
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme
                                    code="text.account.orderHistory.datePlaced"/></td>
                            <td headers="header2" class="responsive-table-cell">
                                      ${order.orderPlacedDate}
                            </td>
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.type"/></td>
                            <td headers="header2" id="order-type" class="responsive-table-cell">
                                    <c:choose>
         							<c:when test = "${order.orderType == 'Online'}">
         								${fn:escapeXml(order.orderType)} (${fn:replace(order.firstName, fn:substring(order.firstName, 20, fn:length(order.firstName)), '..')})
         							</c:when>
         							<c:otherwise>
         								${fn:escapeXml(order.orderType)}
         							</c:otherwise>
      							</c:choose>
                            </td>
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.orderStatus"/></td>
                            <td headers="header4" class="responsive-table-cell ${textLeft}">
                                ${order.statusDisplay}
                            </td>
                            <c:if test="${cmsSite.uid ne 'sga'}">
                                <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.deliveryDate"/></td>
                                <c:choose>
                                    <c:when test="${not empty order.deliveryRequestDate}">
                                        <td headers="header5" class="responsive-table-cell">
                                                ${order.deliveryRequestDate}
                                        </td>
                                    </c:when>
                                    <c:otherwise>
                                        <td headers="header5" class="responsive-table-cell"><spring:theme code="text.account.orderHistory.inProgress" /></td>
                                    </c:otherwise>
                                </c:choose>
                            </c:if>
                            <c:if test="${!isNAPGroup}">
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.orderHistory.total"/><spring:theme code="asahi.text.label.separator"/></td>
                            <td headers="header6" class="responsive-table-cell responsive-table-cell-bold">
                                <div>${fn:escapeXml(order.total.formattedValue)}</div>
                            </td>

                 			<td id="orderHistoryReorderCol" class="responsive-table-cell ">
								<c:if test = "${order.orderType == 'Online'}">
									<c:choose>
										<c:when test="${order.allProductExcluded != null && order.allProductExcluded.booleanValue()}">
											<button type="submit" class="btn btn-primary btn-template-block reorder-button" disabled="disabled" id="reorderButton">
												<spring:theme code="text.order.reorderbutton"/>
											</button>
										</c:when>
										<c:when test="${order.isOnlyBonus != null && order.isOnlyBonus.booleanValue()}">
											<button type="submit" class="btn" disabled="disabled" id="reorderButton">
												<spring:theme code="text.order.reorderbutton"/>
											</button>
										</c:when>
										<c:otherwise>
											<button type="submit" class="btn btn-primary btn-template-block reorder-button" id="reorderButton">
												<spring:theme code="text.order.reorderbutton"/>
											</button>
										</c:otherwise>
									</c:choose>
								</c:if>
								<input type="hidden" name="orderCode" value="${order.code}" />
								</td>
							</c:if>
                        </tr>
                    </c:forEach>
                    </form:form>
                    </tbody>
                </table>
                <c:if test="${empty searchPageData.results}">
                        <tr>
                            <div class="account-orderhistory-label orderhistory-noresults-label">
                                <spring:theme code="text.account.orderHistory.noresults.message"/>
                            </div>
                        </tr>
                    </c:if>
            </div>
            <div class="account-orderhistory-pagination">
            <nav:paginationwithnumbering top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}"
	searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
			</div>
        </div>
    </div>
</c:if>
