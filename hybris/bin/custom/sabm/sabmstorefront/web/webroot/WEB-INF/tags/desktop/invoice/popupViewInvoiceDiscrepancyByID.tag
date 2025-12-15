<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ attribute name="index" required="true" %>

<div id="invoiceDiscrepancyAlreadyRaisedPopup${index}" class="invoice-discrepancy-popup view-invoice-discrepancy-popup mfp-hide">
	<div class="container-fluid no-padding">
		<div class="row row-header">
			<div class="col-md-8 col-sm-9 title"><h2><spring:theme code="text.raisedinvoicediscrepancy.modal.invoicediscrepancy" /></h2></div>
			<div class="col-md-4 col-sm-3 refNo"><h2>
			<a href="#" onclick="$.magnificPopup.close()">
				<svg class="icon-close">
					<use xlink:href="#icon-close-white"></use>
				</svg>
			</a></h2></div>
		</div>
		
		<div class="row row-additional-info">
			<div class="col-md-12"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.invoicenumber" /> <span>{{modalData.invoiceNumber}}</span></h3></div>
			<div class="col-md-12"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.datesubmitted" /> <span>{{modalData.raisedDate}}</span></h3></div>
		</div>
		
		<div class="row row-body">
			<div class="col-md-12">
				<div class="row">
					<div class="col-md-6 col-sm-6 business-name"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.businessname"/></h3><p>{{modalData.soldTo}}</p></div>
					<div class="col-md-6 col-sm-6 raised-by"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.raisedby"/></h3><p>{{modalData.raisedBy}} <br /> {{modalData.raisedBy_email}}</p></div>
				</div>
				<div class="row">
					<div class="col-md-12 col-sm-6 type-of-issue">
						<h3><spring:theme code="text.raisedinvoicediscrepancy.modal.invoicetype" /></h3>
						<p ng-if="modalData.invoiceType == 'PRICE'"><spring:theme code="text.raisedinvoicediscrepancy.modal.pricediscrepancy" /></p>
						<p ng-if="modalData.invoiceType == 'FREIGHT'"><spring:theme code="text.raisedinvoicediscrepancy.modal.freightdiscrepancy" /></p>
					</div>
				</div>
				
				<div class="row row-items" ng-show="modalData.invoices.length > 0">
					<div class="col-md-12">
						<div class="row item-header container-fluid" ng-class="{'item-header-mobile': widthSize < mobileSize}">
							<div class="col-md-3 col-xs-3"><spring:theme code="text.raisedinvoicediscrepancy.modal.items" /></div>
							<div ng-show="modalData.invoiceType == 'PRICE'">
								<div class="col-md-1 col-xs-1 trim-left"><spring:theme code="text.raisedinvoicediscrepancy.modal.quantity" /></div>
								<div class="col-md-3 col-xs-3 trim-left"><spring:theme code="text.raisedinvoicediscrepancy.modal.discountreceived" /></div>
								<div class="col-md-3 col-xs-3 trim-left"><spring:theme code="text.raisedinvoicediscrepancy.modal.discountexpected" /></div>
								<!--- Desktop --->
								<div class="col-md-2 col-xs-2 trim-left" ng-show="widthSize >= mobileSize"><spring:theme code="text.raisedinvoicediscrepancy.modal.creditadjustmentexpected" /></div>
								<!--- Mobile --->
								<div class="col-md-2 col-xs-2 trim-left" ng-show="widthSize < mobileSize"><spring:theme code="text.raisedinvoicediscrepancy.modal.creditexpected" /></div>
							</div>
						</div>
					</div>
					<div class="col-md-12" ng-repeat="modalItem in modalData.invoices">
						<div class="row row-item container-fluid">
							<div class="col-md-3 col-xs-3">{{modalItem.itemDescriptionLine1}} <br /> {{modalItem.itemDescriptionLine2}}</div>
							<div class="col-md-1 col-xs-1 trim-left">{{modalItem.quantity | number: 0}}</div>
							<div class="col-md-3 col-xs-3 trim-left">{{'$'+(modalItem.discountReceived | number: 2)}}</div>
							<div class="col-md-3 col-xs-3 trim-left">{{'$'+(modalItem.discountExpected | number: 2)}}</div>
							<div class="col-md-2 col-xs-2 trim-left">{{'$'+(((modalItem.discountExpected - modalItem.discountReceived) * modalItem.quantity) | number: 2 ) }}</div>
						</div>
					</div>
				</div>
				<div class="row freight-section" ng-show="modalData.invoiceType == 'FREIGHT'">
					<div class="col-md-4 col-sm-4">
						<div class="form-group row">
							<label for="" class=" trim-right col-sm-7 col-xs-7 col-form-label"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.freightcharged" /></h3></label>
							<div class="col-sm-5  col-xs-5">
								<input type="text" class="form-control"  value="{{'$'+(modalData.freightChargedAmount | number: 2)}}" readonly />
							</div>
						</div>
					</div>
					<div class="col-md-4 col-sm-4">
						<div class="form-group row">
							<label for="" class=" trim-right col-sm-7 col-xs-7 col-form-label"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.freightexpected" /></h3></label>
							<div class="col-sm-5 col-xs-5">
								<input type="text" class="form-control"  value="{{'$'+(modalData.freightExpectedAmount | number: 2)}}" readonly />
							</div>
						</div>
					</div>
					<div class="col-md-4 col-sm-4">
						<div class="form-group row">
							<label for="" class=" trim-right col-sm-7 col-xs-7 col-form-label"><h3><spring:theme code="text.raisedinvoicediscrepancy.modal.creditadjustmentexpected" /></h3></label>
							<div class="col-sm-5 col-xs-5">
								<input type="text" class="form-control"  value="{{'$'+((modalData.freightChargedAmount - modalData.freightExpectedAmount) | number: 2)}}" readonly />
							</div>
						</div>
					</div>
				</div>	
							
				<div class="row">
					<div class="col-md-12 description">
						<h3>Description</h3>
						<p>{{modalData.requestDescription}}</p>			
					</div>
				</div>

				<div class="row">
					<div class="col-md-12 confirmation-email">
						<h3 style="text-transform: none"><spring:theme code="text.raisedinvoicediscrepancy.modal.confirmationemail" /></h3>
						<p ng-repeat="notification in modalData.notificationList track by $index">{{notification}}</p>
						
					</div>
				</div>
			</div>
		</div>
	</div>
</div>