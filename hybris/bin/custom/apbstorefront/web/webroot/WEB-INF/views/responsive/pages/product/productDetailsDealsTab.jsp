<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/responsive/product"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<c:if test="${fn:length(dealsData) > 0}" >
	<div class="tabhead">
		<a href="" class="deals-title">${fn:escapeXml(title)}</a> <span class="glyphicon"></span>
		<div id="deals-count">${fn:length(dealsData)}</div>
	</div>
	<div class="tabbody deals-body">
		<div class="container-lg">
			<div class="row no-margin">
				<div class="no-padding">
					<div class="tab-container">
						<div class="tab-details">
							<c:forEach items="${dealsData}" var="dealInfo">
								<div class="item mb-15">
									<p class="no-margin"><b>${dealInfo.title}</b></p>
									<p><i>Valid from <fmt:formatDate value="${dealInfo.validFrom}" pattern="dd/MM/yyyy" /> - <fmt:formatDate value="${dealInfo.validTo}" pattern="dd/MM/yyyy" /> (expires in ${dealInfo.expiryDaysRemaining} days)</i></p>
								</div>
							</c:forEach>
						</div>

						<div class="deals-bottom-text">
							<spring:theme code="sga.pdp.deal.tab.text1"/> 
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
</c:if>