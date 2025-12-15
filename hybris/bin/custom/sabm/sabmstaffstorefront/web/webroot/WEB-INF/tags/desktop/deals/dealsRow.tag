<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common"%>

<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="tempImgPath" value="/_ui/desktop/SABMiller/img/placeholders/" />
<c:url value="1" var="firstBundle"/>
<c:set value="0" var="dealConditionCount"/>
<c:set value="" var="dealConditionProductCode"/>

<div class="deal" ng-repeat="deal in deals | filter:dealExpiredFilter" ng-init="dealInit(deal,false)">
	<div class="row deal-item-head">
		<div class="col-xs-12">
			<h2 data-ng-bind-html="getHtml(deal.title)" ></h2>
		</div>
		<div class="col-xs-12">
			<div class="row labels">
				<div class="col-xs-12 deal-daterange">
					<ul class="list-inline">
						<li ng-show="deal.badges.indexOf(4) >= 0"><div class="na-label small-text"><spring:theme code="text.deals.list.now.available" /></li>
						<li><span><spring:theme code="text.deals.valid.from" arguments="{{deal.validFrom | date:'dd/MM/yyyy'}},{{deal.validTo | date:'dd/MM/yyyy'}}"/></span></li>
					</ul>
						
					<div class="item-toggle text-right visible-sm-block visible-md-block visible-lg-block">
						<span class="inline" ng-click="toggleThis(deal)"><span ng-hide="deal.dealToggle"><spring:theme code="text.deals.list.view.hide" /></span><span ng-hide="!deal.dealToggle"><spring:theme code="text.deals.list.view.show" /></span> <spring:theme code="text.deals.list.view.details" /></span>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div class="row deal-item-body" ng-hide="deal.dealToggle" ng-init="deal.totalQty = calcTotalQty(deal)">
		<div class="col-xs-12">
		<!-- Ranges -->
		<div ng-repeat="range in deal.ranges"  ng-init="rangeInit(deal,range)">
			<div class="row range-title" ng-show="range.title">
				<div class="col-md-12"><h4>{{range.title}}</h4></div>
			</div>
			<!-- Base Product -->
			<div class="base-item" ng-repeat="base in range.baseProducts">

			<div class="row item">
				<div class="col-xs-2 visible-sm-block visible-md-block visible-lg-block">
					<a ng-href="{{base.url}}"><img ng-src="{{base.image}}" alt="Placeholder Image"></a>
				</div>

				<div class="col-xs-8 col-sm-5">
						<h4 class="clamp-2">{{base.title}}</h4>
						<div class="h4 h4-subheader clamp-1">{{base.packConfig}}</div>
				</div>
				<div class="col-xs-4 col-sm-4 text-right">
						<div class="num-free"><span>{{base.qty}} </span>
						<span class="mobile-block"></span>
						<span class="item-uom" ng-show="base.qty == 1">{{base.uomS}}</span><span class="item-uom" ng-show="base.qty > 1 || base.qty == 0">{{base.uomP}}</span></div>
				</div>

				
			</div>
			</div>
		</div>

		<!-- Free Product -->
		<div class="col-xs-12 free" ng-class="{'last':$last}" ng-repeat="free in deal.freeProducts">
			<div class="row item">
				<div class="col-xs-2 visible-sm-block visible-md-block visible-lg-block">
					<a ng-href="{{free.url}}"><img ng-src="{{free.image}}" alt="Placeholder Image"></a>
				</div>
				<div class="col-xs-8 col-sm-5">
						<h4>{{free.title}}</h4>
						<div class="h4 h4-subheader">{{free.packConfig}}</div>
				</div>
				<div class="col-xs-4 col-sm-4">
					<div class="item-qty clearfix">
						<div class="num-free"><span>{{mapQty(deal)}} </span>
						<span class="mobile-block"></span>
						<span class="item-uom"><spring:theme code="deal.page.free" /></span></div>
					</div>
				</div>
			</div>
		</div>

				<!-- Selected Products -->
			<div ng-show="deal.selectableProducts.length" class="free-list-title">
				<span><spring:theme code="deal.page.free.product.select.title"/></span>
			</div>
				<div class="col-xs-12 free" ng-class="{'last':$last}" ng-repeat="selectable in deal.selectableProducts">
					<div class="row item">
						<div class="col-xs-2 col-sm-1">
								<div class="radio">
								    <input id="freeSelect{{$parent.$index}}-{{$index}}" type="radio" name="freeSelect" ng-value="{{selectable.code}}" ng-click="deal.selectedItem = selectable.code">
								    <label for="freeSelect{{$parent.$index}}-{{$index}}"></label>
								</div>
						</div>
						<div class="col-xs-2 visible-sm-block visible-md-block visible-lg-block">
							<a ng-href="{{selectable.url}}"><img ng-src="{{selectable.image}}" alt="Placeholder Image"></a>
						</div>
						<div class="col-xs-7 col-sm-5 trim-right-5">
								<h4>{{selectable.title}}</h4>

								<div class="h4 h4-subheader">{{selectable.packConfig}}</div>
						</div>
						<div class="col-xs-3 trim-left-5">
							<div class="item-qty clearfix">

								<div class="num-free"><span>{{mapQty(deal)}} </span>
								<span class="mobile-block"></span>
								<span class="item-uom"><spring:theme code="deal.page.free" /></span></div>

							</div>
						</div>
					</div>
				</div>
			<hr class="hr-2">
		</div>

	</div>
		<div class="row deal-cta" ng-hide="userStatus === 'inactive'">

	    	<div class="col-sm-5 col-sm-offset-7 text-right">
	        	<div ng-show="!deal.active"><spring:theme code="staff.portal.deals.changes.active.tip"/></div>
	        	<input type="checkbox" ng-model="deal.active" ng-change="checkActive(deals[$index])" class="js-switch" ui-switch="{color: '#01B04F', secondaryColor: '#B6B6B6'}" />
			</div>
		</div>
</div>


<!-- Popup -->

<div id="confirmChanges" class="confirm-changes mfp-hide">
  <h2 class="h1"><spring:theme code="text.page.confirmChange.title" /></h2>
  <p class="offset-bottom-small"><spring:theme code="text.page.confirmChange.note" /></p>
  <button class="btn btn-primary offset-bottom-small" ng-click="addChangesDeal()"><spring:theme code="text.page.confirmChange.button" /></button>
  <c:url value="/backToCustomerSearchResults" var="backToCustomerSearchResultsLink"/>
  <a href="${backToCustomerSearchResultsLink}" class="inline offset-bottom-xsmall"><spring:theme code="text.page.confirmationSent.backToSearchResults" /></a><br>
  <div class="offset-bottom-xsmall"></div>
  <c:url value="/customer-search" var="customerSearchLink"/>
  <a href="${customerSearchLink}" class="inline"><spring:theme code="text.page.confirmationSent.backToCustomerSearch" /></a>
</div>