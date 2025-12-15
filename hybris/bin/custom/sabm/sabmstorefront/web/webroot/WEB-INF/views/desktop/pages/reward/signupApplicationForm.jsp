<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/desktop/template" %>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/desktop/formElement"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>
<%@ taglib prefix="org-common" tagdir="/WEB-INF/tags/addons/commerceorgaddon/desktop/common" %>
<%@ taglib prefix="breadcrumb" tagdir="/WEB-INF/tags/desktop/nav/breadcrumb" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>

<c:set var="context" value="${pageContext.request.contextPath}" />

<spring:message code="text.rewards.signup.page.step3.mobile.placeholder" var="mobilePlaceholder"/>
<spring:message code="text.rewards.signup.page.step3.landline.placeholder" var="landlinePlaceholder"/>

<script id="b2bUnits" type="text/json">${ycommerce:generateJson(b2bUnits)}</script>
<script id="user" type="text/json">${ycommerce:generateJson(user)}</script>

<div id="globalMessages">
    <common:globalMessages/>
    <c:if test="${not empty param.message}">
    <div class="global-message ${param.messageType }">
        ${param.message }
    </div>
</c:if>
</div>

<div ng-controller="rewardsCtrl" ng-init="init(true)" ng-cloak>
    <h1><spring:theme code="text.rewards.signup.page.title"/></h1>
    <p><spring:theme code="text.rewards.signup.page.text"/></p>
    <form:form action="${context}/rewards/signup/" method="post" id="rewardsApplicationForm" name="rewardsForm" modelAttribute="rewardsApplicationForm">
        <input type="hidden" name="CSRFToken" value="${CSRFToken}">
        <input type="hidden" name="pointsAllToOneVenue" value="{{pointsAllToOneVenue}}">
        <form:input type="hidden" path="b2bUnitForPointsAllToOneVenue" value="{{b2bUnitForPointsAllToOneVenueId}}"/>
        <form:input type="hidden" path="uids" value="{{selectedUsers}}"/>

        <!-- Step 1 START -->
        <h2 ng-if="venueSingle">Step 1 : <spring:theme code="text.rewards.signup.page.step1.venues.single.title"/></h2>
        <h2 ng-if="venueMultiple">Step 1 : <spring:theme code="text.rewards.signup.page.step1.venues.multiple.title"/></h2>
        <div class="row box show-on-top">
            <div class="col-xs-12">
                <p class="highlight" ng-if="venueSingle"><spring:theme code="text.rewards.signup.page.step1.venues.single.label"/></p>
                <p class="highlight" ng-if="venueMultiple"><spring:theme code="text.rewards.signup.page.step1.venues.multiple.label"/></p>
            </div>
            <div class="col-xs-12 form-group" ng-if="venueSingle">
                <input type="text" class="form-control" id="single-venue" value="{{user.currentB2BUnit.name}}" readonly>
            </div>
            <div class="col-xs-12 radio" ng-show="venueMultiple">
                <input type="radio" name="manage-points" id="venues-multi" value="false" ng-model="pointsAllToOneVenue">
                <label for="venues-multi"><spring:theme code="text.rewards.signup.page.step1.venues.multiple.radio.label"/></label>
                <a class="pull-right" data-toggle="tooltip" data-placement="right auto" title="<spring:theme code='text.rewards.signup.page.step1.venues.multiple.radio.tooltip'/>">
                    <svg class="icon-help">
                        <use xlink:href="#icon-help"></use>
                    </svg>
                </a>
            </div>
            <div class="col-xs-12 radio" ng-show="venueMultiple">
                <input type="radio" name="manage-points" id="venues-single" value="true" ng-model="pointsAllToOneVenue">
                <label for="venues-single"><spring:theme code="text.rewards.signup.page.step1.venues.single.radio.label"/></label>
                <a class="pull-right" data-toggle="tooltip" data-placement="right auto" title="<spring:theme code='text.rewards.signup.page.step1.venues.single.radio.tooltip'/>">
                    <svg class="icon-help">
                        <use xlink:href="#icon-help"></use>
                    </svg>
                </a>
            </div>
            <div class="col-xs-12 form-group" ng-if="pointsAllToOneVenue == 'true'">
                <hr>
                <label for="venue-search"><spring:theme code="text.rewards.signup.page.step1.venues.multiple.input.label"/></label>
                <div class="input-group search venues">
                    <input type="text" name="venueSearch" class="form-control" id="venue-search" placeholder="<spring:theme code='text.rewards.signup.page.step1.venues.multiple.input.placeholder'/>" maxlength="100" ng-model="b2bUnitForPointsAllToOneVenue" ng-focus="venueSearch()" autofocus required>
                    <div class="input-group-addon search-icon">
                        <svg class="icon-search">
                            <use xlink:href="#icon-search"></use>
                        </svg>
                    </div>
                </div>
            </div>
            <div class="col-xs-12" ng-show="pointsAllToOneVenue == 'true'">
                <p ng-if="venueSingle"><spring:theme code="text.rewards.signup.page.step1.venues.single.text"/></p>
                <p class="required"><spring:theme code="text.rewards.signup.page.step1.venues.multiple.required"/><span ng-if="rewardsForm.venueSearch.$error.required && rewardsForm.venueSearch.$touched" class="red"><spring:theme code="text.rewards.signup.page.step1.venues.multiple.input.invalid"/></span></p>
            </div>
        </div>
        <!-- Step 1 END -->

        <!-- Step 2 START -->
        <h2 ng-if="venueSingle">Step 2 : <spring:theme code="text.rewards.signup.page.step2.users.single.title"/></h2>
        <h2 ng-if="venueMultiple">Step 2 : <spring:theme code="text.rewards.signup.page.step2.users.multiple.title"/></h2>
        <div class="row box">
            <div class="col-xs-12">
                <a class="pull-right" data-toggle="tooltip" data-placement="auto right" title="<spring:theme code='text.rewards.signup.page.step2.users.multiple.label.tooltip'/>">
                    <svg class="icon-help">
                        <use xlink:href="#icon-help"></use>
                    </svg>
                </a>
                <p class="highlight" ng-if="venueSingle"><spring:theme code="text.rewards.signup.page.step2.users.single.label"/></p>
                <p class="highlight" ng-if="venueMultiple"><spring:theme code="text.rewards.signup.page.step2.users.multiple.label"/></p>
            </div>
            <div class="col-xs-12 form-group" ng-if="venueSingle">

                <input type="text" class="form-control" id="single-user" value="{{user.firstName}}&nbsp;{{user.lastName}}" readonly>
            </div>
            <div class="col-xs-12" ng-if="venueMultiple">
                <p class="text-right users-selected">{{selectedUsersQty}} <spring:theme code="text.rewards.signup.page.step2.users.multiple.list.selected"/></p>
                <div class="form-group margin-0 search users">
                    <div class="input-group">
                        <input type="text" ng-model="rewards.users.search" name="userSearch" class="form-control" id="users-search" placeholder="<spring:theme code='text.rewards.signup.page.step2.users.multiple.placeholder'/>">
                        <div class="input-group-addon search-icon">
                            <svg class="icon-search">
                                <use xlink:href="#icon-search"></use>
                            </svg>
                        </div>
                    </div>
                </div>
                <div class="list-group users-list">
                    <div class="list-group-item">
                        <div class="checkbox disabled margin-0">
                            <input type="checkbox" name="user-current" id="user-current" value="{{user.uid}}|{{user.currentB2BUnit.uid}}" disabled checked>
                            <label for="user-current">{{user.firstName}}&nbsp;{{user.lastName}}&nbsp;<em><spring:theme code="text.rewards.signup.page.step2.users.multiple.this"/></em></label>
                        </div>
                    </div>
                    <div ng-repeat="venue in b2bUnits | orderBy:'name'" ng-show="(venue.customers | filter: rewards.users.search).length">
                        <div class="list-group-item disabled" ng-if="(venue.length > 1) || (venue.customers.length > 0)">{{venue.name}}</div>
                        <div class="list-group-item" ng-repeat="customer in venue.customers | filter: rewards.users.search | orderBy:'name'">
                            <div class="checkbox margin-0">
                                <input type="checkbox" name="user{{customer.gaUid}}{{venue.uid}}" id="user-{{customer.gaUid}}-{{venue.uid}}" ng-model="customer.canAccessRewards" ng-change="userSelect(customer)">
                                <label for="user-{{customer.gaUid}}-{{venue.uid}}">{{customer.name}}</label>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-xs-12">
                <p ng-if="venueSingle"><spring:theme code="text.rewards.signup.page.step2.users.single.text"/></p>
                <p ng-if="venueMultiple"><spring:theme code="text.rewards.signup.page.step2.users.multiple.text"/></p>
            </div>
            <div ng-if="rewardsForm.venueSearch.$error.required && rewardsForm.venueSearch.$touched" class="step-disabled"></div>
        </div>
        <!-- Step 2 END -->

        <!-- Step 3 START -->
        <h2><spring:theme code="text.rewards.signup.page.step3.title"/></h2>
        <div class="row box">
            <div class="col-xs-12">
                <p class="highlight">
                    <spring:theme code="text.rewards.signup.page.step3.text"/>
                </p>
            </div>
            <div class="col-sm-4 form-group">
                <label for="rewards-mobile-number"><spring:theme code="text.rewards.signup.page.step3.mobile"/></label>
                <form:input path="mobileNumber" type="text" class="form-control" id="rewards-mobile-number" maxlength="12" placeholder="${mobilePlaceholder}" ng-model="contactMobile" ng-pattern="/^04(\s?[0-9]{2}\s?)([0-9]{3}\s?[0-9]{3}|[0-9]{2}\s?[0-9]{2}\s?[0-9]{2})$/" ng-required="!contactLandline" />
            </div>
            <div class="col-sm-4 form-group">
                <label for="rewards-landline-number"><spring:theme code="text.rewards.signup.page.step3.landline"/></label>
                <form:input path="landline" type="text" class="form-control" id="rewards-landline-number" maxlength="12" placeholder="${landlinePlaceholder}" ng-model="contactLandline" ng-pattern="/^(?:\+?(61))? ?(?:\((?=.*\)))?(0?[2-57-8])\)? ?(\d\d(?:[- ](?=\d{3})|(?!\d\d[- ]?\d[- ]))\d\d[- ]?\d[- ]?\d{3})$/" ng-required="!contactMobile" />
            </div>
            <div class="col-xs-12">
                <p class="required">
                    <spring:theme code="text.rewards.signup.page.step3.required"/>
                    <span ng-if="rewardsForm.mobileNumber.$error.pattern && rewardsForm.mobileNumber.$pristine" class="red"><spring:theme code="text.rewards.signup.page.step3.mobile.invalid"/></span>
                    <span ng-if="rewardsForm.landline.$error.pattern && rewardsForm.landline.$pristine" class="red"><spring:theme code="text.rewards.signup.page.step3.landline.invalid"/></span>
                </p>
            </div>
        </div>
        <!-- Step 3 END -->
        <!-- Terms & Conditions START -->
        <div class="row box">
            <div class="col-xs-12">
                <h2 class="highlight"><spring:theme code="text.rewards.signup.page.tac.title"/></h2>
                <div class="checkbox offset-bottom-small">
                    <input type="checkbox" name="rewardsTaCone" id="rewards-tac-one" ng-model="rewards.tac.one" required>
                    <label for="rewards-tac-one"><spring:theme code="text.rewards.signup.page.tac.box1.label"/></label>
                </div>
                <div class="checkbox offset-bottom-small">
                    <input type="checkbox" name="rewardsTaCtwo" id="rewards-tac-two" ng-model="rewards.tac.two" required>
                    <label for="rewards-tac-two"><spring:theme code="text.rewards.signup.page.tac.box2.label"/></label>
                </div>
            </div>
            <div class="col-xs-12">
                <p class="required">
                    <spring:theme code="text.rewards.signup.page.tac.required"/>
                    <span ng-if="(rewardsForm.rewardsTaCone.$dirty || rewardsForm.rewardsTaCtwo.$dirty) && (rewardsForm.rewardsTaCone.$invalid || rewardsForm.rewardsTaCtwo.$invalid)" class="red"><spring:theme code="text.rewards.signup.page.tac.invalid"/></span>
                </p>
            </div>
        </div>
        <!-- Terms & Conditions END -->
        <a href="#rewards-exit-application" class="btn btn-cancel regular-popup" role="button"><spring:theme code="text.rewards.signup.page.button.cancel"/></a>
        <button type="submit" class="btn btn-primary" title="<spring:theme code='text.rewards.signup.page.button.submit.title'/>" ng-disabled="'${bdeUser}'=='true' || rewardsForm.$invalid"><spring:theme code="text.rewards.signup.page.button.submit"/></button>
    </form:form>
</div>