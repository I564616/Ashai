<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ attribute name="trackOrderData" required="true" type="com.sabmiller.facades.order.data.TrackOrderData"%>


<div class="row">

	<p>Delivering to</p>
	
	<c:set value="${trackOrderData.consignment.shippingAddress.town}" var="town"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.title}" var="title"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.firstName}" var="firstName"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.lastName}" var="lastName"/>
	<c:set value="${trackOrderData.b2bUnitName}" var="b2bUnitName"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.line1}" var="shippingAddressLine1"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.line2}" var="shippingAddressLine2"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.region.name}" var="regionName"/>
	<c:set value="${trackOrderData.consignment.shippingAddress.postalCode}" var="postalCode"/>
	
	
	<p class="deliveringTo-value">
	${title} ${not empty title ? ',' : ''} ${firstName} ${empty firstName ? '' : '&nbsp;'} ${lastName}
	${b2bUnitName}${not empty b2bUnitName ? ',' : ''}
	${shippingAddressLine2}${not empty shippingAddressLine2 ? ',' : ''} 
	${shippingAddressLine1}${not empty shippingAddressLine1 ? ',' : ''} 
	${town}${not empty town ? ',' : ''} 
	${regionName}${not empty regionName ? '' : postalCode}
	</p>

	<c:if test="${trackOrderData.consignment.status.code eq 'CREATED' || trackOrderData.consignment.status.code eq 'PROCESSING' || trackOrderData.consignment.status.code eq 'CANCELLED'  }">
		<div class="col-xs-12 trim-left">
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
								</br>
								<c:forEach items="${trackOrderData.consignment.entries }" var="entry" varStatus="status">
									<product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
									<div class="table-row">
										<input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
										<div class="row">
											<div class="col-xs-8 col-md-5">
												<order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
											</div>
											<div class="col-xs-12 col-md-7">
												<div class="row">
													<div class="col-md-7"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
													<div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${trackOrderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span>
														<c:choose>
															<c:when test="${!isProductPackTypeAllowed &&  consignment.status.code eq 'CREATED'}">
																<div class="btn btn-primary btn-invert btn-block btn-changeDeliveryDate"><spring:theme code="basket.change.delivery.date"/></div>
															</c:when>
														</c:choose></div>
												</div>
											</div>
										</div>
									</div>
								</c:forEach>
					</div>
				</div>
			</div>
		</div>

	</c:if>



	<c:if test="${trackOrderData.consignment.status.code eq 'SHIPPED' || trackOrderData.consignment.status.code eq 'INTRANSIT' || trackOrderData.consignment.status.code eq 'DELIVERED' ||  trackOrderData.consignment.status.code eq 'NOTDELIVERED'}">
		<div class="col-xs-12 trim-left">
			<div class="table-items">
				<div class="custom-table">
					<!-- <div class="table-headers"> -->
					<c:choose>
						<c:when test="${isTrackDeliveryOrderFeatureEnabled}">	
							<div class="table-headers" style="padding-bottom : 45px">	
						</c:when>								
						<c:otherwise>
							<div class="table-headers">
						</c:otherwise>
					</c:choose>
						<div class="col-xs-5">
							<div class="row">
								<div class="col-md-3 visible-md-block visible-lg-block"><spring:theme code="basket.page.image" /></div>
								<div class="col-md-9 trim-left-5-lg"><spring:theme code="basket.page.product" /></div>
							</div>
						</div>
						<div class="col-xs-7">
							<div class="row">
								<div class="col-md-3 trim-both"><span><spring:theme code="text.account.order.details.quantity.ordered"/></span></div>
								<%-- <div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.dispatched"/></span></div> --%>
								<c:choose>
									<c:when test="${isTrackDeliveryOrderFeatureEnabled}">
										<div class="col-md-4"><span><spring:theme code="text.account.order.details.quantity.beingdispatched" /></span></div>
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
								<c:forEach items="${trackOrderData.consignment.entries }" var="entry" varStatus="status">
									<product:productPackTypeAllowed unit="${entry.orderEntry.unit.code}"/>
									<div class="table-row">
										<input type="hidden" class="entryNumber" value="${entry.orderEntry.entryNumber}">
										<div class="row">
											<div class="col-xs-8 col-md-5">
												<order:orderConsignmentProduct product="${entry.orderEntry.product }" productListPosition="${status.count}"/>
											</div>
											<div class="col-xs-12 col-md-7">
												<div class="row">
													<div class="col-md-3"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.ordered"/></span><span class="h3">${entry.quantity}</span></div>
													<%-- <div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div> --%>
													<c:choose>
														<c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.status.display.beingDispatched" /></span><span class="h3">${entry.shippedQuantity }</span></div>
														</c:when>								
														<c:otherwise>
															<div class="col-md-4"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.quantity.dispatched"/></span><span class="h3">${entry.shippedQuantity }</span></div>
														</c:otherwise>
													</c:choose>
													<div class="col-md-5"><span class="custom-headers-mob"><spring:theme code="text.account.order.details.requested.delivery.date"/></span><span class="h3"><fmt:formatDate value="${trackOrderData.requestedDeliveryDate}" pattern="dd/MM/yyyy" type="date" /></span></div>
												</div>
											</div>
										</div>
									</div>
								</c:forEach>

					</div>
				</div>
			</div>
		</div>
	</c:if>



</div>