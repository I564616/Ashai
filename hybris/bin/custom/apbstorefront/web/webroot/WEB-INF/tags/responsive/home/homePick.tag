<%@ taglib prefix="home" tagdir="/WEB-INF/tags/responsive/home" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ attribute name="product" required="true" type="de.hybris.platform.commercefacades.product.data.ProductData" %>
<%@ attribute name="title" required="true" type="String" %>
<%@ attribute name="smartRecommendationModel" required="true" type="String" %>
<%@ attribute name="count" required="false" type="Integer" %>
<spring:htmlEscape defaultHtmlEscape="true" />


<home:recommendationItem product="${product}" title="${title}" smartRecommendationModel="${smartRecommendationModel}"/>
