<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<c:set var="srType" value="DELIVERY_ISSUE" />

<div class="row request-content delivery-issue" ng-show="sr.type == '${srType}'">
	<div class="col-xs-12">
		<p>This enquiry should be used if there has been an issue with the delivery of your stock (either driver or product related).</p>
		
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="delivery_issue-invnumber">Invoice number<span class="required">*</span></label>
				<input id="delivery_issue-invnumber" name="delivery_issue_invnumber" class="form-control" ng-pattern="/^\d+$/" type="text" ng-model="${srType}.invnumber" ng-required="sr.type == '${srType}'">
				<span class="error" ng-show="serviceRequest.delivery_issue_invnumber.$error.pattern">Your invoice number consists of numbers only</span>
				<span class="error" ng-show="(notComplete || serviceRequest.delivery_issue_invnumber.$touched) && sr.type == '${srType}' && serviceRequest.delivery_issue_invnumber.$invalid">Please enter the invoice number above</span>
			</div>

			<div class="col-sm-6 col-md-3 form-group">
				<label for="delivery_issue-invdate">Invoice date<span class="required">*</span></label>
				<div class="select">
					<span class="arrow"></span>
					<input id="delivery_issue-invdate" name="delivery_issue_invdate" placeholder="Select" data-container=".form-group" class="form-control basic-datepicker" type="text" readonly="readonly" ng-model="${srType}.invdate" ng-required="sr.type == '${srType}'">
					<span class="error" ng-show="(notComplete ||serviceRequest.delivery_issue_invdate.$touched) && sr.type == '${srType}' && serviceRequest.delivery_issue_invdate.$invalid">Please select an option above</span>
				</div>
			</div>
		</div>


		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
				<label for="delivery_issue-product">Product<span ng-show="(${srType}.damage_stock || ${srType}.not_collected)" class="required">*</span></label>
				<input id="delivery_issue-product" name="delivery_issue_product" class="form-control" type="text" ng-model="${srType}.product" ng-required="(${srType}.damage_stock || ${srType}.not_collected)">
			<span class="error" ng-show="(notComplete || serviceRequest.delivery_issue_product.$touched) && sr.type == '${srType}' &&(${srType}.damage_stock || ${srType}.not_collected) && serviceRequest.delivery_issue_product.$invalid">Please enter the product above</span>
			
			</div>

			<div class="col-sm-6 col-md-3 form-group">
				<label>Quantity affected</label><span class="required">*</span>
				<div class="row">
					<div class="col-xs-6 trim-right-5">
						<ul class="select-quantity">
							<li class="down disabled" qty-selector-service>
								<svg class="icon-minus" >
								    <use xlink:href="#icon-minus"></use>    
								</svg>
							</li>
							<li><input class="qty-input" type="number" name="delivery_issue_qyt" ng-init="${srType}.qty = 0" ng-model="${srType}.qty" ng-value="${srType}.qty" data-val="qty" data-scope="${srType}" data-minqty="0" ng-minlength="1" maxlength="3" pattern="\d*" ng-blur="${srType}.qty==''?${srType}.qty='0':${srType}.qty=0"></li>
							<li class="up" qty-selector-service>
								<svg class="icon-plus">
								    <use xlink:href="#icon-plus"></use>    
								</svg>
							</li>
						</ul>
						<%-- <span class="error" ng-if="${srType}.qty == undefined" style=color:red ng-value=0>Invalid Input</span> --%>
					</div>
					<div class="col-xs-6 trim-left-5">
	             		<div class="select">
	  					 	<span class="arrow"></span>
	  					 	<select name="delivery_issue_uom" id="delivery_issue-uom" class="form-control" ng-model="${srType}.uom" ng-required="${srType}.qty > 0">
	             			<%-- <select name="uom" class ="form-control" ng-model="${srType}.uom" ng-required="sr.type == '${srType}'"> --%>
	               			    <option value=""  selected>Select</option>
	             				<option value="Case">Case</option>
	             				<option value="Keg">Keg</option>
	             				<option value="Bottle">Bottle</option>
	             				<option value="Can">Can</option>
							</select>
	  					<span class="error" ng-show="(notComplete ||serviceRequest.delivery_issue_uom.$touched) && sr.type == '${srType}' && serviceRequest.delivery_issue_uom.$invalid">Please select an option above</span>
	  					</div>
					</div>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="col-md-8 form-group">
				<label for="delivery_issue-issue">Warehouse enquiry/issue<span class="required">*</span></label>
				<span class="error" ng-show="(notComplete ||serviceRequest.check.$touched) && serviceRequest.check.$invalid">Please select an option</span>
				<ul id="delivery_issue-issue" class="list-checkbox checkbox">
				    <li>
				        <input id="delivery_issue-damage_stock" type="checkbox" ng-model="${srType}.damage_stock" name="check" value="Damage to stock" ng-click="updateDelIssueChecks(1)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}' ">
				        <label for="delivery_issue-damage_stock">
				            Damage to stock
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-not_collected" type="checkbox" ng-model="${srType}.not_collected" name="check" value="Empty pallets/kegs not collected" ng-click="updateDelIssueChecks(2)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-not_collected">
				        	Empty pallets/kegs not collected
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-damage_premise" type="checkbox" ng-model="${srType}.damage_premise" name="check" value="Damage to premise" ng-click="updateDelIssueChecks(3)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-damage_premise">
				        	Damage to premise
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-picking_error" type="checkbox" ng-model="${srType}.picking_error" name="check" value="Picking error" ng-click="updateDelIssueChecks(4)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-picking_error">
				        	Picking error
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-driver_complaint" type="checkbox" ng-model="${srType}.driver_complaint" name="check" value="Driver complaint" ng-click="updateDelIssueChecks(5)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-driver_complaint">
				        	Driver complaint
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-not_complete" type="checkbox" ng-model="${srType}.not_complete" name="check" value="Not all items delivered" ng-click="updateDelIssueChecks(6)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-not_complete">
				        	Not all items delivered
				        </label>
				    </li>
				    <li>
				        <input id="delivery_issue-other" type="checkbox" ng-model="${srType}.other" name="check" value="Other" ng-click="updateDelIssueChecks(7)" ng-required="delIssueChecks.length == 0 && sr.type == '${srType}'">
				        <label for="delivery_issue-other">
				        	Other
				        </label>
				    </li>
				</ul>
			</div>
		</div>
		

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="delivery_issue-other">Other information</label>
				<textarea id="delivery_issue-other" class="form-control" name="text" rows="15" ng-model="${srType}.otherinfo"></textarea>
			</div>
      	</div>
	</div>
</div>