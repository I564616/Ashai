
var oDoc = document;
                  
ACC.navigation = {

    _autoload: [
        "offcanvasNavigation",
        "myAccountNavigation",
        "orderToolsNavigation",
        "shopByCategoryNavigation",
        "termsAndConditionsAccordian",
        "homepageAccordian",
        "homepagePromoSlotsCheck",
        "b2bUnitsNavigate",
        "removeExcludedCategoriesFromNav"
    ],

    offcanvasNavigation: function(){

        enquire.register("screen and (max-width:"+screenSmMax+")", {

            match : function() {
            	// level 1 navigation option in mobile and tablet
                $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub .js_nav__link--drill__down",function(e){
                    e.preventDefault();
                    $(".js-userAccount-Links").hide();
                    $("#shopByCategoryToggle").hide();
                    $(".js-enquire-offcanvas-navigation ul.js-offcanvas-links").addClass("active");
                    $(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");
                    $(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("levelthree-active");
                    $(this).parent(".js-enquire-has-sub").addClass("active");
                    var selectedL2name = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("title");
                    var selectedL2link = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("href");
                    $(".show-all-link").attr("href", selectedL2link).text("Show all " + selectedL2name);
                    $("input[name='selectedL2']").val(selectedL2name);
                });

                	
                
                // level 2 navigation option in mobile and tablet
                $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub .js_nav__link--drill__down__level2",function(e){
                	e.preventDefault();
                    $(".js-userAccount-Links").hide();
                    $("#shopByCategoryToggle").hide(); 
                    $(this).parents().find('.js-enquire-has-sub').removeClass('levelthree-active').addClass('levelthree-active');
                    $('.sub-navigation-section').find('.sub-navigation-list').removeClass('show');
                    $(this).closest('.sub-navigation-section').find('.sub-navigation-level-3').addClass('show');
                    var selectedL3name = $(this).parent(".title").find("a").attr("title");
                    var selectedL3link = $(this).parent(".title").find("a").attr("href");
                    $(".show-all-link").attr("href", selectedL3link).text("Show all " + selectedL3name);			
                    $("input[name='selectedL3']").val(selectedL3name);
                    $('.navigation__overflow').scrollTop(0);
                });
                // close navigation when user click on close icon in mobile and tablet
                $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close",function(e){
                	var selectedL3name = $("input[name='selectedL3']").val();
                	$("input[name='selectedL3']").val("");
                	if(selectedL3name == "")
            		{
                		e.preventDefault();
                        $(".js-userAccount-Links").show();
                        $("#shopByCategoryToggle").show();
                        $(".js-enquire-offcanvas-navigation ul.js-offcanvas-links").removeClass("active");
                        $(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");
                        $("input[name='selectedL2']").val("");
                        
            		}else {
            			var selectedL2name = $("input[name='selectedL2']").val();
            			$("input[name='selectedL2']").val("");
            			var l3CompLinkname = "a[title='" + selectedL3name + "'";
            			var l3Comp = $(l3CompLinkname).closest(".title").find(".js_nav__link--drill__down__level2");
                		$(l3Comp).parents().find('.js-enquire-has-sub').removeClass('levelthree-active');
                		$(l3Comp).closest('.sub-navigation-section').find('.sub-navigation-level-3').removeClass('show').addClass('hide');
            			var l2CompLinkname = "a[title='" + selectedL2name + "'";
            			var l2Comp = $(l2CompLinkname).closest(".js-enquire-has-sub").closest(".js_nav__link--drill__down");
            			l2Comp.trigger("click");
            			
            		}
					
					var selectedL2name = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("title");
                    var selectedL2link = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("href");
                    $(".show-all-link").attr("href", selectedL2link).text("Show all " + selectedL2name);
                    
					// The below needs to be added into APB as well:
					var selectedL2name = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("title");
                    var selectedL2link = $(this).parents(".nav__links--primary-has__sub").find('.js_nav__link:first').find("a").attr("href");
                    $(".show-all-link").attr("href", selectedL2link).text("Show all " + selectedL2name);
                });


            },

            unmatch : function() {
            	 $(document).on("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub .js_nav__link--drill__down",function(e){
            		 location.href = $(this).find('a').attr('href');
                 });

                $(".js-userAccount-Links").show();
                $(".js-enquire-offcanvas-navigation ul.js-offcanvas-links").removeClass("active");
                $(".js-enquire-offcanvas-navigation .js-enquire-has-sub").removeClass("active");

                $(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-has-sub > a");
                $(document).off("click",".js-enquire-offcanvas-navigation .js-enquire-sub-close");
                


            }


        });

    },

    myAccountNavigation: function(){
    	
    	var currentPage = $("#checkPage").val();
    	
        //copy the site logo
        $('.js-mobile-logo').html( $('.js-site-logo a').clone());
        
        if(currentPage === "multiAccount"){
        	$('.js-mobile-logo').html( $('.js-site-logo ').clone());
        }

        //Add the order form img in the navigation
        $('.nav-form').html($('<span class="glyphicon glyphicon-list-alt"></span>'));


        var aAcctData = [];
        var sSignBtn = "";

        //my account items
        var oMyAccountData = $(".accNavComponent");

        //the my Account hook for the desktop
        var oMMainNavDesktop = $(".js-secondaryNavAccount > ul");

        //offcanvas menu for tablet/mobile
        var oMainNav = $(".navigation--bottom > ul.nav__links.nav__links--products");
        var existSuperUser = $("#existSuperUser").val();
        if(oMyAccountData){
            var aLinks = oMyAccountData.find("a");
	            for(var i = 0; i < aLinks.length; i++)
	            {
		            if(existSuperUser==="true")
		         	{
		            	aAcctData.push({link: aLinks[i].href, text: aLinks[i].title});
		         	}
		             if(existSuperUser=="false")
			         	{
			            	 var company1 =  aLinks[i].href.indexOf("/company");
			            	 if(company1==-1)
			            		 {
			            		 aAcctData.push({link: aLinks[i].href, text: aLinks[i].title});
			            		 }
			         	}
            	}
        }

        var navClose = '';
        navClose += '<div class="close-nav">';
        navClose += '<button type="button" class="js-toggle-sm-navigation btn"><img class="menu-close-icon" src="' + ACC.config.commonResourcePath + '/images/close_grey.svg"  /></button>';
        navClose += '</div>';
        
        //create Welcome User + expand/collapse and close button
        //This is for mobile navigation. Adding html and classes.
        var oUserInfo = $(".nav__right ul li.logged_in");
        //Check to see if user is logged in
        if(oUserInfo && oUserInfo.length === 1)
        {
            var sUserBtn = '';
            sUserBtn += '<li class=\"auto \"  id="close-btn-text">';
            sUserBtn += '<a class="userSign js-toggle-sm-navigation" href="#">CLOSE</a>';

            sUserBtn += navClose;

            $('.js-sticky-user-group').html(sUserBtn);


            $('.js-userAccount-Links').append(sSignBtn);
            $('.js-userAccount-Links').append($('<li class="auto"><div class="myAccountLinksContainer js-myAccountLinksContainer"></div></li>'));

			$("#myAccountLabel").remove();
            //FOR DESKTOP
            var myAccountHook = $('<div class="addPaddingMyAccount add-nav-bottom-padding"><a class=\"myAccountLinksHeader collapsed js-myAccount-toggle\" data-toggle=\"collapse\" data-parent=".nav__right" href=\"#accNavComponentDesktopOne\">' + oMyAccountData.data("title") + '</a></div>');
            myAccountHook.insertBefore(oMyAccountData);

            //FOR MOBILE
            //create a My Account Top link for desktop - in case more components come then more parameters need to be passed from the backend
            //Added a LogOut button above the My Account hook. 
            var myAccountHook = [];
            myAccountHook.push('<div class="sub-nav">');
            myAccountHook.push('<a id="signedInUserAccountToggle" class=\"myAccountLinksHeader collapsed js-myAccount-toggle\" data-toggle=\"collapse\" data-target=".offcanvasGroup2">');
            myAccountHook.push(oMyAccountData.data("title"));
            myAccountHook.push('<span class="glyphicon glyphicon-plus myAcctExp"></span>');
            myAccountHook.push('</a>');
            myAccountHook.push('</div>');

            $('.js-myAccountLinksContainer').append(myAccountHook.join(''));

            //add UL element for nested collapsing list
            $('.js-myAccountLinksContainer').append($('<ul data-trigger="#signedInUserAccountToggle" class="offcanvasGroup2 offcanvasNoBorder collapse js-nav-collapse-body subNavList js-myAccount-root sub-nav"></ul>'));


            // offcanvas items
            // TODO Follow up here to see the output of the account data in the offcanvas menu
            // Changed the loop so that the order is not reverersed anymore. @SM
            for(var i = 0; i < aAcctData.length; i++){ 
                var oLink = oDoc.createElement("a");
                oLink.title = aAcctData[i].text;
                oLink.href = aAcctData[i].link;
                oLink.innerHTML = aAcctData[i].text;
                
                var	oListItem = oDoc.createElement("li");
                    oListItem.appendChild(oLink);
                    oListItem = $(oListItem);
                    oListItem.addClass("auto ");
                $('.js-myAccount-root').append(oListItem);
            }

        } else {
            var navButtons = ('<li class="auto liUserSign" id="close-btn-text"><a class="userSign js-toggle-sm-navigation" href="#">CLOSE</a>' + navClose) + '</li>';
            $('.js-sticky-user-group').html(navButtons);
        }

        //desktop
        for(var i = 0; i < aAcctData.length; i++){
            var oLink = oDoc.createElement("a");
            oLink.title = aAcctData[i].text;
            oLink.href = aAcctData[i].link;
            oLink.innerHTML = aAcctData[i].text;

            var oListItem = oDoc.createElement("li");
            oListItem.appendChild(oLink);
            oListItem = $(oListItem);
            oListItem.addClass("auto nav-margin-right");
            oMMainNavDesktop.get(0).appendChild(oListItem.get(0));
        }

        //hide and show contnet areas for desktop
        $('.js-secondaryNavAccount').on('show.bs.collapse', function () {

            if($('.js-secondaryNavCompany').hasClass('in')){
                $('.js-myCompany-toggle').click();
            }
            
            $('.addPaddingMyAccount').toggleClass('link-selected-state');
            $('.addPaddingMyAccount').toggleClass('add-nav-bottom-padding');
            $('.js-myAccount-toggle').toggleClass('add-bottom-border');

        });
        
        $('.js-secondaryNavAccount').on('shown.bs.collapse', function () {
			ACC.global.pageBodyHeightCalc();
			ACC.global.homepageWhiteSpace();

            if($('.js-secondaryNavCompany').hasClass('in')){
                $('.js-myCompany-toggle').click();
            }
            
            
        });

        $('.js-secondaryNavAccount').on('hide.bs.collapse', function() {
            $('.addPaddingMyAccount').toggleClass('link-selected-state');
            $('.js-myAccount-toggle').toggleClass('add-bottom-border');
        });
        
        $('.js-secondaryNavAccount').on('hidden.bs.collapse', function() {

            $('.addPaddingMyAccount').toggleClass('add-nav-bottom-padding');
			ACC.global.pageBodyHeightCalc();
			ACC.global.homepageWhiteSpace();
            
        });        

        //change icons for up and down


        $('.js-nav-collapse-body').on('hidden.bs.collapse', function(e){

            var target = $(e.target);
            var targetSpan = target.attr('data-trigger') + ' > span';
            if(target.hasClass('in')) {
                $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
            }
            else {
                $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
            }

        });

        $('.js-nav-collapse-body').on('show.bs.collapse', function(e){
            var target = $(e.target)
            var targetSpan = target.attr('data-trigger') + ' > span';
            if(target.hasClass('in')) {
                $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');

            }
            else {
                $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
            }

        });
    },

    orderToolsNavigation: function(){
        $('.js-nav-order-tools').on('click', function(e){
            $(this).toggleClass('js-nav-order-tools--active');
        });
    },
    
    shopByCategoryNavigation: function(){
        
        $('.js-shop-by-links-body').on('show.bs.collapse', function(e){
            
            var targetSpan = $("#shopByCategoryToggleIcon");
            if(targetSpan.hasClass('glyphicon-plus')) {
                $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
            }
            else {
                $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
            }

        });
        
        $('.js-shop-by-links-body').on('hide.bs.collapse', function(e){
            
            var targetSpan = $("#shopByCategoryToggleIcon");
            if(targetSpan.hasClass('glyphicon-plus')) {
                $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
            }
            else {
                $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
            }

        });
    },
    
    termsAndConditionsAccordian: function(){
        
        $('.js-tandl-1-body').on('show.bs.collapse', function(e){
            var targetSpan = $("#tandl1ToggleIcon");
            $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
        });
        
        $('.js-tandl-1-body').on('hide.bs.collapse', function(e){
            var targetSpan = $("#tandl1ToggleIcon");
            $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
        });
        
        $('.js-tandl-2-body').on('show.bs.collapse', function(e){
            var targetSpan = $("#tandl2ToggleIcon");
            $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
        });
        
        $('.js-tandl-2-body').on('hide.bs.collapse', function(e){
            var targetSpan = $("#tandl2ToggleIcon");
            $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
        });
        
        $('.js-tandl-3-body').on('show.bs.collapse', function(e){
            var targetSpan = $("#tandl3ToggleIcon");
            $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
        });
        
        $('.js-tandl-3-body').on('hide.bs.collapse', function(e){
            var targetSpan = $("#tandl3ToggleIcon");
            $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
        });
        
        $('.js-tandl-4-body').on('show.bs.collapse', function(e){
            var targetSpan = $("#tandl4ToggleIcon");
            $(targetSpan).removeClass('glyphicon-plus').addClass('glyphicon-minus');
        });
        
        $('.js-tandl-4-body').on('hide.bs.collapse', function(e){
            var targetSpan = $("#tandl4ToggleIcon");
            $(targetSpan).removeClass('glyphicon-minus').addClass('glyphicon-plus');
        });
    },
    
    homepageAccordian: function(){      
        $('#ourStoryBox').on('show.bs.collapse', function(e){
            var onPremOpen = $("#onPremiseBox").hasClass("in");
            if (onPremOpen) {
                
                $("#onPremiseBox").collapse('hide');

            } else {
                var $panel = $("div#homepage-info-buttons-desktop");
                $('.pageBodyContent').animate({
                scrollTop: $panel.offset().top - 120
                }, 1500); 
                
            }
        });
        
        $('#onPremiseBox').on('show.bs.collapse', function(e){
            var ourStoryOpen = $("#ourStoryBox").hasClass("in");
            if (ourStoryOpen) {
        
                $("#ourStoryBox").collapse('hide');

            } else {

                var $panel = $("div#homepage-info-buttons-desktop");
                $('.pageBodyContent').animate({
                scrollTop: $panel.offset().top - 120
                }, 1500); 
                
            }
        });
        
    },

    homepagePromoSlotsCheck: function() {
		
		for (i = 1; i < 5; i++) {
			if ($("#slot-margin" + i).find(".content").is(':empty')) {
				$("#slot-margin" + i).remove();
			} else {
				$(".cs-padding").css("position","relative");
				$("#slot-margin" + i).css("background-color","#fff");
			}
		}
        
        if( $('.home-credit-exceed').length ) {
			var windowSize = $(window).width();
			if (windowSize < 768) {
				var finalMarginTopWithCredit = 'calc(100vh - 550px)'; 
			} else if (windowSize < 1280) {
				var finalMarginTopWithCredit = 'calc(100vh - 770px)'; 
			} else {
				var finalMarginTopWithCredit = 'calc(100vh - 480px)'; 
			};
           
            $('#logged-in-user-row').css('margin-top',finalMarginTopWithCredit);
        }
    },
    
    b2bUnitsNavigate:function(){
    	var isLoginFlag = window.customerLoggedIn;
    	if(window.showB2BUnitDropDown){
    		
    	
    	var isLogin = (isLoginFlag == "1");
    	if(isLogin){
    		$('.b2bDropDown').slideDown();
        	$('.bgDrop').show();
    		
    	}
    	else{
    		$('.b2bDropDown').slideUp();
        	$('.bgDrop').hide();
    	}
    	
    	$('.b2bDropDown li:first-child').addClass('selected');
    	
    	var Displaytext = $('.selected').data("value");
    	
    	$('#b2bUnits').html(Displaytext + '&#9662');
    	$('#b2bUnits').click( function(){
    	  $('.b2bDropDown').slideToggle("fast");
    	 
    	});
    	$('.b2bDropDown').on('click','a', function(e){
    	 e.preventDefault();
    	 
    	 var $this = $(this).parent();
    	 $this.addClass("selected").siblings().removeClass("selected");
    	 var selectedB2b = $this.data("value");
    	 $("#b2bUnits").html(selectedB2b + '&#9662');
    	
    	 $.ajax({
         	type: "POST",
         	url: "/storefront/apb/en/AUD/update-b2bunit",
         	data:'b2bUnit='+selectedB2b,
         	success: function(data){
         		$("#state-list").html(data);
         	},
         	error:function(){
         		console.error('Got error while adding b2b unit');
         	}
         	});
    	});
    	
    	$('#b2bUnits').on('focusout', function(){
    		$('.bgDrop').hide();
            $(".b2bDropDown").slideUp("fast");
    	})
    	}
    },

    removeExcludedCategoriesFromNav: function() {
        var navSpans = $('#shopByCategoryLinks').find('li.nav__links--primary').find('span.yCmsComponent.nav__link');
        navSpans.each((i, element) => {
            if ($(element).children('a').length === 0) {
                $(element).parent('li').hide();
            }
        });        
    }
};