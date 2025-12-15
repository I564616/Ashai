<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="EMPTY_PALLET_PICKUP" />

<div class="row request-content" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if you have empty pallets that you would like picked up from your premise.</p>
		
		<div class="row">
			<div class="col-xs-12 form-group">
				<label for="pallet-empty">Number of empty pallets to be collected<span class="required">*</span></label>
				<ul class="select-quantity select-quantity-fixed">
					<li class="down disabled" qty-selector-service>
						<svg class="icon-minus">
						    <use xlink:href="#icon-minus"></use>    
						</svg>
					</li>
					<li><input id="pallet-empty" name="pallet-empty" class="qty-input" type="tel" ng-init="${srType}.empty = 1" ng-value="${srType}.empty" data-val="empty" data-scope="${srType}" data-minqty="1" data-maxqty="99" maxlength="2" max="20" pattern="\d*"></li>
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
	       		<label for="pallet-otherInfo">Other information</label>
				<textarea id="pallet-otherInfo" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>