<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>

<div id="kegQtyErrorMessage"
	class="hide alert alert-danger alert-dismissable"><spring:theme code="keg.return.fill.message.error"/></div>
<div class="row">
	<div class="col-xs-3 col-md-1">
		<span class="keg-title">Keg Size</span>
	</div>
	<div class="col-xs-9 col-md-3">
		<span class="keg-title">Number of Kegs</span>
	</div>
</div>

<c:forEach items="${kegSizes}" var="size" varStatus="status">
	<div class="row">
		<div class="col-xs-3 col-md-1">
			<span class="keg-size">${size.kegSize}</span>
		</div>
		<div class="col-xs-9 col-md-3">
			<c:set var="qtyMinus" value="0" />
			<div class="addtocart-component">
				<div class="qty-selector input-group js-keg-qty-selector">
					<span class="input-group-btn" style> 
						<button class="btn btn-default js-qty-selector-minus"
							style="display: inline" type="button">
							<span class="glyphicon glyphicon-minus" aria-hidden="true"></span>
						</button>
					</span>
					<input type="hidden" maxlength="3" value="${size.kegSize}"	 name="apbKegReturnKegSizForm[${status.index}].kegSize"
						/> 
					<input type="text" maxlength="3" style="display: inline"
						class="form-control js-qty-selector-input" size="1" value="${size.kegQuantity}"
						data-max="999" data-min="0" name="apbKegReturnKegSizForm[${status.index}].kegQuantity"
						id="kegReturnsQtyInput" /> 
						<span class="input-group-btn"> 
						
						<button class="btn btn-default js-qty-selector-plus"
							style="display: inline" type="button">
							<span class="glyphicon glyphicon-plus" aria-hidden="true"></span>
						</button>
					</span>
				</div>
			</div>
		</div>
	</div>
</c:forEach>









