
/*jshint unused:false*/

    'use strict';

rm.carousels = {

    init: function(){

        $('.other-packages .slick-slider').on('init', function () {
            $(this).fadeIn(1000);
        })
        .slick({
            infinite: true,
            slidesToShow: 4,
            slidesToScroll: 1,
            prevArrow: $('.other-packages .slider-prev'),
            nextArrow: $('.other-packages .slider-next'),
            responsive: [
                {
                    breakpoint: 990,
                    settings: {
                        slidesToShow: 2
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 2
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1
                    }
                }
            ]
        });

        $('.related-products .slick-slider').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            infinite: true,
            slidesToShow: 4,
            slidesToScroll: 1,
            prevArrow: $('.related-products li.slider-prev'),
            nextArrow: $('.related-products li.slider-next'),
            responsive: [
                {
                    breakpoint: 990,
                    settings: {
                        slidesToShow: 2
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 2
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1
                    }
                }
            ]
        });

        $('.related-recommendations .slick-slider').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            infinite: true,
            slidesToShow: 4,
            slidesToScroll: 1,
            prevArrow: $('.related-recommendations li.slider-prev'),
            nextArrow: $('.related-recommendations li.slider-next'),
            adaptiveHeight: true,
            responsive: [
                {
                    breakpoint: 990,
                    settings: {
                        slidesToShow: 4
                    }
                },
                {
                    breakpoint: 768,
                    settings: {
                        slidesToShow: 2
                    }
                },
                {
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1
                    }
                }
            ]
        });

        $('.product-images .slick-hero').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            infinite: false,
            slidesToShow: 1,
            slidesToScroll: 1,
            arrows: false,
            fade: true
        });

        $('.product-images .slick-slider-thumbs').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            infinite: false,
            slidesToShow: 4,
            slidesToScroll: 1,
            asNavFor: '.product-images .slick-hero',
            focusOnSelect: true,
            prevArrow: $('.product-images li.slider-prev'),
            nextArrow: $('.product-images li.slider-next')
        });

        $('.general-carousel').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            autoplay:true,
            autoplaySpeed: 3000,
            slidesToShow: 1,
            slidesToScroll: 1,
            fade: true,
            cssEase: 'linear',
            asNavFor: '.general-carousel',
            focusOnSelect: true,
            prevArrow: $('li.slider-prev'),
            nextArrow: $('li.slider-next')
        });

        //homepage Hero Carousel for desktop
        $('#homepage_slider_desktop').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            dots: true,
            infinite: false,
            autoplay: false,
            fade: true,
            centerMode: true,
            cssEase: 'linear',
            autoplaySpeed: $('.homepage_slider_timeout').val(),
            speed: 300,
            adaptiveHeight: true,
            lazyLoad: 'ondemand',
            slidesToShow: 1,
            prevArrow: $('.home-slider .visible-sm-block li.slider-prev'),
            nextArrow: $('.home-slider .visible-sm-block li.slider-next')
        });

        //homepage Hero Carousel for mobile
        $('#homepage_slider_mobile').on('init', function () {
            $(this).fadeIn(1000);
        }).slick({
            dots: true,
            infinite: true,
            autoplay:true,
            speed: 300,
            fade: true,
            centerMode: true,
            cssEase: 'linear',
            adaptiveHeight: true,
            slidesToShow: 1,
            prevArrow: $('.home-slider .visible-xs-block li.slider-prev-mob'),
            nextArrow: $('.home-slider .visible-xs-block li.slider-next-mob')
        });

        //Forbid the Pagination dots,The dots will not be clickable
        $('#homepage_slider :button').each(function(){
            $(this).attr('disabled', true);
        });

        rm.carousels.setupCarouselVideos();
    },

    setupCarouselVideos: function() {

        /* Allow jQuery to use window width */
        /* globals window */

        var $sliderGroup,
            $sliderVideoHeroes,
            $sliderVideoThumbs;

        $sliderGroup            = $('.video-carousel');
        $sliderVideoThumbs      = $sliderGroup.find('.slick-video-thumb');
        $sliderVideoHeroes      = $sliderGroup.find('.slick-video-hero');

        // Ensure there are video thumbnails on the page
        if($sliderGroup.length > 0 && $sliderVideoThumbs.length > 0) {

            // Loop thumbs to populate poster/thumbnail images
            $sliderVideoThumbs.each(function(i, thumb) {

                var $thumb,
                    videoUrl,
                    videoDetails,
                    playIcon,
                    iframeCode,
                    homeVideoUrl;

                $thumb = $(thumb).find('[data-url]');
                videoUrl = $thumb.attr('data-url');
                homeVideoUrl = $(thumb).attr('data-url');

                if($sliderVideoHeroes.length > 0){
                    videoDetails = rm.utilities.parseVideoURL(videoUrl);
                } else {
                    videoDetails = rm.utilities.parseVideoURL(homeVideoUrl);
                }

                playIcon = '<svg class="icon-play-button"><use xmlns:xlink="http://www.w3.org/1999/xlink" xlink:href="#icon-play-button"></use></svg>';

                // Get thumbnails and append to details array

                if(videoDetails.provider === 'youtube') {

                    var youtubePoster,
                        youtubeThumb;

                    youtubePoster = 'https://img.youtube.com/vi/' + videoDetails.id + '/maxresdefault.jpg';
                    youtubeThumb = 'https://img.youtube.com/vi/' + videoDetails.id + '/2.jpg';

                    // Youtube video iframe
                    iframeCode = '<div class="video-slide-mobile">' +
                                        '<iframe width="560" height="315" src="https://www.youtube.com/embed/' + videoDetails.id + '?rel=0&controls=0&amp;showinfo=0;" frameborder="0" allowfullscreen></iframe>' +
                                    '</div>';

                    // Youtube can get from URL
                    if($sliderVideoHeroes.length > 0){
                        $sliderVideoHeroes.eq(i).attr('href', videoUrl);
                        $sliderVideoHeroes.eq(i).find('img').attr('src', youtubePoster);
                        $thumb.find('img').attr('src', youtubeThumb);
                        $thumb.prepend(playIcon);

                        $sliderVideoHeroes.eq(i).parent().find('.slick-video-mobile').append(iframeCode);
                    } else {
                        // console.log($(thumb));
                        $(thumb).on('click touchstart',function(){
                            $(this).next('.slick-video-mobile').html(iframeCode);
                        });
                    }


                } else if(videoDetails.provider === 'vimeo') {
                    // Vimeo video iframe
                    iframeCode = '<div class="video-slide-mobile">' +
                                        '<iframe width="560" height="315" src="https://player.vimeo.com/video/' + videoDetails.id + '?autoplay=1;" frameborder="0" allowfullscreen></iframe>' +
                                    '</div>';

                    if($sliderVideoHeroes.length > 0){
                        // Vimeo needs to get from JSON
                        $.get('https://vimeo.com/api/v2/video/' + videoDetails.id + '.json', function( data ) {


                                $sliderVideoHeroes.eq(i).attr('href', videoUrl);

                                /* JShint ignored below to allow for third party camel-casing */
                                /* jshint ignore:start */

                                $sliderVideoHeroes.eq(i).find('img').attr('src', data[0].thumbnail_large);
                                $thumb.find('img').attr('src', data[0].thumbnail_small);
                                $thumb.prepend(playIcon);

                                /* jshint ignore:end */


                        });
                    }  else {
                            $(thumb).on('click touchstart',function(){
                                $(this).next('.slick-video-mobile').html(iframeCode);
                            });
                    }

                }

            });

        }




        // Same height for all thumbnails
        var updateThumbnailHeight = function() {
            var stHeight = $('.slick-slider-thumbs .slick-slide').first().height();
            $('.slick-slider-thumbs .slick-video-thumb').css('height', stHeight + 'px');
        };

        // Resize thumbnail size to highest on screen resize
        $(window).on('resize', updateThumbnailHeight);
        updateThumbnailHeight();

    },

    homepageHeroCarouselDotDisable: function(){
        $('.home-slider ul.slick-dots button').each(function(){
            $(this).attr('disabled',true);
            $(this).css('cursor','default');
        });
    },

    tabletSliderBtnAdjust: function(){
    	$('#homepage_slider_desktop').on('afterChange', function (event, slick, currentSlide) {
    		var $bannerBtns;
            $bannerBtns = $(slick.$slides.get(currentSlide)).find('[class="banner-button"]');
            if($bannerBtns.length===0)
            {
            	 $('.slider-nav-wrap').addClass('tablet-pos-adjust');
            	 $('.slick-dots').addClass('tablet-pos-adjust');
            }else{
            	 $('.slider-nav-wrap').removeClass('tablet-pos-adjust');
            	 $('.slick-dots').removeClass('tablet-pos-adjust');
            }
        });
    },

    genericHeroBannerRotatingImage: function() {
      $('.hero-banner-rotating-images').each(function (idx, item) {

        var
          carouselId = 'carousel' + idx,
          colorTreatment,
          positionTreatment,
          promoName,promoUrl,position,type;
        $(this).id = carouselId;
 
        $(this)
        .on('init', function(event, slick) {
          $(this).fadeIn(100);
          colorTreatment = $(this).find('.slick-active').data('colortreatment')?.toLowerCase() || 'dark';
          positionTreatment = $(this).find('.slick-active').data('textposition')?.toLowerCase() || 'left';
          promoName = $(this).find('.slick-active .carouselBannerTag').data('id');	
        	promoUrl=$(this).find('.slick-active .carouselBannerTag').data('url');	
        	position=$(this).find('.slick-active .carouselBannerTag').data('position');	
        	type=$(this).find('.slick-active .carouselBannerTag').data('type');	
        	rm.tagManager.trackPromotionExpressionView(promoName, promoUrl, position, type);
        	 $(this).attr('data-mainColorTreatment', colorTreatment);
             $(this).attr('data-mainPositionTreatment', positionTreatment);
        })
        .slick({
          adaptiveHeight: true,
          autoplay: true,
          autoplaySpeed: $(this).data('timeout'),
          cssEase: 'linear',
          dots: true,
          fade: true,
          infinite: true,
          speed: 400,
          responsive: [{
            breakpoint: 768,
            settings: {
              arrows: false
            }
          }],
          prevArrow: '<svg class="slick-prev icon-chevron-left"><use xlink:href="#icon-chevron-left"></use></svg>',
          nextArrow: '<svg class="slick-next icon-chevron-right"><use xlink:href="#icon-chevron-right"></use></svg>'
        })
        .on('beforeChange', function(event, slick, currentSlide, nextSlide) {
          colorTreatment = $(this).find('.slick-slide').eq(nextSlide).data('colortreatment').toLowerCase() || 'dark';
          positionTreatment = $(this).find('.slick-slide').eq(nextSlide).data('textposition').toLowerCase() || 'left';
          $(this).attr('data-mainColorTreatment', colorTreatment);
          $(this).attr('data-mainPositionTreatment', positionTreatment);
        })
        .on('afterChange', function(event, slick, currentSlide) {	
         	 colorTreatment = $(this).find('.slick-slide').eq(currentSlide).data('colortreatment').toLowerCase() || 'dark';	
              positionTreatment = $(this).find('.slick-slide').eq(currentSlide).data('textposition').toLowerCase() || 'left';	
              $(this).attr('data-mainColorTreatment', colorTreatment);	
              $(this).attr('data-mainPositionTreatment', positionTreatment);	
         	promoName = $(this).find('.slick-active .carouselBannerTag').data('id');	
         	promoUrl=$(this).find('.slick-active .carouselBannerTag').data('url');	
         	position=$(this).find('.slick-active .carouselBannerTag').data('position');	
         	type=$(this).find('.slick-active .carouselBannerTag').data('type');	
         	rm.tagManager.trackPromotionExpressionView(promoName, promoUrl, position, type);   
       
        });
      });
    },

    genericCarousel:function(){

    	$('#genericCarousel').slick({
    		autoplay: true,
    		infinite: true,
            mobileFirst: true,
            responsive: [
				 {
				     breakpoint: 320,
				     settings: {
				         slidesToShow: 2,
				         slidesToScroll: 2,
				         dots: true
				     }
				 },
				 {
				     breakpoint: 580,
				     settings: {
				         slidesToShow: 3,
				         slidesToScroll: 3,
				         dots: true
				     }
				 },
				 {
				     breakpoint: 768,
				     settings: {
				         slidesToShow: 4,
				         slidesToScroll: 1
				     }
				 },
				 {
				     breakpoint: 990,
				     settings: {
				         slidesToShow: 5,
				         slidesToScroll: 1
				     }
				 },
            ],
            prevArrow: '<div class="slick-prev"><svg class="icon-chevron-left"><use xlink:href="#icon-chevron-left"></use></svg></div>',
            nextArrow: '<div class="slick-next"><svg class="icon-chevron-right"><use xlink:href="#icon-chevron-right"></use></svg></div>'
         });
    },

    // Only setup carousel for mobile devices below 768px, otherwise unslick
    setupNewRecsCarousel: function() {
        console.log('Setting up new recommendations carousel');

				let carouselCheck = function() {
					let windowWidth = $(window).width();
					let $recsCarousel = $('#newRecommendations');
					let noOfRecs = $recsCarousel.children('.product-pick').length;

					if (windowWidth < 768) {
						if (!$recsCarousel.hasClass('slick-initialized')) {
							$recsCarousel.slick({
							prevArrow: $('.recommendation-component li.slider-prev'),
            	nextArrow: $('.recommendation-component li.slider-next'),
							slidesToShow: 3,
							slidesToScroll: 1,
							dots: true,
							responsive: [
								{
                    breakpoint: 480,
                    settings: {
                        slidesToShow: 1
                    }
                },
								{
									breakpoint: 768,   // when screen width is <= 768px (mobile / small tablets)
									settings: {
										slidesToShow: 2,
										slidesToScroll: 1,
										autoplay: false,
										autoplaySpeed:1000,
										variableWidth: noOfRecs === 1,
										centerMode: noOfRecs === 1,
										// maybe enable arrows/swipe/dots etc
									}
								},
							]
							});
						}
					} else {
						if ($recsCarousel.hasClass('slick-initialized')) {
							$recsCarousel.slick('unslick');
						}
					}
				};

				// write jquery which triggers on screen resize, I want 2 conditions for <768 and above 768
				$(window).on('resize', carouselCheck);
				carouselCheck();
    }
};
