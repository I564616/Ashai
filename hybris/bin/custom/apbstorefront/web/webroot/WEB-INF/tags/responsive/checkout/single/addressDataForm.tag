<%@ attribute name="deliveryAddresses" required="true" type="java.util.List"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<div class="row">
    <div class="col-md-3 col-sm-6">
        <div id="ptest">
            <p><spring:theme code="checkout.summary.delivery.address" /></p>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-3 col-sm-6">
        <form:select class="form-control" id="address-select-dropdown" path="selectedDeliveryAddress">
            <c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">
               <!--  <option id="${status.count}" value="${deliveryAddress.recordId}">Address #${status.count}, ${deliveryAddress.firstName} ${deliveryAddress.lastName} </option> -->
					<c:choose>
            		<c:when test="${deliveryAddress.defaultAddress eq 'true'}">
            			<<form:option id="${status.count}" value="${deliveryAddress.recordId}">Default Address,  ${deliveryAddress.town}</form:option>
            		</c:when>
            		<c:otherwise>
            			<<form:option id="${status.count}" value="${deliveryAddress.recordId}"> Alt Add ${status.count-1},  ${deliveryAddress.town}</form:option>
            		</c:otherwise>
            	</c:choose>
 				
            </c:forEach>
        </form:select>
    </div>
</div>
    
 <div class="selected-address">
	<c:forEach items="${deliveryAddresses}" var="deliveryAddress" varStatus="status">					
		<c:choose>
			<c:when test="${deliveryAddress.recordId == requestScope.customerCheckoutForm.selectedDeliveryAddress }">
				<c:set var="defaultAddressRecordId" value="${deliveryAddress.recordId}" scope="page" />
				<c:set var="defaultDeliveryInstruction" value="${deliveryAddress.deliveryInstruction}" scope="page" />
				<div class="address-data" >
			</c:when>
			<c:otherwise>
				<div class="address-data hide">
			</c:otherwise>
		</c:choose>
			<input type="hidden" class="address-data-index" value="${deliveryAddress.recordId}" />
			<input type="hidden" class="delivery-instruction" value="${deliveryAddress.deliveryInstruction}" />
			<div class="selected-address-data"> 
             <c:if test="${not empty deliveryAddress.companyName}">
                  ${fn:escapeXml(deliveryAddress.companyName)}<br>
            </c:if>
           <c:if test="${(not empty deliveryAddress.firstName) || (not empty deliveryAddress.lastName)}">
                     ${fn:escapeXml(deliveryAddress.title)}
          </c:if>  
           ${fn:escapeXml(deliveryAddress.firstName)}
           ${fn:escapeXml(deliveryAddress.lastName)}
           <c:if test="${(not empty deliveryAddress.firstName) || (not empty deliveryAddress.lastName)}">
                    <br>
            </c:if>
           <c:if test="${(not empty deliveryAddress.line1) || (not empty deliveryAddress.line2)}">
                 ${fn:escapeXml(deliveryAddress.line1)}
               ${fn:escapeXml(deliveryAddress.line2)}
             </c:if>
           <c:if test="${not empty deliveryAddress.town}">
                 <br>
                 ${fn:escapeXml(deliveryAddress.town)}
             </c:if>
              <c:if test="${not empty deliveryAddress.region.name}">
                              &nbsp;${fn:escapeXml(deliveryAddress.region.name)}
                  </c:if>
              <c:if test="${not empty deliveryAddress.postalCode}">
                     <br>
                      ${fn:escapeXml(deliveryAddress.postalCode)}
                  </c:if>
           </div>
           <br><br>
            <div class="row">
			    <div class="deliveryInstructionHeader col-md-5 col-sm-6">
			    </div>
			</div>
            <div class="row">
                <div class="col-md-3 col-sm-6">
                    <div class="delivery-instruction">
                         <textarea id="delivery-instructions-checkout" rows="5" maxlength="${deliveryInstructionLength}" data-role="none" style="resize: none;" class="deliveryInstructionInput form-control" name="deliveryAddress.deliveryInstruction" value="${fn:escapeXml(deliveryAddress.deliveryInstruction)}">${fn:escapeXml(deliveryAddress.deliveryInstruction)}</textarea> 
                    		<!-- <form:textarea id="delivery-instructions-checkout" maxlength="${deliveryInstructionLength}" data-role="none" style="resize: none;" class="deliveryInstructionInput form-control"  path="deliveryInstruction"  />  --> 
                    </div>  
                </div>
            </div>
           </div>    
	</c:forEach>
	<input type="hidden" id="selectedAddressRecordId" name="deliveryAddressId" value="${defaultAddressRecordId}" />
	<input type="hidden" id="deliveryInstruction" name="deliveryInstruction" value="${defaultDeliveryInstruction}" />
</div> 
