<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="cms" uri="http://hybris.com/tld/cmstags"%>

<c:if test="${component.visible}">
	<div class="navbar navbar-default">
		<div class="container-fluid">
	
			<!-- <nav class="main-navigation js-enquire-offcanvas-navigation"
					role="navigation"> -->
			<ul class="nav navbar-nav navbar-left">
				<%--<li class="hidden-md hidden-lg">
							<a class="sm-back js-toggle-sm-navigation" href="#">Back</a>
						</li>--%>
				<c:forEach items="${components}" var="component">
					<c:if test="${component.navigationNode.visible}">
						<cms:component component="${component}" evaluateRestriction="true"
							navigationType="offcanvas" />
					</c:if>
				</c:forEach>
			</ul>
			<!-- </nav> -->
	
		</div>
	</div>
</c:if>