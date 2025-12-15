<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:set var="srType" value="KEG_ISSUE" />

				
				
				
				

<div class="row request-content" ng-show="sr.type == '${srType}'">
    <!-- Hybris Values have pass to  salesforce -->
    
    
	<div hidden>
	
			<c:set var="customerEmail" value="${customer.email }" />
	<!-- Hidden attributes -->
			<input type=hidden name="type" value="Keg_Complaint">
            <input type=hidden name="sfkegUrl" id="sfkegUrl" value="${sfKegIssueUrl}">
            <input type=hidden name="orgid" value="${sforgid}">
			<input type=hidden name="status" id="status" value="Draft">
			<input type=hidden name="recordType" id="recordType"  value="${sfRecordType}">
			<input type=hidden name="WebToCase_Contact_PK_Value__c" id="WebToCase_Contact_PK_Value__c">
			<input type=hidden name="WebToCase_user_Contact_PK_Value__c" id="WebToCase_user_Contact_PK_Value__c">
			<input type=hidden name="WebToCase_Product_IPL__c" id="WebToCase_Product_IPL__c" value="32567">
			<input type=hidden name="WebToCase_Product_SKU__c" id="WebToCase_Product_SKU__c" >
			<input type=hidden name="Product_Name__c" id="Product_Name__c">
			<input type=hidden name="WebToCase_Account_ZALB__c" id="WebToCase_Account_ZALB__c">
			<input type=hidden name="WebToCase_Source__c" id="WebToCase_Source__c" value="CUBOnlineWebForm">
			<input type=hidden name="Alternate_Keg_Tag_Mailing_Addrs_Required__c" id="Alternate_Keg_Tag_Mailing_Addrs_Required__c"  value="No">
			<input type=hidden name="external" id="external" value="1" /><br>
			<input type=hidden name="retURL" value="http://">
			<input type=hidden name="company" id="company" class="form-control"/>
		    <input type=hidden name="Production_Code_Plant__c"  id="plant_time" value="{{KEG_ISSUE.plantcode}} {{KEG_ISSUE.timecode}}"> 
			<input type=hidden name="Confirm_Notification_Receiver__c" id="notifytype"  value="Customer"> 
		    <input type=hidden name="Notification_Receiver_eMail_Address__c" class="form-control" id="email" type="email" value="${customerEmail}">
		    <input type=hidden name="email" id="email" type="email"class="form-control"  value="${customerEmail}">
			<input type=hidden  value="submit" name="submit">
	</div>


	<div class="col-xs-12">
		<p>This enquiry should be used if you have found a problem with a keg that CUB has delivered.<br/>
		<strong>Note: you MUST attach the yellow tag to the keg and store upright for it to be collected.</strong></p>
		<p>If your keg quality request is related to Balter kegs, please call 1300 127 244.</p>
		
		<div class="row">
			<div class="col-md-4 form-group">
				<label for="keg_products_label">Keg Products<span
					class="required">*</span></label>
				<div class="select">
					<span class="arrow"></span>
						 <select id="Product_Name__code" class="form-control" name="Product_Name__code" ng-model="${srType}.Product_Name__code" ng-required="sr.type == '${srType}'">
						<option value="" disabled selected>Select</option>
						<c:forEach items="${filterKegMaterials}" var="filterKegMaterials">
							<option value="${filterKegMaterials.sellingName}--${filterKegMaterials.code}">${filterKegMaterials.sellingName}</option>
						</c:forEach>
					</select>
				</div>
				<span class="error" ng-show="(notComplete || serviceRequest.Product_Name__code.$touched) && (sr.type == '${srType}') && serviceRequest.Product_Name__code.$invalid">Please select a product from the list above</span>
			</div>

			<div class="col-sm-6 col-md-3 form-group">
				<label for="keg-number">6 or 7 digit keg number<span class="required">*</span></label>
				<div class="input-icon-group">
					<input id="keg-number" ng-pattern="/^\d+$/" name="Keg_Number__c" class="form-control" ng-minlength="6" type="text" ng-maxlength="7" ng-model="${srType}.number" ng-required="sr.type == '${srType}'">
					<span class="input-group-addon info" data-toggle="tooltip" data-placement="top" title='This number can be found on the top or side of the keg. Please make sure the yellow tag you attach matches this number.'><svg class="icon-info"><use xlink:href="#icon-info"></use></svg></span>
				</div>

				<span class="error" ng-show="serviceRequest.Keg_Number__c.$error.pattern">The keg number consists of numbers only</span>
				<span class="error" ng-show="serviceRequest.Keg_Number__c.$error.maxlength">The keg number is 6 or 7 digits</span>
				<span class="error" ng-show="(notComplete || serviceRequest.Keg_Number__c.$touched) && (sr.type == '${srType}') && serviceRequest.Keg_Number__c.$invalid">Please enter the keg number above</span>
			</div>
		</div>
		<div class="row">
			<div class="col-sm-6 col-md-4 form-group">
			 <label for="keg-bestbefore">Best Before Date Available<span class="required">*</span></label>
			 <div class="select">
					<input class="keg_bestbefore_avl" name="Best_Before_Date_Available__c" type="radio" value="Yes" ng-model="sr.bestbefore" ng-required="sr.type == '${srType}'"/>Yes &nbsp;&nbsp;
					<input class="keg_bestbefore_avl" name="Best_Before_Date_Available__c" type="radio" value="No" ng-model="sr.bestbefore" ng-required="sr.type == '${srType}'"/>No
				<span class="error" ng-show="(notComplete || serviceRequest.Best_Before_Date_Available__c.$touched) && (sr.type == '${srType}')  && (sr.type == '${srType}') && serviceRequest.Best_Before_Date_Available__c.$invalid">Please select an option above</span>
          
				</div>
				</div>
			  <div class="col-sm-6 col-md-3 form-group" ng-show="sr.bestbefore === 'Yes'">
			    <label for="keg_bestbefore_date">Best before date<span class="required">*</span></label>
				<div class="select">
					<span class="arrow"></span>
					<input id="keg_bestbefore_date" name="Best_Before_Date__c" placeholder="Select" data-container=".form-group" class="form-control basic-datepicker" type="text" style="cursor: pointer;" ng-model="${srType}.bestbefore" ng-required="sr.bestbefore === 'Yes'">
				 <span class="error" ng-show="(notComplete || serviceRequest.Best_Before_Date__c.$touched) && (sr.type == '${srType}') && serviceRequest.Best_Before_Date__c.$invalid">Please select an option above</span>
				</div>
			    
			    </div>
       </div>
		 <div class="row">
		 <div class="col-sm-6 col-md-4 form-group">
		   <label for="time-code">Time Code<span class="required">*</span></label>&nbsp;
			   	<div class="input-icon-group">
			   	<input id="time-code" class="hide form-control" type="text" name="time-code"   ng-model='${srType}.time_code' ng-required="sr.type == '${srType}'"> 
                <input id="time_code" class="form-control" type="text" name="time_code" ng-pattern="/^((([0]?[1-9]|1[0-2])(:|\.)[0-5][0-9]((:|\.)[0-5][0-9])?( )?(AM|am|aM|Am|PM|pm|pM|Pm))|(([0]?[0-9]|1[0-9]|2[0-3])(:|\.)[0-5][0-9]((:|\.)[0-5][0-9])?))$/"  ng-model='${srType}.time_code' ng-required="sr.type == '${srType}'"> 
               
                <span class="input-group-addon info" style="height: 34px;" data-toggle="tooltip" data-placement="top" title='The Time Code can be found just below the Best Before Date and will look similar to 10:30.'><svg class="icon-info"><use xlink:href="#icon-info"></use></svg></span>
            	</div>
            <span class="error" ng-show="serviceRequest.time_code.$error.pattern">Please enter correct time</span>
            <span class="error" ng-show="(notComplete || serviceRequest.time_code.$touched) && (sr.type == '${srType}') && serviceRequest.time_code.$invalid">Please enter the time code above</span>
              </div>

            <div class="col-md-3 form-group">
                <label for="plant-code">Plant Code<span class="required">*</span></label>&nbsp;
                <div class="select"><span class="arrow"  style="margin-right: 15%;"></span>
                <div class="input-icon-group">

                <span class="input-group-addon info" data-toggle="tooltip" data-placement="top" title='The Plant Code is the number to the right of the Time Code.'><svg class="icon-info"><use xlink:href="#icon-info"></use></svg></span></div>
                      <select name="Production_Code_Plant__c" id="plant-code" class="form-control" ng-model="${srType}.plantcode" ng-required="sr.type == '${srType}'">
                         <option value=""  selected>Select</option>
                         <option value = "0">0</option>
                         <option value = "1">1</option>
                         <option value = "2">2</option>
                         <option value = "3">3</option>
                         <option value = "4">4</option>
                         <option value = "5">5</option>
                         <option value = "6">6</option>
                         <option value = "7">7</option>
                         <option value = "8">8</option>
                         <option value = "9">9</option>
                </select></div>
                <span class="error" ng-show="(notComplete || serviceRequest.Production_Code_Plant__c.$touched) && (sr.type == '${srType}') && serviceRequest.Production_Code_Plant__c.$invalid">Please select an option above</span>
              </div>

                </div>

                <div class="row">
                  <div class="col-sm-6 col-md-4 form-group">
                    <label for="reason-code">Reason Code <span class="required">*</span></label>
                    <div class="select">
                      <span class="arrow"></span>
                      <select id="reason-code" name="Reason_Code" class="form-control" ng-model="sr.reasonCode" ng-required="sr.type == 'KEG_ISSUE'">
                        <option value="" disabled selected>Select</option>
                        <option value="Taste Complaint">Taste Complaint</option>
                        <option value="Smell Complaint">Smell Complaint</option>
                        <!--<option value="Mouldy Keg">Mouldy Keg</option> -->
                        <option value="Other">Other</option>
                      </select>
                    </div>

                    <span class="error"
                          ng-show="(notComplete || serviceRequest.Reason_Code.$touched)  && sr.type == 'KEG_ISSUE' && serviceRequest.Reason_Code.$invalid">
                      Please select a reason code
                    </span>
                  </div>
                </div>

      	<div class="row">
      		<div class="col-md-8 form-group">
	       		<label for="keg-other">Perceived problem with keg <span class="required">*</span></label>
				<textarea id="keg-other" class="form-control" name="Reason__c" rows="15" ng-model="${srType}.otherinfo" ng-required="sr.type == '${srType}'"></textarea>
			<span class="error" ng-show="(notComplete || serviceRequest.Reason__c.$touched) && (sr.type == '${srType}') && serviceRequest.Reason__c.$invalid">Please enter details above</span>
			</div>
      	</div>
	</div>
</div>