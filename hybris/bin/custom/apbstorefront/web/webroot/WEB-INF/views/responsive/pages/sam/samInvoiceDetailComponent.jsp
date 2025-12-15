<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt"%>
<c:set var="submitSamInvoicesUrl" value="${request.contextPath}/invoice/submitInvoice" scope="session" />
<c:set var="fetchInvoicesURL" value="${request.contextPath}/invoice/fetchInvoiceRecords" scope="session" />
<spring:eval expression="T(de.hybris.platform.core.Registry).applicationContext.getBean('asahiConfigurationService')" var="asahiConfigurationService" scope="session"/>

<div id="debitInvRefPopup" class="modal fade" role="dialog" data-backdrop="false" >
    <input type="hidden" id="disablePopupForUserUrl" value="${request.contextPath}/invoice/disableDebitInvRefPopup"/>
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-body">
                <div class="reorder-popup-heading">
                    <spring:theme code="sip.page.popup.inv.ref.headline"/>
                </div>
                <div class="reorder-popup-body">
                    <input id="closeDebitInvRefPopup" type="checkbox" <c:if test='${disableDebitInvRefPopup eq true}'>checked = "checked"</c:if> /><label for="closeDebitInvRefPopup" style="float: right; width: 93%; text-transform: none !important;"><spring:theme code="sip.page.popup.inv.ref.msg"/></label></div>
                <div style="text-align: center;">
                    <button type="button" class="btn btn-primary closeDebitInvoicePopupBtn noSpinnerCls" data-dismiss="modal" style="height: 100% !important; width: 20%;">Ok</button>
                </div>
            </div>
        </div>
    </div>
</div>

<c:choose>
	<c:when test="${accessType eq 'ORDER_ONLY' || requestDenied eq true}">
		<div class="col-md-12">
			<spring:theme code="sga.homepage.order.only.payoff.message"/>
			<br><br>
			<a href="" action="/validateForPayerAccess?code=" accessRequestType="PAY_ONLY" method="GET" class="site-anchor-link request-access-js"><spring:theme code="sga.homepage.click.here.link"/></a><spring:theme code="sga.homepage.order.only.payoff.link" arguments="${approvalEmailId}"/>
		</div>
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${pendingApproval eq true }">
				<div class="col-md-12">
					<spring:theme code="sga.homepage.order.only.payoff.message"/><br><br>
					<spring:theme code="sga.homepage.pay.access.pending.message" arguments="${approvalEmailId}" />
				</div>
			</c:when>
			<c:otherwise>
				<input type="hidden" id="numberOfInvoicePageSize" name="" value="${invoiceDetails.invoicePageSize}" />
				<input type="hidden" id="currentNumberOfInvoicesShown" name="" value="${invoiceDetails.invoicePageSize}" />
				<div class="col-md-2 js-product-facet">
					<div class="hidden-sm hidden-xs invoices-payments-facet product__facet">
						<input type="hidden" id="numberOfAppliedFilters" name="numberOfAppliedFilters" value="0" />
						
						<form action="${fetchInvoicesURL}" method="post" class="fetch-more-invoices-form">	
							<input type="hidden" id="status" name="status" value="open" />
							<input type="hidden" id="dueStatus" name="dueStatus" value="" />
							<input type="hidden" id="documentType" name="documentType" value="" />
							<input type="hidden" id="keyword" name="keyword" value="" />
							<input type="hidden" id="page" name="page" value="0" />
							
						</form>
						<!-- applied filters -->
				
						<div id="applied-filters" class="facet applied-filters-js" style="display: none;">
							<div class="facet__name">
								<span class="glyphicon facet__arrow"></span>Applied Filters
								<span class="hidden-xs hidden-sm facet__clear">
				                     <a class="clear-all-js site-anchor-link" href=""><span>Clear All</span></a>
								</span>
							</div>
							<div class="facet__values facet-applied-js">
								<ul class="facet__list facet-list-js">
									<li class="hidden-md hidden-lg clear-text">
										<a class="clear-all-js" href=""><span>Clear All Filters</span></a>
									</li>
								</ul>
							</div>
						</div>
				
						<!-- filter 1 -->
				
						<div id="documentType-js" class="facet" <c:if test="${invoiceDetails.creditCount eq 0 && invoiceDetails.invoiceCount eq 0}"> style="display: none;" </c:if>>
							<div class="facet__name">
								<span class="glyphicon facet__arrow"></span> 
								Document Type
							</div>
							<div class="facet__values facet-docval-js">
								<ul class="facet__list">
									<li id="creditCount-js" <c:if test="${invoiceDetails.creditCount eq 0}"> style="display: none;" </c:if>>
										<a href="" class="credit-filter-js">Credit (<span id="creditCount">${invoiceDetails.creditCount}</span>)</a>
									</li>
									<li id="invoiceCount-js" <c:if test="${invoiceDetails.invoiceCount eq 0}"> style="display: none;" </c:if>>
										<a href="" class="invoice-filter-js">Invoice (<span id="invoiceCount">${invoiceDetails.invoiceCount}</span>)</a>
									</li>
								</ul>
							</div>
						</div> 
				
						<!-- filter 2 -->
				
						<div id="dueStatus-js" class="facet" <c:if test="${invoiceDetails.creditCount eq 0 && invoiceDetails.invoiceCount eq 0}"> style="display: none;" </c:if>>
							<div class="facet__name">
								<span class="glyphicon facet__arrow"></span> 
								Due Date
							</div>
							<div class="facet__values facet-dueval-js">
								<ul class="facet__list">
									<li id="dueNow-js" <c:if test="${invoiceDetails.dueNowCount eq 0}"> style="display: none;" </c:if>>
										<a href="" class="due-now-js">Due Now (<span id="dueNow">${invoiceDetails.dueNowCount}</span>)</a>
									</li>
									<li id="notYetDue-js" <c:if test="${invoiceDetails.notYetDueCount eq 0}"> style="display: none;" </c:if>>
										<a href="" class="not-yet-due-filter-js">Not Yet Due (<span id="notYetDue">${invoiceDetails.notYetDueCount}</span>)</a>
									</li>
								</ul>
							</div>
						</div>
				
						<!-- filter 3 -->
				
						<div id="keyword-js" class="facet">
							<div class="facet__name">
								Keywords
							</div>
							<div class="keyword-search">
								<div class="form-group">
									<input name="keywords" id="keywords" class="form-control js-keyword" type="text">
									<button class="btn btn-primary btn-block keyword-search-go js-keyword-filter-go" disabled="disabled">Go</button>
								</div>
							</div>
						</div>
					</div>
				</div>
				<div class="col-md-10">
					<div class="col-md-12 col-lg-12 paddingForPDP paddingForSAM">
						<div id="productTabs" class="invoices-and-credits-open-closed">
							<div class="tabs js-tabs tabs-responsive">
								<div class="row">
									<div class="col-sm-6 col-md-12 col-lg-12">
										<ul class="clearfix tabs-list tabamount2">
											<li id="accessibletabsnavigation0-0" class="first active"><a href="#accessibletabscontent0-0" class="open-items-js"><span class="current-info">current tab: Both</span>Open Items (<span id="openCountTab">${invoiceDetails.openCount}</span>)</a></li>
											<li id="accessibletabsnavigation0-1" class="last"><a href="#accessibletabscontent0-1" class="closed-items-js">Closed Items (<span id="closedCountTab">${invoiceDetails.closedCount}</span>)</a></li>
										</ul>
									</div>
									<div class="col-sm-6 hidden-md hidden-lg"> 
										<div class="col-sm-2 hidden-md hidden-lg">
										</div>
										<div class="col-sm-10 hidden-md hidden-lg no-padding">
											<button id="" type="submit" class="btn btn-default refine-button refine-button-sips hidden-md hidden-lg js-show-facets noSpinnerCls">			
												Refine <span id="filterApplied" style="display: none;">| <span class="number-of-filters-js hidden-md hidden-lg">0</span> Applied</span>
											</button>
										</div>
									</div>
								</div>
								<div class="content">
									<div class="tabhead first active" id="accessibletabscontent0-0" tabindex="-1">
										<a class="open-items-tab-js" href="">Open Items (${invoiceDetails.openCount})</a> <span class="glyphicon"></span>
									</div>
									<div class="tabbody open-tabbody" style="display: block;">
										
											<input type="hidden" id="numberOfInvoices" name="numberOfInvoices" value="0" />
											<form:form action="${submitSamInvoicesUrl}" id="submitSamIvoices" modelAttribute="asahiSamPaymentForm" class="" method="post">
												<c:if test="${null ne invoiceDetails && fn:length(invoiceDetails.invoices) gt 0}">
													<div class="paySelectedSectionJS cart-actions--print checkoutcartsummary quickordersummary">
														<div class="cart__actions border cart-bottom-btn">
															<div class="row no-margin">
																<div class="col-xs-12 col-sm-5 col-md-4 col-md-offset-8 col-sm-offset-7">
																	<button type="submit" class="paySelctedJs btn btn-primary btn-block credit-block-disable-js" disabled="disabled">			
																			Pay Selected
																	</button>
																	<input type="hidden" id="totalPayableAmount" name="initialTotalAmount" value="" />
																	<input type="hidden" id="totalInvoiceCount" name="totalInvoiceCount" value="${fn:length(invoiceDetails.invoices)}" />
																</div>
															</div>
														</div>
													</div>
												</c:if>
												<div id="lazyloadSIPS" class="account-section-content">
													<div class="account-open-invoices">
														<div class="responsive-table">
															<table class="responsive-table">
																<thead>
																	<tr class="responsive-table-head hidden-xs">
																		<th id="header1">
																			<spring:theme code="text.invoices.credits.table.heading.docno" />
																		</th>
																		<th id="header2">
																			<spring:theme code="text.invoices.credits.table.heading.delno" />
																		</th>
																		<th id="header3">
																			<spring:theme code="text.invoices.credits.table.heading.soldto" />
																		</th>
																		<th id="header4">
																			<spring:theme code="text.invoices.credits.table.heading.duedate" />
																		</th>
																		<th id="header5">
																			<spring:theme code="text.invoices.credits.table.heading.doctype" />
																		</th>
																		<th id="header6">
																			<spring:theme code="text.invoices.credits.table.heading.remaining" />
																		</th>
																		<th id="header7" class="site-anchor-link">
																			<a class="js-select-all-invoices" href="#">
																				<spring:theme code="text.invoices.credits.table.heading.selectall" />
																			</a>
																		</th>
																	</tr>
		                                                            <tr class="responsive-table-head hidden-sm hidden-md hidden-lg">
																		<th id="header7" class="site-anchor-link">
																			<a class="js-select-all-invoices" href="#">
																				<spring:theme code="text.invoices.credits.table.heading.selectall" />
																			</a>
																		</th>
																	</tr>
																</thead>
																<tbody id="openInvoicesTableBody">
																	<c:if test="${null ne invoiceDetails && fn:length(invoiceDetails.invoices) gt 0}">
																	<c:forEach items="${invoiceDetails.invoices}" var="invoice" varStatus="status">
                                                                        <c:set var="enableLink" value="true" />
                                                                        <c:set var="enableCheckBox" value="true" />
		                                                                    <tr class="tr-invoice-ref responsive-table-item js-open-invoice${status.index}">
		                                                                        <input type="hidden" id="js-invoice-debitInvcRefNo${status.index}" value="${invoice.debitInvoiceRef}">
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.docno.mobile" />
		                                                                        </td>
                                                                                <c:forTokens items="${asahiConfigurationService.getString('invoice.no.link.prefix', 'xxx')}" delims=", " var="token">
                                                                                    <c:if test="${enableLink && fn:startsWith(invoice.documentNumber, token)}">
                                                                                        <c:set var="enableLink" value="false" />
                                                                                    </c:if>
                                                                                </c:forTokens>
                                                                                <c:forTokens items="${asahiConfigurationService.getString('invoice.no.checkbox.prefix', 'xxx')}" delims=", " var="token">
                                                                                    <c:if test="${enableCheckBox && fn:startsWith(invoice.documentNumber, token)}">
                                                                                        <c:set var="enableCheckBox" value="false" />
                                                                                    </c:if>
                                                                                </c:forTokens>
																				<c:choose>
																					<c:when test="${enableLink && (invoice.enableDownloadLink) && (invoice.documentType ne 'Payment') && (invoice.documentType ne 'PAYMENT')}">
																						<td headers="header1" id="header1" class="responsive-table-cell responsive-table-cell-bold">
																							<c:url value="/invoice/download?documentNumber=${ycommerce:encodeUrl(invoice.documentNumber)}&lineNumber=${ycommerce:encodeUrl(invoice.lineNumber)}" var="downloadURL" />
																							<a id="documentNumberJS" href="${downloadURL}">${invoice.documentNumber}</a>
																							<input type="hidden" id="documentNumber" class="js-invoice-docno${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].docNumber" value="${invoice.documentNumber}" />
																							<input type="hidden" id="lineNumber" class="js-invoice-lineno${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].lineNumber" value="${invoice.lineNumber}" />
																						</td>
																					</c:when>
																					<c:otherwise>
																						<td headers="header1" class="responsive-table-cell responsive-table-cell-bold">
																							${invoice.documentNumber}
																							<input type="hidden" id="documentNumber" class="js-invoice-docno${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].docNumber" value="${invoice.documentNumber}" />
																							<input type="hidden" id="lineNumber" class="js-invoice-lineno${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].lineNumber" value="${invoice.lineNumber}" />
																						</td>
																					</c:otherwise>
																				</c:choose>
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.delno.mobile" />
		                                                                        </td>
		                                                                        <td id="deliveryNumberJS" headers="header2" class="responsive-table-cell">
		                                                                            ${invoice.deliveryNumber}
		                                                                        </td>
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.soldto.mobile" />
		                                                                        </td>
		                                                                        <td id="soldToJS" headers="header3" class="responsive-table-cell sold-to-account-name-td">
		                                                                            ${invoice.soldToAccount}
		                                                                        </td>
																				
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.duedate.mobile" />
		                                                                        </td>
		                                                                        <td id="invoiceDueDateJS" headers="header4" class="responsive-table-cell">
		                                                                            <span id="dueDateJS">${invoice.invoiceDueDate}</span>
																					<c:choose>
																						<c:when test="${invoice.paymentMade}">
																							<div id="pendingJS"><spring:theme code="text.invoices.payment.update.pending.from.ecc" /></div>
																						</c:when>
																						<c:otherwise>
																							<c:if test="${(invoice.overdue) && (!invoice.paymentMade) && (invoice.documentType ne 'Payment') && (invoice.documentType ne 'PAYMENT') && (invoice.documentType ne 'Credit') && (invoice.documentType ne 'CREDIT')}">
																								<div id="overDueJS"><spring:theme code="text.invoices.credits.overdue" /></div>
																							</c:if>
																						</c:otherwise>
																					</c:choose>
		                                                                        </td>
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.doctype.mobile" />
		                                                                        </td>
		                                                                        <td id="docTypeJS" headers="header5" class="responsive-table-cell">
		                                                                            ${invoice.documentType}
		                                                                            <input type="hidden" id="documentType" class="js-invoice-doctype${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].documentType" value="${invoice.documentType}" />
		                                                                        </td>
		                                                                        <td class="hidden-sm hidden-md hidden-lg">
		                                                                            <spring:theme code="text.invoices.credits.table.heading.remaining.mobile" />
		                                                                        </td>
		                                                                        <td id="remainingAmountJS" headers="header6" class="responsive-table-cell invoices-rem-amount invoices-rem-amount-mobile" id="header6">
		                                                                            <c:set var="remAmount"><fmt:formatNumber type="number" groupingUsed="false" minFractionDigits="2" value ="${invoice.remainingAmount}" /></c:set>
																					<c:choose>
																						<c:when test="${invoice.documentType eq 'Payment' or invoice.documentType eq 'PAYMENT'}">
																							&ndash;${remAmount}
																						</c:when>
																						<c:otherwise>
																							${remAmount}
																						</c:otherwise>
																					</c:choose>								  
		                                                                            <input type="hidden" id="remainingAmount" class="js-invoice-amount${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].remainingAmount" value="${remAmount}" />
		                                                                            <input type="hidden" id="paidAmount" class="js-invoice-payamount${status.index} js-invoice${status.index}" name="asahiSamInvoiceForm[${status.index}].paidAmount" value="${remAmount}" />
		                                                                        </td>
		                                                                        
		                                                                        <td headers="header7" class="responsive-table-cell checkbox-mobile-fix">
																					<c:if test="${enableCheckBox && !invoice.paymentMade && (invoice.documentType ne 'Payment') && (invoice.documentType ne 'PAYMENT')}">
																						<input id="${status.index}" class="js-invoice-select js-invoice-checkbox${status.index} js-invoice${status.index}" type="checkbox" value="true">
																					</c:if>
		                                                                        </td>
		                                                                    </tr>
																	</c:forEach>
																	</c:if>
																</tbody>
															</table>
														</div>
													</div>
												</div>
												<c:if test="${null ne invoiceDetails && fn:length(invoiceDetails.invoices) gt 0}">
													<div class="paySelectedSectionJS cart-actions--print checkoutcartsummary quickordersummary">
														<div class="cart__actions border cart-bottom-btn bottom-payselected">
															<div class="row no-margin">
																<div class="col-xs-12 col-sm-5 col-md-4 col-md-offset-8 col-sm-offset-7">
																	<button type="submit" class="paySelctedJs btn btn-primary btn-block credit-block-disable-js" disabled="disabled">			
																			Pay Selected
																	</button>
																</div>
															</div>
														</div>
													</div>
												</c:if>
											</form:form>
										<c:if test="${null eq invoiceDetails || fn:length(invoiceDetails.invoices) eq 0}">
											<div class="no-invoices">
												<spring:theme code="text.invoice.no.invoices.available" />
											</div>
										</c:if>
									</div>
					
									<div class="tabhead last" id="accessibletabscontent0-1" tabindex="-1">
										<a class="closed-items-tab-js" href="">Closed Items (${invoiceDetails.closedCount})</a> <span class="glyphicon"></span>
									</div>
									<div class="tabbody closed-tabbody" style="display: block;">
										<fmt:parseNumber var="parsedClosedCount" integerOnly="true" type="number" value = "${invoiceDetails.closedCount}" />
											<div class="account-section-content">
												<div class="account-open-invoices">
													<div class="responsive-table">
														<table class="responsive-table">
															<thead>
															<tr class="responsive-table-head hidden-xs">
																<th id="header1"><spring:theme code="text.invoices.credits.table.heading.docno"/></th>
																<th id="header2"><spring:theme code="text.invoices.credits.table.heading.delno"/></th>
																<th id="header3"><spring:theme code="text.invoices.credits.table.heading.soldto"/></th>
																<th id="header4"><spring:theme code="text.invoices.credits.table.heading.invoicedate"/></th>
																<th id="header5"><spring:theme code="text.invoices.credits.table.heading.doctype"/></th>
																<th id="header6"><spring:theme code="text.invoices.credits.table.heading.docamount"/></th>
															</tr>
															</thead>
															<tbody id="closedInvoicesTableBody">
															</tbody>
														</table>
													</div>
												</div>
											</div>
										<c:if test="${null eq invoiceDetails || fn:length(invoiceDetails.invoices) eq 0}">
											<div class="no-invoices-closed">
												<spring:theme code="text.invoice.no.invoices.available" />
											</div>
										</c:if>
									</div>
									<span id="trigger_lazy_load"></span>
								</div>
							</div>
						</div>
					</div>
				</div>
			</c:otherwise>
		</c:choose>
		
		
	</c:otherwise>
</c:choose>
<script type="text/javascript">
    var invoiceIdxMap = new Map();
    var creditInvoiceMap = new Map();

    var disableLinkPrefixes = [];
    var disableCheckBoxPrefixes = [];
    //populate JS set which would be required to disable invoice download link and checkbox

    <c:forTokens items="${asahiConfigurationService.getString('invoice.no.link.prefix', 'xxx')}" delims=", " var="token">
        disableLinkPrefixes.push('${token}');
    </c:forTokens>
    <c:forTokens items="${asahiConfigurationService.getString('invoice.no.checkbox.prefix', 'xxx')}" delims=", " var="token">
        disableCheckBoxPrefixes.push('${token}');
    </c:forTokens>

</script>