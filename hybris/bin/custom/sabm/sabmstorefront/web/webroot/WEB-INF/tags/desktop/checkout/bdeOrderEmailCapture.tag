<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

<c:url value="checkout/updateBDEOrderDetails" var="updateBDEOrderDetailsUrl" />
<input type="hidden" id="updateBDEOrderDetailsUrl" value="${updateBDEOrderDetailsUrl}">

<!-- Modal 1 -->
<div class="modal fade" id="bdeOrderModal1" tabindex="-1" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-body">
           <button type="button" class="close" data-dismiss="modal" aria-label="Close">
              <span aria-hidden="true">&times;</span>
   	       </button>
      
			<h1><spring:theme code="checkout.bde.order.finalise" /></h1>       
       		       	
       		<div class="checkbox checkbox-inline">
       	        <input type="checkbox" id="bdeOrderCheckbox" />
       			<label for="bdeOrderCheckbox"><spring:theme code="checkout.bde.order.paragraph1" /></label>	        
 			</div>
      </div> 
    </div>
  </div>
</div>

<!-- Modal 2 -->
<div class="modal fade" id="bdeOrderModal2" tabindex="-1" style="" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-body">
         <img src="/_ui/desktop/common/images/spinner.gif" alt="Loading" height="30" width="30" style="position: absolute; top: 0; left: 0; bottom: 0; right: 0; margin: auto" />
      </div>
    </div>
  </div>
</div>

<!-- Modal 3 -->
<div class="modal fade" id="bdeOrderModal3" tabindex="-1" style="" role="dialog" aria-labelledby="exampleModalLabel" aria-hidden="true">
  <div class="modal-dialog" role="document">
    <div class="modal-content">
      <div class="modal-body">
      
      		<div class="row">
      			<div class="col-md-12">
		    		<h1><spring:theme code="checkout.bde.order.sendConfirmationMail"/></h1>  	
		    		
		    		<h3><spring:theme code="checkout.bde.order.preferredEmail"/></h3>
		    		
		    		<p><spring:theme code="checkout.bde.order.paragraph2"/></p>
		    		<input type="hidden" id="bdeUserEmailIds" value="${bdeUserEmailId}" />
					
		    		<div class="checkbox" ng-repeat='userEmailId in userEmailIds'>
		    			<input type="checkbox" id="{{'userEmail'+ $index}}" value="{{userEmailId.email}}" ng-model="userEmailId.checked" ng-change="validateFormTextField()" />
		    			<label for="{{'userEmail'+$index}}">{{userEmailId.email}}</label>
		    			
		    			<!-- <span style="cursor:pointer" ng-click="deleteEmailId($index,'users')">&times;</span>  -->
		    		</div>
		    			
		    		<br />
		    		<div class="form-group row">
		    			<div class="col col-sm-5">
		    				<input type="text" ng-model='userEmail' class="form-control" />
		    			</div>
		    			<div class="col col-sm">
		   		    		<button class="btn btn-primary mb-2" ng-click="addNewEmailFor('users')" ><spring:theme code="checkout.bde.order.buttonAdd" /></button>
		    			</div>
		    			
		    			<div class="col col-sm-12">
		 					<div id="cubEmailAddressError" class="hide">Please enter a CUB email address.</div>
		    			</div>
		    		</div>
		    		
		    		
		    		<hr />
		    		
		    		<h3><spring:theme code="checkout.bde.order.addImportantOrderDetail" /><span class="required"> (Required)</span></h3>
		    		<textarea ng-model="orderDetailsTextarea" ng-keyup="validateFormTextField()"  maxlength="255" class="form-control" cols="30" rows="3" placeholder="<spring:theme code='checkout.bde.order.textareaPlaceholder' />"></textarea>
		     
		     		<hr />
		     		
		     		<h3><spring:theme code="checkout.bde.order.customerEmail" /></h3>
		     		<p><spring:theme code="checkout.bde.order.paragraph3" /></p>

		    		<input type="hidden" id="customerEmailIds" value="${customerEmailIds}" />
		    		<div class="checkbox" ng-repeat='custEmailId in custEmailIds'>
		    			<input type="checkbox" id="{{'custEmail'+$index}}" value="{{custEmailId.email}}" ng-model="custEmailId.checked" ng-change="validateFormTextField()" />
		    			<!-- <label for="{{'custEmail'+$index}}">{{custEmailId.email}}</label> -->
		    			<p ng-if="custEmailId.email==''" for="{{'custEmail'+$index}}">Please add customer email address and select at least one customer email</p>
						<label ng-if="custEmailId.email!=''" for="{{'custEmail'+$index}}">
						<!-- {{custEmailId.email}} -->						
						{{custEmailId.email.indexOf(':') > -1 ? custEmailId.email.substr(0,custEmailId.email.indexOf(':')) : custEmailId.email}}
						</label>
		    			<!-- <span style="cursor:pointer" ng-click="deleteEmailId($index,'customers')">&times;</span>  -->
		    			
		    		</div>

		    		<h3><spring:theme code="checkout.bde.order.addNewEmail"/></h3>
		    		<div class="form-group row">
		    			<div class="col col-sm-5">
		    				<input type="text" ng-model="custEmail" class="form-control" />
		    			</div>
		    			<div class="col col-sm">
		   		    		<button class="btn btn-primary mb-2" ng-click="addNewEmailFor('customers')"><spring:theme code="checkout.bde.order.buttonAdd" /></button>
		    			</div>

		    			<div class="col col-sm-12">
		 					<div id="custEmailAddressError" class="hide">Please enter valid customer email address.</div>
		    			</div>
		    		</div>
		    		
		    		<p><spring:theme code="checkout.bde.order.paragraph4" /></p>	
		    		
		    		<div class="modal-footer">
		    			<div class="row">
		    				<div class="col-md-6 text-left">
		    					<a href="cart" class="btn btn-primary"><spring:theme code="checkout.bde.order.buttonCancel" /></a>
		    				</div>
		    				<div class="col-md-6">
						    	<button class="btn btn-primary" id="btn-save-details"  ng-click="saveDetails()" ng-disabled="!saveDetailsEnabled"><spring:theme code="checkout.bde.order.buttonSaveDetails" /></button>
		    				</div>
		    			</div>
		    		</div>
				</div>
      		</div>
      </div>
    </div>
  </div>
</div>