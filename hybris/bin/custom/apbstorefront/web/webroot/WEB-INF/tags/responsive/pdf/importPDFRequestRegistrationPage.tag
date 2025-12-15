<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/responsive/common" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<spring:htmlEscape defaultHtmlEscape="true"/>

<div class="row no-margin">
	<div class="form-group request-file-upload js-file-upload">
		<div class="file-upload__wrapper btn btn-primary btn-upload btn-block"
			id="chooseFileButton">
			<span><spring:theme code="import.pdf.request.register.chooseFile" /></span>
			<input type="file" id="pdfFile" name="pdfFile" <spring:theme	code="register.request.trustDeed" />
				class="file-upload__input js-file-upload__input" accept="application/pdf"
				data-file-max-size="${pdfFileMaxSize}" />
		</div>
	
<!--    please don't comment this line -->
			<span class="file-upload__file-name-request js-file-upload__file-name"></span>

		<c:if test="${not empty errors}">
			<c:forEach items="${errors}" var="error">
			
			     <c:if test="${fn:contains(error.code, 'import.pdf.file')}">
			     		<div class="form-group has-error">
			     			<div class="help-block">
	 						 	<spring:theme code="${error.code}" />
	 						</div>
	 				 	</div>	
	 				</c:if> 
	 				
			</c:forEach>
		</c:if>
	</div>
</div>

<%-- <spring:theme code="import.no.file.selected"/> --%>

<div style="display: none">
        <span id="import-csv-generic-error-message"><spring:theme code="import.csv.savedCart.genericError"/></span>
        <span id="import-csv-file-max-size-exceeded-error-message"><spring:theme code="import.pdf.file.fileMaxSizeExceeded"/></span>
        <span id="import-csv-file-csv-required"><spring:theme code="import.pdf.file.filePDFRequired"/></span>
        <span id="import-csv-no-file-chosen-error-message"><spring:theme code="import.csv.savedCart.noFile"/></span>
    </div>
<common:globalMessagesTemplates/>