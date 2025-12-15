var rm = rm || {};
/* globals document */
/* globals window */
/* globals localStorage */
/* globals sessionStorage */
/* globals $clamp */


'use strict';

rm.utilities = {
    sapCall: false,

	init: function() {

		this.needClamp();
		this.readmore();
		this.windowscroll();
		this.linkParagraphContent();
		this.closeTemplateOrderPopup();
        this.closeNewTemplatePopup();
		this.clamp();

		//hide more transactions message on billing page
    	$('#moreTransactions').hide();

        this.bindInlineEdit();
        //quantity input checking
        $('.main-content').on('keydown','.qty-input',function (e) {
            // Allow: backspace, delete, tab, escape, enter and .
            if ($.inArray(e.keyCode, [46, 8, 9, 27, 13, 110]) !== -1 ||
                 // Allow: Ctrl+A, Command+A
                (e.keyCode === 65 && ( e.ctrlKey === true || e.metaKey === true ) ) ||
                 // Allow: home, end, left, right, down, up
                (e.keyCode >= 35 && e.keyCode <= 40)) {
                     // let it happen, don't do anything
                     return;
            }
            // Ensure that it is a number and stop the keypress
            if ((e.shiftKey || (e.keyCode < 48 || e.keyCode > 57)) && (e.keyCode < 96 || e.keyCode > 105)) {
                e.preventDefault();
            }
        });


		//back to top
		$('#back-to-top a').on('click',function(){
			rm.utilities.goBackTop();
			return false;
		});


		// Expand filter for mobile

		$('.list-refine-btn').on('click', function(){
			$('.list-filter').slideToggle();
			$(this).toggleClass('open');
		});
	},


	goBackTop: function(){
		$('html,body').animate({scrollTop:0},'500');
	},
	clamp: function() {
		var headers1Line = document.getElementsByClassName('clamp-1'),headers2Lines = document.getElementsByClassName('clamp-2');

        setTimeout(function(){

		for(var i=0; i< headers1Line.length; i++){
			$clamp(headers1Line[i],{
				clamp: 1
			});
		}

		for(var j=0; j< headers2Lines.length; j++){
			$clamp(headers2Lines[j],{
				clamp: 2
			});
		}

        }, 100);

	},
	linkParagraphContent: function() {
		var content = document.getElementsByClassName('account-information-item-content');

		for(var j=0; j< content.length; j++){
			$clamp(content[j],{
				clamp: 5
			});
		}
	},
    parseVideoURL: function(url) {
        function getParm(url, base) {
            var re = new RegExp('(\\?|&)' + base + '\\=([^&]*)(&|$)');
            var matches = url.match(re);
            if (matches) {
                return(matches[2]);
            } else {
                return('');
            }
        }

        var retVal = {};
        var matches;

        if (url.indexOf('youtube.com/watch') !== -1) {
            retVal.provider = 'youtube';
            retVal.id = getParm(url, 'v');
        } else if (url.indexOf('vimeo.com/') !== -1) {
            matches = url.match(/(.+)?\/([\d+]{5,})\/?/);
            retVal.provider = 'vimeo';
            if(matches.length > 2){
            	retVal.id = matches[2];
            }
        }
        return(retVal); 
    },
	needClamp: function(target, line, desClass) {
		var headers2Lines = document.getElementsByClassName(target);

		for(var i=0; i< headers2Lines.length; i++){

			$clamp(headers2Lines[i],{
				clamp: line
			});
		}
		$('.'+target).removeClass(target).addClass(desClass);
	},

	readmore: function() {
		if($('.readmore-2').length){
			$('.readmore-2').readmore({
				collapsedHeight: 35,
				moreLink: '<span>View more</span>',
				lessLink: '<span>View less</span>'
			});
		}
	},

	windowscroll: function(){
		$(window).scroll(function() {
			var targetOffset = 1000;
			// toggle Back to top button
			if($(window).scrollTop()>targetOffset){
				$('#back-to-top').removeClass('hidden').affix({
					offset:{body:100}
				});
			}else{
				$('#back-to-top').removeClass('affix').addClass('hidden');
			}



           //For product list lazy loading :when scroll bar move to the windows bottom.
           if($(window).scrollTop() + $(window).height()+1 >= $(document).height()) {
           	$('<div id="spinner"><span>&nbsp;</span></div>').insertAfter('#resultsListRow');
           	if(rm.productlisting){
           		rm.productlisting.triggerLoadMoreResults();
           	}
           }
       });
	},
	loadingMessage: function(message,show){
		$('.loading-text p').text(message);
		if(show){
			$('.loading-text').show();
		}else {
			$('.loading-text').hide();
		}
		
	},
	
	showOverlay: function(show) {
		if( show ) {
			$('#overlay').css('display','block');
		} else {
			$('#overlay').css('display','none');
		}
	},
	
	debounce: function(func, wait, immediate) {
		var timeout;
		return function() {
			var context = this, args = arguments;
			var later = function() {
				timeout = null;
				if (!immediate) {
					func.apply(context, args);
				}
			};
			var callNow = immediate && !timeout;
			clearTimeout(timeout);
			timeout = setTimeout(later, wait);
			if (callNow) {
				func.apply(context, args);
			}
		};
	},

	showSpinnerById: function(id) {
		$('#'+id).show();
	},

	hideSpinnerById: function(id) {
		$('#'+id).hide();
	},

	closeTemplateOrderPopup:function(){
		$('#magnific-close').click(function(){
			$.magnificPopup.close();
		});
	},

    closeNewTemplatePopup:function(){
        $('#magnific-close-nt').click(function(){
            $.magnificPopup.close();
        });
    },

	bindInlineEdit : function(selector) {
		//$('#templateTitle').click(function() {
		$(selector).click(function() {
			var replaceWith = $('<input name="temp" class="h1 full-width" type="text"/>'), connectWith = $('input[name="targetField"]');
			var elem = $(this);

			elem.hide();
			elem.after(replaceWith);
			replaceWith.focus();
			replaceWith.val(elem.html());

			replaceWith.blur(function() {

				if ($(this).val() !== '') {
					connectWith.val($(this).val()).change();
					elem.text($(this).val());
				}

				$(this).remove();
				elem.show();
			});
		});
	},
	
	bindtemplateInlineEdit : function(selector) {
		//$('#templateTitle').click(function() {
		$(selector).click(function() {
			var replaceWith = $('<input name="temp" class="h1 full-width" type="text"/>'), connectWith = $('input[name="targetField"]');
			var elem = $(this);
			elem.hide();
			elem.after(replaceWith);
			replaceWith.focus();
			replaceWith.val(elem.text());

			replaceWith.blur(function() {

				if ($(this).val() !== '') {
					connectWith.val($(this).val()).change();
					elem.text($(this).val());
				}

				$(this).remove();
				elem.show();
			});
		});
	},

	convertDollar : function(stringSelector){
		var dollar = '$' + stringSelector.replace(/(\d)(?=(\d{3})+\.)/g, '$1,');
		return dollar;
	},
	convertDollarToString : function(stringSelector){
		var string = stringSelector.replace(/\$|,/g, '');
		return string;
	},

	merchantServiceFee : function(){
		if($('#showMSFPopup').length > 0) {
			$.magnificPopup.open({
				items:{
			        src: '#merchant-service-fee',
			        type: 'inline'
				},
		        modal: true
			});
		}
	},

	showCheckoutMSFPopup : function(){
			$.magnificPopup.open({
				items:{
			        src: '#checkout-msf-popup',
			        type: 'inline'
				},
		        modal: true
			});
	},
	
	addItemToStorage : function(key, value) {
		if(sessionStorage){
			rm.utilities.addItemToBrowserStorage(sessionStorage, key, value);
		}else if(localStorage){
			rm.utilities.addItemToBrowserStorage(localStorage, key, value);
		}
	},

	addItemToBrowserStorage : function(storage, key, value) {
		if(storage !== null){
			storage.setItem(key, value);
		}
	},
	getItemFromStorage : function(key) {
		if(sessionStorage){
			return rm.utilities.getItemFromBrowserStorage(sessionStorage, key);
		}else if(localStorage){
			return rm.utilities.getItemFromBrowserStorage(localStorage, key);
		}
	},

	getItemFromBrowserStorage : function(storage, key) {
		if(storage !== null && storage.getItem(key) !== null) {
			return storage.getItem(key);
		}
	},


	addItemToArrayStorage : function(key, value) {
		if(sessionStorage){
			rm.utilities.addItemToArrayBrowserStorage(sessionStorage, key, value);
		}else if(localStorage){
			rm.utilities.addItemToArrayBrowserStorage(localStorage, key, value);
		}
	},

	addItemToArrayBrowserStorage : function(storage, key, value) {
		if(storage !== null) {
			var arr;
			if(storage.getItem(key) === null) {
				arr = [];
			} else {
				arr = storage.getItem(key).split(',');
			}
			arr.push(value);
			storage.setItem(key, arr);
		}
	},

	removeItemFromArrayStorage : function(key, value) {
		if(sessionStorage){
			rm.utilities.removeItemFromArrayBrowserStorage(sessionStorage, key, value);
		}else if(localStorage){
			rm.utilities.removeItemFromArrayBrowserStorage(localStorage, key, value);
		}
	},

	removeItemFromArrayBrowserStorage : function(storage, key, value) {
		if(storage !== null) {
			var arr;
			if(storage.getItem(key) !== null) {
				arr = storage.getItem(key).split(',');
				arr = jQuery.grep(arr, function(item) {
					  return item !== value;
					});
			}
			storage.setItem(key, arr);
		}
	},

	removeItemFromStorage : function(key) {
		if(sessionStorage){
			rm.utilities.removeItemFromBrowserStorage(sessionStorage, key);
		}else if(localStorage){
			rm.utilities.removeItemFromBrowserStorage(localStorage, key);
		}
	},

	removeItemFromBrowserStorage : function(storage, key) {
		if(storage !== null) {
			storage.removeItem(key);
		}
	},

	getArrayFromStorage : function(key) {
		if(sessionStorage){
			return rm.utilities.getArrayFromBrowserStorage(sessionStorage, key);
		}else if(localStorage){
			return rm.utilities.getArrayFromBrowserStorage(localStorage, key);
		}
	},

	getArrayFromBrowserStorage : function(storage, key) {
		if(storage !== null && storage.getItem(key) !== null) {
			return storage.getItem(key).split(',');
		}
	},

	//Set equal height
	setEqualHeight: function($elems) {
		var heights = $.map($elems, function(elem) {
	return $(elem).height();
		});
		var maxHeight = Math.max.apply(null, heights);

		$elems.each(function() {
		$(this).height(maxHeight);
		});
	}
};
/* ===== yeoman scripts-hook do-not-remove ===== */

/* ===== Hybris OOTB Scripts ===== */

ACC.common = {
	currentCurrency: "USD",
	$page: $("#page"),

	setCurrentCurrency: function ()
	{
		ACC.common.currentCurrency = ACC.common.$page.data("currencyIsoCode");
	},

	refreshScreenReaderBuffer: function ()
	{
		// changes a value in a hidden form field in order
		// to trigger a buffer update in a screen reader
		$('#accesibility_refreshScreenReaderBufferField').attr('value', new Date().getTime());
	},

	bindAll: function ()
	{
		// ACC.common.bindToUiCarouselLink();
		ACC.common.bindShowProcessingMessageToSubmitButton();
	},

	processingMessage: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif'/>"),

	bindShowProcessingMessageToSubmitButton: function ()
	{

		$(':submit.show_processing_message').each(function ()
		{
			$(this).on("click", ACC.common.showProcessingMessageAndBlockForm)
		});
	},

	showProcessingMessageAndBlockForm: function ()
	{
		$("#checkoutContentPanel").block({ message: ACC.common.processingMessage });
	},

	blockFormAndShowProcessingMessage: function (submitButton)
	{
		var form = submitButton.parents('form:first');
		form.block({ message: ACC.common.processingMessage });
	},

	showSpinnerById: function(id) {
		$('#'+id).show();
	},

	hideSpinnerById: function(id) {
		$('#'+id).hide();
	}
};

$(document).ready(function ()
{
	$(document).on('keyup change','.qty-input',function(e){
				$(this).attr('value', $(this).val())
				 $(document).find('.input-set').trigger('click')
			});
	ACC.common.setCurrentCurrency();
	ACC.common.bindAll();
});


/* Extend jquery with a postJSON method */
jQuery.extend({
	postJSON: function (url, data, callback)
	{
		return jQuery.post(url, data, callback, "json");
	}
});

//add a CSRF request token to POST ajax request if its not available
$.ajaxPrefilter(function (options, originalOptions, jqXHR)
{
	// Modify options, control originalOptions, store jqXHR, etc
	if (options.type === "post" || options.type === "POST")
	{
		jqXHR.setRequestHeader("CSRFToken", ACC.config.CSRFToken);
	}
});
ACC.track = {
	trackAddToCart: function (productCode, quantity, cartData)
	{
		window.mediator.publish('trackAddToCart',{
			productCode: productCode,
			quantity: quantity,
			cartData: cartData
		});
	},
	trackRemoveFromCart: function(productCode, initialCartQuantity,cartData)
	{
		window.mediator.publish('trackRemoveFromCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			cartData: cartData
		});
	},

	trackUpdateCart: function(productCode, initialCartQuantity, newCartQuantity,cartData)
	{
		window.mediator.publish('trackUpdateCart',{
			productCode: productCode,
			initialCartQuantity: initialCartQuantity,
			newCartQuantity: newCartQuantity,
			cartData: cartData
		});
	}
	

};
/* globals pageType */
ACC.product

ACC.product = {

	initQuickviewLightbox:function(){
		this.enableAddToCartButton();
		this.bindToAddToCartForm();
	},

	changedeliverydt: function ()
	{
		$('.delivery-header.delivery-header-desktop').mouseenter();
	},

	enableAddToCartButton: function ()
	{
		if($('#productPurchable').val() == 'true' && $('#stockStatus').val()!='outOfStock'){
			$('#addToCartButton').removeAttr("disabled");
		}
		if($('#stockStatus').val() == 'outOfStock'){
			$('#addToCartButton').html('OUT OF STOCK');
		}

	},

	bindToAddToCartForm: function ()
	{
		var addToCartForm = $('.add_to_cart_form');
		addToCartForm.ajaxForm({success: ACC.product.displayAddToCartPopup});
	},

	displayAddToCartPopup: function (cartResult, statusText, xhr, formElement)
	{

		// add max quantity error message
        handleOrderError(cartResult);

		var productCode = $(formElement).children('input[name="productCodePost"]').val(); // Fix to satisfy ACC.track.trackAddToCart() call below. Remove if needed.
		var quantity = parseInt($(formElement).children('input[name="qty"]').val());

		$('#addToCartLayer').remove();

		$('.js-track-product-addtocartpopup').remove();
		$('.js-track-order-addtocartpopup').remove();

		if (typeof ACC.minicart.refreshMiniCartCount == 'function')
		{
			ACC.minicart.refreshMiniCartCount();
		}

		if ($('#header .miniCart').is(':visible')) {
			$("#header .miniCart #miniCartPopup").append(cartResult.addToCartLayer);
		}

		if ($('#nav').is(':visible')) {
			$("#nav").append(cartResult.addToCartLayer);
		}

		/* update both desktop and mobile minicart badge count in the header component and remove `inivisible` class if count > 0 */
		$(".miniCart .count").html(cartResult.totalItemCount).removeClass('hide');
        $(".cart-mobile .count").html(cartResult.totalItemCount).removeClass('hide');

		/* show minicart tooltip then hiden after 2secs */
		$('.minicart-tooltip').fadeIn('slow').delay(2000).fadeOut('slow');
		/*
		$('#addToCartLayer').show(function(){
			ACC.product.popupActions();
			if (typeof timeoutId != 'undefined')
			{
				clearTimeout(timeoutId);
			}
			timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').hide();
			}, 5000);

		}); */

        if($('#recommendationsCount').html() != ''){
           rm.recommendation.displayRecommendationCount($('#recommendationsCount').html());
        }
		ACC.track.trackAddToCart(productCode, quantity, cartResult.cartData);

		ACC.product.addListeners();

		if ( $('.addToCartEventTag').length === 0 && statusText === 'success' ) {
			rm.responsivetable.addAddToCartListener('.js-track-product-addtocartpopup');
		}

		rm.responsivetable.addAddToCartListener('.js-track-order-addtocartpopup');
	},

	viewCartPopup: function(cartResult){
		var windowHeight = $(window).height(),
			allowedHeight;

		$('#addToCartLayer').remove();

		if ($('#header .miniCart').is(':visible')) {
			$("#header .miniCart #miniCartPopup").append(cartResult.addToCartLayer);
			ACC.product.popupActions();
		}

		if ($('#nav').is(':visible')) {
			$("#nav").append(cartResult.addToCartLayer);
		}

		/* $('#addToCartLayer').show(); */
		ACC.product.popupActions();
		//$('body').css('overflow','hidden');

		//hide minicart badge if cartResult.totalItemCount == 0
		if(cartResult.totalItemCount == 0){
			$(".miniCart .count").addClass('hide');
		}

		ACC.product.addListeners();
	},
	popupActions: function(){
		var windowHeight = $(window).height(),
			allowedHeight = windowHeight - 300, // Minus height of header and button etc at bottom of minicart
			link = $('.miniCart .items');

			$('#addToCartLayer .itemList').css('max-height',allowedHeight);

		//$('#addToCartLayer .close').on('touchend',function (){
		$('#addToCartLayer .close').on('click',function (){
		    var popup = $('#addToCartLayer');
		    popup.hide();
		    link.removeClass('open');
		    $('html').removeClass('overflow-hidden');
		});

		// if(link.hasClass('open')){
			$(document).one('click',function (e){
			    var popup = $('#addToCartLayer');

			    if (!popup.is(e.target) && popup.has(e.target).length === 0) {
			        popup.hide();
			        link.removeClass('open');
			        $('html').removeClass('overflow-hidden');
			    }
			});
		// }
	},

	addListeners: function() {
		$('.js-track-product-link').on('click', function(e) {
			with ($(this)) {
				var productObj = {
					'currencycode' 	: data('currencycode'),
					'name' 			: data('name'),
					'id'			: data('id'),
					'price'			: data('price'),
					'brand'			: data('brand'),
					'category'		: data('category'),
					'variant'		: data('variant'),
					'position'		: data('position'),
					'url'			: data('url'),
					'actionfield'	: data('actionfield')
				};
			};

			if (typeof rm.tagManager.trackProductClick !== 'undefined') {
				rm.tagManager.trackProductClick(productObj);
			}
		});


	}

};

function handleOrderError (result) {
    if (result.hasOwnProperty("maxOrderError")) {
        var errorMsg = result.maxOrderError.message;
        if (errorMsg === '') {
            $(".order-error-message").removeClass("alert").text(errorMsg);
        } else {
            $(".order-error-message").addClass("alert").text(errorMsg);
        }
    }
}

$(document).ready(function ()
{
	with(ACC.product)
	{
		bindToAddToCartForm();
		enableAddToCartButton();
	}
});
ACC.autocomplete = {

	bindAll: function ()
	{
		this.bindSearchAutocomplete();
	},
	
	addToCartFromSearchPopup: function(obj){
		$addToCartForm=$(obj).closest("form");
		$.ajax({
			url:$addToCartForm.attr('action'),
			type:'POST',
			data:$addToCartForm.serialize(),
			success: function(result) {
				ACC.product.displayAddToCartPopup(result,null,null,$addToCartForm);
				rm.tagManager.trackAddtoCartFromSearchPopup($addToCartForm);
			},
			error:function(result) {
				console.error(result); 
			}
		});
	},
	
	redirectToTargetPDP:function(obj){
		console.log(obj);
		rm.tagManager.trackSearchPopupInteractions($(obj).closest("form"));
		window.location.href=obj.href;
	},
	
	redirectToTargetPage:function(obj){
		window.location.href=obj.href;
	},

	bindSearchAutocomplete: function ()
	{
		// extend the default autocomplete widget, to solve issue on multiple instances of the searchbox component
		$.widget( "custom.yautocomplete", $.ui.autocomplete, {
			_create:function(){

				// get instance specific options form the html data attr
				var option = this.element.data("options");

				// set the options to the widget
				this._setOptions({
					minLength: option.minCharactersBeforeRequest,
					displayProductImages: option.displayProductImages,
					delay: option.waitTimeBeforeRequest,
					autocompleteUrl: option.autocompleteUrl,
					source: this.source
				});
				// call the _super()
				$.ui.autocomplete.prototype._create.call(this);

			},
			options:{
				cache:{}, // init cache per instance
				focus: function (){return false;}, // prevent textfield value replacement on item focus
				open: function(event,ui){
					
					var dealsTitle = $.trim($('.deals-badge[data-toggle="tooltip"]').attr('data-original-title'));

					if ( dealsTitle!=null && dealsTitle!="null" && dealsTitle!="") {
						// invoke tooltip
						$('[data-toggle="tooltip"]').tooltip();
						var deals = dealsTitle.split(',').join('</p><p><span class="deal-title">Deal: </span>');
						// update tooltip data
						$('[data-toggle="tooltip"]').attr('data-original-title',deals);
					}

					// Show all position
					var headerHeight = $('header#header').outerHeight(),
						navHeight = $('.main-nav .ui-autocomplete').outerHeight(),
						navpos = $('.main-nav .ui-autocomplete').position().top,
						minusPos = 45;

					if ($(window).width() >= 990) {
						minusPos = 40;
					}
					// For mobile view
					if ($(window).width() <= 767) {
						headerHeight = $('.navbar-header.hidden-sm').outerHeight();
						navHeight = $('.mobile-search .ui-autocomplete').outerHeight();
						navWidth = $('.mobile-search .ui-autocomplete').outerWidth();
						navpos = $('.mobile-search .ui-autocomplete').position().top;
						minusPos = 31;
						$('.product p.show-all').css('width',(navWidth - 20));
					}
					
					console.log(headerHeight);
					console.log(navHeight);
					console.log(navpos);
					console.log(minusPos);
					$('.product p.show-all').css('top',(headerHeight + navHeight + navpos) - minusPos);
				},
				select: function (event, ui){
					event.preventDefault();
					throw true;
				},
				close: function(event, ui){
					if ($('.force-open').length) {
						$('ul.ui-autocomplete').show();
						$('ul.ui-autocomplete').removeClass("force-open");
					} else {
						$('ul.ui-autocomplete').hide();
					}
				}
				// appendTo: this.options.appendTo,
				// position: {of: ".top-autocomplete", at: "right-280 bottom"}
			},
			_renderItem : function (ul,item){

				if (item.type == "autoSuggestion"){
					var renderHtml = "<a href='" + item.url + "' class='clearfix'>" + item.value + "</a>";
					if(item.value == null){
						return $("")
						.data("item.autocomplete", item)
						.append(null)
						.appendTo(ul);
					}
					return $("<li class='suggestions'>")
							.data("item.autocomplete", item)
							.append(renderHtml)
							.appendTo(ul);
				}
				else if (item.type == "productResult"){
					var isProductPackTypeAllowed=false;
					var productPackType=null;
					var isnapGroup = $('input[id=nap-group-id]').val();
					
					if(item.uomList.length > 0){
						productPackType=item.uomList[0].name;
						if(productPackType.toUpperCase() != 'KEG'){
							productPackType='PACK';
						}
						if(ACC.deliveryDatePackType.indexOf(productPackType.toUpperCase())!==1){
							isProductPackTypeAllowed=true;
						}
					}
					var position=item.count+1;
					var renderHtml = "<a id='searchPopupItem"+item.count+"' href='" + item.url + "' onclick='ACC.autocomplete.redirectToTargetPDP(searchPopupItem"+item.count+")' class='product clearfix' data-list='searchPopup' data-actionfield='searchPopup' data-id='" + item.code +"' data-name='" + item.value +"' data-url='" + item.url + "' data-dealsflag='"+item.deals+"' data-variant='"+productPackType+"' data-position='"+position+"'>";

					

					if (item.image != null){
						renderHtml += "<span class='thumb'><img src='" + item.image + "' />";
						if (item.new === true) {
							renderHtml += "<span class='badge badge-green badge-postion'>New</span>";
						} else if (item.deals === true) {
								if(item.dealsTitle!=null && item.dealsTitle!="null" && item.dealsTitle!=""){
							renderHtml += "<span class='badge badge-red badge-postion deals-badge' data-toggle='tooltip' data-placement='auto' data-container='body' data-html='true' data-original-title='<p>"+'<span class="deal-title">Deal: </span>'+item.dealsTitle+"</p>'>Deal</span>";
								}
								else {
									renderHtml += "<span class='badge badge-red badge-postion deals-badge' data-toggle='tooltip' data-placement='auto' data-container='body' data-html='true'>Deal</span>";
											
								}
								}
						renderHtml += "</span>";
					}
					renderHtml += 	"<span class='desc clearfix'>";
					if(item.packConfiguration!=null){
						renderHtml += 	"<span class='title'><h5>" + item.value + "</h5><p>" + item.packConfiguration +"</p></span>";
					}
					else{
						renderHtml += 	"<span class='title'><h5>" + item.value +"</h5></span>";
					}
					if ((item.cubStockStatus!=undefined && item.cubStockStatus.code == 'lowStock' && isnapGroup !== 'true'))
					{
						 renderHtml += "<span class='title low-stock-status-label'>Likely Out of Stock</span>"
					}	
					if ((item.maxorderquantity != undefined && item.maxorderquantity != null && isnapGroup !== 'true'))
					{
						renderHtml += "<span style='color:red; font-weight:700;'>Max Quantity Order is "+item.maxorderquantity+"</span>"
					}
					renderHtml += 	"</span>";
					renderHtml += 	"</a>";
					
					
					
					
					renderHtml += 	'<div class="row product-pick-selectors">';
					if ((item.cubStockStatus!=undefined && item.cubStockStatus.code == 'outOfStock') || !isProductPackTypeAllowed)
					{
						renderHtml += '<div class="search-selectors col-xs-7 trim-right-5 disabled">';
					} else {
						renderHtml += '<div class="search-selectors col-xs-7 trim-right-5">';
					}
					
					renderHtml += '<div class="col-xs-6 trim-left trim-right-5">'+
					                    '<ul class="select-quantity clearfix">'+
					                        '<li class="down disabled">'+
					                            '<svg class="icon-minus">'+
					                                '<use xlink:href="#icon-minus"></use>'+
					                            '</svg>'+
					                        '</li>'+
					                        '<li><input class="qty-input min-1" type="tel" value="1" data-minqty="1" maxlength="3"></li>'+
					                        '<li class="up">'+
					                            '<svg class="icon-plus">'+
					                                '<use xlink:href="#icon-plus"></use>'+
					                            '</svg>'+
					                        '</li>'+
					                    '</ul>'+
					                '</div>';
					renderHtml += '<div class="col-xs-6 trim-right trim-left">'+
									'<div class="select-list">';

					if (item.uomList.length === 1) {
						renderHtml += '<div class="select-single">'+item.uomList[0].code+'</div>';
					} else if (item.uomList.length > 1) {
						renderHtml += '<div class="select-btn" data-value="'+item.uomList[0].code+'">'+item.uomList[0].name+'</div>'+
										'<ul class="select-items">';
						for (var i = 0; i < item.uomList.length; i++) {
							renderHtml += '<li data-value="'+item.uomList[i].code+'">'+item.uomList[i].name+'</li>';
						}
						renderHtml += '</ul>';
					}
					renderHtml += '</div>'+
								'</div>'+
								'</div>'+
								'<div class="search-cta col-xs-5 trim-left">'+
								'<form id="searchAddToCartForm'+item.count+'" action="/sabmStore/en/cart/add" method="POST" class="add_to_cart_form" data-name="' + item.value +'" data-list="searchPopup" data-actionfield="searchPopup" data-position="'+position+'" data-variant="'+productPackType+'" data-dealsflag="'+item.deals+'">'+
										'<input type="hidden" name="productCodePost" value="'+item.code+'" />'+
										'<input type="hidden" name="qty" class="qty" value="1">'+
										'<input type="hidden" name="unit" class="addToCartUnit" value="">'+
										'<input type="hidden" name="listOriginPos" value="'+item.count+'"/>'+
										'<input type="hidden" name="CSRFToken" value="'+ACC.config.CSRFToken+'">';
					
						if(item.cubStockStatus!=undefined && item.cubStockStatus.code == 'outOfStock'){
							renderHtml +=		'<button id="searchAddToCartButton'+item.count+'" type="submit" class="btn btn-primary btn-block searchItem addToCartButton bde-view-only" disabled>OUT OF STOCK</button>';
						}
						else if(!isProductPackTypeAllowed){
							renderHtml += '<button type="submit" class="btn btn-primary btn-invert btn-block addToCartButton changeDeliveryDate bde-view-only" disabled><b>CHANGE DELIVERY DATE</b></button>';  
						}
						else{
							renderHtml += '<button type="submit" id="searchAddToCartButton'+item.count+'" onclick="ACC.autocomplete.addToCartFromSearchPopup(searchAddToCartButton'+item.count+')" class="btn btn-primary btn-block addToCartButton bde-view-only ">Add to order</button>';
						}
						renderHtml +=		'</form>';
						
						renderHtml +=		'</div>'+
								'</div>';
								var showAllResults='<p class="show-all"><a id="showAllSearchPopup'+item.count+'" onclick="ACC.autocomplete.redirectToTargetPage(showAllSearchPopup'+item.count+')" href="'+item.searchTermUrl+'">Show all results</a></p>';

					if(item.value == null){
						return $("").data("item.autocomplete", item).append(null).appendTo(ul)
					}
					return $("<li class='product addtocart-qty'>").data("item.autocomplete", item).append(renderHtml).append(showAllResults).appendTo(ul);
				}
			},
			source: function (request, response)
			{
				var self=this;
				var term = request.term.toLowerCase();
				if (term in self.options.cache)
				{
					return response(self.options.cache[term]);
				}

				if(request.term.trim() !== '') {
					$.getJSON(self.options.autocompleteUrl, {term: request.term.trim()}, function (data)
					{
						var autoSearchData = [];
						if(data.suggestions != null){
							$.each(data.suggestions, function (i, obj)
							{
								autoSearchData.push({
									value: obj.term,
									url: ACC.config.encodedContextPath + "/search?text=" + obj.term,
									type: "autoSuggestion"
								});
							});
						}
						if(data.products != null){
							$.each(data.products, function (i, obj)
							{
								autoSearchData.push({
									value: obj.name,
									code: obj.code,
									desc: obj.description,
									uomList: obj.uomList,
									deals: obj.dealsFlag,
									new: obj.newProductFlag,
									dealsTitle: obj.dealsTitle,
									count: i,
									manufacturer: obj.manufacturer,
									cubStockStatus: obj.cubStockStatus,
									maxorderquantity: obj.maxOrderQuantity,
									packConfiguration:obj.packConfiguration,//displayed the SAP product packConfiguration
									url: ACC.config.encodedContextPath + obj.url,
									type: "productResult",
									searchTermUrl: ACC.config.encodedContextPath + "/search?text=" + term,
									image: (obj.images!=null && self.options.displayProductImages) ? obj.images[0].url : null // prevent errors if obj.images = null
								});
							});
						}
						self.options.cache[term] = autoSearchData;
						return response(autoSearchData);
					});
				} else {
					return response([]);
				}
			}

		});


		$search = $(".siteSearchInput");
		if($search.length>0){
			$search.yautocomplete()
		}
	}
};

$(document).ready(function ()
{
	ACC.autocomplete.bindAll();
});
ACC.pwdstrength = {

	bindAll: function ()
	{
		this.bindPStrength();
	},
	bindPStrength: function ()
	{
		$('.strength').pstrength({ verdicts: [ACC.pwdStrengthVeryWeak,
			ACC.pwdStrengthWeak,
			ACC.pwdStrengthMedium,
			ACC.pwdStrengthStrong,
			ACC.pwdStrengthVeryStrong],
			tooShort: ACC.pwdStrengthTooShortPwd,
			minCharText: '' });
	}

};

$(document).ready(function ()
{
	ACC.pwdstrength.bindAll();
});
ACC.password = {

	bindAll: function ()
	{
		$(":password").bind("cut copy paste", function (e)
		{
			e.preventDefault();
		});
	}
};

$(document).ready(function ()
{
	ACC.password.bindAll();
});
ACC.minicart = {
	
	$layer:$('.miniCartLayer'),

	bindMiniCart: function ()
	{
		// $(document).on('click', '.addToCartButton', function(){
		// 	ACC.minicart.showMiniCart();
		// 	setTimeout(function (){
		// 		ACC.minicart.hideMiniCart();
		// 	}, 5000);
		// });

		// $(document).on('mouseleave', '.miniCart', this.hideMiniCart);
	},
	
	showMiniCart: function ()
	{

		if(ACC.minicart.$layer.data("hover"))
		{
			return;
		}
		
		if(ACC.minicart.$layer.data("needRefresh") != false){
			ACC.minicart.getMiniCartData(function(){
				ACC.minicart.$layer.fadeIn(function(){
					ACC.minicart.$layer.data("hover", true);
					ACC.minicart.$layer.data("needRefresh", false);
				});
			})
		}
		
		ACC.minicart.$layer.fadeIn(function(){
			ACC.minicart.$layer.data("hover", true);
		});
	},
	
	hideMiniCart: function ()
	{
		ACC.minicart.$layer.fadeOut(function(){
			ACC.minicart.$layer.data("hover", false);
		});
	},
	
	getMiniCartData : function(callback)
	{
		$.ajax({
			url: ACC.minicart.$layer.attr("data-rolloverPopupUrl"),
			cache: false,
			type: 'GET',
			success: function (result)
			{
				ACC.minicart.$layer.html(result);
				callback();
			}
		});	
	},

	refreshMiniCartCount : function()
	{
		console.log('update quantity');

		
		$.ajax({
			dataType: "json",
			url: ACC.minicart.$layer.attr("data-refreshMiniCartUrl") + Math.floor(Math.random() * 101) * (new Date().getTime()),
			success: function (data)
			{
				$(".miniCart .count").html(data.miniCartCount);
				$(".cart-mobile .count").html(data.miniCartCount);
				$(".miniCart .price").html(data.miniCartPrice);
				ACC.minicart.$layer.data("needRefresh", true);
			}
		});
	}
};

$(document).ready(function ()
{
	ACC.minicart.bindMiniCart();
});
ACC.paginationsort = {

	downUpKeysPressed: false,

	bindAll: function ()
	{
		this.bindPaginaSort();
	},
	bindPaginaSort: function ()
	{
		with (ACC.paginationsort)
		{
			bindSortForm($('#sort_form1'));
			bindSortForm($('#sort_form2'));
		}
	},
	bindSortForm: function (sortForm)
	{
		if ($.browser.msie)
		{
			this.sortFormIEFix($(sortForm).children('select'), $(sortForm).children('select').val());
		}

		sortForm.change(function ()
		{
			if (!$.browser.msie)
			{
				this.submit();
			}
			else
			{
				if (!ACC.paginationsort.downUpPressed)
				{
					this.submit();
				}
				ACC.paginationsort.downUpPressed = false;
			}
		});
	},
	sortFormIEFix: function (sortOptions, selectedOption)
	{
		sortOptions.keydown(function (e)
		{
			// Pressed up or down keys
			if (e.keyCode === 38 || e.keyCode === 40)
			{
				ACC.paginationsort.downUpPressed = true;
			}
			// Pressed enter
			else if (e.keyCode === 13 && selectedOption !== $(this).val())
			{
				$(this).parent().submit();
			}
			// Any other key
			else
			{
				ACC.paginationsort.downUpPressed = false;
			}
		});
	}
};

$(document).ready(function ()
{
	ACC.paginationsort.bindAll();
});
ACC.checkout = {
	spinner: $("<img id='taxesEstimateSpinner' src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),

	bindAll: function ()
	{
		this.bindCheckO();
	},

	bindCheckO: function ()
	{
		var cartEntriesError = false;
		// Alternative checkout flows options
		$('.doFlowSelectedChange').change(function ()
		{
			if ('multistep-pci' == $('#selectAltCheckoutFlow').attr('value'))
			{
				$('#selectPciOption').css('display', '');
			}
			else
			{
				$('#selectPciOption').css('display', 'none');

			}
		});

		$('#estimateTaxesButton').click(function ()
		{
			$('#zipCodewrapperDiv').removeClass("form_field_error");
			$('#countryWrapperDiv').removeClass("form_field_error");

			var countryIso = $('#countryIso').val();
			if (countryIso === "")
			{
				$('#countryWrapperDiv').addClass("form_field_error");
			}
			var zipCode = $('#zipCode').val();
			if (zipCode === "")
			{
				$('#zipCodewrapperDiv').addClass("form_field_error");
			}

			if (zipCode !== "" && countryIso !== "")
			{
				$("#order_totals_container").append(ACC.checkout.spinner);
				$.getJSON("cart/estimate", {zipCode: zipCode, isocode: countryIso  }, function (estimatedCartData)
				{
					$("#estimatedTotalTax").text(estimatedCartData.totalTax.formattedValue)
					$("#estimatedTotalPrice").text(estimatedCartData.totalPrice.formattedValue)
					$(".estimatedTotals").show();
					$(".realTotals").hide();
					$("#taxesEstimateSpinner").remove();

				});
			}
		});
	}

};

$(document).ready(function ()
{
	ACC.checkout.bindAll();
});
ACC.placeorder = {

	bindAll: function ()
	{
		this.bindPlaceOrder();
		this.updatePlaceOrderButton();
	},

	bindPlaceOrder: function ()
	{
		$(".placeOrderWithSecurityCode").on("click", function ()
		{
			ACC.common.blockFormAndShowProcessingMessage($(this));
			$(".securityCodeClass").val($("#SecurityCode").val());
			$("#placeOrderForm1").submit();
		});
	},

	updatePlaceOrderButton: function ()
	{
		
		$(".place-order").removeAttr("disabled");
		// need rewrite /  class changes
	}
};

$(document).ready(function ()
{
	ACC.placeorder.bindAll();
});
ACC.common.cart = {

	bindAll: function ()
	{
		this.bindMultidCartProduct();
	},
	bindMultidCartProduct: function ()
	{

		$(document).on("click",'.submitRemoveProductMultiD', function ()
				{	
					$('body').addClass('loading');
					var that = $(this);
					var itemIndex = $(this).data("index");
					
					var $form = $('#updateCartForm' + itemIndex);
					var initialCartQuantity = $form.find('input[name=initialQuantity]');
					var cartQuantity = $form.find('input[name=quantity]');
					var entryNumber = $form.find('input[name=entryNumber]').val(); 
					var productCode = $form.find('input[name=productCode]').val(); 
					
					cartQuantity.val(0);
					initialCartQuantity.val(0);
							
					ACC.track.trackRemoveFromCart(productCode, initialCartQuantity, cartQuantity.val());
						
					var method = $form.attr("method") ? $form.attr("method").toUpperCase() : "GET";
					$.ajax({
						url: $form.attr("action"),
						data: $form.serialize(),
						type: method,
						success: function(data) 
						{
							if($(".row.cartRow").length === 1){
								location.reload();
							} else {
								that.closest('.row.cartRow').remove();
								rm.cart.showRecalculate();
							}
						},
						error: function() 
						{
							alert("Failed to remove quantity. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
						},
						complete:function(){
							$('body').removeClass('loading');
						}

					});
				
				});
		
		
		// link to display the multi-d grid in non read-only mode
	    $(document).on("click",'.updateQuantityProduct', function (event){
	        ACC.common.cart.populateAndShowGrid(this, event, false);
		});
	    
	    // link to display the multi-d grid in read-only mode
        $(document).on("click",'.showQuantityProduct', function (event){
            ACC.common.cart.populateAndShowGrid(this, event, true);
        });
	    
	},
        
	populateAndShowGrid: function(element, event, readOnly)
    {
        event.preventDefault();

        var itemIndex = $(element).data("index");
        
        var gridEntries = $('#grid' + itemIndex);
        
        grid = $("#ajaxGrid" + itemIndex);
        
        if (!grid.is(":hidden")) {
        	grid.slideUp();
        	$("#QuantityProductToggle" + itemIndex).html("+");
        	return;
        }
      
		grid.slideDown("slow");
		$("#QuantityProductToggle" + itemIndex).html("-");

		if(grid.html() != ""){
			return;
		}
    		        
        var strSubEntries = gridEntries.data("sub-entries");
        var arrSubEntries= strSubEntries.split(',');
        var firstVariantCode = arrSubEntries[0].split(':')[0];

        var mapCodeQuantity = new Object();
        for (var i = 0; i < arrSubEntries.length; i++)
        {
            var arrValue = arrSubEntries[i].split(":");
            mapCodeQuantity[arrValue[0]] = arrValue[1];
        }

        var targetUrl = ACC.config.contextPath;

        if (readOnly === false)
        {
            targetUrl = targetUrl + '/cart/getProductVariantMatrix';
        }
        else
        {
            targetUrl = targetUrl + "/checkout/multi/getProductVariantMatrix";
        }

        var method = "GET";
        $.ajax({
            url: targetUrl,
            data: {productCode: firstVariantCode},
            type: method,
            success: function(data)
            {
                grid.html(data);
                
                if (grid.find("div[id='"+firstVariantCode+"']").val() === undefined){
                	location.reload();
                }

                var $gridContainer = grid.find(".product-grid-container");
                var numGrids = $gridContainer.length;

                for (var i = 0; i < numGrids; i++)
                {
                    ACC.common.cart.getProductQuantity($gridContainer.eq(i), mapCodeQuantity);
                }

                ACC.common.cart.coreTableActions(itemIndex, mapCodeQuantity);

                grid.slideDown("slow");
            },
            error: function(xht, textStatus, ex)
            {
                alert("Failed to get variant matrix. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
            }

        });
    },

    getProductQuantity: function(gridContainer, mapData)
	{
		var skus          = jQuery.map(gridContainer.find("input[type='hidden'].sku"), function(o) {return o.value});
		var quantities    = jQuery.map(gridContainer.find("input[type='textbox'].sku-quantity"), function(o) {return o});
		
		var totalPrice = 0.0;
		var totalQuantity = 0.0;
	
		$.each(skus, function(index, skuId) 
		{ 
			var quantity = mapData[skuId];
			if (quantity != undefined)
			{
				quantities[index].value = quantity;
				totalQuantity += parseFloat(quantity);
				
				var indexPattern = "[0-9]+";
				var currentIndex = parseInt(quantities[index].id.match(indexPattern));
				
				var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();
				totalPrice += parseFloat(currentPrice) * parseInt(quantity);
			}
		});
		
		var subTotalValue = Currency.formatMoney(Number(totalPrice).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
		var avgPriceValue = 0.0;
		if (totalQuantity > 0)
		{
			avgPriceValue = Currency.formatMoney(Number(totalPrice/totalQuantity).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
		}

		gridContainer.parent().find('#quantity').html(totalQuantity);
		gridContainer.parent().find("#avgPrice").html(avgPriceValue);
		gridContainer.parent().find("#subtotal").html(subTotalValue);
		
		var $inputQuantityValue = gridContainer.parent().find('#quantityValue');
		var $inputAvgPriceValue = gridContainer.parent().find('#avgPriceValue');
		var $inputSubtotalValue = gridContainer.parent().find('#subtotalValue');

		$inputQuantityValue.val(totalQuantity);
		$inputAvgPriceValue.val(Number(totalPrice/totalQuantity).toFixed(2));
		$inputSubtotalValue.val(Number(totalPrice).toFixed(2));
		
	}, 
	
	coreTableActions: function(itemIndex, mapCodeQuantity)  
	{
        var skuQuantityClass = '.sku-quantity';

		var quantityBefore = 0;
		var quantityAfter = 0;

		var grid = $('#ajaxGrid' + itemIndex);
		
		grid.on('click', skuQuantityClass, function(event) {
            $(this).select();
        });

        grid.on('focusin', skuQuantityClass, function(event) {
            quantityBefore = jQuery.trim(this.value);
            if (quantityBefore == "") {
                quantityBefore = 0;
                this.value = 0;
            }
        });

        grid.on('focusout', skuQuantityClass, function(event) {
            var indexPattern           = "[0-9]+";
            var currentIndex           = parseInt($(this).attr("id").match(indexPattern));
            var $gridGroup             = $(this).parents('.orderForm_grid_group');
            var $closestQuantityValue  = $gridGroup.find('#quantityValue');
            var $closestAvgPriceValue  = $gridGroup.find('#avgPriceValue');
            var $closestSubtotalValue  = $gridGroup.find('#subtotalValue');
            
            var currentQuantityValue   = $closestQuantityValue.val();
            var currentSubtotalValue   = $closestSubtotalValue.val();

            var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();
            var variantCode = $("input[id='cartEntries["+currentIndex+"].sku']").val();

            quantityAfter = jQuery.trim(this.value);

            if (isNaN(jQuery.trim(this.value))) {
                this.value = 0;
            }

            if (quantityAfter == "") {
                quantityAfter = 0;
                this.value = 0;
            }

            if (quantityBefore == 0) {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + parseInt(quantityAfter));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * parseInt(quantityAfter));
            } else {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore)));
            }

            if (parseInt($closestQuantityValue.val()) > 0) {
                $closestAvgPriceValue.val(parseFloat($closestSubtotalValue.val()) / parseInt($closestQuantityValue.val()));
            } else {
                $closestAvgPriceValue.val(0);
            }

            $closestQuantityValue.parent().find('#quantity').html($closestQuantityValue.val());
            $closestAvgPriceValue.parent().find('#avgPrice').html(ACC.productorderform.formatTotalsCurrency($closestAvgPriceValue.val()));
            $closestSubtotalValue.parent().find('#subtotal').html(ACC.productorderform.formatTotalsCurrency($closestSubtotalValue.val()));
            
            if (quantityBefore != quantityAfter)
            {
            	var method = "POST";
            	$.ajax({
            		url: ACC.config.contextPath + '/cart/updateMultiD',
            		data: {productCode: variantCode, quantity: quantityAfter, entryNumber: -1},
					type: method,
					success: function(data, textStatus, xhr) 
					{
						ACC.common.cart.refreshCartData(data, -1, null, itemIndex);
						mapCodeQuantity[variantCode] = quantityAfter;
					},
					error: function(xhr, textStatus, error) 
					{
						var redirectUrl = xhr.getResponseHeader("redirectUrl");
						var connection = xhr.getResponseHeader("Connection");
						// check if error leads to a redirect
						if (redirectUrl !== null) {
							window.location = redirectUrl;
						// check if error is caused by a closed connection
						} else if (connection === "close") {
							window.location.reload();
						}
					}
				
				});
            }

        }); 

	},
	
	refreshCartData: function(cartData, entryNum, quantity, itemIndex) 
	{
		// if cart is empty, we need to reload the whole page
		if (cartData.entries.length == 0)
		{
			location.reload();
		}
		else
		{
			var form;	
			var removeItem = false;
		
			if (entryNum == -1) // grouped item
			{
				var editLink = $('#QuantityProduct' + itemIndex);
				form = editLink.closest('form');
				var productCode = form.find('input[name=productCode]').val(); 
			
				var quantity = 0;
				var entryPrice = 0;
				for (var i = 0; i < cartData.entries.length; i++)
				{
					var entry = cartData.entries[i];
					if (entry.product.code == productCode)
					{			
						quantity = entry.quantity;
						entryPrice = entry.totalPrice;
						break;
					}
				}

				if (quantity == 0)
				{
					removeItem = true;
					form.parent().parent().remove();
				}
				else
				{
					form.find(".qty").html(quantity);
					form.parent().parent().find(".total").html(entryPrice.formattedValue);
				}
			
			}
			else //ungrouped item
			{
				form = $('#updateCartForm' + itemIndex);
		
				if (quantity == 0)
				{
					removeItem = true;
					form.parent().parent().remove();
				}
				else
				{
					for (var i = 0; i < cartData.entries.length; i++)
					{
						var entry = cartData.entries[i];
						if (entry.entryNumber == entryNum)
						{				
							form.find('input[name=quantity]').val(entry.quantity);
							form.parent().parent().find(".total").html(entry.totalPrice.formattedValue);
						}
					}
				}
			}
			
			// remove item, need to update other items' entry numbers
			if (removeItem === true)
			{
				$('.cartItem').each(function(index)
				{
					form = $(this).find('.quantity').children().first();
					var productCode = form.find('input[name=productCode]').val(); 

					for (var i = 0; i < cartData.entries.length; i++)
					{
						var entry = cartData.entries[i];
						if (entry.product.code == productCode)
						{				
							form.find('input[name=entryNumber]').val(entry.entryNumber);
							form.attr('id','updateCartForm' + entry.entryNumber);
							form.find('input[name=quantity]').attr('id','quantity' + entry.entryNumber);
							form.find('label[class=skip]').attr('for','quantity' + entry.entryNumber);
							break;
						}
					}
				});
			}
			// refresh mini cart 	
			ACC.minicart.refreshMiniCartCount();
			$('#orderTotals').next().remove();
			$('#orderTotals').remove();
			$('#ajaxCart').html($("#cartTotalsTemplate").tmpl(cartData));
		}
	}
}


$(document).ready(function ()
{
	ACC.common.cart.bindAll();
});
ACC.address = {

	spinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),
	addressID: '',

	handleChangeAddressButtonClick: function ()
	{


		ACC.address.addressID = ($(this).data("address")) ? $(this).data("address") : '';
		$('#summaryDeliveryAddressFormContainer').show();
		$('#summaryOverlayViewAddressBook').show();
		$('#summaryDeliveryAddressBook').hide();


		$.getJSON(getDeliveryAddressesUrl, ACC.address.handleAddressDataLoad);
		return false;
	},

	handleAddressDataLoad: function (data)
	{
		ACC.address.setupDeliveryAddressPopupForm(data);

		// Show the delivery address popup
		$.colorbox({
			inline: true,
			href: "#summaryDeliveryAddressOverlay",
			overlayClose: false,
			onOpen: function ()
			{
				// empty address form fields
				ACC.address.emptyAddressForm();
				$(document).on('change', '#saveAddress', function ()
				{
					var saveAddressChecked = $(this).prop('checked');
					$('#defaultAddress').prop('disabled', !saveAddressChecked);
					if (!saveAddressChecked)
					{
						$('#defaultAddress').prop('checked', false);
					}
				});
			},
			onComplete: function ()
			{
				ACC.common.refreshScreenReaderBuffer();
				$.colorbox.resize();

			},
			onClosed: function ()
			{
				ACC.common.refreshScreenReaderBuffer();
			}
		});
	},

	setupDeliveryAddressPopupForm: function (data)
	{
		// Fill the available delivery addresses
		$('#summaryDeliveryAddressBook').html($('#deliveryAddressesTemplate').tmpl({addresses: data}));
		// Handle selection of address
		$('#summaryDeliveryAddressBook button.use_address').click(ACC.address.handleSelectExistingAddressClick);
		// Handle edit address
		$('#summaryDeliveryAddressBook button.edit').click(ACC.address.handleEditAddressClick);
		// Handle set default address
		$('#summaryDeliveryAddressBook button.default').click(ACC.address.handleDefaultAddressClick);
	},

	emptyAddressForm: function ()
	{
		var options = {
			url: getDeliveryAddressFormUrl,
			data: {addressId: ACC.address.addressID, createUpdateStatus: ''},
			type: 'GET',
			success: function (data)
			{
				$('#summaryDeliveryAddressFormContainer').html(data);
				ACC.address.bindCreateUpdateAddressForm();
			}
		};

		$.ajax(options);
	},

	handleSelectExistingAddressClick: function ()
	{
		var addressId = $(this).attr('data-address');
		$.postJSON(setDeliveryAddressUrl, {addressId: addressId}, ACC.address.handleSelectExitingAddressSuccess);
		return false;
	},

	handleEditAddressClick: function ()
	{

		$('#summaryDeliveryAddressFormContainer').show();
		$('#summaryOverlayViewAddressBook').show();
		$('#summaryDeliveryAddressBook').hide();

		var addressId = $(this).attr('data-address');
		var options = {
			url: getDeliveryAddressFormUrl,
			data: {addressId: addressId, createUpdateStatus: ''},
			target: '#summaryDeliveryAddressFormContainer',
			type: 'GET',
			success: function (data)
			{
				ACC.address.bindCreateUpdateAddressForm();
				$.colorbox.resize();
			},
			error: function (xht, textStatus, ex)
			{
				alert("Failed to update cart. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
			}
		};

		$(this).ajaxSubmit(options);
		return false;
	},

	handleDefaultAddressClick: function ()
	{
		var addressId = $(this).attr('data-address');
		var options = {
			url: setDefaultAddressUrl,
			data: {addressId: addressId},
			type: 'GET',
			success: function (data)
			{
				ACC.address.setupDeliveryAddressPopupForm(data);
			},
			error: function (xht, textStatus, ex)
			{
				alert("Failed to update address book. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
			}
		};

		$(this).ajaxSubmit(options);
		return false;
	},

	handleSelectExitingAddressSuccess: function (data)
	{
		if (data != null)
		{
			ACC.refresh.refreshPage(data);
			parent.$.colorbox.close();
		}
		else
		{
			alert("Failed to set delivery address");
		}
	},

	bindCreateUpdateAddressForm: function ()
	{
		$('.create_update_address_form').each(function ()
		{
			var options = {
				type: 'POST',
				beforeSubmit: function ()
				{
					$('#checkout_delivery_address').block({ message: ACC.address.spinner });
				},
				success: function (data)
				{
					$('#summaryDeliveryAddressFormContainer').html(data);
					var status = $('.create_update_address_id').attr('status');
					if (status != null && "success" === status.toLowerCase())
					{
						ACC.refresh.getCheckoutCartDataAndRefreshPage();
						parent.$.colorbox.close();
					}
					else
					{
						ACC.address.bindCreateUpdateAddressForm();
						$.colorbox.resize();
					}
				},
				error: function (xht, textStatus, ex)
				{
					alert("Failed to update cart. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				},
				complete: function ()
				{
					$('#checkout_delivery_address').unblock();
				}
			};

			$(this).ajaxForm(options);
		});
	},

	refreshDeliveryAddressSection: function (data)
	{
		$('.summaryDeliveryAddress').replaceWith($('#deliveryAddressSummaryTemplate').tmpl(data));

	},

	bindSuggestedDeliveryAddresses: function ()
	{
		var status = $('.add_edit_delivery_address_id').attr('status');
		if (status != null && "hasSuggestedAddresses" == status)
		{
			ACC.address.showSuggestedAddressesPopup();
		}
	},

	showSuggestedAddressesPopup: function ()
	{
		$.colorbox({
			inline: true,
			height: false,
			width: 525,
			href: "#popup_suggested_delivery_addresses",
			overlayClose: false,
			onComplete: function ()
			{
				$(this).colorbox.resize();
			}
		});
	},

	bindCountrySpecificAddressForms: function ()
	{
		$('#countrySelector :input').on("change", function ()
		{
			var options = {
				'addressCode': '',
				'countryIsoCode': $(this).val()
			};
			ACC.address.displayCountrySpecificAddressForm(options, ACC.address.showAddressFormButtonPanel);
		})

	},

	showAddressFormButtonPanel: function ()
	{
		if ($('#countrySelector :input').val() !== '')
		{
			$('#addressform_button_panel').show();
		}
	},

	bindToColorboxClose: function ()
	{
		$(document).on("click", ".closeColorBox", function ()
		{
			$.colorbox.close();
		})
	},


	displayCountrySpecificAddressForm: function (options, callback)
	{
		$.ajax({
			url: ACC.config.encodedContextPath + '/your-business/addressform',
			async: true,
			data: options,
			dataType: "html",
			beforeSend: function ()
			{
				$("#i18nAddressForm").html(ACC.address.spinner);
			}
		}).done(function (data)
				{
					$("#i18nAddressForm").html($(data).html());
					if (typeof callback == 'function')
					{
						callback.call();
					}
				});
	},

	bindToChangeAddressButton: function ()
	{
		$(document).on("click", '.summaryDeliveryAddress .editButton', ACC.address.handleChangeAddressButtonClick);
	},

	bindViewAddressBook: function ()
	{
		$(document).on("click", '#summaryOverlayViewAddressBook', function ()
		{
			$('#summaryDeliveryAddressFormContainer').hide();
			$('#summaryOverlayViewAddressBook').hide();
			$('#summaryDeliveryAddressBook').show();
			$.colorbox.resize();
		});
	}
}

// Address Verification
$(document).ready(function ()
{
	with (ACC.address)
	{
		bindToChangeAddressButton();
		bindCreateUpdateAddressForm();
		bindSuggestedDeliveryAddresses();
		bindCountrySpecificAddressForms();
		showAddressFormButtonPanel();
		bindViewAddressBook();
		bindToColorboxClose();
	}
});
ACC.forgotpassword = {

	bindAll: function()
	{
		this.bindForgotPasswordLink($('.password-forgotten'));
	},

	bindForgotPasswordLink: function(link)
	{
		link.click(function()
		{
			$.get(link.data('url')).done(function(data) {
				$.colorbox({
					html: data,
					width:500,
					height: false,
					overlayClose: false,
					onOpen: function()
					{
						$('#validEmail').remove();
					},
					onComplete: function()
					{
						var forgottenPwdForm = $('#forgottenPwdForm');
						forgottenPwdForm.ajaxForm({
							success: function(data)
							{
								if ($(data).closest('#validEmail').length)
								{
									
									if ($('#validEmail').length === 0)
									{
										$('#globalMessages').append(data);
									}
									$.colorbox.close();
								}
								else
								{
							
									$("#forgottenPwdForm .control-group").replaceWith($(data).find('.control-group'));
									$.colorbox.resize();
								}
							}
						});
						ACC.common.refreshScreenReaderBuffer();
					},
					onClosed: function()
					{
						ACC.common.refreshScreenReaderBuffer();
					}
				});
			});
		});
	}
};

$(document).ready(function()
{
	ACC.forgotpassword.bindAll();
});
ACC.productDetail = {

	
	initPageEvents: function ()
	{
		
		
		// $('.productImageGallery .jcarousel-skin').jcarousel({
		// 	vertical: true
		// });
		
		
		$(document).on("click","#imageLink, .productImageZoomLink",function(e){
			e.preventDefault();
			
			$.colorbox({
				href:$(this).attr("href"),
				height:555,
				onComplete: function() {
				    ACC.common.refreshScreenReaderBuffer();
					
					$('#colorbox .productImageGallery .jcarousel-skin').jcarousel({
						vertical: true
					});
					
				},
				onClosed: function() {
					ACC.common.refreshScreenReaderBuffer();
				}
			});
		});
		
		
		
		$(".productImageGallery img").click(function(e) {
			$(".productImagePrimary img").attr("src", $(this).attr("data-primaryimagesrc"));
			$("#zoomLink, #imageLink").attr("href",$("#zoomLink").attr("data-href")+ "?galleryPosition="+$(this).attr("data-galleryposition"));
			$(".productImageGallery .thumb").removeClass("active");
			$(this).parent(".thumb").addClass("active");
		});


		$(document).on("click","#colorbox .productImageGallery img",function(e) {
			$("#colorbox  .productImagePrimary img").attr("src", $(this).attr("data-zoomurl"));
			$("#colorbox .productImageGallery .thumb").removeClass("active");
			$(this).parent(".thumb").addClass("active");
		});
		
		
		
		$("body").on("keyup", "input[name=qtyInput]", function(event) {
  			var input = $(event.target);
		  	var value = input.val();
		  	var qty_css = 'input[name=qty]';
  			while(input.parent()[0] != document) {
 				input = input.parent();
 				if(input.find(qty_css).length > 0) {
  					input.find(qty_css).val(value);
  					return;
 				}
  			}
		});
		
	


		$("#Size").change(function () {
			var url = "";
			var selectedIndex = 0;
			$("#Size option:selected").each(function () {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
			});
			if (selectedIndex != 0) {
				window.location.href=url;
			}
		});

		$("#variant").change(function () {
			var url = "";
			var selectedIndex = 0;

			$("#variant option:selected").each(function () {
				url = $(this).attr('value');
				selectedIndex = $(this).attr("index");
			});
			if (selectedIndex != 0) {
				window.location.href=url;
			}
		});

		$(".selectPriority").change(function () {
			var url = "";
			var selectedIndex = 0;

			url = $(this).attr('value');
			selectedIndex = $(this).attr("index");

			if (selectedIndex != 0) {
				window.location.href=url;
			}
		});

	}

};

$(document).ready(function ()
{

	with(ACC.productDetail)
	{
		initPageEvents();
		if($('#dealPageWrapper').length >0){
			ACC.product.addListeners();
		}
	}
});
ACC.productTabs = {

	bindAll: function ()
	{
		if($('#productTabs').length>0){
	
			// only load review one at init 
			ACC.productTabs.showReviewsAction("reviews");
		
			 ACC.productTabs.productTabs = $('#productTabs').accessibleTabs({
		        wrapperClass: 'content',
		        currentClass: 'current',
		        tabhead: '.tabHead',
		        tabbody: '.tabBody',
		        fx:'show',
		        fxspeed: null,
		        currentInfoText: 'current tab: ',
		        currentInfoPosition: 'prepend',
		        currentInfoClass: 'current-info',
				autoAnchor:true
		    });


			$(document).on("click", '#write_review_action_main, #write_review_action', function(e){
				e.preventDefault();
				ACC.productTabs.scrollToReviewTab('#write_reviews')
				$('#reviewForm input[name=headline]').focus();
			});
		
			$('#based_on_reviews, #read_reviews_action').bind("click", function(e) {
				e.preventDefault();
				ACC.productTabs.scrollToReviewTab('#reviews')
			});
		
		
			$(document).on("click", '#show_all_reviews_top_action, #show_all_reviews_bottom_action', function(e){
				e.preventDefault();
				ACC.productTabs.showReviewsAction("allreviews");
				$(this).hide();
			});
		
		}

	},
	
	scrollToReviewTab: function (pane)
	{
		$.scrollTo('#productTabs', 300, {axis: 'y'});
		ACC.productTabs.productTabs.showAccessibleTabSelector('#tab-reviews');
		$('#write_reviews,#reviews').hide();
		$(pane).show();
	},
	
	showReviewsAction: function (s)
	{
		$.get($("#reviews").data(s), function (result){
			$('#reviews').html(result);
		});
	}
};

$(document).ready(function ()
{
	ACC.productTabs.bindAll();
});
ACC.productorderform = {

	$addToCartOrderForm:        $("#AddToCartOrderForm"),
	$omsErrorMessageContainer:  $("#globalMessages"),
	$emptySkuQuantityInputs:    $(".sku-quantity[value]"),
    $nonEmptySkuQuantityInputs: $(".sku-quantity[value]"),

	// Templates
	$futureTooltipTemplate:      $("#future-tooltip-template"),
	$futureTooltipErrorTemplate: $("#future-tooltip-error-template"),
	$omsErrorMessageTemplate:    $("#oms-error-message-template"),
  
	coreTableActions: function()  {
        var skuQuantityClass = '.sku-quantity';

		var quantityBefore = 0;
		var quantityAfter = 0;

		ACC.productorderform.$addToCartOrderForm.on('click', skuQuantityClass, function(event) {
            $(this).select();
        });

        ACC.productorderform.$addToCartOrderForm.on('focusin', skuQuantityClass, function(event) {
            quantityBefore = jQuery.trim(this.value);
            if (quantityBefore == "") {
                quantityBefore = 0;
                this.value = 0;
            }
        });

        ACC.productorderform.$addToCartOrderForm.on('focusout', skuQuantityClass, function(event) {
            var indexPattern           = "[0-9]+";
            var currentIndex           = parseInt($(this).attr("id").match(indexPattern));
            var $gridGroup             = $(this).parents('.orderForm_grid_group');
            var $closestQuantityValue  = $gridGroup.find('#quantityValue');
            var $closestAvgPriceValue  = $gridGroup.find('#avgPriceValue');
            var $closestSubtotalValue  = $gridGroup.find('#subtotalValue');
            var $currentTotalItems     = $('#total-items-count');
            var currentTotalItemsValue = $currentTotalItems.html();
            var currentTotalPrice      = $('#total-price-value').val();
            var currentQuantityValue   = $closestQuantityValue.val();
            var currentSubtotalValue   = $closestSubtotalValue.val();

            var totalPrice = 0;
            var currentPrice = $("input[id='productPrice["+currentIndex+"]']").val();

            quantityAfter = jQuery.trim(this.value);

            if (isNaN(jQuery.trim(this.value))) {
                this.value = 0;
            }

            if (quantityAfter == "") {
                quantityAfter = 0;
                this.value = 0;
            }

            if (quantityBefore == 0) {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + parseInt(quantityAfter));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * parseInt(quantityAfter));

                $currentTotalItems.html(parseInt(currentTotalItemsValue) + parseInt(quantityAfter));
                totalPrice = parseFloat(currentTotalPrice) + parseFloat(currentPrice) * parseInt(quantityAfter);
            } else {
                $closestQuantityValue.val(parseInt(currentQuantityValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                $closestSubtotalValue.val(parseFloat(currentSubtotalValue) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore)));

                $currentTotalItems.html(parseInt(currentTotalItemsValue) + (parseInt(quantityAfter) - parseInt(quantityBefore)));
                totalPrice = parseFloat(currentTotalPrice) + parseFloat(currentPrice) * (parseInt(quantityAfter) - parseInt(quantityBefore));
            }
            
            // if there are no items to add, disable addToCartBtn, otherwise, enable it
            if ($('#total-items-count').text() == 0) {
                $('#addToCartBtn').attr('disabled','disabled');
            } else {
            	$('#addToCartBtn').removeAttr('disabled');
            }

            $('#total-price').html(ACC.productorderform.formatTotalsCurrency(totalPrice));
            $('#total-price-value').val(totalPrice);

            if (parseInt($closestQuantityValue.val()) > 0) {
                $closestAvgPriceValue.val(parseFloat($closestSubtotalValue.val()) / parseInt($closestQuantityValue.val()));
            } else {
                $closestAvgPriceValue.val(0);
            }

            $closestQuantityValue.parent().find('#quantity').html($closestQuantityValue.val());
            $closestAvgPriceValue.parent().find('#avgPrice').html(ACC.productorderform.formatTotalsCurrency($closestAvgPriceValue.val()));
            $closestSubtotalValue.parent().find('#subtotal').html(ACC.productorderform.formatTotalsCurrency($closestSubtotalValue.val()));

        });

	},

	bindUpdateFutureStockButton: function(updateFutureStockButton) {
		updateFutureStockButton.live("click", function(event) {
			event.preventDefault();

			var $gridContainer = $(this).parent().parent().find(".product-grid-container");
			var $skus          = jQuery.map($gridContainer.find("input[type='hidden'].sku"), function(o) {return o.value});
			var skusId         = $(this).data('skusId');
			var futureStockUrl = $(this).data('skusFutureStockUrl');
			var postData       = {skus: $skus, productCode: skusId};

			ACC.common.showSpinnerById(skusId);
			
			$.ajax({
				url:         futureStockUrl,
				type:        'POST',
				data:        postData,
				traditional: true,
				dataType:    'json',
				success:     function(data) { ACC.productorderform.updateFuture($gridContainer, $skus, data, skusId)},
				error:       function(xht, textStatus, ex) {
					ACC.common.hideSpinnerById(skusId);
					alert("Failed to get delivery modes. Error details [" + xht + ", " + textStatus + ", " + ex + "]");
				}
			});
		});
	},

	bindExpandGridButton: function(expandGridButton) {
		expandGridButton.click(function(event) {
			event.preventDefault();

			$.colorbox({
				html:      ACC.productorderform.$addToCartOrderForm.clone(true),
				scroll:    true,
				width:     "98%",
				height:    "98%",
				onCleanup: function() { ACC.productorderform.syncGrid("#cboxContent", "form#AddToCartOrderForm") }
			});
		});
	},

	updateFuture: function(gridContainer, skus, freshData, callerId) {
		// clear prior error messages
		ACC.productorderform.$omsErrorMessageContainer.find("div").remove();

		if (freshData !== null && typeof freshData['basket.page.viewFuture.unavailable'] !== 'undefined') { 
			// future stock service is not available
			$.tmpl(ACC.productorderform.$omsErrorMessageTemplate, {
				errorMessage:  freshData['basket.page.viewFuture.unavailable']
			}).appendTo(ACC.productorderform.$omsErrorMessageContainer);
		}
		else {
			$.each(skus, function(index, skuId) {
				var stocks = freshData[skuId];

				var cell               = gridContainer.find("[data-sku-id='" + skuId + "']");
				var isCurrentlyInStock = cell[0].attributes['class'].nodeValue.indexOf("in-stock") != -1;
				var futureStockPresent = typeof stocks !== 'undefined' && stocks !== null && stocks[0] !== null && typeof stocks[0] !== 'undefined';

				cell.children(".future_tooltip, .out-of-stock, .future-stock").remove(); // remove previous tool tips

				if (futureStockPresent) {
					// we have stock for this product
					if (!isCurrentlyInStock) { cell.addClass("future-stock"); }

					// render template and append to cell
					$.tmpl(ACC.productorderform.$futureTooltipTemplate, {
						deliverMessage: ACC.productorderform.$addToCartOrderForm.data("gridFutureTooltipHeadingDelivery"),
						qtyMessage:     ACC.productorderform.$addToCartOrderForm.data("gridFutureTooltipHeadingQty"),
						formattedDate:  stocks[0].formattedDate,
						availabilities: stocks
					}).appendTo(cell);

				} else {
					// no future stock for this product
					if (!isCurrentlyInStock) {
						cell[0].attributes['class'].nodeValue = "td_stock out-of-stock";
					}
				}
			});
		}
		ACC.common.hideSpinnerById(callerId);
	},

	syncGrid: function(sourceContainer, targetContainer) {
		var $allSkus = $(sourceContainer + " .sku-quantity");

		$.each($allSkus, function(index, sku) {
			var selectorSuffix     = " input[name='" + sku.name + "'].sku-quantity";
			var $skuQuantitySource = $(sourceContainer + selectorSuffix);
			var $skuQuantityTarget = $(targetContainer + selectorSuffix);

			$skuQuantityTarget.val(sku.value);

			ACC.productorderform.syncTotalsBySku($skuQuantitySource, $skuQuantityTarget);
		});
	},

	toJSON: function(gridForm, skipZeroQuantity) {
		var skus          = gridForm.find("input.sku").map(function(index, element) {return element.value}),
			skuQuantities = gridForm.find("input.sku-quantity").map(function(index, element) {return parseInt(element.value)}),
			skusAsJSON      = [];

		for (var i = 0; i < skus.length; i++) {
			if (!(skipZeroQuantity && skuQuantities[i] === 0)) {
				skusAsJSON.push({"product": { "code": skus[i] }, "quantity": skuQuantities[i]});
			}
		}

		return JSON.stringify({"cartEntries": skusAsJSON});
	},

	syncTotalsBySku: function(skuQuantitySource, skuQuantityTarget) {

		var $sourceQuantityValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#quantityValue');
		var $sourceAvgPriceValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#avgPriceValue');
		var $sourceSubtotalValue = $(skuQuantitySource).closest('.orderForm_grid_group').find('#subtotalValue');

		var $targetQuantityValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#quantityValue');
		var $targetAvgPriceValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#avgPriceValue');
		var $targetSubtotalValue = $(skuQuantityTarget).closest('.orderForm_grid_group').find('#subtotalValue');

		$targetQuantityValue.parent().find('#quantity').html($sourceQuantityValue.val());
		$targetAvgPriceValue.parent().find('#avgPrice').html(Number($sourceAvgPriceValue.val()).toFixed(2));
		$targetSubtotalValue.parent().find('#subtotal').html(Number($sourceSubtotalValue.val()).toFixed(2));

		$targetQuantityValue.val($sourceQuantityValue.val());
		$targetAvgPriceValue.val($sourceAvgPriceValue.val());
		$targetSubtotalValue.val($sourceSubtotalValue.val());
	},
	
	formatTotalsCurrency: function(amount)  {
		return Currency.formatMoney(Number(amount).toFixed(2), Currency.money_format[ACC.common.currentCurrency]);
	},
	
	cleanValues: function() {
		if ($(".orderForm_grid_group").length !== 0) {
			var formattedTotal = ACC.productorderform.formatTotalsCurrency('0.00');

			ACC.common.$page.find('#avgPrice, #subtotal, #total-price').html(formattedTotal);
			ACC.common.$page.find('#quantity, #total-items-count').html(0);

			ACC.common.$page.find('#quantityValue, #avgPriceValue, #subtotalValue, #total-price-value').val(0);
			ACC.productorderform.$emptySkuQuantityInputs.val(0);
		}
	},

	bindAll: function() {
		ACC.productorderform.coreTableActions();
		ACC.productorderform.bindUpdateFutureStockButton($(".update_future_stock_button"));
		ACC.productorderform.bindExpandGridButton($(".js-expand-grid-button"));
	},

    calculateGrid: function() {
        ACC.productorderform.$nonEmptySkuQuantityInputs.trigger('focusout');
    }
};

$(document).ready(function() {
	ACC.productorderform.bindAll();
	ACC.productorderform.cleanValues();
    ACC.productorderform.calculateGrid();
});
ACC.futurelink = {
	bindAll: function() {
		this.bindFutureStockLink();
	},
	
	bindFutureStockLink: function() {
		$(document).on("click",".futureStockLink", function(e) {
			e.preventDefault();
			$.colorbox({
				href:       $(this).attr("href"),
				width:      440,
				height:     250,
				onComplete: function() {
					$.colorbox.resize();
				}
			});
		})
	}

};

$(document).ready(function() {
	ACC.futurelink.bindAll();
});
ACC.skiplinks = {

	bindAll: function()
	{
		this.bindLinks();
	},

	bindLinks: function()
	{
		$("a[href^='#']").not("a[href='#']").click(function()
		{
			var target = $(this).attr("href");
			$(target).attr("tabIndex", -1).focus();
		});
	}
};

$(document).ready(function ()
{
	if ($.browser.webkit)
	{
		ACC.skiplinks.bindAll();
	}
});


/* ===== SAB Miller Scripts ===== */

/* globals document */
/* globals window */
/*jshint devel:true*/
/*jshint unused:false*/

	'use strict';
	
	var changingQuantityFlag = false;
rm.customUI = {

	init: function(){
		this.dropdowns();
		this.tooltips();
		this.filterShowMore();
		this.accordionToggle();
		this.QtyIncrementors();

		$('.nav-tabs a').click(function (e) {
		  e.preventDefault();
		  $(this).tab('show');
		});

		/* modified as this was breaking the mobile nav*/
		// $('.collapse').not('.global-navigation .collapse').collapse();
        // commented out above line as it was affecting global Bootstrap Collapse's behaviour + its intent wasn't clear/testable

	},
	tooltips: function(){
		$('[data-toggle="tooltip"]').tooltip({
			trigger: 'hover click',
			placement: 'auto',
			html: true
		});

		/**SAB-1567 hide tooltip**/
		$('body').on('touchstart', function(e){
			$('[data-toggle="tooltip"]').each(function () {
				// hide any open tooltips when the anywhere else in the body is clicked
				if (!$(this).is(e.target) && $(this).has(e.target).length === 0 && $('.tooltip').has(e.target).length === 0) {
					$(this).tooltip('hide');
				}////end if
			});
		});

	},
	QtyIncrementors: function(){
		// Quantity Incrementors ,Change for cart page

		$('.qty-input').each(function(){
			var input = $(this);
			// rm.customUI.checkChangeable(input);

			input.on('keyup change',function() {
				rm.customUI.checkChangeable(input);
			}).keyup();
		});

		$(document).on('touchstart click','.select-quantity .up:not(.cart-entry)',function(e){                  
            if (!changingQuantityFlag) {
            	changingQuantityFlag = true;
                setTimeout(function(){ changingQuantityFlag = false; }, 150);
                // do something
                
                  e.stopPropagation();
                  e.preventDefault();
                  
                  var $input = $(this).closest('.select-quantity').find('.qty-input');
                  if($input.val() == ''){
               		$input.val(0);
                      }
                      
				  var $input_id = $(this).closest('.select-quantity').find('.qty-input').attr("id");
                  
                  if($input.val()<99 && (($input_id == "pallet-empty") || ($input_id == "pickup-empty")
                  || ($input_id == "pickup-part"))){
					 
					  var $qty = $(this).closest('.addtocart-qty').find('.qty');
                        $input.val(parseInt($input.val()) + 1);

                              //update qty when adding to cart 
                             $qty.val($input.val());
				  }
                  
                  else if($input.val() < 999 && (($input_id != "pallet-empty") && ($input_id != "pickup-empty")
                  && ($input_id != "pickup-part"))){
                        var $qty = $(this).closest('.addtocart-qty').find('.qty');
                        $input.val(parseInt($input.val()) + 1);

                              //update qty when adding to cart 
                              $qty.val($input.val());

                        }
                        rm.customUI.checkChangeable($input);
              }
      });

      // Change for cart page
      $(document).on('click touchstart','.select-quantity .down:not(.cart-entry)',function(e){
            if (!changingQuantityFlag) {
            	changingQuantityFlag = true;
                setTimeout(function(){ changingQuantityFlag = false; }, 150);
                
                e.stopPropagation();
                  e.preventDefault();
                  var $input = $(this).closest('.select-quantity').find('.qty-input'),
                        minQty = $input.data('minqty');

                  if($input.val() >= (minQty + 1)){
                        var $qty = $(this).closest('.addtocart-qty').find('.qty');
                        $input.val(parseInt($input.val()) - 1);

                        //update qty when adding to cart 
                        $qty.val($input.val());

                  }
                  rm.customUI.checkChangeable($input);
            }
            
      });
      
      $(document).on('blur','.qty-input',function(){
       	  var $input = $(this).closest('.select-quantity').find('.qty-input');
       	  if($input.val() == ''){
       		$input.val();
              }  
       	$('.checkoutButton').hide();
		$('.doCartBut').show();
    		});

      
		$(document).on('blur','.qty-input',function(){
			var $hiddenQty = $(this).closest('.addtocart-qty').find('.qty');
			$hiddenQty.val($(this).val());
		}).on('keyup','.qty-input.min-1',function(){
			if($(this).val() === '0'){
				$(this).val('1');
			}
		});
	},

	checkChangeable: function(item){
		var qtyValue = item.val(),
			minqty = item.data('minqty'),
			down = item.parents('.select-quantity').find('.down');
			maxqty = item.data('maxqty'),
        	up = item.parents('.select-quantity').find('.up');
		setTimeout(function(){ // Wait for Angular to set the min property
			if(qtyValue < (minqty + 1)){
				down.addClass('disabled');
			} else {
				down.removeClass('disabled');
			}
			if(qtyValue >= maxqty){
                up.addClass('disabled');
            } else {
                up.removeClass('disabled');
            }
		},10);
	},
	dropdowns: function(){

		$(document).on('click',function(){
			$('.select-items').hide();
		});
		// Open dropdown and close all others on page
		$(document).on('click','.select-btn',function(e){

			e.stopPropagation();

			var $list = $(this).next('.select-items');

			$list.toggle();
			$('.select-items').not($list).hide();
		});

		$(document).ready(function(){
			var $selectSingle = $('.select-single:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			// Store value in data-value
			$selectSingle.each(function(){
				var $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				// Set hidden field value on load
				$hiddenField.val($(this).text());
			});

			var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			// Store value in data-value
			$selectBtn.each(function(){
				var $firstItem = $(this).next('ul').children('li').first(),
				    $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				// Set hidden field value and button value on load
				$hiddenField.val($firstItem.attr('data-value'));
				$(this).text($firstItem.html());
			});
			console.log('hit');
			// Change value of hidden field to selection

			//$(document).on('click touchend', '.select-items:not(.sort) li:not(.cart-entry)',function(event){
			var touchmoved;
			$(document).on('click touchend', '.select-items:not(.sort):not(.header):not(.js-expiry-date) li:not(.cart-entry)',function(e){
				e.preventDefault();
			    var $selectBtn = $(this).parent().siblings('.select-btn'),
		        $hiddenField = $(this).closest('.addtocart-qty').find('.addToCartUnit');

				$hiddenField.val($(this).attr('data-value'));
				$selectBtn.text($(this).text());
				$selectBtn.attr('data-value', $(this).attr('data-value'));
				if(touchmoved !== true){
					$(this).parent().hide();
		        }

				// Business Unit sub-title to be driven from the Business Unit filter in the billing and payment page
				var billingFilter = $(this).closest('.billing-filters');
				if (billingFilter.length>0) {
					$('#forUnit').attr('data-unit',$(this).attr('data-text'));
					// $('#billingBusinessUnit').text($(this).attr('data-text'));
				}
				e.stopPropagation();
			}).on('touchmove', function(e){
			    touchmoved = true;
			}).on('touchstart', function(){
			    touchmoved = false;
			});

			$('.select-btn.sort').text($('.select-items.sort li[data-selected="selected"]').html());

			// Change value of hidden field to selection
			$('.select-items.sort li').on('click',function(){
			    $('#sortHiddenField').val($(this).attr('data-value'));
			    $('#sort_form').submit();
			});
		});
	},
	filterShowMore: function(){
		var $itemBlocks = $('.list-filter .panel-group');

		$itemBlocks.each(function(){
			var $items = $(this).find('li'),
				$hiddenItems = $(this).find('li:gt(4)'),
				$showMore = $(this).find('.more');

			$hiddenItems.hide();

			if($items.length >= 5){
				$showMore.show();
			}

			$showMore.on('click',function(){
				$hiddenItems.show();
				$showMore.hide();
			});
		});
	},
	accordionToggle: function(){
		$(document).on('click', '.accordion-toggle', function(event) {
			event.stopPropagation();

			var $body = $(this).next('.panel-collapse');

		    $('.panel-collapse').not($body).removeClass('in');
		});
	}
};

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
/* globals window */

/*jshint unused:false*/
/* globals ACC */
/* globals angular */


	'use strict';

	rm.deals = {
	minOrderQty: 1,
	maxOrderQty: 999,
	specificDate: $('.hid-spcific-day').val(),
	rangeFromDate: $('.hid-range-from-day').val(),
	rangeToDate: $('.hid-range-to-day').val(),
	scope:1,
	init: function(){
		this.datepicker();
		this.expand();
		this.toggleDetails();
		this.bindAddtoCart();
		this.bindFilterCheckbox();
		this.checkIfDealsLoaded();
	},
		 
	toggleDetails:function(){
		$('.js-toggleDetails').on('click',function(){
			$(this).children('span').toggleClass('hidden');
		});
	},
	
	//Add the date picker to the date filter
	datepicker: function(){
		var specificDateSelector = $('#datepicker-specific-day');
		if($('.hid-spcific-day').val() !== ''){
			specificDateSelector.datepicker('setValue',null);
			rm.deals.specificDate = $('.hid-spcific-day').val();
			specificDateSelector.datepicker('setStartDate',new Date());
		}else{
			specificDateSelector.datepicker();
			specificDateSelector.datepicker('setStartDate',new Date());
		}
		specificDateSelector.on('changeDate', function() {
		    if(rm.deals.specificDate === specificDateSelector.datepicker('getDate')){
		    	$('.deal-filter .hid-spcific-day').val('');
		    } else{
    	    	$('.deal-filter .hid-spcific-day').val(
    			    	specificDateSelector.datepicker('getDate').getTime()
    	    	);
    	    	angular.element('#dealPageWrapper').scope().deliveryDateChange();
		    }
		    // rm.deals.submitUserChange('specific');
		});
	},
	
	//submit user's selected filter items 
	submitUserChange: function(type) {
		var $form = $('#filter-form');
		//check date type
		if(type === 'range'){
			var fromDate = $form.find('.hid-range-from-day').val();
			var toDate = $form.find('.hid-range-to-day').val();
			if(fromDate !=='' && toDate !==''){
				rm.deals.checkDateBeforeSubmit(type);
				rm.deals.checkCheckboxBeforeSubmit();
				$form.submit();
			}else if(fromDate ==='' && toDate ==='' && rm.deals.rangeToDate !== '' && rm.deals.rangeFromDate !== ''){
				rm.deals.checkDateBeforeSubmit(type);
				rm.deals.checkCheckboxBeforeSubmit();
				$form.submit();
			}
		}else{
			rm.deals.checkDateBeforeSubmit(type);
			rm.deals.checkCheckboxBeforeSubmit();
			$form.submit();
		}
	},
	
	// check the date value ,if the date value is not empty,commit the form with the date.
	checkDateBeforeSubmit: function(type){
		if(type === 'specific'){
			rm.deals.checkSpecificDateBeforeSubmit();
		}else if(type === 'range'){
			rm.deals.checkRangeDateBeforeSubmit();
		}else if(type === 'checkbox' && !rm.deals.checkSpecificDateBeforeSubmit()){
			rm.deals.checkRangeDateBeforeSubmit();
		}
	},
	
	//check the Specific date
	checkSpecificDateBeforeSubmit: function(){
		if($('.deal-filter .hid-spcific-day').val() !== ''){
			$('.deal-filter .hid-spcific-day').attr('name', 'sd');
			$('.deal-filter .hid-range-from-day').removeAttr('name');
			$('.deal-filter .hid-range-to-day').removeAttr('name');
			return true;
		}else{
			return false;
		}
	},
	
	//check the range date of the filter
	checkRangeDateBeforeSubmit: function(){
		if($('.deal-filter .hid-range-from-day').val() !== '' &&  $('.deal-filter .hid-range-to-day').val() !== ''){
			$('.deal-filter .hid-range-from-day').attr('name','fd');
			$('.deal-filter .hid-range-to-day').attr('name','td');
			$('.deal-filter .hid-spcific-day').removeAttr('name');
			return true;
		}else{
			return false;
		}
	},
	
	//check the checkbox of the category or brand filter
	checkCheckboxBeforeSubmit: function(){
		$('.deal-filter .panel-group .checkbox').each(function(){
			var $thisCheckbox = $(this).find('.facet-check');
			if($thisCheckbox.attr('checked')){
				var inputName = $(this).closest('.panel-group').find('.facet-code-name').val();
				$(this).find('.facet-value').attr('name',inputName);
			}
		});
	},
	
	//add the bind to the checkbox
	bindFilterCheckbox: function(){
		$('.deal-filter .panel-group .checkbox').on('change',function(){
			rm.deals.submitUserChange('checkbox');
		});
	},
	
	expand: function() {
		var list = $('.deal-row');
		var hidden = 0;
		var isHidden;

		list.each(function(){
			if($(this).index() > 2) {
				$(this).addClass('deal-overflow');
				hidden ++;
			}
		});

		// $('#hiddenDeals').text(hidden);

		if(hidden >= 1) {
			$('.toggle-deals').show();
		}

		$('.toggle-deals').on('click',function(){
			$('.deal-overflow').toggleClass('open');
			$('.toggle-deals').toggleClass('open');
			rm.deals.moreDealsText(hidden);
		});

		rm.deals.moreDealsText(hidden);
	},
	moreDealsText: function(hidden){
		if($('.toggle-deals').hasClass('open')){
			$('#hiddenDeals').text($('#showLess').val());
		} else {
			$('#hiddenDeals').text($('#showMore').val());
		}

	}, 
	increaseOnly: function(upitem, downitem) {
		upitem.removeClass('disabled');
		downitem.addClass('disabled');
	},
	reduceOnly: function(upitem, downitem) {
		upitem.addClass('disabled');
		downitem.removeClass('disabled');
	},
	
	// calculate the base quantity of the product
	calculateQty: function(obj,qty){
		var $formQty = obj.closest('.row').find('.qty');
		var dealType = obj.closest('.row').find('.dealType').val();
		
		if($formQty.length > 0){
			if(dealType === 'discount'){
				$formQty.val(qty);
			}else if(dealType === 'bundle'){
				$formQty.val($formQty.attr('base-qty')*qty);
			}
		}
	},
	
	// bind the add to cart button
	bindAddtoCart: function(){
		$('.deal-item .addToCartButton').on('click',function(){
			var $addToCartForm = $(this).closest('.deal-item').find('.add_to_cart_form');
			if($addToCartForm.length > 0){
				$.ajax({
					url:$addToCartForm.attr('action'),
					type:'POST',
					data:$addToCartForm.serialize(),
					success: function(result) {
						ACC.product.displayAddToCartPopup(result,null,null,$addToCartForm);
					},
					error:function(result) {
						console.error(result); 
					}
				});
			}

		});
	},
	
	checkIfDealsLoaded: function(){
		var isDealLoadInProgress = $('#dealPageWrapper').attr('data-dealsLoadInProgress');
		
		if(isDealLoadInProgress === 'true')
		{
			rm.utilities.sapCall = true;
			
			$('body').addClass('loading');
			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkDealsRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	}
	
};

/* globals document */
/* globals window */
/* globals ACC */
/* globals trackProductImpressionAndPositionForAdditionalResults */
/*jshint unused:false*/


	'use strict';
	rm.productlisting = {
			currentPath: window.location.pathname,
			//searchParam: window.location.search==='' ? '?q=' : window.location.search.replace('&text=',''),
			//SAB-1121 window.location.search.replace('text=','q=') the search controller JSON response always looking for the 'q' not the 'text'
		searchParam: window.location.search==='' ? '?q=' : (window.location.search.indexOf('q=') ===-1 ? (window.location.search.replace('text=','q=').substr(0, 3)+window.location.search.replace('text=','q=').charAt(3).toUpperCase()+window.location.search.replace('text=','q=').slice(4)):window.location.search),
			//searchParam: window.location.search==='' ? '?q=' : window.location.search,
			infiniteScrollingConfig: {offset: '100%'},
			currentPage: 0,
			processingPage: true,
			numberOfPages: Number.MAX_VALUE,
			baseQuery: $('#sort_form1 input[type="hidden"]').val() || '',

			triggerLoadMoreResults: function ()
			{
				if (rm.productlisting.currentPage < rm.productlisting.numberOfPages && rm.productlisting.processingPage){
					// show the page loader
					rm.productlisting.processingPage = false;
					rm.productlisting.loadMoreResults(parseInt(rm.productlisting.currentPage) + 1);
				}else{
					$('#spinner').remove();
				}
			},

			loadMoreResults: function (page)
			{
				var isProductListPage = $('div#resultsList').length > 0;

				if (isProductListPage)
				{
					$.ajax({
						//url: rm.productlisting.currentPath + '/results?q=' + rm.productlisting.baseQuery + '&page=' + page,
						  url: rm.productlisting.currentPath + '/results'+rm.productlisting.searchParam + rm.productlisting.baseQuery +  '&page=' + page,
						//url: rm.productlisting.currentPath + '/results?q='+ rm.productlisting.baseQuery +  '&page=' + page,
						success: function (data)
						{
							if (data.pagination !== undefined)
							{
								$('div#resultsListRow').append($.tmpl($('#resultsListItemsTemplate'), data));
								ACC.product.bindToAddToCartForm({enforce: true});
								ACC.product.addListeners();
								rm.productlisting.updatePaginationInfos(data.pagination);

								rm.utilities.needClamp('need-clamp2',2,'clamp-2');
								rm.utilities.needClamp('need-clamp1',1,'clamp-1');
							}

							  $('[data-toggle="tooltip"]').tooltip({
				                trigger: 'hover click',
				                placement: 'auto',
				                html: true
				              });

				              $('.regular-popup').magnificPopup({
                                  type:'inline',
                                  removalDelay: 500,
                                  mainClass: 'mfp-slide',
                                  closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
                              });

				             data.results.forEach(function(result){
                                 var deliveryDatePackType = $('[name=deliveryDatePackType]').val();
                                 var bdeUser = $('[name=bdeUser]').val();
                                 var unit = result.unit;
                                 if (result.uomList !== null && result.uomList.size !== 0) {
                                    unit = result.uomList[0].name;
                                 }
                                 unit = (unit!== null && unit.toUpperCase() === 'KEG') ? 'KEG' : 'PACK';
                                 var isProductPackTypeAllowed = deliveryDatePackType.indexOf(unit) !== -1 ? true : false;
                                 if (!isProductPackTypeAllowed){
                                    $('div#productPackTypeNotAllowed'+result.code).addClass('disabled-productPackTypeNotAllowed');
                                    $('#addToCart'+result.code).addClass('hidden');
                                    $('#changeDeliveryDate'+result.code).removeClass('hidden');
                                    if (bdeUser!== undefined && !bdeUser) {
                                        $('div#productQtyNotAllowed'+result.code).addClass('disabled-productPackTypeNotAllowed');
                                    }
                                 }

				                $('#addRecommendationText'+result.code).on('click',function(){
                                     var addToCartForm = $(this).closest('.add_to_cart_form');
                                     var quantityField = $(this).closest('.addtocart-qty');
                                     var productCode = $('[name=productCodePost]', addToCartForm).val();
                                     var quantityValue = $('[name=qty]', addToCartForm).val();
                                     var uom = $('[name=unit]', addToCartForm).val();
                                     var dataPost = {'productCodePost': productCode,
                                                     'qty': quantityValue,
                                                     'unit': uom};
                                     var recommendationAction = $(this);

                                     $.ajax({
                                         url:'/sabmStore/en/recommendation/add',
                                         type:'POST',
                                         dataType: 'json',
                                         data: JSON.stringify(dataPost),
                                         contentType: 'application/json',
                                         success: function(result) {
                                            console.log('recommendations:' + result);
                                            rm.recommendation.displayAddToRecommendationPopup(result);
                                            $(recommendationAction).find('#recommendationStar').removeClass('icon-star-normal').addClass('icon-star-add');
                                            $(recommendationAction).find('#recommendationText').html($('#addedText').html());
                                            rm.recommendation.displayAddToRecommendationPopup(result);
                                            setTimeout(function ()
                                            {
                                            	$(recommendationAction).find('#recommendationStar').removeClass('icon-star-add').addClass('icon-star-normal');
                                                $(recommendationAction).find('#recommendationText').html($('#addText').html());
                                                $(quantityField).find('.qty-input')[0].value = 1;
                                                $('[name=qty]', addToCartForm)[0].value = 1;
                                            }, 5000);
                                         },
                                         error:function(result) {
                                             console.error(result);
                                         }
                                     });

                                 });
				             });


							$('#spinner').remove();
						}
					});
					//remove for cancel to add the event to the change quantity button
					//rm.cart.QtyIncrementors();
				}
			},

			updatePaginationInfos: function (paginationInfo)
			{
				rm.productlisting.currentPage = parseInt(paginationInfo.currentPage);
				rm.productlisting.numberOfPages = parseInt(paginationInfo.numberOfPages);
				rm.productlisting.processingPage = true;
			},

			bindSortingSelector: function ()
			{
				$('#sort_form1, #sort_form2').change(function ()
				{
					this.submit();
				});
			},

			checkIfCupLoaded: function(){
				var isCupInProgress = $('#productListPageCupLoad').attr('data-cupRefreshInProgress');

				if(isCupInProgress === 'true')
				{
					rm.utilities.sapCall = true;

					//$('body').addClass('loading');

					console.log('Cup loading is in progress');
					setInterval(function(){
						$.ajax({
						url:'/b2bunit/checkCupRefreshStatus',
						type:'GET',
						success: function(result) {
							var jsonObj = JSON.parse(result);
							if(jsonObj.status === 'false')
							{
								window.location.reload();
							}
						},
						error:function() {
							console.log('Error occured while checking deal refresh status');
						}
					});
					}, 5000);
				}
			},

			initialize: function()
			{
				rm.productlisting.bindSortingSelector();
				//rm.productlisting.checkIfCupLoaded();
			},

			init: function ()
			{
				rm.productlisting.initialize();

				/* The addListeners() function should only be called in this init() function
				 * and should not be included in the document.ready() function. Otherwise, the
				 * addListeners() function will be called twice. */
				ACC.product.addListeners();
			}

		};

		$(document).ready(function ()
		{
			rm.productlisting.initialize();
		});
/* globals document */
/* globals window */
/* globals ACC */
/*jshint unused:false*/
/*author yuxiao.wang*/

'use strict';
rm.forgotpassword = {

	init : function() {
		this.bindForgotPasswordInput($('#forgottenPwd_email'));
		this.bindForgotPasswordDocument();
	},

	//show the error message and change color
	show : function() {
		$('#forgottenPwd_email').css('border', '1px solid red');
		$('#forgottenPwd_label').css('color', 'red');
		$('#invalidEmail').show();
		$('#emailNotFound').hide();
	},

	//hide the error message and change color
	hide : function() {
		$('#forgottenPwd_email').removeAttr('style');
		$('#forgottenPwd_label').removeAttr('style');
		$('#invalidEmail').hide();
		$('#emailNotFound').hide();
	},

	bindForgotPasswordDocument : function() {
		//Enter event
		$('#forgottenPwd_email').bind('keypress', function(event) {
			if (event.keyCode === 13) {
				var email = $('#forgottenPwd_email');
				//add validate to email by yuxiao
				if (!rm.customer.emailInvalid(email.val())) {
				    rm.forgotpassword.emailPresentSubmit(email.val());
					return false;
				} else {
					rm.forgotpassword.show();
					return false;
				}
			}
		});
	},

	/* keep it. Does not need it now, might be used in the future
	emailPresent: function(email)
    {
        email = $.trim(email);
        $.get('/login/pw/forgot/validateEmail',{email:email},function(returned)
            {
                if (returned.valueOf() === 'INVALID') {

                    $('#forgottenPwd_email').css('border', '1px solid red');
                    $('#forgottenPwd_label').css('color', 'red');
                    $('#emailNotFound').show();
                } else {
                    $('#forgottenPwd_email').removeAttr('style');
                    $('#forgottenPwd_label').removeAttr('style');
                    $('#invalidEmail').hide();
                    $('#emailNotFound').hide();
                }
            });
    },
    */

	emailPresentSubmit: function(email)
    {
        email = email || $.trim($('#forgottenPwd_email').val());
        if (!rm.customer.emailInvalid(email)) {
            $.magnificPopup.open({
                items: {
                  src: '#forgotpwd-popup',
                  type: 'inline'
                },
                callbacks: {
                    open: function() {
                        $('#submit_button').mousedown(function(e) {
                            $.magnificPopup.close();
                            setTimeout(function() {
                                $('#forgottenPwdForm').submit();
                            }, 100);
                        });
                    },
                }
            });
        } else {
            rm.forgotpassword.show();
        }
    },
	bindForgotPasswordInput : function(input) {
		//the blur event of input
		input.blur(function() {
			//add validate to email by yuxiao
			if (!rm.customer.emailInvalid(input.val())) {
				rm.forgotpassword.hide();
			} else {
				rm.forgotpassword.show();
			}
		});

        input.focus(function() {
            rm.forgotpassword.hide();
        });
	}
};
/* globals window */
/* globals ACC */
/* globals angular */


'use strict';


rm.cart = {

	tenMinutesInterval: null,

	init:function() {
		this.recalculate();
        this.sabmCheckOut();
        this.clearCart();
        this.saveOrderTemplate();
        this.deliveryListener();
        this.changeDelivery();
        this.chooseUpdateFunction();
        this.quickInit();
	},

	quickInit: function(){
		this.bingCartEntry();
		this.removeItemBind();
		this.chooseFreeProduct();
		this.seeDeal();
		rm.modals.init();
		rm.cart.updatable=true;

		ACC.product.addListeners();
	},

	deliveryListener: function(){
		$('#deliveryInstructions').on('click',function(){
			$('.delivery-instructions').slideDown(300);
		});
	},
	//show "See deal" on mobile
	seeDeal: function(){
		$('.see-deal-link').on('click',function(){
			var link = $(this),
				content = $(this).closest('.row').find('div#js-see-deal-title');

			link.toggleClass('open');
			if(link.hasClass('open')){
				content.show();
			} else {
				content.hide();
			}
		});
	},
	removeItemBind: function(){

		$('.submitRemoveProduct').on('click', function (event){ // On Delete
			event.preventDefault();

			$('#checkoutNotAllowed .checkoutNotAllowed').hide();
			$('.checkoutButton').removeClass('disabled');

			var prodid = $(this).data('index'),
				form = $('#updateCartForm' + prodid),
				cartQuantity = form.find('input[name=quantity]'),
				entryNumber = form.find('input[name=entryNumber]').val(),
				unit = form.find('input[name=unit]').val(),
				elementWithProductData = $(this).closest('.cartRow').find('.js-track-product-link');

			if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
				var productObj = new rm.tagManager.ProductForTrackCart(
						$(this).data('currencycode'),
						$(this).data('name'),
						$(this).data('id'),
						$(this).data('price'),
						$(this).data('brand'),
						$(this).data('category'),
						$(this).data('uomlist')[unit],
						$(this).data('position'),
						$(this).data('dealsflag'),
						cartQuantity.val(),
						$(this).data('actionfield'));

				if (typeof rm.tagManager.trackCart !== 'undefined') {
					rm.tagManager.trackCart(productObj, 'remove');
				}
			}

			cartQuantity.attr('value',0);

			$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
				$('body').removeClass('loading');
				if(result.isLost){
						//$('#loseDealPopup h3').text(result.title);

						var titles = result.title;
						var resultTitleHtml = '';
                   	 	var productsInLoseDealModal = [];
						if(titles){
							 for (var i = 0; i < titles.length; i++) {
								 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                            	 productsInLoseDealModal.push({
         			        		'name'		: elementWithProductData.data('name'),
         			            	'id'		: elementWithProductData.data('id'),
         			            	'price'		: elementWithProductData.data('price'),
         			            	'brand'		: elementWithProductData.data('brand'),
         			            	'category'	: elementWithProductData.data('category'),
         			            	'variant'	: elementWithProductData.data('variant'),
         			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
         			            	'position'	: (i+1),
         			            	'dealsFlag'	: true
         			        	});
	                        }
				        }
						$('#loseDealPopup h3').html(resultTitleHtml);

						rm.cart.loseDealPopup(form,'remove');

                        if (productsInLoseDealModal.length > 0) {
             				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
             					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
             				}
             	        }

						$('#loseDealPopup').attr('data-item', prodid);
						$('#loseDealPopup').attr('data-qty', cartQuantity.val());
				} else {
						if($('.cartRow').length > 1){
							rm.cart.resetCart();
						} else {
							rm.cart.refreshPage();
						}

						rm.cart.showRecalculate();
						$('#totals-section').addClass('hidden');
				}
			});
		});
	},

	chooseFreeProduct: function(){
		$('.chooseFreeProduct').on('click', function (e){ // On Delete
			e.preventDefault();

			angular.element('#cartCtrl').scope().chooseFreeProduct($(this).data('deal-code'));
		});
	},

	loseDealPopup: function(form,modal){

		if(modal === 'remove'){
			$.magnificPopup.open({
				items:{
			   src: '#loseDealPopup',
			   type: 'inline'
				},
			   removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true,
			   callbacks:{
				   open: function(){
					   rm.tagManager.addDealsImpressionAndPosition('Viewed', 'LoseDeal');
				   },
				   close: function(){
				   }
			   }
			});
		} else {
			$.magnificPopup.open({
				items:{
			   src: '#loseDealPopupReduce',
			   type: 'inline'
				},
			   removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true
			});
		}

	},

	resetCart: function(){

		$('body').addClass('loading');
    	$.ajax({
			url:'/cart',
			type:'GET',
			success: function(result) {

				$.magnificPopup.close();
                $('.cart').html($(result).find('[class=cart]').html());
                $('#orderTotals').html($(result).find('[id=orderTotals]').html());
                $('#simulationErrors').html($(result).find('#simulationErrors').html());
                $('#globalMessage').html($(result).find('[id=globalMessage]').html());

				rm.cart.quickInit();
				ACC.minicart.refreshMiniCartCount();
				$('body').removeClass('loading');
			},
			error:function() {
				console.log('error resetting cart');
				$('body').removeClass('loading');
			}

		});
	},

	checkLoseDeal: function(entry, qty,unit){
		var data = {
			entryNumber: entry,
			quantity: qty.val(),
			uom: unit
		};

		$('body').addClass('loading');

		return $.ajax({
			url:'/sabmStore/en/cart/isLostDeal',
			type:'POST',
			data:data,
			success: function (result) {
			    if (result.hasOwnProperty('newQty') && result.newQty !== null) {
			        var text = result.newQty.indexOf('maxOrderQuantityExceeded:') !== -1 ? result.newQty.split(':')[1] : '';
                    $('.order-error-message-' + entry).text(text);
			    }
            },
            error: function (error) {
                // close block ui
                $.unblockUI();
            }
		});
	},

	// show Re-calculate button
	showRecalculate: function() {
		if($('.checkoutButton').attr('data-sap-disable') !== 'true') {
			$('.checkoutButton').hide();
			$('.doCartBut').show();
		}
	},
	// show Checkout button
	showCheckout: function() {
		$('.checkoutButton').show();
		$('.doCartBut').hide();
	},
	getRecalculatedData: function(){
		if($('.checkoutButton').attr('data-sap-disable') !== 'true') {
			$('.breadcrumb').addClass('inactive');

			rm.utilities.loadingMessage($('.loading-message').data('simulate'),true);
			$('body').addClass('loading');
			rm.utilities.sapCall = true;

	    	$.ajax({
				url:'/cart/orderSimulation',
				type:'POST',
				success: function(result) {
					if ($(result).find('[class=cart-body]').length > 0) {
						$('#cartDealsData').html($(result).find('#cartDealsData').html()); // Replace cart angular data

						$('.deals-listing').html($(result).find('.deals-listing').html());
						$('.cart').html($(result).find('[class=cart]').html()); // Replace cart body
	                    $('#orderTotals').html($(result).find('[id=orderTotals]').html()); // Replace totals table
	                    $('#deal-notification-bad').html($(result).find('[id=deal-notification-bad]').html());
	                    $('#deal-notification-bad').attr('class', $(result).find('[id=deal-notification-bad]').attr('class'));
	                    $('#deal-notification-auto').html($(result).find('[id=deal-notification-auto]').html());
	                    $('#deal-notification-auto').attr('class', $(result).find('[id=deal-notification-auto]').attr('class'));

	                    $('#minFreight').html($(result).find('#minFreight').html()); // Replace cart angular data
						$('#simulationErrors').append($(result).find('[id=simulationErrors]').html());
	                    $('#globalMessage').html($(result).find('[id=globalMessage]').html());

						if(!$('#globalMessage .alert.server-error').not('.hidden').length && !$('#simulationErrors .alert.negative').length) {
							rm.cart.showCheckout();
						} else {
							rm.cart.showRecalculate();
							rm.utilities.goBackTop();
						}
						//angular.element('#cartCtrl').scope().init(false); // Rerun angular cart controller
						rm.cart.quickInit();
						//ACC.minicart.refreshMiniCartCount();
						$('.miniCart .count').html($('.miniCart .count', result).html());
						$('.cart-mobile .count').html($('.cart-mobile .count', result).html());

						$('.breadcrumb').removeClass('inactive');

						$('.doCartBut').text('Recalculate');
						$('#totals-section').removeClass('hidden');
						$('body').removeClass('loading');
					} else {
						rm.cart.refreshPage();
					}

                },
				error:function() {
					$('.breadcrumb').removeClass('inactive');
					$('body').removeClass('loading');
					$('#globalMessage .alert').removeClass('hidden');
					$('.doCartBut').text('Recalculate');
					rm.cart.showRecalculate();
					rm.utilities.goBackTop();
					rm.cart.removeItemBind();
				}

			});
		}


	},

	// Re-calculate button add click event
	recalculate : function() {
		$('.doCartBut').on('click touch', function() {
			//$('.doCartBut').text('Calculating..');
			//$('#simulationErrors').html('');
			//rm.cart.getRecalculatedData();
			rm.cart.refreshPage();
		});
	},

	// clear cart add click event
	clearCart: function() {
			$('.clear-cart-popup .btn-primary').on('click', function() { // This is firing twice in some cases
					var $productElements = $('span.inline.submitRemoveProduct:not(.visible-xs-block):not(.visible-sm-block)'),
						productElement,
						productObj,
						prodid,
						form,
						cartQuantity,
						unit;

					for (var i=0; i<$productElements.length; i++) {
						productElement = $productElements[i];

						prodid = $(productElement).data('index');
						form = $('#updateCartForm' + prodid);
						cartQuantity = form.find('input[name=quantity]');
						unit = form.find('input[name=unit]').val();

						if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
							productObj = new rm.tagManager.ProductForTrackCart(
									$(productElement).data('currencycode'),
									$(productElement).data('name'),
									$(productElement).data('id'),
									$(productElement).data('price'),
									$(productElement).data('brand'),
									$(productElement).data('category'),
									$(productElement).data('position'),
									$(productElement).data('dealsflag'),
									cartQuantity.val(),
									$(productElement).data('actionfield'));

							if (typeof rm.tagManager.trackCart !== 'undefined') {
								rm.tagManager.trackCart(productObj, 'remove');
							}
						}

					}

					window.location.href = $('#cartClearUrl').val();
			});
	},
    /**
     * levelNumber: number of parents level up
     */
    hidePrice: function($obj) {

    	$obj.closest('.cartRow').find('.total').html('&mdash;');
    	$obj.closest('.cartRow').find('.text-normal').hide();
    	// hide Price summary table
    	$('#totals-section').addClass('hidden');
    },

	bingCartEntry: function(){
		rm.utilities.needClamp('cartItemClamp-2',2,'clamp-2');
		rm.utilities.needClamp('cartItemClamp-1',1,'clamp-1');
		// Quantity Incrementors
		$('.cartRow .select-quantity .up').on('click touchstart',function(){
			if(rm.cart.updatable){
				var $input = $(this).closest('.select-quantity').find('.qty-input');

				if($input.val() < 999){
					var entryLoopIndex = $(this).closest('.cartRow').find('.entry-loop-index').val();
					var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
					$newQuantity.val(parseInt($input.val()) + 1);

					rm.cart.updateQuantityOrUnit($(this), false);
					rm.cart.showRecalculate();
				}
			}

			//hidePrice
			rm.cart.hidePrice($(this));

		});

		$('.cartRow .select-quantity .down').on('click touchstart',function(){
			if(rm.cart.updatable && !$(this).hasClass('disabled')){
				var that = $(this),
					prodid = that.closest('.cartRow').data('index'),
					$input = that.closest('.select-quantity').find('.qty-input'),
					form = $('#updateCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					// fromIndex = form.find('input[name=fromIndex]').val(),
					entryNumber = form.find('input[name=entryNumber]').val(),
					unit = form.find('input[name=unit]').val(),
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

				cartQuantity.val(parseInt($input.val())-1);
	            rm.cart.hidePrice($(this));

				$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
					$('body').removeClass('loading');
					if(result.isLost){
                            //$('#loseDealPopupReduce h3').text(result.title);
                             var titles = result.title;
                             var resultTitleHtml = '';
                        	 var productsInLoseDealModal = [];
                             if(titles){
                                 for (var i = 0; i < titles.length; i++) {
                                     resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                                	 productsInLoseDealModal.push({
             			        		'name'		: elementWithProductData.data('name'),
             			            	'id'		: elementWithProductData.data('id'),
             			            	'price'		: elementWithProductData.data('price'),
             			            	'brand'		: elementWithProductData.data('brand'),
             			            	'category'	: elementWithProductData.data('category'),
             			            	'variant'	: elementWithProductData.data('variant'),
             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
             			            	'position'	: (i+1),
             			            	'dealsFlag'	: true
             			        	});
                                 }
                            }
                            $('body').removeClass('loading');

                            $('#loseDealPopupReduce h3').html(resultTitleHtml);
                            $input.val(parseInt($input.val()));
                            rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

                            //$('#loseDealPopupReduce').attr('data-item', entryNumber);
                            $('#loseDealPopupReduce').attr('data-item', prodid);
                            $('#loseDealPopupReduce').attr('data-qty', $input.val());
                    } else {
                    		$('body').removeClass('loading');
                    		console.log(form.find('.base-quantity'));
                    		rm.cart.changeBaseQuantity(result.newQty,that);
                            form.find('#initialQuantity' + prodid).val(parseInt($input.val()));
                    		// rm.cart.resetCart();
                            rm.cart.showRecalculate();

                            var entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val();
                            $('#maxorderqty'+entryLoopIndex).html('');
                    }

				});
			}
		});

		// Quantity input. If the user input 0,set the quantity to 1.
		$('.cartRow .select-quantity input').each(function(){
			if(rm.cart.updatable){
				var that = $(this);
				var typingTimer;                //timer identifier
				var doneTypingInterval = 1500;  //time in ms, 5 second for example

				//on keyup, start the countdown
				$(this).on('keyup', function () {
				  clearTimeout(typingTimer);

				  typingTimer = setTimeout(doneTyping, doneTypingInterval);

				});

				//on keydown, clear the countdown
				$(this).on('keydown', function (event) {
					if(event.keyCode === 13){
						return false;
					}
					clearTimeout(typingTimer);
				});

				//user is "finished typing," do something
				var doneTyping = function () {
				  var prodid = that.closest('.cartRow').data('index'),
				  	form = $('#updateCartForm' + prodid),
				  	cartQuantity = form.find('input[name=quantity]'),
				  	tempQuantity = parseInt(that.val(),10),
				  	//fromIndex = form.find('input[name=fromIndex]').val(),
				  	entryNumber = form.find('input[name=entryNumber]').val(),
				  	unit = form.find('input[name=unit]').val(),
				  	notIsLost = false,
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

				  	rm.cart.hidePrice(that);

	  				if(isNaN(tempQuantity)){
	  					notIsLost = false;
	  				  	tempQuantity = cartQuantity.val();
	  				  	that.val(tempQuantity);
	  				}else if(tempQuantity <= 0){
	  					notIsLost = true;
	  					that.val(parseInt(1));
	  				}else{
	  					that.val(tempQuantity);
	  				}

	  				cartQuantity.val(tempQuantity);

	  				if(!notIsLost){
	  					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
	  						$('body').removeClass('loading');
	  						if(result.isLost){
	  								//$('#loseDealPopupReduce h3').text(result.title);
	  								var titles = result.title;
	  								 var resultTitleHtml = '';
	  								var productsInLoseDealModal = [];
	  								 if(titles){
	  									 for (var i = 0; i < titles.length; i++) {
	  										 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

	  										 productsInLoseDealModal.push({
	  	             			        		'name'		: elementWithProductData.data('name'),
	  	             			            	'id'		: elementWithProductData.data('id'),
	  	             			            	'price'		: elementWithProductData.data('price'),
	  	             			            	'brand'		: elementWithProductData.data('brand'),
	  	             			            	'category'	: elementWithProductData.data('category'),
	  	             			            	'variant'	: elementWithProductData.data('variant'),
	  	             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
	  	             			            	'position'	: (i+1),
	  	             			            	'dealsFlag'	: true
	  	             			        	 });
	                                      }
	  						        }
	  								$('#loseDealPopupReduce h3').html(resultTitleHtml);

	  								rm.cart.loseDealPopup(form,'reduce');

	  	                            if (productsInLoseDealModal.length > 0) {
	  	                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
	  	                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
	  	                 				}
	  	                 	        }

	  								$('#loseDealPopupReduce').attr('data-item', prodid);
	  								$('#loseDealPopupReduce').attr('data-qty', that.val());
	  						} else {
	  							var entryLoopIndex,
	  								$newQuantity;

	  							var tempCartEntryQty = cartQuantity.val();
	  						    that.val(tempCartEntryQty);

	  							entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val();
	  							$newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
	  							$newQuantity.val(that.val());
	  							form.find('.base-quantity span').text(result.newQty);
	  							rm.customUI.checkChangeable(that);
	  							rm.cart.showRecalculate();
	  							if(parseInt(tempCartEntryQty) > parseInt(result.newQty))
	  							{
	  								console.log("Inside If parseInt(tempCartEntryQty) > parseInt(result.newQty) ");
	  								$('#maxorderqty'+entryLoopIndex).html('<span class="error">The maximum quantity available of this product to order is ' + result.newQty+ '</span>');
	  						     }else{
	  						    	console.log("Inside else parseInt(tempCartEntryQty) > parseInt(result.newQty) ");
	  						            $('#maxorderqty'+entryLoopIndex).html('');
	  						     }

	  						}
	  					});
	  				}
				};
			}
		});

		// Change value of hidden field to selection
		$('.cartRow .select-items li').on('click', function(){
			if(rm.cart.updatable){
				var that = $(this),
					prodid = that.closest('.cartRow').data('index'),
					input = that.closest('.cartRow').find('.qty-input'),
					form = $('#updateCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					entryLoopIndex = that.closest('.cartRow').find('.entry-loop-index').val(),
					unit = that.data('value'),
					selectBtn = that.closest('.select-list').find('.select-btn'),
					$updateEntryUnit = that.closest('.cartRow').find('#updateEntryUnit'+entryLoopIndex),
					elementWithProductData = $(that).closest('.cartRow').find('.js-track-product-link');

					selectBtn.text($(this).text());

					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
						$updateEntryUnit.val(that.attr('data-value'));

						$('body').removeClass('loading');

						$('.select-items').hide();

						if(result.isLost){
							// $('#loseDealPopupReduce h3').text(result.title);

							var titles = result.title;
							var resultTitleHtml = '';
							var productsInLoseDealModal = [];
							if(titles){
								 for (var i = 0; i < titles.length; i++) {
									 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

									 productsInLoseDealModal.push({
             			        		'name'		: elementWithProductData.data('name'),
             			            	'id'		: elementWithProductData.data('id'),
             			            	'price'		: elementWithProductData.data('price'),
             			            	'brand'		: elementWithProductData.data('brand'),
             			            	'category'	: elementWithProductData.data('category'),
             			            	'variant'	: elementWithProductData.data('variant'),
             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
             			            	'position'	: (i+1),
             			            	'dealsFlag'	: true
             			        	 });
		                        }
					        }
							$('#loseDealPopupReduce h3').html(resultTitleHtml);
							rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

							$('#loseDealPopupReduce').attr('data-item', prodid);
							$('#loseDealPopupReduce').attr('data-qty', input.val());
						} else {
							form.find('.base-quantity span').text(result.newQty);
							// rm.cart.resetCart();
							rm.cart.hidePrice($(that));
							rm.cart.showRecalculate();
						}
					});
			}
		});
	},

	chooseUpdateFunction: function(){
		$('.lose-deal-popup .btn-primary').one('click',function(){
			var modal = $(this).closest('.lose-deal-popup'),
				item = modal.attr('data-item'),
				form = $('#updateCartForm' + item),
				entryLoopIndex = form.closest('.cartRow').find('.entry-loop-index').val(),
				initialQuantity = $('#initialQuantity'+entryLoopIndex).val(),
				cartQuantity = form.find('input[name=quantity]').val(),
				$elementWithProductData = form.closest('.cartRow').find('.js-track-product-link');

			    console.log('binding click');
				rm.cart.updateQuantityOrUnit(form, true);

				if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
					var productObj = new rm.tagManager.ProductForTrackCart(
							$elementWithProductData.data('currencycode'),
							$elementWithProductData.data('name'),
							$elementWithProductData.data('id'),
							$elementWithProductData.data('price'),
							$elementWithProductData.data('brand'),
							$elementWithProductData.data('category'),
							$elementWithProductData.data('variant'),
							$elementWithProductData.data('position'),
							true,
							(initialQuantity !== cartQuantity ? parseInt(initialQuantity) - parseInt(cartQuantity) : initialQuantity),
							$elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal');

					if (typeof rm.tagManager.trackCart !== 'undefined') {
						rm.tagManager.trackCart(productObj, 'remove');
					}
				}
		});

	},

	refreshPage: function(){
        setTimeout(function (){
            $(window)[0].location.reload();
        }, 500);
	},

	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj, refresh){
		var entryLoopIndex = obj.closest('.cartRow').find('.entry-loop-index').val();
		var $input = obj.closest('.select-quantity').find('.qty-input');
		var $form = $('#updateCartForm'+entryLoopIndex);
		var $selectBtn = obj.parent().siblings('.select-btn');

		var $initialQuantity = $('#initialQuantity'+entryLoopIndex);
		var $initialUnit = $('#initialUnit'+entryLoopIndex);
		var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
		var $newUnit = $('#updateEntryUnit'+entryLoopIndex);
		var $displayQuantity = $('#displayQuantity'+entryLoopIndex);
		//if the quantity is not change and the unit is not change will not do the ajax

		console.log($initialQuantity.val() + ' ' + $newQuantity.val());
		if($initialQuantity.val() !== $newQuantity.val() || $initialUnit.val() !== $newUnit.val()){

			$('body').addClass('loading');
			rm.cart.hidePrice(obj);
			rm.cart.updatable = false;
			$input.attr('disabled', true);
			$.ajax({
				url:$('#cartUpdateQuantityUrl').val(),
				type:'POST',
				data:$form.serialize(),
				success: function(result) {
					if(result !== null){
					    // render max quantity order error message
                        var text = result.indexOf('maxOrderQuantityExceeded:') !== -1 ? result.split(':')[1] : '';
                        $('.order-error-message-' + entryLoopIndex).text(text);

						$initialQuantity.val($newQuantity.val());
						$initialUnit.val($newUnit.val());
						$input.removeAttr('disabled');
						$displayQuantity.val($newQuantity.val());
						$selectBtn.text(obj.text());
						$selectBtn.attr('data-value',obj.attr('data-value'));
						rm.cart.changeBaseQuantity(result,obj);
						rm.cart.updatable = true;

						var tempCartEntryQty = $newQuantity.val();
                        $initialQuantity.val(parseInt(result));
                        $displayQuantity.val(parseInt(tempCartEntryQty));
                        if(parseInt(tempCartEntryQty) > parseInt(result)){
                        	console.log("Inside If parseInt(tempCartEntryQty) > parseInt(result)");
                            $('#maxorderqty'+entryLoopIndex).html('<span class="error">The maximum quantity available of this product to order is ' + result + '</span>');
                        }

						rm.cart.showRecalculate();
						rm.cart.removeItemBind();
						ACC.minicart.refreshMiniCartCount();
						$.magnificPopup.close();
					}
				},
				complete:function(){
					if(refresh) {
						//rm.cart.getRecalculatedData();
						rm.cart.refreshPage();
					} else {
						$('body').removeClass('loading');
					}
				}
			});
		}
	},

	//if customer changed the quantity or the unit the baseQuantity will changed.
	changeBaseQuantity: function(quantity,obj){
		var productBaseUnit = obj.closest('.cartRow').find('.entry-product-unit').val();
		var productBasePluralUnit = obj.closest('.cartRow').find('.entry-product-plural-unit').val();
		var $section = obj.closest('.cartRow').find('.base-quantity');
		var baseUOM = obj.closest('.cartRow').find('.base-quantity').attr('data-base-unit');
		var currentUOM = obj.closest('.cartRow').find('.select-btn.sort').attr('data-value');
		//var input = obj.closest('.cartRow').find('.qty-input').val();

		if(baseUOM === currentUOM) {
			$section.hide();
		} else {
			$section.show();
		}

        var qtyHtml = '<span>' + quantity + '</span>' + '&nbsp;';
		if(parseInt(quantity) === 1){
			$section.html(qtyHtml + productBaseUnit);
		}else if(parseInt(quantity) > 1){
			console.log(parseInt(quantity));
			$section.html(qtyHtml + productBasePluralUnit);
		}
	},

	//if customer select the address will changed.
	changeDelivery: function(){
		// Customer carriers
		$('#customerArrangedDelivery .select-items li').on('click touchstart', function(){

			 $('body').addClass('loading');
			 var shippingCarrierCode = $(this).attr('data-value');
		     $.ajax({
					url:$('#updateSABMdeliveryUrl').val(),
					type:'POST',
					data:{delmodeCode:null,carrierCode:shippingCarrierCode},
					success: function() {
						rm.cart.showRecalculate();
					},
					complete: function() {
						// Sync/Show calendar after ajax finish to load
						rm.datepickers.syncCalendarPicker('carrier');
					},
					error:function(result) {
						console.error(result);
					}
				}).always(function() {
					$('body').removeClass('loading');
				});
		});
		// Delivery methods
		$('#deliveryMethod input[name="deliveryMethod"]').on('change', function(){
			$('body').addClass('loading');

			$('.datepicker').remove();

			var shippingOption = 'CUB Arranged';
			var shippingCarrierCode = $('#customerArrangedDelivery .select-list .select-btn').attr('data-value');
			if(shippingCarrierCode===''){
				shippingCarrierCode = $('#customerArrangedDelivery .select-items li').attr('data-value');
				shippingOption = 'Customer Arranged';
			}

			var step =  $('input[name="checkoutStep"]').val();
			if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
				rm.tagManager.trackOnCheckoutOption(step, 'Shipping|' + shippingOption);
			}

			 var deliveryModeCode = this.value;
		     $.ajax({
					url:$('#updateSABMdeliveryUrl').val(),
					type:'POST',
					data:{addressId:null,delmodeCode:deliveryModeCode,carrierCode:shippingCarrierCode},
					success: function() {
						rm.cart.showRecalculate();
					},
					complete: function() {
						// Sync/Show calendar after ajax finish to load
						rm.datepickers.syncCalendarPicker('deliveryMode');
					},
					error:function(result) {
						console.error(result);
					}
				}).always(function() {
					$('body').removeClass('loading');
				});

		});
	},

	sabmCheckOut: function() {
	    $('document').ready(function(){
	        $('div', '#simulationErrors').each(function(){
	        	if (typeof rm.tagManager.trackCheckoutError !== 'undefined') {
	        		rm.tagManager.trackCheckoutError($(this).html());
	        	}
	        });
	        $('div', '#globalMessage').each(function(){
	        	if (typeof rm.tagManager.trackCheckoutError !== 'undefined') {
	        		rm.tagManager.trackCheckoutError($(this).html());
	        	}
            });

		    if($('#cartCtrl').length >0){
		        $('div.cart-deliverydate .form-control.cart-datepicker').datepicker()
		        .on('changeDate',function(){
		        	var date = $(this).datepicker('getDate');

		        	var formattedDate = $.datepicker.formatDate('DD dd/mm/yy', date);
		        	var step =  $('input[name="checkoutStep"]').val();
		        	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
		        		rm.tagManager.trackOnCheckoutOption(step, 'Delivery Date|' + formattedDate);
		        	}
		        });
		    }

		    if ($('#checkoutNotAllowed').length > 0) {
		    	if (!rm.cart.areAllProductPackTypesAllowed()) {
		    		$('#checkoutNotAllowed .checkoutNotAllowed').show();
		    		$('.checkoutButton').addClass('disabled');
		    	}
		    	else {
		    		$('#checkoutNotAllowed .checkoutNotAllowed').hide();
		    		$('.checkoutButton').removeClass('disabled');
		    	}
		    }
	    });

		$('.checkoutButton').on('click', function(){
			if (!rm.cart.areAllProductPackTypesAllowed()) {
				$('#checkoutNotAllowed .checkoutNotAllowed').show();
				$('.checkoutButton').addClass('disabled');
			}
			else {
				$('#checkoutNotAllowed .checkoutNotAllowed').hide();
				$('.checkoutButton').removeClass('disabled');
				rm.utilities.loadingMessage($('.loading-message').data('checkout'),true);
				$('.cartdDeliveryInstructions').attr('value', $('#deliveryInstructionsinfo').val());
				$('._sabmcheckoutForm').submit();
			}
		});
	},

	// check if pack types of all products in the cart are allowed for the selected delivery date
	areAllProductPackTypesAllowed: function() {
		if ($('div.cartRow .disabled-productPackTypeNotAllowed').length > 0) {
			return false;
		} else {
			return true;
		}
	},

	saveOrderTemplate: function() {

		$('.saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			e.preventDefault();

			if($('#template-name').val().trim() !== '') {

				var dataPost = {'name':$('#template-name').val()};
				$.ajax({
					url:'/cart/saveOrderTemplate',
					type:'POST',
                    contentType: 'application/json; charset=utf-8',
					data: JSON.stringify(dataPost),
					success: function(response) {
						$('#globalMessage').html($('#simulationErrors', response).html());
						$('#template-name').val('');
						$('.magnific-template-order').magnificPopup('close');

						rm.utilities.goBackTop();
					},
					error:function(result) {
						console.error(result);
					}
				});
			}else{
				$('#empty-msg').removeClass('hidden');
			}
		});
	},

    findOutMore:function () {
        $('#_sabmFindOutMoreForm').submit();
    }
};
/*jshint unused:false*/
/* globals window */
/* globals validate */
/* globals document */
/* globals trackOnCheckoutOption */
/* globals trackOnCheckout */
/* globals trackCheckoutError */
/*globals MerchantSuite*/
/*globals test*/
'use strict';
    
rm.checkout = {
    init:function() {
        this.payByCard();
        this.checkoutTimer();
        rm.utilities.merchantServiceFee();
		rm.responsivetable.saveOrderTemplate();

		
		var msfData = [], msfData1 = [];
        $('.doCheckoutBut.processButton').on('click', function(e){
        	msfData = []; msfData1 = [];
        	
        	e.preventDefault();

            var step = 3;

            if($('#payByCard').is(':checked') || $('.card-carddetailsOnlca').length){
            	if (typeof rm.tagManager.trackOnCheckout !== 'undefined') {
            		rm.tagManager.trackOnCheckout(step, 'Payment|Pay by Card');
            	}
            	
            	//commented current functionality
            	/* 
                if($('#ccFormId').hasClass('ng-valid')){
                    $('body').addClass('loading');
                    $('#customPONumberId').val($('#poNumber').val());
		        	if(rm.checkout.verifyMonthAndYear()){
                        rm.utilities.loadingMessage($('.loading-message').data('confirm'),true);
                    	$('#ccFormId').submit();
                	}
				} */
            	
                if ( $('#ccFormId').hasClass('ng-valid') ) {
	
	        		msfData.push({
	        			'cardNumber': $('#cardNumber').val(),
	        			'cardHolderName': $('#nameOnCard').val(),
	        			'securityCode': $('#securityCode').val(),
	        			'expiryDateMonth': $('.expiryDateMonth').val(),
	    				'expiryDateYear': ( $('.expiryDateMonth').val() === '99' ) ? $('#expiryDateYearHidden').val() : $('.expiryDateYear').val()
	        		});
	            	
	        		var cardType = $('#cardType').val();
	        		var poNumber = $('#poNumber').val();
	        		
	        		console.log(cardType);
	        		$.magnificPopup.close();
	                rm.utilities.showOverlay(true);
	                
	            	$.ajax({
						url: '/checkout/payByCard',
						data: {'cardType': cardType, 'poNumber':poNumber},
		                dataType: 'json',
		                type: 'POST'
	            	}).done(function(res){
	            		
	            		if( res.error === null) {
	            		
		            		msfData1.push({
		            			'authKey': res.authKey,
		            			'paymentUrl': res.paymentUrl
		            		});
		            		
		            		$.extend(true, msfData, msfData1);
		            		
		            		console.log(msfData);
		            		
		            		//pass data to MSF modal
		            		$('#amount').text('$'+parseFloat(res.displayAmount).toFixed(2));
		            		$('#totalAmount').text('$'+parseFloat(res.displayTotalAmount).toFixed(2));
		            		$('#surcharge').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
		            		$('#msf').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
		            					            		
		                    rm.utilities.showOverlay(false);
		            		rm.utilities.showCheckoutMSFPopup();
	            		} else {
	            			
	                        var checkout = '/checkout?';
	                        window.location.replace(checkout + res.error);
	            		}
	             	}); 
                }
                
            } else {
            	if (typeof rm.tagManager.trackOnCheckout !== 'undefined') {
            		rm.tagManager.trackOnCheckout(step, 'Payment|Pay on Account');
            	}
            	
                $('body').addClass('loading');
                var checkoutUrl = $('.doCheckoutBut.processButton').attr('data-checkout-po-url');
                rm.utilities.loadingMessage($('.loading-checkout').data('confirm'),true);
                window.location = checkoutUrl +'?poNumber=' + $('#poNumber').val();
            }
        });
        
        $('.checkout-msf-popup-button').on('click', function(){
    
    		$.magnificPopup.close();
            rm.utilities.showOverlay(true);
            
            MerchantSuite.BaseUrl = msfData[0].paymentUrl;
            
            console.log(msfData);
            
        	MerchantSuite.ProcessPayment({
        		AuthKey: msfData[0].authKey,
        		CardNumber: msfData[0].cardNumber,
        		CVN: msfData[0].securityCode,
        		ExpiryMonth: msfData[0].expiryDateMonth,
        		ExpiryYear: msfData[0].expiryDateYear,
        		CardHolderName: msfData[0].cardHolderName,
        		CallbackFunction: function(res) {
        			
        			var checkoutUrl = '/checkout?';
        			if ( res.AjaxResponseType === 0 ) {
        				if ( res.ApiResponseCode === 0 ) {
                			window.location.href = res.RedirectionUrl;
            			} else { 
            				window.location.replace(checkoutUrl + 'declined=true');
            			}
        			} else if ( res.AjaxResponseType === 1 ) {
        				window.location.replace(checkoutUrl + 'invalidCard=true');
        			} else if ( res.AjaxResponseType === 2 ) {
        				window.location.replace(checkoutUrl + 'paymentError=true');
        			} else {
        				window.location.replace(checkoutUrl + 'gatewayError=true');
        			}
        		}
        	});
        });
        
        this.clamp();
        this.deliveryModesVisibility();

    },
    
    //update for title wrap
    clamp: function(){
        rm.utilities.needClamp('checkoutClamp-2',2,'clamp-2');
        rm.utilities.needClamp('checkoutClamp-1',1,'clamp-1');
    },

    verifyMonthAndYear:function(){
        var date=new Date();
		var fullYear = date.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var month=date.getMonth()+1;
		if(shortYear === $('.expiryDateYear').val() && $('.expiryDateMonth').val() && $('.expiryDateMonth').val() < month){
			$('#Invalid_Expiry_Date').removeClass('ng-hide');
			$('.doCheckoutBut').attr('disabled','disabled');
			return false;
		}else if($('.expiryDateMonth').val() && $('.expiryDateYear').val()){
			$('#Invalid_Expiry_Date').addClass('ng-hide');
			$('.doCheckoutBut').removeAttr('disabled');
			return true;
		}
    },

    payByCard: function(){
        var westpacData = JSON.parse($('#westpacResponse').html());

        window.setPaymentCheckoutDisable = 0;
        var that = this;

        rm.billing.setupMonthYearPayment();
        if(!westpacData){
            if($('#payByAccount').is(':checked')){
                $('.doCheckoutBut.continueCheckout').removeAttr('disabled');
                $('.doCheckoutBut.continueCheckout').removeClass('disabled');
            }

            $('.cart-paymentoptions input[type="radio"]').on('change', function(){
            	var step =  $('input[name="checkoutStep"]').val();

                if($('#payByCard').is(':checked')){
                	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
                		rm.tagManager.trackOnCheckoutOption(step, 'Payment|Pay by Card');
                	}
                	
                    //that.getCCToken();
                    $('.card-carddetails').slideDown();
                    $('.doCheckoutBut.processButton').attr('data-checkout-url','#');
                } else {
                	if (typeof rm.tagManager.trackOnCheckoutOption !== 'undefined') {
                		rm.tagManager.trackOnCheckoutOption(step, 'Payment|Pay on Account');
                	}
                	
                    $('.card-carddetails').slideUp();
                    $('.doCheckoutBut.processButton').attr('data-checkout-url','checkout/placeOrderByAccount');
                    $('.doCheckoutBut.continueCheckout').removeAttr('disabled');
                    $('.doCheckoutBut.continueCheckout').removeClass('disabled');
                }
            });
        } else {
            if(westpacData.error === null || westpacData.error === undefined){
                $('#ccFormId').attr('action', westpacData.url);
                $('#communityCodeId').attr('value', westpacData.communityCode);
                $('#tokenNumber').attr('value', westpacData.token);
                $('#ignoreDuplicateId').attr('value', westpacData.ignoreDuplicate);
            } else {
                window.errorOnTokenRequest = westpacData.error;
                var cart = '/sabmStore/en/cart?';
                window.location.replace(cart + window.errorOnTokenRequest);
            }
        }
    },

    getCCToken: function(){
        if($('#tokenNumber').val() === null || $('#tokenNumber').val() === '') {
            $('body').addClass('loading');
            $.ajax({
                url: '/checkout/beforePayment',
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                type: 'GET'
            }).done(function(response) {
                if(response.error === null || response.error === undefined){
                    $('#ccFormId').attr('action', response.url);
                    $('#communityCodeId').attr('value', response.communityCode);
                    $('#tokenNumber').attr('value', response.token);
                    $('#ignoreDuplicateId').attr('value', response.ignoreDuplicate);
                }else{
                    window.errorOnTokenRequest = response.error;
                    var cart = '/sabmStore/en/cart?';
                    window.location.replace(cart + window.errorOnTokenRequest);
                }
                $('body').removeClass('loading');
            });
        }
    },

    checkoutTimer: function(){
        setTimeout(function(){
           window.location.href='/cart?cartTimeout=1';
        }, 5 * 60 * 1000);
    },

    waitingProcessingPage: function(){
        $('document').ready(function() {
            $('body').addClass('loading');
            rm.checkout.checkProcessingJSON();
        });
    },

    checkProcessingJSON: function(){
        var url = window.location.pathname;
        var value = url.substring(url.lastIndexOf('/') + 1);
        $.get('/checkout/sop/processingJson/' + value, function(result) {
            if(result.length > 0) {
                window.location.replace(result);
            } else{
                setTimeout(function(){
                    rm.checkout.checkProcessingJSON();
                }, 3000);
            }
        });
    },
    
    webHookPaymentDoneWaitingPage: function(){ 
        $('document').ready(function() {        	
            $('body').addClass('loading');
            rm.checkout.checkProcessingWebHookTransaction();
        });
    },

    checkProcessingWebHookTransaction: function(){
        var url = window.location.pathname;
        var value = url.substring(url.lastIndexOf('/') + 1);
        $.get('/checkout/sop/processingPostbackResult/' + value, function(result) {        	
            if(result.length > 0) {
                window.location.replace(result);
            } else{
                setTimeout(function(){
                    rm.checkout.checkProcessingWebHookTransaction();
                }, 3000);
            }
        });
    },

    errorMessaging: function(element, message){
        if(message !== '') {
            $(element).addClass('error-input').attr('placeholder', message);
        } else {
            $(element).removeClass('error-input');
        }

    },

    getUrlParameter:function (sParam) {
        var sPageURL = decodeURIComponent(window.location.search.substring(1)),
            sURLVariables = sPageURL.split('&'),
            sParameterName,
            i;

        for (i = 0; i < sURLVariables.length; i++) {
            sParameterName = sURLVariables[i].split('=');

            if (sParameterName[0] === sParam) {
                return sParameterName[1] === undefined ? true : sParameterName[1];
            }
        }
    },

    /*
    *   Show/Hide CUB Arranged radio button
    *   @author: nolan.b.trazo@accenture.com
    */
    deliveryModesVisibility: function() {
        var data = rm.datepickers.getDeliveryData();
        if (typeof data !== 'undefined') {
            if (data.cubArrangedEnabled === false) {
                $('.cart-deliverymethod .cub-arranged-block').hide();
            }
        }
    }
};
/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/
/* globals sessionStorage */


	'use strict';

	rm.customer = {
	        loginSubmitted: false,
			valueUserNameIncorrect: $('#username_incorrect').val(),
			valueErrorTopMessage: '<div class="alert negative">'+$('#loginError_topMessage').val()+'</div>',
			profileReceiveCheckbox: $('.personal-profile #confirm1').attr('checked'),
			profileSMSReceiveCheckbox: $('.personal-profile #confirm2').attr('checked'),
			valueCustomerSearchErrorMessage: $('#customerSearchError_essage').val(),
			// this will be used in validate.js
			constraints: {
				from: {
					email: true
				},
				confirmPassword: {
		    		equality: 'password'
		    	},

		    	passwordSecurity:{
					format:{
						pattern: '^(?=.*[0-9].*)(?=.*[a-z].*).{8,}$',
						message: 'Security check failed'
					}
				},
				firstNameSecurity:{
					format:{
						pattern: '[a-zA-Z- ]*$',
						message: 'Security check failed'
					}
				},
				orderLimitSecurity:{
					format:{
						pattern: /^(0|\+?[1-9][0-9]*)$/,
						message: 'Security check failed'
					}
				}
			},

			//check the email address format remove the space at the end
			emailInvalid: function(email)
			{
				email = $.trim(email);
				return validate.isEmpty(email) || validate({from: email}, rm.customer.constraints);
			},



			//check the password format
			passwordInvalid: function(password)
			{
				return validate.isEmpty(password) || validate({passwordSecurity: password}, rm.customer.constraints);
			},

			//check the login password format
			loginPasswordInvalid: function(password)
			{
				return validate.isEmpty(password);
			},

			//go to check the email format and give out the result
			checkLoginUsername: function()
			{
				var username = $('#j_username').val();

				if(rm.customer.emailInvalid(username)){
					//if the email have error, show the error message under the email input field
					$('#username_common').html(rm.customer.valueUserNameIncorrect);
//					$('#j_username').addClass('error-input');
					$('#globalMessages').html(rm.customer.valueErrorTopMessage);
					return false;
				}
				//if the email have no error , remove the error messages
				$('#username_common').html('');
				//$('#j_username').removeClass('error-input');
				$('#globalMessages').html('');
				return true;
			},


			//go to check the password format and give out the result
			checkLoginPassword: function()
			{
				var password = $('#j_password').val();

				if(rm.customer.loginPasswordInvalid(password)){
					return false;
				}
				return true;
			},

			//show the top error message
			showErrorTopMessage: function()
			{
				 $('#globalMessages').html(rm.customer.valueErrorTopMessage);
			},

			//hide the top error message
			hideErrorTopMessage: function()
			{
				$('#globalMessages').html('');
			},

			//bing the blur event for the input field of the email and password

			bindLoginCheckForm: function()
			{
                $('#j_password').popover({
                    placement: 'bottom',
                    html: true,
                    trigger: 'manual',
                    animation: true,
                    offset: 20,
                    delay:{show: 100, hide: 100},
                    template: '<div class="popover popover-loginattempt" ><div class="arrow"></div><div class="popover-content popover-loginattempt-content"></div></div>'
                });
			    if(parseInt($('#loginAttempts').text()) > 1){
                    $('#j_password').popover('toggle');
                    rm.customer.hideErrorTopMessage();
			    }

                if($('#j_password').length > 0){
                    sessionStorage.setItem('ShowWelcome','TRUE');
                }
				$('#j_username').blur(function(){
					rm.customer.checkLoginUsername();
				});
				$('#YT\\.png').click(function(e){
					window.location.href = 'https://www.youtube.com/watch?v=wqqT_i7A_Ks&amp;feature=youtu.be';
				});
				$('#Portal2\\.png').click(function(e){
					window.location.href = window.location.href.split('sabmStore')[0].concat('staffPortal/sabmStore/en/login');
				});
				$('#loginForm .form-actions .btn').click(function(e) {
					rm.customer.hideErrorTopMessage();
		            setTimeout(function() {
		            	if(rm.customer.checkLoginUsername() && rm.customer.checkLoginPassword()){
		            	    rm.customer.submitLoginForm();
						 }else{
							 if(parseInt($('#loginAttempts').text()) <= 1){
					          rm.customer.showErrorTopMessage();
							 }
						 }

		            },100);
		        });
				$('#loginForm .form-control').bind('keypress', function(event) {
					if (event.keyCode === 13) {
						//console.log('keypress');
						rm.customer.hideErrorTopMessage();
			            setTimeout(function() {
			            	if(rm.customer.checkLoginUsername() && rm.customer.checkLoginPassword()){
								rm.customer.submitLoginForm();
							 }else{
								 if(parseInt($('#loginAttempts').text()) <= 1){
								 rm.customer.showErrorTopMessage();
								 }
							 }
			            },100);
					}
				});
			},

			submitLoginForm: function(){
			    if (!rm.customer.loginSubmitted) {
                    rm.customer.loginSubmitted = true;
                    $('#loginForm').submit();
                }
			},

			validateProfileNotificationOpt: function(){

				if($('.personal-profile #confirm1').attr('checked') !== rm.customer.profileReceiveCheckbox){
					return true;
				}

				if($('.personal-profile #confirm2').attr('checked') !== rm.customer.profileSMSReceiveCheckbox){
					return true;
				}

				return false;
			},

			bindProfileCheckBox: function(){
				$('.personal-profile #confirm1, .personal-profile #confirm2').on('change',function(){
					if($('.personal-profile #confirm1').attr('checked')){
						$('.personal-profile #profile_receiveUpdates').val('true');
					}else{
						$('.personal-profile #profile_receiveUpdates').val('false');
					}

					if($('.personal-profile #confirm2').attr('checked')){
						$('.personal-profile #profile_receiveUpdatesForSMS').val('true');
					}else{
						$('.personal-profile #profile_receiveUpdatesForSMS').val('false');
					}

					if ( rm.customer.validateProfileNotificationOpt() ) {
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
					} else {
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').removeClass('btn-primary');
						$('.personal-profile .save-profile').addClass('btn-cancel');
					}

				});

				if($('body').hasClass('page-profile')){

					var $custMobileNumber = $('#customerMobileNumber'), $custBusinessPhoneNumber = $('#customerBusinessPhoneNumber');
					var $curMobileNumber = $('#mobileNumberField'), $curBusinessPhoneNumber = $('#businessPhoneNumber');
					var $id, dato, $this;
                    if ($curMobileNumber.val() === '') {
						$('#confirm2').prop('disabled', true);
					} 
					if ($custMobileNumber.val() != '') {
						$('#confirm2').prop('disabled', false);
					}
					var enabledSaveButton = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#555', 'border': '1px solid #d6d6d6'});
						$('#alert-mobileNumber').addClass('hide');
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
					};

					var disabledSaveButton = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#ff0000', 'border': '1px solid #ff0000'});
						$('#alert-mobileNumber').removeClass('hide');
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').addClass('btn-cancel');
						$('.personal-profile .save-profile').removeClass('btn-primary');
					};

					var reset = function(e){
						$id = $('#'+$(e).attr('id'));
						$id.css({'color':'#555', 'border': '1px solid #d6d6d6'});
						$('#alert-mobileNumber').addClass('hide');
						$('.personal-profile .save-profile').attr('disabled','true');
						$('.personal-profile .save-profile').addClass('btn-cancel');
						$('.personal-profile .save-profile').removeClass('btn-primary');
					};

					if(typeof $curMobileNumber !== 'undefined'){
						$curMobileNumber.css('background','#fff');

						if($custMobileNumber.val().length > 0){
							var mobileArray = $custMobileNumber.val().split('');
							mobileArray.splice(4,0,' ');
							mobileArray.splice(8,0,' ');
							$custMobileNumber.val(mobileArray.join(''));
							$curMobileNumber.val($custMobileNumber.val());
						}

						$curMobileNumber.on('focus',function(){
							$this = $(this);

							if ($this.val().length === 0) {
								$this.val('04');
								$this[0].setSelectionRange(2,2);
							}
							else {
								var val = $this.val();
								var len = val.length;
								$this[0].setSelectionRange(len,len);  // Ensure cursor remains at the end
							}
						}).on('keydown',function(e){

							var key = e.charCode || e.keyCode || 0;
							$this = $(this);

							// Auto-format- do not expose the mask as the user begins to type
							if (key !== 8 && key !== 9) {
								if ($this.val().length === 4) {
									$this.val($this.val() + ' ');
								}
								if ($this.val().length === 8) {
									$this.val($this.val() + ' ');
								}
							}

							// Allow numeric (and tab, backspace, delete) keys only
							return (key === 8 ||
									key === 9 ||
									key === 46 ||
									(key >= 48 && key <= 57) ||
									(key >= 96 && key <= 105));

						}).on('mouseout blur',function(){

							var $this = $(this);
							var val = $this.val().replace(/[ _]/g,'');
							$('#mobileNumber').val(val);

							var mobileNumberPattern = /[0-9 ]$/g;

							if($this.val() === '04' || $this.val() === ''){
								$curMobileNumber.val('');
							}
                             if($curMobileNumber.val() === ''){
								$('#confirm2').removeAttr('checked');
							}
							if($custMobileNumber.val() !== $curMobileNumber.val()){
								if($curMobileNumber.val().length !== 0){
									if(($curMobileNumber.val().trim().charAt(0) + $curMobileNumber.val().trim().charAt(1) === '04' && $curMobileNumber.val().length === 12 && mobileNumberPattern.test($curMobileNumber.val()))){
										enabledSaveButton($this);
										// Enable the SMS updates checkbox if the input is valid
										$('#confirm2').prop('disabled', false);
									}else{
										disabledSaveButton($this);
										// Disable the SMS updates checkbox if the input is invalid
										$('#confirm2').prop('disabled', true);
									}

									if($curMobileNumber.val() === '04' || $this.val() === ''){
										$curMobileNumber.val('');
										enabledSaveButton($this);
										// Enable the SMS updates checkbox if the default '04' is the only input
        								$('#confirm2').prop('disabled', $curMobileNumber.val() !== '04');
						
									}

								}else{
									$('#confirm2').prop('disabled', true);

										enabledSaveButton($this);
								}
							}else{
								// if current and original is the same value
								reset($this);
							}
						});

					}

					} /* check if in profile page */
				/*
				if(typeof $curBusinessPhoneNumber !== 'undefined'){

					$curBusinessPhoneNumber.css('background','#fff');
					$curBusinessPhoneNumber.on('mouseout',function(){

						var $this = $(this);

						if($custBusinessPhoneNumber.val() !== $curBusinessPhoneNumber.val()){
							if($curBusinessPhoneNumber.val().length !== 0){
								if($curBusinessPhoneNumber.val().length === 10){
									enabledSaveButton($this);
								}else{
									disabledSaveButton($this);
								}
							}else{
								enabledSaveButton($this);
							}
						}else{
							reset($this);
						}
					});
				} */
			},
			bindProfileRadio: function(){
				$('input[name=defaultUnit]').on('change',function(){
						$('.personal-profile .save-profile').removeAttr('disabled');
						$('.personal-profile .save-profile').removeClass('btn-cancel');
						$('.personal-profile .save-profile').addClass('btn-primary');
				});
			},
			showErrorMessage:function(){
				var flag = false;
		         $('#customerSearch_button').click(function(){

		        	 $('.form-group input').each(function(){
		        		 if($(this).val() !== '' &&  $(this).val().length >= $(this)[0].attributes['ng-minlength'].value){
		        			 flag = true;
		        		 }
		        	 });

		        	 if(flag){
		        		 $('#customer_errorMessage').hide();
		        		 $('#customerSearchForm').submit();
		        	 }else{
		        		 $('#customer_errorMessage').show();
		        	 }
		         });
		         $('#customerSearchForm').bind('keypress', function(event) {
		        	 	if (event.keyCode === 13) {

				        	 $('.form-group input').each(function(){
				        		 if($(this).val() !== '' &&  $(this).val().length >= $(this)[0].attributes['ng-minlength'].value){
				        			 flag = true;
				        		 }
				        	 });

				        	 if(flag){
				        		 $('#customer_errorMessage').hide();
				        		 $('#customerSearchForm').submit();
				        	 }else{
				        		 $('#customer_errorMessage').show();
				        	 }
				         }
					});

			},


			bindDeleteUser: function(){
				$('#confirm-delete-user').on('click',function(){
					var $deleteUserForm = $('#deleteUserForm');
					$deleteUserForm.submit();
				});
			},

			init: function ()
			{
				rm.customer.bindLoginCheckForm();
				rm.customer.bindProfileCheckBox();
				rm.customer.showErrorMessage();
				rm.customer.bindProfileRadio();
				//if there have error, show error under the input filed
				if($('#j_username').length && $('#loginError').val()){
					rm.customer.checkLoginUsername();
				}
				 this.bindDeleteUser();
			},
	};
'use strict';


rm.user = {
     
      setupListeners: function() {
    	  
    	  $('.users-table .sendWelcomeEmail').on('click', function(e) {
    		e.preventDefault();
    		var that = $(this);
    		var customerUid = $(that).attr('id');
    		
    		$.ajax({
    			url:'/sabmStore/en/register/sendWelcomeEmail/' + customerUid,
    			type:'POST',
    			success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					
					$.magnificPopup.close();
					$('#sendWelcomeEmailPopup p').html($('#sendWelcomeEmailPopup p').html() + ' ' + customerUid + '.');
					$.magnificPopup.open({
						items:{
					        src: '#sendWelcomeEmailPopup',
					        type:'inline'
						},
				        modal: true
					});				
					
					/*
					if ($(that).text().trim() === 'Send') {
						$(that).text('Re-Send');
					}
					*/
    			},
    			error: function(result) {
					console.log('error: ' + JSON.stringify(result));
				}
    		});
    					
    	});  
      }
      
};

$('document').ready(function() {
	rm.user.setupListeners();
});
/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/

'use strict';
rm.resetpassword = {
	bindALl : function() {
		this.bindBlur($('.js_password'));
		this.bindMatch($('.checkPwd'));
		this.bindButtonClick($('#updatePwd_button'));
	},
//  validate the password confirm match
	checkMatch : function(password,confirmPassword){
		if(validate.isEmpty(password) || validate.isEmpty(confirmPassword)||
				validate({password: password, confirmPassword: confirmPassword}, rm.customer.constraints)){
			return true;
		}
		return false;
	},
	
//	Control message top display and hide
	showTopMessage : function() {
		if ($('.error_security').css('display') === 'none' && $('.error_match').css('display') === 'none') {
			$('#topMessage').hide();
		}else{
			$('#topMessage').show();
		}

	},
	
// Show the error message of security error	
	showSecurityError : function(){
		$('.error_security').show();
		$('#globalMessages').hide();
		$('.help-inline').hide();
		$('.js_password').css('border', '1px solid red');
		$('#pwd label').css('color', 'red');
	},

// Hide the error message of security error	
	hideSecurityError : function(){
		$('.error_security').hide();
		$('.js_password').removeAttr('style');
		$('#pwd label').removeAttr('style');
	},
	
// Show not match error
	showNotMatchError: function(){
		$('.error_match').show();
		$('.help-inline').hide();
		$('.checkPwd').css('border', '1px solid red');
		$('#confirmPwd label').css('color', 'red');
		$('#globalMessages').hide();
	},
	
// Hide not match error
	hideNotMatchError: function(){
		$('.error_match').hide();
		$('.checkPwd').removeAttr('style');
		$('#confirmPwd label').removeAttr('style');
	},
	
//	the blur event of password input
	bindBlur : function(password) {
		password.blur(function() {
			if (!rm.customer.passwordInvalid(password.val())) {
				rm.resetpassword.hideSecurityError();
			} else {
				rm.resetpassword.showSecurityError();
			}
			rm.resetpassword.showTopMessage();
		});

	},
	
//	the blur event of confirm password input
	bindMatch : function(checkPwd) {
		//the match event of input
		checkPwd.blur(function() {
			if (!rm.resetpassword.checkMatch($('.js_password').val(),checkPwd.val())) {
				rm.resetpassword.hideNotMatchError();
			} else {
				rm.resetpassword.showNotMatchError();
			}
			rm.resetpassword.showTopMessage();
		});

	},
	
//	button click event to submit by ajax
    bindButtonClick : function(button) {
        button.mousedown(function(e) {
            setTimeout(function() {
            	if (rm.customer.passwordInvalid($('.js_password').val())) {
            		rm.resetpassword.showSecurityError();
                } 
                if(rm.resetpassword.checkMatch($('.js_password').val(),$('.checkPwd').val())){
                	rm.resetpassword.showNotMatchError();
                }
                if(!rm.customer.passwordInvalid($('.js_password').val()) && !rm.resetpassword.checkMatch($('.js_password').val(),$('.checkPwd').val())){
                	rm.resetpassword.hideSecurityError();
                	rm.resetpassword.hideNotMatchError();
                	$('#updatePwdForm').submit();
                }
                rm.resetpassword.showTopMessage();
            }, 100);
        });
    },


	init : function() {
		rm.resetpassword.bindALl();
	},
    
};
/* globals validate */
/*jshint unused:false*/

'use strict';

rm.updatepassword = {
	constraints : {
		currentPassword : {
			// currentPassword is required
			presence : true
		},
		newPassword : {
			// newPassword is also required
			presence : true,
			// And must be at least 8 characters long
			length : {
				minimum : 8
			},

			format : {
				pattern: '^(?=.*[0-9].*)(?=.*[a-z].*).{8,}$',
				message : 1
			}
		},
		checkNewPassword : {
			// You need to confirm your password
			presence : true,
			// and it needs to be equal to the other password
			equality : {
				attribute : 'newPassword',
				message : 2
			}
		}
	},
	init : function() {
		this.showPasswordText();
//		this.handleForm();
		this.handleInputChange();

		// Fix the browser to remember the password, leading to the password input box automatically filled
		if ($('#updatepasswordform').length) {
			$('#updatepasswordform')[0].reset();
		}
	},
	// form submit
	handleForm : function() {
		// validate form field
        var form = $('#updatepasswordform');
        var errors = validate(form, rm.updatepassword.constraints);
        rm.updatepassword.showErrors(form, errors || {});
        rm.updatepassword.showTopMessage();
        if (!errors) {
            this.isExistingUser();
            return true;
        }
        return false;
	},
	// input value validate
	handleInputChange : function() {
		var form = $('#updatepasswordform');
		$('#updatepasswordform input').each(
			function(index, that) {
				$(that).on('input blur',function(ev) {
					var errors = validate(form,rm.updatepassword.constraints) || {};
					rm.updatepassword.showErrorForInput(that,errors[that.name]);
				});
			});
	},
	// input value validate show error
	showErrorForInput : function(input, error) {
		var formGroup = $(input).closest('.form-group'), message = $(formGroup).find('.message');
		if (error) {
			message.show();
			$(input).css('border', '1px solid red');
			if (error[0] === 2) {
				// checkNewPassword don`t match
				$('.error_empty').hide();
			} else if (error[0] === 1) {
				// checkNewPassword is empty
				$('.error_match').hide();
			} else if (error[0].indexOf('blank') > 0) {
				$('.error_match').hide();
			}
		} else {
			// After the submit of the error
			$(input).closest('.control-group').find('.help-inline').hide();
			message.hide();
			$(input).removeAttr('style');
		}
		rm.updatepassword.showTopMessage();
	},

	// show current password in text
	showPasswordText: function(){
		$('.show-password input[type="checkbox"]').change(function () {
      var input = $(this).parents('.form-group').find('.text');

      if($(this).is(':checked')){
        input.attr('type', 'text');
      } else {
        input.attr('type', 'password');
      }
    });
	},

	//	Control message top display and hide
	showTopMessage : function() {
		var error = false;
		$('#updatepasswordform').find('.help-inline').each(function() {
			if ($(this).css('display') !== 'none') {
				error = true;
				return;
			}
		});
		if (error) {
			$('#topMessage').show();
		}else{
			$('#topMessage').hide();
		}

	},
	// show validate all error
	showErrors : function(form, errors) {
		$('#updatepasswordform input').each(function(index, that) {
			rm.updatepassword.showErrorForInput(that,errors && errors[that.name]);
		});
	},

	isExistingUser: function() {
	    var customer = JSON.parse($('#customerData').text());
        var url = '/sabmStore/en/register/isExistingUser/' + customer.email + '?createUser=false';
        $.ajax({
            url: url,
            type: 'GET',
            success: function (data) {
                if (data === 'TRUE') {
                    $.magnificPopup.open({
                        items: {
                          src: '#forgotpwd-popup',
                          type: 'inline'
                        },
                        callbacks: {
                            open: function() {
                                $('#submit_button').mousedown(function(e) {
                                    $.magnificPopup.close();
                                    setTimeout(function() {
                                        $('#updatepasswordform').submit();
                                    }, 100);
                                });
                            },
                        }
                    });
                } else {
                    $('#updatepasswordform').submit();
                }
            },
            error: function (xhr, errorText, thrownError) {
                console.log(thrownError)
            }
        });
	}
};
/*jshint unused:false*/
/* globals ACC */
/* globals window */
/* globals invoiceSelectedList */
/* globals trackCart */
/* globals ProductForTrackCart */


'use strict';

rm.responsivetable = {

    //Initiate the functions required by this particular footable
    init: function () {
        this.sortable = $('.sortable');

        if($('#showMSFPopup').length <= 0) {
        	rm.utilities.removeItemFromStorage('invoices');
        }

        this.orderLimitSort();
        this.initTable();
        this.filterListener();
        this.emailInvoices();
        this.sortingAPI(this.sortable);
        this.saveOrderTemplate();

        if($('.page-customerSearchResults').length){
          this.bdePortal();
        }
    },

    initTable: function () {
       this.orderAddtoCartButton($('#order-detail-inputID'));

       $('#addToCartForTemplateFromPopUp').on('click',function(){
	       	rm.responsivetable.orderItemsAddtoCart($('#order-detail-inputID').val());
	   	});
       
        //If we can find a sortableclass then we know we have to make a sortable table other wise there is no need doing all the js stuff required
        if (this.sortable.length <= 0) {
            return false;
        }

        //We know we have a sortable item but what type of table is required. If this is a table that loads in json it need to run along and do that first before it initiates
        if (this.sortable.hasClass('sortable-json')) {
            this.readTableData(this.sortable);
        }

        this.createClearTableListener(this.sortable);

        this.selectedTotal();
        this.numRows();
        this.enableEmailButton();

        if ($('.billing-options').length) {
            this.inCredit();
            $('.billing-options input').change(function () {
                rm.responsivetable.inCredit();
            });
        }

        this.sortable.footable({
            breakpoints: {
                phone: 480,
                tablet: 768,
                desktop: 990
            }
        }).bind({
            'footable_paging': function (e) {
                rm.responsivetable.clearSelected();
            }
        });

        if (this.sortable.hasClass('order-table')) {
            //get the footable sort object
            var footableSort = $('.footable').data('footable-sort');
            //sorting the table by Date Descending
            footableSort.doSort(1, false);
        }
        
         if (this.sortable.hasClass('billing-table')) {
            //get the footable sort object
            var footableSort = $('.footable').data('footable-sort');
            //sorting the table by Date Descending
            footableSort.doSort(4, false);
        }

        if($('#showMSFPopup').length > 0) {
        	var arr = rm.utilities.getArrayFromStorage('invoices');

        	if(arr !== null && arr !== undefined) {
	        	for (var index = 0; index < arr.length; ++index) {
	        	    $('#'+arr[index]).click();
	        	}
	            $('label[for="selectedAmount"]').trigger('click');
        	}
        }
    },

    //Go read json and get all the info that this table needs
    readTableData: function (_sortable) {

        var that = this, //used to get around this binding incorrectly to the each loop
            json = this.ajaxGetData(_sortable);

        if(json !== null)
        {
        	// Show number of orders on Order History page
            $('.numItems span').html(json.length);

            if(json.length > 0){
            	$('#noDataError').hide();
            } else {
            	$('#noDataError').show();
            }

            //will display more transactions message if transactions is over 500
            var status=$('.select-btn:not(.sort):not(.header)').attr('data-value');

            //will not show message for "open transactions"
            if(json.length >= 500 && (status==='A' || status==='')){
            	$('#moreTransactions').show();
            }

            // Use 'Order' if only one order
            if(json.length === 1){
                $('.numItem').show();
                $('.numItems').hide();
            } else {
                $('.numItems').show();
                $('.numItem').hide();
            }

            var $bodyTable = _sortable.find('tbody');
            var openAmount = 0;

            var createRow;

        	if(_sortable.hasClass('billing-table')){
        		createRow = that.createRowBillings;
        	}else if(_sortable.hasClass('order-table')){
        		createRow = that.createRowOrders;
        	}

            $.each(json, function(index, item) {
            	var row = createRow(item);

            	$bodyTable.append(row);

                /*if(item.status === 'Open' && item.openAmount !== '') {
                	openAmount += parseFloat(item.openAmount.replace(/\$|,/g, ''));
                }*/
            });
            if(json.hasOwnProperty('__OpenBalance')){
                openAmount = parseFloat(json.__OpenBalance);
                openAmount = isNaN(openAmount) ? 0 : openAmount;
            }

            $('#openBalanceValue').html(rm.utilities.convertDollar(openAmount.toFixed(2)));
        }

        $('body').removeClass('loading');
    },
    orderLimitSort: function (){

        $('.orderLimitBody').each(function(){
            console.log($(this)[0].innerHTML);
            if(!$(this)[0].innerHTML){
                console.log('no text');
            } else {
                console.log('text');
            }
            // console.log( === '');
            // console.log($(this).text());
        });
    },

    ajaxGetData: function (_sortable) {
        var $selectBtn = $('.select-btn:not(.sort):not(.header)');
        var requestData = {
            lineItem: $selectBtn.attr('data-value'),
            forUnit: $('.select-btn[id="forUnit"]').attr('data-value'),
            startDate: $('.form-control[name="start"]').attr('data-value'),
            endDate: $('.form-control[name="end"]').attr('data-value'),
            type: $('.select-btn[id="type"]').attr('data-value')
        };
        var json = null,
            tableDataURL = _sortable.data('url');

        $.ajax({
            'async': false,
            'global': false,
            'url': tableDataURL,
            'data': requestData,
            'method': 'POST',
            'dataType': 'json',
            'success': function (data) {
                //json = data; old feature
                //new feature for SABMC-1091
            	if(_sortable.hasClass('billing-table')){
            		json = data.invoices || [];
                    json.__OpenBalance=data.openBalance;
            	}else{
            		json = data;
            	}
            },
            'error': function () {
                $('#noDataError').show();
                $('.num-rows').hide();
            }
        });
        return json;
    },

    //Go ahead and create the rows required by looping through the json
    createRowBillings: function (item) {
        var row = '<tr><td><div class="checkbox"><input id="' + item.invoiceNumber + '" type="checkbox" name="billing" value="' + item.invoiceNumber + '" data-status="' + item.status + '"><label for="' + item.invoiceNumber + '"></label></div></td><td>' + item.invoiceNumber + '</td><td class="openAmount text-right">' + rm.utilities.convertDollar(item.openAmount) + '</td><td class="footable-po">' + item.purchaseOrderNumber + '</td><td data-value="' + item.transactionDateStamp + '">' + item.transactionDate + '</td><td data-value="' + item.dueDateStamp + '">' + item.dueDate + '</td><td>' + item.branch + '</td><td>' + item.status + '</td><td>' + item.type + '</td><td>' + item.orderNumber + '</td><td>';

    	if(item.printable === true) {
        	row += '<a class="inline" href="billing/invoice/pdf/' + item.invoiceNumber + '" target="_blank">View</a>';
        }
        row += '</td></tr>';

        return $(row);
    },

    createRowOrders: function (item) {
        var row = '',
            actionText = $('#tableActionText').data('text'),
            orderNo = '\'' + item.orderNo + '\'';
        var status = $('#order-status-'+item.status.toLowerCase()).html();
        if(actionText !== undefined)
        	{
        		row = '<tr><td><a href="order/' + item.orderNo + '" class="inline">' + item.sapOrderNo + '</a></td><td data-value="' + item.dateStamp + '">' + item.date + '</td><td data-value="' + item.deliveryDateStamp + '">' + item.deliveryDate + '</td><td><span class="status status-' + item.status.toLowerCase() + '"></span>'+status+'</td><td><a class="btn btn-primary btn-small bde-view-only" onclick="rm.responsivetable.orderAddtoCart('+ orderNo +')"  href="javascript:void(0);">' + actionText + '</a></tr>';
        	}
        else
        	{
        	    row = '<tr><td><a href="order/' + item.orderNo + '" class="inline">' + item.sapOrderNo + '</a></td><td data-value="' + item.dateStamp + '">' + item.date + '</td><td data-value="' + item.deliveryDateStamp + '">' + item.deliveryDate + '</td><td><span class="status status-' + item.status.toLowerCase() + '"></span>'+status+'</tr>';
        	}
        return $(row);
    },

    enableEmailButton: function () {

        $('input[name=billing]').change(function () {
            var selectedDocNos = $('input[name=billing]:checked').map(function () {
                return $(this).val();
            }).get();

            if (selectedDocNos !== '' && selectedDocNos.length > 0) {
                $('#email-invoices').removeClass('disabled');
            } else {
                $('#email-invoices').addClass('disabled');
            }
        });
    },
    emailInvoices: function () {
        $('#email-invoices').click(function () {

        	if($('.view-only-mode').length) {
    			return;
    		}
            var selectedDocNos = $('input[name=billing]:checked').map(function () {
                return $(this).val();
            }).get();
            $.ajax({
                url: $(this).attr('data-url'),
                type: 'POST',
                data: 'docNumList=' + selectedDocNos,
                cache: false,
                success: function (result) {
                    console.log(result);
                },
            });

            $.magnificPopup.open({
            	items: {
                	src: '#mail-invoices-success'
                },
                type: 'inline',
                closeMarkup: '<div class="mfp-close"><svg class="icon-cross"><use xlink:href="#icon-cross"></use></span></div>'
		    });
        });
    },

    orderAddtoCart: function (orderNo) {
    	
    	if($('.view-only-mode').length){
			return;
		}


        $.ajax({
        	url: $('.orderAddToCartUrl').val(),
            type: 'POST',
            data: {'orderCode':orderNo},
            cache: false,
            success: function (result) {
                if (result.addToCartForErrorLayer) {
                    $('#globalMessages').empty();
                    $('#globalMessages').append(result.addToCartForErrorLayer);
                }else if(result.excludedError){
                	$('#outOfStockPopup h2').empty().append(result.excludedError);
                	$('#outOfStockPopup').modal(); 
                } else {
                    $('#globalMessages').empty();
                }
                if (result) {
                	var miniCartCountBeforeRefresh = $('.miniCart .count').text();
                	
                    ACC.product.displayAddToCartPopup(result);
                    ACC.minicart.refreshMiniCartCount();
                    
                    var miniCartCountAfterRefresh = $('.miniCart .count').text();
                    if (miniCartCountBeforeRefresh === miniCartCountAfterRefresh) {
						$('#addToCartLayer').hide();
					}
					
                    ACC.common.refreshScreenReaderBuffer();
                }
            },
            error: function (result) {
                $('#globalMessages').append(result);
                ACC.common.refreshScreenReaderBuffer();
            }
        });
    },

    orderItemsAddtoCart: function(orderNo) {
    	if($('.view-only-mode').length){
			return;
		}
		
    	var entryNumbers = '';
    	
    	$('.table-row').each(function () {
    		if($(this).css('display') !== 'none'){
				if (!$(this).hasClass('disabled-productPackTypeNotAllowed')) {
					var entryNumber = $(this).find('.entryNumber').val();
					if (entryNumbers === '') {
						entryNumbers = entryNumber;
					} else {
						entryNumbers += ',' + entryNumber;
					}
				}
			}
    	});
    	
    	$.ajax({
        	url: $('.orderItemsAddToCartUrl').val(),
            type: 'POST',
            data: {'orderCode':orderNo, 'entries':entryNumbers},
            cache: false,
            success: function (result) {
                if (result.addToCartForErrorLayer) {
                    $('#globalMessages').empty();
                    $('#globalMessages').append(result.addToCartForErrorLayer);
                } else {
                    $('#globalMessages').empty();
                }
                if (result) {
                    ACC.product.displayAddToCartPopup(result);
                    ACC.minicart.refreshMiniCartCount();
                    ACC.common.refreshScreenReaderBuffer();
                }
            },
            error: function (result) {
                $('#globalMessages').append(result);
                ACC.common.refreshScreenReaderBuffer();
            }
        });
    },
    
    orderAddtoCartButton: function (orderNo) {
        $('#order-detail-button-id1,#order-detail-button-id2').on('click', function () {
        
	       /* if($('.disabled-productPackTypeNotAllowed').length > 0) {
	    	    $('#outOfStockPopup h2').empty().append(ACC.deliveryPackDate);
	    		$('#outOfStockPopup').modal();
	    		return;
	    	}*/
        
            rm.responsivetable.orderAddtoCart(orderNo.val());
        });
    },

    //create a listener that clears away the previous seach terms
    createClearTableListener: function (_sortable) {

        var clear = $('.clear-filter'),
            filterStatus = $('.filter-status');

        //if there is no clear then there is no need to attach a listener to it
        if (clear.length <= 0) {
            return false;
        }

        clear.click(function (e) {
            e.preventDefault();
            _sortable.trigger('footable_clear_filter');
            filterStatus.val('');
        });
    },

    sortingAPI: function (table) {
        //get the footable sort object
        var footableSort = table.data('footable-sort');

        $('.columnSort').on('click touchstart',function () {

            //get the index we are wanting to sort by
            var index = $(this).data('index');

            //get the sort order
            var ascending = $(this).data('ascending');

            footableSort.doSort(index, ascending);

            // Return to first page after sort
            $('.pagination a').filter('[data-page="first"]').trigger('click');
        });
    },


    checkForFailPayment: function(){
        if(window.location.href.indexOf('declined=true') > -1) {
        	//alert(invoiceSelectedList.length);
        	if(invoiceSelectedList.length){
	            // If payment failed, reselect invoices previously selected.	        	
	            var invoices = invoiceSelectedList.split(',');
	            //alert(invoices);
	            if(invoices.length){
	                $.each(invoices,function(index,value){                	
	                 var id = '#' + value;
	                 $(id).prop('checked',true);
	                });              
	                rm.responsivetable.totalSelectAll();
	                $('#selectedAmount').click();
	                
	            }
        	}
        }
    },

    selectedTotal: function () {
        var total = 0,
            allCheckboxes = $('#seller-table td .checkbox input'),
            selectAll = $('#selectAllBilling'),
            table = $('#seller-table');

        allCheckboxes.change(function () {
            rm.responsivetable.totalSelectAll();
            rm.responsivetable.inCredit();

            if($(this).prop('checked')) {
                rm.utilities.addItemToArrayStorage('invoices', $(this).val());
            } else {
                rm.utilities.removeItemFromArrayStorage('invoices', $(this).val());
            }
        });

        selectAll.change(function () {
            var rows = $('#seller-table tr:visible td .checkbox input');
            rows.prop('checked', $(this).prop('checked'));
            rm.responsivetable.totalSelectAll();
            rm.responsivetable.inCredit();
        });
    },

    totalSelectAll: function () {
        var selected = $('#seller-table tr:visible td .checkbox input'),
            totalCredit = 0,
            totalDebt = 0;

        selected.each(function () {
            var amount = $(this).closest('tr').find('.openAmount'),
                amountVal = amount.html(),
                valToNum = parseFloat(amountVal.replace(/\$|,/g, '')),
                valToNumFixed = rm.utilities.convertDollar(valToNum.toFixed(2)),
                total = 0;

            if ($(this).is(':checked')) {
                if (valToNum < 0) {

                    totalCredit += valToNum;
                } else {
                    totalDebt += valToNum;
                }
            }

            total = totalDebt + totalCredit;

            $('#selectedAmountValue').html(rm.utilities.convertDollar(total.toFixed(2)));
            $('.amountToPay').html(rm.utilities.convertDollar(total.toFixed(2)));
        });

    },

    clearSelected: function () {
        var checkboxes = $('#seller-table .checkbox input');

        checkboxes.prop('checked', false);
        rm.responsivetable.selectedTotal();
    },

    numRows: function () {
        var options = $('.num-rows .option'),
        that = this;

        options.on('click', function () {
            that.sortable.data('page-size', $(this).data('value'));
            $(this).addClass('active').siblings().removeClass('active');
            that.sortable.trigger('footable_redraw');
        });
    },

    filterListener: function () {
        var that = this;
        $('#billingUpdateFilter').on('click', function (e) {
            e.stopPropagation();
            e.preventDefault();
            that.updateTable(that.createRowBillings);
        });
    },

    resetWhenUpdate: function(){
    	$('label[for="openBalance"]').trigger('click');
    	$('#openBalanceValue').html('$0.00');
    	$('#selectedAmountValue').html('$0.00');
    	$('#selectAllBilling').prop('checked', false);
    	$('#email-invoices').addClass('disabled');
    },

    updateTable: function (createRowFunction) {
    	var that = this;
    	$('body').addClass('loading');
    	that.resetWhenUpdate();

    	setTimeout(function()
    	{
            //get the footable object
            var footable = $('.sortable').data('footable');
            var openAmount = 0;
            that.emptyFootable(footable);
            var json = that.ajaxGetData($('.sortable'));
            console.log(footable);
	        if(json !== null)
	        {
		        $.each(json, function(index, item) {
		            var row = createRowFunction(item);
		            //Adding row using footable plugin
		            footable.appendRow(row);
		            /*if(item.status === 'Open' && item.openAmount !== '') {
	                	openAmount += parseFloat(item.openAmount.replace(/\$|,/g, ''));
	                }*/
		        });
                if(json.hasOwnProperty('__OpenBalance')){
                    openAmount = parseFloat(json.__OpenBalance);
                    openAmount = isNaN(openAmount) ? 0 : openAmount;
                }
		        $('#openBalanceValue').html(rm.utilities.convertDollar(openAmount.toFixed(2)));
	        }

	        that.selectedTotal();
	        that.enableEmailButton();

            // Business Unit sub-title to be driven from the Business Unit filter in the billing and payment page
            // if ($('.billing-filters').length>0) {
                console.log($('#forUnit').data('unit'));
                $('#billingBusinessUnit').text($('#forUnit').attr('data-unit'));
            // }

	        $('body').removeClass('loading');
    	}, 50);
    },

    updateOrderHistory: function () {
        this.updateTable(this.createRowOrders);
    },

    emptyFootable: function (footable) {
    	var selected = $('table tbody tr'),
            table = $('.sortable').data('footable');
        //Removing rows body using footable plugin
        selected.each(function () {
        	table.removeRow(this);
        });
    },

    // Disable 'Pay Amount' Button if in credit
    inCredit: function () {
        var selectedRadio = $('.billing-options input[name=amountToPay]:checked'),
            value = selectedRadio.closest('.row').find('span');

        if (parseInt(rm.utilities.convertDollarToString(value.html()), 10) <= 0) {
            $('#payment-modal-trigger').addClass('disabled');
        } else {
            $('#payment-modal-trigger').removeClass('disabled');
            $('.amountToPay').html(value.html());
        }
    },

    customerActive: function (uid, acticeFlag) {
        $('#businessCustomerActive').attr('value', acticeFlag);
        $('#businessCustomerUid').attr('value', uid);
        $('#_sabmCustomerActiveForm').submit();
    },

    customerRemove: function (uid) {
        $('#businessCustomerUid').attr('value', uid);
        $('#_sabmCustomerActiveForm').submit();
    },

    bdePortal: function () {
      var resizeTimer;

      // Hack to always show hidden row
      $(window).on('resize', function(e) {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(function() {
          if($('.footable-toggle').length){
            $('.footable-toggle').click();
          }
        }, 250);
      });

      $(window).resize();
    },
    createUser: function () {
        $('#_sabmcreateUserForm').submit();
    },

	saveOrderTemplate: function() {

		$('.saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			e.preventDefault();

			if($('#template-name').val().trim() !== '') {
				$.ajax({
		            async: false,
		            global: false,
					url:'/your-business/saveOrderTemplate',
					type:'POST',
					data:{orderName:$('#template-name').val(),orderCode:$('#order-detail-inputID').val()},
		            dataType: 'json',
					success: function(result) {
						$('#template-name').val('');
						$('.magnific-template-order').magnificPopup('close');
						$('#globalMessages').empty();
						if(result){
							$('#globalMessages').append($('#templateSuccess').html());
						}else{
							$('#globalMessages').append($('#templateError').html());
						}
						rm.utilities.goBackTop();
					},
					error:function(result) {
						console.error(result);
						$('#templateError').show();
					}
				});
			}else{
				$('#empty-msg').removeClass('hidden');
			}

		});
	},

	addAddToCartListener: function(theClassName) {
		var startItemId = '';
		$(theClassName).each(function(i, item) {
			/* traps multiple reloads of the same elements - start */
			if (i === 0) {
				startItemId = item.id;
			} else {
				if (item.id === startItemId) {
					return false;
				}
			}
			/* traps multiple reloads of the same elements - end */

			if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
				var productObj = new rm.tagManager.ProductForTrackCart(
						$(this).data('currencycode'),
						$(this).data('name'),
						$(this).data('id'),
						$(this).data('price'),
						$(this).data('brand'),
						$(this).data('category'),
						$(this).data('variant'),
						$(this).data('position'),
						$(this).data('dealsflag'),
						$(this).data('quantity'),
						$(this).data('actionfield'));
				
				if (typeof rm.tagManager.trackCart !== 'undefined') {
					rm.tagManager.trackCart(productObj, 'add');
				}
			}
			
		});
	}
};

/*jshint unused:false*/
/* globals enquire*/
	'use strict';

rm.breadcrumb = {
	
	init:function(){
		this.updateMobileBreadcrumb();
	},
	
	updateMobileBreadcrumb:function(){
		var screenMobileMax = '767px';
		var screenTablet = '768px';
		enquire.register('screen and (max-width:'+screenMobileMax+')',function(){
			var lastLi = $('.breadcrumb li').last();
			
			//var firstLi = $('.breadcrumb li').first().
			$('.breadcrumb li').each(function(){
				if (($(this) === lastLi) || ($(this).html() !==lastLi.prev().html())){
					$(this).addClass('hidden');
				}
			});
		});
		enquire.register('screen and (min-width:'+screenTablet+')',function(){
			$('.breadcrumb li').each(function(){
				$(this).removeClass('hidden');
			});
		});
	}
};
/* globals window */

/*jshint unused:false*/
/* globals ACC */
/* globals enquire*/

	'use strict';

rm.templatesOrder = {

	init: function(){
		this.tableIntialize();
		this.rankingOrderTemplateButton();
		this.sortOrderTemplateButton();
	},

	tableIntialize:function(){
		var curURL = window.location.href;
		/*user click sorting, will disable the moveUp, moveDown*/
		if(curURL.match(/\?./)){
			rm.templatesOrder.disableMoving();
		}else{
			/*page first loading without the manual sort*/
			rm.templatesOrder.defaultTableInit();
		}

	},

	disableMoving:function(){
		$('a.js-move-up, a.js-move-down').addClass('disabled');
	},

	defaultTableInit: function(){
		var screenMobileMax = '767px';
		var screenTablet = '768px';

		enquire.register('screen and (max-width:'+screenMobileMax+')',function(){
			$('.template-sort div.visible-xs-block a.js-move-up').first().addClass('disabled');
			$('.template-sort div.visible-xs-block a.js-move-down').last().addClass('disabled');
		});
		enquire.register('screen and (min-width:'+screenTablet+')',function(){
			$('.template-sort div.hidden-xs a.js-move-up').first().addClass('disabled');
			$('.template-sort div.hidden-xs a.js-move-down').last().addClass('disabled');
		});


	},

	moveUp:function(){
		$('.js-expand-all').on('click',function(){
			var parent = $('div.deal-item').find('div.panel-group');
			$('.deal-item .panel-group .panel-collapse').collapse('show');

			$(this).parent().removeClass('collapsed');
			parent.find('.panel-collapse').addClass('in').removeAttr('style');
			parent.find('div.panel-heading').each(function(){
				var atag = $(this).find('a');
				var spans = atag.children();
				atag.removeClass('collapsed');
				spans.first().removeClass('hidden');
				spans.last().addClass('hidden');
			});
			$(this).addClass('hidden');
			$(this).prev().removeClass('hidden');
		});
	},

	moveDown:function(){
		$('.js-collapse-all').on('click',function(){
			var parent = $('div.deal-item').find('div.panel-group');
			$('.deal-item .panel-group .panel-collapse').collapse('hide');
			$(this).parent().addClass('collapsed');
			parent.find('.panel-collapse').removeClass('in');
			parent.find('div.panel-heading').each(function(){
				var atag = $(this).find('a');
				var spans = atag.children();
				atag.addClass('collapsed');
				spans.first().addClass('hidden');
				spans.last().removeClass('hidden');
			});
			$(this).addClass('hidden');
			$(this).next().removeClass('hidden');

		});
	},

    rankingOrderTemplate: function(code, direction){
    	$('body').addClass('loading');
    	$.ajax({
			url:'ordertemplates/move',
			type:'POST',
			data:{orderCode:code, directionUp:direction},
			success: function(result) {
				$('.templates-order-table').html($('.templates-order-table', result).html());
				rm.templatesOrder.tableIntialize();
				rm.templatesOrder.rankingOrderTemplateButton();
				$('body').removeClass('loading');
			},
			error:function(result) {
				$('body').removeClass('loading');
			}
		});
	},

	rankingOrderTemplateButton: function(){
		$('.js-move-up').on('click',function(e){
			e.preventDefault();
			if(!$(this).hasClass('disabled')) {
				rm.templatesOrder.rankingOrderTemplate($(this).data('ordercode'), true);
			}
		});
		$('.js-move-down').on('click',function(e){
			e.preventDefault();
			if(!$(this).hasClass('disabled')) {
				rm.templatesOrder.rankingOrderTemplate($(this).data('ordercode'), false);
			}
		});
	},

	sortOrderTemplateButton: function(){
		$('.columnSort').on('click touchstart',function(e){
			e.preventDefault();
			rm.templatesOrder.sortOrderTemplate($(this).data('sort-asc'));
		});
	},

	sortOrderTemplate: function(sort){
		$('body').addClass('loading');
		$('a.js-move-up, a.js-move-down').addClass('disabled');
    	$.ajax({
			url:'ordertemplates/sort',
			type:'POST',
			data:{sort:sort},
			success: function(result) {
				$('.templates-order-table').html($('.templates-order-table', result).html());
				rm.templatesOrder.tableIntialize();
				rm.templatesOrder.rankingOrderTemplateButton();
				if(sort !== '') {
					$('a.js-move-up, a.js-move-down').addClass('disabled');
				}
				$('body').removeClass('loading');
			},
			error:function(result) {
				$('body').removeClass('loading');
			}
		});
	},
    addToTemplate: function(orderNo){
    	if($('.view-only-mode').length){
			return;
		}

    	var url = $('.addToTemplate-hide').val();

		$.ajax({
			url:url,
			type:'POST',
			data: {'orderCode':orderNo},
			cache:false,
			success: function(result) {
				if(result.addToCartForErrorLayer){
					$('#globalMessages').empty();
					$('#globalMessages').append(result.addToCartForErrorLayer);
				}else if(result.excludedError){
					$('#orderHistoryPopUpError').html(result.excludedError);
                	$('#outOfStockPopup').modal();
				}else{
					$('#globalMessages').empty();
				}
				if (result) {
					var miniCartCountBeforeRefresh = $('.miniCart .count').text();
					
					ACC.product.displayAddToCartPopup(result);
					ACC.minicart.refreshMiniCartCount();
					
					var miniCartCountAfterRefresh = $('.miniCart .count').text();
					if (miniCartCountBeforeRefresh === miniCartCountAfterRefresh) {
						$('#addToCartLayer').hide();
					}
					
					ACC.common.refreshScreenReaderBuffer();
				}
			},
			error:function(result) {
				$('#globalMessages').append(result);
				ACC.common.refreshScreenReaderBuffer();
			}
		});
	},
};
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
                $(this).addClass('hovered open');
                rm.navigation.hoverTablet($(this));
            }
        }).on('mouseleave', function(){
            if(that.resizeWidthCheck() >= bpmobile){
                $(this).removeClass('hovered open');
                rm.navigation.hoverTablet($(this));
            }
        });
    },
    createListeners:function($mobileHead){
        var that = this;
         //Create Listeners
        $(document).on('click', '.megamenu .dropdown-menu', function(e) {
            e.stopPropagation();
        }).on('mouseover', '.megamenu .dropdown-menu, .megamenu-fw .dropdown-menu', function() {
            that.setHoverFlag($(this));
        }).on('mouseout', '.megamenu .dropdown-menu, .megamenu-fw .dropdown-menu', function() {
            that.removeHoverFlag($(this));
        }).on('click', '.mobile-head .select-btn', function() {
            that.rotateDDCaret($mobileHead);
        }).on('click', '.megamenu .dropdown-link, #header .dropdown-link', function(e) {
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
        $this.parent().addClass('hovered open');
    },
    //This removes it when the user is no longer over the parent
    removeHoverFlag:function($this){
        $this.parent().removeClass('hovered open');
    }

};
/* globals window */

/* globals ACC */
/* globals enquire*/
/* globals ACC */

/*jshint unused:false*/

	'use strict';

rm.templatesOrderDetail = {

	orderCode: $('#orderTemplateCode').val(),

	init: function(){
		rm.utilities.bindtemplateInlineEdit('#templateTitle');
		//for templates order create
		rm.templatesOrderDetail.createListeners();
		ACC.product.addListeners();
		this.enableOrderTemplateDetailDragAndDrop();
	},

	createListeners: function(){
		// Quantity Incrementors
		$('.save-template').on('click',function(){
			if(!$(this).children('button').hasClass('disabled')){
				// rm.templatesOrderDetail.removeAndSaveProduct($(this));
				rm.templatesOrderDetail.updateQuantityOrUnit($(this));
			}
		});
		$('.addToCartForTemplate').on('click',function(){
			if($('.isOutOfStock').val() === 'true'){
				$('#outOfStockPopup h2').empty().append(ACC.outOfStock);
				$('#outOfStockPopup').modal();
				return;
			}
			if(!$(this).children('button').hasClass('disabled')){

		    	if($('.disabled-productPackTypeNotAllowed').length > 0) {
		    	    $('#outOfStockPopup h2').empty().append(ACC.deliveryPackDate);
		    		$('#outOfStockPopup').modal();
		    		return;
		    	}

				// rm.templatesOrderDetail.removeAndSaveProduct($(this));
				rm.templatesOrderDetail.addToCartForTemplate($(this));
			}
		});
		$('#addToCartForTemplateFromPopUp').on('click',function(){
			if(!$(this).children('button').hasClass('disabled')){
				rm.templatesOrderDetail.addToCartForTemplate($(this));
			}
		});

		// Removing a product from template
		$('.removeProductTemplate').on('click',function(e){
			e.preventDefault();
			rm.templatesOrderDetail.removeProduct($(this));
			rm.templatesOrderDetail.enableSaveTemplate();
		});
		// Qty changed
		$('.select-quantity .up, .select-quantity .down, .select-list .select-items > li')
		.on('click touchstart',function() {
			var input = $(this).closest('.row').find('.qty-input'),
				min = input.data('minqty');

			setTimeout(function(){
				if(isNaN(input.val())){
					input.val(min);
				}
			},50);

			if(!$(this).hasClass('disabled')){
				rm.templatesOrderDetail.enableSaveTemplate();
			}
		});

		$('.qty-input')
		.on('keyup',function() {
			var that = $(this),
				min = that.data('minqty');
			setTimeout(function(){
				if(that.val() === ''){
					that.val(min);
				}

				rm.templatesOrderDetail.enableSaveTemplate();
			},2000);
		});


		$('.select-quantity .up, .select-quantity .down, .select-list .select-items > li .qty-counter')
		.on('input oninput change keyup mousemove click focus mousedown', function() {
			var values = $.map($('.qty-counter'), function(item) {
				return parseInt( $(item).val() );
			 });
			console.log(values);
			var sum = values.reduce(function (a, b) {
				return a + b;
			});
			console.log(sum);

 			$('.template-actions .addToCartForTemplate').prop('disabled', sum === 0);
        });

		// Title change
		$('#templateTitleInput').change(function() {
			rm.templatesOrderDetail.enableSaveTemplate();
		});

        // Minimum Stock on Hand
        $('.minSOH').on('keyup',function() {
            var $this = $(this);

            setTimeout(function() {
               rm.templatesOrderDetail.updateMinSOH($this);
            }, 2000);
        });
	},

	removeProduct: function(obj){
		$('body').addClass('loading');
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		if($('#'+entryNumber+'_isOutOfStock').val() === 'true'){
			$('#'+entryNumber+'_isOutOfStock').val('false');
		}
		obj.closest('.table-row').hide();
//		obj.closest('.table-row').removeClass('entryNumber');
		var templateEntryNumber = $('#removeTemplateEntryNumbers').val();
		if(templateEntryNumber === '' || templateEntryNumber === null){
			$('#removeTemplateEntryNumbers').val(entryNumber);
		}else{
			$('#removeTemplateEntryNumbers').val(templateEntryNumber + ',' + entryNumber);
		}

		$('body').removeClass('loading');

	},

	enableSaveTemplate: function(){


        $(window).on('beforeunload', function() {
            return 'You have unsaved changes!';
        });

		$('.save-template').removeClass('disabled');
		$('.template-actions').addClass('disabled');
		$('.template-actions .hrefAddtoCart').addClass('notActive');
	},

	disableSaveTemplate: function(){

		console.log('disableSaveTemplate');

		$(window).off('beforeunload');

		$('.save-template').addClass('disabled');
		$('.template-actions').removeClass('disabled');
		$('.template-actions .hrefAddtoCart').removeClass('notActive');
	},

	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj){
		$('body').addClass('loading');
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		var qty = obj.closest('.table-row').find('.qty-input').val();
		var unit = obj.closest('.table-row').find('.select-btn').attr('data-value');
		var entryNumberForRemove = $('#removeTemplateEntryNumbers').val();
		var dataPost = {'code': rm.templatesOrderDetail.orderCode,
						'name': $('#templateTitle').text(),
						'entries': [],
						'entryNumber': entryNumberForRemove
						};

		$('.table-row').each(function () {
			if($(this).css('display') !== 'none'){
				var entryNumber = $(this).find('.entryNumber').val();
				var qty = $(this).find('.qty-input').val();
				var unit = $(this).find('.select-btn').attr('data-value');
				var entry = {};
				entry.entryNumber = entryNumber;
				entry.quantity = qty;
				entry.unit = unit;
				dataPost.entries.push(entry);
			}

		});

		$('#globalMessages .succesSavingTemplate').hide();
		$('#globalMessages .errorSavingTemplate').hide();

		$.ajax({
			url:'/your-business/orderTemplateDetail/updateTemplate',
			type:'POST',
			dataType: 'json',
			data: JSON.stringify(dataPost),
            contentType: 'application/json',
			success: function(result) {
				console.log(result);
				if(result) {
					var allSequence = [];
					$('.table-row').each(function () {
						if($(this).css('display') !== 'none'){
							var sequenceNumber = $(this).find('.sequenceNumber').val();
							allSequence.push(sequenceNumber);
						}
					});
					allSequence.sort(function(a, b){return a-b;});
					jQuery.each(allSequence, function(index, item) {
					    $('.table-row').each(function () {
							if($(this).css('display') !== 'none'){
								var sequenceNumber = $(this).find('.sequenceNumber').val();
								if(sequenceNumber === item){
									$(this).find('.sequenceNumber').val(index+1);
								}
							}
						});
					});
					$('#globalMessages').html($('#succesSavingTemplate').html());
					$('#globalMessages .succesSavingTemplate').show();
				} else {
					$('#globalMessages').html($('#errorSavingTemplate').html());
					$('#globalMessages .errorSavingTemplate').show();
				}
				$('body').removeClass('loading');
				rm.templatesOrderDetail.disableSaveTemplate();
			},
			error:function(result) {
				$('#errorSavingTemplate').show();
				$('body').removeClass('loading');
			}

		});
		$('#removeTemplateEntryNumbers').val('');
	},

	//Add to order
	addToCartForTemplate: function(obj){
		var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
		var qty = obj.closest('.table-row').find('.qty-input').val();
		var unit = obj.closest('.table-row').find('.select-btn').attr('data-value');
		var entryNumberForRemove = $('#removeTemplateEntryNumbers').val();
		var dataPost = {'code': rm.templatesOrderDetail.orderCode,
						'name': $('#templateTitle').text(),
						'entries': [],
						'entryNumber': entryNumberForRemove
						};

		$('.table-row').each(function () {
			if($(this).css('display') !== 'none'){
				var entryNumber = $(this).find('.entryNumber').val();
				var qty = $(this).find('.qty-input').val();
				var unit = $(this).find('.select-btn').attr('data-value');
				var entry = {};

				if($('#'+entryNumber+'_isOutOfStock').val() !== 'true' && !$(this).hasClass('disabled-productPackTypeNotAllowed')){
				entry.entryNumber = entryNumber;
				entry.quantity = qty;
				entry.unit = unit;
				dataPost.entries.push(entry);

				}
			}
		});

		$('#globalMessages .succesSavingTemplate').hide();
		$('#globalMessages .errorSavingTemplate').hide();

		$.ajax({
			url:'/your-business/orderTemplateDetail/addToCart/' + rm.templatesOrderDetail.orderCode,
			type:'POST',
			dataType: 'json',
			data: JSON.stringify(dataPost),
            contentType: 'application/json',
			success: function(result) {

				if(result.addToCartForErrorLayer){
					$('#globalMessages').empty();
					$('#globalMessages').append(result.addToCartForErrorLayer);
				}else{
					$('#globalMessages').empty();
				}
				if (result) {
					ACC.product.displayAddToCartPopup(result);
					ACC.minicart.refreshMiniCartCount();
					ACC.common.refreshScreenReaderBuffer();
				}
                if (result.hasOwnProperty('orderTemplateMaxOrderError')) {
                    var row = $('.page-orderTemplateDetail .table-row');
                    for(var i = 0; i < row.length; i ++) {
                        var id = row[i].dataset.productCode;
                        var text = '';
                        for (var j = 0; j < result.orderTemplateMaxOrderError.length; j ++) {
                            if (result.orderTemplateMaxOrderError[j][id]) {
                                text = result.orderTemplateMaxOrderError[j][id]
                            }
                        }
                        $('.page-orderTemplateDetail .order-error-message-' + id).text(text);
                    }
                }
			},
			error:function(result) {
				$('#errorSavingTemplate').show();
				$('body').removeClass('loading');
			}

		});
		$('#removeTemplateEntryNumbers').val('');
	},
    updateMinSOH: function(obj) {
        var entryNumber = obj.closest('.table-row').find('.entryNumber').val();
        var minStockOnHand = obj.val();

        $.ajax({
            url:'/your-business/orderTemplateDetail/updateMinStock',
            type:'POST',
            data:{
                orderCode: rm.templatesOrderDetail.orderCode,
                entryNumber: entryNumber,
                minStockOnHand: minStockOnHand
            },
            success: function(result) {
                console.log('Successfully updated Min SOH to', minStockOnHand);
            },
            error: function(result) {
               console.error('Error updating Minimum Stock On Hand!');
            }
        });
    },

    moveOrderTemplateDetail: function(orderCode, entryNumber, newEntryNum){
    	$('body').addClass('loading');
    	$.ajax({
			url:'/your-business/orderTemplateDetail/move',
			type:'POST',
			data:{
				orderCode: orderCode,
				entryNumber: entryNumber,
				newEntryNum: newEntryNum
			},
			success: function(result) {
				// $('.templates-order-table').html($('.templates-order-table', result).html());
				// rm.templatesOrderDetail.init();
				$('body').removeClass('loading');
			},
			error: function(result) {
				$('body').removeClass('loading');
			}
		});
	},

	enableOrderTemplateDetailDragAndDrop: function () {
		var previousIndex = 0;
		$('.templates-order-table').sortable({
			items: 'div.table-row',
			start: function (event, ui) {
				previousIndex = ui.item.index();
			},
			update: function(event, ui) {
				var orderCode = ui.item.data('order-code');
				var entryNumber = ui.item.find('.sequenceNumber').val();
				var index = ui.item.index();
				var isDraggedUp = index < previousIndex;
				var replacedRowIndex = index + (isDraggedUp ? 1 : - 1);
				var newEntryNum = $($('.templates-order-table > div').get(replacedRowIndex))
					.find('.sequenceNumber').val();
				var parseEntryNumber = parseInt(entryNumber);
				var parseNewEntryNum = parseInt(newEntryNum);
				var isUp = parseEntryNumber > parseNewEntryNum;
				var newSequence = isUp ? 1 : -1;
				$('.table-row').each(function () {
					var indexEntryNo = parseInt($(this).find('.sequenceNumber').val());
					if($(this).css('display') !== 'none'){
						if (indexEntryNo === parseEntryNumber)
						{
							$(this).find('.sequenceNumber').val(parseNewEntryNum);
						}
						else if ((indexEntryNo > parseEntryNumber && indexEntryNo <= parseNewEntryNum) || (indexEntryNo >= parseNewEntryNum && indexEntryNo < parseEntryNumber))
						{
							var tempSequenceNumber = indexEntryNo + parseInt(newSequence);
							$(this).find('.sequenceNumber').val(tempSequenceNumber);
						}
					}
				});
				console.log('update: ', orderCode, isDraggedUp, entryNumber, newEntryNum);

				rm.templatesOrderDetail.moveOrderTemplateDetail(orderCode, entryNumber, newEntryNum);
			}
		});
	},
};
/* globals window */

/*jshint unused:false*/
/* globals ACC */
/* globals enquire*/


	'use strict';

rm.createordertemplate = {
	
	init: function(){
		this.createNewOrderTemplate();
	},
	
	createNewOrderTemplate: function() {
		
		
		$('.magnific-template-order').on('click',function(){
			$('#create-empty-msg').addClass('hidden');
		});
		
		$('#create-new-template .saveTemplateBtn').on('click touchstart', function(e){
			e.stopPropagation();
			
			if($('#create-template-name').val().trim() === '') {
				$('#create-empty-msg').removeClass('hidden');
				e.preventDefault();
			}
		});
	}
};
/* globals window */
/* globals document */
/* globals ACC */

'use strict';

rm.header = {
	originalWidth: 0,
	init : function() {
		this.dropdowns();
		this.topNavMenu();
		this.dealsCounter();
		this.removeHeaderLink();
		this.minicart();
		this.chooseUpdateFunction();
		rm.header.minicart.updatable=true;
	},
	minicart : function() {
		var link = $('.miniCart .items');
		var timeoutId;
		if(!$('body').hasClass('view-only-mode')){

			$('.global-header-list .miniCart').on('mouseenter',function(){
				if(!link.hasClass('open')){
			        $.ajax({
			            url:'/sabmStore/en/cart/view',
			            type:'POST',
			            success: function(result) {
			                ACC.product.viewCartPopup(result);
			                link.addClass('open');
			                $('.viewCartPopup .row.list-qty').hide();
			                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
			                $('.select-btn').show();

			               // var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			    			// Store value in data-value
			                /* $selectBtn.each(function(){
			    				var $firstItem = $(this).next('ul').children('li').first();
			    				$(this).text($firstItem.html());
			    			});
							*/
			    			rm.header.removeItemBind();
			    			rm.header.bingCartEntry();

			            },
			            error:function(result) {
			                console.log('error'+result.responseText);
			            }
			        });
			    } else {
			        $('#addToCartLayer .close').click();
			    }
			}).on('mouseleave',function(){
				$('#addToCartLayer').hide();
				link.removeClass('open');
				$('body').css('overflow','auto');
			});

		    if (typeof timeoutId !== 'undefined')
			{
				clearTimeout(timeoutId);
			}
			/* timeoutId = setTimeout(function ()
			{
				$('#addToCartLayer').hide();
			}, 5000); */
		    /*
			link.on('click',function(){
				var that = $(this);

			    if(!$(this).hasClass('open')){
			        $.ajax({
			            url:'/sabmStore/en/cart/view',
			            type:'POST',
			            success: function(result) {
			                ACC.product.viewCartPopup(result);
			                that.addClass('open');

			                $('.viewCartPopup .row.list-qty').hide();
			                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
			                $('.select-btn').show();

			                var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

			    			// Store value in data-value
			    			$selectBtn.each(function(){
			    				var $firstItem = $(this).next('ul').children('li').first();
			    				$(this).text($firstItem.html());
			    			});

			    			rm.header.removeItemBind();
			    			rm.header.bingCartEntry();
			            },
			            error:function(result) {
			                console.log('error'+result.responseText);
			            }
			        });
			    } else {
			        $('#addToCartLayer .close').click();
			    }
				if (typeof timeoutId !== 'undefined')
				{
					clearTimeout(timeoutId);
				}
				timeoutId = setTimeout(function ()
				{
					$('#addToCartLayer').hide();
					link.removeClass('open');
					$('html').removeClass('overflow-hidden');
				}, 5000);
			}); */

		}
	},

	topNavMenu: function(){
		$(document).ready(function() {

			var $selectBtn = $('.select-btn.header');
			$selectBtn.on('click',function(){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Account Info',window.location.href);
				}
			});

			$('.btn-changeDeliveryDate').on('click',function(){
				$('.delivery-header.delivery-header-desktop').mouseenter();
			});

			/* display header datepicker by default */
			/* show/hide header datepicker on mouseenter/mouseleave */
			$('.delivery-header.delivery-header-desktop').on('mouseenter',function(){
				/* checking if there's no datepicker opened */
				if($('.datepicker').length === 0){
					$('.delivery-header-input', $(this)).datepicker('show');
				}
			}).on('mouseleave', function(){
				if(!$('.delivery-header').hasClass('open')){
					$('.datepicker', $(this)).remove();
				}
			});

			$('.delivery-header.delivery-header-mobile').on('click',function(){
				$('.delivery-header-input', $(this)).datepicker('show');
			});

			$('.cart-datepicker').on('click',function(){
				$('.global-header-list .datepicker').remove();
				rm.header.showDatepickerDaysBorderRadius();
			});

			/* add hyperlink to the a tag when the data-disabled attribute is false */
			$('.sub-menu .sub-menu-item a').each(function(){
				if(typeof($(this).data('disabled')) !==  'undefined'){
					if($(this).data('disabled') === false){
						$(this).attr('href' , $(this).data('href'));
					}else{
						$(this).addClass('disabled');
					}
				}
			});

			var windowHeight = $(window).height(),
			allowedHeight = windowHeight - 300;

			var globalHeaderList = '.global-header-list';
			$(globalHeaderList +' .select-list .select-items').css('max-height',allowedHeight);

		});
	},

	dropdowns : function() {
		$(document).ready(function() {
			/*
			var $selectBtn = $('.select-btn.header');

			// Store value in data-value
			$selectBtn.each(function() {
				if(!$('#userSelectBusinessUnit').val()) {
					var $firstItem = $(this).next('ul').children('li').first();
					$(this).text($firstItem.attr('data-value'));
				}
			}); */

			$(document).on('click touchend', '.select-items.header li', function() {
				//var $selectBtn = $(this).parent().siblings('.select-btn');

				if(!($(this).hasClass('bde-view-only') && $(this).parents('.view-only-mode').length)){
					if ($(this).attr('data-value')) {
						//click the 'li' area to trigger the 'a' label
						$('a',this)[0].click();
						//$selectBtn.text($(this).attr('data-value'));
					} else {
						if (!$('.unsaved-changes').length) {
							window.location=$(this).attr('data-url');
						}
					}
				}
			});
			rm.header.originalWidth = $('div.select-list').innerWidth();
		});
	},

	dealsCounter : function(){
		var pop = document.getElementById('dealsAudio');
		$(document).ready(function(){
			var playAnimation = true;
			var curLoginURL = window.location.href;
			if(curLoginURL.indexOf('login')>=0 || curLoginURL.indexOf('paSearch')>=0){
				rm.utilities.addItemToStorage('PlayAnimation','TRUE');
				playAnimation = true;
			}

			if(rm.utilities.getItemFromStorage('PlayAnimation') !== null){
				if(rm.utilities.getItemFromStorage('PlayAnimation') === 'FALSE'){
					playAnimation = false;
				}
			}
			if(playAnimation && !$('.d-content .d-content-trans').hasClass('no_transform') && $(window).width() > 768 && $('.d-content').length){
				setTimeout(function(){
					$('.d-content .d-content-trans').addClass('ready');
					console.log('loading');
					setTimeout(function(){
						pop.play();
						rm.utilities.addItemToStorage('PlayAnimation','FALSE');
					},50);
				},1000);
			}
		});
	},


	removeHeaderLink: function() {
		$(document).ready(function() {
			if(window.location.href.indexOf('paSearch') > -1){
				var headlist = $('.global-header-list');

				headlist.addClass('text-right');
				headlist.find('ul').addClass('hidden');
				$('body').addClass('paSearch');
				$('.siteLogo').find('a').removeAttr('href');
				$('.js-only-signout').removeClass('hidden');
				$('#breadcrumb ol li:first').remove();
	        }

		});
	},

	removeItemBind: function(){

		$('.submitRemoveProduct').on('click', function (event){ // On Delete mini cart
			event.preventDefault();

			var prodid = $(this).data('index'),
				form = $('#updateMiniCartForm' + prodid),
				cartQuantity = form.find('input[name=quantity]'),
				entryNumber = form.find('input[name=entryNumber]').val(),
				unit = form.find('input[name=unit]').val(),
				elementWithProductData = $(this).closest('.popupCartItem').find('.js-track-product-link');
				cartQuantity.attr('value',0);

			$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
				$('body').removeClass('loading');
				if(result.isLost){
						var titles = result.title;
						var resultTitleHtml = '';
                   	 	var productsInLoseDealModal = [];
						if(titles){
							 for (var i = 0; i < titles.length; i++) {
								 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                            	 productsInLoseDealModal.push({
          			        		'name'		: elementWithProductData.data('name'),
          			            	'id'		: elementWithProductData.data('id'),
          			            	'price'		: elementWithProductData.data('price'),
          			            	'brand'		: elementWithProductData.data('brand'),
          			            	'category'	: elementWithProductData.data('category'),
          			            	'variant'	: elementWithProductData.data('variant'),
          			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
          			            	'position'	: (i+1),
          			            	'dealsFlag'	: true
          			        	});
	                        }
				        }
						$('#loseDealPopup h3').html(resultTitleHtml);

						rm.cart.loseDealPopup(form,'remove');

                        if (productsInLoseDealModal.length > 0) {
             				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
             					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
             				}
             	        }

						$('#loseDealPopup').attr('data-item', prodid);
						$('#loseDealPopup').attr('data-qty', cartQuantity.val());
						} else {
						if($('.popupCartItem').length > 1){
							//rm.cart.resetCart();
							rm.header.resetMiniCart();
						} else {
							rm.cart.refreshPage();
						}
				}
			});
		});
	},

		chooseUpdateFunction: function(){
		$('.lose-deal-minicart .btn-primary').one('click',function(){
			var modal = $(this).closest('.lose-deal-minicart'),
				item = modal.attr('data-item'),
				form = $('#updateMiniCartForm' + item),
				entryLoopIndex = form.closest('.popupCartItem').find('.entry-loop-index').val(),
				initialQuantity = $('#initialQuantity'+entryLoopIndex).val(),
				cartQuantity = form.find('input[name=quantity]').val(),
				$elementWithProductData = form.closest('.popupCartItem').find('.js-track-product-link');

				rm.header.updateQuantityOrUnit(form);

				if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
					var productObj = new rm.tagManager.ProductForTrackCart(
							$elementWithProductData.data('currencycode'),
							$elementWithProductData.data('name'),
							$elementWithProductData.data('id'),
							$elementWithProductData.data('price'),
							$elementWithProductData.data('brand'),
							$elementWithProductData.data('category'),
							$elementWithProductData.data('variant'),
							$elementWithProductData.data('position'),
							true,
							(initialQuantity !== cartQuantity ? parseInt(initialQuantity) - parseInt(cartQuantity) : initialQuantity),
							$elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal');

					if (typeof rm.tagManager.trackCart !== 'undefined') {
						rm.tagManager.trackCart(productObj, 'remove');
					}
				}
		});


	},

	bingCartEntry: function(){
		//rm.utilities.needClamp('cartItemClamp-2',2,'clamp-2');
		//rm.utilities.needClamp('cartItemClamp-1',1,'clamp-1');
		// Quantity Incrementors
		$('.popupCartItem .select-quantity .up').on('click touchstart',function(){

			if(rm.header.minicart.updatable){
				var $input = $(this).closest('.select-quantity').find('.qty-input');

				if($input.val() < 999){
					/*var entryLoopIndex = $(this).closest('.cartRow').find('.entry-loop-index').val();*/
					var entryLoopIndex = $(this).closest('.popupCartItem').find('.entry-loop-index').val();
					var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
					$newQuantity.val(parseInt($input.val()) + 1);
					rm.header.updateQuantityOrUnit($(this));
				}
			}

		});



		$('.popupCartItem .select-quantity .down').on('click touchstart',function(){
			if(rm.header.minicart.updatable && !$(this).hasClass('disabled')){
				var that = $(this),
					prodid = that.closest('.popupCartItem').data('index'),
					$input = that.closest('.select-quantity').find('.qty-input'),
					//form = $('#updateCartForm' + prodid),
					form = $('#updateMiniCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					unit = form.find('input[name=unit]').val(),
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

				cartQuantity.val(parseInt($input.val())-1);

				$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
					$('body').removeClass('loading');
					if(result.isLost){
                             var titles = result.title;
                             var resultTitleHtml = '';
                        	 var productsInLoseDealModal = [];
                             if(titles){
                                 for (var i = 0; i < titles.length; i++) {
                                     resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

                                	 productsInLoseDealModal.push({
              			        		'name'		: elementWithProductData.data('name'),
              			            	'id'		: elementWithProductData.data('id'),
              			            	'price'		: elementWithProductData.data('price'),
              			            	'brand'		: elementWithProductData.data('brand'),
              			            	'category'	: elementWithProductData.data('category'),
              			            	'variant'	: elementWithProductData.data('variant'),
              			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
              			            	'position'	: (i+1),
              			            	'dealsFlag'	: true
              			        	});
                                 }
                            }
                            $('body').removeClass('loading');

                            $('#loseDealPopupReduce h3').html(resultTitleHtml);
                            $input.val(parseInt($input.val()));
                            rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

                            //$('#loseDealPopupReduce').attr('data-item', entryNumber);
                            $('#loseDealPopupReduce').attr('data-item', prodid);
                            $('#loseDealPopupReduce').attr('data-qty', $input.val());
                    } else {
                    		$('body').removeClass('loading');
                    		console.log(form.find('.base-quantity'));
                    		rm.header.changeBaseQuantity(result.newQty,that);
                    		rm.header.displayUpdatedQuantityAndUnit(cartQuantity.val(), that);
                            form.find('#initialQuantity' + prodid).val(parseInt($input.val()));
    						ACC.minicart.refreshMiniCartCount();
                    }

				});
			}
		});

		// Quantity input. If the user input 0,set the quantity to 1.
		$('.popupCartItem .select-quantity input').each(function(){
			if(rm.header.minicart.updatable){
				var that = $(this);
				var typingTimer;                //timer identifier
				var doneTypingInterval = 1500;  //time in ms, 5 second for example

				//on keyup, start the countdown
				$(this).on('keyup', function () {
				  clearTimeout(typingTimer);

				  typingTimer = setTimeout(doneTyping, doneTypingInterval);

				});

				//on keydown, clear the countdown
				$(this).on('keydown', function (event) {
					if(event.keyCode === 13){
						return false;
					}
					clearTimeout(typingTimer);
				});

				//user is "finished typing," do something
				var doneTyping = function () {
				  //var prodid = that.closest('.cartRow').data('index'),
				  var prodid = that.closest('.popupCartItem').data('index'),
				  	//form = $('#updateCartForm' + prodid),
				  	form = $('#updateMiniCartForm' + prodid),
				  	cartQuantity = form.find('input[name=quantity]'),
				  	tempQuantity = parseInt(that.val(),10),
				  	entryNumber = form.find('input[name=entryNumber]').val(),
				  	unit = form.find('input[name=unit]').val(),
				  	notIsLost = false,
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

	  				if(isNaN(tempQuantity)){
	  					notIsLost = true;
	  					that.val(parseInt(1));
	  				}else if(tempQuantity <= 0){
	  					notIsLost = true;
	  					that.val(parseInt(1));
	  				}else{
	  					that.val(tempQuantity);
	  				}

	  				cartQuantity.val(tempQuantity);

	  				if(!notIsLost){
	  					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
	  						$('body').removeClass('loading');
	  						if(result.isLost){
	  								var titles = result.title;
	  								 var resultTitleHtml = '';
		  								var productsInLoseDealModal = [];
	  								 if(titles){
	  									 for (var i = 0; i < titles.length; i++) {
	  										 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

	  										 productsInLoseDealModal.push({
	  	             			        		'name'		: elementWithProductData.data('name'),
	  	             			            	'id'		: elementWithProductData.data('id'),
	  	             			            	'price'		: elementWithProductData.data('price'),
	  	             			            	'brand'		: elementWithProductData.data('brand'),
	  	             			            	'category'	: elementWithProductData.data('category'),
	  	             			            	'variant'	: elementWithProductData.data('variant'),
	  	             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
	  	             			            	'position'	: (i+1),
	  	             			            	'dealsFlag'	: true
	  	             			        	 });
	                                      }
	  						        }
	  								$('#loseDealPopupReduce h3').html(resultTitleHtml);
	  								rm.cart.loseDealPopup(form,'reduce');

	  	                            if (productsInLoseDealModal.length > 0) {
	  	                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
	  	                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
	  	                 				}
	  	                 	        }

	  								$('#loseDealPopupReduce').attr('data-item', prodid);
	  								$('#loseDealPopupReduce').attr('data-qty', that.val());
	  						} else {
	  							var entryLoopIndex,
	  								$newQuantity;

	  							entryLoopIndex = that.closest('.popupCartItem').find('.entry-loop-index').val();
	  							$newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
	  							$newQuantity.val(that.val());
	  							//form.find('.base-quantity span').text(result.newQty);
	                    		rm.header.changeBaseQuantity(result.newQty,that);
	                    		rm.header.displayUpdatedQuantityAndUnit($newQuantity.val(), that);
	  							rm.customUI.checkChangeable(that);
	  							ACC.minicart.refreshMiniCartCount();
	  						}
	  					});
	  				}
				};
			}
		});

		// Change value of hidden field to selection
		$('.popupCartItem .select-items li').on('click', function(){
			if(rm.header.minicart.updatable){
				var that = $(this),
					prodid = that.closest('.popupCartItem').data('index'),
					input = that.closest('.popupCartItem').find('.qty-input'),
					form = $('#updateMiniCartForm' + prodid),
					cartQuantity = form.find('input[name=quantity]'),
					entryNumber = form.find('input[name=entryNumber]').val(),
					entryLoopIndex = that.closest('.popupCartItem').find('.entry-loop-index').val(),
					unit = that.data('value'),
					selectBtn = that.closest('.select-list').find('.select-btn'),
					$updateEntryUnit = that.closest('.popupCartItem').find('#updateEntryUnit'+entryLoopIndex),
					elementWithProductData = $(that).closest('.popupCartItem').find('.js-track-product-link');

					selectBtn.text($(this).text());

					$.when(rm.cart.checkLoseDeal(entryNumber,cartQuantity,unit)).done(function(result){
						$updateEntryUnit.val(that.attr('data-value'));

						$('body').removeClass('loading');

						$('.select-items').hide();

						if(result.isLost){
							var titles = result.title;
							var resultTitleHtml = '';
							var productsInLoseDealModal = [];
							if(titles){
								 for (var i = 0; i < titles.length; i++) {
									 resultTitleHtml += '<div class="title">'+titles[i]+'</div>';

									 productsInLoseDealModal.push({
	             			        		'name'		: elementWithProductData.data('name'),
	             			            	'id'		: elementWithProductData.data('id'),
	             			            	'price'		: elementWithProductData.data('price'),
	             			            	'brand'		: elementWithProductData.data('brand'),
	             			            	'category'	: elementWithProductData.data('category'),
	             			            	'variant'	: elementWithProductData.data('variant'),
	             			            	'list'		: elementWithProductData.data('actionfield') + '/About To Lose A Deal Modal',
	             			            	'position'	: (i+1),
	             			            	'dealsFlag'	: true
	             			        	 });
		                        }
					        }
							$('#loseDealPopupReduce h3').html(resultTitleHtml);
							rm.cart.loseDealPopup(form,'reduce');

                            if (productsInLoseDealModal.length > 0) {
                 				if (typeof rm.tagManager.trackProductImpressionAndPosition !== 'undefined') {
                 					rm.tagManager.trackProductImpressionAndPosition(productsInLoseDealModal);
                 				}
                 	        }

							$('#loseDealPopupReduce').attr('data-item', prodid);
							$('#loseDealPopupReduce').attr('data-qty', input.val());
						} else {
							//form.find('.base-quantity span').text(result.newQty);
                    		rm.header.changeBaseQuantity(result.newQty,that);
                    		rm.header.displayUpdatedQuantityAndUnit(cartQuantity.val(), that);
							// rm.cart.resetCart();
							ACC.minicart.refreshMiniCartCount();
						}
					});
			}
		});

		$('.popupCartItem .minicart-update-item').each(function(){
			if(rm.header.minicart.updatable){
				$(this).on('click', function() {
					// restrict all other items from being editable
					$('.minicart-update-item').show();
					$('.viewCartPopup .row.list-qty').hide();
					$('.viewCartPopup .minicart-delete-item').children('.inline.submitRemoveProduct').hide();

					// only one item (the one being clicked on) should be editable at a time
					$(this).hide();
					$(this).next('.viewCartPopup .row.list-qty').show();
					$(this).nextAll('.viewCartPopup .minicart-delete-item').children('.inline.submitRemoveProduct').first().show();
				});
			}
		});
	},


	//update the quantity or Unit by ajax
	updateQuantityOrUnit: function(obj){

		var entryLoopIndex = obj.closest('.popupCartItem').find('.entry-loop-index').val();
		var $input = obj.closest('.select-quantity').find('.qty-input');
		//var $form = $('#updateCartForm'+entryLoopIndex);
		var $form = $('#updateMiniCartForm'+entryLoopIndex);
		var $selectBtn = obj.parent().siblings('.select-btn');

		var $initialQuantity = $('#initialQuantity'+entryLoopIndex);
		var $initialUnit = $('#initialUnit'+entryLoopIndex);
		var $newQuantity = $('#updateEntryQuantity'+entryLoopIndex);
		var $newUnit = $('#updateEntryUnit'+entryLoopIndex);
		var $displayQuantity = $('#displayQuantity'+entryLoopIndex);
		//if the quantity is not change and the unit is not change will not do the ajax

		//console.log($initialQuantity.val() + ' ' + $newQuantity.val());
		if($initialQuantity.val() !== $newQuantity.val() || $initialUnit.val() !== $newUnit.val()){

			$('body').addClass('loading');
			rm.header.minicart.updatable = false;
			$input.attr('disabled', true);
			$.ajax({
				url:$('#cartUpdateQuantityUrl').val(),
				type:'POST',
				data:$form.serialize(),
				success: function(result) {
					if(result !== null){
					    // render max quantity order error message
                        var text = result.indexOf('maxOrderQuantityExceeded:') !== -1 ? result.split(':')[1] : '';
                        $('.order-error-message-' + entryLoopIndex).text(text);

						$initialQuantity.val($newQuantity.val());
						$initialUnit.val($newUnit.val());
						$input.removeAttr('disabled');
						$displayQuantity.val($newQuantity.val());
						$selectBtn.text(obj.text());
						$selectBtn.attr('data-value',obj.attr('data-value'));
						rm.header.changeBaseQuantity(result,obj);
						rm.header.displayUpdatedQuantityAndUnit($newQuantity.val(), obj);
						rm.header.minicart.updatable = true;
						//rm.header.removeItemBind();
						ACC.minicart.refreshMiniCartCount();
						$.magnificPopup.close();
					}
				},
				complete:function(){
					$('body').removeClass('loading');
				}
			});
		}
	},

	//if customer changed the quantity or the unit the baseQuantity will changed.
	changeBaseQuantity: function(quantity,obj){
		var productBaseUnit = obj.closest('.popupCartItem').find('.entry-product-unit').val();
		var productBasePluralUnit = obj.closest('.popupCartItem').find('.entry-product-plural-unit').val();
		var $section = obj.closest('.popupCartItem').find('.base-quantity');
		var baseUOM = obj.closest('.popupCartItem').find('.base-quantity').attr('data-base-unit');
		var currentUOM = obj.closest('.popupCartItem').find('.select-btn.sort').attr('data-value');
		//var input = obj.closest('.cartRow').find('.qty-input').val();

		if(baseUOM === currentUOM) {
			$section.hide();
		} else {
			$section.show();
		}

		if(parseInt(quantity) === 1){
			$section.html(quantity+'&nbsp;'+productBaseUnit);
		}else if(parseInt(quantity) > 1){
			console.log(parseInt(quantity));
			$section.html(quantity+'&nbsp;'+productBasePluralUnit);
		}
	},

	// display updated quantity and unit beside the product name in the minicart popup
	displayUpdatedQuantityAndUnit: function(quantity, obj){
		var entryLoopIndex = obj.closest('.popupCartItem').find('.entry-loop-index').val();

		var unitName = '';
		var selectBtn = obj.closest('.popupCartItem').find('.select-list').find('.select-btn');
		if (selectBtn.length === 0) {
			var selectSingle = obj.closest('.popupCartItem').find('.select-list').find('.select-single');
			unitName = selectSingle.html().trim().toLowerCase();
		} else {
			unitName = selectBtn.html().trim().toLowerCase();
		}

		if(parseInt(quantity) === 1){
			$('#itemQuantityAndUnit'+entryLoopIndex).html(quantity+'&nbsp;'+unitName);
		} else if(parseInt(quantity) > 1){
			$('#itemQuantityAndUnit'+entryLoopIndex).html(quantity+'&nbsp;'+unitName+'s');
		}
	},

	resetMiniCart: function(){

		$('body').addClass('loading');

		$.ajax({
            url:'/sabmStore/en/cart/view',
            type:'POST',
            success: function(result) {
                ACC.product.viewCartPopup(result);
                //that.addClass('open');

                /* added by mb - start */
                $('.viewCartPopup .row.list-qty').hide();
                $('.viewCartPopup .minicart-delete-item .inline.submitRemoveProduct').hide();
                $('.select-btn').show();

                var $selectBtn = $('.select-btn:not(.sort):not(.header):not(.js-expiry-date):not(.js-billingunit)');

    			// Store value in data-value
    			$selectBtn.each(function(){
    				var $firstItem = $(this).next('ul').children('li').first();

    				$(this).text($firstItem.html());
    			});

    			rm.header.removeItemBind();
    			rm.header.bingCartEntry();
                /* added by mb - end */

				ACC.minicart.refreshMiniCartCount();
				$('body').removeClass('loading');
            },
            error:function(result) {
                $('body').html(result);
                console.error('error');
				$('body').removeClass('loading');
            }
        });
	},

	carouselBannerClick: function(ev){
	    if($('.carouselBannerTag').length > 0){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(ev).data('id'),$(ev).data('url'), $(ev).data('position'), $(ev).data('type'));
        		if (!$(ev).data('isexternal')) {
        		    window.location.href=$(ev).data('url');
        		}
        	}
	    }
	},

	showDatepickerDaysBorderRadius: function(){
		for(var z=1;z<=7;z++){
			if($('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').length === 1){
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').first().css('border-radius','15px');
			}else{
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').first().css('border-radius','15px 0 0 15px');
				$('.datepicker-days.delivery-calendar tr:nth-child('+z+') td:not(.disabled)').last().css('border-radius','0 15px 15px 0');
			}
		}
	}

};
/*jshint unused:false*/
/* globals enquire*/
/* globals document */
/* globals validate */
/* globals window */
/* globals alert */
/*globals MerchantSuite*/
'use strict';

rm.billing = {
	init:function(){
		this.assignTokenforPayment();
		this.setupMonthYearPayment();
		this.changeBillingOptions();
		this.setSelectedListeners();
        rm.utilities.merchantServiceFee();        
	},

    setSelectedListeners: function(){
    	$(document).on('change', 'input[name="billing"]', function(){
            $('label[for="selectedAmount"]').trigger('click');
            document.getElementById('selectedAmount').setAttribute('checked', 'checked');

        });
    },
    
    generateTokenForPayment: function(funct){
        var invoicesValue = '', amountValue = '';
        if ($('#selectedAmount').is(':checked')) {
            var selectedInvoices = [];
            $('#seller-table tbody input[name="billing"]:checked').each(function (index) {
                selectedInvoices.push($(this).val());
            });
            invoicesValue = selectedInvoices.join(',');
            amountValue = rm.utilities.convertDollarToString($('#selectedAmountValue').text());
        } else {
            var allOpenInvoices = [];
            $('#seller-table tbody input[name="billing"]').each(function (index) {
                if ($(this).attr('data-status') === 'Open') {
                    allOpenInvoices.push($(this).val());
                }
            });
            invoicesValue = allOpenInvoices.join(',');
            amountValue = rm.utilities.convertDollarToString($('#openBalanceValue').text());
        }

        var sendInfo = {invoices: invoicesValue, amount: amountValue, currencyIso: 'AUD', paymentTypeSelected:  $('input[name="accountType"]').val() };

        funct(sendInfo);

    },
    allBillingInvoices : [],
	assignTokenforPayment: function(){

		window.setBillingCheckoutDisable = 0;

		$.post( 'billing/invoices', function( data ) {
			for(var i=0; i< data.length; i++) {
				if (data[i].status === 'Open'){
                    rm.billing.allBillingInvoices.push(data[i].invoiceNumber);
				}
			}
		});
		
        $('#billingButtonEFTPayment').on('click', function () {
            if ($('#makeEFTPaymentForm').hasClass('ng-valid')) {
            	
            	var EFTData = [];
                rm.billing.generateTokenForPayment(function (info) {

                	EFTData.push({
            			'userAccountBSB': $('#makeEFTPaymentForm #bsb').val(),
            			'userAccountName': $('#makeEFTPaymentForm #nameOnAccount').val(),
            			'userAccountNumber': $('#makeEFTPaymentForm #accountNumber').val(),
            			'amount': info.amount,
            			'currencyIso': info.currencyIso,
            			'invoices': info.invoices	
	            	});
            	
	            	$.magnificPopup.close();
	        		rm.utilities.showOverlay(true);
	        		
	            	$.ajax({
						url: '/your-business/billing/payByEFT',
						data: EFTData[0],
		                dataType: 'json',
		                type: 'POST'
	            	}).done(function(res){
	            		window.location.href = res;
	            		rm.utilities.showOverlay(false);
	
	            	});
                });
            }
        });

        var msfData = [], msfData1 = [];
        $('#billingButtonCCPayment').on('click', function () {
        	msfData = []; msfData1 = [];
        	
        	//check if all the required field not empty
            if ($('#makePaymentForm').hasClass('ng-valid')) {
            	
            	//check the expiry date is a future date and valid
                rm.billing.generateTokenForPayment(function (info) {
            		var cardType = $('#cardType').val();
            		var data = {
            			'invoices': info.invoices,
            			'currencyIso': info.currencyIso,
            			'amount': info.amount,
            			'cardType': cardType
            		};
                    
            		msfData.push({
            			'cardNumber': $('#makePaymentForm #cardNumber').val(),
            			'cardHolderName': $('#makePaymentForm #nameOnCard').val(),
            			'securityCode': $('#makePaymentForm #securityCode').val(),
            			'expiryDateMonth': $('.expiryDateMonth').val(),
        				'expiryDateYear': ( $('.expiryDateMonth').val() === '99' ) ? $('#expiryDateYearHidden').val() : $('.expiryDateYear').val()
            		});

            		$.magnificPopup.close();
            		rm.utilities.showOverlay(true);

            		$.ajax({
    					url: '/your-business/billing/payByCard',
    					data: data,
    	                dataType: 'json',
    	                type: 'POST'
                	}).done(function(res){

                		if ( res.error === null) {

                		msfData1.push({
                			'authKey': res.authKey,
                			'paymentUrl': res.paymentUrl
                		});
                		
                		$.extend(true, msfData, msfData1);

	            		console.log(msfData);

                		$('#invalidExpiryDate').addClass('hide');

	            		//pass data to MSF modal
	            		$('#amount').text('$'+parseFloat(res.displayAmount).toFixed(2));
	            		$('#totalAmount').text('$'+parseFloat(res.displayTotalAmount).toFixed(2));
	            		$('#surcharge').text('$'+parseFloat(res.displaySurcharge).toFixed(2));
	            		$('#msf').text('$'+parseFloat(res.displaySurcharge).toFixed(2));

                		rm.utilities.showOverlay(false);
                		rm.utilities.showCheckoutMSFPopup();

                		} else {
                            var billing = '/your-business/billing?';
                            window.location.replace(billing + res.error);
                		}
                		
                	});	
                });
            }
        });
        
        $('.billing-msf-popup-button').on('click', function(){
            
    		$.magnificPopup.close();
    		rm.utilities.showOverlay(true);
        	
        	MerchantSuite.BaseUrl = msfData[0].paymentUrl;
    		
        	MerchantSuite.ProcessPayment({
        		AuthKey: msfData[0].authKey,
        		CardNumber: msfData[0].cardNumber,
        		CVN: msfData[0].securityCode,
        		ExpiryMonth: msfData[0].expiryDateMonth,
        		ExpiryYear: msfData[0].expiryDateYear,
        		CardHolderName: msfData[0].cardHolderName,
        		CallbackFunction: function(res) {
        			
        			var billingUrl = '/your-business/billing?';
        			if ( res.AjaxResponseType === 0 ) {
        				if ( res.ApiResponseCode === 0 ) {
                			window.location.href = res.RedirectionUrl;
            			} else { 
            				window.location.replace(billingUrl + 'declined=true');
            			}
        			} else if ( res.AjaxResponseType === 1 ) {
        				window.location.replace(billingUrl + 'invalidCard=true');
        			} else if ( res.AjaxResponseType === 2 ) {
        				window.location.replace(billingUrl + 'paymentError=true');
        			} else {
        				window.location.replace(billingUrl + 'gatewayError=true');
        			}
        		
        		}
        	});
        });
	},

    verifyMonthAndYear:function(){
        var date=new Date();
		var fullYear = date.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var month=date.getMonth()+1;
		if(shortYear === $('.expiryDateYear').val() && $('.expiryDateMonth').val() && $('.expiryDateMonth').val() < month){
			$('#invalidExpiryDate').removeClass('hide');
			$('#billingButtonCCPayment').attr('disabled','disabled');
			return false;
		}else if($('.expiryDateMonth').val() && $('.expiryDateYear').val()){
			$('#invalidExpiryDate').addClass('hide');
			$('#billingButtonCCPayment').removeAttr('disabled');
			return true;
		}
    },
    
	paymentToWestpac: function (sendInfo, funct){
		$.ajax({
	        url: 'billing/pay',
	        data: sendInfo,
	        dataType: 'json',
	        type: 'POST'
		}).done(function(response) {
			var communityCodeData 	= response.communityCode;
        	var tokenData			= response.token;
        	var urlData				= response.url;
        	var ignoreDuplicateData = response.ignoreDuplicate;
        	var errorData 			= response.error;
        	if(errorData === null || errorData === undefined){
	        	$('#makePaymentForm, #makeEFTPaymentForm').attr('action', urlData);
	        	$('.billingCommunityCodeInput').attr('value', communityCodeData);
	        	$('.billingTokenInput').attr('value', tokenData);
	        	$('.ignoreDuplicateInput').attr('value', ignoreDuplicateData);
	        	window.setBillingCheckoutDisable = 1;
        	}else{
	        	window.errorOnTokenRequest = errorData;
	        	window.setBillingCheckoutDisable = 1;
        	}
            // call the anonymous method passed in as async return.
            funct();

		});
	},
	waitingProcessingPage: function(){
		$('document').ready(function() {
			$('body').addClass('loading');
			rm.billing.checkProcessingJSON();
		});
	},
	checkProcessingJSON: function(){
		var url = window.location.pathname;
		var value = url.substring(url.lastIndexOf('/') + 1);
		$.get('/sabmStore/en/your-business/billing/pay/waitJson/' + value, function(result) {
	        if(result.length > 0) {
	        	window.location.replace(result);
	        } else{
	        	setTimeout(function(){
	        		rm.billing.checkProcessingJSON();
	        	}, 3000);
	        }
	    });
	},
	setupMonthYearPayment : function(){
		$('.expiry .js-expiry-year').append(rm.billing.getYearLiEle());
	},
	getYearLiEle:function() {
		var currentData = new Date();
		var fullYear = currentData.getFullYear()+'';
		var shortYear = fullYear.substr(2, 2);
		var liHtml = '';
		for (var i = 0; i <= 15; i++) {
			liHtml+='<option value="'+(parseInt(shortYear,10)+i)+'">'+(parseInt(shortYear,10)+i)+'</option>';
		}
		return liHtml;
	},
	changeBillingOptions:function() {
		//$('.billing-options #selectedAmount').attr('checked',false);
		//$('.billing-options #openBalance').attr('checked',true);
		$('#billingDropdownFilter .select-items li').on('click touchstart', function(){
			$('.billing-options #selectedAmount').attr('checked',true);
			$('.billing-options #openBalance').attr('checked',false);
		});
	}
};

/*jshint unused:false*/
/* globals document*/
/* globals window*/
'use strict';

rm.serviceRequest = {
	
	init:function(){
		this.bindMessageUpdate();
		this.hideTabNavItems();
		this.bindTabNavMenus();
	},
	//update email send type
	bindMessageUpdate:function(){
		$(document).on('click touchend', '.select-items.service-items li', function() {
			var $selectBtn = $(this).parent().siblings('.select-btn');
			var $selectKey = $(this).parent().siblings('.select-key');
			var $selectType = $(this).parent().siblings('.select-type');
			$selectBtn.text($(this).attr('data-value'));
			$selectKey.val($(this).attr('data-key'));
			$selectType.val($(this).attr('data-value'));
			$('#serviceMessage').val($(this).attr('data-msg'));
		});
		
		$('#serviceMessage').on('blur',function(){
			
			if($.trim($(this).val())===''){
				$(this).next().removeClass('hidden');
			}else{
				$(this).next().addClass('hidden');
			}
		});
	},
	bindTabNavMenus:function(){
		var $serviceRequest = $('.service-request');
		var $tabItemContainer = $('.service-request').find('.tab-items-container');

		if(window.location.hash.substr(1) !== ''){
			var hash = window.location.hash.substr(1);
			
			if(hash === 'contact-details'){
				$('button[data-id="contact-us"]').addClass('active');
				$tabItemContainer.find('#contact-us-tab').show();
				
				//scroll down to the bottom of the page to show the contact us details
				$('html, body').animate({
			        scrollTop: $(document).height()
			    }, 500);
				
			}else{
				$('button[data-id="'+hash+'"]').addClass('active');
				$tabItemContainer.find('#'+hash+'-tab').show();
			}

		}else{
			$('button[data-id="contact-us"]').addClass('active');
			$tabItemContainer.find('#contact-us-tab').show();
		}
				
		$serviceRequest.find('.nav-link').each(function(){
			if($(this).length > 0){
				$(this).live('click',function(){
					
					var $id = $(this).attr('data-id');

			        window.location.hash = $id;
					$serviceRequest.find('.nav-link').removeClass('active');

					/* check if the current tab menu has `active` class */
					if(!$(this).hasClass('active')){
						/* add `active` class to the selected tab menu */
						$(this).addClass('active');
						
						/* display the selected tab item */
						rm.serviceRequest.selectActiveTabMenu($id);
					}
				});
			}
		});
		
	},
	hideTabNavItems:function(){
		var $tabItemContainer = $('.service-request').find('.tab-items-container');
		
		$tabItemContainer.find('.tab-item').each(function(){
			$(this).hide();
		});
	},
	selectActiveTabMenu: function(tabID){
		var $tabItemContainer = $('.service-request').find('.tab-items-container');
		rm.serviceRequest.hideTabNavItems();
		$tabItemContainer.find('#'+tabID+'-tab').show();
	}

};

/*jshint unused:false*/
/* globals document*/
	'use strict';

rm.contactus = {
	
	init:function(){
		this.bindElementsChange();
	},
	
	bindElementsChange:function(){
		$('#contactUsSubject, #contactUsMessage').on('keyup',function(){
			rm.contactus.changeButtonStatus();
		});
		
		$('#contactUsSubject, #contactUsMessage').on('blur',function(){
			
			if($.trim($(this).val())===''){
				$(this).next().removeClass('hidden');
			}else{
				$(this).next().addClass('hidden');
			}
		});
	},
	
	changeButtonStatus:function(){
		if($.trim($('#contactUsSubject').val())==='' || $.trim($('#contactUsMessage').val())===''){
			$('#btnSend').attr('disabled','disabled');
		}else{
			$('#btnSend').attr('disabled',false);
		}
	},
	
	
};
/*jshint unused:false*/
/* globals document*/
/* globals window */
/* globals angular*/
/* globals trackTopNavEvent*/
	'use strict';

rm.datepickers = {
	calendarDeltaTime:1123200000,//13 days in milliseconds
	init:function(){

		this.datePickers();
		this.updateDeliveryDatePicker();
		this.dealDeliveryDatePicker();
		this.getDeliveryData();
		this.getPublicHolidaysData();
		this.defaultCalendarPicker();
	},

	datePickers: function() {

	    var eighteenMonthsBefore = new Date();
    	eighteenMonthsBefore.setMonth(eighteenMonthsBefore.getMonth() - 18);

		$('.basic-datepicker').datepicker({
			autoclose: true,
			orientation: 'bottom left',
			format: 'dd/mm/yyyy'
		});

		/*
		// Billing Page
		$('.billing-payment .input-daterange').datepicker({
		    format: 'dd/mm/yyyy',
		    orientation: 'bottom left',
		    autoclose: true
		}); */


		$('.billing-payment .billingdate-start').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			// data-value is always updated
            $(this).attr('data-value',rm.datepickers.convertDate(date));
            $(this).attr('data-selectDate', date);

            var toDate = $('.billing-payment .billingdate-end').attr('data-selectDate');

            if ( rm.datepickers.validateFromToDate(toDate, date) ) {
                // when date range is correct, update toDate data-value
                $('.billing-payment .billingdate-end').attr('data-value', rm.datepickers.convertDate(new Date(toDate)));
                $('#billingUpdateFilter').attr('disabled', false);
                $('.billing-payment .billingdate-end').css('border', '1px solid #ccc');
            } else {
                $('#billingUpdateFilter').attr('disabled', 'disabled');
                $('.billing-payment .billingdate-end').css('border', '1px solid #f00');
            }
		});

		$('.billing-payment .billingdate-end').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
            $(this).attr('data-selectDate', date);

            var fromDate = $('.billing-payment .billingdate-start').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(date, fromDate) ) {
				$(this).attr('data-value',rm.datepickers.convertDate(date));
				$('#billingUpdateFilter').attr('disabled', false);
				$(this).css('border', '1px solid #ccc');
			} else {
				$(this).attr('data-value','');
				$('#billingUpdateFilter').attr('disabled', 'disabled');
				$(this).css('border', '1px solid #f00');
			}
		});

		$('.raised-invoice-discrepancy .invoicedate-start').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-selectDate', date);

			$(this).attr('data-value',rm.datepickers.convertDate(date));

			var toDate = $('.raised-invoice-discrepancy .invoicedate-end').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(toDate, date) ) {
                // when date range is correct, update toDate data-value
                $('.raised-invoice-discrepancy .invoicedate-end').attr('data-value', rm.datepickers.convertDate(new Date(toDate)));
                $('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', false);
                $('.raised-invoice-discrepancy .invoicedate-end').css('border', '1px solid #ccc');
            } else {
                $('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', 'disabled');
                $('.raised-invoice-discrepancy .invoicedate-end').css('border', '1px solid #f00');
            }
		});

		$('.raised-invoice-discrepancy .invoicedate-end').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-selectDate', date);

			var fromDate = $('.raised-invoice-discrepancy .invoicedate-start').attr('data-selectDate');

			if ( rm.datepickers.validateFromToDate(date, fromDate) ) {
				$(this).attr('data-value',rm.datepickers.convertDate(date));
				$('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', false);
				$(this).css('border', '1px solid #ccc');
			} else {
				$(this).attr('data-value','');
				$('#raisedInvoiceDiscrepancyUpdateFilter').attr('disabled', 'disabled');
				$(this).css('border', '1px solid #f00');
			}
		});

        $('#smart-date').datepicker('setStartDate',eighteenMonthsBefore);
		$('#smart-date').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate'),
				year = date.getFullYear(),
				month = date.getMonth() + 1,
				day = date.getDate(),
				formattedDate = year+'-'+month+'-'+day;

			angular.element('#smartCtrl').scope().getData(formattedDate, 'specific');
			// angular.element('#smartCtrl').scope().$apply();
		});

		// Order History Page
		$('.order-history .input-daterange').datepicker({
		    format: 'dd/mm/yyyy - DD',
		    orientation: 'bottom left',
		    autoclose: true
		});

		$('#orderDate').datepicker({
		    format: 'D dd/mm/yyyy',
		    orientation: 'bottom left',
		    autoclose: true,
		    container: '.input-calendar-container'
		});

		var todayDate = new Date();
		var threeMonthBefore = new Date();
		threeMonthBefore.setMonth(threeMonthBefore.getMonth() - 3);

		$('.order-history .orderdate-end').datepicker('setDate', todayDate);
		$('.order-history .orderdate-end').attr('data-value',rm.datepickers.convertDate(todayDate));
		$('.order-history .orderdate-start').datepicker('setDate', threeMonthBefore);
		$('.order-history .orderdate-start').datepicker('setStartDate',eighteenMonthsBefore);
		$('.order-history .orderdate-start').attr('data-value',rm.datepickers.convertDate(threeMonthBefore));

		$('.order-history .orderdate-end').datepicker('setStartDate',threeMonthBefore);

		$('.order-history .orderdate-start').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-value',rm.datepickers.convertDate(date));
			$('.order-history .orderdate-end').datepicker('setStartDate',date);
			rm.responsivetable.updateOrderHistory();
		});

		$('.order-history .orderdate-end').datepicker().on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-value',rm.datepickers.convertDate(date));
			rm.responsivetable.updateOrderHistory();
		});

		// Cart Page
		$('.page-cartPage .cart-datepicker').datepicker()
			.on('changeDate',function(e){
				var date = $(this).datepicker('getDate');

				var date2 = $('.global-header .delivery-header-input').datepicker('getDate');
				if(date2 === null || date.valueOf() !== date2.valueOf()){
					console.log('update header date');
					console.log(date);
					$('.global-header .delivery-header-input').datepicker('setDate', date);
				}
			});


		// Header Mobile
		$('.delivery-header-mobile').find('.delivery-header-input').datepicker({
            container: '.mobile-calendar-container'
        }).each(function(){
			$(this).on('changeDate',function(e){
				var date = $(this).datepicker('getDate'),
					dateValue = rm.datepickers.convertDate(date),
				 	prev = $(this).attr('data-prev-value'),
					selectedDeliveryMode = $('input:radio[name=deliverymodes]:checked').val(),
					selectedDeliveryModeAttr = $('input:radio[name=deliverymodes]:checked').attr('data-delivery-mode'),
					selectedCarrier = $('.customer-carriers .select-btn').attr('data-value'),
					defaultDeliveryMode = $('.delivery-modes').attr('data-default-delivery');

				$(this).attr('data-value', dateValue);

				if (dateValue && dateValue !== prev || selectedDeliveryModeAttr !== defaultDeliveryMode) {
					var selectedDeliveryDatePackType = rm.datepickers.getDeliveryDatePackType($(this));

					rm.utilities.loadingMessage($('.loading-message').data('login'),true);
			    	$('body').addClass('loading');

			    	$.ajax({
			    		//sabmStore/en/cart/updateDeliveryDate"
						url:$(this).attr('data-update-url'),
						type:'POST',
						data:{'deliveryDate':dateValue, 'deliveryDatePackType':selectedDeliveryDatePackType},
						success: function() {
							window.location.reload();
						},
						error:function(result) {
							$('body').removeClass('loading');
						}
					});
					$.ajax({
						//sabmStore/en/cart/updateSABMdelivery"
                        url:$(this).attr('data-update-cart-url'),
                        type:'POST',
                        data:{'delmodeCode':selectedDeliveryModeAttr,'carrierCode':selectedCarrier},
                        success: function() {
                             console.log('setting selected data in calendar in cart');
                        },
                        error:function(result) {
                            console.error(result);
                        }
                    });
			    }

			});

			$(this).on('click',function(e){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Delivery date',$(this).attr('data-update-url'));
				}
            });
		});

        // Header desktop
    	$('.delivery-header-desktop').find('.delivery-header-input').datepicker({container: '#deliveryDatepickerHeader'}).each(function(){
			$(this).on('changeDate',function(e){
				var date = $(this).datepicker('getDate'),
					dateValue = rm.datepickers.convertDate(date),
				 	prev = $(this).attr('data-prev-value'),
					selectedDeliveryMode = $('input:radio[name=deliverymodes]:checked').val(),
					selectedDeliveryModeAttr = $('input:radio[name=deliverymodes]:checked').attr('data-delivery-mode'),
					selectedCarrier = $('.customer-carriers .select-btn').attr('data-value'),
					defaultDeliveryMode = $('.delivery-modes').attr('data-default-delivery');

				$(this).attr('data-value', dateValue);

				if (dateValue && dateValue !== prev || selectedDeliveryModeAttr !== defaultDeliveryMode) {
					var selectedDeliveryDatePackType = rm.datepickers.getDeliveryDatePackType($(this));

					rm.utilities.loadingMessage($('.loading-message').data('login'),true);
			    	$('body').addClass('loading');
			    	$.ajax({
			    		//sabmStore/en/cart/updateDeliveryDate"
						url:$(this).attr('data-update-url'),
						type:'POST',
						data:{'deliveryDate':dateValue, 'deliveryDatePackType':selectedDeliveryDatePackType},
						success: function() {
							window.location.reload();
						},
						error:function(result) {
							$('body').removeClass('loading');
						}
					});
					$.ajax({
						//sabmStore/en/cart/updateSABMdelivery"
                        url:$(this).attr('data-update-cart-url'),
                        type:'POST',
                        data:{'delmodeCode':selectedDeliveryModeAttr,'carrierCode':selectedCarrier},
                        success: function() {
                             console.log('setting selected data in calendar in cart');
                        },
                        error:function(result) {
                            console.error(result);
                        }
                    });
			    }

			});
			$(this).on('click',function(e){
				if (typeof rm.tagManager.trackTopNavEvent !== 'undefined') {
					rm.tagManager.trackTopNavEvent('Delivery date',$(this).attr('data-update-url'));
				}
            });
    	});

        $(document).on('click touchend', '.basic-datepicker', function(){
        	$(this).blur();
        	//$(this).focus();
        });
	},

	validateFromToDate: function (to, from) {

		var toDate = new Date(to);
		var fromDate = new Date(from);

		if ( toDate >= fromDate ) {
			return true;
		} else {
			return false;
		}

	},

	getDeliveryDatePackType: function(obj) {
		return obj.hasClass('pack') ? 'PACK' : $(obj).hasClass('keg') ? 'KEG' : $(obj).hasClass('pack-keg') ? 'PACK_KEG' : '';
	},

	convertDate: function(date){
		if(date !== null) {
			var d = '0' + date.getDate(),
				m = '0' + (date.getMonth() + 1),
				y = date.getFullYear(),
				newDate = y+m.slice(-2)+d.slice(-2);

			return newDate;
		} else {
			return '';
		}
	},

	convertDateWithMinus: function(date){
		if(date !== null) {
			var d = '0' + date.getDate(),
				m = '0' + (date.getMonth() + 1),
				y = date.getFullYear(),
				newDate = y + '-' + m.slice(-2) + '-' + d.slice(-2);

			return newDate;
		} else {
			return '';
		}
	},

	updateDeliveryDatePicker: function(){

		var convertDateDDMMYY = function(date){
			if(date !== null) {
				var d = '0' + date.getDate(),
					m = '0' + (date.getMonth() + 1),
					y = date.getFullYear(),
					newDate = d.slice(-2)+'-'+m.slice(-2)+'-'+y.toString().substring(2, 4);
				return newDate;
			} else {
				return '';
			}
		};

		var selectedDate = $('.global-header .delivery-header-input').attr('data-selected-date');

		var startDate = new Date();
		var endDate = new Date(startDate.valueOf() + rm.datepickers.calendarDeltaTime);

		$('.global-header .delivery-header-input').datepicker('setStartDate', startDate);
		$('.global-header .delivery-header-input').datepicker('setEndDate', endDate);

		$('.mobile-head .delivery-header-input').datepicker('setStartDate', startDate);
		$('.mobile-head .delivery-header-input').datepicker('setEndDate', endDate);

		$('.page-cartPage .cart-datepicker').datepicker('setStartDate', startDate);
		$('.page-cartPage .cart-datepicker').datepicker('setEndDate', endDate);

		if(selectedDate) {
			//remove HHMMSS to pass end date check
			//var tempDate = new Date(parseInt($('.global-header .delivery-header-input').attr('data-selected-date')));
			//var date = new Date(tempDate.getFullYear(),tempDate.getMonth(),tempDate.getDate());
			var dateString = $('.global-header .delivery-header-input').attr('data-selected-date');
			var date = $.datepicker.parseDate('d/m/yy', dateString);
			$('.global-header .delivery-header-input').attr('data-prev-value', rm.datepickers.convertDate(date));
			$('.mobile-head .delivery-header-input').attr('data-prev-value', rm.datepickers.convertDate(date));

			$('.global-header .delivery-header-input').datepicker('setDate', date);
			$('.page-cartPage .cart-datepicker').datepicker('setDate', date);
			$('.mobile-head .delivery-header-input').datepicker('setDate', date);
		}
	},

	dealDeliveryDatePicker: function() {
		var convertDateYYMMDD = function(date){
			if(date !== null) {
				var d = '0' + date.getDate(),
					m = '0' + (date.getMonth() + 1),
					y = date.getFullYear(),
					newDate = y.toString()+'-'+m.slice(-2)+'-'+d.slice(-2);

				return newDate;
			} else {
				return '';
			}
		};

		var startDate = new Date();
		var endDate = new Date(startDate.valueOf() + rm.datepickers.calendarDeltaTime);

		$('.calendars #datepicker-specific-day').datepicker('setStartDate', startDate);
		$('.calendars #datepicker-specific-day').datepicker('setEndDate', endDate);

		$('.calendars #date-range-from').datepicker('setStartDate', startDate);
		$('.calendars #date-range-from').datepicker('setEndDate', endDate);

		$('.calendars #date-range-to').datepicker('setStartDate', startDate);
		$('.calendars #date-range-to').datepicker('setEndDate', endDate);

		var disabledDates = $('.calendars #datepicker-specific-day').attr('data-disabled-dates');
		if(disabledDates) {
			var disabledDatesArray = JSON.parse(disabledDates);
			var disabledDatesObj = [];

			for(var i=0, len = disabledDatesArray.length; i<len; i++){
				disabledDatesObj[i] =  convertDateYYMMDD(new Date(parseInt(disabledDatesArray[i])));
			}
			$('.calendars #datepicker-specific-day').datepicker('setDatesDisabled', disabledDatesObj);
		}
	},

	/*
	* 	Get json data from DOM
	*	@author: nolan.b.trazo@accenture.com
	*/
	getDeliveryData: function() {
		// check if #deliveryDatesData json exist

		if ($('#deliveryDatesData').length > 0) {
			var deliveryData = JSON.parse($('#deliveryDatesData').html());
			if (typeof deliveryData !== 'undefined' && deliveryData!==null && deliveryData.length !== 0) {
				return deliveryData;
			} else {
				console.log('Delivery data not found!');
			}
		} else {
			console.log('#deliveryDatesData not exist');
		}
	},
	/*
	* 	Get public holidays data from DOM
	*	@author: lester.l.gabriel
	*/
	getPublicHolidaysData: function() {
		// check if #publicHolidayData json exist
		if($('#publicHolidayData').html() !== 'null'){
			if ($('#publicHolidayData').length > 0) {
				var publicHolidayData = JSON.parse($('#publicHolidayData').html());
				if (typeof publicHolidayData !== 'undefined' && publicHolidayData!==null && publicHolidayData.length !== 0) {
					return publicHolidayData;
				} else {
					console.log('publicHolidayData not found!');
				}
			} else {
				console.log('publicHolidayData not exist');
			}
		}
	},


	/*
	* 	Populate delivery modes and customer carrier
	*	@author: nolan.b.trazo@accenture.com
	*/
	calendarDeliveryModes: function() {
		var data = rm.datepickers.getDeliveryData(),
			customerStatus = '',
			cubStatus = '',
			selectedCus = '',
			selectedCUB = '',
			disabledCarrier = '',
			hideCarrier = '',
			defaultDeliveryMode = '';

		// check if data is defined
		if (typeof data !== 'undefined') {
			if (data.customerArrangedEnabled === true) {
				customerStatus = 'active';
			}

			if (data.cubArrangedEnabled === true && data.customerArrangedEnabled === true) {
				cubStatus = 'active';
			}

            if (data.cubArrangedEnabled === false) {
                $('.cart-deliverymethod .cub-arranged-block').hide();
            }

			if (data.customerOwned === true) {
				selectedCus = 'checked';
				defaultDeliveryMode = 'Customer-Arranged-Delivery';
			} else {
				selectedCUB = 'checked';
				hideCarrier = 'hide';
				defaultDeliveryMode = 'CUB-Arranged-Delivery';
			}

			if (data.shippingCarriers && data.shippingCarriers.length > 0) {
				var carriers = [],
					showCarriers = 'disabled';
				var multipleCarrierClass='';

				if (data.shippingCarriers && data.shippingCarriers.length !== 1) {
					for (var i = 0; i < data.shippingCarriers.length; i++) {
						if (data.shippingCarriers[i].customerOwned === true) {
							carriers[i] = '<li data-value="'+data.shippingCarriers[i].code+'" onclick="rm.datepickers.saveShippingCarrier(&apos;'+data.shippingCarriers[i].code+'&apos;,&apos;'+data.shippingCarriers[i].description+'&apos;);">'+data.shippingCarriers[i].description+'</li>';
						}
					}
					if (data.customerOwned === true) {
						showCarriers = 'active';
					} else {
						showCarriers = '';
					}
				} else if (data.shippingCarriers && data.shippingCarriers.length === 1) {
					disabledCarrier = 'disabled';
					if (cubStatus !== 'active') {
						customerStatus = '';
					}
				}

				var template = '<div class="delivery-modes '+customerStatus+'" data-default-delivery="'+defaultDeliveryMode+'">'+
								'<p class="error-msg">Please select dispatch date to proceed or click <span onclick="rm.datepickers.closeCalendarPicker();">cancel</span> to reset</p>'+
								'<p>Select a delivery method:</p>'+
								'<ul>'+
									'<li id="CUBArranged" class=" '+cubStatus+'">'+
										'<input onclick="rm.datepickers.updateCalendarPicker(this.value);rm.datepickers.calendarUnsavedChanges();" type="radio" name="deliverymodes" data-delivery-mode="CUB-Arranged-Delivery" value="CUB_DELIVERY" id="cub-arranged" '+selectedCUB+'><label for="cub-arranged">CUB Arranged</label><div class="radio-button"></div>'+
									'</li>'+
									'<li id="customerArranged" class=" '+customerStatus+'">'+
										'<input onclick="rm.datepickers.updateCalendarPicker(this.value);rm.datepickers.calendarUnsavedChanges();" type="radio" name="deliverymodes" data-delivery-mode="Customer-Arranged-Delivery" value="CUSTOMER_DELIVERY" id="customer-arranged" '+selectedCus+'><label for="customer-arranged">Customer Arranged</label><div class="radio-button"></div>'+
									'</li>'+
								'</ul>'+
								'<div class="select-list customer-carriers '+customerStatus+' '+hideCarrier+'">'+
								'<div data-value="'+data.selectedCarrier.code+'" class="select-btn '+showCarriers+'">'+data.selectedCarrier.description+'</div>'+
				                        '<ul class="select-items dropdown-overflow '+showCarriers+' '+disabledCarrier+'">'+
											carriers.join('')+
										'</ul>'+
									'</div>'+
								'</div>'+
								'</div>';
				return template;
			}
		}
	},

	/*
	* 	Default calendar highlighted days base on dates from JSON
	*	@author: nolan.b.trazo@accenture.com
	*/
	defaultCalendarPicker: function() {
		var data = rm.datepickers.getDeliveryData(),
			publicHolidayData = rm.datepickers.getPublicHolidaysData(),
			clientTimezone = new Date().getTimezoneOffset(),
			kegDatesArr = [],
			packDatesArr = [],
			publicHolidayDatesArr = [],
			packKegDatesArr = [],
			deliveryType,
			cutoffTime,
			packTypesArr = [];

		// check if data is defined
		if (typeof data !== 'undefined') {
			var deliveryDates = data.deliveryDatesData;
			// check if customer has shipping carriers
			if (data.customerOwned === true) {
				deliveryType = 'CUSTOMER_DELIVERY';
			} else {
				deliveryType = 'CUB_DELIVERY';
			}

			if (deliveryDates) {
				for(var i=0; i < deliveryDates.length; i++) {
					if (deliveryDates[i].mode === deliveryType) {
						if (deliveryDates[i].packType === 'KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var k=0; k < deliveryDates[i].dateList.length; k++) {
									kegDatesArr[k] = this.resetTime(deliveryDates[i].dateList[k]);
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = this.resetTime(deliveryDates[i].dateList[p]);
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = this.resetTime(deliveryDates[i].dateList[pk]);
								}
								packTypesArr.push('PACK_KEG');
							}
						}
					}
				}

				//check if publicHolidayData is not undefined
				if(typeof publicHolidayData !== 'undefined'){
					if (publicHolidayData.length > 0) {
						for(var x=0; x < publicHolidayData.length; x++) {
							publicHolidayDatesArr[x] = this.resetTime(publicHolidayData[x]);
						}

						//pass the public holidays array in the bootstrap-datepicker.js
						$('.global-header .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.mobile-head .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.page-cartPage .cart-datepicker').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('#datepicker-specific-day').datepicker('setPublicHolidayDates', publicHolidayDatesArr);

						packTypesArr.push('PUBLIC_HOLIDAY');
					}
				}

				if(typeof $('#cutofftime') !== 'undefined'){
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('#datepicker-specific-day').datepicker('setCutoffTime', this.insertDate(cutoffTime));

                    packTypesArr.push('CUTOFF-TIME');
                }

				// Pass dates to bootstrap-datepicker.js
				$('.global-header .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setKegDates', kegDatesArr);
				$('#datepicker-specific-day').datepicker('setKegDates', kegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackDates', packDatesArr);
				$('#datepicker-specific-day').datepicker('setPackDates', packDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackKegDates', packKegDatesArr);
				$('#datepicker-specific-day').datepicker('setPackKegDates', packKegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.mobile-head .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('#datepicker-specific-day').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
			}
		}
	},

	/*
	* 	Update calendar highlighted days base on dates from JSON
	*	@author: nolan.b.trazo@accenture.com
	*/
	updateCalendarPicker: function(deliveryType) {

		var data = rm.datepickers.getDeliveryData(),
			publicHolidayData = rm.datepickers.getPublicHolidaysData(),
			clientTimezone = new Date().getTimezoneOffset(),
			kegDatesArr = [],
			packDatesArr = [],
			publicHolidayDatesArr = [],
			cutoffTime,
			packKegDatesArr = [],
			packTypesArr = [];

		// check if data is defined
		if (typeof data !== 'undefined') {
			var deliveryDates = data.deliveryDatesData;
			if (deliveryDates) {
				for(var i=0; i < deliveryDates.length; i++) {
					if (deliveryDates[i].mode === deliveryType) {
						if (deliveryDates[i].packType === 'KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var k=0; k < deliveryDates[i].dateList.length; k++) {
									kegDatesArr[k] = this.resetTime(deliveryDates[i].dateList[k]);
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = this.resetTime(deliveryDates[i].dateList[p]);
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = this.resetTime(deliveryDates[i].dateList[pk]);
								}
								packTypesArr.push('PACK_KEG');
							}
						}
					}
				}

				//check if publicHolidayData is not undefined
				if(typeof publicHolidayData !== 'undefined'){
					if (publicHolidayData.length > 0) {
						for(var x=0; x < publicHolidayData.length; x++) {
							publicHolidayDatesArr[x] = this.resetTime(publicHolidayData[x]);
						}

						//pass the public holidays array in the bootstrap-datepicker.js
						$('.global-header .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.mobile-head .delivery-header-input').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('.page-cartPage .cart-datepicker').datepicker('setPublicHolidayDates', publicHolidayDatesArr);
						$('#datepicker-specific-day').datepicker('setPublicHolidayDates', publicHolidayDatesArr);

						packTypesArr.push('PUBLIC_HOLIDAY');
					}
				}

				if(typeof $('#cutofftime') !== 'undefined'){
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', this.insertDate(cutoffTime));
                    $('#datepicker-specific-day').datepicker('setCutoffTime', this.insertDate(cutoffTime));

                    packTypesArr.push('CUTOFF-TIME');
                }

				// Pass dates to bootstrap-datepicker.js
				$('.global-header .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setKegDates', kegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setKegDates', kegDatesArr);
				$('#datepicker-specific-day').datepicker('setKegDates', kegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackDates', packDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackDates', packDatesArr);
				$('#datepicker-specific-day').datepicker('setPackDates', packDatesArr);

				$('.global-header .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.mobile-head .delivery-header-input').datepicker('setPackKegDates', packKegDatesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setPackKegDates', packKegDatesArr);
				$('#datepicker-specific-day').datepicker('setPackKegDates', packKegDatesArr);

				$('.global-header .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.mobile-head .delivery-header-input').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('.page-cartPage .cart-datepicker').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);
				$('#datepicker-specific-day').datepicker('setDeliveryModesTypes', deliveryType, packTypesArr);

				$('.global-header .delivery-header-input').datepicker('removeActiveDay');
				$('.mobile-head .delivery-header-input').datepicker('removeActiveDay');
				$('.page-cartPage .cart-datepicker').datepicker('removeActiveDay');
				$('#datepicker-specific-day').datepicker('removeActiveDay');
			}
		}

		$('.datepicker-days.delivery-calendar tbody').find('td').removeClass('active');
		rm.header.showDatepickerDaysBorderRadius();

	},

	/*
	* 	Trigger function when user clicked delivery modes or customer carrier is change
	*	@author: nolan.b.trazo@accenture.com
	*/
	calendarUnsavedChanges: function() {
		// Add class to determine if there's unsaved changes
		$('.datepicker').addClass('unsaved-changes');
		$('a:not(.navbar-toggle):not(.navbar-close), .highlight-link').unbind('click');

		$('button').attr('disabled', 'disabled');

		$('.delivery-header').addClass('open');

		if($('.delivery-header-desktop').hasClass('open')){
			$('.cart-datepicker').attr('disabled','disabled');
		}

		/* show delivery method error msg when there's unsaved changes */
		$('.delivery-modes .error-msg').addClass('show');
	},

	/*
	* 	Close calendar and reset calendar data when cancel/close(x) is clicked
	*	@author: nolan.b.trazo@accenture.com
	*/
	closeCalendarPicker: function() {
		$('.datepicker').removeClass('unsaved-changes');
		$('a:not(.navbar-toggle):not(.navbar-close), .highlight-link').unbind('click');

		$('input[data-provide="datepicker"], .basic-datepicker, .billing-payment, .cart-datepicker, .delivery-header-input, #datepicker-specific-day, .order-history').datepicker('hide');
		$('.datepicker').remove();

		$('.cart-datepicker').attr('disabled',false);

		$('button').attr('disabled', false);

		// reset to the default calendar data
		rm.datepickers.defaultCalendarPicker();

		$('.delivery-header').removeClass('open');
	},

	/*
	* 	Sync/Show calendar when delivery modes or customer carriers is change in Cart Page
	*	@author: nolan.b.trazo@accenture.com
	*/
	syncCalendarPicker: function(selected) {
		// show calendar in header for desktop and calendar in cart page for mobile
		var windowWidth = $(window).width();
		if (selected === 'deliveryMode') {
			if (windowWidth < 768) {
				$('.page-cartPage .cart-datepicker').datepicker('show');
			} else {
				$('.global-header .delivery-header-input').datepicker('show');
			}
		}

		// Replace the Delivery data to the updated data/Get updated JSON response
		$.ajax({
			//sabmStore/en/cart/updateDeliveryDate"
			url:'/sabmStore/en/view/DeliveryDatepickerComponentController/getUpdatedDeliveryDateConfig',
			type:'GET',
			success: function(data) {
				var updatedDeliveryData = JSON.stringify(data);
				//replace the old data with the updated json response
				$('#deliveryDatesData').empty().append(updatedDeliveryData);
			},
			complete: function() {
				// Reset calendar data after ajax finish to load
				rm.datepickers.defaultCalendarPicker();
				rm.header.showDatepickerDaysBorderRadius();

				// only triggers unsaved notification when delivery mode is change
				if (selected === 'deliveryMode') {
					rm.datepickers.calendarUnsavedChanges();
				}
				// Remove active day
				$('.global-header .delivery-header-input').datepicker('removeActiveDay');
				$('.mobile-head .delivery-header-input').datepicker('removeActiveDay');
				$('.page-cartPage .cart-datepicker').datepicker('removeActiveDay');
				$('#datepicker-specific-day').datepicker('removeActiveDay');
			},
			error:function(result) {
				$('body').removeClass('loading');
			}
		});
	},

	/*
	*	Save customer shipping carrier when carrier is changed
	*	@author: nolan.b.trazo@accenture.com
	*/
	saveShippingCarrier: function(code,desc) {
		if (code && desc) {
			$.ajax({
				url:'/sabmStore/en/cart/updateSABMdelivery',
				type:'POST',
				data:{'delmodeCode':'Customer-Arranged-Delivery','carrierCode':code},
				success: function() {
					console.log('Shipping carrier saved!');
				},
				complete: function() {
					$('#customerArrangedDelivery .select-btn').text(desc);
				},
				error:function(result) {
					console.error(result);
				}
			}).always(function() {
				$('body').removeClass('loading');
			});
		}
	},

	 resetTime: function (timestamp) {
        var options = {
            timeZone: 'Australia/Sydney',
            year: 'numeric',
            month: 'numeric',
            day: 'numeric'
        };
        var localString = new Date(parseInt(timestamp)).toLocaleString("en-AU", options);
        var date = localString.split(',')[0].split('/');
        var year = date[2];
        var month = this.zeroPrefix(date[1]);
        var day = this.zeroPrefix(date[0]);
        return new Date(year + '-' + month + '-' + day);
    },

    zeroPrefix: function (number) {
        return number.toString().length > 1 ? number : '0' + number;
    },

    insertDate: function (str) {
        var data = JSON.parse(str);
        var regex = new RegExp('(^[^\\d]+)\<([\\w\\s\\W]+)\'\>', 'i');
        var div = document.createElement('div');
        var p = document.createElement('p');
        var b = document.createElement('b');
        div.textContent = data.text;
        p.style = data.styling;
        b.textContent = this.converter(data.cutofftime, data.plantcutofftimezone);
        p.append(b);
        div.append(p);
        return div;
    },

    converter: function(date, timezoneOffset) {
        var currentDate = new Date();
        // use Sydney/Melbourne time as reference: Monday 13 Jun 2022 03:10 PM
        // this will be always Sydney/Melbourne timezone
        // and do conversion
        var fullDate = this.getFullDate(date, currentDate.getFullYear());
        return this.getNewDate(fullDate, timezoneOffset);
    },

    getNewDate: function (date, timezoneOffset) {
        // convert date&time based on given timezone offset
        var localeDate = this.addTimezone(date, timezoneOffset);
        var options = { weekday: 'long', month: 'short', day: 'numeric', hour: 'numeric', minute: 'numeric', hour12: true };
        var formatted = localeDate.toLocaleString(undefined, options).split(',');
        var hour12 = formatted[formatted.length - 1]; // extract am/pm
        formatted[formatted.length - 1] = hour12.toUpperCase(); // upper case 'am/pm'
        return formatted.join(' ');
    },

    addTimezone: function(date, timezoneOffset) {
        return new Date(date + ' GMT' + timezoneOffset);
    },

    getFullDate: function (date, year) {
        return date.replace(/\d\d:\d\d/i, function(match, p1) {
            return year + ' ' + match;
        });
    }
};

/*jshint unused:false*/
/* globals enquire*/
/* globals window */
/* globals trackPDPSaveToTemplate */
/* globals trackPDPPriceConditions */

'use strict';

rm.productdetails = {

	init:function(){
		//this.checkIfCupDealsLoaded();
		this.createOrderTemplate();
	},

	checkIfCupDealsLoaded: function(){
		var inProgress = $('#product-detail-panel').attr('data-cupdealsRefreshInProgress');

		if(inProgress === 'true')
		{
			rm.utilities.sapCall = true;

			//$('body').addClass('loading');
			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkCupDealsRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	},
	createOrderTemplate: function() {
		$('#save-to-template-pdp').on('click',function(){
			$('#save-to-template .error').addClass('hidden');
			
			if (typeof rm.tagManager.trackPDPSaveToTemplate!=='undefined') {
				rm.tagManager.trackPDPSaveToTemplate();
			}
		});
		$('#priceConditionsTag').on('click',function(){
			if (typeof rm.tagManager.trackPDPPriceConditions!=='undefined') {
				rm.tagManager.trackPDPPriceConditions();
			}
        });
		$('.createTemplateBtn').on('click touchstart', function(e){
			if($('#template-name').val().trim() === '') {
				$('#empty-msg').removeClass('hidden');
				return false;
			}
		});
	}
};
/* globals window */

/*jshint unused:false*/
/* globals ACC */

	'use strict';

rm.cubpicks = {

	init: function(){
		//this.checkIfCupPicksCupLoaded();
		ACC.product.addListeners();
	},

	checkIfCupPicksCupLoaded: function(){
		var isCupInProgress = $('#cubPickSection').attr('data-cupRefreshInProgress');

		if(isCupInProgress === 'true')
		{
			//$('#cubPickSection').addClass('loading');
			console.log('Cup loading is in progress');

			setInterval(function(){
				$.ajax({
				url:'/b2bunit/checkCupRefreshStatus',
				type:'GET',
				success: function(result) {
					var jsonObj = JSON.parse(result);
					if(jsonObj.status === 'false')
					{
						window.location.reload();
						//$('#cubPickSection').removeClass('loading');
					}
				},
				error:function() {
					console.log('Error occured while checking deal refresh status');
				}
			});
			}, 5000);
		}
	}
};
/* globals document */
/* globals window */
/* globals validate */
/*jshint unused:false*/

'use strict';


rm.notifications = {
	init : function() {
		rm.notifications.showNotifications();
		rm.notifications.hideNotification();
		/*rm.notifications.showUnsavedChangesPopup();*/
	},

	showNotifications: function(){
    setTimeout(function() {
      console.log('fired');
		  $('.home-notification-box').slideDown();
    }, 1000);
	},
	hideNotification : function() {

		$('.hide-notification').on('click', function(e) {
			e.preventDefault();
			$.ajax({
				url : '/notification/hide/' + $(this).attr('id'),
				type : 'GET'
			});
			$(this).closest('.home-notification-box').slideUp();
		});
	},
	
	/*invokeUnsavedChangesPopup : function() {
		console.log('onbeforeunload switched ON');
 
		//$(window).on('beforeunload', function() {
			//return 'You have unsaved changes!';
		//});

		$('body').addClass('unsaved-changes');
		
	},
	
	switchOffUnsavedChangesPopup : function() {
		$('body').removeClass('unsaved-changes');
		console.log('onbeforeunload switched OFF');
		//$(window).off('beforeunload');
	},

	showUnsavedChangesPopup : function(){
		console.log($('unsavedNotification').scope());
		$('a').on('click', function(e){
			if ($('body.unsaved-changes').length) {
				console.log('has unsaved changes');
				e.preventDefault();
				$('#unsavedNotification').modal('show');
			}
		});
	}*/
};
/* globals document */
/* globals window */
/* globals dataLayer */
/* globals requestOrigin */
/* globals searchPageDataBreadCrumbs */
/* globals getOnDeal */
/* globals sessionStorage */

'use strict';

$.fn.isOnViewPort = function() {
    var win = $(window);

    var viewPort = {
        top : win.scrollTop(),
        left : win.scrollLeft()
    };
    viewPort.right = viewPort.left + win.width();
    viewPort.bottom = viewPort.top + win.height();

    var bounds = this.offset();
    bounds.right = bounds.left + this.outerWidth();
    bounds.bottom = bounds.top + this.outerHeight();

    return (!(viewPort.right < bounds.left || viewPort.left > bounds.right || viewPort.bottom < bounds.top || viewPort.top > bounds.bottom));
};

$(document).ready(function ()
{


	if ($('.addToCartEventTag').length > 0 ) {
		$('.add_to_cart_form [type="submit"]').on('click', function(){
		    var addToCartEventTag = $(this).parents('.addToCartEventTag');
		    if ($('.recommendation-highlight').has($(this)).length > 0) {
		        var recommendationGroup = $('.recommendation-component').data('smart-recommendation-group');
		        var componentSlot = $('.recommendation-component').data('recommendation-component-slot');
                var componentPosition = $('.recommendation-component').data('recommendation-component-position');
		        rm.tagManager.trackSmartRecommendationAddToCart(addToCartEventTag, recommendationGroup, componentSlot, componentPosition);
            }
			rm.tagManager.trackCartItems(addToCartEventTag);
		});
	}

	if ($('.recommendation-component').length > 0) {
	    var recommendedProducts = [];
	    var recommendationGroup = $('.recommendation-component').data('smart-recommendation-group');
	    var componentSlot = $('.recommendation-component').data('recommendation-component-slot');
	    var componentPosition = $('.recommendation-component').data('recommendation-component-position');
	    var products = $('.recommendation-component').find('.product-pick-description .js-track-product-link');
	    $.each(products, function() {
            var product = {
                'name'          : $(this).data('name'),
                'id'            : $(this).data('id'),
                'price'         : $(this).data('price'),
                'brand'         : $(this).data('brand'),
                'category'      : $(this).data('category'),
                'variant'       : $(this).data('variant'),
                'list'          : $(this).data('list'),
                'actionfield'   : $(this).data('actionfield'),
                'position'      : $(this).data('position'),
                'dealsFlag'     : $(this).data('dealsflag'),
                'dimension7'    : recommendationGroup,
                'dimension8'    : $(this).closest('.productImpressionTag').data('smart-recommendation-model')
            };
            recommendedProducts.push(product);
        });
        rm.tagManager.trackSmartRecommendationImpression(recommendedProducts);

        $('.js-track-product-link').on('click', function() {
            rm.tagManager.trackSmartRecommendationProductView($(this), recommendationGroup, componentSlot, componentPosition);
        });

    }

    if ($('.recommended-products').length > 0) {
        var orderedRecommendedProducts = [];
        var productList = $('.recommended-products').find('.recommended-products-purchased');
        var orderId = $('.recommended-products').data('order-id');
        $.each(productList, function() {
            var smartRecommendationModel = rm.tagManager.getSmartRecommendationModel($(this).data('recommendation-model'));
            var product = {
                'name'          : $(this).data('name'),
                'id'            : $(this).data('id'),
                'price'         : $(this).data('price'),
                'quantity'      : $(this).data('quantity'),
                'brand'         : $(this).data('brand'),
                'category'      : $(this).data('category'),
                'variant'       : $(this).data('variant'),
                'list'          : $(this).data('list'),
                'actionfield'   : $(this).data('actionfield'),
                'position'      : $(this).data('position'),
                'dealsFlag'     : $(this).data('dealsflag'),
                'dimension7'    : $(this).data('recommendation-group'),
                'dimension8'    : smartRecommendationModel
            };
            orderedRecommendedProducts.push(product);
        });
        rm.tagManager.trackSmartRecommendationOrdered(orderedRecommendedProducts, orderId);
    }
	
	//track support page tabs
	if ( $('.service-request .nav-item .nav-link').length > 0 ) {
		$('.service-request .nav-item .nav-link').each(function(){
			$(this).on('click', function(){
				if(!$(this).hasClass('active')){
					var eventLabel = $(this).data('label') + ' Tab';
					
					if(typeof rm.tagManager.trackSupportTabClick!==undefined){
						rm.tagManager.trackSupportTabClick(eventLabel);
					}
				}
			});
		});
	}	
	
	//track chosen deal modal option 
	$('.deals-conflict-popup .btn-apply-deal').on('click', function(){
		rm.tagManager.addDealsImpressionAndPosition('Clicked', 'ConflictDeal', 'ApplyChosenDeal');
	});
	
	//commented gtm event for invoice discrepancy
	/*
	//track invoice discrepancy links
	if ( $('#invoice-discrepancy a.data-link').length > 0 ) {
		$('#invoice-discrepancy a.data-link').each(function(){
			$(this).on('click', function(){
				var action = $(this).attr('data-action'), label = $(this).attr('data-label');
				rm.tagManager.trackInvoiceDiscrepancyLinkClick(action, label);
				//e.preventDefault();
			});
		});
	}	*/
	
	if($('#searchEmptyPage').length >0 && typeof rm.tagManager.trackEmptySearchResult !== 'undefined'){
		rm.tagManager.trackEmptySearchResult($('#searchText').html());
    }
	
    if($('.rotatingBannerTag').length >0){
        var promotions = [];

        var length = $('.rotatingBannerTag').length/2;
        $('.rotatingBannerTag').each(function(index){
            var parentClass = $(this).parent().hasClass('brand-grid-item');

           if((index < length && !parentClass) || parentClass){

               promotions.push({
                   'id'			: $(this).data('alttext'),
                   'name'			: $(this).data('alttext'),
                   'creative'		: $(this).data('type')+ $(this).data('position'),
                   'position'		: 'slot'+$(this).data('position'),
               });
           }
        });
        
        if (promotions.length > 0 && typeof rm.tagManager.trackPromotionExpression!=='undefined') {
        	rm.tagManager.trackPromotionExpression(promotions);
        }
        
        $('.rotatingBannerTag').on('click', function(){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(this).data('alttext'),$(this).data('url'), $(this).data('position'), $(this).data('type'));
        	}
		});
    }
    if($('#imagelinkTag').length >0){
        $('#imagelinkTag').on('click', function(){
        	if(typeof rm.tagManager.trackShopLikeAGenius!=='undefined'){
        		rm.tagManager.trackShopLikeAGenius($(this).data('url'));
        	}
    	});
    }
    if($('.linkParagraphtag').length >0){
        $('.linkParagraphtag').on('click', function(){
        	if(typeof rm.tagManager.trackYourBusinessBilling!=='undefined'){
        		rm.tagManager.trackYourBusinessBilling($(this).data('url'));
        	}
    	});
    }
    
    if($('#dealPageWrapper').length >0){
    	$('.js-track-deals-addtocart').on('click', function() {
    		var dealsRowItems = $(this).closest('.deal').find('.row.deal-item-head, .row.deal-item-body').find('.base-item');
    	 	rm.tagManager.trackCartItems(dealsRowItems);
    	});

    }
    
    
    
    if($('.carouselBannerTag').length > 0){
		var carousels = [];

		$('.carouselBannerTag').each(function(){
    		
			carousels.push({
	            'id'			: $(this).data('id'),
	            'name'			: $(this).data('name'),
	            'creative'		: $(this).data('type')+$(this).data('position'),
	            'position'		: 'slot'+$(this).data('position')
            });
    	});
    	
		/*By AM team commenting default push of all slides
    
        if (carousels.length > 0 && typeof rm.tagManager.trackPromotionExpression!=='undefined') {
        	rm.tagManager.trackPromotionExpression(carousels);
        }
  By hypercare
        /*
        $('#carouselBannerTag').on('click', function(){
        	if(typeof rm.tagManager.trackPromotionClick!=='undefined'){
        		rm.tagManager.trackPromotionClick($(this).data('id'),$(this).data('url'), $(this).data('position'), $(this).data('type'));
        		//window.location.href=$(this).data('url');
        	}
        	
        	if(typeof rm.tagManager.trackCarouselBanner!=='undefined'){
        		var label = $(this).data('id') || $(this).data('name');  //$(this).data('type') + $(this).data('position');
        		rm.tagManager.trackCarouselBanner(label);
        		window.location.href=$(this).data('url');
        	}

        }); */
		
		
		
    }
    
    /*
    // Partially Qualified Deals Modal - Add to Cart - Start 
    if($('#partiallyQualified').length >0){
    	$('.js-track-deals-addtocart').on('click', function() {
    		var dealsRowItems = $('#partiallyQualified').find('.js-track-deal-row').find('.row.base-rows');
    		rm.tagManager.trackCartItems(dealsRowItems);
    	});
    }
    // Partially Qualified Deals Modal - Add to Cart - End
    */
    
    // for Best Sellers (in Home page) and Recommendations (in Cart page) Carousels - Start
    if ($('.related-products li.slider-prev, .related-recommendations li.slider-prev').length > 0) {
    	$('.related-products li.slider-prev, .related-recommendations li.slider-prev').on('click', function() {
    		var visibleElements = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active');
    		var visibleElementsLen = visibleElements.length;
    		
    		if (visibleElementsLen > 0) {
    			
    			var firstOfTheCurrentVisibleElements = visibleElements.eq(0);
    			var ndxOfTheFirstOfTheCurrentVisibleElements = parseInt(firstOfTheCurrentVisibleElements.attr('data-slick-index'));
    			
    			var nextToBeVisibleElement = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide[data-slick-index="' + (ndxOfTheFirstOfTheCurrentVisibleElements - 1) + '"]');
    			var elementWithProductInfo = nextToBeVisibleElement.find('.js-track-product-link');
    			
    			if (elementWithProductInfo.length > 0) {
    				var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
    				
	        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
	        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
	        			
	    				var product = [{
			            	'name'		: elementWithProductInfo.data('name'),
			            	'id'		: elementWithProductInfo.data('id'),
			            	'price'		: elementWithProductInfo.data('price'),
			            	'brand'		: elementWithProductInfo.data('brand'),
			            	'category'	: elementWithProductInfo.data('category'),
			            	'variant'	: elementWithProductInfo.data('variant'),
			            	'list'		: elementWithProductInfo.data('list'),
			            	'position'	: elementWithProductInfo.data('position'),
			            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
			            }];
	    				
	    				if (typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined') {
	    					rm.tagManager.trackProductImpressionAndPosition(product);
	    				}
	        		}
        		}
    	    }
    	});
    }
    
    if ($('.related-products li.slider-next, .related-recommendations li.slider-next').length > 0) {
    	$('.related-products li.slider-next, .related-recommendations li.slider-next').on('click', function() {
    		var visibleElements = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active');
    		var visibleElementsLen = visibleElements.length;
    		
    		if (visibleElementsLen > 0) {
    			
    			var lastOfTheCurrentVisibleElements = visibleElements.eq(visibleElementsLen - 1);
    			var ndxOfTheLastOfTheCurrentVisibleElements = parseInt(lastOfTheCurrentVisibleElements.attr('data-slick-index'));
    			
    			var nextToBeVisibleElement = $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide[data-slick-index="' + (ndxOfTheLastOfTheCurrentVisibleElements + 1) + '"]');
    			var elementWithProductInfo = nextToBeVisibleElement.find('.js-track-product-link');
    			
    			if (elementWithProductInfo.length > 0) {
    				var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
    				
	        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
	        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
	        			
	    				var product = [{
			            	'name'		: elementWithProductInfo.data('name'),
			            	'id'		: elementWithProductInfo.data('id'),
			            	'price'		: elementWithProductInfo.data('price'),
			            	'brand'		: elementWithProductInfo.data('brand'),
			            	'category'	: elementWithProductInfo.data('category'),
			            	'variant'	: elementWithProductInfo.data('variant'),
			            	'list'		: elementWithProductInfo.data('list'),
			            	'position'	: elementWithProductInfo.data('position'),
			            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
			            }];
	    				if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	    					rm.tagManager.trackProductImpressionAndPosition(product);
	    				}
	        		}
        		}
    	    }
    	});
    }
    // for Best Sellers (in Home page) and Recommendations (in Cart page) Carousels - End
    
    rm.tagManager.addProductImpressionListener();
    
    var scrollTimeout = null;
    $(window).scroll(function() {
        if (scrollTimeout) {
        	clearTimeout(scrollTimeout);
        }
        
        scrollTimeout = setTimeout(rm.tagManager.addProductImpressionListener(), 500);
    });
    
});


rm.tagManager = {

	trackProductImpressionAndPosition: function(productList) {
		
			var products = [];
			productList.forEach(function(product) {
				products.push({
					'name' 			: product.name,
					'id'			: product.id,
					'price'			: product.price,
					'brand'			: product.brand,
					'category'		: product.category,
					'variant'		: product.variant,
					'list'			: (product.list !== undefined) ? product.list : requestOrigin,
					'position'		: product.position,
					'dimension13'	: getOnDeal(String(product.dealsFlag))
				});	
			});
			
			rm.tagManager.pushProductImpressionAndPosition(products);
			
	},
	
	trackDealsImpressionAndPosition: function(event, dealsList) {
		
		var deals = [];
		dealsList.forEach(function(product) {
			deals.push({
				'name' 			: product.name,
				'id'			: product.id,
				'price'			: product.price,
				'brand'			: product.brand,
				'category'		: product.category,
				'variant'		: product.variant,
				'list'			: (product.list !== undefined) ? product.list : requestOrigin,
				'position'		: product.position,
				'dimension13'	: getOnDeal(String(product.dealsFlag))
			});	
		});
		
		console.log('track deals impression and position:' + event);

		
		rm.tagManager.pushDealsImpressionAndPosition(event, deals);
		
	},
	
	trackSearchPopupInteractions: function(elementWithProductInfo) {
		var product = {
        	'name'		: elementWithProductInfo.data('name'),
        	'id'		: elementWithProductInfo.data('id'),
        	'price'		: elementWithProductInfo.data('price'),
        	'brand'		: elementWithProductInfo.data('brand'),
        	'category'	: elementWithProductInfo.data('category'),
        	'variant'	: elementWithProductInfo.data('variant'),
        	'list'		: elementWithProductInfo.data('list'),
        	'actionfield'		: elementWithProductInfo.data('actionfield'),
        	'position'	: elementWithProductInfo.data('position'),
        	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
        };
		rm.tagManager.trackProductClick(product);
	},
	
	trackAddtoCartFromSearchPopup : function(addtoCartForm){
		var qty = parseInt(addtoCartForm.find('[name="qty"]').val());
		var code = addtoCartForm.find('[name="productCodePost"]').val();
		var product = {
        	'name': addtoCartForm.data('name'),
        	'id':code,
        	'qty':qty,
        	'list': addtoCartForm.data('list'),
        	'variant':addtoCartForm.data('variant'),
        	'position':addtoCartForm.data('position'),
        	'dealsflag':addtoCartForm.data('dealsflag'),
        	'actionfield':addtoCartForm.data('actionfield')
        };
		if (typeof rm.tagManager.trackCart!=='undefined') {
				rm.tagManager.trackCart(product, 'add');
			}
	},
		
	
	trackProductImpressionAndPositionForAdditionalResults: function(results) {
		
		var products = [];
		
		results.forEach(function(product, ndx) {
			var category = '';
			if (product.categories !== null) {
				category = product.categories[product.categories.length - 1].name;
			}
			
			products.push({
				'name' 			: product.name,
				'id'			: product.code,
				'price'			: (product.price !== null) ? product.price.value : '',
				'brand'			: product.brand,
				'category'		: rm.tagManager.escapeHtml(category),
				'variant'		: (product.unit !== null) ? product.unit.name : '',
				'list'			: requestOrigin,
				'position'		: ndx + 1,
				'dimension13'	: getOnDeal(product.dealsFlag)
			});	
			
		});
		
		rm.tagManager.pushProductImpressionAndPosition(products);
		
	},
	
	
	pushProductImpressionAndPosition: function(products) {
		
		var productImpression = {
			'event': 'impressionPushed',
			'ecommerce': {
				'currencyCode': 'AUD',
				'impressions': products
			}
		};
		
	    var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(productImpression);
	    
	    if (okToPush) {
			dataLayer.push(productImpression);
	    }
	},
	
	pushDealsImpressionAndPosition: function(event, deals) {
		
		var dealsImpression = {
			'event': event,
			'ecommerce': {
				'currencyCode': 'AUD',
				'impressions': deals
			}
		};
		
		console.log('push deals impression and position:' + event);
		
	    var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(dealsImpression);
	    
	    if (okToPush) {
			dataLayer.push(dealsImpression);
	    }
	},
	
	isProductImpressionAndPositionOKToPush: function(productImpressionObj) {

		var okToPush = false;
		
		// check/search if group of product impressions is already in the dataLayer and
		// return the results
		var result = $.grep(dataLayer, function(item) { 
			return item.event === productImpressionObj.event && JSON.stringify(item.ecommerce) === JSON.stringify(productImpressionObj.ecommerce);
	    });
	    
	    // if group of product impressions was not present in the dataLayer, check each product impression if it
	    // is present in one of the product impressions that is already in the dataLayer
	    if (result.length === 0) {
	    	okToPush = true;
	    	
	    	result = $.grep(dataLayer, function(item) { 
	    		return item.event === productImpressionObj.event;
	        });
	    	
	        if (result.length > 0) {
	    	    $.each(productImpressionObj.ecommerce.impressions, function(productImpressionObjKey, productImpressionObjValue) {
	    	    	
	    	    	$.each(result, function(resultKey, resultValue) {
	    	    		$.each(resultValue.ecommerce.impressions, function(resultImpressionKey, resultImpressionValue) {
		    	    		if (JSON.stringify(resultImpressionValue) === JSON.stringify(productImpressionObjValue)) {
		    	    			okToPush = false;
		    	    			return okToPush;
		    	    		}
	    	    		});
	    	    	});
	    	    	
	    		}); 
	        }
	    }
	    
	    return okToPush;
	},
	
	
	trackOnCheckoutOption: function(step, checkoutOption) {
		console.log('onCheckoutOption');
		console.log('step=' + step);
		console.log('option=' + checkoutOption);
		
		dataLayer.push({
			'event': 'checkoutOption',
			'ecommerce': {
				'checkout_option': {
					'actionField': {'step': step, 'option': checkoutOption}
				}
			}
		});
	},
	
	
	trackEmptySearchResult: function(searchText) {
		dataLayer.push({
				'event': 'zeroSearch',
				'eventCategory': 'error',
				'eventAction':'0 Search Results',
				'eventLabel': searchText
			});
	},

	trackPDPSaveToTemplate:	function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Save to Template'
		 	});
	},

	trackPDPOtherPackOptions: function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Other pack options'
		 	});
	},

	trackPDPPackConfiguration: function() {
		 dataLayer.push({
		 		'event': 'productPageClick',
		 		'eventCategory': 'Products',
		 		'eventAction':'Click',
		 		'eventLabel': 'Pack configuration'
		 	});
	},

	trackPDPPriceConditions: function() {
		  dataLayer.push({
		  		'event': 'productPageClick',
		  		'eventCategory': 'Products',
		  		'eventAction':'Click',
		  		'eventLabel': 'Price conditions'
		  	});
	},

	trackListingFilter:	function(filter) {
	    dataLayer.push({
	    		'event': 'refine',
	    		'eventCategory': 'Product Listing',
	    		'eventAction':'filter by',
	    		'eventLabel': filter
	    	});
	},

	trackTopNavEvent: function(eventAction, url) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Top Nav',
	        'eventAction': eventAction,
	        'eventLabel': url
	    });
	 },

	 trackShopLikeAGenius: function(url) {
	      if(url.indexOf('smartOrders') !== -1){
	           dataLayer.push({
	              'event': 'gaEvent',
	              'eventCategory': 'Page Body',
	              'eventAction': 'Genius',
	              'eventLabel': url
	          });
	      }
	      return true;
	 },
	 
	 trackYourBusinessBilling: function(url) {
		 if(url.indexOf('your-business') !== -1){
            if(url.indexOf('/billing') !== -1){
                 dataLayer.push({
                     'event': 'gaEvent',
                     'eventCategory': 'Page Body',
                     'eventAction': 'Billing and Payment',
                     'eventLabel': url
                 });
            } else {
                dataLayer.push({
                    'event': 'gaEvent',
                    'eventCategory': 'Page Body',
                    'eventAction': 'Your Business',
                    'eventLabel': url
                });
            }

		 }
		 return true;
	 },

	 
	 trackCheckoutError: function(msg) {
        dataLayer.push({
             'event': 'checkoutError',
             'text': msg
         });
     },

     
     trackPromotionClick: function(promoName, promoUrl, position, type) {
       dataLayer.push({
           'event': 'promotionClick',
           'ecommerce': {
                 'promoClick': {
                     'promotions':[{
                         'id': promoName,
                         'name': promoName,
                         'creative': type+position,
                         'position': 'slot'+position
                     }]
                 }
           },
           'eventCallback': function() {}
       });
     },
     
     //tracking slick view
     trackPromotionExpressionView: function(promoName, promoUrl, position, type) {
         dataLayer.push({
        	 'event': 'promotionView',
             'ecommerce': {
                   'promoView': {
                  	 'promotions':[{
                       'id': promoName,
                       'name': promoName,
                       'creative': type+position,
                       'position': 'slot'+position
                   }]
         }
             }
         });
       },

     
     trackPromotionExpression: function(promos) {
       dataLayer.push({
    	   'event': 'promoView',
           'ecommerce': {
                 'promoView': {
                     'promotions':promos
                 }
           }
       });
     },
     
     
	 trackBillingPayment: function(url) {
	    if(url.indexOf('your-business') !== -1){
	         dataLayer.push({
	            'event': 'GA',
	            'eventCategory': 'Page Body',
	            'eventAction': 'Your Business',
	            'eventLabel': url
	        });
	    }
	    return true;
	 },
	 
	 trackProductClick: function(productObj) {
		var categories = productObj.category;
		
		searchPageDataBreadCrumbs.forEach(function(breadCrumb) {
			categories += '/' + breadCrumb.facetValueName;
		});
		
		sessionStorage.setItem('listName', productObj.actionfield);
		sessionStorage.setItem('listOriginPos', productObj.position);
		
		dataLayer.push({
			'event': 'productClick',
			'ecommerce': {
				'currencyCode': productObj.currencycode,
				'click': {
					'actionField': {'list': productObj.actionfield}, 
					'products': [{
						'name': productObj.name, // Name or ID is required.
						'id': productObj.id,
						'price': productObj.price,
						'brand': productObj.brand,
						'category': categories, // composed of product root category and breadcrumb categories
						'variant': productObj.variant,
						'position': productObj.position
					}]
				}
			},
			
			'eventCallback': function() {}
		});
	},

	
	trackCartItems: function(productElements) {
		productElements.each(function() {
			var elementWithProductInfo = $(this).find('.js-track-product-link');
			var qty = parseInt($(this).find('.qty-input').val());
			
 			if (elementWithProductInfo.length > 0 && qty > 0) {
 				
 				if (typeof rm.tagManager.ProductForTrackCart !== 'undefined') {
 				
 					var productObj = new rm.tagManager.ProductForTrackCart(
     						elementWithProductInfo.data('currencycode'),
     						elementWithProductInfo.data('name'),
     						elementWithProductInfo.data('id'),
     						elementWithProductInfo.data('price'),
     						elementWithProductInfo.data('brand'),
     						elementWithProductInfo.data('category'),
     						elementWithProductInfo.data('variant'),
     						elementWithProductInfo.data('position'),
     						elementWithProductInfo.data('dealsflag'),
     						qty,
     						elementWithProductInfo.data('actionfield'));

     				if (typeof rm.tagManager.trackCart!=='undefined') {
     					rm.tagManager.trackCart(productObj, 'add');
     				}
 				}
 				
     		}
		});
	},
	
	trackCart: function(productObj, actionName) {
		// Adding/removing a product to/from the shopping cart
		var eventName = '';
		if (actionName !== undefined && actionName !== null) {
			if (actionName === 'add') {
				eventName = 'addToCart';
			} 
			else if (actionName === 'remove') {
				eventName = 'removeFromCart';
			}
		}
		
		if (eventName !== '') {
			var trackCartEventJson = {
	   			'event': eventName,
	   			'ecommerce': {
	   				'currencyCode': productObj.currencycode
	   			}
	   		};
	   			    
	   		trackCartEventJson.ecommerce[actionName] = {
				'actionField': {'list': productObj.actionfield},
				'products': [{ 
					'name'			: productObj.name,
					'id'			: productObj.id,
					'price'			: productObj.price,
					'brand'			: productObj.brand,
					'category'		: productObj.category, 	
					'variant'		: productObj.variant, 	
					'position'		: productObj.position, 	
					'dimension13'	: getOnDeal(String(productObj.dealsflag)), // Deal or No Deal
					'quantity'		: productObj.quantity
				}]
			};
	   		
	   		dataLayer.push(trackCartEventJson);
		}
		
		console.log('track cart:' + JSON.stringify(dataLayer));
	},
	 
	ProductForTrackCart: function(currencycode, name, id, price, brand, category, variant, position, dealsflag, quantity, actionfield) {
		this.currencycode = currencycode;
		this.name = name;
		this.id = id;
		this.price = price;
		this.brand = brand;
		this.category = category;
		this.variant = variant;
		this.position = position;
		this.dealsflag = dealsflag;
		this.quantity = quantity;
		this.actionfield = actionfield;
	},
	
	escapeHtml: function(text) {
		  var characters = {
		    '&': '&amp;',
		    '"': '&quot;',
		    '\'': '&#039;',
		    '<': '&lt;',
		    '>': '&gt;'
		  };
		  
		  return (text + '').replace(/[<>&"']/g, function(m) {
		    return characters[m];
		  });
	},
		
	addProductImpressionListener: function() {
		
		// for product listing Carousels such as Best Sellers in Home page and Recommendations in Cart page
		if ($('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active').length > 0) {
	        var productsInCarousel = [];
	        $('div.productImpressionTag').filter('.bestSellerTag, .recommendationsTag').filter('.slick-slide.slick-active').each(function() {
	        	
	        	if ($(this).length > 0 && $(this).isOnViewPort()) {
	        		var elementWithProductInfo = $(this).find('.js-track-product-link');
		        	
		        	if (elementWithProductInfo.length > 0) {
		        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
		        		
		        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
		        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
		        			
		        			productsInCarousel.push({
				            	'name'		: elementWithProductInfo.data('name'),
				            	'id'		: elementWithProductInfo.data('id'),
				            	'price'		: elementWithProductInfo.data('price'),
				            	'brand'		: elementWithProductInfo.data('brand'),
				            	'category'	: elementWithProductInfo.data('category'),
				            	'variant'	: elementWithProductInfo.data('variant'),
				            	'list'		: elementWithProductInfo.data('list'),
				            	'position'	: elementWithProductInfo.data('position'),
				            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
				            });
		        		}
	        		}
	        	
	        	}
	        });
	        
	        if (productsInCarousel.length > 0) {
	        	if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	        		rm.tagManager.trackProductImpressionAndPosition(productsInCarousel);
	        	}
	        }
	    }


		// for product listings such as CUB Picks, Category, Order History, Order Templates, Deals listings, Partially Qualified Deals modal
		if ($('div.productImpressionTag:not(.bestSellerTag, .recommendationsTag):not(.slick-slide):not(.slick-active)').length > 0) {
	        var products = [];
	        $('div.productImpressionTag:not(.bestSellerTag, .recommendationsTag):not(.slick-slide):not(.slick-active)').each(function() {
	        	
	        	if ($(this).length > 0 && $(this).isOnViewPort()) {
	        		var elementWithProductInfo = $(this).find('.js-track-product-link');
		        	
		        	if (elementWithProductInfo.length > 0) {
		        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');

		        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
		        			
		        			if ($('#dealPageWrapper').length === 0 || 
		        				($('#dealPageWrapper').length > 0 && 
		        						($(this).closest('.js-track-deal-row').find('.row.deal-item-head.single').length > 0 || // for deals with single base product
		        						 $(this).closest('.js-track-deal-row').find('.js-track-show-details').hasClass('wasclicked')) // for deals with multiple base products
		        				) ||
		        				// for Partially Qualified Deals Modal
		        				($('#partiallyQualified').length > 0 && 
		        				   $(this).closest('.js-track-deal-row').find('.js-track-show-details').hasClass('wasclicked'))
		        			   )
		        			{ 
		        			
		        				$(elementWithProductInfo).attr('data-wasonviewport', 'true');
			        			
					            products.push({
					            	'name'		: elementWithProductInfo.data('name'),
					            	'id'		: elementWithProductInfo.data('id'),
					            	'price'		: elementWithProductInfo.data('price'),
					            	'brand'		: elementWithProductInfo.data('brand'),
					            	'category'	: elementWithProductInfo.data('category'),
					            	'variant'	: elementWithProductInfo.data('variant'),
					            	'list'		: elementWithProductInfo.data('list'),
					            	'position'	: elementWithProductInfo.data('position'),
					            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
					            });
					            /*
					        	var isSegmentEnabled = ( $('#isSegmentEnabled').val() === 'true' ) ? true : false;

					            if ( isSegmentEnabled ) {
					            	
						            //Segment in recommendation page
						            if($('body.page-recommendationCusPage').length > 0){
						        		rm.segment.trackProductImpressionAndPosition('Recommendation Viewed', elementWithProductInfo);
		        					}
	
						            //Segment in deals page
						            if($('body.page-deals').length > 0){
						        		rm.segment.trackProductImpressionAndPosition('Deals Viewed', elementWithProductInfo);
						            }
						            
					            }*/
					            
		        			}
		        		
		        		}
	        		}
	        	}
	        	
	        });
	        
	        
	        if (products.length > 0) {
	        	if(typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined'){
	        		rm.tagManager.trackProductImpressionAndPosition(products);
	        	}
	        }
	    }
		
		
		// Conflicting Deals Modal - Start 
	    if ($('#dealsConflictPopup').length > 0) {
	  		setTimeout(function() {
		        var productsInConflictDealsModal = [];
		        
		        $('div.deal-option.productImpressionTag').each(function() { 
		        	
		        	if ($(this).length > 0 && setTimeout(function() {$(this).isOnViewPort();}, 0)) {
			        	var elementWithProductInfo = $(this).find('.js-track-product-link');
			        	
			        	if (elementWithProductInfo.length > 0) {
			        		var wasOnViewPort = $(elementWithProductInfo).attr('data-wasonviewport');
			
			        		if (wasOnViewPort === undefined || wasOnViewPort !== 'true') { // check if element was not on view port previously
			        			
			        			$(elementWithProductInfo).attr('data-wasonviewport', 'true');
			        			
			        			productsInConflictDealsModal.push({
					        		'name'		: elementWithProductInfo.data('name'),
					            	'id'		: elementWithProductInfo.data('productcode'),
					            	'price'		: elementWithProductInfo.data('price'),
					            	'brand'		: elementWithProductInfo.data('brand'),
					            	'category'	: elementWithProductInfo.data('category'),
					            	'variant'	: elementWithProductInfo.data('variant'),
					            	'list'		: elementWithProductInfo.data('list'),
					            	'position'	: elementWithProductInfo.data('position'),
					            	'dealsFlag'	: elementWithProductInfo.data('dealsflag')
					        	});
			        		}
			        	}
		        	}
		        });
		        
				if (productsInConflictDealsModal.length > 0 && typeof rm.tagManager.trackProductImpressionAndPosition!=='undefined') {
					rm.tagManager.trackProductImpressionAndPosition(productsInConflictDealsModal);
				}
	  		}, 0);
	    }
	    // Conflicting Deals Modal - End
	},

	 trackCarouselBanner: function(label) {
         dataLayer.push({
            'event': 'gaEvent',
            'eventCategory': 'Ecommerce',
            'eventAction': 'promotionClick',
            'eventLabel': label
        });
	      return true;
	 }, 

	 trackRecommendation: function(action, label) {
		 
		 if ( action === 'remove' ) {
	       dataLayer.push({
	          'event': 'gaEvent',
	          'eventCategory': 'Page Body',
	          'eventAction': 'Clicked',
	          'eventLabel': label
	      });
		 }

	      return true;
	 },

	 trackDealsModal: function(type) {
       dataLayer.push({
          'event': 'gaEvent',
          'eventCategory': 'Modal',
          'eventAction': 'Viewed',
          'eventLabel': type
      });

      return true;
	 },

	 trackChosenDealsClick: function(type, label) {
	       dataLayer.push({
	          'event': 'gaEvent',
	          'eventCategory': 'Modal',
	          'eventAction': 'Clicked',
	          'eventLabel': type +' | '+ label
	      });

	      return true;
		 },

	addDealsImpressionAndPosition: function(action, type, label){
		
		if ( action === 'Viewed' ) {
			if ( typeof rm.tagManager.trackDealsModal !== 'undefined' ) {
				rm.tagManager.trackDealsModal(type);
			}
		} else if ( action === 'Clicked' ) {
			if ( typeof rm.tagManager.trackChosenDealsClick !== 'undefined' ) {
				rm.tagManager.trackChosenDealsClick(type, label);
			}
		}
	},
	
	trackCarouselClick: function(carouselObj) {
		var obj = carouselObj;

		dataLayer.push({
			   'event':'carouselBannerClick',
			   'ecommerce': {
			     'promoClick': {
			       'promotions': [{
			         'id': obj.altText === '' ? 'Carousel Banner Image' : obj.altText,
			         'name': obj.altText === '' ? 'Carousel Banner Image' : obj.altText,
			         'creative': obj.type+obj.position,
			         'position': 'slot'+obj.position
			      }]
			    }
			  },
			  'eventCallback': function() {
				  window.location.href = obj.url;
			  }
		});
		
        console.log('carousel tagging sent' + JSON.stringify(dataLayer));
	},

	trackSupportTabClick: function(label) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Page Body',
	        'eventAction': 'navigationClick',
	        'eventLabel': label
	    });
	},
	
	trackInvoiceDiscrepancyLinkClick: function(action, label) {
	    dataLayer.push({
	        'event': 'gaEvent',
	        'eventCategory': 'Page Body',
	        'eventAction': action,
	        'eventLabel': label
	    });
	 },
	 
	trackInvoiceDiscrepancyStepProcess: function(title) {
	    dataLayer.push({
	        'event': 'VirtualPageview',
	        'virtualPageURL': '/your-business/invoicediscrepancy',
	        'virtualPageTitle': title,
	    });
	 },

    trackSmartRecommendationImpression: function(recommendedProducts) {
        var smartRecommendationImpression = {
            'event': 'smartRecommendationImpression',
            'ecommerce': {
                'currencyCode': 'AUD',
        		'impressions': recommendedProducts
            }
        };

        var okToPush = rm.tagManager.isProductImpressionAndPositionOKToPush(smartRecommendationImpression);
        if (okToPush) {
            dataLayer.push(smartRecommendationImpression);
        }
    },

	trackSmartRecommendationAddToCart: function (productElement, recommendationGroup, componentSlot, componentPosition) {
	    var elementWithProductInfo = productElement.find('.js-track-product-link');
        var qty = parseInt(productElement.find('.qty-input').val());

        if (elementWithProductInfo.length > 0 && qty > 0) {
            var product = {
                'name'                  : elementWithProductInfo.data('name'),
                'id'                    : elementWithProductInfo.data('id'),
                'price'                 : elementWithProductInfo.data('price'),
                'quantity'              : qty,
                'brand'                 : elementWithProductInfo.data('brand'),
                'category'              : elementWithProductInfo.data('category'),
                'variant'               : elementWithProductInfo.data('variant'),
                'list'                  : elementWithProductInfo.data('list'),
                'actionfield'           : elementWithProductInfo.data('actionfield'),
                'position'              : elementWithProductInfo.data('position'),
                'dealsFlag'             : elementWithProductInfo.data('dealsflag'),
                'dimension7'            : recommendationGroup,
                'dimension8'            : elementWithProductInfo.closest('.productImpressionTag').data('smart-recommendation-model')
            };
            var products = [];
            products.push(product);

            var trackSmartRecommendationAddToCartJson = {
                'event': 'smartRecommendationAddToCart',
                'componentSlot' : componentSlot,
                'componentPosition' : componentPosition,
                'ecommerce': {
                    'currencyCode'  : 'AUD'
                }
            };

            trackSmartRecommendationAddToCartJson.ecommerce.add = {
                'actionField'   : {'action': 'add'},
                'products'      : products
            };

            dataLayer.push(trackSmartRecommendationAddToCartJson);

        }
	},

	trackSmartRecommendationProductView: function (productElement, recommendationGroup, componentSlot, componentPosition) {
	    var product = {
            'name'                  : productElement.data('name'),
            'id'                    : productElement.data('id'),
            'price'                 : productElement.data('price'),
            'brand'                 : productElement.data('brand'),
            'category'              : productElement.data('category'),
            'variant'               : productElement.data('variant'),
            'list'                  : productElement.data('list'),
            'actionfield'           : productElement.data('actionfield'),
            'position'              : productElement.data('position'),
            'dimension7'            : recommendationGroup,
            'dimension8'            : productElement.closest('.productImpressionTag').data('smart-recommendation-model')
        };

        var trackRecommendationViewEventJson = {
            'event': 'smartRecommendationProductView',
            'componentSlot' : componentSlot,
            'componentPosition' : componentPosition,
            'ecommerce': {
                'currencyCode'  : 'AUD'
            }
        };

        var products = [];
        products.push(product);

        trackRecommendationViewEventJson.ecommerce.click = {
            'actionField'   : {'action': 'click'},
            'products'       : products
        };

        dataLayer.push(trackRecommendationViewEventJson);
	},

	trackSmartRecommendationOrdered: function (products, orderId) {
        var trackRecommendationOrderEventJson = {
            'event': 'smartRecommendationOrdered',
            'ecommerce': {
                'currencyCode'  : 'AUD'
            }
        };

        trackRecommendationOrderEventJson.ecommerce.purchase = {
            'actionField'   : {'id': orderId.toString()},
            'products'      : products
        };

        dataLayer.push(trackRecommendationOrderEventJson);
	},

	getSmartRecommendationModel: function (smartRecommendationModel) {
	    if (smartRecommendationModel === 'MODEL1') {
	        return 'M1';
	    }
	    if (smartRecommendationModel === 'MODEL2') {
	        return 'M2';
	    }
        if (smartRecommendationModel === 'MODEL3') {
	        return 'M3';
	    }
	    return '';
	}

};
/* globals ACC */
/* globals window */
/* globals document*/
/* globals sessionStorage*/

'use strict';


rm.recommendation = {

		createListeners: function() {
		    var bdeViewOnly= false;
		    if($('#bdeUser').length > 0){
                    bdeViewOnly = true;
            }
			$('#recommendationHeaderStar').popover({
                placement: 'bottom',
                html: true,
                trigger: 'manual',
                animation: true,
                delay:{show: 100, hide: 1000},
                template: '<div class="popover popover-recommendationwelcome" ><div class="arrow"></div><div class="popover-content popover-recommendationwelcome-content"></div></div>'
            });
			

			$('#globalMessages .recommendationwelcome-mobile').addClass('hidden');
			$('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');

			if(parseInt($('.recommendationsCount').text()) > 1 && sessionStorage.getItem('ShowWelcome') === 'TRUE' && !bdeViewOnly){
                $('#recommendationHeaderStar').popover('toggle');
				$('#globalMessages .recommendationwelcome-mobile').removeClass('hidden');
				$('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');


                sessionStorage.setItem('ShowWelcome','FALSE');
                setTimeout(function ()
                 {
                     $('#recommendationHeaderStar').popover('hide');
					 $('#globalMessages .recommendationwelcome-mobile').addClass('hidden');
					 $('#cartCtrl #simulationErrors .recommendationwelcome-mobile').addClass('hidden');


                 }, 10000);
            }

			// When changing the quantity (Sales Rep page & Customer page)
			$('.recommendationRepRow .select-quantity .up, .recommendationRepRow .select-quantity .down, .recommendationRepRow .select-list .select-items > li, .recommendationCusRow .select-quantity .up, .recommendationCusRow .select-quantity .down, .recommendationCusRow .select-list .select-items > li')
			.on('click touchstart',function() {
				var input = $(this).closest('.product-row').find('.qty-input'),
					min = input.data('minqty');

				setTimeout(function(){
					if(isNaN(input.val()) || parseInt(input.val(), 10) < min) {
						input.val(min);
					}

				},50);

				if (!$(this).hasClass('disabled')){ // for Sales Rep page
					rm.recommendation.enableUpdateRecommendations();
				}
			});

			// When changing the quantity (Sales Rep page & Customer page)
			$('.recommendationRepRow .select-quantity .qty-input, .recommendationCusRow .select-quantity .qty-input').on('keyup',function() {
				var that = $(this),
					min = that.data('minqty');

				setTimeout(function(){
					if(isNaN(that.val()) || parseInt(that.val(), 10) < min) {
						that.val(min);
					}

					if ($(this).hasClass('recommendationRepRow') && $(':focus').is('.qty-input')) { // for Sales Rep page
						rm.recommendation.enableUpdateRecommendations();
					}
				},2000);
			});

			// When deleting a recommendation (Sales Rep page)
			//$('.recommendationRepRow .submitRemoveRecommendation').on('click',function(e) {
			$('.recommendationRepRow .deleteRecommendation').on('click',function(e) {
				e.preventDefault();
				rm.recommendation.deleteRecommendation($(this));
				rm.recommendation.enableUpdateRecommendations();
			});

			// When deleting a recommendation (Customer page) or when rejecting a recommendation (Cart page)
			$('.recommendationCusRow .deleteRecommendation, .recommendation-cart-actions .deleteRecommendation').on('click',function(e) {
				e.preventDefault();
				rm.recommendation.updateRecommendation($(this), 'REJECTED');
                rm.cart.refreshPage();
			});

			// When clicking the "UPDATE RECOMMENDATIONS" button (Sales Rep page)
			$('#update-recommendation-button').on('click',function() {
				if(!$(this).hasClass('disabled')){
					rm.recommendation.updateQuantityOrUnit();
				}
			});

      // Copy number of recommendations to mobile view on load
      $(document).ready(function(){
        var src = $('.recommendation .recommendationsCount').html();
        console.log(src);
        $('.navbar-toggle .recommendationsCount').html(src);
        rm.recommendation.displayRecommendationCount(src);
      });


			/*
			// When clicking on any anchor tag except the "Delete" link on each recommendation (Sales Rep page)
			$('a:not(.recommendationRepRow .submitRemoveRecommendation)').on('mousedown',function(e) {
				e.preventDefault();

				var target = $(this).attr('href');

				if (target !== '#' && target !== '' && target !== null) {
					if (!$('#update-recommendation-button').hasClass('disabled')) {
						$('#unsavedChangesPopup').attr('data-target', target);
						rm.recommendation.unsavedChangesPopup();
					} else {
						window.location.href = target;
					}
				}
			});

			// When clicking "Yes, Save My Changes" button on the unsaved changes popup
			$('.unsaved-changes-popup .btn-primary').one('click',function() {
				rm.recommendation.updateQuantityOrUnit();
				$.magnificPopup.close();
			});
			*/
            $('.addRecommendationAction').on('click',function(){
                var addToCartForm = $(this).closest('.add_to_cart_form');
                var quantityField = $(this).closest('.addtocart-qty');
                var productCode = $('[name=productCodePost]', addToCartForm).val();
                var quantityValue = $('[name=qty]', addToCartForm).val();
                var uom = $('[name=unit]', addToCartForm).val();
                var dataPost = {'productCodePost': productCode,
                							'qty': quantityValue,
                							'unit': uom
                							};
                var recommendationAction = $(this);
                sessionStorage.setItem('ShowWelcome','FALSE');

                $(recommendationAction).find('#recommendationText').html($('#addedText').html());
                if($(recommendationAction).hasClass('adding')){
                  return;
                }
                $(recommendationAction).addClass('adding');

                $.ajax({
                    url:'/sabmStore/en/recommendation/add',
                    type:'POST',
                    dataType: 'json',
                    data: JSON.stringify(dataPost),
                    contentType: 'application/json',
                    success: function(result) {
                    	rm.recommendation.displayAddToRecommendationPopup(result);
                       console.log('recommendations:' + result.recommendationsCount);
                        $(recommendationAction).find('#recommendationStar').removeClass('icon-star-normal').addClass('icon-star-add');

                        setTimeout(function ()
                        {
                            $(recommendationAction).find('#recommendationStar').removeClass('icon-star-add').addClass('icon-star-normal');
                            $(recommendationAction).find('#recommendationText').html($('#addText').html());
                            $(quantityField).find('.qty-input')[0].value = 1;
                            $('[name=qty]', addToCartForm)[0].value = 1;
                            $(recommendationAction).removeClass('adding');
                        }, 5000);


                    },
                    error:function(result) {
                       console.error('results:' + result);
                    }
                });

            });

            // Add to cart (Customer page)
      $('.recommendation-addToOrder').on('click',function(e) {
				e.preventDefault();

				var that = $(this);
				var closestTableRow = $(this).closest('.product-row');
				var recommendationType = $(closestTableRow).find('.recommendationType').val();

				var recommendation = {};

				if (recommendationType === 'PRODUCT') {
					var productCode = $(closestTableRow).find('[name=productCodePost]').val();
					var qty = $(closestTableRow).find('.qty-input').val();
					var unit = $(closestTableRow).find('.select-btn').attr('data-value');

					recommendation.productCodePost = productCode;
					recommendation.qty = qty;
					recommendation.unit = unit;
				}
				else { // if DEAL

					var dealCode = $(closestTableRow).find('[name=dealCodePost]').val();
					var dealProducts = [],
				    	dealProduct = {};

					$(closestTableRow).find('.deal-product-row').each(function () {
						if ($(this).css('display') !== 'none') {
							var dealProductCode = $(this).find('[name=productCodePost]').val();
							var dealProductQty = $(this).find('.qty-input').val();
							//var dealProductUnit = $(this).find('.select-btn').attr('data-value');

							dealProduct.productCodePost = dealProductCode;
							dealProduct.qty = dealProductQty;
							//dealProduct.unit = dealProductUnit;

							dealProducts.push(dealProduct);
						}
					});

					recommendation.dealCode = dealCode;
					recommendation.baseProducts = dealProducts;
				}

				var dataPost = recommendation;

				rm.recommendation.addRecommendationToCart(recommendationType, dataPost).success(function(result) {
                    console.log('success');

                    if(result.addToCartForErrorLayer){
    					$('#globalMessages').empty();
    					$('#globalMessages').append(result.addToCartForErrorLayer);
    				}else{
    					$('#globalMessages').empty();
    				}
    				if (result) {
    					$(closestTableRow).hide();
    					//$(window)[0].location.reload();
                        ACC.product.displayAddToCartPopup(result);
    					rm.recommendation.updateRecommendation(that, 'ACCEPTED');
    					ACC.minicart.refreshMiniCartCount();
    					//ACC.common.refreshScreenReaderBuffer();
    				}

				}).error(function(result) {
                    console.error('error:' + JSON.stringify(result));
				});

            });

            // When clicking "Yes" (Cart page)
            $('.recommendation-cart-actions .recommendation-addToOrder').on('click',function(e) {
				e.preventDefault();

				var that = $(this);
				var closestTableRow = $(this).closest('.cartRecommendations');
				var recommendationType = $(closestTableRow).find('.recommendationType').val();

				var recommendation = {};

				if (recommendationType === 'PRODUCT') {
					var productCode = $(closestTableRow).find('[name=productCodePost]').val();
					var qty = $(closestTableRow).find('[name=qty]').val();
					var unit = $(closestTableRow).find('[name=unit]').val();

					recommendation.productCodePost = productCode;
					recommendation.qty = qty;
					recommendation.unit = unit;
				}
				//else { // if DEAL - the add to cart function defined in dealsCtrl.js is invoked
				//}

				var dataPost = recommendation;

				rm.recommendation.addRecommendationToCart(recommendationType, dataPost).success(function(result) {
					console.log('success');

                    if(result.addToCartForErrorLayer){
    					$('#globalMessages').empty();
    					$('#globalMessages').append(result.addToCartForErrorLayer);
    				}else{
    					$('#globalMessages').empty();
    				}
    				if (result) {
    					$(closestTableRow).hide();
    					ACC.product.displayAddToCartPopup(result);
    					rm.recommendation.updateRecommendation(that, 'ACCEPTED', result);
    					//ACC.minicart.refreshMiniCartCount();
    					//ACC.common.refreshScreenReaderBuffer();

    					if ($('.cartRow').length > 0) {
							rm.recommendation.resetCartForRecommendation(dataPost.productCodePost, 'product');
						} else {
							rm.cart.refreshPage();
						}
    				}

				}).error(function(result) {
					console.error('error:' + JSON.stringify(result));
				});

            });
		},

		displayAddToRecommendationPopup: function (result)
		{

			$('.recommendationCounter').show();
      // $('.recommendation .recommendationsCount').html(result.recommendationsCount);
      // $('.navbar-toggle .recommendationsCount').html(result.recommendationsCount);
      rm.recommendation.displayRecommendationCount(result.recommendationsCount);

			var timeoutId;

			$('#addToRecommendationLayer').remove();


			if ($('#header').is(':visible')) {
				$('#header .recommendation').append(result.addToRecommendationLayer);
			}

			else if ($('#nav').is(':visible')) {
				$('#nav').append(result.addToRecommendationLayer);
			}

			$('#addToRecommendationLayer').show(function(){

				if (typeof timeoutId !== 'undefined')
				{
					clearTimeout(timeoutId);
				}
				timeoutId = setTimeout(function ()
				{
					$('.itemsAddedToRecommendation').hide();
				}, 3000);

			});
		},
		resetCartForRecommendation: function(data,type){

        	$('body').addClass('loading');
        	$.ajax({
        		url:'/cart',
        		type:'GET',
        		success: function(result) {

        			$.magnificPopup.close();
                    $('.cart').html($(result).find('[class=cart]').html());
                    $('#orderTotals').html($(result).find('[id=orderTotals]').html());
                    $('#simulationErrors').html($(result).find('#simulationErrors').html());
                    $('#globalMessage').html($(result).find('[id=globalMessage]').html());

        			rm.cart.quickInit();
        			ACC.minicart.refreshMiniCartCount();
        			$('body').removeClass('loading');
        			rm.recommendation.highlightNewlyAddedToCart(data, type);
        		},
        		error:function() {
        			console.log('error resetting cart');
        			$('body').removeClass('loading');
        		}

        	});
        },

		highlightNewlyAddedToCart: function(data, type){
		    if ($('.cartRow').length > 0) {
		        $('.cartRow').each(function(){
		            var productID = $(this).find('.js-track-product-link').attr('data-id');
		            var productURL = $(this).find('.js-track-product-link').attr('data-url');

		            if(type === 'deal'){
		                for(var range in data.ranges){ // Loop over ranges in deal
                        	for(var prod in data.ranges[range].baseProducts){ // Loop of products in range
                        			var productCodePost = data.ranges[range].baseProducts[prod].productCode;
                        			if(productCodePost === productID){
                        			    $(this).effect('highlight', {color: '#f0f4f9'}, 5000);
                        			    break;
                        			}
                        	}
                        }
		            } else {
		                if(data === productID || productURL.endsWith(data)){
                            $(this).effect('highlight', {color: '#f0f4f9'}, 5000);

                        }
		            }

		        });
		    }
		},

		displayRecommendationCount: function(count) {
		  if (count > 0) {
              $('.recommendation .recommendationsCount').html(count);
              $('.navbar-toggle .recommendationsCount').html(count);
              $('.recommendationsCount').removeClass('hidden');
              $('.navbar-toggle .recommendationsCount').removeClass('hidden');
          } else {
              $('.recommendationsCount').addClass('hidden');
              $('.navbar-toggle .recommendationsCount').addClass('hidden');
          }
		},

		// function to call when clicking the "Delete" link (Sales Rep page)
		deleteRecommendation: function(obj) {
			$('body').addClass('loading');
			//var recommendationId = obj.closest('.table-row').find('.recommendationId').val();
			var recommendationId = obj.closest('.product-row').find('.recommendationId').val();

			//obj.closest('.table-row').hide();
			obj.closest('.product-row').hide();
			var recommendationIdsToDelete = $('#recommendationIdsToDelete').val();
			if(recommendationIdsToDelete === '' || recommendationIdsToDelete === null){
				$('#recommendationIdsToDelete').val(recommendationId);
			}else{
				$('#recommendationIdsToDelete').val(recommendationIdsToDelete + ',' + recommendationId);
			}

			$('body').removeClass('loading');
		},

		// function to call when clicking the "Delete" link (Customer page) or when clicking "No" (Cart page)
		updateRecommendation: function(obj, status) {
			$('body').addClass('loading');

			var closestTableRow = $(obj).closest('.product-row, .cartRecommendations'),
			    recommendationId = $(closestTableRow).find('.recommendationId').val(),
			    recommendationType = $(closestTableRow).find('.recommendationType').val(),
			    recommendations = [],
				recommendation = {};

			recommendation.recommendationId = recommendationId;
			recommendation.recommendationType = recommendationType;
			recommendation.status = status;
			recommendations.push(recommendation);

			var dataPost = {'recommendations': recommendations,
							'recommendationsToDelete': ''
							};

			$.ajax({
				url: '/sabmStore/en/recommendation/update',
				type: 'POST',
				dataType: 'json',
				data: JSON.stringify(dataPost),
	            contentType: 'application/json',
				success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					rm.recommendation.displayRecommendationCount(result);
					if(result === '0'){
					    $('#recommendationCarousel').addClass('hidden');
					}

				},
				error: function(result) {
					console.log('error: ' + JSON.stringify(result));
				}
			});

			$('body').removeClass('loading');
		},

		// function to call when clicking the "UPDATE RECOMMENDATIONS" button (Sales Rep page)
		updateQuantityOrUnit: function() {
			$('body').addClass('loading');

			var recommendationIdsToDelete = $('#recommendationIdsToDelete').val();
			var dataPost = {'recommendations': [],
							'recommendationsToDelete': recommendationIdsToDelete
							};

			$('.product-row').each(function () {
				if($(this).css('display') !== 'none'){
					var recommendationId = $(this).find('.recommendationId').val();
					var recommendationType = $(this).find('.recommendationType').val();

					var recommendation = {};
					recommendation.recommendationId = recommendationId;
					recommendation.recommendationType = recommendationType;

					if (recommendationType === 'PRODUCT') {
						var qty = $(this).find('.qty-input').val();
						var unit = $(this).find('.select-btn').attr('data-value');

						recommendation.quantity = qty;
						recommendation.unit = unit;
					}
					else { // if DEAL
						var dealProducts = [];

						$(this).find('.deal-product-row').each(function () {
							if ($(this).css('display') !== 'none') {
								var dealProductCode = $(this).find('[name=productCodePost]').val();
								var dealProductQty = $(this).find('.qty-input').val();
								//var dealProductUnit = $(this).find('.select-btn').attr('data-value');

								var dealProduct = {};
								dealProduct.productCodePost = dealProductCode;
								dealProduct.qty = dealProductQty;
								//dealProduct.unit = dealProductUnit;

								dealProducts.push(dealProduct);
							}
						});

						recommendation.baseProducts = dealProducts;
					}

					dataPost.recommendations.push(recommendation);

				}
			});

			//$('#globalMessages .successSavingRecommendations').hide();
			//$('#globalMessages .errorSavingRecommendations').hide();
			$('#successSavingRecommendations .successSavingRecommendations').hide();
			$('#errorSavingRecommendations .errorSavingRecommendations').hide();

			$.ajax({
				url:'/sabmStore/en/recommendation/updateRecommendations',
				type:'POST',
				dataType: 'json',
				data: JSON.stringify(dataPost),
	            contentType: 'application/json',
				success: function(result) {
					console.log('success: ' + JSON.stringify(result));
					rm.recommendation.displayRecommendationCount(result);

					if(result) {
						//$('#globalMessages').html($('#successSavingRecommendations').html());
						//$('#globalMessages .successSavingRecommendations').show();

						$('#successSavingRecommendations .successSavingRecommendations').show();

						setTimeout(function() { // hide message after 5 seconds
							$('#successSavingRecommendations .successSavingRecommendations').hide();
						}, 5000);
					} else {
						//$('#globalMessages').html($('#errorSavingRecommendations').html());
						//$('#globalMessages .errorSavingRecommendations').show();
						$('#errorSavingRecommendations .errorSavingRecommendations').show();
					}

					$('body').removeClass('loading');
					rm.recommendation.disableUpdateRecommendations();
				},
				error:function(result) {
					console.log('error: ' + result);
					$('#errorSavingRecommendations').show();
					$('body').removeClass('loading');
				}
			});

			$('#recommendationIdsToDelete').val('');
		},

		addRecommendationToCart: function(recommendationType, dataToPost) {
			return $.ajax({
	            url: (recommendationType === 'PRODUCT') ? '/sabmStore/en/cart/addAjax' : '/sabmStore/en/cart/add/deal',
	            type: 'POST',
	            dataType: 'json',
				data: JSON.stringify(dataToPost),
	            contentType: 'application/json'
	        });
		},

		enableUpdateRecommendations: function() {
			$(window).on('beforeunload', function() {
				return 'You have unsaved changes!';
	        });

			$('#update-recommendation-button').removeClass('disabled');
			//$('.template-actions').addClass('disabled');
			//$('.template-actions .hrefAddtoCart').addClass('notActive');
		},

		disableUpdateRecommendations: function() {
			$(window).off('beforeunload');

			$('#update-recommendation-button').addClass('disabled');
			//$('.template-actions').removeClass('disabled');
			//$('.template-actions .hrefAddtoCart').removeClass('notActive');
		},

		unsavedChangesPopup: function() {
			$.magnificPopup.open({
				items:{
			   src: '#unsavedChangesPopup',
			   type: 'inline'
				},
			   //removalDelay: 500,
			   mainClass: 'mfp-slide',
			   modal: true
			});
		},

		proceedToTarget: function() {
			var target = $('#unsavedChangesPopup').attr('data-target');
			window.location.href = target;
		},

		handleNoRecommendationMessage: function() {
		    if ($('.no-rec-message').length > 0) {
		        if ($('.recommendation-component').length > 0) {
		            console.log('No recommendations but with smart recommendations');
		        } else {
		            $('.no-rec-message').show();
		        }
		    }
		}
};

$('document').ready(function() {
	rm.recommendation.createListeners();
	rm.recommendation.handleNoRecommendationMessage();
});
//Sets equal height on load
$(window).bind('load', function(){
	if ($('#slider-load').hasClass('slick-initialized')){
		//rm.utilities.setEqualHeight($('.cartRecommendations .product-pick-description h3' ));
		rm.utilities.setEqualHeight($('.cartRecommendations .product-pick .card-content .col-md-9' ));
		rm.utilities.setEqualHeight($('.cartRecommendations .product-pick .product-pick-title' ));
}});
/*jshint unused:false*/

'use strict';

/* Tab Component */
rm.tabs = {
	
	init: function(){
		var tabContent = '<div class="tab-content" id="pills-tabContent"></div>';
		var tabCount = $('.tabComponentUID').length;
	    	
		$(tabContent).appendTo('.tabComponent');

		
		$('.tabComponent > #pills-tab').addClass('tab-count-' + tabCount);
		
		$('.tabComponentUID').map(function(){
		  		$('#'+$(this).val()).detach().appendTo('.tabComponent > #pills-tabContent');
		});

		/* display the 1st tab by default */
		$('.tabComponent #pills-tab li.nav-item:first-child').addClass('active');
		$('#pills-tabContent .tab-pane:first-child').addClass('active in');

		$('.tabComponent ul li #pills-tabContent').remove();		
	}
};


/* Notifications popup in edit profile */

/// Assuming jQuery is available

$(document).ready(function() {
    var currentPath = window.location.pathname;
    var expectedPath = "/sabmStore/en/your-business/profile";
    
    if (currentPath === expectedPath) {
        var initialMobileNumber = $('#customerMobileNumber').val();
        var notificationField = $('#notificationField').val();
        var modal = $('#notificationModal');
        var confirmBtn = $('#confirmModal');
        var cancelBtn = $('#cancelModal'); // Assuming you have a cancel button with ID 'cancelModal'
        var mobileNumberField = $('#mobileNumber');
        var updateProfileForm = $('#updateProfileForm');
        var mobileNumberFieldVal = $('#mobileNumberField');
        
        function containsSpaces(inputString) {
            return /\s/.test(inputString);
        }

        function resetMobileNumber() {
            mobileNumberField.val(initialMobileNumber);
            let modifiedString = initialMobileNumber;
            if(!containsSpaces(modifiedString)){
                modifiedString = initialMobileNumber.substring(0, 4) + " " + initialMobileNumber.substring(4, 7) 
                + " " + initialMobileNumber.substring(7);
            }
            mobileNumberFieldVal.val(modifiedString);
            modal.hide();
        }
        
        // Attach the event handler to the cancel button
        cancelBtn.on('click', function() {
            resetMobileNumber();
            modal.hide(); // Hide the modal when cancel is clicked
        });

        confirmBtn.on('click', function() {
            modal.hide(); // Hide the modal first
            updateProfileForm.off('submit').submit(); // Submit the form
        });

        // Close modal when clicking outside of it
        $(window).on('click', function(event) {
            if ($(event.target).is(modal)) {
                resetMobileNumber();
            }
        });

        updateProfileForm.on('submit', function(event) {
            var currentMobileNumber = mobileNumberField.val();

            if (initialMobileNumber && currentMobileNumber === '' && notificationField) {
                event.preventDefault();
                modal.show();
            }
        });
    }
});


/* Notifications popup in edit user */

// Ensure DOM is fully loaded before executing script
$(document).ready(function() {
    // Cache DOM elements for later use
    var modal = $('#notificationModal'); // The modal dialog element
    var confirmBtn = $('#confirmModal'); // Button for confirming action within modal
    var cancelBtn = $('#cancelModal'); // Button for canceling action within modal
    var mobileNumberField = $('#register_phoneNumber'); // Input field for mobile number
    var initialMobileNumber = mobileNumberField.val(); // Store initial mobile number value for reset
    var notificationField = $('#notificationField'); // Hidden field or input for notification logic
    var phoneNumberField = $('#phoneNumber'); // Hidden field or input for phone number logic
    var saveBtn = $('#register-save'); // Button to trigger modal display and form submission logic

    // Function to check if a string contains spaces
    function containsSpaces(inputString) {
        return /\s/.test(inputString);
    }

    // Function to format mobile numbers according to specific criteria
    function formatMobileNumber(mobileNumber) {
        // Add spaces for readability if the number doesn't contain spaces already
        if (!containsSpaces(mobileNumber)) {
            return mobileNumber.substring(0, 4) + " " + mobileNumber.substring(4, 7) + " " + mobileNumber.substring(7);
        }
        return mobileNumber;
    }

    // Function to reset the mobile number to its initial value
    function resetMobileNumber() {
        mobileNumberField.val(initialMobileNumber); // Reset field value
        mobileNumberField.val(formatMobileNumber(initialMobileNumber)); // Format and set the mobile number
        modal.hide(); // Hide the modal
    }

    // Event handler for the confirm button click
    confirmBtn.on('click', function(event) {
        event.stopPropagation(); // Prevent event from bubbling up the DOM
        modal.hide(); // Hide the modal
        // Trigger AngularJS's digest cycle to handle form submission
        angular.element($('#sabmEditUserForm')).scope().$apply(function(scope) {
            scope.editUserSubmit(scope.editUser);
        });
    });

    // Event handler for the cancel button click
    cancelBtn.on('click', function(event) {
        event.stopPropagation(); // Prevent event from bubbling up the DOM
        resetMobileNumber(); // Reset the mobile number and hide the modal
        modal.hide(); // This could be redundant if resetMobileNumber() always hides the modal
    });

    // Event handler for the save button click
    saveBtn.off('click.save').on('click.save', function(event) {
        event.preventDefault(); // Prevent default form submission
        // Show modal if certain conditions are met, otherwise submit form
        if (notificationField.val() !== '' && phoneNumberField.val() !== '' && mobileNumberField.val() === '') {
            modal.show(); // Conditions met, show modal
        } else {
            // Conditions not met, directly invoke AngularJS function for form submission
            angular.element($('#sabmEditUserForm')).scope().$apply(function(scope) {
                scope.editUserSubmit(scope.editUser);
            });
        }
    });
});

$(document).ready(function () {
  if (parent.smarteditJQuery) {
    $("[ng-cloak]").removeAttr("ng-cloak");
  }
});
