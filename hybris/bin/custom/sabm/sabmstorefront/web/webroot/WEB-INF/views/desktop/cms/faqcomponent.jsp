<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>
<%@ taglib prefix="components" tagdir="/WEB-INF/tags/desktop/components" %>

<script id="faqData" type="text/json">${faqsComponent}</script>
<c:set var="frameVisible" value="${component.frameVisible}" />

<c:if test="${empty frameVisible}">
	<c:set var="frameVisible" value="true" />
</c:if>

<div class="faq-component row" data-frame-visible="${frameVisible}" ng-controller="faqCtrl" ng-init="init(true)" ng-cloak>
	<c:if test="${not empty component.title}">  
	  <div class="col-xs-12">
	    <h2 class="h1">${component.title}</h2>
	  </div>
	</c:if>
  <div class="col-xs-12 search">
    <div class="form-group">
      <div class="input-group">
        <input type="search" name="faqSearch" class="form-control" id="faq-search" placeholder="<spring:theme code='text.component.faq.search.placeholder'/>" ng-model="faq.search" ng-keyup="updatePaginationItemCount(filteredFaq.length)" ng-click="filterCategories.selected=''; category.selected=''; hideExpanded(); showAllQuestions();" ng-focus="filterCategories.selected=''; category.selected=''; hideExpanded(); ">
        <div class="input-group-addon clear-search" role="button" ng-click="faq.search=''; elem.prev().focus();" ng-keypress="faq.search='';" tabindex="0" ng-if="faq.search">
          <svg class="icon-close-white">
            <use xlink:href="#icon-close-white"></use>
          </svg>
        </div>
        <div class="input-group-addon search-icon">
          <svg class="icon-search">
            <use xlink:href="#icon-search"></use>
          </svg>
        </div>
      </div>
    </div>
  </div>
  <div class="col-xs-12 alert alert-dismissible fade in" role="alert" ng-if="faqSent" ng-class="{'alert-success': faqSentSuccess,'alert-danger': faqSentError}">
    <span ng-if="faqSentSuccess"><spring:theme code="text.component.faq.modal.send.question.message.success"/></span>
    <span ng-if="faqSentError"><spring:theme code="text.component.faq.modal.send.question.message.error"/></span>
  </div>
    <div class="col-xs-12 col-sm-3 faq-categories">
      <ul class="list-group hidden-xs">
        <li class="list-group-item" ng-repeat="category in faqData.categories" ng-if="category.questionList.length > 0">
          <a ng-click="filterCategories.selected=category.title; faq.search=''; showQuestionsByCategory($index);" ng-keypress="filterCategories.selected=category.title; faq.search=''" ng-class="{'active': category.title == filterCategories.selected}" tabindex="0">{{category.title}}</a>
        </li>
      </ul>
      <div class="dropdown visible-xs-block">
        <select class="form-control" ng-model="category.selected" ng-change="filterCategories.selected=category.selected; faq.search=''; showQuestionsByCategory(category.selected);">
          <option value="" selected disabled><spring:theme code='text.component.faq.category.placeholder'/></option>
          <option ng-repeat="category in faqData.categories" ng-if="category.questionList.length > 0" value="{{$index}}">{{category.title}}</option>
        </select>
      </div>
    </div>
    <div class="col-xs-12 col-sm-9 faq-questions">
    <!-- 
      <div class="panel-group" class="accordion" role="tablist" aria-multiselectable="false" ng-repeat="faqData in filteredFaq = (faqData.categories | filter:filterCategories.selected | filter: faq.search)" ng-if="faqData.questionList.length > 0">
        <div class="panel panel-default" ng-repeat="faq in faqData.questionList | slice: (currentPage-1)*numPerPage:currentPage*numPerPage | filter: faq.search">
          <div class="panel-heading" role="tab" id="question{{$parent.$index}}{{$index}}">
            <h3 class="panel-title">
              <a class="collapsed" role="button" data-toggle="collapse" data-parent=".accordion" href="#answer{{$parent.$index}}{{$index}}" aria-expanded="false" aria-controls="answer{{$parent.$index}}{{$index}}" ng-click="hideExpanded()">
                {{faq.text}}
              </a>
            </h3>
          </div>
          <div id="answer{{$parent.$index}}{{$index}}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="question{{$parent.$index}}{{$index}}">
            <div class="panel-body">
              <div ng-bind-html="faq.subText"></div>
            </div>
          </div>
        </div>
      </div>-->  
      
      
      <div class="panel-group" class="accordion" role="tablist" aria-multiselectable="false">
        <div class="panel panel-default" ng-repeat="faq in filteredFaq = (all | filter: faq.search) | slice: (currentPage-1)*numPerPage:currentPage*numPerPage">
          <div class="panel-heading" role="tab" id="question{{$parent.$index}}{{$index}}">
            <h3 class="panel-title">
              <a class="collapsed" role="button" data-toggle="collapse" data-parent=".accordion" href="#answer{{$parent.$index}}{{$index}}" aria-expanded="false" aria-controls="answer{{$parent.$index}}{{$index}}" ng-click="hideExpanded()">
                <span ng-bind-html="faq.text | highlight: searchText"></span>
              </a>
            </h3>
          </div>
          <div id="answer{{$parent.$index}}{{$index}}" class="panel-collapse collapse" role="tabpanel" aria-labelledby="question{{$parent.$index}}{{$index}}">
            <div class="panel-body">
              <div ng-bind-html="faq.subText"></div>
            </div>
          </div>
        </div>
      </div>

      <p class="no-results" ng-if="filteredFaq.length === 0"><spring:theme code='text.component.faq.search.noresults.text'/>&nbsp;<span class="search-query">&lsquo;{{faq.search}}&rsquo;</span></p>
      <p class="highlight">{{faqData.sendQuestionText}} <a href="#faq-send-question" class="regular-popup" tabindex="0" ng-click="faqQuestion = null">{{faqData.sendQuestionLinkButtonText}}</a></p>
      
      <div ng-show="((totalItems/numPerPage) | number: 0) > 2">
	 	 <ul uib-pagination boundary-links="true" class="pagination-sm" items-per-page="numPerPage" max-size="maxSize" total-items="totalItems" ng-model="currentPage" num-pages="numPages" previous-text="&lsaquo;" next-text="&rsaquo;" first-text="&laquo;" last-text="&raquo;" ng-change="pageChanged()" force-ellipses="true" ng-show="totalItems > numPerPage" rotate="false"></ul>
	  </div>
      <div ng-show="((totalItems/numPerPage) | number: 0) <= 2">
	 	 <ul uib-pagination class="pagination-sm" items-per-page="numPerPage" max-size="maxSize" total-items="totalItems" ng-model="currentPage" num-pages="numPages" previous-text="&lsaquo;" next-text="&rsaquo;" ng-change="pageChanged()" force-ellipses="true" ng-show="totalItems > numPerPage" rotate="false"></ul>
	  </div>	  
	<components:popupFAQask/>
	</div>
</div>
