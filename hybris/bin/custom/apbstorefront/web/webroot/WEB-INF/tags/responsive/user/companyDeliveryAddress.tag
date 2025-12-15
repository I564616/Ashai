<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
	<input type="hidden" value="${companyData.companyBillingAddress}" id="companyBillingAddress">
	<c:forEach items="${apbCompanyDetailsForm.b2bUnitDeliveryAddressDataList}" var="company" varStatus="statusComp">
	<c:set value="0" var="count"/>
	
	<%-- <input type="text" value="${company.deliveryAddresses}" id="deliveryAddress"/> --%>
<c:forEach items="${company.deliveryAddresses}" var="item" varStatus="status">
	<input type="hidden" value="${item.defaultAddress}" id="defaultAddressCheck" />
	<div class="dynamic-delivery-address" data-del-id="${status.index}" id="change-address">
		<div class="user-register__subheadline">
			<c:if test="${status.index ge 0 && item.defaultAddress}">
				<c:set var="showCounterValue" value="${status.index}" />
			</c:if>
			<c:if test="${status.index ge 0 && !item.defaultAddress}">
				<c:set var="showCounterValue" value="${showCounterValue + 1}" />
			</c:if>
			<c:choose>
				<c:when test="${status.index ge 0 && item.defaultAddress}">
					<p class="add-additional-address-text">
						<span class="removeAddressLink">
                      </span>
					</p>

				</c:when>
				<c:otherwise>
					<p class="add-additional-address-text checkout_subheading row-margin-fix">
						<spring:theme code="company.detail.additional.delivery.address" />&nbsp;<span id="headingCounter">(#${showCounterValue})</span>
						<c:if test="${cmsSite.uid ne 'sga'}">
							<span class="removeAddressLink">
								<a class="company-remove-link" href="javascript:void(0)" ><spring:theme code="company.detail.remove.delivery.address"/> </a>
							 </span>
						</c:if>
					</p>

				</c:otherwise>
			</c:choose>
		
		<c:choose>
			<c:when test="${cmsSite.uid ne 'sga'}">
				<p class="company-label">
					<spring:theme code="company.detail.delivery.address" />
				</p>
				</div>
				<div class="row">
					<div id="deliveryCalendar-companyDetails" class="col-md-4 col-sm-6">
						<input class="form-control dynamic-input-field req" id="deliveryAddress2" type="text" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryAddress" value="${item.deliveryAddress}" data-field-id="apbDeliveryAdress-${status.index}" maxlength="${inputMaxSize}" />
						<p class="del-error company-error">
							<spring:theme code="company.details.delivery.adddress.invalid" />
						</p>

						<c:if test="${status.index eq 0}">
							<input type="hidden" value="${item.deliveryAddress}" id="deliveryAddress0">

						</c:if>
					</div>
				</div>
			</c:when>
			<c:otherwise>
			</div>
				<div class="row label-section">
					<div class="col-md-4 col-sm-6">
						<div class="label-heading">
							<spring:theme code="company.detail.delivery.address" /><br>
						</div>
						<c:choose>
							<c:when test="${not empty item.deliveryAddress}">  
								${item.deliveryAddress}
							</c:when>
							<c:otherwise>
								<spring:theme code="sga.null.value.found" />						
							</c:otherwise>
						</c:choose>
					</div>
				</div>	
				
				<c:if test="${status.index eq 0}">
					<input type="hidden" value="${item.deliveryAddress}" id="deliveryAddress0">

				</c:if>
			</c:otherwise>
		</c:choose>

		<input type="hidden" id="removeRequestAddress" name="apbCompanyDeliveryAddressForm[${status.index}].removeRequestAddress" value="0" />
		<input type="hidden" id="changeRequestAddress" name="apbCompanyDeliveryAddressForm[${status.index}].changeRequestAddress" value="0" />
		<input type="hidden" id="changeRequestAddressOnAddbutton" name="changeRequestAddressOnAddbutton[${status.index}].changeRequestAddressOnAddbutton" value="0" />
		
		<c:if test="${cmsSite.uid ne 'sga'}">
		<div class="company-label">
			<spring:theme code="company.detail.address.timeframe" />
		</div>
		<div class="row">
			<div class="col-md-2 col-sm-3 col-xs-6">
				<div class="company-label company-label-secondary">
					<spring:theme code="company.detail.deliver.from" />
				</div>

				<c:set value="${item.deliveryTimeFrameFromHH}" var="fromHH" />
				<c:set value="${item.deliveryTimeFrameFromMM}" var="fromMM" />
				<c:if test="${item.deliveryTimeFrameFromHH eq 0}">
					<c:set value="00" var="fromHH" />
				</c:if>
				<c:if test="${item.deliveryTimeFrameFromMM eq 0}">
					<c:set value="00" var="fromMM" />
				</c:if>
				<input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>23){this.value='HH';}else if(this.value<0){this.value='0';};" type="text" maxlength="2" id="timefrom-HH" placeholder="HH" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryTimeFrameFromHH" value="${fromHH}" />
				<span id="seperater_tag">:</span>


				<input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>59){this.value='MM';}else if(this.value<0){this.value='0';};" type="text" maxlength="2" id="timefrom-MM" placeholder="MM" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryTimeFrameFromMM" value="${fromMM}" />

			</div>

			<div class="col-md-2 col-sm-3 col-xs-6">
				<div class="company-label company-label-secondary">
					<spring:theme code="company.detail.deliver.to" />
				</div>
				<c:set value="${item.deliveryTimeFrameToHH}" var="toHH" />
				<c:set value="${item.deliveryTimeFrameToMM}" var="toMM" />
				<c:if test="${item.deliveryTimeFrameToHH eq 0}">
					<c:set value="12" var="toHH" />
				</c:if>
				<c:if test="${item.deliveryTimeFrameToMM eq 0}">
					<c:set value="00" var="toMM" />
				</c:if>
				<input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>23){this.value='HH';}else if(this.value<0){this.value='0';};" type="text" maxlength="2" id="timeTo-HH" placeholder="HH" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryTimeFrameToHH" value="${toHH}" />

				<span id="seperater_tag">:</span>

				<input class="form-control company-input-date req" onkeyup="this.value=this.value.replace(/[^0-9]/g,'')" onblur="if(this.value>59){this.value='MM';}else if(this.value<0){this.value='0';};" type="text" id="timeTo-MM" maxlength="2" placeholder="MM" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryTimeFrameToMM" value="${toMM}" />

			</div>
			<div class="clearfix"></div>
			<div class="col-md-12 col-sm-12 col-xs-12">
				<div class="col-md-2 no-padding">
					<p class="input-date-1 company-error">
						<spring:theme code="company.detail.deliverfrom.hh.invalid" />
					</p>
					<p class="input-date-2 company-error">
						<spring:theme code="company.detail.deliverfrom.mm.invalid" />
					</p>
				</div>
				<div class="col-md-2 no-padding">
					<p class="input-date-3 company-error">
						<spring:theme code="company.detail.deliverto.hh.invalid" />
					</p>
					<p class="input-date-4 company-error">
						<spring:theme code="company.detail.deliverto.mm.invalid" />
					</p>
				</div>
				<div class="clearfix"></div>
				<div class="col-md-12 col-sm-12 col-xs-12 no-padding">
					<p class="deliveryDateError">
						<spring:theme code="company.detail.deliverto.condition.invalid" />
					</p>
				</div>
			</div>
		</div>
		</c:if>
		
		<c:choose>
			<c:when test="${cmsSite.uid ne 'sga'}">
				<div class="row">
					<div class="col-md-4 col-sm-6 col-xs-12">
						<div class="company-label">
							<spring:theme code="company.detail.deliveryInstructions" />
						</div>
						<textarea class="textarea form-control company-instructions" id="deliveryInstruction" maxlength="${delInstMaxSize}" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryInstruction">${item.deliveryInstruction}</textarea>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<br>
				<div class="row label-section">
					<div class="col-md-4 col-sm-6">
						<div class="label-heading">
							<spring:theme code="sga.company.detail.deliveryInstructions" />
						</div>
						<c:choose>
							<c:when test="${not empty item.deliveryInstruction}">  
								${item.deliveryInstruction}
							</c:when>
							<c:otherwise>
								<spring:theme code="sga.null.value.found" />						
							</c:otherwise>
						</c:choose>
						<br><br>
					</div>
				</div>	
			</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${cmsSite.uid ne 'sga'}">
				<div class="row">
					<div id="deliveryCalendar-companyDetails" class="col-md-4 col-sm-6">
						<div class="company-label">
							<spring:theme code="company.detail.deliveryCalendar" />
						</div>
						<input class="form-control company-instructions" id="deliveryCalendar" type="text" maxlength="${calMaxSize}" name="apbCompanyDeliveryAddressForm[${status.index}].deliveryCalendar" value="${item.deliveryCalendar}" /> <br>
					</div>
				</div>
			</c:when>
			<c:otherwise>
				<div class="row label-section">
					<div class="col-md-4 col-sm-6">
						<div class="label-heading">
							<spring:theme code="sga.company.detail.deliveryCalendar" /> 
						</div>
						<c:choose>
							<c:when test="${not empty item.deliveryCalendar}">  
								${item.deliveryCalendar}
							</c:when>
							<c:otherwise>
								<spring:theme code="sga.null.value.found" />						
							</c:otherwise>
						</c:choose>
					</div>
				</div>	
			</c:otherwise>
		</c:choose>
	</div>


	<c:if test="${status.index eq (fn:length(company.deliveryAddresses) -1)}">

		<!---------------------------- ADDTIONAL DYNAMIC DELIVERY ADDRESSES ARE ADDED INSIDE THIS DIV  ------------------------------------->
		<div class="new-address-dynamic"></div>

		<!---------------------------- THIS HIDDEN DIV IS CLONED TO CREATE NEW ADDTIONAL DELIVERY ADDRESSES ------------------------------------->
	</c:if>
	<c:set value="${count + 1}" var="count" />
</c:forEach>


</c:forEach>

<input type="hidden" value="${count}" id="counter" />