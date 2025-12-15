<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="PRICE_ENQUIRY" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have a query about the price of a product.</p>
		
		<div class="row">
			<div class="col-xs-12 col-md-4 form-group">
	       			<label>Type<span class="required">*</span></label>
             		<div class="select">
  					 	<span class="arrow"></span>
             			<select name="price_type" class="form-control validate-input" ng-model="${srType}.type" ng-required="sr.type == '${srType}'">
             				<option value="" disabled selected>Select</option>
             				<option value="base">Base Price Inquiry</option>
             				<option value="promotion">Promotion or Deal</option>
						</select>
  					</div>
  					<span class="error" ng-show="(notComplete || serviceRequest.price_type.$touched) && (sr.type == '${srType}') && serviceRequest.price_type.$invalid">Please select a type from the list above</span>
		    </div> 
		</div>

		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="price-product">Product<span class="required">*</span></label>
				<input id="price-product" name="price_product" class="form-control" type="text" ng-model="${srType}.product" ng-required="sr.type == '${srType}'">
			<span class="error" ng-show="(notComplete || serviceRequest.price_product.$touched) && (sr.type == '${srType}') && serviceRequest.price_product.$invalid">Please enter the product above</span>
			</div>

			<div class="col-sm-6 col-md-4 form-group" ng-show="${srType}.type == 'promotion'">
				<label for="price-min">Minimum buy quantity<span class="required">*</span></label>
				<input id="price-min" name="price_min" class="form-control" type="text" ng-model="${srType}.min" ng-required="sr.type == '${srType}' && ${srType}.type == 'promotion'">
			<span class="error" ng-show="(notComplete || serviceRequest.price_min.$touched) && (sr.type == '${srType}') && serviceRequest.price_min.$invalid">Please enter the minimum buy quantity above</span>
			</div>
		</div>

		<div class="row">
			<div class="col-sm-4 col-md-3 form-group">
				<label for="price-expected">Discount expected<span class="required">*</span></label>
				<input id="price-expected" name="price_expected" class="form-control half-width" type="text" ng-model="${srType}.expected" ng-required="sr.type == '${srType}'">
		<span class="error" ng-show="(notComplete || serviceRequest.price_expected.$touched) && (sr.type == '${srType}') && serviceRequest.price_expected.$invalid">Please enter the Discount expected above</span>
			</div>

			<div class="col-sm-4 col-sm-offset-2 col-md-3 col-md-offset-1 form-group">
				<label for="price-displayed">Discount displayed<span class="required">*</span></label>
				<input id="price-displayed" name="price_displayed" class="form-control half-width" type="text" ng-model="${srType}.displayed" ng-required="sr.type == '${srType}'">
			<span class="error" ng-show="(notComplete || serviceRequest.price_displayed.$touched) && (sr.type == '${srType}') && serviceRequest.price_displayed.$invalid">Please enter the Discount displayed above</span>
			</div>
		</div>
		

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="price-other">Other information</label>
				<textarea id="price-other" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>