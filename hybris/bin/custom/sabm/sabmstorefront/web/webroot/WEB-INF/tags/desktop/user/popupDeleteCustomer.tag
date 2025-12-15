<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<div id="remove-user" class="price-popup mfp-hide">
	<h2 class="h1"><spring:theme code="text.business.unit.delete.user.popup.confirm.title"/></h2>
	<p><spring:theme code="text.business.unit.delete.user.popup.notic.message"/></p>
	<p  class="offset-bottom-small"><spring:theme code="text.business.unit.delete.user.popup.confirm.title"/></p>
	
	<ul class="list-modal">
      <li>
        <button onclick="$.magnificPopup.close()" class="btn btn-secondary btn-s offset-bottom-small">
        		<spring:theme code="button.cancle.delete.user.popup"/>
        </button>
      </li>
      <li>
        <button id="confirm-delete-user" class="btn btn-primary btn-s">
        		<spring:theme code="button.confirmed.delete.user"/>
        </button>
      </li>
    </ul>
	
	<c:url value="/your-business/deleteUser" var="deleteUserUrl"/>
	<form:form action="${deleteUserUrl}" method="post" id="deleteUserForm">
		<input type="hidden" name="uid" value="${uId}"/>
		<input type="hidden" name="b2bUnitId" value="${b2bUnitId}"/>
	</form:form>
		
</div>