<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>


<div class="col-sm-6 product-deal flex-col">
    <div class="row product-deal-description-wrapper">
        <div class="col-xs-9">
            <div class="product-deal-description">
             <p class="h2 h2-subheader">Buy Carlton Draught with Fat yak, get 1 Carlton Draught for free</p>
                <a href="#" class="product-deal-link">View details</a>
            </div>
        </div>
         <div class="col-xs-3">
            <div class="badge badge-red badge-md pull-right">Deals</div>
        </div>
    </div>
    <div class="row product-deal-selectors addtocart-qty">
        <div class="col-xs-6 col-md-5 trim-right-5-lg offset-bottom-xsmall">
            <ul class="select-quantity select-quantity-sm">
                <li class="down">
                    <svg class="icon-minus">
                        <use xlink:href="#icon-minus"></use>    
                    </svg>
                </li>
                <li><input name="qtyInput" maxlength="3" size="1" class="qty-input" type="tel" value="1" data-minqty="1" pattern="\d*"></li>
                <li class="up">
                    <svg class="icon-plus">
                        <use xlink:href="#icon-plus"></use>    
                    </svg>
                </li>
            </ul>
        </div>
        <div class="col-xs-6 col-md-2 trim-left-5-lg offset-bottom-xsmall">
            <label class="select-quantity-label select-quantity-label-sm" for="qtyInput">Cases</label>
        </div>
        <div class="col-xs-12 col-md-5 trim-left-5-lg">
            <a href="#" class="btn btn-primary btn-block">ADD DEAL</a>
        </div>
    </div>
</div>



