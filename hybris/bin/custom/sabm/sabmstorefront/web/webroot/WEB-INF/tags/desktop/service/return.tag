<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="PRODUCT_RETURN" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have products that you would like to return, because they are damaged, incorrect or you have ordered too much.</p>
		
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="invoice-num">Invoice number<span class="required">*</span></label>
				<input id="invoice-num" ng-pattern="/^\d+$/" minlength= "10" maxlength= "10" name="return_invoicenum" class="form-control" type="text" ng-model="${srType}.invoicenumber" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="serviceRequest.return_invoicenum.$error.pattern">Your invoice number consists of numbers only</span>
				<span class="error" ng-show="serviceRequest.return_invoicenum.$error.minlength">Your invoice number must be 10 digits </span>
				<span class="error" ng-show="(notComplete || serviceRequest.return_invoicenum.$touched) && (sr.type == '${srType}') && serviceRequest.return_invoicenum.$invalid">Please enter the invoice number above</span>
			</div>
			<div class="col-sm-6 col-md-3 form-group">
				<label for="invoice-date">Invoice date<span class="required">*</span></label>
				<div class="select">
					<span class="arrow"></span>
					<input id="invoice-date" name="invoice_date" placeholder="Select" data-container=".form-group" class="form-control basic-datepicker" type="text" readonly="readonly" ng-model="${srType}.invoicedate" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="(notComplete || serviceRequest.invoice_date.$touched) && (sr.type == '${srType}') && serviceRequest.invoice_date.$invalid">Please select an option above</span>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="description">Product description<span class="required">*</span></label>
				<input id="description" name="description" class="form-control" type="text" ng-model="${srType}.description" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="(notComplete || serviceRequest.description.$touched) && (sr.type == '${srType}') && serviceRequest.description.$invalid">Please enter the product description above</span>
				
			</div>

			<div class="col-sm-6 col-md-3 form-group">
				<label>Product quantity<span class="required">*</span></label>
				<div class="row">
					<div class="col-xs-6 trim-right-5">
						<ul class="select-quantity">
							<li class="down disabled" qty-selector-service>
								<svg class="icon-minus">
								    <use xlink:href="#icon-minus"></use>    
								</svg>
							</li>
							<li><input class="qty-input" type="tel" ng-init="${srType}.qty = 1" ng-value="${srType}.qty" data-val="qty" data-scope="${srType}" data-minqty="1" maxlength="3" pattern="\d*"></li>
							<li class="up" qty-selector-service>
								<svg class="icon-plus">
								    <use xlink:href="#icon-plus"></use>    
								</svg>
							</li>
						</ul>
					</div>
					<div class="col-xs-6 trim-left-5">
	             		<div class="select">
	  					 	<span class="arrow"></span>
	             			<select name="uom" class ="form-control" ng-model="${srType}.uom" ng-required="sr.type == '${srType}'">
	             				<option value="" disabled selected>Select</option>
	             				<option value="Case">Case</option>
	             				<option value="Keg">Keg</option>
	             				<option value="Bottle">Bottle</option>
	             				<option value="Can">Can</option>
							</select>
							<span class="error" ng-show="(notComplete ||serviceRequest.uom.$touched) && sr.type == '${srType}' && serviceRequest.uom.$invalid">Please select an option above</span>
	  					
	  					</div>
					</div>
				</div>
			</div>
		</div>
		
		<div class="row">
		    <div class="col-xs-12 col-md-4 form-group">
  	       			<label>Reason for return<span class="required">*</span></label>
               		<div class="select">
    					 	<span class="arrow"></span>
               			<select name="returnReason" class="form-control" ng-model="${srType}.returnReason" ng-required="sr.type == '${srType}'">
               				<option value="" disabled selected>Select</option>
	             				<option value="Damaged stock">Damaged Stock</option>
	             				<option value="Incorrect stock">Incorrect Stock</option>
	             				<option value="Other">Other</option>
  							</select>
    					</div>
    					<!-- <span class="error" ng-show="serviceRequest.request_type.$touched && serviceRequest.request_type.$error.required">Please select a request type</span> -->
  		     <span class="error" ng-show="(notComplete || serviceRequest.returnReason.$touched) && (sr.type == '${srType}') && serviceRequest.returnReason.$invalid">Please select a product from the list above</span>
		
  		      	<div>
  		    </div> 
  		  </div>	
		</div>
		
		<div class="row">
		    <div class="col-sm-4 form-group">
		       <h3 class="label offset-bottom-small" style= "color: #002f5f !important;  font-size: 14px; line-height: 30px;">Has the stock been returned already? <span class="required">*</span></h3>
		      <span class="error" ng-show="(notComplete ||serviceRequest.returned.$touched) && sr.type == '${srType}' && serviceRequest.returned.$invalid">Please select an option</span>
		       <ul class="list-radio radio offset-bottom-small">
		           <li class="offset-bottom-xsmall">
		               <input id="returnedYes" type="radio" ng-model="${srType}.returned" name="returned" ng-value="true"ng-required="sr.type == '${srType}'">
		               <label for="returnedYes">
		                   Yes
		               </label>
		           </li>
		           <li>
		               <input id="returnedNo" type="radio" ng-model="${srType}.returned" name="returned" ng-value="false"ng-required="sr.type == '${srType}'">
		               <label for="returnedNo">
		               No
		               </label>
		           </li>
		       </ul>
		    </div>
		</div>

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="otherInfo">Other information</label>
				<textarea id="otherInfo" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
				<span class="error hidden"><spring:theme code="text.service.request.message.empty" /></span>
			</div>
      	</div>
	</div>
</div>