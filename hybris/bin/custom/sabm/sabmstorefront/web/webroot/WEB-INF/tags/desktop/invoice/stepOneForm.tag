<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<form name="form">
  <div class="form-group">
    <label for=""><spring:theme code="text.invoicediscrepancy.typeofissue"/></label>
    <div class="row">
    	<div class="col-md-7 col-sm-7">
       		<div class="custom-dropdown typeOfIssue-dropdown">
       			<div class="input-mask" ng-class="{'open': showtypeOfIssueDropdown}" ng-click="showtypeOfIssueDropdown=!showtypeOfIssueDropdown">
	    			<input type="text" name="typeOfIssue" autocomplete="off" placeholder="<spring:theme code="text.invoicediscrepancy.pleaseselectissue" />" ng-model="typeOfIssue" id="typeOfIssue" class="form-control" required readonly />
		    	</div>
		    	<ul class="list-group" ng-show="showtypeOfIssueDropdown == true" ng-click="showtypeOfIssueDropdown=false;">
				  <li class="list-group-item" data-value="{{type}}" ng-click="selectFromDropdown($event, type); validateForm()" ng-repeat="type in typeOfIssueList track by $index">{{type}}</li>
				</ul>
			</div>
    	</div>
    	<div class="col-md-5 col-sm-5 form-link-paragraph">
    		<spring:theme code="text.invoicediscrepancy.differentissuetype"/> <br />
    		<a href="/serviceRequest#contact-us" class="data-link" onClick="trackCreditDiscrepancyLink(event)" data-action="click" data-label="Log Your Issue" ><spring:theme code="text.invoicediscrepancy.logyourissue"/></a>.
    	</div>
    </div>
  </div>
  <div class="form-group">
    <label for=""><spring:theme code="text.invoicediscrepancy.invoicenumber"/></label>
    <div class="row">
       	<div class="col-md-4 col-sm-4">
       		<div class="custom-dropdown">
    			<input type=text class="form-control" autocomplete="off" ng-change="validateForm()" ng-pattern="/^[0-9]{1,25}$/" name="invoiceNumber" id="invoiceNumber" ng-model="invoiceNumber" required />
		    	<ul class="list-group invoice-list-group" ng-show="form.invoiceNumber.$valid && form.invoiceNumber.$dirty">
				  <li class="list-group-item" data-value="{{invoice}}" ng-click="selectFromDropdown($event, invoice); validateForm(); form.invoiceNumber.$setPristine();" ng-repeat="invoice in invoiceList | limitTo: 5 | filter: invoiceNumber track by $index">{{invoice}}</li>
				</ul>
       		</div>
       		<span ng-show="isInvoiceNumberNotExist" style="color: red"><spring:theme code="text.invoicediscrepancy.invoicenotexist" /></span>			       		
    	</div>
    	<div class="col-md-8 col-sm-8  form-link-paragraph">
    		<spring:theme code="text.invoicediscrepancy.donthaveinvoicenumber"/> <br /> 
    		<a href="/your-business/billing"  class="data-link" onClick="trackCreditDiscrepancyLink(event)" data-action="click" data-label="View Your Invoices"><spring:theme code="text.invoicediscrepancy.viewyourinvoices"/></a>
    	</div>
    </div>
  </div>
</form>