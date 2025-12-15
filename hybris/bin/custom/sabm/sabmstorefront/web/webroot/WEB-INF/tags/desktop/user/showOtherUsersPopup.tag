<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<div id="showOtherUsersPopup" class="change-Address-Popup mfp-hide">
   <h1><spring:theme code="text.show.otheruser.title" /></h1>
   <p> <spring:theme code="text.show.otheruser.prompt.info" /></p>
   <ul >
   <c:forEach items="${customerList}" var="customer" >
	<li class="offset-bottom-xxsmall">${customer.name};<spring:theme code="text.users.list.item.email" arguments="${customer.uid},${customer.uid}" /></li>
	</c:forEach>
   </ul>
  <div class="col-md-6 btn-wrap">
	<span class="btn btn-primary" onclick="$.magnificPopup.close()" ><spring:theme code="text.show.otheruser.ok.botton" /></span>
	</div>	
</div>