<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="invoice" tagdir="/WEB-INF/tags/desktop/invoice"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<script id="raisedInvoicesList" type="text/json">${ycommerce:generateJson(raisedInvoiceslist)}</script> 

<h2><spring:theme code="text.raisedinvoicediscrepancy.header" /></h2>

<div ng-controller="raisedInvoiceDiscrepancyCtrl" ng-cloak>
<form class="container-fluid" name="form">

<div class="row r-row offset-bottom-medium update-filter">
	<div class="col-md-5">
		<div class="row">
		<div class="col-md-2 col-xs-2">
				<p  class="label-standarized"><spring:theme code="text.dropdown.for"/></p>
		</div>
		<div class="col-md-10 col-xs-10 offset-bottom-small">
			<div id="unitDropdownFilter" class="select-list">
				<c:choose>
					<c:when test="${customerData.isZadp and customerData.primaryAdmin}">								
		     			<div data-value="" class="select-btn js-billingunit" id="forUnit"><spring:theme code="text.billing.forunit.all" /></div>
					</c:when>
					<c:otherwise>								
		     			<div data-value="${user.currentB2BUnit.uid}" class="select-btn js-billingunit" id="forUnit">${user.currentB2BUnit.name }</div>
					</c:otherwise>
				</c:choose>
				<ul class="select-items" style="max-height:900%;overflow-y:auto;">
					<c:if test="${customerData.isZadp and customerData.primaryAdmin}">
						<li data-value="" data-text="${customerData.unit.name}"><spring:theme code="text.billing.forunit.all" /></li>
					</c:if>									
					<c:forEach items="${user.branches}" var="group">
			        	<li data-value="${group.uid }" data-text="<c:if test="${customerData.isZadp and customerData.primaryAdmin}"><spring:theme code="text.business.unit.prefix.name"/></c:if>${group.name }">${group.name }</li>	
					</c:forEach>
					
					<!-- only display the 'All' filter when there's 2 or more venue -->	
					<c:if test="${user.branches.size() > 1}">
						<li data-value="" data-text=""><spring:theme code="text.billing.forunit.all" /></li>
					</c:if>
	    			</ul>
			</div>
			</div>
		</div>
	</div>
	<div class="col-md-4 col-md-offset-1">
 		<div class="input-daterange clearfix" id="datepicker">
			<div class="row offset-bottom-xsmall">
				<div class="col-md-2 col-xs-2">
						<label for="invoicedate-start" class="label-standarized"><spring:theme code="text.from.datepicker"/></label>
				</div>
				<div class="col-md-7 col-xs-7">
					<div class="input-icon-group white-background">
						<input type="text" class="form-control invoicedate-start" data-value="" id="start" name="start" readonly="readonly" placeholder="<spring:theme code="text.from.datepicker.placeholder" />" />
						<svg class="icon-calendar icon-calendar-primary"><use xlink:href="#icon-calendar"></use></svg>
					</div>
			  </div>
		  </div>
		<div class="row">
			<div class="col-md-2 col-xs-2">
					<label for="invoicedate-end" class="label-standarized"><spring:theme code="text.to.datepicker"/></label>
			</div>
			<div class="col-md-7 col-xs-7 offset-bottom-small clearfix">
	   		<div class="input-icon-group white-background">
			    <input type="text" class="form-control invoicedate-end" readonly="readonly" data-value="" id="end" name="end" placeholder="<spring:theme code="text.to.datepicker.placeholder" />" />
			    <svg class="icon-calendar icon-calendar-primary">
			        <use xlink:href="#icon-calendar"></use>
			    </svg>
				</div>
			</div>
	    </div>
		</div>
		<input type="hidden" name="isCustomFilter" id="isCustomFilter" />
	</div>					
	<div class="col-md-2 ba-col clearfix offset-bottom-small">
		<a href="#" id="raisedInvoiceDiscrepancyUpdateFilter" class="btn btn-primary btn-simple pull-right"><spring:theme code="text.account.profile.saveUpdates" /></a>
	</div>
</div>
</form>

<div class="row">
	<div class="col-md-4 col-md-offset-8 search-component">
		<div class="input-icon-group">
			<input type="text" class="form-control" placeholder="Invoice No." ng-model="raisedInvoice.search" />
			<svg class="icon-search">
			    <use xlink:href="#icon-search"></use>
			</svg>
		</div>
	</div>
</div>
<br />

<div class="row">
	<div class="col-md-6 pagination-component">
		<ul uib-pagination boundary-links="true" class="pagination-sm" items-per-page="numPerPage" max-size="maxSize" total-items="filteredRaisedInvoices.length<=raisedInvoicesList.length ? filteredRaisedInvoices.length : raisedInvoicesList.length" ng-model="currentPage" num-pages="numPages" previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;" ng-change="pageChanged()" force-ellipses="true"  rotate="false"></ul>
	</div>
	<div class="col-md-6 text-right  num-rows">
		<ul class="list-inline">
			<li><spring:theme code="text.show.title" /></li>
			<li ng-click="numPerPage = 25" class="option" ng-class="{'active': numPerPage == 25}"><spring:theme code="text.num.rows.min" /></li>
			<li ng-click="numPerPage = 50" class="option" ng-class="{'active': numPerPage == 50}"><spring:theme code="text.num.rows.med" /></li>
			<li ng-click="numPerPage = totalItems" class="option" ng-class="{'active': numPerPage == totalItems}"><spring:theme code="text.num.rows.all.label" /></li>
		</ul>
	</div>
</div>

<table class="table tablesortable sortable-json billing-table desktop footable-loaded footable">
	<thead>
		<tr ng-show="widthSize >= mobileSize">
			<th class="footable-visible footable-sortable" ng-click="sortData('raisedOn')" ng-class="getSortClass('raisedOn')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.dateRaised"/> <span class="footable-sort-indicator"></span></th>
			<th class="footable-visible footable-sortable" ng-click="sortData('raisedBy')" ng-class="getSortClass('raisedBy')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.raisedBy"/> <span class="footable-sort-indicator"></span></th>
			<th class="footable-visible footable-sortable" ng-click="sortData('invoiceNumber')" ng-class="getSortClass('invoiceNumber')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.invoiceNo"/> <span class="footable-sort-indicator"></span></th>
			<th class="footable-visible footable-sortable" ng-click="sortData('invoiceType')" ng-class="getSortClass('invoiceType')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.invoiceType"/> <span class="footable-sort-indicator"></span></th>
			<th class="footable-visible footable-sortable" ng-click="sortData('creditAdjustmentStatus')" ng-class="getSortClass('creditAdjustmentStatus')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.status"/> <span class="footable-sort-indicator"></span></th>
			<th class="footable-visible footable-sortable discount-table-header" ng-click="sortData('expectedTotal')" ng-class="getSortClass('expectedTotal')"><spring:theme code="text.raisedinvoicediscrepancy.table.header.totalDiscountExpected"/>
				<i rel="tooltip" style="position: absolute; top: 30px; right: 20%;" title="<spring:theme code='text.raisedinvoicediscrepancy.value.exclude.taxes.deposits' />">
					<svg class="icon-price-info"><use xlink:href="#icon-price-info"></use></svg>
				</i>
				<span class="footable-sort-indicator" style="right: 10%"></span></th>
			<th><spring:theme code="text.raisedinvoicediscrepancy.table.header.actions"/></th>
		</tr>
		<tr  ng-show="widthSize < mobileSize">
			<th><spring:theme code="text.raisedinvoicediscrepancy.table.header.dateRaised"/></th>
			<th ng-show="orientation == 'Landscape'"><spring:theme code="text.raisedinvoicediscrepancy.table.header.invoiceNo"/></th>
			<th style="position: relative"><spring:theme code="text.raisedinvoicediscrepancy.table.header.totalDiscountExpected"/>
				<i rel="tooltip" style="position: absolute; top: 30px; right: 15%;" title="<spring:theme code='text.raisedinvoicediscrepancy.value.exclude.taxes.deposits' />">
					<svg class="icon-price-info"><use xlink:href="#icon-price-info"></use></svg>
				</i>
			</th>
			<th ng-show="orientation == 'Landscape'"><spring:theme code="text.raisedinvoicediscrepancy.table.header.status"/></th>
			<th><spring:theme code="text.raisedinvoicediscrepancy.table.header.actions"/></th>
		</tr>
	</thead>
	<tbody ng-show="widthSize >= mobileSize">
		<tr ng-repeat="data in filteredRaisedInvoices = (raisedInvoicesList | filter: {'invoiceNumber': raisedInvoice.search} | orderBy:sortColumn:reverseSort) | slice: (currentPage-1)*numPerPage:currentPage*numPerPage" ng-show="raisedInvoicesList.length > 0">
			<td>{{data.raisedDate}}</td>
			<td>{{data.raisedBy}}</td>
			<td>{{data.invoiceNumber}}</td>
			<td>{{data.invoiceType}}</td>
			<td>{{data.creditAdjustmentStatus}}</td>
			<td>
				<span ng-if="data.invoiceType == 'PRICE'">
					{{'$'+(data.expectedTotalAmount | number: 2)}}
				</span>
				<span ng-if="data.invoiceType == 'FREIGHT'">
					{{'$'+((data.freightChargedAmount - data.freightExpectedAmount) | number: 2)}}
				</span>
			</td>
			<td>
				<a href="#" ng-click="viewInvoiceDiscrepancyByIDPopup(data.invoiceNumber)">View</a>
				<invoice:popupViewInvoiceDiscrepancyByID index="{{data.invoiceNumber}}"/>
			</td>
		</tr>
		<tr ng-show="raisedInvoicesList.length == 0">
			<td colspan="8"><i>You have no raised invoice discrepancies</i></td>
		</tr>
	</tbody>
	<tbody ng-show=" widthSize < mobileSize">
		<tr ng-repeat="data in filteredRaisedInvoices = (raisedInvoicesList | filter: {'invoiceNumber': raisedInvoice.search} | orderBy:sortColumn:reverseSort) | slice: (currentPage-1)*numPerPage:currentPage*numPerPage" ng-show="raisedInvoicesList.length > 0">
			<td>{{data.raisedDate | date: 'dd/MM/yyyy'}}</td>
			<td ng-show="orientation == 'Landscape'">{{data.invoiceNumber}}</td>
			<td>
				<span ng-if="data.invoiceType == 'PRICE'">
					{{'$'+(data.expectedTotalAmount | number: 2)}}
				</span>
				<span ng-if="data.invoiceType == 'FREIGHT'">
					{{'$'+(data.freightExpectedAmount | number: 2)}}
				</span>
			</td>
			<td ng-show="orientation == 'Landscape'">{{data.creditAdjustmentStatus}}</td>
			<td>
				<a href="#" ng-click="viewInvoiceDiscrepancyByIDPopup(data.invoiceNumber)"><spring:theme code="text.raisedinvoicediscrepancy.view" /></a>
				<invoice:popupViewInvoiceDiscrepancyByID index="{{data.invoiceNumber}}"/>
			</td>
		</tr>
		<tr ng-show="raisedInvoicesList.length == 0">
			<td colspan="8"><i><spring:theme code="text.raisedinvoicediscrepancy.noRaiseInvoiceDiscrepancies" /></i></td>
		</tr>
	</tbody>
</table>

<div class="row">
	<div class="col-md-6 pagination-component">
		<ul uib-pagination boundary-links="true" class="pagination-sm" items-per-page="numPerPage" max-size="maxSize" total-items="filteredRaisedInvoices.length<=raisedInvoicesList.length ? filteredRaisedInvoices.length : raisedInvoicesList.length" ng-model="currentPage" num-pages="numPages" previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;" ng-change="pageChanged()" force-ellipses="true" rotate="false"></ul>
	</div>
</div>

</div>