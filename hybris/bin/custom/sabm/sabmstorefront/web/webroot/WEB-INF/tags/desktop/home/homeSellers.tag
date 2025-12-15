<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>

<!-- Best sellers -->

<section class="related-products">
    <div class="row">
        <div class="col-xs-6"> 
            <h2>Best Sellers</h2>
        </div>
        <div class="col-xs-6">
            <div class="slider-nav-wrap pull-right">
                <ul class="slider-nav">
                    <li class="slider-prev">
                        <svg class="icon-arrow-left">
                            <use xlink:href="#icon-arrow-left"></use>    
                        </svg>
                    </li>
                    <li class="slider-next">
                        <svg class="icon-arrow-right">
                            <use xlink:href="#icon-arrow-right"></use>    
                        </svg>
                    </li>
                </ul>
            </div>
        </div>
    </div>
    <div class="offset-left-small">
        <div class="slick-slider clearfix">
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
            <home:homeSeller />
        </div>
    </div>
</section>
     