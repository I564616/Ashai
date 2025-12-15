<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<c:if test="${not empty trackOrderDataList}">
	<div class="row track-your-order-homepage">
		<div class="col-xs-12">

			<h2><spring:theme code="text.trackorder.header"/></h2>
			
			
			<div class="track-order-table">
				<div class="row">
					<div class="col-md-12 table-header visible-lg visible-md">
						<div class="row">
							<div class="col-md-3 trim-left"><spring:theme code="text.trackorder.header.status"/></div>
							<div class="col-md-3"><spring:theme code="text.trackorder.header.orderid"/></div>
							<div class="col-md-2">
								<spring:theme code="text.trackorder.header.deliverydate"/>
							</div>
							<div class="col-md-2"><spring:theme code="text.trackorder.header.eta"/></div>
							<div class="col-md-2">&nbsp;</div>
						</div>
					</div>
					<div class="col-md-12 table-body">
	
						<c:choose>
						<c:when test="${not empty trackOrderDataList}">
							<c:forEach items="${trackOrderDataList}" var="trackOrderData" >
		
								<c:url value="/trackorders?code=${trackOrderData.orderCode}" var="trackMyDeliveryPath" />

								<div class="row">
									<div class="item col-md-3 col-sm-12 col-xs-12">
										<span class="icon icon-${trackOrderData.statusId}"></span>
										<%-- ${trackOrderData.status} --%>
										<c:choose>
											<c:when test="${isTrackDeliveryOrderFeatureEnabled and fn:toLowerCase(trackOrderData.status) == 'dispatched'}">
												<spring:theme code="text.account.order.status.display.beingDispatched" />
											</c:when>								
											<c:otherwise>
												${trackOrderData.status}
											</c:otherwise>
										</c:choose>
									</div>
									<div class="item col-md-3 col-sm-12 col-xs-12">
										<label class="visible-xs-inline visible-sm-inline"><spring:theme code="text.trackorder.header.orderid"/>:</label>
										${trackOrderData.orderCode} - Shipment #${trackOrderData.shipmentId}
									</div>
									<div class="item col-md-2 col-sm-12 col-xs-12">
										<label class="visible-xs-inline visible-sm-inline"><spring:theme code="text.trackorder.header.deliverydate"/>:</label>
										<fmt:formatDate value="${trackOrderData.requestedDeliveryDate}" pattern="dd/MM/yyyy"/>
									</div>
									<div class="item col-md-2 col-sm-12 col-xs-12">
										<label class="visible-xs-inline visible-sm-inline"><spring:theme code="text.trackorder.header.eta"/>:</label>

										<c:if test="${trackOrderData.statusId eq '1' || trackOrderData.statusId eq '2' || trackOrderData.statusId eq '3' || trackOrderData.statusId eq '6'}">
											-
										</c:if>

										<c:if test="${trackOrderData.statusId eq '4'}" >

											<c:choose>
												<c:when test="${not empty trackOrderData.arrivedTime}">
													<fmt:formatDate value="${trackOrderData.arrivedTime}" timeZone="${trackOrderData.timeZone}" pattern="HH:mm"/>
												</c:when>
												<c:otherwise>

													<c:if test="${not empty trackOrderData.startETA}" >
														<fmt:formatDate value="${trackOrderData.startETA}" timeZone="${trackOrderData.timeZone}" pattern="HH:mm"/>
													</c:if>
													<c:if test="${not empty trackOrderData.startETA}" >
														to
													</c:if>
													<c:if test="${not empty trackOrderData.endETA}" >
														<fmt:formatDate value="${trackOrderData.endETA}" timeZone="${trackOrderData.timeZone}" pattern="HH:mm"/>
													</c:if>

												</c:otherwise>

											</c:choose>

										</c:if>

										<c:if test="${trackOrderData.statusId eq '5'}" >
											<fmt:formatDate value="${trackOrderData.deliveredTime}" timeZone="${trackOrderData.timeZone}" pattern="HH:mm"/>
										</c:if>


									</div>
									<div class="item col-md-2 col-sm-12 col-xs-12">
										<a href="${trackMyDeliveryPath}" class="btn btn-primary">Track</a>
									</div>
								</div>
							
							</c:forEach>
						</c:when>
						</c:choose>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>