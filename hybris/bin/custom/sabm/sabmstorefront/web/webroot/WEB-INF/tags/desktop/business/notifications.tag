<%@ taglib prefix="business" tagdir="/WEB-INF/tags/desktop/business"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<script id="notificationData" type="text/json">${ycommerce:generateJson(notification)}</script>
<%--<business:dummyData />--%>

<br/>

<div ng-controller="notificationsCtrl" class="page-notifications" ng-init="init()" ng-cloak>

	<h1 class="pull-left"><spring:theme code="text.notification.header.title"/></h1>
	<button ng-show="$parent.breakpoint.windowSize > 767" class="btn btn-primary btn-invert pull-right" ng-click="resetNotifications();invokeUnsavedChangesPopup();"><spring:theme code="text.notification.turnOff"/></button>
	<div class="clearfix"></div>
	<h2><spring:theme code="text.notification.manage"/>&nbsp;<span>${user.currentB2BUnit.name}</span></h2>

	<span class="learn-more link-cta" ng-click="learnMore = !learnMore" ng-show="$parent.breakpoint.windowSize < 768">Learn more</span>

	<p ng-show="learnMore || $parent.breakpoint.windowSize > 767"><spring:theme code="text.notification.paragraph1"/></p>
	<p ng-show="learnMore || $parent.breakpoint.windowSize > 767"><spring:theme code="text.notification.paragraph2"/></p>

	<div class="row">
		<div class="col-md-12">
			<div id="mobileNumberShowError" class="alert negative"><spring:theme code="text.notification.mobileNumber.errorMessage"/></div>
		</div>
		<div class="col-md-12">
			<div id="mobileNumberInput" class="alert negative"><spring:theme code="text.notification.mobileNumber.input"/></div>
		</div>
	</div>

	<div class="panel panel-primary panel-mobile-number">
		<div class="panel-body">
			<div class="row" id="showMobileNumberField">
				<div class="col-md-8">
					<span class="enter-mobile-text"><spring:theme code="text.notification.enterMobileNumber"/></span>
				</div>
				<div class="col-md-4">
					<form name="form">
						<input type="text" ng-model="mobileNumber" id="mobileNumberField" maxlength="12" placeholder="Enter your mobile number" class="form-control" ng-change="invokeUnsavedChangesPopup()" />
					</form>
				</div>
			</div>
			<div class="row" id="showMobileNumberLink">
				<div class="col-md-12">
					<span class="enter-mobile-text"> <spring:theme code="text.notification.enterMobileNumber"/>&nbsp;&nbsp;<a href="/your-business/profile" class="link" style="text-decoration: underline">View and Update</a>  your contact details</span>
				</div>
			</div>

		</div>
	</div>

	<p><spring:theme code="text.notification.notice"/></p>
<c:if test="${!isNAPGroup}">
	<section id="deals-promotions">
		<h2><span><spring:theme code="text.notification.deals.promotions"/></span></h2>
		<div class="row">
			<div class="col-xs-12 col-sm-9 col-md-9">
				<h3>
					<spring:theme code="text.notification.deals" />
					<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code='text.notification.deals.tooltip' />"></i>
				</h3>
			</div>



			<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
				<div class="checkbox checkbox-inline">
					<input type="hidden" id="deals.notificationEnabled" value="{{deals.emailEnabled}}" />
					<input id="deals.emailEnabled"  type="checkbox" ng-model="deals.emailEnabled" ng-change="invokeUnsavedChangesPopup()" >
					<label for="deals.emailEnabled" class="label-text">
						<spring:theme code="text.notification.email"/>
					</label>
				</div>
			</div>
			
			<div class="clearfix"></div>
			
			<!-- it will display when email checkbox is ticked -->
			<div class="col-md-12" ng-show="deals.emailEnabled">
				<form action="" class="form-inline">
					<div class="form-group-inline" >
						<label for="inputPassword">
							<p><spring:theme code="text.notification.deals.choose" /></p>
						</label>
						&nbsp;&nbsp;
						<select name="uom" class ="form-control form-inline" ng-model="deals.duration" ng-change="invokeUnsavedChangesPopup()">
							<option ng-repeat="days in deals.durationList" value="{{days}}">
								{{days}}
							</option>
						</select>
					</div>
				</form>
			</div>

		</div>
	</section>
	

	<hr class="hr-1" />

	<section id="orders-cutoff-confirmation">
		<h2><span><spring:theme code="text.notification.orderscutoff.heading"/></span></h2>
		<div class="row">
			<div class="col-xs-12 col-sm-9 col-md-9">
				<h3>
					<spring:theme code="text.notification.orderscutoff" />
					<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.cutofftime.tooltip" />"></i>
				</h3>
			</div>
			<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
				<input type="hidden" id="orders.notificationEnabled" value="{{orders.smsEnabled || orders.emailEnabled}}" />
				<div class="checkbox checkbox-inline">
					<input id="orders.smsEnabled" type="checkbox" ng-model="orders.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
					<label for="orders.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
					</label>
				</div>
				<div class="checkbox checkbox-inline">
					<input id="orders.emailEnabled" type="checkbox" ng-model="orders.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
					<label for="orders.emailEnabled" class="label-text">
						<spring:theme code="text.notification.email"/>
					</label>
				</div>
			</div>

			<div class="clearfix"></div>

			<!-- it will display when email checkbox is ticked -->
			<div class="col-md-12" ng-show="orders.emailEnabled || orders.smsEnabled">
				<form action="" class="form-inline">
					<div class="form-group-inline" >
						<label>
							<p><spring:theme code="text.notification.order.when.title" /></p>
						</label>
						&nbsp;&nbsp;
						<select id="order_duration" name="uom" class ="form-control form-inline" ng-model="orders.duration" ng-change="invokeUnsavedChangesPopup()" >
							<option ng-repeat="d in orders.durationList" value="{{d}}">
								{{getDropDownLabel(d)}}
							</option>
						</select>
						&nbsp;&nbsp;
						<label>
							<p><spring:theme code="text.notification.order.beforeCutoff" /></p>
						</label>

					</div>
				</form>
			</div>

		</div>
	</section>
	
	</c:if>

	<hr class="hr-1" />

	<section>
	<%-- <h2><span><spring:theme code="text.notification.orderdispatched.heading"/></span></h2> --%>
	<c:choose>
         <c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
			<h2><span><spring:theme code="text.notification.orderbeingdispatched.heading"/></span></h2>	
		</c:when>						
		<c:otherwise>
			<h2><span><spring:theme code="text.notification.orderdispatched.heading"/></span></h2>
		</c:otherwise>
	</c:choose>
	<div class="row">

		<div class="col-xs-12 col-sm-9 col-md-9">
		<h3>
			<%-- <spring:theme code="text.notification.orderdispatched" /> --%>
			<c:choose>
		         <c:when test="${isTrackDeliveryOrderFeatureEnabled}">															
					<spring:theme code="text.notification.orderbeingdispatched" />
				</c:when>						
				<c:otherwise>
					<spring:theme code="text.notification.orderdispatched" />
				</c:otherwise>
			</c:choose>
			<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.orderdispatched.tooltip" />"></i>
		</h3>
	</div>
	<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
		<input type="hidden" id="delivery.notificationEnabled" value="{{delivery.emailEnabled || delivery.smsEnabled}}" />
		<div class="checkbox checkbox-inline">
			<input id="delivery.smsEnabled" type="checkbox" ng-model="delivery.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
			<label for="delivery.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
			</label>
		</div>
		<div class="checkbox checkbox-inline">
			<input id="delivery.emailEnabled" type="checkbox" ng-model="delivery.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
			<label for="delivery.emailEnabled" class="label-text">
				<spring:theme code="text.notification.email"/>
			</label>
		</div>
	</div>
	</div>
	</section>
	<div class="w-100"></div>
	<input type="hidden" id="isTrackDeliveryOrderFeatureEnabled" value="${isTrackDeliveryOrderFeatureEnabled}" />
	<div ng-show="{{isTrackDeliveryOrderFeatureEnabled}}">
		<hr class="hr-1" />

		<section id="track-your-delivery">
			<h2><span><spring:theme code="text.notification.track.your.delivery"/></span></h2>
			<div class="row">
				<div class="col-xs-12 col-sm-9 col-md-9">
					<h3>
						<spring:theme code="text.notification.intransit" />
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.intransit.tooltip" />"></i>
					</h3>
				</div>
				<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
					<input type="hidden" id="intransit.notificationEnabled" value="{{intransit.emailEnabled || intransit.smsEnabled}}" />
					<div class="checkbox checkbox-inline">
						<input id="intransit.smsEnabled" type="checkbox" ng-model="intransit.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="intransit.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
						</label>
					</div>
					<div class="checkbox checkbox-inline">
						<input id="intransit.emailEnabled" type="checkbox" ng-model="intransit.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="intransit.emailEnabled" class="label-text">
							<spring:theme code="text.notification.email"/>
						</label>
					</div>
				</div>

				<div class="w-100"></div>



				<div class="col-xs-12 col-sm-9 col-md-9">
					<h3>
						<spring:theme code="text.notification.nextdelivery" />
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.nextdelivery.tooltip" />"></i>
					</h3>
				</div>
				<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
					<input type="hidden" id="nextInQueue.notificationEnabled" value="{{nextInQueue.smsEnabled || nextInQueue.emailEnabled}}" />
					<div class="checkbox checkbox-inline">
						<input id="nextInQueue.smsEnabled" type="checkbox" ng-model="nextInQueue.smsEnabled" ng-change="invokeUnsavedChangesPopup()">
						<label for="nextInQueue.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
						</label>
					</div>
					<div class="checkbox checkbox-inline">
						<input id="nextInQueue.emailEnabled" type="checkbox" ng-model="nextInQueue.emailEnabled" ng-change="invokeUnsavedChangesPopup()">
						<label for="nextInQueue.emailEnabled" class="label-text">
							<spring:theme code="text.notification.email"/>
						</label>
					</div>
				</div>

				<div class="w-100"></div>

				<div class="col-xs-12 col-sm-9 col-md-9">
					<h3>
						<spring:theme code="text.notification.informETA" />
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.informETA.tooltip" />"></i>
					</h3>
				</div>
				<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
					<input type="hidden" id="updateForETA.notificationEnabled" value="{{updateForETA.smsEnabled || updateForETA.emailEnabled}}" />
					<div class="checkbox checkbox-inline">
						<input id="updateForETA.smsEnabled" type="checkbox" ng-model="updateForETA.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="updateForETA.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
						</label>
					</div>
					<div class="checkbox checkbox-inline">
						<input id="updateForETA.emailEnabled" type="checkbox" ng-model="updateForETA.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="updateForETA.emailEnabled" class="label-text">
							<spring:theme code="text.notification.email"/>
						</label>
					</div>
				</div>

				<div class="w-100"></div>

				<div class="col-xs-12 col-sm-9 col-md-9">
					<h3>
						<spring:theme code="text.notification.delivered" />
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.delivered.tooltip" />"></i>
					</h3>

				</div>
				<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
					<input type="hidden" id="delivered.notificationEnabled" value="{{delivered.smsEnabled || delivered.emailEnabled}}" />
					<div class="checkbox checkbox-inline">
						<input id="delivered.smsEnabled" type="checkbox" ng-model="delivered.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="delivered.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
						</label>
					</div>
					<div class="checkbox checkbox-inline">
						<input id="delivered.emailEnabled" type="checkbox" ng-model="delivered.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="delivered.emailEnabled" class="label-text">
							<spring:theme code="text.notification.email"/>
						</label>
					</div>
				</div>

			</div>
		</section>

	</div>
	<div class="w-100"></div>
	<input type="hidden" id="isInvoiceDiscrepancyEnabled" value="${isInvoiceDiscrepancyEnabled}" />
	<c:if test="${!isNAPGroup}">
	<hr class="hr-1" />
	<sec:authorize access="hasAnyRole('ROLE_B2BADMINGROUP,ROLE_B2BINVOICECUSTOMER')">
	<c:if test="${isInvoiceDiscrepancyEnabled}">
		<section id="credit-adjustment">
			<h2><span><spring:theme code="text.notification.credit.adjustment"/></span></h2>
			<div class="row">
				<div class="col-xs-12 col-sm-9 col-md-9">
					<h3>
						<spring:theme code="text.notification.credit.processed" />
						<i class="icon icon-price-info" rel="tooltip" title="<spring:theme code="text.notification.credit.adjustment.tooltip" />"></i>
					</h3>
				</div>
				<div class="col-xs-12 col-sm-3 col-md-3 sms-email-cbox-component text-right">
					<input type="hidden" id="creditprocessed.notificationEnabled" value="{{creditprocessed.smsEnabled || creditprocessed.emailEnabled}}" />
					<div class="checkbox checkbox-inline">
						<input id="creditprocessed.smsEnabled" type="checkbox" ng-model="creditprocessed.smsEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="creditprocessed.smsEnabled" class="label-text">
							<spring:theme code="text.notification.sms"/>
						</label>
					</div>
					<div class="checkbox checkbox-inline">
						<input id="creditprocessed.emailEnabled" type="checkbox" ng-model="creditprocessed.emailEnabled" ng-change="invokeUnsavedChangesPopup()" />
						<label for="creditprocessed.emailEnabled" class="label-text">
							<spring:theme code="text.notification.email"/>
						</label>
					</div>
				</div>
		</section>
		<hr class="hr-1" />
	</c:if>
	</sec:authorize>
	</c:if>

	<section id="button-component">
		<div class="row">
			<div class="col-xs-8 trim-right">
				<button class="visible-xs btn btn-primary btn-invert pull-right" ng-click="resetNotifications();invokeUnsavedChangesPopup()"><spring:theme code="text.notification.turnOff"/></button>
			</div>
			<div class="col-xs-4 trim-left-5">
				<button ng-disabled="!notificationFormValid" ng-click="submit();switchOffUnsavedChangesPopup()" class="btn btn-primary pull-right">Save</button>
			</div>
		</div>
	</section>

	<!-- Unsaved Changes Modal -->
	<div class="modal fade modal-unsaved-changes" id="unsavedNotification" role="dialog">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-body">
					<h2><spring:theme code="text.notification.modal.confirmation.question"/></h2>
					<p><button class="btn btn-primary submit-notification" ng-click="submitFromPopup();switchOffUnsavedChangesPopup()" data-dismiss="modal"><spring:theme code="text.notification.modal.submit"/></button></p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-default back" data-dismiss="modal"><span class="glyphicon glyphicon-menu-left"></span><spring:theme code="text.notification.modal.back"/></button>
				</div>
			</div>
		</div>
	</div>

</div>

