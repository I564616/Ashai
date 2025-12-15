<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--Markup for Free Products	--%>

<div class="col-xs-12 free" ng-class="{'last':$last}" ng-repeat="free in deal.freeProducts">
  <div class="row item">
    <div class="deal-img">
      <a ng-href="{{free.url}}"><img ng-src="{{free.image}}" alt="Placeholder Image"></a>
    </div>
    <div class="item-title">
        <h4>{{free.title}}</h4>
        <div class="h4 h4-subheader">{{free.packConfig}}</div>
        <div class="visible-xs">
        <div class="item-qty clearfix">
          <div class="num-free">
              <span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
            </div>
          </div>
        </div>
    </div>
    <div class="col-xs-4 col-sm-4 trim-left visible-sm visible-md visible-lg ">
      <div class="item-qty clearfix">
        <div class="num-free">
          <span class="num">{{mapQty(deal)}} </span><span class="item-uom"><spring:theme code="deal.page.free" /></span>
        </div>
      </div>
    </div>
  </div>
</div>