<%@ page trimDirectiveWhitespaces="true" contentType="application/json" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags" %>
<%@ taglib prefix="format" tagdir="/WEB-INF/tags/shared/format" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

{"miniCartCount": ${totalItems}, "miniCartPrice": "<c:if test="${totalDisplay == 'TOTAL'}"><format:price priceData="${totalPrice}"/></c:if><c:if test="${totalDisplay == 'SUBTOTAL'}"><c:choose><c:when test="${cmsSite.uid eq 'sga' and !wasCheckoutInterfce and minicartSubTotal ne null}"><format:price priceData="${minicartSubTotal}" /></c:when><c:otherwise><format:price priceData="${subTotal}" /></c:otherwise></c:choose></c:if><c:if test="${totalDisplay == 'TOTAL_WITHOUT_DELIVERY'}"><format:price priceData="${totalNoDelivery}"/></c:if>"}
