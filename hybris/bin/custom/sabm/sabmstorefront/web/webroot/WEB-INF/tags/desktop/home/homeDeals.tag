<%@ taglib prefix="home" tagdir="/WEB-INF/tags/desktop/home" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="theme" tagdir="/WEB-INF/tags/shared/theme"%>
<section class="product-deals clearfix">
    <div class="container">

         <div class="row">
            <div class="col-xs-12">
                <h2 class="product-deals-title">Deals</h2>
            </div>
        </div>

        <div class="row-flex row-flex-wrap product-deal-container">
            
            <home:homeDeal />

            <home:homeDeal />

            <home:homeDeal />

            <home:homeDeal />

        </div>

       
        <div class="col-xs-12">
            <a href="#" class="link-cta pull-right">View all deals</a> 
        </div>
       
    
    </div>


        
</section>