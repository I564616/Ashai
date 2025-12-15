<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ attribute name="shipment" required="true" type="com.sabmiller.facades.order.data.TrackOrderData"%>
<%@ taglib prefix="order" tagdir="/WEB-INF/tags/desktop/order" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>


<c:set var="count" scope="request" value="${count + 1}"/>

<c:forEach items="${shipment.consignment.entries }" var="entry" varStatus="status">
	<c:set var="orderedQuantity" value="${entry.quantity}" scope="page" />
	<c:set var="shippedQuantity" value="${entry.shippedQuantity}" scope="page" />
</c:forEach>

<c:set value="${shipment.orderedAndDispatchedQuantityNotEqual}" var="orderedAndDispatchedQuantityNotEqual" scope="page" />


<div class="row track-my-delivery">
        <div class="col-xs-12 shipment shipment-${count}">
            <h3>Shipment #${count}</h3>
            <div class="panelbox">
                <div class="headpanel">
                    <div class="row" style="margin:0;">
                        <div class="nav-panel col-md-4 active" style="background: #f6f7f6;">
                            <span class="icon icon-${shipment.statusId} ${shipment.statusId == 5 ? 'delivered' : 'active'}"></span>
                            <%-- <span class="status-indicator">${shipment.status}</span> --%>
                            <c:choose>
								<c:when test="${isTrackDeliveryOrderFeatureEnabled and fn:toLowerCase(shipment.status) == 'dispatched'}">
									<span class="status-indicator"><spring:theme code="text.account.order.status.display.beingDispatched" /></span>
								</c:when>								
								<c:otherwise>
									<span class="status-indicator">${shipment.status}</span>
								</c:otherwise>
							</c:choose>
                        </div>
                        <div class="nav-panel col-md-4 col-sm-6 col-xs-6" style="background:#ecedec">
                            	
                            	<c:choose>
                            		<c:when test="${shipment.statusId ne '6'}">
	                            	<i class="glyphicon glyphicon-time"></i>
	                                <span class="subStatus-text">${shipment.subStatus}</span>
	                                </c:when>
	                                <c:otherwise>
	                                	<!-- Display Nothing -->
	                                </c:otherwise>
                            	</c:choose>
                            	
                            <i class="arrowright"></i>
                        </div>
                        <div class="nav-panel col-md-4 col-sm-6 col-xs-6" style="background:#f6f7f6">
                            <h2>
                                <c:choose>
                                    <c:when test="${shipment.statusId eq '1' || shipment.statusId eq '2' || shipment.statusId eq '3' }">
                                        <c:if test="${not empty shipment.requestedDeliveryDate}">
                                            <fmt:formatDate value="${shipment.requestedDeliveryDate}" pattern="MMM dd, yyyy"/>
                                        </c:if>
                                    </c:when>


                                    <c:when test="${shipment.statusId eq '4'}">

                                        <c:if test="${empty shipment.arrivedTime}">
                                        
                                        	<c:if test="${ empty shipment.startETA &&  empty shipment.endETA}">
                                            <spring:theme code="text.trackorder.recalculating" />
                                            </c:if>
                                            <c:if test="${not empty shipment.startETA and not empty shipment.endETA}">
                                                <fmt:formatDate value="${shipment.startETA}" timeZone="${shipment.timeZone}" pattern="HH:mm"/> <small>to</small>
                                                <fmt:formatDate value="${shipment.endETA}" timeZone="${shipment.timeZone}" pattern="HH:mm"/>
                                            </c:if>
                                        </c:if>

                                        <c:if test="${not empty shipment.arrivedTime}">
                                            <fmt:formatDate value="${shipment.arrivedTime}" timeZone="${shipment.timeZone}" pattern="HH:mm"/>
                                        </c:if>
                                    </c:when>

                                    <c:when test="${shipment.statusId eq '5'}">
                                        <fmt:formatDate value="${shipment.deliveredTime}" timeZone="${shipment.timeZone}"
                                                        pattern="HH:mm"/>
                                    </c:when>
                                    <c:when test="${shipment.statusId eq '6'}">
                                       <!--display NO Time -->
                                    </c:when>
                                </c:choose>

                           </h2>
                        </div>
                    </div>
                </div>



                <div class="bodypanel">

					<c:choose>
						<c:when test="${shipment.status eq 'Delivered' && not empty shipment.signature}">
							<div class="signed-by">
                                <c:choose>
                                    <c:when test="${shipment.notDeliveredReason eq 'Completed on Paper'}">
                                        <span class="name"><spring:theme code="text.account.order.consignment.completed.on.paper"/></span>
                                    </c:when>
                                    <c:otherwise>
                                        Signed by <span class="name">${shipment.signature}</span> on <fmt:formatDate
                                            value="${shipment.deliveredTime}" timeZone="${shipment.timeZone}" pattern="dd MMM yyyy"/>
                                    </c:otherwise>
                                </c:choose>

							</div>
						</c:when>
					</c:choose>

                    <div class="container">
                        <ul class="track-my-delivery-icons">
                            <li>
                                <span class="icon icon-1"></span>
                                <p class="text"><spring:theme code="text.account.order.consignment.status.CREATED" /></p>
                                <span class="footer-text"><fmt:formatDate value="${shipment.confirmedTime}" timeZone="${shipment.timeZone}"
                                                                          pattern="dd/MM/yyyy"/> <br>
                                <fmt:formatDate value="${shipment.confirmedTime}" timeZone="${shipment.timeZone}" pattern="HH:mm"/>
                                </span>
                            </li>
                            <li>
                                <span class="icon icon-2 ${shipment.statusId >= 2 ? 'done' : '' }"></span>
                                <span class="line"></span>
                                <p class="text"><spring:theme code="text.account.order.consignment.status.PROCESSING" /></p>
                                <span class="footer-text"><fmt:formatDate value="${shipment.beingPickedTime}"
                                                                          timeZone="${shipment.timeZone}"
                                                                          pattern="dd/MM/yyyy"/>
                                    <br>
                                    <fmt:formatDate value="${shipment.beingPickedTime}" timeZone="${shipment.timeZone}"
                                                    pattern="HH:mm"/></span>
                            </li>
                            <li>
                                <span class="icon icon-3 ${shipment.statusId >= 3 ? 'done' : '' }">
                             		 <span class="icon icon-err-indicator icon-warning ${shipment.statusId == 3 && orderedAndDispatchedQuantityNotEqual ? 'visible' : 'invisible'}"></span>
                                </span>
                                
                                <span class="line"></span>
                                <%-- <p class="text"><spring:theme code="text.account.order.consignment.status.SHIPPED" /></p> --%>
                                <c:choose>
									<c:when test="${isTrackDeliveryOrderFeatureEnabled}">									
										 <p class="text"><spring:theme code="text.account.order.status.display.beingDispatched" /></p>
									</c:when>								
									<c:otherwise>
										 <p class="text"><spring:theme code="text.account.order.consignment.status.SHIPPED" /></p>
									</c:otherwise>
								</c:choose>
                                <span class="footer-text">
                                    <fmt:formatDate value="${shipment.dispatchedTime}" timeZone="${shipment.timeZone}"
                                                    pattern="dd/MM/yyyy"/>
                                    <br>
                                    <fmt:formatDate value="${shipment.dispatchedTime}" timeZone="${shipment.timeZone}"
                                                    pattern="HH:mm"/></span>
                            </li>
                            
                            <li class="${shipment.statusId >= 4 ? 'visible' : 'invisible'}">
                                <span class="icon icon-4 ${shipment.statusId >= 4 ? 'done' : '' }">
                              		 <span class="icon icon-err-indicator icon-warning ${shipment.statusId == 4 && orderedAndDispatchedQuantityNotEqual ? 'visible' : 'invisible'}"></span>
                                </span>
                                <span class="line"></span>
                                <p class="text"><spring:theme code="text.account.order.consignment.status.INTRANSIT" /></p>
                                <span class="footer-text"><fmt:formatDate value="${shipment.firstETAMessageTime}"
                                                                          timeZone="${shipment.timeZone}"
                                                                          pattern="dd/MM/yyyy"/> <br>
                                <fmt:formatDate value="${shipment.firstETAMessageTime}" timeZone="${shipment.timeZone}" pattern="HH:mm"/>
                                </span>
                            </li> 
                            <li class="${shipment.statusId == 4 || shipment.statusId == 5 || shipment.statusId == 6  ? 'visible' : 'invisible'}">
                                <span class="icon icon-${shipment.statusId eq 4 || shipment.statusId eq 5 ? '5' : shipment.statusId } ${shipment.statusId == 5 ? 'done' : '' }">
                               		 <span class="icon icon-err-indicator icon-unabletodeliver ${shipment.statusId eq '6' ? 'visible' : 'invisible'}"></span>
                                </span>
                                
                                <span class="line"></span>
                                <p class="text"><spring:theme code="text.account.order.consignment.status.${shipment.statusId == 6 ? 'NOT' : ''}DELIVERED" />
                                </p>
                                <span class="footer-text">

                                    <fmt:formatDate value="${shipment.deliveredTime}" timeZone="${shipment.timeZone}"
                                                        pattern="dd/MM/yyyy"/>
                                            <br>
                                    <fmt:formatDate value="${shipment.deliveredTime}" timeZone="${shipment.timeZone}"
                                                    pattern="HH:mm"/></span>

                            </li>
                        </ul>
                    </div>
					<c:choose>
						<c:when test="${shipment.ETAPassed == true}">
		                   	<div class="custom-alert alert alert-danger">
								<spring:theme code="text.trackorder.eta.passed.error"/>
                     		</div>
						</c:when>
						<c:when test="${shipment.statusId eq '3' || shipment.statusId eq '4'}">
							
							<!-- display when there is a difference between quantity ordered and quantity dispatched -->
							<c:choose>
								<c:when test="${orderedAndDispatchedQuantityNotEqual}">
							     	<div class="custom-alert alert alert-warning">
				                    	<spring:theme code="text.account.order.dispatchedErrMessage"/>
				                   	</div>
								</c:when>
							</c:choose>
	
		                </c:when>
						<c:when test="${shipment.statusId eq '6'}">
		                   	<div class="custom-alert alert alert-danger">
		                    	<span class="icon icon-err-indicator icon-unabletodeliver visible-lg-inline-block visible-md-inline-block"></span>
		                    	<spring:theme code="text.account.order.unableToDeliverErrMessage"/>
		                   	</div>
		                </c:when>
					</c:choose>
                     <footer>  
                     
                     <div class="view-order-items">
                     	<a href="javascript:void(0)"><span class="arrow right"></span> &nbsp; <spring:theme code="text.trackorder.accordion.viewOrderItems"/></a>
						 <div class="trackorder-view-details hide">
                         <order:trackOrderViewDetails trackOrderData="${shipment}"/>
						 </div>
                     </div>
					</footer>
            		
                </div>
            		
            		
            		
            </div>
        </div>
</div>
