<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>


<div class="col-md-2 hidden-sm hidden-xs">
	<div class="hidden-sm hidden-xs product__facet invoices-payments-facet js-product-facet">
		<div class="">
			<input type="hidden" id="numberOfAppliedHistoryFilters" name="numberOfAppliedFilters" value="0" />
			<div id="applied-filters" class="applied-history-filters-js facet" style="display: none;">
				<div id="applied-filters-mobile-js" class="facet__name">
					<span class="glyphicon facet__arrow"></span>
					<span class="glyphicon facet__arrow"></span>Applied Filters
					<span class="hidden-xs hidden-sm facet__clear">
						 <a class="payment-clear-all-js site-anchor-link" href=""><span>Clear All</span></a>
					</span>
				</div>
				<div class="facet__values facet-applied-js">
					<ul class="facet__list facet-list-js">
						<li class="hidden-md hidden-lg clear-text">
							<a class="payment-clear-all-js" href=""><span>Clear All Filters</span></a>
						</li>
					</ul>
				</div>
			</div>
		</div>
		<div id="dates-js" class="facet">
			<div id="dates-mobile-js" class="facet__name">
				<span class="glyphicon facet__arrow"></span>
				<spring:theme code="sam.payment.history.date.range" />
			</div>
			<div class="form-group dates-refine-mobile-js">
				<c:url value="/paymentHistory/fetchPaymentRecords" var="fetchMoreRecordsURL" />
					<div>
						<span class="date-range-label"><spring:theme code="sam.payment.history.from.text" /></span>
						<span class="date-range-input"><input name="from" id="fromDate" class="form-control js-from-date" type="date"/></span>
					</div>
					<div>
						<span class="date-range-label"><spring:theme code="sam.payment.history.to.text" /></span>
						<span class="date-range-input"><input name="from" id="toDate" class="form-control js-to-date" type="date"/></span>
					</div>
						
					<button id="1" type="submit" class="btn btn-primary btn-block keyword-search-go js-date-filter-go-btn" disabled="disabled">
						<spring:theme code="sam.payment.history.go.text" />
					</button>
			</div>
		</div>
		
		<form:form action="${fetchMoreRecordsURL}" modelAttribute="paymentHistoryForm" method="post" class="payment-history-form">
		<div id="keywords-js" class="facet">
			<div id="keywords-mobile-js" class="facet__name">
				<span class="glyphicon facet__arrow"></span>
				<spring:theme code="sam.payment.history.keywords.text" />
			</div>
			<div class="keyword-search keywords-refine-mobile-js">
				<div class="form-group">
						<form:input name="keywords" id="keyword" path="keyword" class="form-control js-keyword" type="text"/>
						<button type="submit" id="1" class="btn btn-primary btn-block keyword-search-go js-keyword-filter-go" disabled="disabled">
							<spring:theme code="sam.payment.history.go.text" />
						</button>
						<input type="hidden" id="fromDateVal" name="fromDate" value="" />
						<input type="hidden" id="toDateVal" name="toDate" value="" />
						<input type="hidden" id="pageNo" name="pageNo" value="0" />
						<form:input type="hidden" id="paymentHistoryPageSize" path="pageSize"/>
				</div>
			</div>
		</div>
		</form:form>
	</div>
</div>
