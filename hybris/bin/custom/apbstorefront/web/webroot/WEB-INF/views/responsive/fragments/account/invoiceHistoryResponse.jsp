<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

{
	"response":{
		"openCount":"${invoiceDetails.openCount}",
		"closedCount":"${invoiceDetails.closedCount}",
		"dueNowCount":"${invoiceDetails.dueNowCount}",
		"notYetDueCount":"${invoiceDetails.notYetDueCount}",
		"creditCount":"${invoiceDetails.creditCount}",
		"invoiceCount":"${invoiceDetails.invoiceCount}",
		"invoicePageSize":"${invoiceDetails.invoicePageSize}",
		"payAccess" : "${payAccess}",
		"invoices": [
			<c:forEach items="${invoiceDetails.invoices}" var="invoice" varStatus="invoiceStatus">
				{
								"documentNumber":"${invoice.documentNumber}",
								"deliveryNumber":"${invoice.deliveryNumber}",
								"soldToAccount":"${invoice.soldToAccount}",
								"invoiceDueDate":"${invoice.invoiceDueDate}",
								"invoiceDate":"${invoice.invoiceDate}",
								"overdue":"${invoice.overdue}",
								"paymentMade":"${invoice.paymentMade}",
								"documentType":"${invoice.documentType}",
								"lineNumber":"${invoice.lineNumber}",
								"enableDownloadLink":"${invoice.enableDownloadLink}",
								"remainingAmount":"${invoice.remainingAmount}"
				}<c:if test="${not invoiceStatus.last}">,</c:if>
			</c:forEach>
			]
		}
}