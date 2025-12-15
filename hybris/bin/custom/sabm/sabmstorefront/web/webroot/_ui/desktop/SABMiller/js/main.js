$(function () {
    'use strict';

    $('body').addClass('loading');

    // Global scripts
    rm.utilities.sapCall = false;

    rm.customUI.init();
    rm.utilities.init();

    //Datepickers.init has to be called before header to avoid start call on onchange.
    rm.navigation.init();
    rm.datepickers.init();
    rm.header.init();
    rm.notifications.init();

    // Conditional script loading
    if($('.page-productDetails').length){
        rm.productdetails.init();
    }

    if($('.page-productList').length){
        rm.productlisting.init();
    }

    if($('.page-deals').length || $('.deals-listing').length){
        rm.deals.init();
    }

    if($('.product-picks').length){
        rm.cubpicks.init();
    }

    if($('.slick-hero').length || $('.slick-slider').length || $('.carousel-wraper').length){
        rm.carousels.init();
    }

    if($('.page-cartPage').length){
        rm.cart.init();
    }

    if($('.page-multiStepCheckoutSummaryPage').length){
        rm.checkout.init();
    }

    if($('.table').length || $('.order-detail').length || $('.checkoutConfirmation').length){
        rm.responsivetable.init();
    }
    
    //update js file in template order list and template order detail.
    if($('.page-orderTemplates').length){
        rm.templatesOrder.init();
    }

    if($('.page-orderTemplateDetail').length){
        rm.templatesOrderDetail.init();
    }

    if($('.template-order-popup').length){
        rm.createordertemplate.init();
    }

    // if($('.page-homepage').length && rm.utilities.sapCall){
    //     console.log('call made');
    //     rm.utilities.loadingMessage($('.loading-message').data('simulate'),true);
    // }

    //add edit uer and create user page
    if($('.page-homepage form').length || $('.page-login').length || $('.page-createUser').length || $('.page-editUser').length || $('.page-updatePassword').length){
        rm.customer.init();
        rm.resetpassword.init();
        rm.updatepassword.init();
        rm.forgotpassword.init();
    }

    $(document).ready(function() {

    	$('[rel="tooltip"]').tooltip({placement: 'bottom', html: true, trigger: 'hover'});


    	if(pageType === 'Trackorder'){
    		var $viewOrderItemsTrigger = $('.view-order-items');

    		$viewOrderItemsTrigger.each(function(){
        		$(this).on('click',function(){
            		var $trackOrderViewDetails = $('.trackorder-view-details', this);
            		if($trackOrderViewDetails.hasClass('hide')){
            			$viewOrderItemsTrigger.find('.arrow').addClass('down');
            			$trackOrderViewDetails.removeClass('hide');
            		}else{
            			$viewOrderItemsTrigger.find('.arrow').removeClass('down');
            			$trackOrderViewDetails.addClass('hide');
            		}
        		});
    		});
    	}

    	if($('.view-only-mode').length){
            $('.bde-view-only').on('click', function(e){
            	e.preventDefault();
            });
        }

        if($('.breadcrumb').length){
            rm.breadcrumb.init();
        }

        if($('.page-billing').length){
            rm.billing.init();
            rm.responsivetable.checkForFailPayment();
        }

        if($('.page-serviceRequest').length || $('.page-sabmSupportPage').length){
            rm.serviceRequest.init();
        }

        if($('.page-authenticatedContactUs').length){
            rm.contactus.init();
        }

        if(rm.utilities.sapCall === false){
            $('body').removeClass('loading');
        }

        rm.cart.seeDeal();
        rm.modals.init();
        rm.carousels.homepageHeroCarouselDotDisable();
        rm.carousels.genericHeroBannerRotatingImage();
        rm.carousels.tabletSliderBtnAdjust();
        rm.carousels.genericCarousel();
        rm.carousels.setupNewRecsCarousel();
        rm.tabs.init();
    });

//    $(document).on('click touchend', '.view-only-mode .bde-view-only', function(e) {
//        e.preventDefault();
//    });

    $(window).resize(function() {
        rm.breadcrumb.updateMobileBreadcrumb();
    });

    if($('.page-PaymentWaitingPage').length){
        rm.checkout.waitingProcessingPage();
    }

    if($('.page-InvoicePaymentWaitingPage').length){
        rm.billing.waitingProcessingPage();
    }
    
    if($('.page-WebHookPaymentDoneWaitingPage').length){ 
    	rm.checkout.webHookPaymentDoneWaitingPage();
    }

    if($('.page-profile').length){
        rm.customer.bindProfileCheckBox();
        rm.customer.bindProfileRadio();
    }


});