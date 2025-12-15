<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>
	
<div class="col-xs-12 col-sm-12 col-md-10 col-lg-10">
	<div class="row">
		<div class="col-sm-12 hidden-md hidden-lg">
			<div class=" col-sm-offset-7 col-sm-5 hidden-md hidden-lg no-padding">
				<button id="refine-button-payment" type="submit" class="btn btn-default refine-button refine-button-sips hidden-md hidden-lg js-show-facets">			
					<spring:theme code="payment.history.number.refine.btn" /> <span id="filterApplied" style="display: none;">&nbsp;| <span class="number-of-payment-filters-js hidden-md hidden-lg">0</span> Applied</span>
				</button>
			</div>
		</div>
	</div>

	<c:if test="${fn:length(paymentDetails) > 0}">
		<div class="statements-count-history">
			<spring:theme code="payment.history.number.of.statements" arguments="1,${currentPageRecordCount}, ${totalRecordCount}" />
		</div>
	</c:if>
	<div class="account-section-content">
		<div class="account-paymenthistory">
			<div class="responsive-table">
				<div class="hidden-xs">
					<div class="responsive-table-head">
						<div class="paymenthistory-columns header">
							<div>
								<spring:theme code="text.payment.history.table.heading.date" />
							</div>
							<div>
								<spring:theme code="text.payment.history.table.heading.refno" />
							</div>
							<div>
								<spring:theme code="text.payment.history.table.heading.paymenttype" />
							</div>
							<div class="pricing-text">
								<spring:theme code="text.payment.history.table.heading.amount" />
							</div>
							<div></div>
						</div>
					</div>
				</div>
				<c:if test="${fn:length(paymentDetails) > 0}">
					<c:forEach items="${paymentDetails}" var="payment" varStatus="paymentObjRow">


						<div class="responsive-table-item js-payment-row${paymentObjRow.index}">
							<div class="paymenthistory-columns">
								<div class="hidden-sm hidden-md hidden-lg">
									<spring:theme code="text.payment.history.table.heading.date.mobile" />
								</div>
								<div id="transactionDate" class="responsive-table-cell">${payment.transactionDate}
								</div>
								<div class="hidden-sm hidden-md hidden-lg">
									<spring:theme code="text.payment.history.table.heading.refno.mobile" />
								</div>
								<div id="paymentReference" class="responsive-table-cell">${payment.paymentReference}</div>
								<div class="hidden-sm hidden-md hidden-lg">
									<spring:theme code="text.payment.history.table.heading.paymenttype.mobile" />
								</div>
								<div id="paymentType" class="responsive-table-cell">${payment.paymentType}
								</div>
								<div class="hidden-sm hidden-md hidden-lg">
									<spring:theme code="text.payment.history.table.heading.amount.mobile" />
								</div>
								<div id="paymentAmount" class="responsive-table-cell pricing-text">${payment.amount}
								</div>
								<div class="hidden-sm hidden-md hidden-lg">
								</div>
								<div class="responsive-table-cell lastCol">
									<c:if test="${not empty payment.invoice}">
										<a class="site-anchor-link view-more-link" href="#" id="${paymentObjRow.index + 1}">
											<spring:theme code="text.payment.history.table.view.more" />
										</a>
									</c:if>
								</div>
							</div>
						</div>
						<c:if test="${not empty payment.invoice}">
							<div id="viewMoreTableHeader${paymentObjRow.index + 1}" class="responsive-table-item responsive-table-item-vm" hidden="hidden">
								<div class="paymenthistory-columns-viewmore">
									<div class="payment-detail-title">
										<spring:theme code="text.payment.history.view.more.table.payment.details" />
									</div>
									<div class="payment-detail-reference">
										<c:if test="${not empty payment.receiptNumber}">
											<spring:theme code="text.payment.history.view.more.table.payment.ref" />&nbsp;${payment.receiptNumber}
										</c:if>
									</div>
								</div>
							</div>

							<div id="viewMoreTable${paymentObjRow.index + 1}" class="responsive-table-item responsive-table-item-vm view-more-border-bottom" hidden="hidden">
								<div class="paymenthistory-columns-viewmore">
									<table class="view-more-table">
										<thead class="hidden-xs">
											<tr>
												<td class="payment-detail-heading col-md-2">
													<spring:theme code="text.payment.history.view.more.table.doc.no" />
												</td>
												<td class="payment-detail-heading col-md-2">
													<spring:theme code="text.payment.history.view.more.table.doc.type" />
												</td>
												<td class="payment-detail-heading lastCol col-md-2">
													<spring:theme code="text.payment.history.view.more.table.doc.amount.paid" />
												</td>
												<td class="payment-detail-heading"></td>
											</tr>
										</thead>
										<tbody id="viewMoreTableBody${paymentObjRow.index + 1}">
											<c:forEach items="${payment.invoice}" var="invoice" varStatus="status">
												<tr class="mobile-alignment js-invoices-row${status.index}">
													<td class="payment-detail-heading hidden-sm hidden-md hidden-lg">
														<spring:theme code="text.payment.history.view.more.table.doc.no" />
													</td>
													<td id="documentNumber" class="payment-detail-item">${invoice.documentNumber}</td>
													<td class="payment-detail-heading hidden-sm hidden-md hidden-lg">
														<spring:theme code="text.payment.history.view.more.table.doc.type" />
													</td>
													<td id="documentType" class="payment-detail-item">${invoice.documentType}</td>
													<td class="payment-detail-heading lastCol hidden-sm hidden-md hidden-lg">
														<spring:theme code="text.payment.history.view.more.table.doc.amount.paid" />
													</td>
													<td id="paidAmount" class="payment-detail-item lastCol">${invoice.paidAmount}</td>
												</tr>
											</c:forEach>
										</tbody>
									</table>
								</div>
							</div>
						</c:if>
					</c:forEach>
				</c:if>
			</div>
		</div>
	</div>


	<div id="no-payment-invoices" <c:if test="${fn:length(paymentDetails) > 0}">style="display: none;"</c:if>>
		<spring:theme code="text.payment.history.no.statements.available" />
	</div>

	<c:set var="numberOfPages" value="${totalPages}" />
	<input type="hidden" id="numberOfPages" name="numberOfPages" value="${totalPages}" />
	<c:url value="/paymentHistory/fetchPaymentRecords" var="fetchMoreRecordsURL" />

	<div class="account-paymenthistory-pagination">
		<div class="pagination-toolbar">
			<div class="helper clearfix hidden-md hidden-lg"></div>
			<div class="sort-refine-bar">
				<div class="row">
					<div class="col-xs-12 col-sm-12 col-md-5 prices-text">
					</div>
					<div class="col-xs-12 col-sm-6 col-md-7 ">
					</div>
					<div class="col-xs-12 col-sm-12 col-md-12 pagination-wrap">
						<c:if test="${(numberOfPages > 1)}">
							<ul class="pagination">
								<li class="pagination-prev disabled"><a class="pagination-prev-js"><span class="glyphicon glyphicon-chevron-left"></span></a></li>
								<c:if test="${(numberOfPages > 5)}">
									<c:set var="numberOfPages" value="5" />
								</c:if>

								<c:forEach var="i" begin="1" end="${numberOfPages}">
									<li <c:if test="${i eq 1}"> class="active" </c:if>>
						<a class="page-number page-number-js${i}" id="${i}" <c:if test="${i ne 1}"> href=""</c:if>>${i}</a>
						</li>
						</c:forEach>

						<li class="pagination-next"><a class="pagination-next-js"><span class="glyphicon glyphicon-chevron-right"></span></a></li>
						</ul>
						</c:if>
					</div>
				</div>
			</div>
		</div>
	</div>
</div>