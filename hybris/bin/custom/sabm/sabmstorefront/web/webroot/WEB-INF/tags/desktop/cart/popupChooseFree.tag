<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="chooseFreePopup" class="choose-free-Popup mfp-hide">
	<h2 class="h1 offset-bottom-small"><spring:theme code="basket.popup.bonus.title"/></h2>
	<p class="offset-bottom-medium"><spring:theme code="basket.popup.bonus.description"/></p>
	<hr>
	<form name="chooseFree">
		<div class="row" ng-repeat="prod in dealsFreeData">
			<div class="col-xs-12">
				<div class="row">
					<div class="col-xs-8 col-sm-9 trim-right-5">
						<div class="radio">
							<input id="chooseFree{{$index}}" type="radio" ng-model="selectedFree.selected" name="chooseFree" ng-value="prod.code">
							<label for="chooseFree{{$index}}">
							    <div class="item-image visible-sm-inline-block visible-md-inline-block visible-lg-inline-block">
							    	<img ng-src="{{prod.image}}" alt="Item Image">
							    </div>
							    <div class="item-info">
							    	<h4 class="clamp-2">{{prod.title}}</h4>
							    	<div class="h4 h4-subheader clamp-2">{{prod.packConfig}}</div>
							    </div>
							</label>
						</div>
					</div>
					<div class="col-xs-4 col-sm-3 trim-left">
						<div class="item-bonus">
							{{prod.qty[0]}} <spring:theme code="basket.popup.bonus.text.bonus"/>
						</div>
					</div>
				</div>
				<hr>
			</div>
		</div>
		<button ng-click="addFreeToCart(selectedFree.selected)" ng-disabled="chooseFree.$pristine" class="btn btn-primary"><spring:theme code="basket.popup.bonus.add.to.cart"/></button>
	</form>
</div>