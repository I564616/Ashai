<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>

<spring:htmlEscape defaultHtmlEscape="true"/>
<div class="row">
	<div class="col-md-12 col-sm-12 col-xs-12">
		<div class="form-group" id="browse_btn-contactus">
			 <spring:theme code="contactus.fileUpload" />
			  <format:bytes bytes="${pdfFileMaxSize}"/>
			  <spring:theme code="contactus.fileUpload.size.format"/>
			<span class="skip"></span>
		</div>
	</div>
</div>
    
<div class="row">
    <div class="col-md-12 col-sm-12 col-xs-12">
        <div class="file-upload__container float-left ">
            <!-- <div class="file-upload__wrapper btn btn-primary btn-upload btn-block"
                id="chooseFileButton"> -->
            <div class="form-group contactus-file-upload js-file-upload">
                <div class="file-upload__wrapper btn btn-primary btn-upload btn-block" id="chooseFileButton">

                    <spring:theme code="import.pdf.request.register.chooseFile" />
                    <div class="form-group" id="browse_btn-contactus">
                         <input type="file" id="pdfFile" name="pdfFile" <spring:theme code="register.request.trustDeed" /> class="file-upload__input js-file-upload__input noSpinnerCls" accept="application/pdf, image/png, image/jpg, image/jpeg, image/gif" data-file-max-size="${pdfFileMaxSize}" />
                    </div>
                </div>
            </div>
            <c:if test="${not empty errors}">
                <c:forEach items="${errors}" var="error">
                    <c:if test="${fn:contains(error.code, 'import.pdf.file') && error.code ne 'import.pdf.file.fileRequired'}">
                        <div class="form-group has-error">
                            <div class="help-block">
                                <spring:theme code="${error.code}" />
                            </div>
                        </div>
                    </c:if>

                </c:forEach>
            </c:if>
        </div>
        <div class="float-left">
            <span class="file-upload__file-name-contactus js-file-upload__file-name"></span>
        </div>
    </div>
</div>


<div style="display: none;">
        <span id="import-csv-file-max-size-exceeded-error-message"><spring:theme code="import.pdf.file.fileMaxSizeExceeded"/></span>
        <span id="import-csv-no-file-chosen-error-message"><spring:theme code="import.csv.savedCart.noFile"/></span>
    </div>
<common:globalMessagesTemplates/>