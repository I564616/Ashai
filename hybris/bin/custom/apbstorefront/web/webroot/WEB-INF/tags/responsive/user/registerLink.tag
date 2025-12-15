<%@ tag language="java" pageEncoding="ISO-8859-1"%>
<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="formElement" tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>

<!-- Register section start -->
<c:url value="/register" var="registerUrl"/>
<div class="registerSection">
    <c:choose>
        <c:when test="${cmsSite.uid eq 'sga'}">
            <div class="login-page__headline hidden-xs hidden-sm">
                <spring:theme code="login.register.title.sga" />
            </div>
        </c:when>
        <c:otherwise>
            <div class="login-page__headline hidden-xs hidden-sm">
                <spring:theme code="login.register.title.asahi" />
            </div>
        </c:otherwise>
    </c:choose>  
	
	<div class="register-benefit">
	<spring:theme code="register.benefit.message"/>
	</div>
	<div class="registerLink" id="register-link-login">
		<a href="${registerUrl}" class="site-anchor-link"><spring:theme code="register.link"/></a>
	</div>
</div>

<!-- Register section end -->
