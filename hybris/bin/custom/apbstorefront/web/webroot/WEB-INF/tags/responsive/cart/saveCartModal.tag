<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ attribute name="actionUrl" required="true" type="java.lang.String"%>
<%@ attribute name="titleKey" required="true" type="java.lang.String"%>
<%@ attribute name="messageKey" required="false" type="java.lang.String"%>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<spring:htmlEscape defaultHtmlEscape="true" />
<c:set var="loc_val">
    <spring:message code="basket.save.cart.max.chars" />
</c:set>

<%--Cart Page Customization removed the form fields as per the requirement --%>
<input type="hidden" id="localized_val" name="localized_val" value="${loc_val}"/>
	 <div class="saveCartTemplate">
		<form:form action="${actionUrl}" id="saveCartForm" cssClass="form-horizontal" modelAttribute="saveCartForm" autocomplete="off">
			 <div class="form-group">
			      <label class="control-label col-sm-6 col-md-6"><label:message messageCode="basket.save.cart.name.apb"/></label>
			      
			      <div class="col-xs-6 col-sm-3 col-md-3">   
			      	<form:input cssClass="form-control" id="saveCartName" path="name" maxlength="75" placeholder="Template Name" />           
			      </div>
			      <div class="col-xs-6 col-sm-3 col-md-3 no-xs-padding-left">   
			      	<input type="hidden" id="hasBonusStockProduct" name="hasBonusStockProduct" value="${hasBonusStockProductOnly}"/>  
				      <c:choose>
				      	<c:when test="${cmsSite.uid eq 'apb' && asmMode eq 'true' && hasBonusStockProductOnly}">
				      		<button type="submit" class="btn btn-primary btn-template-block " id="saveCartButton" disabled="disabled"/>
				            <spring:theme code="basket.save.cart.action.save"/>
				      	</c:when>
				      	<c:otherwise>
						      <c:choose>
						    	 <c:when test="${fn:length(cartData.entries) > 0}"> 
							    	 <button type="submit" class="btn btn-primary btn-template-block " id="saveCartButton" />
				                     <spring:theme code="basket.save.cart.action.save"/>
						    	 </c:when>
						    	 <c:otherwise>
							    	 <button type="submit" class="btn btn-primary btn-template-block " id="saveCartButton" disabled="disabled"/>
				                <spring:theme code="basket.save.cart.action.save"/>
						    	 </c:otherwise>  
						      </c:choose>  
				      	</c:otherwise>
				      </c:choose>
			      </div>
   			 </div>
			</form:form>
	</div>

