                <%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags" %>
                <%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
                <%@ taglib prefix="template" tagdir="/WEB-INF/tags/responsive/template"%>

                <spring:url value="/checkout/multi/summary/view" var="summaryViewUrl"/>

                <template:page pageTitle="${pageTitle}">
                    <div class="login-page__headline">
                        <spring:theme code="promotion.winners.page.title" />
                    </div>
  				<div class="promotion-logo">
  					<cms:pageSlot position="Toplogo" var="feature" limit="1">
  						<cms:component component="${feature}" element="div" class="yComponentWrapper"/>
  					</cms:pageSlot>
  				</div>
                	<cms:pageSlot position="Section1" var="feature" element="div">
                		<cms:component component="${feature}" element="div" class="clearfix"/>
                	</cms:pageSlot>

                </template:page>
