<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
           tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="pdf" tagdir="/WEB-INF/tags/responsive/pdf"%>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>


<div class="user-register__headline">
    <spring:theme code="register.become.customer1" />
</div>
<p>
    <spring:theme code="apb.user.register.updated.notification1" />
</p>
<br>
<div class="row btn-register-request">
    <div class="col-md-3 col-sm-6">
        <div class="form-actions clearfix">
            <button type="button" class="btn btn-default btn-block btn-vd-primary apb-registration-link1"><spring:theme code="apb.user.registration.link" /></button>
        </div>
    </div>
</div>

<div class="user-register__headline">
    <spring:theme code="register.become.customer2" />
</div>
<p>
    <spring:theme code="apb.user.register.updated.notification2" />
</p>
<br>
<div class="row btn-register-request">
    <div class="col-md-3 col-sm-6">
        <div class="form-actions clearfix">
            <button type="button" class="btn btn-default btn-block btn-vd-primary apb-registration-link2"><spring:theme code="apb.user.registration.link" /></button>
        </div>
    </div>
</div>
