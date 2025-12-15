<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>

<nav:steps current="4"/>

<div class="page-confirmDealsChange row">
	<c:choose>
		<c:when test="${not empty activatedDealList or not empty deactivatedDealList}">
			<div class="col-lg-8">
				 <h1 class="h1"><spring:theme code="staff.portal.confirm.enabled.deals.title" /></h1>
				 <p class="offset-bottom-medium">
			    	<c:if test="${primaryAdminStatus == 'INVITED' }">
			    		<spring:theme code="staff.portal.invited.users.text" />
			    	</c:if>	
			    </p>

				 <c:if test="${not empty activatedDealList}">
				 <section class=" offset-bottom-large">
					 	<h3><spring:theme code="staff.portal.activated.deals.title" /></h3>
					 	<ul class="list-bordered">
					 		<c:forEach items="${activatedDealList}" var="activedEntry">
					 			<li>${activedEntry}</li>
					 		</c:forEach>
					 	</ul>
				</section>
				 </c:if>
				 
				 <c:if test="${not empty deactivatedDealList}">
				 <section class="offset-bottom-large">
					 	<h3><spring:theme code="staff.portal.deactivated.deals.title" /></h3>
					 	<ul class="list-bordered">
					 	<c:forEach items="${deactivatedDealList}" var="deactivatedEntry">
					 		<li>${deactivatedEntry}</li>
					 	</c:forEach>
					 	</ul>
				</section>
				 </c:if>
				 
				 <section class="offset-bottom-large">
				 <c:url value="/confirm-send" var="confirmAndSendUrl"/>
				 <form:form action="${confirmAndSendUrl}" method="post" id="confirmChangedDealsForm">
					 <div class="offset-bottom-large">
					 	<label for="comments" class="offset-bottom-small"><spring:theme code="staff.portal.instore.behaviour.title" /></label>
					 	<textarea name="behaviour" class="form-control" id="comments" cols="30" rows="10"></textarea>
					 </div>
					 
					 
					 <p class="offset-bottom-small"><spring:theme code="staff.portal.selected.customer.notice" /></p>
		
					<ul class="list-checkbox">
					 <c:if test="${not empty otherCustomerUid}">
				   <c:forEach items="${otherCustomerUid}" var="otherUid" varStatus="status">
					<li><div class="checkbox"><input id="checkEmail${status.index}" class="hidden" type="text" value="${otherUid}">
					<input name="toEmails" id="check${status.index}" type="checkbox"><label for="check${status.index}">${otherUid}</label></div></li>
					</c:forEach>
					</c:if>
			      </ul>
					<div class="form-group">
						<label for="add-email-filed"><spring:theme code="staff.portal.add.email.title" /></label>
		
						 <div class="row">
						 	<div class="col-sm-6 offset-bottom-xxsmall">
						 		<input type="text" class="form-control" id="add-email-filed">
						 		<span class="error" style="display:none" id="email-error"><spring:theme code="login.error.username.format.error"/></span>
						 		<input id="username_incorrect" type="text" class="hidden">
						 	</div>
						 	<div class="col-sm-3">
						 		<button id="add-email_button" type="button" class="btn btn-primary btn-large btn-simple btn-pull-top"><spring:theme code="staff.portal.button.add.email" /></button>
						 	</div>
						 </div>
					</div>
		
					<div class="checkbox offset-bottom-xlarge">
					    <input type="checkbox" id="checkSendToMe" name="sendToMe">
					    <label for="checkSendToMe"><spring:theme code="staff.portal.send.confirm.email.messages"/></label>
					</div>
					 
					<button id="confirmSend_button" type="submit" class="btn btn-primary btn-large btn-simple" disabled="disabled" ><spring:theme code="staff.portal.button.confirm.send.email" /></button>
		
		   	 </form:form>
		   	 </section>
			</div>
		</c:when>
		<c:otherwise>
			<div class="col-lg-8">
				<h1 class="h1"><spring:theme code="staff.portal.confirm.enabled.no.changed.deals.title" /></h1>
			</div>
		</c:otherwise>
	</c:choose>
</div>