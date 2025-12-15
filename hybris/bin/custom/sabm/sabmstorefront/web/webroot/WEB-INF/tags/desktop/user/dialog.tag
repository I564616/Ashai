<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="message" required="false" type="java.lang.String" %>
<%@ attribute name="title" required="false" type="java.lang.String" %>

<div class="price-popup mfp-hide" id="forgotpwd-popup">
    <h2 class="h1">
        <c:choose>
            <c:when test="${empty title}">
                <spring:theme code="forgottenPwd.send" />
            </c:when>
            <c:otherwise>
                ${title}
            </c:otherwise>
        </c:choose>
    </h2>
    <p class="text-justify">
	    <spring:theme code="forgottenPwd.popup.text1" />
		    <c:choose>
		        <c:when test="${empty message}">
		            <spring:theme code="forgottenPwd.popup.text2" />
		        </c:when>
		        <c:otherwise>
		            ${message}
		        </c:otherwise>
		    </c:choose>
	</p>
    <div class="row text-right">
        <div class="col-xs-12 mt-10 text-center">
            <button
                id="submit_button"
                type="button"
                class="btn btn-primary">
                <spring:theme code="forgottenPwd.popup.confirm" />
            </button>
        </div>
    </div>
</div>