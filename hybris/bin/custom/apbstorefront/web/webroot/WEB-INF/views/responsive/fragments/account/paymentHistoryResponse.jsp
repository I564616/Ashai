<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
{
	"response" : {
		"totalRecordsCount":${paymentHistoryForm.totalRecords},
		"fromCount" : ${paymentHistoryForm.fromCount},
		"toCount" : ${paymentHistoryForm.toCount},
		"paymentHistory": [
			<c:forEach items="${paymentDetails}" var="payment" varStatus="paymentStatus">
			{
				"transactionDate":"${payment.transactionDate}",
				"amount":"${payment.amount}",
				"paymentType":"${payment.paymentType}",
				"paymentReference":"${payment.clearingdocumentNumber}",
				"invoice":[
					<c:forEach items="${payment.invoice}" var="invoice" varStatus="invoiceStatus">
						{
							"documentNumber":"${invoice.documentNumber}",
							"documentType":"${invoice.documentType}",
							"paidAmount":"${invoice.paidAmount}"
						}<c:if test="${not invoiceStatus.last}">,</c:if>
					</c:forEach>
				]
			}<c:if test="${not paymentStatus.last}">,</c:if>
		</c:forEach>
		]
	} 
}