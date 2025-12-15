  <%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
  
<!-- Selected Products -->
<div ng-show="deal.selectableProducts.length" class="free-list-title">
  <span><spring:theme code="deal.page.free.product.select.title"/></span>
</div>

<div class="col-xs-12 free" ng-class="{'last':$last}" ng-repeat="selectable in deal.selectableProducts">
  <div class="row item">

    <div class="radio">
        <input id="freeSelect{{$parent.$index}}-{{$index}}" type="radio" name="freeSelect" ng-value="{{selectable.code}}" ng-click="deal.selectedItem = selectable.code">
        <label for="freeSelect{{$parent.$index}}-{{$index}}"></label>
    </div>

    <div class="deal-img">
      <a ng-href="{{selectable.url}}"><img ng-src="{{selectable.image}}" alt="Placeholder Image"></a>
    </div>
    <div class="item-title">
        <h4>{{selectable.title}}</h4>
        <div class="h4 h4-subheader">{{selectable.packConfig}}</div>
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