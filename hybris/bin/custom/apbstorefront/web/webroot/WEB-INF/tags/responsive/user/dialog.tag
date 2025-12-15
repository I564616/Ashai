<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ attribute name="message" required="false" type="java.lang.String" %>
<%@ attribute name="title" required="false" type="java.lang.String" %>

<!-- Get close icon url-->
<spring:theme code="img.closeIcon" text="/" var="closeIconPath" />
<c:choose>
	<c:when test="${originalContextPath ne null}">
		<c:url value="${closeIconPath}" context="${originalContextPath}" var="closeIconUrl" />
	</c:when>
	<c:otherwise>
		<c:url value="${closeIconPath}" var="closeIconUrl" />
	</c:otherwise>
</c:choose>

<c:choose>
    <c:when test="${cmsSite.uid eq 'sga'}">
        <c:set value="" var="white" />
    </c:when>
    <c:otherwise>
        <c:set value="white" var="white" />
    </c:otherwise>
</c:choose>

<div id="forgorpwd-template" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="uploaderTemplateLabel"  aria-hidden="false" data-backdrop="false">
    <div class="modal-dialog recommendation-popup-container">
        <div class="modal-content" style="cursor: auto;">
            <div class="modal-body ">
                <h2 class="h3">
                    <strong>
                        <c:choose>
                            <c:when test="${empty title}">
                                <spring:theme code="login.link.resetPwd" />
                            </c:when>
                            <c:otherwise>
                                ${title}
                            </c:otherwise>
                        </c:choose>
                    </strong>
                </h2>

                <div class="row mt-25">
                    <div class="col-xs-12">
                        <c:choose>
                            <c:when test="${cmsSite.uid eq 'sga'}">
                                <spring:theme code="sga.login.link.resetPwd.popup.alb.text" var="site"/>
                                <spring:theme code="sga.login.link.resetPwd.popup.cubpb" var="otherSite"/>
                            </c:when>
                            <c:otherwise>
                                <spring:theme code="apb.login.link.resetPwd.popup.cubpb.text" var="site"/>
                                <spring:theme code="sga.login.link.resetPwd.popup.alb" var="otherSite"/>
                            </c:otherwise>
                        </c:choose>
                        <p>${site}&nbsp;<spring:theme code="sga.login.link.resetPwd.popup.text1" /></p>
                        <ul style="margin-left: 15px;">
                            <li>
                                <spring:theme code="sga.login.link.resetPwd.popup.cub" /> <spring:theme code="sga.login.link.resetPwd.popup.or" />
                            </li>
                            <li>${otherSite}</li>
                        </ul>
                        <c:choose>
                            <c:when test="${empty message}">
                                <p><spring:theme code="sga.login.link.resetPwd.popup.text2" /></p>
                            </c:when>
                            <c:otherwise>
                                <p>${message}</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>
            </div>

            <div class="modal-footer" style="text-align:center; border-top: none;">
                <button type="submit" class="btn btn-vd-primary ${white}" style="width: 50%">CONFIRM</button>
            </div>

            <div class="popup-close cursor" data-dismiss="modal">
                <img src="${closeIconUrl}">
            </div>
        </div>
    </div>
</div>