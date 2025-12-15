<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="footer" tagdir="/WEB-INF/tags/responsive/common/footer"  %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<c:if test="${component.visible}">
	<div class="container-fluid" id="remove-padding-footer">
	    <div class="footer__top">
                <c:choose>
                    <c:when test="${cmsSite.uid eq 'apb'}">
                        <div class="col-xs-12 col-sm-12 col-md-12 footer-list">
                            <div class="col-md-2 col-sm-3 col-xs-4 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/contactus"/>"><spring:theme code="footer.nav.link.contact" /></a></b>
                            </div>
                            <div class="col-md-2 col-sm-3 col-xs-4 footer-nav-links">
                               <b><span> <spring:theme code="footer.nav.link.privacy.apb"/></span></b>
                            </div>
                            <div class="col-md-2 col-sm-3 col-xs-4 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/termsAndLegal"/>"><spring:theme code="footer.nav.link.legal" /></a></b>
                            </div>
                        </div>
                    </c:when>
                    <c:otherwise>
                        <div class="col-xs-12 col-sm-12 col-md-12 footer-list">
                            <div class="col-md-2 col-sm-3 col-xs-3 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/contactus"/>"><spring:theme code="footer.nav.link.contact" /></a></b>

                            </div>
                            <div class="col-md-2 col-sm-3 col-xs-3 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/faq"/>"><spring:theme code="footer.nav.link.faq" /></a></b>
                            </div>
                            <div class="col-md-2 col-sm-3 col-xs-3 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/termsAndLegal"/>"><spring:theme code="footer.nav.link.legal" /></a></b>
                            </div>
                            <div class="col-md-2 col-sm-3 col-xs-3 footer-nav-links">
                               <b><a class="footer_test_text" href="<c:url value="/promotion-winners"/>"><spring:theme code="footer.nav.link.promotion-winners" /></a></b>
                            </div>
                        </div>
                    </c:otherwise>
                </c:choose>
            </div>
	    </div>
	    	<div class="footer__bottom">
	    		<c:choose>
	    		<c:when test="${cmsSite.uid eq 'apb'}">
        	    <div class="footer__copyright">
        	        <div>${fn:escapeXml(notice)}</div>
        	        <div>
        	        <spring:theme code="footer.apb.paragraph" />
        	        </div>
        	    </div>
        	    </c:when>
        	    <c:otherwise>
        	     <div class="footer__copyright">
        	        <div class="container">${fn:escapeXml(notice)}</div>
        	    </div>
        	    </c:otherwise>
        	    </c:choose>
        	</div>
	</div>
</c:if>