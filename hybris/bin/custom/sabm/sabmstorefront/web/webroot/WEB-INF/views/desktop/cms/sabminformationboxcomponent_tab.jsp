<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="fn" uri="jakarta.tags.functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<a class="nav-link" id="pills-tab" data-toggle="pill" href="#${component.uid}" role="tab">${component.desktopTitle}</a>
<input type="hidden" class="tabComponentUID" value="${component.uid}" />

<div class="tab-content ${component.uid}" id="pills-tabContent">
	<div id="${component.uid}" class="tab-pane fade" role="tabpanel">
	<c:forEach items="${component.textList}" var="item">
			<div class="row row-eq-height">
				<div class="col col-md-1  col-sm-2 col-xs-3 img-icon">
					<img src="${item.media.url}" />
				</div>
				<div class="col col-md-11 col-sm-10  col-xs-9 tab-details">
					<p class="item-name">${item.name}</p>
					<div>${item.sabmSubText}</div>
				</div>
			</div>
	</c:forEach>
	</div>
</div>
