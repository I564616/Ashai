<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="storepickup" tagdir="/WEB-INF/tags/responsive/storepickup" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>

<c:url var="pickupInStoreUrl" value="/store-pickup/${searchPageData.product.code}/pointOfServices"/>

<storepickup:pickupStoreResults searchPageData="${searchPageData}" cartPage="${cartPage}" entryNumber="${entryNumber}"/>


