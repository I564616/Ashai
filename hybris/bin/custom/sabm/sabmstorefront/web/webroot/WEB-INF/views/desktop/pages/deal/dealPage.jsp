<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="deals" tagdir="/WEB-INF/tags/desktop/deals" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<template:page pageTitle="${pageTitle}">

<script id="dealsData" type="text/json">${ycommerce:generateJson(deals)}</script>
    <%--<deals:dummyData/>   --%>
	<div id="simulationErrors">
		 <%--<div class="alert negative"></div>--%>
	</div>
    <div id="globalMessages">
        <common:globalMessages/>
    </div>
    <cms:pageSlot position="TopContentSlot" var="feature">
        <cms:component component="${feature}"/>
    </cms:pageSlot>
    <br/>
    <div id="dealPageWrapper" class="row page-productList" ng-controller="dealsCtrl" data-dealsLoadInProgress="${dealsCallInProgress}" ng-init="init(true)" ng-cloak>
        <div class="col-xs-12">
            <deals:availDeals />
        </div>

        <div class="clearfix"></div>
            <div class="col-md-3 deal-filter clearfix">
                <div class="list-filter clearfix">
                    <div class="deals-refinements" ng-init="filterInit()">
                        <form action="#" name="dealDelivery" method="get" id="filter-form">
                                <hr class="blue-2">
                                <h2 class="offset-bottom-none">{{dealsfiltered.length}} Deals</h2>
                                <hr class="blue-2">
                                <div ng-show="brandsChecked.length || categoriesChecked.length || badgesChecked.length">
                                  <div class="filter-clear" ng-click="clearFilter()">CLEAR ALL SELECTIONS</div> 
                                  <hr class="blue-2">
                                </div>

                                <div class="toggle-slide" ng-show="hasBadges()" ng-init="badgesToggle = true">
                                  <h4 class="panel-title h4-alt toggle-head" ng-class="{open : badgesToggle}" ng-click="badgesToggle = !badgesToggle">Type of deal</h4>
                                  <div class="toggle-body" ng-show="badgesToggle">
                                      <ul>
                                          <li class="checkbox" ng-hide="!showing.badges && ($index + 1) > limitfilter.badges || badge == 4" ng-repeat="badge in badgesOnPage | filter:badgesFilterFilter | orderBy:badgeSorting:true as filteredBadges" >
                                                  <input class="facet-check" id="badge-{{$index}}" type="checkbox" ng-click="addRemoveBadges(badge)" value="badge"/>
                                                  <label class="facet_block-label text-left" for="badge-{{$index}}">
                                                  <span class="type-label" ng-show="badge == 0"><spring:theme code="text.deals.last.chance" /></span> 
                                                  <span class="type-label" ng-show="badge == 1"><spring:theme code="text.deals.last.agreedInstore" /></span>
                                                  <span class="type-label" ng-show="badge == 2"><spring:theme code="text.deals.last.limitedoffer" /></span>
                                                  <span class="type-label" ng-show="badge == 3"><spring:theme code="text.deals.last.onlineonly" /></span>
                                                  <span class="type-label" ng-show="badge == 5"><spring:theme code="text.deals.single.unit" /></span>
                                                  <span class="type-label" ng-show="badge == 6"><spring:theme code="text.deals.multi.unit" /></span>
                                                  <span class="type-label" ng-show="badge == 7"><spring:theme code="text.deals.bonus.stock" /></span>
                                                  <span class="type-label" ng-show="badge == 8"><spring:theme code="text.deals.discount" /></span>
                                                  <span class="filter-count">({{badgesMap[badge]}})</span></label>
                                          </li>
                                      </ul>
                                      <div class="moreFacetValues" ng-click="showing.badges = true" ng-show="filteredBadges.length > limitfilter.badges && !showing.badges">Show all</div>
                                  </div>
                                  <hr class="blue-2">
                                </div>
                                <div class="toggle-slide" ng-show="dealsfiltered.length" ng-init="categoryToggle = true">
                                    <h4 class="panel-title h4-alt toggle-head" ng-class="{open : categoryToggle}" ng-click="categoryToggle = !categoryToggle">Category</h4>
                                    <div class="toggle-body" ng-show="categoryToggle">
                                        <ul>
        	                                <li class="checkbox" ng-hide="!showing.categories && ($index + 1) > limitfilter.categories" ng-repeat="category in categoriesOnPage | filter:categoryFilterFilter | orderBy:categorySorting:true as filteredCategories" >
        	                                        <input class="facet-check" id="category-{{$index}}" type="checkbox" ng-click="addRemoveCategories(category)" value="category"/>
        	                                        <label class="facet_block-label" for="category-{{$index}}">{{category}} <span class="filter-count">({{categoriesMap[category]}})</span></label>
        	                                </li>
                                        </ul>
                                        <div class="moreFacetValues" ng-click="showing.categories = true"  ng-show="filteredCategories.length > limitfilter.categories && !showing.categories">Show all</div>
                                    </div>
                                </div>

								<div class="toggle-slide" ng-show="dealsfiltered.length" ng-init="brandsToggle = true">
									<hr class="blue-2">
                                    <h4 class="panel-title h4-alt toggle-head" ng-class="{open : brandsToggle}" ng-click="brandsToggle = !brandsToggle">Brand</h4>
                                    <div class="toggle-body" ng-show="brandsToggle">
                                        <ul>
        	                                <li class="checkbox" ng-hide="!showing.brands && ($index + 1) > limitfilter.brands" ng-repeat="brand in brandsOnPage | filter:brandFilterFilter | orderBy:brandSorting:true as filteredBrands" >
        	                                        <input class="facet-check" id="brand-{{$index}}" type="checkbox" ng-click="addRemoveBrands(brand)" value="{{brand}}"/>
        	                                        <label class="facet_block-label" for="brand-{{$index}}">{{brand}} <span class="filter-count">({{brandsMap[brand]}})</span></label>
        	                                </li>
                                        </ul>
                                        <div class="moreFacetValues" ng-click="showing.brands = true" ng-show="filteredBrands.length > limitfilter.brands && !showing.brands">Show all</div>
                                    </div>
                                </div>
                                
                                <hr class="blue-2">

                                <deals:calendars/>
                        </form>
                    </div>
                </div>
                <cms:pageSlot position="DealLeftRefinements" var="feature">
                    <cms:component component="${feature}"/>
                </cms:pageSlot>
            </div>
        <div class="col-md-9 deal-items">
            <deals:dealsRow/>
            <cms:pageSlot position="DealListSlot" var="feature">
                <cms:component component="${feature}"/>
            </cms:pageSlot>
        </div>
    </div>
    <cms:pageSlot position="BottomContentSlot" var="feature">
        <cms:component component="${feature}"/>
    </cms:pageSlot>
    <nav:backToTop/>
</template:page>
