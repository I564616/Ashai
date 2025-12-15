<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>

<div class="row">
    <c:forEach items="${orderData.consignments}" var="consignment">
        <c:if test="${consignment.status.code eq 'CREATED' }">
            <c:set value="true" var="dispalyCreated"/>
        </c:if>
    </c:forEach>
    <c:if test="${dispalyCreated }">
        <div class="col-xs-12">
            <h2><spring:theme code="text.account.order.details.items.created"/></h2>
            <div class="table-items">
                <div class="custom-table">
                    <div class="table-headers">
                        <div class="col-xs-5">
                            <div class="row">
                                <div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
                                <div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
                            </div>
                        </div>
                        <div class="col-xs-7">
                            <div class="row">
                                <div class="col-md-7"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
                                <div class="col-md-5"><span><spring:theme code="text.account.order.details.requested.delivery.date"/></span></div>
                            </div>
                        </div>
                    </div>
                    <div class="table-body">
                        <c:forEach items="${orderData.consignments}" var="consignment">
                            <c:if test="${consignment.status.code eq 'CREATED' }">
                                <c:forEach items="${consignment.entries }" var="entry" varStatus="status">
                                    <product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
                                    <div class="table-row<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                        <input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
                                        <div class="row">
                                            <div class="col-xs-8 col-md-5">
                                                <order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
                                            </div>
                                            <div class="col-xs-12 col-md-7">
                                                <div class="row">
                                                    <div class="col-md-7"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
                                                    <div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span>
                                                        <c:choose>
                                                            <c:when test="${!isProductPackTypeAllowed}">
                                                                <div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
                                                            </c:when>
                                                        </c:choose></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <c:forEach items="${orderData.consignments}" var="consignment">
        <c:if test="${consignment.status.code eq 'PROCESSING' }">
            <c:set value="true" var="dispalyProcessing"/>
        </c:if>
    </c:forEach>
    <c:if test="${dispalyProcessing }">
        <div class="col-xs-12">
            <h2><spring:theme code="text.account.order.details.items.processing"/></h2>
            <div class="table-items">
                <div class="custom-table">
                    <div class="table-headers">
                        <div class="col-xs-5">
                            <div class="row">
                                <div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
                                <div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
                            </div>
                        </div>
                        <div class="col-xs-7">
                            <div class="row">
                                <div class="col-md-7"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
                                <div class="col-md-5"><span><spring:theme code="text.account.order.details.requested.delivery.date"/></span></div>
                            </div>
                        </div>
                    </div>
                    <div class="table-body">
                        <c:forEach items="${orderData.consignments}" var="consignment">
                            <c:if test="${consignment.status.code eq 'PROCESSING' }">
                                <c:forEach items="${consignment.entries }" var="entry" varStatus="status">
                                    <product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
                                    <div class="table-row<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                        <input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
                                        <div class="row">
                                            <div class="col-xs-8 col-md-5">
                                                <order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
                                            </div>
                                            <div class="col-xs-12 col-md-7">
                                                <div class="row">
                                                    <div class="col-md-7"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.shippedQuantity}</span></div>
                                                    <div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <c:forEach items="${orderData.consignments}" var="consignment">
        <c:if test="${consignment.status.code eq 'SHIPPED'  || consignment.status.code eq 'INTRANSIT' || consignment.status.code eq 'DELIVERED' || consignment.status.code eq 'NOTDELIVERED'}">
            <c:set value="true" var="dispalyDispatched"/>
        </c:if>
    </c:forEach>
    <c:if test="${dispalyDispatched }">
        <div class="col-xs-12">
            <%-- <h2><spring:theme code="text.account.order.details.items.dispatched"/></h2> --%>
            <c:choose>
            	<c:when test="${isTrackDeliveryOrderFeatureEnabled}">	
					<h2><spring:theme code="text.account.order.details.items.beingdispatched"/></h2>
				</c:when>								
				<c:otherwise>
					<h2><spring:theme code="text.account.order.details.items.dispatched"/></h2>
				</c:otherwise>
			</c:choose>
            <div class="table-items">
                <div class="custom-table">
                    <div class="table-headers">
                        <div class="col-xs-5">
                            <div class="row">
                                <div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
                                <div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
                            </div>
                        </div>
                        <div class="col-xs-7">
                            <div class="row">
                                <div class="col-md-3 trim-right"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
                                <%-- <div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.dispatched"/></span></div> --%>
                                <c:choose>
                                	<c:when test="${isTrackDeliveryOrderFeatureEnabled}">
										<div class="col-md-4 trim-right"><span><spring:theme code="text.account.order.details.quantity.beingdispatched"/></span></div>		
									</c:when>						
									<c:otherwise>
										<div class="col-md-4 trim-right"><span><spring:theme code="text.account.order.details.items.dispatched"/></span></div>
									</c:otherwise>
								</c:choose>
                                <div class="col-md-5"><span><spring:theme code="text.account.order.details.requested.delivery.date"/></span></div>
                            </div>
                        </div>
                    </div>
                    <div class="table-body">
                        <c:forEach items="${orderData.consignments}" var="consignment">
                            <c:if test="${consignment.status.code eq 'SHIPPED' || consignment.status.code eq 'INTRANSIT' || consignment.status.code eq 'DELIVERED' || consignment.status.code eq 'NOTDELIVERED'}">
                                <c:forEach items="${consignment.entries }" var="entry" varStatus="status">
                                    <product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
                                    <div class="table-row<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                        <input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
                                        <div class="row">
                                            <div class="col-xs-8 col-md-5">
                                                <order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
                                            </div>
                                            <div class="col-xs-12 col-md-7">
                                                <div class="row">
                                                    <div class="col-md-3"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
                                                   <%--  <div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div> --%>
                                                   <c:choose>
					                                	<c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.beingdispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div>		
														</c:when>						
														<c:otherwise>
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div>
														</c:otherwise>
													</c:choose>
                                                    <div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <c:forEach items="${orderData.consignments}" var="consignment">
        <c:if test="${consignment.status.code eq 'CANCELLED' }">
            <c:set value="true" var="dispalyCancelled"/>
        </c:if>
    </c:forEach>
    <c:if test="${dispalyCancelled }">
        <div class="col-xs-12">
            <h2><spring:theme code="text.account.order.details.items.cancelled"/></h2>
            <div class="table-items">
                <div class="custom-table">
                    <div class="table-headers">
                        <div class="col-xs-5">
                            <div class="row">
                                <div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
                                <div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
                            </div>
                        </div>
                        <div class="col-xs-7">
                            <div class="row">
                                <div class="col-md-7"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
                                <div class="col-md-5"><span><spring:theme code="text.account.order.details.requested.delivery.date"/></span></div>
                            </div>
                        </div>
                    </div>
                    <div class="table-body">
                        <c:forEach items="${orderData.consignments}" var="consignment">
                            <c:if test="${consignment.status.code eq 'CANCELLED' }">
                                <c:forEach items="${consignment.entries }" var="entry" varStatus="status">
                                    <product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
                                    <div class="table-row<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                        <input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
                                        <div class="row">
                                            <div class="col-xs-8 col-md-5">
                                                <order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
                                            </div>
                                            <div class="col-xs-12 col-md-7">
                                                <div class="row">
                                                    <div class="col-md-7"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
                                                    <div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
    <c:forEach items="${orderData.consignments}" var="consignment">
        <c:if test="${consignment.status.code eq 'RETURNED' }">
            <c:set value="true" var="dipalyReturned"/>
        </c:if>
    </c:forEach>
    <c:if test="${dipalyReturned }">
        <div class="col-xs-12">
            <h2><spring:theme code="text.account.order.details.items.returned"/></h2>
            <div class="table-items">
                <div class="custom-table">
                    <div class="table-headers">
                        <div class="col-xs-5">
                            <div class="row">
                                <div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
                                <div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
                            </div>
                        </div>
                        <div class="col-xs-7">
                            <div class="row">
                                <div class="col-md-3 trim-right"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
                                <%-- <div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.dispatched"/></span></div> --%>
                                <c:choose>
                                	<c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
										<div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.beingdispatched"/></span></div>		
									</c:when>						
									<c:otherwise>
										<div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.dispatched"/></span></div>
									</c:otherwise>
								</c:choose>  
                                <div class="col-md-5"><span><spring:theme code="text.account.order.details.requested.delivery.date"/></span></div>
                            </div>
                        </div>
                    </div>
                    <div class="table-body">
                        <c:forEach items="${orderData.consignments}" var="consignment">
                            <c:if test="${consignment.status.code eq 'RETURNED' }">
                                <c:forEach items="${consignment.entries }" var="entry" varStatus="status">
                                    <product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
                                    <div class="table-row<c:if test="${!isProductPackTypeAllowed}"> disabled-productPackTypeNotAllowed</c:if>">
                                        <input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
                                        <div class="row">
                                            <div class="col-xs-8 col-md-5">
                                                <order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
                                            </div>
                                            <div class="col-xs-12 col-md-7">
                                                <div class="row">
                                                    <div class="col-md-3 trim-right-lg"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
                                                   <%--  <div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div> --%>
                                                    <c:choose>
					                                	<c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.beingdispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div>	
														</c:when>						
														<c:otherwise>
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div>
														</c:otherwise>
													</c:choose>
                                                    <div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${orderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span></div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </c:forEach>
                            </c:if>
                        </c:forEach>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>