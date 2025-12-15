<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="label" uri="/WEB-INF/tld/message.tld" %>
<%@ taglib prefix="b2b-order" tagdir="/WEB-INF/tags/addons/apbb2baccaddon/responsive/order" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="account" tagdir="/WEB-INF/tags/responsive/account"%>

<c:set var="contextPath" value="${request.contextPath}/invoice/submitInvoice" scope="session" />

<div class="col-md-12 statements-section">
	<c:choose>
		<c:when test="${statements ne null}">
			<div id="productTabs" class="invoices-and-credits-open-closed">
				<div class="tabs js-tabs tabs-responsive">
					<ul class="clearfix tabs-list three-tabs tabamount2">
						<c:forEach items="${statements.months}" var="entry">
							<c:if test="${entry.key eq 'current' }">
								<li><a class="js-statements-current" href="">Current FY</a></li>
							</c:if>
							<c:if test="${entry.key eq 'last'}">
								<li><a class="js-statements-last" href="">FY &nbsp; ${statements.lastYear}</a></li>
							</c:if>
							<c:if test="${entry.key eq 'previous' }">
								<li><a class="js-statements-previous" href="">FY &nbsp; ${statements.previousYear}</a></li>
							</c:if>
						</c:forEach>
						
					</ul>
				</div>
			</div>
			<div class="statements-count">
			</div>
			<c:forEach items="${statements.months}" var="entry">
				<c:if test="${entry.key eq 'current'}">
					<div id="js-statements-current">
						<c:forEach var="mapValue" items="${entry.value}">
                            <c:set var="value" value="${mapValue.value}"/>
							<c:if test="${value.link eq true}">
								<div class="statements-displayed statements-available">
									<a class="site-anchor-link" href="${contextPath}/statement/download?statementMonth=${value.code}&statementYear=${value.queryYear}&FY=current">${value.name}&nbsp;${value.displayYear}</a>
								</div>
							</c:if>
						</c:forEach>
						<c:if test="${empty entry.value}">
							<div class="statements-displayed">
								<spring:theme code="invoice.statements.no.statements.available" />
							</div>
						</c:if>
					</div>	
				</c:if>
				<c:if test="${entry.key eq 'last'}">
					<div id="js-statements-last" class="hidden">
                        <c:forEach var="mapValue" items="${entry.value}">
                        <c:set var="value" value="${mapValue.value}"/>
                        <c:if test="${value.link eq true}">
						<div class="statements-displayed statements-available">
							<a  class="site-anchor-link" href="${contextPath}/statement/download?statementMonth=${value.code}&statementYear=${value.queryYear}&FY=last">${value.name}&nbsp;${value.displayYear}</a>
						</div>
						</c:if>
					</c:forEach>
					<c:if test="${empty entry.value}">
							<div class="statements-displayed">
								<spring:theme code="invoice.statements.no.statements.available" />
							</div>
					</c:if>
					</div>
				</c:if>
				<c:if test="${entry.key eq 'previous'}">
					<div id="js-statements-previous" class="hidden" >
                            <c:forEach var="mapValue" items="${entry.value}">
                            <c:set var="value" value="${mapValue.value}"/>
                            <c:if test="${value.link eq true}">
							<div class="statements-displayed statements-available">
								<a class="site-anchor-link" href="${contextPath}/statement/download?statementMonth=${value.code}&statementYear=${value.queryYear}&FY=previous">${value.name}&nbsp;${value.displayYear}</a>
							</div>
							</c:if>
						</c:forEach>
						<c:if test="${empty entry.value}">
							<div class="statements-displayed">
								<spring:theme code="invoice.statements.no.statements.available" />
							</div>
						</c:if>
					</div>
				</c:if>
			</c:forEach>
		</c:when>
		<c:otherwise>
			<div class="statements-displayed">
				<spring:theme code="invoice.statements.no.statements.available" />
			</div>
		</c:otherwise>
	</c:choose>
</div>