/* globals window */
/* globals document */

'use strict';
rm.navigation = {

    init: function() {

        // Initialize the media query
        var mobileHead = $('.mobile-head'),
            navClose = $('.navbar-close'),
            navCartMobile = $('.cart-mobile'),
            navToggle = $('.navbar-toggle'),
            navMobileBrand = $('.navbar-brand-mobile'),
            navBar = $('#navbar-collapse-1'),
            body = $('body'),
            that = this;

        this.createListeners(mobileHead);
        //create tablet listeners
        this.createTabletListeners();
        //check to see if menu is open or not
        this.setInitialVisibilityStates(navCartMobile, navClose, navMobileBrand);
        // Fire resized initially to make sure everything is in the right position and heights are set
        this.resized(body, navClose, navCartMobile, navMobileBrand);
        //Set heights for nav content
        this.resizeFunctions(body);
        // Toggle
        this.navigationToggler(navToggle, navCartMobile, navClose, navMobileBrand, body, that);
        //This is used to close the menu
        this.navigationClose(navBar, navToggle, navCartMobile, navClose, navMobileBrand, body);
        //set up listeners for collapsers
        this.createCollapseListeners();
    },

    navigationClose: function($navBar, $navToggle, $navCartMobile, $navClose, $navMobileBrand, $body, $resizeWidth){
        $navClose.on('click', function() {
            //use the following check to see if menu is open
            if($navBar.hasClass('in')){
                $navToggle.click();
                $navCartMobile.show();
                $navClose.hide();
                $navMobileBrand.stop( true, true ).fadeIn().finish();
                $body.removeClass('no-scroll'); 
            }
        });
        
        /*
         * reset the hamburger menu into it's original state when in desktop view
         * created by: lester.l.gabriel
         * 
         * */
        if($resizeWidth >= 768){
            $navToggle.click();
            $navCartMobile.show();
            $navClose.hide();
            $navMobileBrand.stop( true, true ).fadeIn().finish();
            $body.removeClass('no-scroll'); 
        }
    },

    navigationToggler:function($navToggle, $navCartMobile, $navClose, $navMobileBrand, $body, $that){
        $navToggle.on('click', function() {
            $that.switchMobileHeadItems($navCartMobile, $navClose);
            $navMobileBrand.stop( true, true ).fadeToggle();
            $body.toggleClass('no-scroll');
        });
    },

    createCollapseListeners:function(){
        var bpmobile = 768,
            collapserHead = $('.collapser-header'),
            that = this;
        //create new listeners
        collapserHead.on('click', function() {
            var item = $(this);

            if(that.resizeWidthCheck() < bpmobile){
                item.toggleClass('open');
                item.next('.collapser-content').toggle();
            }
        });
    },
    createTabletListeners: function(){
        var that = this,
            bpmobile = 768;

        $('.dropdown').on('click', function(){
            if(that.resizeWidthCheck() >= bpmobile){
                rm.navigation.clickTablet($(this));
            }
        }).on('mouseenter', function(){
            if(that.resizeWidthCheck() >= bpmobile){
                $(this).addClass('hovered');
                rm.navigation.hoverTablet($(this));
            }
        }).on('mouseleave', function(){
            if(that.resizeWidthCheck() >= bpmobile){
                $(this).removeClass('hovered');
                rm.navigation.hoverTablet($(this));
            }
        });
    },
    createListeners:function($mobileHead){
        var that = this;
         //Create Listeners
        $(document).on('click', '.megamenu .dropdown-menu', function(e) {
            e.stopPropagation();
        }).on('mouseover', '.megamenu .dropdown-menu', function() {
            that.setHoverFlag($(this));
        }).on('mouseout', '.megamenu .dropdown-menu', function() {
            that.removeHoverFlag($(this));
        }).on('click', '.mobile-head .select-btn', function() {
            that.rotateDDCaret($mobileHead);
        }).on('click', '.megamenu .dropdown-link', function(e) {
             that.parentLinkBehaviour($(this), e);
        });

    },
    // Listening for click on tablet and above
    clickTablet: function($dropdownLink){
        var dropdownLink = $dropdownLink;
        if(dropdownLink.hasClass('open')){
            dropdownLink.find('ul.dropdown-menu').css('visibility','hidden');
            dropdownLink.removeClass('hovered');
        } else {
            dropdownLink.find('ul.dropdown-menu').css('visibility','visible');
        }
    },
    // Listening for hover on tablet and above
    hoverTablet: function($dropdownLink){
        var dropdownLink = $dropdownLink;
        if(dropdownLink.hasClass('hovered')){
            dropdownLink.find('ul.dropdown-menu').css('visibility','visible');
        } else {
            dropdownLink.find('ul.dropdown-menu').css('visibility','hidden');
        }
    },
    parentLinkBehaviour:function($item, $e){
        if(this.resizeWidthCheck() <= 1024){
            $e.preventDefault();
        } else {
            var addressValue = $item.attr('href');
            if (!$('.unsaved-changes').length) {
                window.location.href = addressValue;
            }
        }
    },
    resizeWidthCheck:function(){
        var theWindow = $(window),
        ww = theWindow.width();
        //return the window width
        return ww;
    },
    rotateDDCaret:function($mobileHead){
        if($mobileHead.find('.select-items').is(':visible')){
            $mobileHead.find('.select-btn').addClass('open');
        } else {
            $mobileHead.find('.select-btn').removeClass('open');
        }
    },
    resized:function($body, $navClose, $navCartMobile, $navMobileBrand){
        var resizeTimer,
        that = this;
        $(window).on('resize', function() {
          clearTimeout(resizeTimer);
          resizeTimer = setTimeout(function() {
            //Run code here, resizing has "stopped"
            that.setInitialVisibilityStates($navCartMobile, $navClose, $navMobileBrand);
            //Set max height to height of device
            that.resizeFunctions($body);

          }, 0);
        });
    },
    resizeFunctions:function($body){
        var theWindow = $(window),
        wh = theWindow.height(),
        ncc = $('.nav-content-container'),
        navBarHeight = $('.navbar-header').innerHeight(),
        bpmobile = 768,
        availableNavSpace = wh - navBarHeight,
        body = $body,
        collapserHead = $('.collapser-header'),
        //dropdownMenu = $('.dropdown-menu'),
        megaFW = $('.megamenu-fw');

        //close all open nav on rotate or resize
        collapserHead.removeClass('open');
       //dropdownMenu.css('visibility', 'hidden');
        megaFW.removeClass('hovered open');

        if(this.resizeWidthCheck() >= bpmobile){
			body.removeClass('no-scroll');
            // ncc.height('auto');
            this.collapseItem(true);
            
	        /* 
	         * call the `navigationClose` function to close the hamburger menu when resize to desktop view
	         * created by: lester.l.gabriel
	         *  
	         * */
	        //Initialize the media query
	        var navClose = $('.navbar-close'),
	            navCartMobile = $('.cart-mobile'),
	            navToggle = $('.navbar-toggle'),
	            navMobileBrand = $('.navbar-brand-mobile'),
	            navBar = $('#navbar-collapse-1');

	        this.navigationClose(navBar, navToggle, navCartMobile, navClose, navMobileBrand, body, this.resizeWidthCheck());
	        
        } else {
        	//remove any left overs
            //dropdownMenu.css('visibility', 'visible');
            //set height to the height of the device
            ncc.height(availableNavSpace);
            this.collapseItem(false);
        }

        /* remove all open datepicker when window resize */
        //$('.datepicker').remove();
        
    },
    setInitialVisibilityStates:function($navCartMobile, $navClose){
        var navCollapser = $('#navbar-collapse-1');
        if(navCollapser.is(':visible')) {
          $navClose.show();
          $navCartMobile.hide();
        } else {
          $navClose.hide();
          $navCartMobile.show();
        }
    },
    switchMobileHeadItems: function($navCartMobile, $navClose){
        //initially hide the nav closer
        $navCartMobile.toggle();
        $navClose.toggle();
    },
    // Function to do something with the media query
    collapseItem: function($device) {

        var collapser = $('.collapser'),
            collapserHead = collapser.find('.collapser-header'),
            collapserContent = collapser.find('.collapser-content');

        //remove any artifacts of styles
        collapserContent.attr('style', '');

        if ($device) {
            // Media query does match
            collapserHead.removeClass('open');
        } else {
            // Media query does not match anymore
            collapserContent.hide();
            //on click of the head add toggle the open state as you might want to close it if its opened
        }
    },
    //This allows us to have a class that turns on and off when the parent link is hovered
    setHoverFlag:function($this){
        $this.parent().addClass('hovered');
    },
    //This removes it when the user is no longer over the parent
    removeHoverFlag:function($this){
        $this.parent().removeClass('hovered');
    }

};
