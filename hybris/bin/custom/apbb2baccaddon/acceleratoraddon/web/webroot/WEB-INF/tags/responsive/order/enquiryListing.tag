
<%@ tag body-content="empty" trimDirectiveWhitespaces="true" %>
<%@ attribute name="searchUrl" required="true" type="String" %>
<%@ attribute name="messageKey" required="true" type="String" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="nav" tagdir="/WEB-INF/tags/responsive/nav" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>


<!-- FILE FROM ORDER LISTING APBB2BAC ADDON  -->
<spring:htmlEscape defaultHtmlEscape="true"/>
<spring:url value="/my-account/order/" var="orderDetailsUrl" htmlEscape="false"/>


	<div class="enquiry-listing-label"><spring:theme code="text.account.myEnquiries.details.text" /></div>
    <spring:theme code="text.account.myEnquiries.details.text.part.two.link" var="detailsLink"/>
	<div class="enquiry-listing-label">
        <spring:theme code="text.account.myEnquiries.details.text.part.two"/>
        <a href="${detailsLink}"><spring:theme code="text.account.myEnquiries.details.text.part.two.link.text"/></a>.
    </div>
    
    <div class="account-section-content">
        <div class="enquiry-listing">
            <div class="enquiry-listing-pagination">
                <nav:paginationwithdisplay top="true" msgKey="${messageKey}" showCurrentPageInfo="true" hideRefineButton="true"
                                supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"
                                searchPageData="${searchPageData}" searchUrl="${searchUrl}"
                                numberPagesShown="${numberPagesShown}"/>
            </div>
            
            
            <div class="responsive-table enquiry-history-table">
                <table class="responsive-table">
                    <thead>
                    <tr class="responsive-table-head hidden-xs">
                        <th id="header1"><spring:theme code="text.account.myEnquiriesListing.enquiryId"/></th>
                        <th id="header2"><spring:theme code="text.account.myEnquiriesListing.datePlaced"/></th>
                        <th id="header3"><spring:theme code="text.account.myEnquiriesListing.name"/></th>
                        <th id="header4"><spring:theme code="text.account.myEnquiriesListing.enquiryType"/></th>
                        <th id="header5"><spring:theme code="text.account.myEnquiriesListing.contact"/></th>
                    </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${searchPageData.results}" var="enquiry">
                        <tr class="responsive-table-item" title="<spring:theme code='enquiry.listing.enquiry.id'/>&nbsp;${enquiry.requestRefNumber}">
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.myEnquiriesListing.mobile.enquiryId"/></td>
                            <td headers="header1" class="responsive-table-cell">
                                ${enquiry.requestRefNumber}
                            </td>
                        
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.myEnquiriesListing.mobile.datePlaced"/></td>
                            <td headers="header2" class="responsive-table-cell">
                                <fmt:formatDate value="${enquiry.datePlaced}" pattern="MMM dd, yyyy hh:mm a" />
                            </td>
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.myEnquiriesListing.mobile.name"/></td>
                            <td headers="header3" id="order-type" class="responsive-table-cell">
                                ${enquiry.name}
                            </td>
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.myEnquiriesListing.mobile.enquiryType"/></td>
                            <td headers="header4" class="responsive-table-cell">
                                ${enquiry.enquiryType}
                                <c:if test="${enquiry.enquirySubType ne null && enquiry.enquirySubType != ''}">
                                - ${enquiry.enquirySubType}
                                </c:if>
                            </td>
                            
                            <td class="hidden-sm hidden-md hidden-lg"><spring:theme code="text.account.myEnquiriesListing.mobile.contact"/></td>
                            <td headers="header5" class="responsive-table-cell">
                                ${enquiry.contact}
                            </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
                <c:if test="${empty searchPageData.results}">
                        <tr>
                            <div class="enquiry-listing-label orderhistory-noresults-label">
                                <spring:theme code="text.account.myEnquiries.noresults.message"/>
                            </div>
                        </tr>
                </c:if>
            </div>

            <div class="enquiry-listing-pagination">
            <nav:paginationwithnumbering top="false"  supportShowPaged="${isShowPageAllowed}" supportShowAll="${isShowAllAllowed}"  searchPageData="${searchPageData}" 
	searchUrl="${searchUrl}" numberPagesShown="${numberPagesShown}"/>
			</div>

            <div class="enquiry-listing-label"><spring:theme code="text.account.myEnquiries.response.sla" /></div>
            <div class="enquiry-listing-label"><spring:theme code="text.account.myEnquiries.urgentEnquiries" /></div><br />
            <div class="row contact-us-details no-margin">
                <div class="row col-sm-12 col-md-12 customer-service">
                    <div class="col-sm-6 col-md-6 padding-fix-left-right-20">
                        <div class="customer-service-content-heading">
                            <spring:theme code="text.account.myEnquiries.acc.label"/> </div>
                        <div class="customer-service-details">
                            <span class="customer-service-content textword-wrap"><b><spring:theme code="text.account.myEnquiries.phone"/></b> <a href="tel:<spring:theme code="text.account.myEnquiries.acc.phonenumber"/>"><spring:theme code="text.account.myEnquiries.acc.phonenumber"/></a> <spring:theme code="text.account.myEnquiries.acc.hours"/></span> <br>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-6 padding-fix-left-right-20">
                        <div class="customer-service-content-heading">
                            <spring:theme code="text.account.myEnquiries.cr.label"/> </div>
                        <div class="customer-service-details">
                            <span class="customer-service-content textword-wrap"><b><spring:theme code="text.account.myEnquiries.phone"/></b> <a href="tel:<spring:theme code="text.account.myEnquiries.cr.phonenumber"/>"><spring:theme code="text.account.myEnquiries.cr.phonenumber"/></a> <spring:theme code="text.account.myEnquiries.cr.hours"/></span> <br>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-6 padding-fix-left-right-20">
                        <div class="customer-service-content-heading">
                            <spring:theme code="text.account.myEnquiries.act.label"/> </div>
                        <div class="customer-service-details">
                            <span class="customer-service-content textword-wrap"><b><spring:theme code="text.account.myEnquiries.phone"/></b> <a href="tel:<spring:theme code="text.account.myEnquiries.act.phonenumber"/>"><spring:theme code="text.account.myEnquiries.act.phonenumber"/></a> <spring:theme code="text.account.myEnquiries.act.hours"/></span> <br>
                        </div>
                    </div>
                    <div class="col-sm-6 col-md-6 padding-fix-left-right-20">
                        <div class="customer-service-content-heading">
                            <spring:theme code="text.account.myEnquiries.ets.label"/> </div>
                        <div class="customer-service-details">
                            <span class="customer-service-content textword-wrap"><b><spring:theme code="text.account.myEnquiries.phone"/></b> <a href="tel:<spring:theme code="text.account.myEnquiries.ets.phonenumber"/>"><spring:theme code="text.account.myEnquiries.ets.phonenumber"/></a> <spring:theme code="text.account.myEnquiries.ets.hours"/></span> <br>
                        </div>
                    </div>  		
                </div>
            </div>
            <div class="row contact-us-details no-margin">
            </div>
        </div>
    </div>

