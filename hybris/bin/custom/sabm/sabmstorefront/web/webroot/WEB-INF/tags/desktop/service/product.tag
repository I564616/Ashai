<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="PRODUCT_ENQUIRY" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have a query about a product.</p>
		
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="product-details">Product details<span class="required">*</span></label>
				<input id="product-details" class="form-control" name="product_details" type="text" ng-model="${srType}.details" ng-required="sr.type == '${srType}'">
			<span class="error" ng-show="(notComplete || serviceRequest.product_details.$touched) && (sr.type == '${srType}') && serviceRequest.product_details.$invalid">Please enter the product details above</span>
			</div>
		</div>
		
		<div class="row">
		    <div class="col-sm-4 form-group">
		        <h3 class="label offset-bottom-small" style= "color: #002f5f !important;  font-size: 14px; line-height: 30px;">Is this product Promotional Stock?<span class="required">*</span></h3>
		       <span class="error" ng-show="(notComplete ||serviceRequest.product_promo.$touched) && sr.type == '${srType}' && serviceRequest.product_promo.$invalid">Please select an option</span>
		       <ul class="list-radio radio offset-bottom-small">
		           <li class="offset-bottom-xsmall">
		               <input id="product-promoYes" type="radio" ng-model="${srType}.promo" name="product_promo" ng-value="true" ng-required="sr.type == '${srType}'">
		               <label for="product-promoYes">
		                   Yes
		               </label>
		           </li>
		           <li>
		               <input id="product-promoNo" type="radio" ng-model="${srType}.promo" name="product_promo" ng-value="false" ng-required="sr.type == '${srType}'">
		               <label for="product-promoNo">
		               No
		               </label>
		           </li>
		       </ul>
		    </div>
		</div>

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="product-otherInfo">Product Enquiry information</label>
				<textarea id="product-otherInfo" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>