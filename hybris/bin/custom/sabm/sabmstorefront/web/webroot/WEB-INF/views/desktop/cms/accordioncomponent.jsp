<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>

<%-- <c:if test="${not empty accordions }">
	<c:forEach items="${accordions}" var="accordion">
		<c:if test="${accordion.visible}">
			<div class="panel-group accordion-component" id="accordion" role="tablist" aria-multiselectable="true">
			  <div class="panel panel-default">
			  	<div class="panel-heading" role="tab" id="header-${accordion.title}">
					<h4 class="panel-title h4-alt">
						<a role="button" data-toggle="collapse" href="#${accordion.title}" aria-expanded="true" aria-controls="${accordion.title}">
							${accordion.title }
						</a>
					</h4>
					<div id="${accordion.title}" class="topFacetValues accordion-body collapse" role="tabpanel" aria-labelledby="${accordion.title}">
						<div class="panel-body">
							${accordion.content }
						</div>
					</div>
				</div>
			  </div>
			</div>
		</c:if>
	</c:forEach>
</c:if> --%>

<c:if test="${not empty accordions }">
	<c:forEach items="${accordions}" var="accordion" varStatus="count">
		<c:if test="${accordion.visible}">
			<div class="panel-group accordion-component" id="accordion" role="tablist" aria-multiselectable="true">
			  <div class="panel panel-default">
			  	<div class="panel-heading" role="tab" id="header-${accordion.title}">
					<h4 class="panel-title h4-alt">
						<a role="button" data-toggle="collapse" href="#collapse${count.index}" aria-expanded="true" aria-controls="${accordion.title}">
							${accordion.title }
						</a>
					</h4>
					<div id="collapse${count.index}" class="topFacetValues accordion-body collapse" role="tabpanel" aria-labelledby="${accordion.title}">
						<div class="panel-body">
							${accordion.content }
						</div>
					</div>
				</div>
			  </div>
			</div>
		</c:if>
	</c:forEach>
</c:if>
