<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<div class="row pdp-section">
	<div class="col-md-6">
		<table class="full-width">
			<c:if test="${not empty product.presentation}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.packconfiguration.presentation" /></td>
					<td>${product.presentation}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.unit}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.packconfiguration.uom" /></td>
					<td>${product.unit}</td>
				</tr>
			</c:if>
			<c:if test="${not empty product.uomMappingList}">
				<c:forEach items="${product.uomMappingList}" var="uomMapping" varStatus="status">
					<c:if test="${uomMapping.toUnit ne '' and uomMapping.fromUnit ne '' and uomMapping.qtyConversion>0}">
						<tr>
							<td class="half-width">${uomMapping.toUnit} <spring:theme code="text.product.detail.tab.packconfiguration.uom.per" /> ${uomMapping.fromUnit}</td>
							<%-- <td>${uomMapping.qtyConversion}</td> --%>
							<fmt:parseNumber var="umo" type="number" value="${uomMapping.qtyConversion}" />
							<td><c:out value="${umo}" /></td>
						</tr>
					</c:if>
				</c:forEach>
			</c:if>
			<c:if test="${not empty product.weight}">
				<tr>
					<td class="half-width"><spring:theme code="text.product.detail.tab.packconfiguration.weight" /></td>
					<%-- <td>${product.weight}</td> --%>
					<c:set var="mesurment" value="${product.weight}"/>
					<c:set var="space" value=" "/>
					<c:set var="weightmesurment" value="${fn:substringAfter(mesurment, space)}" />
					<fmt:parseNumber var="weight" type="number" value="${product.weight}" />
					<td><c:out value="${weight}"/><c:out value=" ${weightmesurment}"/> </td>
				</tr>
			</c:if>
			<c:if test="${not empty product.length and not empty product.width and not empty product.height}">
				<tr>
					<c:set var="lwhdimension" value=" "/>
					<td class="half-width"><spring:theme code="text.product.detail.tab.packconfiguration.size" /></td>
					<td><spring:theme code="text.product.detail.tab.packconfiguration.size.value" arguments="${product.length},${product.width},${product.height},${lwhdimension}" /></td>
				</tr>
			</c:if>
		</table>
	</div>
</div>