<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%> 
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/desktop/nav" %>
<c:url value="/impersonate" var="impersonate" />
<c:url var="paSearchUrl" value="/paSearch" />
<div class="row">
   <div class="col-lg-12">
       <h1 class="h1"><spring:theme code="text.pa.search.results.title" /></h1>
       <p class="h2">
        <spring:theme code="text.pa.search.results.criteria" /><span style="padding:0px">
			<c:choose>
	         <c:when test="${requestType == 'account'}">
	         	<spring:theme code="text.pa.search.results.label.account" /> "<c:out value="${accountNumber}"/>"
	         </c:when>
	         <c:when test="${requestType == 'customer'}">
	         	<spring:theme code="text.pa.search.results.label.customer" /> "<c:out value="${customerNumber}"/>", "<c:out value="${customerName}"/>"
	         </c:when>
	         <c:when test="${requestType == 'user'}" >
	         	<spring:theme code="text.pa.search.results.label.user" /> "<c:out value="${email}"/>"
	         </c:when>
	      </c:choose>
       </p>
       
      <c:choose>
      	<c:when test="${not empty b2bUnits}">
				<div class="search-result">
		      	<div class="search-result-section offset-bottom-large">
		       	 	<!-- title section -->
		       	 	<c:forEach items="${b2bUnits}" var="b2bUnit">
		   				<div class="section-title offset-bottom-small">
		       	 			<c:choose>
		       	 				<c:when test = "${requestType == 'customer'}">
						         	<h3><spring:theme code="text.pa.search.results.label.customer" />
						         </c:when>
						         <c:otherwise>
						         	<h3><spring:theme code="text.pa.search.results.label.account" />
						         </c:otherwise>
		       	 			</c:choose>
		       	 			<span style="padding:0px">&nbsp;<c:set var="b2bUnitUid" value="${b2bUnit.uid}"/>
		       	 			<c:out value="${b2bUnitUid}"/>, <c:out value="${b2bUnit.name}"/></h3></h3>
		       	 		</div>
		       	 		<!-- list of customers section -->
		       	 		<c:forEach items="${b2bUnit.customers}" var="customer">
		       	 			<div class="section-items">
									<c:choose>
										<c:when test="${customer.active == false}">
											<div class="row search-result-item inactive">
												<div class="col-xs-12 col-md-4 trim-left-small">
													<span class="status status-inactive"></span> <strong><c:out
															value="${customer.name}" /></strong>
												</div>
												<div class="col-xs-12 col-md-4 trim-left-23-xs">
													<c:out value="${customer.uid}" />
												</div>
											</div>
										</c:when>
										<c:when test="${customer.isZadp == true}">
											<form:form action="${impersonate}" method="POST">
												<a href="javascript:;" onclick="parentNode.submit();">
													<div class="row search-result-item primary">
														<div class="col-xs-12 col-md-4 trim-left-small">
															<span class="status status-primary"></span> <strong><c:out
																	value="${customer.name}" /></strong>
														</div>
														<div class="col-xs-12 col-md-4 trim-left-23-xs">
															<c:out value="${customer.uid}" />
														</div>
													</div>
												</a>
												<input type="hidden" name="uid" value="${customer.uid}">
												<c:if test="${requestType == 'customer'}">
													<input type="hidden" name="unit" value="${b2bUnitUid}">
												</c:if>

											</form:form>
										</c:when>
										<c:otherwise>
											<form:form action="${impersonate}" method="POST">
												<a href="javascript:;" onclick="parentNode.submit();">
													<div class="row search-result-item">
														<div class="col-xs-12 col-md-4 trim-left-small">
															<strong><c:out value="${customer.name}" /></strong>
														</div>
														<div class="col-xs-12 col-md-4 trim-left-small">
															<c:out value="${customer.uid}" />
														</div>
													</div>
												</a>
												<input type="hidden" name="uid" value="${customer.uid}">
												<c:if test="${requestType == 'customer'}">
													<input type="hidden" name="unit" value="${b2bUnitUid}">
												</c:if>
											</form:form>
										</c:otherwise>
									</c:choose>
								</div>
		       	 		</c:forEach>
		       	 		<div class="row">&nbsp;</div>
		       	 		<div class="row">&nbsp;</div>
						</c:forEach>
		       	 </div>
		       </div>
	      </c:when>
       	<c:otherwise>
       		<div class="search-result">
	      	 	 <div class="search-result-section offset-bottom-large">
		       	 	<div class="section-title offset-bottom-small">
		       	 		<h3>0 results</h3>
		       	 	</div>
		       	 	<p><spring:theme code="text.pa.search.results.noResults.message" /></p>
		       	 </div>
		       </div>
			</c:otherwise>
       </c:choose>
       <div class="row">
	    	 <div class=" col-md-4">
	      	 <div class="offset-bottom-large">
		         <a href="${paSearchUrl}" class="btn btn-primary btn-large btn-flex-fixed"><spring:theme code="text.pa.search.results.back.search" /></a>
	       	</div>
	     	 </div>
  		 </div>
   </div>    
</div>
<nav:backToTop/>
  
	