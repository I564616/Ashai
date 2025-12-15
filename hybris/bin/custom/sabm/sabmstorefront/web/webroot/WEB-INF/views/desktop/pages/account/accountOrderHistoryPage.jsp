<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="action" tagdir="/WEB-INF/tags/desktop/action"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<template:page pageTitle="${pageTitle}">
	<div class="order-history">
		<cms:pageSlot position="TopContentSlot" var="feature" element="div">
			<cms:component component="${feature}" />
		</cms:pageSlot>
		<c:url value="/your-business/orderAdd/" var="orderAddToCartUrl" scope="session"/>
		<input type="hidden" class="orderAddToCartUrl" value="${orderAddToCartUrl }">
		<div id="globalMessages">
			<common:globalMessages />
		</div>
		<div class="row">
			<div class="col-sm-12">
				<div class="row order-filters">
					<div class="col-md-4 offset-bottom-xsmall">
						<h1><spring:theme code="text.account.orderHistory" /></h1>
			            <div class="numItems"><span></span>&nbsp;<spring:theme code="text.account.orderHistory.multiple.orders" /></div>
			            <div class="numItem"><spring:theme code="text.account.orderHistory.one.order" /></div>
					</div>
                    <div class="col-md-8">
                        <div class="row">
                            <div class="input-daterange form-group offset-bottom-xsmall clearfix margin-top-10" id="orderHistoryRange">
                                <div class="row">
                                    <div class="col-sm-6">

                                        <label for="start" class="col-sm-2 control-label">From</label>

                                        <div class="col-sm-10 trim-right-lg">
                                            <div class="input-icon-group">
                                                <input type="text" class="form-control orderdate-start" readonly="readonly" id="start" name="start" data-value="" placeholder="From" />
                                                <svg class="icon-calendar">
                                                    <use xlink:href="#icon-calendar"></use>    
                                                </svg>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-sm-6">

                                        <label for="end" class="col-sm-2 control-label">To</label>

                                        <div class="col-sm-10 trim-left-lg">
                                            <div class="input-icon-group">
                                                <input type="text" class="form-control orderdate-end" readonly="readonly" id="end" name="end" data-value="" placeholder="To"/>
	                                                <svg class="icon-calendar">
	                                                    <use xlink:href="#icon-calendar"></use>    
	                                                </svg>
                                                </span>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
						<span><spring:theme code="text.account.orderHistory.default.date"/></span>
					</div>
				</div>
			    <div class="row">
				<div class="col-md-8">
						<div class="pagination pagination-top hide-if-no-paging"></div>
			      	</div>
			      	<div class="col-md-1 sort-wrap trim-right">
			      		<span><spring:theme code="text.account.orderHistory.page.sortTitle" /></span>
			      	</div>
			      	<div class="col-md-3 sort-wrap trim-left-lg">

			        	<div class="select-list" >
			         		<div data-value="" class="select-btn"></div>
							<ul class="select-items">
								<li class="columnSort" data-index="0" data-ascending="true"><spring:theme code="text.sort.orderNo.ascending" /></li>
					           	<li class="columnSort" data-index="0" data-ascending="false"><spring:theme code="text.sort.orderNo.descending" /></li>
			           			<li class="columnSort" data-index="1" data-ascending="false"><spring:theme code="text.sort.date.descending" /></li>
					           	<li class="columnSort" data-index="1" data-ascending="true"><spring:theme code="text.sort.date.ascending" /></li>
					           	<li class="columnSort" data-index="2" data-ascending="true"><spring:theme code="text.sort.request.delivery.date.ascending" /></li>
					           	<li class="columnSort" data-index="2" data-ascending="false"><spring:theme code="text.sort.request.delivery.date.descending" /></li>
					           	<li class="columnSort" data-index="3" data-ascending="true"><spring:theme code="text.sort.status.ascending" /></li>
					           	<li class="columnSort" data-index="3" data-ascending="false"><spring:theme code="text.sort.status.descending" /></li>
			         		</ul>
			       		</div>
			     	</div>
			   	</div>
				<c:url value="/your-business/ordersjson" var="ordersJsonCall" />



               <span id="order-status-cancelled" class="hidden"><spring:theme code="text.account.order.status.display.cancelled"/></span>
               <span id="order-status-cancelling" class="hidden"><spring:theme code="text.account.order.status.display.cancelling"/></span>
               <span id="order-status-completed" class="hidden"><spring:theme code="text.account.order.status.display.completed"/></span>
               <span id="order-status-created" class="hidden"><spring:theme code="text.account.order.status.display.created"/></span>
               <span id="order-status-error" class="hidden"><spring:theme code="text.account.order.status.display.error"/></span>
               <span id="order-status-open" class="hidden"><spring:theme code="text.account.order.status.display.open"/></span>
               <span id="order-status-processing" class="hidden"><spring:theme code="text.account.order.status.display.processing"/></span>
               <span id="order-status-dispatched" class="hidden"><spring:theme code="text.account.order.status.display.dispatched"/></span>
               <span id="order-status-returned" class="hidden"><spring:theme code="text.account.order.status.display.returned"/></span>
               
               <span id="order-status-intransit" class="hidden"><spring:theme code="text.account.order.status.display.inTransit"/></span>
               <span id="order-status-delivered" class="hidden"><spring:theme code="text.account.order.status.display.delivered"/></span>
               <span id="order-status-notdelivered" class="hidden"><spring:theme code="text.account.order.status.display.notDelivered"/></span>
               <span id="order-status-partiallydelivered" class="hidden"><spring:theme code="text.account.order.status.display.partiallyDelivered"/></span>
               
               
				<table id="order-table" class="table sortable sortable-json order-table" data-page-navigation=".pagination"
					data-filter-text-only="true" data-filter="#orderFilter" data-url='${ordersJsonCall}'
					data-page-size="<spring:theme code="text.num.rows.min" />" data-limit-navigation="3">
					<thead>
						<tr>
							<th data-toggle="true"><spring:theme code="text.account.orderHistory.orderNumber" /></th>
							<th data-toggle="true" data-sort-initial="true" data-hide="phone,tablet"><spring:theme code="text.account.orderHistory.datePlaced" /></th>
							<th data-toggle="true" data-hide="phone"><spring:theme code="text.account.orderHistory.request.delivery.date" /></th>
							<th data-toggle="true" data-hide="phone,tablet"><spring:theme code="text.account.orderHistory.orderStatus" /></th>
							<c:if test="${!isNAPGroup}">
							<th data-toggle="true" id="tableActionText" data-text="<spring:theme code="text.orderTemplate.table.actions.cart" />" data-sort-ignore="true"><spring:theme code="text.account.orderHistory.actions" /></th>
							</c:if>
						</tr>
					</thead>
					<tbody>
					</tbody>
				</table>
				<div id="noDataError" class="row">
                	<div class="col-xs-12">
                  		<h4><spring:theme code="text.account.orderHistory.emptyOrderHistory" /></h4>
                	</div>
              	</div>
				<div class="row">
					<div class="col-sm-8">
						<div class="pagination hide-if-no-paging"></div>
					</div>
					<div class="col-sm-4 num-rows">
						<ul class="list-inline">
							<li><spring:theme code="text.show.title" /></li>
							<li data-value="<spring:theme code="text.num.rows.min" />" class="option active"><spring:theme code="text.num.rows.min" /></li>
							<li data-value="<spring:theme code="text.num.rows.med" />" class="option"><spring:theme code="text.num.rows.med" /></li>
							<li data-value="<spring:theme code="text.num.rows.all.value" />" class="option"><spring:theme code="text.num.rows.all.label" /></li>
						</ul>
					</div>
				</div>
				<div class="row">&nbsp;</div>
				<cms:pageSlot position="BottomContentSlot" var="feature" element="div">
					<cms:component component="${feature}"/>
				</cms:pageSlot>
			</div>
		</div>
		<hr />
	</div>
	<!-- Products Excluded -->
 <div class="modal fade modal-out-of-stock" id="orderHistoryPopup" role="dialog">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-body">
          <h2><div id="orderHistoryPopUpError"></div></h2>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default back" data-dismiss="modal"><span class="glyphicon glyphicon-menu-left"></span>back</button>
        </div>
      </div>
    </div>
  </div>
</template:page>
<common:addItemsPopup/>