<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>

	<div class="selected-address">
	<c:forEach items="${pickupAddress}" var="pickupAddress" varStatus="status">
		<c:choose>
			<c:when test="${status.index == 0 }">
				<c:set var="defaultAddressRecordId"	value="${pickupAddress.recordId}" scope="page" />
				<div class="address-data hide" >
			</c:when>
			<c:otherwise>
				<div class="address-data hide">
			</c:otherwise>
		</c:choose>
	
		<input type="hidden" class="address-data-index" value="${pickupAddress.recordId}" />
		<div class="selected-address-data">
		      <c:if test="${not empty pickupAddress.companyName}">
		       	${fn:escapeXml(pickupAddress.companyName)}<br>
		      </c:if>
	          <c:if test="${(not empty pickupAddress.firstName) || (not empty pickupAddress.lastName)}">
              		 ${fn:escapeXml(pickupAddress.title)}
            </c:if>  
	            ${fn:escapeXml(pickupAddress.firstName)}
	            ${fn:escapeXml(pickupAddress.lastName)}
            <c:if test="${(not empty pickupAddress.firstName) || (not empty pickupAddress.lastName)}">
              		<br>
              </c:if>
            <c:if test="${(not empty pickupAddress.line1) || (not empty pickupAddress.line2)}">
		      	${fn:escapeXml(pickupAddress.line1)}
	            ${fn:escapeXml(pickupAddress.line2)}
		      </c:if>
		      <c:if test="${not empty pickupAddress.town}">
		      	<br>
		      	${fn:escapeXml(pickupAddress.town)}
		      </c:if>  
		      <c:if test="${not empty pickupAddress.region.name}">
                &nbsp;${fn:escapeXml(pickupAddress.region.name)}
            </c:if>
            <c:if test="${not empty pickupAddress.postalCode}">
               <br>
                ${fn:escapeXml(pickupAddress.postalCode)}
            </c:if>
		</div>
		</div>
	</c:forEach>
	<input type="hidden" id="selectedAddressRecordId" name="deliveryAddressId" value="${defaultAddressRecordId}" />
</div>