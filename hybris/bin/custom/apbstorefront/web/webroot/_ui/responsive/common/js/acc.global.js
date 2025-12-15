ACC.global = {

    _autoload: [
        ["passwordStrength", $('.password-strength').length > 0],
        "bindToggleOffcanvas",
        "bindToggleXsSearch",
        "bindHoverIntentMainNavigation",
        "initImager",
        "btnPress",
        "homepageWhiteSpace",
        "pageBodyHeightCalc",
		"screenResize",
        "backToHome",
        "hideDatePickerOnScroll",
        "getCreditCheck",
		"multiAccountSelct"
    ],

    passwordStrength: function () {
        $('.password-strength').pstrength({
            verdicts: [ACC.pwdStrengthTooShortPwd,
                ACC.pwdStrengthVeryWeak,
                ACC.pwdStrengthWeak,
                ACC.pwdStrengthMedium,
                ACC.pwdStrengthStrong,
                ACC.pwdStrengthVeryStrong],
            minCharText: ACC.pwdStrengthMinCharText
        });
    },

    bindToggleOffcanvas: function () {
        $(document).on("click", ".js-toggle-sm-navigation", function () {
            ACC.global.toggleClassState($("main"), "offcanvas");
            ACC.global.toggleClassState($("html"), "offcanvas");
            ACC.global.toggleClassState($("body"), "offcanvas");
            ACC.global.resetXsSearch();
        });
    },

    bindToggleXsSearch: function () {
        $(document).on("click", ".js-toggle-xs-search", function () {
            ACC.global.toggleClassState($(".site-search"), "active");
            ACC.global.toggleClassState($(".js-mainHeader .navigation--middle"), "search-open");
        });
    },

    resetXsSearch: function () {
        $('.site-search').removeClass('active');
        $(".js-mainHeader .navigation--middle").removeClass("search-open");
    },

    toggleClassState: function ($e, c) {
        $e.hasClass(c) ? $e.removeClass(c) : $e.addClass(c);
        return $e.hasClass(c);
    },

    bindHoverIntentMainNavigation: function () {
        // enquire.register("screen and (min-width:" + screenMdMin + ")", {
        enquire.register("screen and (min-width:1280px)", {

            match: function () {
                // on screens larger or equal screenMdMin (1024px) calculate position for .sub-navigation
                $(".js-enquire-has-sub").hoverIntent(function () {
                    var $this = $(this),
                        itemWidth = $this.width();
                    var $subNav = $this.find('.js_sub__navigation'),
                        subNavWidth = $subNav.outerWidth();
                    var $mainNav = $('.js_navigation--bottom'),
                        mainNavWidth = $mainNav.width();
                    // get the left position for sub-navigation to be centered under each <li>
                    var leftPos = $this.position().left + itemWidth / 2 - subNavWidth / 2;
                    // get the top position for sub-navigation. this is usually the height of the <li> unless there is more than one row of <li>
                    var topPos = $this.position().top + $this.height();

                    if (leftPos > 0 && leftPos + subNavWidth < mainNavWidth) {
                        // .sub-navigation is within bounds of the .main-navigation
                        $subNav.css({
                            "left": leftPos,
                            "top": topPos,
                            "right": "auto"
                        });
                    } else if (leftPos < 0) {
                        // .suv-navigation can't be centered under the <li> because it would exceed the .main-navigation on the left side
                        $subNav.css({
                            "left": 0,
                            "top": topPos,
                            "right": "auto"
                        });
                    } else if (leftPos + subNavWidth > mainNavWidth) {
                        // .suv-navigation can't be centered under the <li> because it would exceed the .main-navigation on the right side
                        $subNav.css({
                            "right": 0,
                            "top": topPos,
                            "left": "auto"
                        });
                    }
                    $this.addClass("show-sub");
                }, function () {
                    $(this).removeClass("show-sub")
                });
            },

            unmatch: function () {
                // on screens smaller than screenMdMin (1024px) remove inline styles from .sub-navigation and remove hoverIntent
                $(".js_sub__navigation").removeAttr("style");
                $(".js-enquire-has-sub").hoverIntent(function () {
                    // unbinding hover
                });
            }

        });
    },

    initImager: function (elems) {
        elems = elems || '.js-responsive-image';
        this.imgr = new Imager(elems);
    },

    reprocessImages: function (elems) {
        elems = elems || '.js-responsive-image';
        if (this.imgr == undefined) {
            this.initImager(elems);
        } else {
            this.imgr.checkImagesNeedReplacing($(elems));
        }
    },

    // usage: ACC.global.addGoogleMapsApi("callback function"); // callback function name like "ACC.global.myfunction"
    addGoogleMapsApi: function (callback) {
        if (callback != undefined && $(".js-googleMapsApi").length == 0) {
            $('head').append('<script class="js-googleMapsApi" type="text/javascript" src="//maps.googleapis.com/maps/api/js?key=' + ACC.config.googleApiKey + '&sensor=false&callback=' + callback + '"></script>');
        } else if (callback != undefined) {
            eval(callback + "()");
        }
    },
    
    btnPress: function () {
        $(".btn").mouseup(function(){
            $(this).blur();
        })
    },
     //calculate height for mobile and tablet w.r.t mobile header and full header
    pageBodyHeightCalc: function () {
        var asm = document.getElementById('_asm');
        if (asm != null) {
            var asmHeight = asm.offsetHeight;
        } else {
            var asmHeight = 0;
        };
		
        var windowSize = $(window).width();
		var currentPage = $('#checkPage').val();
		
        if (windowSize <= 1279) {
			
            var headerElement = document.getElementById('full-header');
			var navBarElement = document.getElementById('mobile-header');
            
			if (headerElement != null) {	
				var headerHeight = headerElement.offsetHeight + navBarElement.offsetHeight;
				
            } else {
                var headerHeight = 0;
            };
        } else {
            var headerElement = document.getElementById('full-header');
			var navBarElement = document.getElementById('navigation-bar');
            
			if (headerElement != null) {	
				var headerHeight = headerElement.offsetHeight + navBarElement.offsetHeight;
				
            } else {
                var headerHeight = 0;
            };
        }
       
        var totalHeaderHeight = asmHeight + headerHeight;
		
        var viewportHeight = window.innerHeight;
        var finalPageBodyHeight = viewportHeight - totalHeaderHeight;
        var finalBodyContentHeight = viewportHeight - 243;
		
        if (asm != null) {
            $('.pageBodyContent').css('height',finalPageBodyHeight);
			if ($('.pageBodyContent').hasClass("only-header-height")) {
				 $('.pageBodyContent').removeClass("only-header-height");
			}
			if ($('.bodyContentGlobal').hasClass("only-header-height")) {
				$('.bodyContentGlobal').removeClass("only-header-height");
				$('.bodyContentGlobal').css('min-height',finalPageBodyHeight);
			}
			
			if (windowSize <= 1279) {
				$(".navigation--bottom .navigation__overflow").css('height',finalPageBodyHeight);
        	}
			
        } else {
            $('.pageBodyContent').css('height',finalPageBodyHeight);
			
			var homepageElementsContainer = document.getElementById('page-body-homepage');
			if ((homepageElementsContainer == null) && (currentPage != "multiAccount")) {
				$('.bodyContentGlobal').css('min-height',finalBodyContentHeight);				// Body Content Global height should not be updated on the homepage if asm is not open.
			} else if ((homepageElementsContainer == null) && (currentPage === "multiAccount")) {
				$('.bodyContentGlobal').css('min-height',(viewportHeight - headerHeight));				// Body Content Global height is different for Multi Account Page.
			}
        };
    },
 // calculate page height when user have ASM module  
    homepageWhiteSpace: function () {        
        var asm = document.getElementById('_asm');
        if (asm != null) {
            var asmHeight = asm.offsetHeight;
        } else {
            var asmHeight = 0;
        };
        
        
        var windowSize = $(window).width();
        if (windowSize <= 1279) {
            var totalWhiteSpacePadding = asmHeight + 365;
        } else {
            var totalWhiteSpacePadding = asmHeight + 285;
        };
        
        var viewportHeight = $(window).height();
        var finalWhiteSpacePadding = viewportHeight - totalWhiteSpacePadding;
        $('.white_space').css('margin-top',finalWhiteSpacePadding);
    },
	
	screenResize: function () {
		
		$(window).resize( function () {
            ACC.global.pageBodyHeightCalc();
			ACC.global.homepageWhiteSpace();
			ACC.navigation.homepagePromoSlotsCheck();
			$.waypoints('refresh');
       	});
    },

    backToHome: function () {
        $(".backToHome").on("click", function () {
            var sUrl = ACC.config.contextPath;
            window.location = sUrl;
        });
    },
    hideDatePickerOnScroll:function(){
    	$('#page-body-checkout').scroll(function(){
    		$('#deferredCalendar').datepicker('hide');
    		$('#deferredCalendar').blur();
    		 
    	})
    },
    getCreditCheck:function(){
       var getHomePage = $('#checkPage').val();
       var method = "GET";
       if(getHomePage === 'homepage'){
       $.ajax({
           url: ACC.config.encodedContextPath + '/creditcheck',
           type: method,
           success: function (data, textStatus, xhr) {
              if(data){
              var hmCreditBlock = $('input[name="showHomepageCreditBlock"]').val();
              $('#creditCheckErrorMsg').html("<div class='asahi-logout-msg'>"+hmCreditBlock+"</div>");
              }
           },
           error: function (xhr, textStatus, error) {
              var hmError = $('input[name="showErrorHomepageCreditBlock"]').val();
              $('#creditCheckErrorMsg').html("<div class='asahi-logout-msg'>"+hmError+"</div>");
           }
       });
       if($('#isAnonymousUser').val() == 'false')
           $('#cubNotification').modal('show');
       }
        
    },
	
	multiAccountSelct: function () {
		 $(document).on("submit", '.multi-account-form', function (e) {
			e.preventDefault();  

			var currentB2BUnit = $(this).find("#b2bUnitID").val(); 
			var updateURL = $(this).attr('action') + "?b2bUnitId=" + currentB2BUnit;
			var multiAccountFormData = $(this).serialize();
			
			$.ajax({
				type: $(this).attr('method'),
				url: updateURL,
				data: multiAccountFormData,
				success: function (data) {
					if(data === "true"){
						var sUrl = ACC.config.contextPath;
						window.location = sUrl;
					 } else if(data === "false") {
						 $('#multiAccountErrorMsg').removeClass("hide");
						 $('.pageBodyContent').animate({
							scrollTop: 0
							}, 1000);
					 } else{
						 window.location = ACC.config.encodedContextPath + data;;
					 }
				},
				error: function (data) {
					console.log(data);
					$('#multiAccountErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
							}, 1000);
				},
			});
        });
		
		$(window).on('load', function () {
			$(".multiAccount-button").removeClass("multiAccount-button-disabled");
			
			$(document).on("click", ".change-site", function (e) {
				e.preventDefault();

				var method = "GET";
				var changeSitURL = ACC.config.encodedContextPath + "/multiAccount/exitCustomer";
				$.ajax({
					type: method,
					url: changeSitURL,
					success: function (data) {
						$.unblockUI();
						var redirectUrl = $(".change-site").attr("href");
						window.location = redirectUrl;
					},
					error: function (data) {
						console.log(data);
						$('#multiAccountErrorMsg').removeClass("hide");
						$('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
					},
				});
			});
		});
    }
};

$(window).bind('load', function () {
    $('#cubNotification').removeClass('cboxElement');
});
