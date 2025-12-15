<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="pagination" tagdir="/WEB-INF/tags/responsive/nav/pagination" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<spring:url value="/my-account/planograms/add" var="uploaderUrl"/>

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

<c:set value="${false}" var="isBDECUSTOMERGROUP" />
<sec:authorize access="hasAnyRole('ROLE_BDECUSTOMERGROUP')" >
    <c:set value="${true}" var="isBDECUSTOMERGROUP" />
</sec:authorize>

<spring:theme code="text.account.planograms.table.head.document.name" var="documentName" />
<spring:theme code="text.account.planograms.table.head.uploaded" var="uploaded" />
<spring:theme code="text.account.planograms.table.head.uploaded.by" var="uploadedBy" />

<!-- Planograms -->
<div class="user-register__headline">
    <spring:theme code="text.account.planograms.heading" /><br>
</div>

<p class="mt-10 mb-25"> <spring:theme code="text.account.planograms.text1" /> 
	<c:if test="${not empty repEmail or not empty repName or not empty repPhone}">
		<spring:theme code="text.account.planograms.text2" /> 
		<c:if test="${not empty repName}"> <spring:theme code="text.account.planograms.text3.repName" arguments="${repName}"/> </c:if>
		<c:if test="${not empty repEmail}"> <spring:theme code="text.account.planograms.text3.repEmail" arguments="${repEmail}"/> </c:if>
		<c:if test="${not empty repPhone}">
			<c:if test="${empty repEmail}">&nbsp;at</c:if>
			<c:if test="${not empty repEmail}">or</c:if>
		 	<spring:theme code="text.account.planograms.text3.repPhone" arguments="${repPhone}" />
		 </c:if>
	</c:if>
</p>

<!-------------------- Additional Planograms -------------------------------------->
<div class="row mt-10 mb-35">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="text.account.planograms.customer.account.section.title" /><br>
    </div>

    <p class="mt-35 mb-25"><spring:theme code="text.account.planograms.customer.account.section.text1" /></p>

    <c:choose>
        <c:when test="${empty additionalPlanograms}">
            <div class="h3 text-center mt-50"><b><spring:theme code="text.account.planograms.no.documents" /></b></div>
        </c:when>
        <c:otherwise>
            <div class="planogram-overview-table additionalPlanograms">
                <table class="responsive-table">
                    <tr class="responsive-table-head hidden-xs">
                        <th><b>${documentName}</b></th>
                        <th><b>${uploaded}</b></th>
                        <th><b>${uploadedBy}</b></th>
                        <th class="text-right pr-0"><c:if test="${isBDECUSTOMERGROUP}"><button class="textButton" onclick="ACC.planogram.removeAll()"><b><spring:theme code="text.account.planograms.file.remove.all" /></b></c:if></button></th>
                    </tr>
                    <c:forEach items="${additionalPlanograms}" var="planogram" varStatus="loop">
                        <c:set value="${loop.index}" var="itemIndex" />
                        <spring:url value="/storefront/sga/en/AUD/my-account/planograms/view" var="previewUrl" htmlEscape="false">
                            <spring:param name="planogramCode" value="${planogram.code}"/>
                        </spring:url>

                        <tr class="responsive-table-item ${planogram.code}">
                            <td class="responsive-table-cell">
                                <span class="info-label visible-xs"><b>${documentName}</b></span>
                                <a href="javascript: void(0)" class="file-link" onclick="ACC.planogram.view(this, '/storefront/sga/en/AUD/my-account/planograms/view?planogramCode=${planogram.code}', 'pdfcontent')">${planogram.documentName}</a>
                            </td>
                            <td class="responsive-table-cell manage-profile-padding">
                                <span class="info-label visible-xs"><b>${uploaded}</b></span>
                                <fmt:formatDate value="${planogram.uploadedDate}" pattern="MMM dd, yyyy hh:mm a" />
                            </td>
                            <td class="responsive-table-cell manage-profile-padding">
                                <span class="info-label visible-xs"><b>${uploadedBy}</b></span>
                                ${planogram.uploadedBy}
                            </td>
                            <td class="responsive-table-cell manage-profile-padding text-right">
                                <span class="info-label visible-xs"></span>
                                <button class="textButton view disable-spinner text-capitalize" data-index="${itemIndex}" onclick="ACC.planogram.view(this, '/storefront/sga/en/AUD/my-account/planograms/view?planogramCode=${planogram.code}', 'pdfcontent')"><spring:theme code="text.account.planograms.file.view" /></button>
                                <c:if test="${isBDECUSTOMERGROUP}">
                                <span class="px-5 px-xs-10">|</span>
                                <button class="textButton" data-code="${planogram.code}" onClick="ACC.planogram.remove(this)" class="textButton"><spring:theme code="text.account.planograms.file.remove" /></button>
                                </c:if>
                            </td>
                        </tr>
                    </c:forEach>
                </table>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- File Preview -->
<div class="row">
    <div class="col-xs-12">
        <div id="pdfcontentframe" class="discrepancy-order-form mb-0 mt-10" style="display: none;">
            <iframe src="" id="pdfcontent" style="width: 80%; height: 95%; border: none;"></iframe>
        </div>
    </div>
</div>

<c:if test="${isBDECUSTOMERGROUP}">
    <div class="row mt-15 mb-35">
        <div class="col-xs-12">
            <div class="h5"><b><spring:theme code="text.account.planograms.popup.title" /></b></div>
            <div class="form-group" id="browse_btn-contactus">
                  <spring:theme code="text.account.planograms.fileUpload" />
                  <format:bytes bytes="${pdfFileMaxSize}" />&nbsp;
                  <spring:theme code="text.account.planograms.fileUpload.size.format" />
                <span class="skip"></span>
            </div>
            <div class="file-upload__container float-left mt-10">
                <label
                    for="file"
                    class="js-file-upload__input btn btn-primary btn-upload btn-block text-uppercase"
                    <c:if test="${fn:length(additionalPlanograms) >= 5}">disabled="disabled"</c:if>
                    ><spring:theme code="text.account.planograms.upload.browse" /></label>
                <input type="file"
                    id="file"
                    name="file"
                    class="js-file-upload__input noSpinnerCls hidden"
                    onchange="ACC.planogram.openModal(this)"
                    accept="application/pdf,image/jpg,image/jpeg,image/png"
                    data-file-max-size="${pdfFileMaxSize}"
                    data-modal-target="uploader-template" />
                <div class="uploader-error alert alert-danger" style="display: none;">
                    <span id="import-csv-file-max-size-exceeded-error-message"><spring:theme code="import.csv.savedCart.fileMaxSizeExceeded"/></span>
                </div>
            </div>
        </div>
    </div>
</c:if>

<!-------------------- Original Planograms -------------------------------------->
<div class="mt-30 mb-35">
    <div class="checkout_subheading row-margin-fix">
        <spring:theme code="text.account.planograms.customer.segment.section.title" />
    </div>

    <p class="mt-35 mb-25"><spring:theme code="text.account.planograms.customer.segment.section.text1" /></p>

    <c:choose>
        <c:when test="${empty defaultPlanogram}">
            <div class="h3 text-center mt-50"><b><spring:theme code="text.account.planograms.no.documents" /></b></div>
        </c:when>
        <c:otherwise>
            <div class="planogram-overview-table defaultPlanograms">
                <table class="responsive-table">
                    <tr class="responsive-table-head hidden-xs">
                        <th><b>${documentName}</b></th>
                        <th><b>${uploaded}</b></th>
                        <th></th>
                    </tr>
                    <c:forEach items="${defaultPlanogram}" var="defaultPlanogram" varStatus="loop">
                        <c:set value="${loop.index}" var="itemIndex" />
                        <tr class="responsive-table-item">
                            <td class="responsive-table-cell">
                                <span class="info-label visible-xs"><b>${documentName}</b></span>
                                <a href="javascript: void(0)" class="file-link" onclick="ACC.planogram.view(this, '/storefront/sga/en/AUD/my-account/planograms/view?planogramCode=${defaultPlanogram.code}', 'defaultPlanograms')">${defaultPlanogram.documentName }</a>
                            </td>
                            <td class="responsive-table-cell manage-profile-padding">
                                <span class="info-label visible-xs"><b>${uploaded}</b></span>
                                <fmt:formatDate value="${defaultPlanogram.uploadedDate}" pattern="MMM dd, yyyy hh:mm a" />
                            </td>
                            <td class="responsive-table-cell manage-profile-padding text-right">
                                <span class="info-label visible-xs"></span>
                                <button
                                    class="textButton view disable-spinner text-capitalize"
                                     data-index="${itemIndex}"
                                    onclick="ACC.planogram.view(this, '/storefront/sga/en/AUD/my-account/planograms/view?planogramCode=${defaultPlanogram.code}', 'defaultPlanograms')"><spring:theme code="text.account.planograms.file.view" /></button>
                            </td>
                        </tr>
                    </c:forEach>
                </table>

            </div>
        </c:otherwise>
    </c:choose>
</div>

<div class="row mt-35">
    <div class="col-xs-12">
        <div id="defaultPlanogramsframe" class="discrepancy-order-form" style="display: none;">
            <iframe src="" id="defaultPlanograms" style="width: 80%; height: 95%; border: none;"></iframe>
        </div>
    </div>
</div>

<!-- Modal -->
<div id="uploader-template" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="uploaderTemplateLabel"  aria-hidden="false" data-backdrop="false">
    <div class="modal-dialog recommendation-popup-container">
        <div class="modal-content">
            <div class="modal-body uploader">
                <div class="alert-danger required hidden"><spring:theme code="form.global.error" /></div>
                <div class="alert-danger failed hidden"></div> <!-- If template exists error message -->
                <h2 class="h3"><strong><spring:theme code="text.account.planograms.popup.title" /></strong></h2>
                <p class="mt-25">Please provide a document name for this planogram.</p>
                <div class="form-group mt-35">
                    <form:form  method="POST" enctype="multipart/form-data" action="${uploaderUrl}">
                        <label for="documentName" class="text-capitalize"><spring:theme code="text.account.planograms.popup.file.input.label" /></label>
                        <input class="text-input" type="text" name="documentName" onfocus="ACC.planogram.onFocus()" />

                        <div class="font-normal alert-danger required mt-10 hidden"><spring:theme code="sga.product.details.page.popup.field.required" /></div>
                        <div class="row">
                            <div class="col-xs-7 mt-25">
                                <button type="submit" class="btn btn-vd-primary text-uppercase" onclick="ACC.planogram.upload(this)"><spring:theme code="text.account.planograms.upload" /></button>
                            </div>
                        </div>
                    </form:form>
                </div>
            </div>
            <div class="recommendation-popup-close cursor" data-dismiss="modal">
                <img src="${closeIconUrl}">
            </div>
        </div>
    </div>
</div>

