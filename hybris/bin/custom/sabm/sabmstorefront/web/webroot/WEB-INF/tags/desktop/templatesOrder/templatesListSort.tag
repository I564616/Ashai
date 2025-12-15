<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="templatesOrder" tagdir="/WEB-INF/tags/desktop/templatesOrder"%>
<c:set var="templateNo" value="${fn:length(orderTemplates)}" />
<c:set var="plural" value="s" />
<spring:theme code="text.account.orderTemplates.template" var="template1"/>
<c:if test="${templateNo > 1 }">
	<c:set var="template1" value="${template1}${plural}" />
</c:if>


<div class="row margin-top-30">
    <div class="col-xs-12 col-sm-5 col-md-8">
        <h3 class="margin-top-10">You have ${templateNo}&nbsp;${template1}</h3>
    </div>
</div>

<div class="row margin-top-30">
  <div class="col-xs-12 col-sm-5 col-md-8">
    <div class="magnific-template-order offset-bottom-small">
        <a href="#create-new-template" class="btn btn-primary btn-simple bde-view-only"><spring:theme code="basket.create.template" /></a>
    </div>
  </div>
 	<div class="col-xs-4 col-sm-3 col-md-1">
 		<span class="static-label"><spring:theme code="text.orderTemplate.sort.by" /></span>
 	</div>

  <div class="col-xs-8 col-sm-4 col-md-3 sort-wrap trim-left-lg">
        <div class="select-list" >
          <div data-value="" class="select-btn"></div>
              <ul class="select-items">
                <li class="columnSort" data-sort-asc=""><spring:theme code="text.orderTemplate.sort.relevance" /></li>
                <li class="columnSort" data-sort-asc="true"><spring:theme code="text.orderTemplate.sort.name.ascending" /></li>
                <li class="columnSort" data-sort-asc="false"><spring:theme code="text.orderTemplate.sort.name.descending" /></li>
             </ul>
           </div>
        </div>
 </div>
<c:set value="Ordertemplates" var="pageName"/>
    <templatesOrder:templateOrderPopup pageName="${pageName}"/>
    
       	