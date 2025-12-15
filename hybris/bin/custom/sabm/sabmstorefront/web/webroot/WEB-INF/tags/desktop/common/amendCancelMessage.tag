<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix = "fmt" uri = "jakarta.tags.fmt" %>

<div class="amendment-cancellation light-blue-messagebox" >
    <div>

        
        <spring:theme code="text.account.order.amendment" arguments="${orderData.cutoffTime}" />

        
    </div>
    <br>
    <div>
        <spring:theme code="text.account.order.cancellation"/>
    </div>
</div>