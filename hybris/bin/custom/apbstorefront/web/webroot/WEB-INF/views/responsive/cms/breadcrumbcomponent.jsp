<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="breadcrumb"
	tagdir="/WEB-INF/tags/responsive/nav/breadcrumb"%>

<c:if test="${fn:length(breadcrumbs) > 0}">
    <c:choose>
        <c:when test="${cmsPage.uid eq 'login'|| (isForgetPassword != null && isForgetPassword == 'true') ||
        (isPasswordUpdated !=null && isPasswordUpdated=='true')}">
            <div id="opacity-background-body-content" class="breadcrumb-section">
                <breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
            </div>
        </c:when>
        <c:otherwise>
         <c:if test="${cmsPage.uid ne 'serverErrorPage'}">
	            <div class="breadcrumb-section">
	                <breadcrumb:breadcrumb breadcrumbs="${breadcrumbs}" />
	            </div>
          </c:if>
        </c:otherwise>
        
    </c:choose>
    
</c:if>