<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
	<div style="display:none" id="removebeforeSubmit">
<input type="hidden" value="${companyData.companyBillingAddress}" id="companyBillingAddress">

		
		<c:set value="false" var="defaultAddress" />	
		<input type="hidden" value="${defaultAddress}" id="defaultAddressCheck"/>
				
	<c:set value="0" var="count"/>
                <div class="dynamic-delivery-address" id="change-address">
                 <div class="row form-row-margin">
					 <div class="checkout_subheading row-margin-fix">
                   		<div class="add-additional-address-text"></div>                  
					 </div>
                </div>
				<p class="company-label">
						<spring:theme code="company.detail.delivery.address" />
					</p>

                    <div class="row">
                        <div class="col-md-4 col-sm-6">
                            <input class="form-control dynamic-input-field req" id="deliveryAddress2" type="text" name="apbCompanyDeliveryAddressForm[0].deliveryAddress" 
                            value="" data-field-id="apbDeliveryAdress-0" maxlength="${inputMaxSize}"/>
                            <p class="del-error company-error"><spring:theme code="company.details.delivery.adddress.invalid"/></p>
                        </div>
                    </div>
							<input type="hidden" id="removeRequestAddress" name="apbCompanyDeliveryAddressForm[0].removeRequestAddress" value="0"/>
							<input type="hidden" id="changeRequestAddress" name="apbCompanyDeliveryAddressForm[0].changeRequestAddress" value="0"/>
							<input type="hidden" id="changeRequestAddressOnAddbutton" name="changeRequestAddressOnAddbutton[0].changeRequestAddressOnAddbutton" value="0"/>
                    <div class="company-label"><spring:theme code="company.detail.address.timeframe" /></div>
                    <div class="row">
                        <div class="col-md-2 col-sm-3 col-xs-6">
			                            <div class="company-label company-label-secondary"><spring:theme code="company.detail.deliver.from" /></div>

										
									<input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>23){this.value='HH';}else if(this.value<0){this.value='0';};"
                            type="text" maxlength="2" id ="timefrom-HH" placeholder="HH" name="apbCompanyDeliveryAddressForm[0].deliveryTimeFrameFromHH" value="" />
                            <span id="seperater_tag">:</span>
                            
									
                            <input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>59){this.value='MM';}else if(this.value<0){this.value='0';};" 
                            type="text" maxlength="2" id="timefrom-MM" placeholder="MM" name="apbCompanyDeliveryAddressForm[0].deliveryTimeFrameFromMM" value="" /> 
                             
                        </div>

                        <div class="col-md-2 col-sm-3 col-xs-6">
                            <div class="company-label company-label-secondary"><spring:theme code="company.detail.deliver.to" /></div>
										
                            <input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>23){this.value='HH';}else if(this.value<0){this.value='0';};" 
                            type="text" maxlength="2" id="timeTo-HH" placeholder="HH" name="apbCompanyDeliveryAddressForm[0].deliveryTimeFrameToHH" value="" />

                            <span id="seperater_tag">:</span>

                            <input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>59){this.value='MM';}else if(this.value<0){this.value='0';};" 
                            type="text" id="timeTo-MM"  maxlength="2" placeholder="MM" name="apbCompanyDeliveryAddressForm[0].deliveryTimeFrameToMM" value=""/>
                        </div>
                        <div class="clearfix"></div>
                         <div class="col-md-12 col-sm-12 col-xs-12">
                         	<div class="col-md-2 no-padding">
		                         <p class="input-date-1 company-error"><spring:theme code="company.detail.deliverfrom.hh.invalid" /></p>
		                         <p class="input-date-2 company-error"><spring:theme code="company.detail.deliverfrom.mm.invalid" /></p>
	                         </div>
	                         <div class="col-md-2 no-padding">
	                         <p class="input-date-3 company-error"><spring:theme code="company.detail.deliverto.hh.invalid" /></p>
	                         <p class="input-date-4 company-error"><spring:theme code="company.detail.deliverto.mm.invalid" /></p>
	                         </div>
	                         <div class="clearfix"></div>
	                         <div class="col-md-12 col-sm-12 col-xs-12 no-padding">
	                         <p class="deliveryDateError"><spring:theme code="company.detail.deliverto.condition.invalid" /></p>
	                         </div>
                         </div>
                    </div>
                      
                    <div class="row">
                    <div class="col-md-4 col-sm-6 col-xs-12">
                        <div class="company-label"><spring:theme code="company.detail.deliveryInstructions" /></div>
                        <textarea class="form-control textarea company-instructions" id="deliveryInstruction" maxlength="${delInstMaxSize}"  name="apbCompanyDeliveryAddressForm[0].deliveryInstruction"></textarea> 
                    </div>
                    </div>

                    <div class="row">
                        <div class="col-md-4 col-sm-6">
                            <div class="company-label"><spring:theme code="company.detail.deliveryCalendar" /></div>
                            <input class="form-control company-instructions" id="deliveryCalendar" type="text" maxlength="${calMaxSize}" name="apbCompanyDeliveryAddressForm[0].deliveryCalendar" value=""/> <br>
                        </div>
                    </div>
                </div>
                </div> 
                
                <c:set value="${count + 1}" var="count"/>
		<input type="hidden" value="${count}" id="counter"/>
		
		