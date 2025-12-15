<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<form name="form2">

<div class="item-data-table container-fluid">
	<div class="row table-header">
		<div class="col-md-4 col-sm-12 col-xs-12">
			<label  ng-style="{'font-size': (widthSize < desktopSize) ? '14px' : '16px' }" class="custom-checkbox" ng-show="typeOfIssue == 'Price Discrepancy'">			
					<span><spring:theme code="text.invoicediscrepancy.tableheader.selectallitems" /></span>
					<input type="checkbox" ng-model="selectAll" ng-change="checkAll(); validateForm();" />
				<span class="check-mask"></span>
			</label>
			<span ng-show="typeOfIssue == 'Freight Discrepancy'"  ng-style="{'font-size': (widthSize < desktopSize) ? '14px' : '16px' }"><spring:theme code="text.invoicediscrepancy.tableheader.items" /></span>
		</div>
		
		<div class="tableHeaderColumn-desktop" ng-show="widthSize >= desktopSize">
			<div ng-show="tableColumn == 1">
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.qty" /></div>
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.uom" /></div>
				<div class="col-md-2"><spring:theme code="text.invoicediscrepancy.tableheader.unitprice" /></div>
				<div class="col-md-2"><spring:theme code="text.invoicediscrepancy.tableheader.discount" /></div>
				<div class="col-md-2"><spring:theme code="text.invoicediscrepancy.tableheader.amount" /></div>
			</div>
			<div ng-show="tableColumn == 2">
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.containerdeposit" /></div>
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.wet" /></div>
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.gst" /> <br /><spring:theme code="text.invoicediscrepancy.tableheader.yn" /></div>
				<div class="col-md-2 text-center"><spring:theme code="text.invoicediscrepancy.tableheader.totalexgst" /></div>
				<div class="col-md-1"><spring:theme code="text.invoicediscrepancy.tableheader.localfreight" /></div>
				<div class="col-md-2"><spring:theme code="text.invoicediscrepancy.tableheader.lucexgst" /></div>
			</div>
		</div>
		
		<div class="tableHeaderColumn-tablet" ng-show="widthSize >= mobileSize && widthSize < desktopSize">
			<div  class="row container-fluid" ng-show="tableColumn == 1">
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.qty" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.uom" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.unitprice" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.discount" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.amount" /></div>
			</div>
			<div  class="row container-fluid" ng-show="tableColumn == 2">
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.containerdeposit" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.wet" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.gst" /> <br /><spring:theme code="text.invoicediscrepancy.tableheader.yn" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.totalexgst" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.localfreight" /></div>
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.lucexgst" /></div>
			</div>
			<div  class="row container-fluid" ng-show="tableColumn == 3">
			</div>
		</div>

		<div class="tableHeaderColumn-mobile" ng-show="widthSize < mobileSize">
			<div  class="row container-fluid" ng-show="tableColumn == 1">
				<div class="col-xs-2"><spring:theme code="text.invoicediscrepancy.tableheader.qty" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.uom" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.unitprice" /></div>
				<div class="col-xs-3 trim-both"><spring:theme code="text.invoicediscrepancy.tableheader.discount" /></div>
			</div>
			<div  class="row container-fluid" ng-show="tableColumn == 2">
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.amount" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.containerdeposit" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.wet" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.gst" /> <br /><spring:theme code="text.invoicediscrepancy.tableheader.yn" /></div>
			</div>
			<div  class="row container-fluid" ng-show="tableColumn == 3">
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.totalexgst" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.localfreight" /></div>
				<div class="col-xs-3"><spring:theme code="text.invoicediscrepancy.tableheader.lucexgst" /></div>
			</div>
			<div  class="row container-fluid" ng-show="tableColumn == 4">
			</div>
		</div>
		<ul class="dots" ng-show="widthSize < desktopSize">
			<li ng-repeat="i in getColumnCount(tableColumnMax) track by $index" ng-click="updateTableColumn($index+1, $event);" ng-class="{'active': $index+1 == tableColumn}"></li>
		</ul>
	</div>

	<div class="table-body-container">
	<div class="table-arrow right" ng-show="tableColumnBtnActive == 'right'" ng-click="showTableColumnRight()"></div>
	<div class="table-arrow left" ng-show="tableColumnBtnActive == 'left'" ng-click="showTableColumnLeft()"></div>
	<div class="table-arrow both" ng-show="tableColumnBtnActive == 'both'">
		<span class="arrow left" ng-click="showTableColumnLeft()"></span>
		<span class="arrow right" ng-click="showTableColumnRight()"></span>
	</div>
	
	
	<div class="row table-body" ng-repeat="item in invoiceItemData.invoices">
		<div class="col-md-4 col-sm-12 col-xs-12 itemDesc">
			<label for="itemCheck{{item.itemID}}" class="custom-checkbox" ng-show="typeOfIssue == 'Price Discrepancy' && item.discount != 'Bonus Stock'"> <span> {{item.itemDescriptionLine1}} <br /> {{item.itemDescriptionLine2}}</span>
				<input type="checkbox"  class="itemCheckbox" name="itemCheck" id="itemCheck{{item.itemID}}" ng-change="validateForm()" ng-checked="selectAll" ng-model="selected[item.itemID]" />
				<span class="check-mask"></span>
			</label>
			<span ng-show="typeOfIssue == 'Freight Discrepancy'"> {{item.itemDescriptionLine1}} <br /> {{item.itemDescriptionLine2}}</span>
			<span ng-show="typeOfIssue == 'Price Discrepancy' && item.discount == 'Bonus Stock'"> {{item.itemDescriptionLine1}} <br /> {{item.itemDescriptionLine2}}</span>
		</div>
		
		<div class="tableBodyColumn-desktop" ng-show="widthSize >= desktopSize">
			<div ng-show="tableColumn == 1">
				<div class="col-md-1">{{item.quantity != null ? (item.quantity | number: 0) : '0'}}</div>
				<div class="col-md-1">{{item.uoM}}</div>
				<div class="col-md-2">{{'$'+(item.unitPrice != null ? (item.unitPrice | number: 2) : '0.00')}}</div>
				<!-- <div class="col-md-2 discount">{{'$'+(item.discount != null ? (item.discount | number: 2) : '0.00')}}</div> -->
				<div class="col-md-2 discount">{{(item.discount != null ? (item.discount !='Bonus Stock' ? '$'+(item.discount | number: 2) : item.discount) : '0.00')}}</div>
				<div class="col-md-2">{{'$'+(item.amount != null ? (item.amount | number: 2) : '0.00')}}</div>
			</div>							
			<div ng-show="tableColumn == 2">
				<div class="col-md-1">{{'$'+(item.containerDeposit != null ? (item.containerDeposit | number: 2) : '0.00')}}</div>
				<div class="col-md-1">{{'$'+(item.wet != null ? (item.wet | number: 2) : '0.00')}}</div>
				<div class="col-md-1">{{item.gst}}</div>
				<div class="col-md-2 text-center">{{'$'+(item.totalExGST != null ? (item.totalExGST | number: 2) : '0.00')}}</div>
				<div class="col-md-1">{{'$'+(item.localFreight != null ? (item.localFreight | number: 2) : '0.00')}}</div>
				<div class="col-md-2">{{'$'+(item.lucExGST != null ? (item.lucExGST | number: 2) : '0.00')}}</div>
			</div>	
		</div>			
			
		<div class="tableBodyColumn-tablet"  ng-show="widthSize >= mobileSize && widthSize < desktopSize">
			<div  class="row container-fluid" ng-show="tableColumn == 1">
				<div class="col-xs-2">{{item.quantity != null ? (item.quantity | number: 0) : '0'}}</div>
				<div class="col-xs-2">{{item.uoM}}</div>
				<div class="col-xs-3">{{'$'+(item.unitPrice != null ? (item.unitPrice | number: 2) : '0.00')}}</div>
				<div class="col-xs-3 discount">{{'$'+(item.discount != null ? (item.discount | number: 2) : '0.00')}}</div>
				<div class="col-xs-2">{{'$'+(item.amount != null ? (item.amount | number: 2) : '0.00')}}</div>
			</div>							
			<div  class="row container-fluid" ng-show="tableColumn == 2">
				<div class="col-xs-2">{{'$'+(item.containerDeposit != null ? (item.containerDeposit | number: 2) : '0.00')}}</div>
				<div class="col-xs-2">{{'$'+(item.wet != null ? (item.wet | number: 2) : '0.00')}}</div>
				<div class="col-xs-2">{{item.gst}}</div>
				<div class="col-xs-2">{{'$'+(item.totalExGST != null ? (item.totalExGST | number: 2) : '0.00')}}</div>
				<div class="col-xs-2">{{'$'+(item.localFreight != null ? (item.localFreight | number: 2) : '0.00')}}</div>
				<div class="col-xs-2">{{'$'+(item.lucExGST != null ? (item.lucExGST | number: 2) : '0.00')}}</div>
			</div>	
		</div>	

		<div class="tableBodyColumn-mobile"  ng-show="widthSize < mobileSize">
			<div  class="row container-fluid" ng-show="tableColumn == 1">
				<div class="col-xs-2">{{item.quantity != null ? (item.quantity | number: 0) : '0'}}</div>
				<div class="col-xs-3">{{item.uoM}}</div>
				<div class="col-xs-3">{{'$'+(item.unitPrice != null ? (item.unitPrice | number: 2) : '0.00')}}</div>
				<div class="col-xs-3 discount">{{'$'+(item.discount != null ? (item.discount | number: 2) : '0.00')}}</div>
			</div>							
			<div  class="row container-fluid" ng-show="tableColumn == 2">
				<div class="col-xs-3">{{'$'+(item.amount != null ? (item.amount | number: 2) : '0')}}</div>
				<div class="col-xs-3">{{'$'+(item.containerDeposit != null ? (item.containerDeposit | number: 2) : '0.00')}}</div>
				<div class="col-xs-3">{{'$'+(item.wet != null ? (item.wet | number: 2) : '0.00')}}</div>
				<div class="col-xs-3">{{item.gst}}</div>
			</div>	
			<div  class="row container-fluid" ng-show="tableColumn == 3">
				<div class="col-xs-3">{{'$'+(item.totalExGST != null ? (item.totalExGST | number: 2) : '0.00')}}</div>
				<div class="col-xs-3">{{'$'+(item.localFreight != null ? (item.localFreight | number: 2) : '0.00')}}</div>
				<div class="col-xs-3">{{'$'+(item.lucExGST != null ? (item.lucExGST | number: 2) : '0.00')}}</div>
			</div>	
		</div>	
		
		<div class="row">
			<div class="col-md-12">
				<div class="row container-fluid discount-section" ng-show="selected[item.itemID] && typeOfIssue == 'Price Discrepancy' && item.discount != 'Bonus Stock'">
					<div class="col-sm-6">
						<div class="form-group row discount-received">
							<label for="" class="col-sm-4 col-sm-offset-4 trim-both col-xs-6 col-form-label"><spring:theme code="text.invoicediscrepancy.discountreceivedperitem" /></label>
							<div class="col-md-3 col-sm-4 col-xs-5" ng-class="{'trim-left': widthSize >= mobileSize}">
								<input type="text" class="form-control" value="{{'$'+(item.discount | number: 2)}}" readonly />
							</div>
						</div>
					</div>
					<div class="col-sm-6">
						<div class="form-group row discount-expected">
							<label for="" class="col-sm-4 trim-both col-xs-6 col-form-label"><spring:theme code="text.invoicediscrepancy.discountexpectedperitem" /></label>
							<div class="col-md-3 col-sm-4 col-xs-5" ng-class="{'trim-left': widthSize >= mobileSize}">
								<input type="text" class="form-control" placeholder="$" ng-focus="appendDollarSign($event)" onkeypress="return isNumber(event)" ng-click="appendDollarSign($event)" ng-pattern="/^([$][0-9]{1,9})+(\.[0-9]{1,2})?$/" ng-keyup="validateForm();appendDollarSign($event);" name="discountExpected[item.itemID]" ng-model="discountExpected[item.itemID]"  autocomplete="off" required />
							</div>
						</div>
					</div>
				</div>			
			</div>
		</div>
	</div>
	<div class="row table-footer" ng-show="typeOfIssue == 'Freight Discrepancy'">
		<div class="col-md-4" ng-style="{'font-size': (widthSize < desktopSize) ? '14px' : '16px' }">
			<spring:theme code="text.invoicediscrepancy.tableheader.totals" />
		</div>
	
		<div class="tableFooterColumn-desktop"  ng-show="widthSize >= desktopSize">	
			<div ng-show="tableColumn == 1">
				<div class="col-md-1">{{totalQty}}</div>
				<div class="col-md-1"></div>
				<div class="col-md-2"></div>
				<div class="col-md-2 discount">{{'$'+(totalDiscount)}}</div>
				<div class="col-md-2">{{'$'+(totalAmount)}}</div>
			</div>
			<div ng-show="tableColumn == 2">
				<div class="col-md-1">{{'$'+(totalContainerDeposit)}}</div>
				<div class="col-md-1">{{'$'+(totalWet)}}</div>
				<div class="col-md-1"></div>
				<div class="col-md-2 text-center">{{'$'+(totalExGST)}}</div>
				<div class="col-md-1">{{'$'+(totalLocalFreight)}}</div>
				<div class="col-md-2"></div>
			</div>
		</div>
		
		<div class="tableFooterColumn-tablet"  ng-show="widthSize >= mobileSize && widthSize < desktopSize">	
			<div ng-show="tableColumn == 1">
				<div class="col-xs-2">{{totalQty}}</div>
				<div class="col-xs-2"></div>
				<div class="col-xs-3"></div>
				<div class="col-xs-3 discount">{{'$'+(totalDiscount)}}</div>
				<div class="col-xs-2">{{'$'+(totalAmount)}}</div>
			</div>
			<div ng-show="tableColumn == 2">
				<div class="col-xs-2">{{'$'+(totalContainerDeposit)}}</div>
				<div class="col-xs-2">{{'$'+(totalWet)}}</div>
				<div class="col-xs-2"></div>
				<div class="col-xs-2">{{'$'+(totalExGST)}}</div>
				<div class="col-xs-2">{{'$'+(totalLocalFreight)}}</div>
				<div class="col-xs-2"></div>
			</div>
		</div>

		<div class="tableFooterColumn-mobile"  ng-show="widthSize < mobileSize">	
			<div ng-show="tableColumn == 1">
				<div class="col-xs-2">{{totalQty}}</div>
				<div class="col-xs-3"></div>
				<div class="col-xs-3"></div>
				<div class="col-xs-3 discount">{{'$'+(totalDiscount)}}</div>
			</div>
			<div ng-show="tableColumn == 2">
				<div class="col-xs-3 trim-left-4">{{'$'+(totalAmount)}}</div>
				<div class="col-xs-3 trim-left-4 text-center">{{'$'+(totalContainerDeposit)}}</div>
				<div class="col-xs-3">{{'$'+(totalWet)}}</div>
				<div class="col-xs-3"></div>
			</div>
			<div ng-show="tableColumn == 3">
				<div class="col-xs-3">{{'$'+(totalExGST)}}</div>
				<div class="col-xs-3">{{'$'+(totalLocalFreight)}}</div>
				<div class="col-xs-3"></div>
			</div>
		</div>

	</div>
	</div>	
</div>

<div class="row" ng-show="typeOfIssue == 'Freight Discrepancy'">
	<div class="col-md-4 col-md-offset-2 col-sm-5 col-sm-offset-1">
		<div class="form-group row">
			<label for="" class="col-sm-7 col-xs-7 col-form-label"><spring:theme code="text.invoicediscrepancy.freightcharged" /></label>
			<div class="col-sm-5  col-xs-5">
				<input type="text" class="form-control discount" value="{{'$'+(totalLocalFreight | number: 2)}}" readonly />
			</div>
		</div>
	</div>
	<div class="col-md-4 col-md-offset-1 col-sm-5">
		<div class="form-group row">
			<label for="" class="col-sm-7 col-xs-7 col-form-label"><spring:theme code="text.invoicediscrepancy.freightexpected" /></label>
			<div class="col-sm-5 col-xs-5 freight-expected">
				<input type="text" class="form-control" ng-model="freightExpected" onkeypress="return isNumber(event)" ng-focus="appendDollarSign($event)" ng-click="appendDollarSign($event)" ng-keyup="validateForm(); appendDollarSign($event)" placeholder="$" ng-pattern="/^([$][0-9]{1,9})+(\.[0-9]{1,2})?$/"  required />
			</div>
		</div>
	</div>
</div>

<div class="description" ng-show="selectedInput.length > 0 || typeOfIssue == 'Freight Discrepancy'">
	<p><spring:theme code="text.invoicediscrepancy.textarea.description" /><span> - <spring:theme code="text.invoicediscrepancy.textarea.optional" /></span></p>
	<textarea cols="30" rows="5" style="resize: none;" class="form-control" ng-model="message"></textarea>
</div>
</form>