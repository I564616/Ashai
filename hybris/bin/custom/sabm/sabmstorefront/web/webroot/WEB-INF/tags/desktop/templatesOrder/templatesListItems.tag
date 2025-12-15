<%@ tag body-content="empty" trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="jakarta.tags.core"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<%@ taglib prefix="ycommerce" uri="http://hybris.com/tld/ycommercetags"%>
<%@ taglib prefix="product" tagdir="/WEB-INF/tags/desktop/product"%>
<%@ taglib prefix="common" tagdir="/WEB-INF/tags/desktop/common" %>

<div class="templates-order-table template-sort row">
	<div class="table-header visible-sm visible-md-block visible-lg-block clearfix">
		<div class="col-md-7 col-sm-6">
			<span><spring:theme code="text.orderTemplate.table.header.name" /></span>
		</div>

	    <div class="col-md-2 col-sm-4 text-center">
	       	<span><spring:theme code="text.orderTemplate.table.header.rank" /></span>
		</div>
	</div>
	<c:forEach items="${orderTemplates}" var="order">
		<div class="table-row clearfix">
			<div class="col-md-7 col-sm-6 col-xs-10 trim-left-5">
				<c:url value="orderTemplateDetail/${order.code}" var="ordersTemplateDetailUrl"/>
				<a class="titles" href="${ordersTemplateDetailUrl}"><strong>${order.name}</strong></a>
			</div>
			<div class="col-md-2 col-sm-2 col-xs-4 trim-right hidden-xs">
				<ul class="list-inline text-center">
					<li>
						<a class="js-move-up" href="" data-ordercode="${order.code}">
							<svg class="icon-ranking-up">
							    <use xlink:href="#icon-ranking-up"></use>
							</svg>
						</a>
					</li>
					<li>
						<a class="js-move-down" href="" data-ordercode="${order.code}">
							<svg class="icon-ranking-down">
							    <use xlink:href="#icon-ranking-down"></use>
							</svg>
						</a>
					</li>
				</ul>
			</div>

		    <div class="col-md-1 col-sm-1 col-xs-2 trim-left trim-right action-edit">
		       	<span><a href="${ordersTemplateDetailUrl}"><spring:theme code="text.orderTemplate.table.actions.edit" /></a></span>
			</div>
			<!-- start mobile section -->
			<div class="col-md-1 col-sm-1 col-xs-3 text-right visible-xs-block">
				<a class="js-move-up" href="" data-ordercode="${order.code}">
					<svg class="icon-ranking-up">
					    <use xlink:href="#icon-ranking-up"></use>
					</svg>
				</a>
			</div>
			<div class="col-md-1 col-sm-1 col-xs-3 visible-xs-block">
				<a class="js-move-down" href="" data-ordercode="${order.code}">
					<svg class="icon-ranking-down">
					    <use xlink:href="#icon-ranking-down"></use>
					</svg>
				</a>
			</div>
		<c:if test="${not empty order}" >
		<c:forEach items="${order.entries}" var="entry" varStatus="loop">
			<c:if test="${entry.product.cubStockStatus.code=='outOfStock'}">
				<input type="hidden" id="isOutOfStock" value="true"/>
			</c:if>
		</c:forEach>
		</c:if>
			
			<div class="col-md-2 col-sm-3 col-xs-6 trim-left trim-right">
				<c:url value="/your-business/addTemplates/" var="addTemplatesUrl" />
				<input type="hidden" class="addToTemplate-hide" value="${addTemplatesUrl}">
				<a href="javascript:void(0);" onclick="rm.templatesOrder.addToTemplate('${order.code}')" class="btn btn-primary btn-block bde-view-only"><spring:theme code="basket.add.to.basket" /></a>

			</div>
		</div>
	</c:forEach>
</div>

<!-- OUT OF STOCK -->
 <div class="modal fade modal-out-of-stock" id="outOfStockPopup" role="dialog">
    <div class="modal-dialog">
      <div class="modal-content">
        <div class="modal-body">
          <h2><spring:theme code="basket.remove.OOS"/></h2>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-default back" data-dismiss="modal"><span class="glyphicon glyphicon-menu-left"></span>back</button>
        </div>
      </div>
    </div>
  </div>
<c:set value="Home" var="pageName"/>
<common:addItemsPopup/>