<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>

<c:set var="tabsNum" value="2" />

<c:if test='${not empty deals and !isNAPGroup}'>
  <c:set var="tabsNum" value="${tabsNum + 1}" />
</c:if>
<c:if test='${not empty product.baseOptions and not empty product.baseOptions[0].options and fn:length(product.baseOptions[0].options) > 1}'>
  <c:set var="tabsNum" value="${tabsNum + 1}" />
</c:if>

<div id="tab-anchor"></div>
<div class="tab-body" ng-controller="PDPCtrl" ng-init="init()" ng-class="breakpoint.class">
  <!-- Tab Buttons -->

  <ul class="tab-buttons clearfix btn-${tabsNum}" ng-init="tab = ${(not empty deals and !isNAPGroup) ? '0' : '1'}">
  <c:if test='${not empty deals and !isNAPGroup}'>
    <li ng-class="{active : tab === 0}" ng-click="changeTab(0)">
      <spring:theme code="product.page.deals" />
    </li>
  </c:if>
    <li ng-class="{active : tab === 1}" ng-click="changeTab(1)">
      <spring:theme code="text.product.detail.tab.about.title" />
    </li>
    <c:if test='${not empty product.baseOptions and not empty product.baseOptions[0].options and fn:length(product.baseOptions[0].options) > 1}'>
      <li ng-class="{active : tab === 2}" ng-click="initSlider('otherPack'); trackPDPOtherPackOptions();">
        <spring:theme code="product.page.other.pack.options" />
    </li>
    </c:if>
      <li ng-class="{active : tab === 3}" ng-click="changeTab(3); trackPDPPackConfiguration();">
        <spring:theme code="text.product.detail.tab.packconfiguration.title" />
    </li>
  </ul> 

<c:if test='${not empty deals and !isNAPGroup}'>
  <div class="toggle-slide" ng-show="tab === 0 || breakpoint.windowSize < 990">
    <h3 id="accordianDeals" class="toggle-head" ng-class="{open : slideDeals}" ng-click="accordionClick('accordianDeals','slideDeals')">
      <spring:theme code="product.page.deals" />
    </h3>
    <div class="toggle-body" ng-show="slideDeals">
      <product:productW1Deals />
    </div>
  </div>
</c:if>

  <div class="toggle-slide" ng-show="tab === 1 || breakpoint.windowSize < 990">
    <h3 id="accordianAbout" class="toggle-head" ng-class="{open : slideAbout}" ng-click="accordionClick('accordianAbout','slideAbout')"><spring:theme code="text.product.detail.tab.about.title" /></h3>
    <div class="toggle-body" ng-show="slideAbout">
      <product:productDetailsTab product="${product}"/>
    </div>
  </div>

  <%-- If there is no variant won't display the Other Packages section  --%>
  <c:if test="${not empty product.baseOptions and not empty product.baseOptions[0].options and fn:length(product.baseOptions[0].options) > 1}">
  <div class="toggle-slide" ng-show="tab === 2 || breakpoint.windowSize < 990">
    <h3 id="accordianPack" class="toggle-head" ng-class="{open : slideOtherPack}" ng-click="initSlider('otherPack','accordianPack')">
      <spring:theme code="product.page.other.pack.options" />
    </h3>
    <div class="toggle-body" ng-show="slideOtherPack">
        <product:productOtherPackages product="${product}"/>
    </div>
  </div>
  </c:if>

  <div class="toggle-slide" ng-show="tab === 3 || breakpoint.windowSize < 990">
    <h3 id="accordianConfig" class="toggle-head" ng-class="{open : slidePackConfig}" ng-click="accordionClick('accordianConfig','slidePackConfig')"><spring:theme code="text.product.detail.tab.packconfiguration.title" /></h3>
    <div class="toggle-body" ng-show="slidePackConfig">
      <product:productPackConfigurationTab product="${product}"/>
    </div>
  </div>

  <c:if test="${not empty productReferences && !isNAPGroup}">
    <div class="toggle-slide related-products">
      <h3 id="accordianRelated" class="toggle-head" ng-class="{open : slideRelated}" ng-click="initSlider('related','accordianRelated')"><spring:theme code="product.you.may.like"/></h3>
          <div class="toggle-body" ng-show="slideRelated">
                <cms:pageSlot position="UpSelling" var="comp" element="div" class="">
                  <cms:component component="${comp}"/>
                </cms:pageSlot>
          </div>
    </div>
  </c:if>
</div>
