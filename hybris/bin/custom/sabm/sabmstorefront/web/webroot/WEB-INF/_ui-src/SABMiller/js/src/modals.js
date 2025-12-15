
/*jshint unused:false*/
/* globals angular */
/* globals sessionStorage */
'use strict';

rm.modals = {

    init: function(){

        /* Regular popup with close button */
        $('.regular-popup').magnificPopup({
            type:'inline',
            removalDelay: 500,
            mainClass: 'mfp-slide',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
        });

        /* Regular popup without close button */
        $('.noclose-popup').magnificPopup({
            type:'inline',
            removalDelay: 500,
            mainClass: 'mfp-slide',
            closeOnBgClick: false,
            modal: true
        });

        /*  */
        $('.magnific-price').magnificPopup({
            delegate: 'a',
            type:'image',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
        });

        /* Slick slider main gallery image popup */
        $('.slick-hero').magnificPopup({
            delegate: 'a',
            type:'image',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
        });

        /*  */
        $('#payment-modal-trigger').magnificPopup({
            type:'inline',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',
            disableOn: function(){
            	if($('.view-only-mode').length){
        			return false;
        		}
            	return true;
            },
            callbacks: {
                open: function(){
                    // Send selected invoice #'s to storage incase payment fails
                    $.ajax({
                        url:'/your-business/billing/selectedinvoices',
                        type:'POST',
                        data: {invoiceSelectedList: sessionStorage.invoices}
                    });
                },
                close: function() {

                    angular.element('#payment-modal').scope().modalClose();

                    $('#expiryMonth').val('');
                    $('#expiryYear').val('');
                    $('#makePaymentForm').find('.message').hide();

                    $('#makePaymentForm .js-expiry-date').each(function() {
                        $(this).text($(this).attr('data-value'));
                    });

                    $('#makePaymentForm button').addClass('disabled');

                    rm.billing.index = 0;

                }
            }
        });

        /*  */
        $('.magnific-template-order').magnificPopup({
            delegate: 'a',
            type:'inline',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',
            callbacks:{
                close:function(){
                    if(!$('#empty-msg').hasClass('hidden')){
                        $('#empty-msg').addClass('hidden');
                    }
                }
            }
        });

        /*  */
        $('.summary-btns').magnificPopup({
            delegate: 'a',
            type: 'inline',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',
            disableOn:function(){
                return true;
            }
        });

        /*  */
        $('#editpage-remove-user').magnificPopup({
            type:'inline',
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
        });

        /* Video Specific Popups */
        $('.popup-video').magnificPopup({
            disableOn: 700,
            type: 'iframe',
            mainClass: 'mfp-fade',
            removalDelay: 160,
            preloader: false,
            fixedContentPos: false,
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',

            /* Required to pause video after it's finished playing */
            iframe: {
                markup: '<div class="mfp-iframe-scaler">'+
                            '<div class="mfp-close"></div>'+
                            '<iframe id="player1" class="mfp-iframe" frameborder="0" allowfullscreen></iframe>'+
                        '</div>',
                patterns: {
                    youtube: {
                        index: 'youtube.com/',
                        id: 'v=',
                        src: '//www.youtube.com/embed/%id%?rel=0&autoplay=1'
                    }
                }
            }
        });

        // FAQ Page Video player
        $('.page-faq a[href*="youtube"], .page-faq a[href*="vimeo"]').addClass('faq-video-popup').magnificPopup({
            disableOn: 700,
            type: 'iframe',
            mainClass: 'mfp-fade',
            removalDelay: 160,
            preloader: false,
            fixedContentPos: false,
            closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>',

            /* Required to pause video after it's finished playing */
            iframe: {
                markup: '<div class="mfp-iframe-scaler">'+
                            '<div class="mfp-close"></div>'+
                            '<iframe id="player1" class="mfp-iframe" frameborder="0" allowfullscreen></iframe>'+
                        '</div>',
                patterns: {
                    youtube: {
                        index: 'youtube.com/',
                        id: 'v=',
                        src: '//www.youtube.com/embed/%id%?rel=0&autoplay=1'
                    }
                }
            }
        });

        // If there's a youtube video on this page add the mobile embed
        if ($('.page-faq a[href*="youtube"]').length > 0) {
            $('.page-faq a[href*="youtube"]').each(function(i, link) {

                var $link,
                    videoLink,
                    videoDetails,
                    iframeCode,
                    poster;

                $link = $(link);
                videoLink = $link.attr('href');
                videoDetails = rm.utilities.parseVideoURL(videoLink);
                poster = 'https://img.youtube.com/vi/' + videoDetails.id + '/mqdefault.jpg';

                // Youtube video iframe
                iframeCode = '<div class="faq-video-mobile">' +
                    '<iframe width="560" height="315" src="https://www.youtube.com/embed/' + videoDetails.id + '?rel=0&amp;controls=0&amp;showinfo=0" frameborder="0" allowfullscreen></iframe>' +
                '</div>';

                $link.parent().append(iframeCode);
                $link.find('img').attr('src', poster);

            });
        }

        // If there's a vimeo video on this page add the mobile embed
        if ($('.page-faq a[href*="vimeo"]').length > 0) {
            $('.page-faq a[href*="vimeo"]').each(function(i, link) {

                var $link,
                    videoLink,
                    videoDetails,
                    iframeCode,
                    poster;

                $link = $(link);
                videoLink = $link.attr('href');
                videoDetails = rm.utilities.parseVideoURL(videoLink);

                // Youtube video iframe
                iframeCode = '<div class="faq-video-mobile">' +
                    '<iframe src="https://player.vimeo.com/video/' + videoDetails.id + '" width="640" height="360" frameborder="0" webkitallowfullscreen mozallowfullscreen allowfullscreen></iframe>' +
                '</div>';

                $link.parent().append(iframeCode);

                $.get('https://vimeo.com/api/v2/video/' + videoDetails.id + '.json', function( data ) {

                    /* JShint ignored below to allow for third party camel-casing */
                    /* jshint ignore:start */

                    $link.find('img').attr('src', data[0].thumbnail_large);

                    /* jshint ignore:end */

                });

            });
        }
    }
};
