<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="KEG_PICKUP" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<!-- <p>This enquiry should be used if you would like kegs picked up from your premise.</p>
		<p>If your keg pickup request is related to Balter Kegstar kegs, please call 1800 KEGSTAR (1800 534 7827) to organise collection.</p> -->
		
		<!-- <p>We will collect 1 empty keg for every full keg delivered. Please use this request if you have additional empty kegs to be collected from your premise.</p>
    	
    	<p> Requests to collect additional kegs will be actioned within 10 business days. Please do not submit additional requests within this time.</p> -->
		
		
		<p>For CUB arranged deliveries we will pick up 1 empty keg for every full keg delivered. You do not need to raise a request for these routine returns.</p>

		<p>Use this request form only if you have ADDITIONAL empty kegs to collect from your premises. Requests to collect additional kegs will be actioned within 10 business days. Please do not submit further requests within this time frame.</p> 

		<!--<p><b>IMPORTANT NOTE: For customer arranged deliveries (will call) please contact your preferred carrier to coordinate the collection and return of <u>all empty kegs</u> to CUB. CUB are unable to process keg return requests for customers using will call carriers.</b></p>-->
		
		
		
		<div class="row">
			<div class="col-xs-12 form-group">
				<label for="pickup-empty">Number of empty kegs to be collected<span class="required">*</span></label>
				<ul class="select-quantity select-quantity-fixed">
					<li class="down disabled" qty-selector-service>
						<svg class="icon-minus">
						    <use xlink:href="#icon-minus"></use>    
						</svg>
					</li>
					<li><input id="pickup-empty" name="pickup-empty" class="qty-input" type="tel" ng-init="${srType}.empty = 1" ng-value="${srType}.empty" data-val="empty" data-scope="${srType}" data-minqty="1" data-maxqty="99" maxlength="2" max="20" pattern="\d*" ></li>
					<li class="up" qty-selector-service>
						<svg class="icon-plus">
						    <use xlink:href="#icon-plus"></use>    
						</svg>
					</li>
					<li class="input-set" style="visibility: hidden" qty-selector-service></li>
				</ul>
			</div>
		</div>

		<div class="row">
			<div class="col-xs-12 form-group">
				<label for="pickup-part">Number of part full kegs to be collected (this could be out of date kegs)</label>
				<ul class="select-quantity select-quantity-fixed">
					<li class="down disabled" qty-selector-service>
						<svg class="icon-minus">
						    <use xlink:href="#icon-minus"></use>    
						</svg>
					</li>
					<li><input id="pickup-part" name="pickup-part" class="qty-input" type="tel" ng-init="${srType}.part = 0" ng-value="${srType}.part" data-val="part" data-scope="${srType}" data-minqty="0" data-maxqty="99" maxlength="2" max="20" pattern="\d*"></li>
					<li class="up" qty-selector-service>
						<svg class="icon-plus">
						    <use xlink:href="#icon-plus"></use>    
						</svg>
					</li>
					<li class="input-set" style="visibility: hidden" qty-selector-service></li>
				</ul>
			</div>
		</div>

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="pickup-otherInfo">Other information</label>
				<textarea id="pickup-otherInfo" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>