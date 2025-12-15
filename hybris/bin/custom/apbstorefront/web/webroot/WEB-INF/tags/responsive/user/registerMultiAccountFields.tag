<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="formElement"
	tagdir="/WEB-INF/tags/responsive/formElement"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ attribute name="index" required="true" type="java.lang.Integer"%>
<%@ attribute name="maxlength" required="true" type="java.lang.Integer"%>
<%@ attribute name="items" required="true" type="java.util.Collection"%>
<%@ attribute name="dataList" required="true" type="java.util.Collection"%>
<%@ attribute name="register" required="true" type="java.util.ArrayList"%>
<%@ attribute name="dropdownListOptions" required="true" type="java.util.ArrayList"%>

<div id="row-${index}" class="fields-row my-10 mt-xs-15 xs-border-line-top clear">
   <div class="fields col-xs-12 col-sm-10 pl-0 pr-xs-0">
        <div class="col-xs-12 col-sm-4 mb-xs-5 pl-0 px-xs-0 pt-xs-20">
            <formElement:formInputBox idKey="register.apb.account.id.${index}"
                labelKey="register.apb.account.id"
                labelCSS="visible-xs my-xs-5"
                path="albCompanyInfoData[${index}].abnAccountId"
                inputCSS="form-control mb-0" maxlength="${maxlength}" mandatory="true" />
        </div>

        <div class="col-xs-12 col-sm-4 mb-xs-5 px-xs-0">
            <formElement:formInputBox idKey="register.abn.${index}"
                labelKey="register.abn"
                labelCSS="visible-xs my-xs-5"
                path="albCompanyInfoData[${index}].abnNumber" inputCSS="form-control mb-0"
                mandatory="true" maxlength="${maxlength}" />
        </div>

        <div class="col-xs-12 col-sm-4 mb-xs-5 pr-0 pl-xs-0">
            <formElement:formSelectBox
                idKey="samAccess.${index}"
                labelKey="Permission"
                labelCSS="visible-xs my-xs-5"
                selectCSSClass="form-control mb-0"
                path="albCompanyInfoData[${index}].samAccess"
                mandatory="true"
                skipBlank="false"
                skipBlankMessageKey="Select permissions"
                items="${items}"
                arrayList="${dropdownListOptions}" />
        </div>
   </div>
  <div class="col-xs-12 col-sm-2 pr-0 pl-xs-0">
        <button
            id="button-${index}"
            type="button"
            class="textButton disable-spinner pull-right pull-xs-left px-0 mt-10 mt-xs-0"
            onclick="ACC.registration.removeAccount(this); return false;"
            <c:if test="${fn:length(dataList) == 1 || dataList == null}">disabled</c:if>>remove</button>
   </div>
   <div class="clear"></div>
</div>