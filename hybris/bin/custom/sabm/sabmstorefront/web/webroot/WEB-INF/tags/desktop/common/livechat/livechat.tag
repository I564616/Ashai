<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

 <sec:authorize access="!hasAnyRole('ROLE_ANONYMOUS')">
	<cms:component uid="livechatcomponent" evaluateRestriction="true"/>
</sec:authorize>  
 
 