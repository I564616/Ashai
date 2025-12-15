var rm = rm || {};

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
					if ((item.cubStockStatus!=undefined && item.cubStockStatus.code == 'lowStock'))
					{
						 renderHtml += "<span class='title low-stock-status-label'>Likely Out of Stock</span>"
					}	
					if ((item.maxorderquantity != undefined && item.maxorderquantity != null))
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


/* ===== SAB Miller Scripts ===== */

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
/* globals document */
/* globals window */
/* globals ACC */
/*jshint unused:false*/
/*author yuxiao.wang*/

'use strict';
rm.forgotpassword = {

	init : function() {
		this.bindForgotPasswordInput($('#forgottenPwd_email'));
		this.bindForgotPasswordButton($('#submit_button'));
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
	emailPresentSubmit: function(email)
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
					    $('#forgottenPwdForm').submit();
            	    }
            	});
    	},
	bindForgotPasswordInput : function(input) {
		//the blur event of input
		input.blur(function() {
			//add validate to email by yuxiao
			if (!rm.customer.emailInvalid(input.val())) {
				rm.forgotpassword.hide();
				rm.forgotpassword.emailPresent(input.val());
			} else {
				rm.forgotpassword.show();
			}
		});

	},

	bindForgotPasswordButton : function(button) {
		//button click event to submit by ajax
		button.mousedown(function(e) {
			setTimeout(function() {
				var email = $('#forgottenPwd_email');
				//add validate to email by yuxiao
				if (!rm.customer.emailInvalid(email.val())) {
					rm.forgotpassword.hide();
				    rm.forgotpassword.emailPresentSubmit(email.val());
				} else {
					rm.forgotpassword.show();
				}
			}, 100);
		});

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
			$(this).attr('data-value',rm.datepickers.convertDate(date));
		});

		$('.billing-payment .billingdate-end').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-value',rm.datepickers.convertDate(date));
		});

		$('.raised-invoice-discrepancy .invoicedate-start').datepicker({
		    format: 'dd/mm/yyyy',
			autoclose: true,
		    orientation: 'bottom left'
		}).on('changeDate',function(e){
			var date = $(this).datepicker('getDate');
			$(this).attr('data-selectDate', date);
			
			$(this).attr('data-value',rm.datepickers.convertDate(date));
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
									kegDatesArr[k] = new Date(parseInt(deliveryDates[i].dateList[k]) - (clientTimezone * 60000));
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = new Date(parseInt(deliveryDates[i].dateList[p]) - (clientTimezone * 60000));
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = new Date(parseInt(deliveryDates[i].dateList[pk]) - (clientTimezone * 60000));
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
							publicHolidayDatesArr[x] = new Date(parseInt(publicHolidayData[x]) - (clientTimezone * 60000));
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
                    console.error("$('#cutofftime').val()="+$('#cutofftime').val());
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', cutoffTime);
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', cutoffTime);
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', cutoffTime);
                    $('#datepicker-specific-day').datepicker('setCutoffTime', cutoffTime);
 
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
									kegDatesArr[k] = new Date(parseInt(deliveryDates[i].dateList[k]) - (clientTimezone * 60000));
								}
								packTypesArr.push('KEG');
							}
						}
						if (deliveryDates[i].packType === 'PACK') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var p=0; p < deliveryDates[i].dateList.length; p++) {
									packDatesArr[p] = new Date(parseInt(deliveryDates[i].dateList[p]) - (clientTimezone * 60000));
								}
								packTypesArr.push('PACK');
							}
						}
						if (deliveryDates[i].packType === 'PACK_KEG') {
							if (deliveryDates[i].dateList.length > 0) {
								for(var pk=0; pk < deliveryDates[i].dateList.length; pk++) {
									packKegDatesArr[pk] = new Date(parseInt(deliveryDates[i].dateList[pk]) - (clientTimezone * 60000));
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
							publicHolidayDatesArr[x] = new Date(parseInt(publicHolidayData[x]) - (clientTimezone * 60000));
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
                    console.error("$('#cutofftime').val()="+$('#cutofftime').val());
                    cutoffTime = $('#cutofftime').val();
                    //pass the public holidays array in the bootstrap-datepicker.js
                    $('.global-header .delivery-header-input').datepicker('setCutoffTime', cutoffTime);
                    $('.mobile-head .delivery-header-input').datepicker('setCutoffTime', cutoffTime);
                    $('.page-cartPage .cart-datepicker').datepicker('setCutoffTime', cutoffTime);
                    $('#datepicker-specific-day').datepicker('setCutoffTime', cutoffTime);
 
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
							
							if($custMobileNumber.val() !== $curMobileNumber.val()){
								if($curMobileNumber.val().length !== 0){
									if(($curMobileNumber.val().trim().charAt(0) + $curMobileNumber.val().trim().charAt(1) === '04' && $curMobileNumber.val().length === 12 && mobileNumberPattern.test($curMobileNumber.val()))){
										enabledSaveButton($this);
									}else{
										disabledSaveButton($this);
									}
									
									if($curMobileNumber.val() === '04' || $this.val() === ''){
										$curMobileNumber.val('');
										enabledSaveButton($this);
									}

								}else{
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
        row = '<tr><td><a href="order/' + item.orderNo + '" class="inline">' + item.sapOrderNo + '</a></td><td data-value="' + item.dateStamp + '">' + item.date + '</td><td data-value="' + item.deliveryDateStamp + '">' + item.deliveryDate + '</td><td><span class="status status-' + item.status.toLowerCase() + '"></span>'+status+'</td><td><a class="btn btn-primary btn-small bde-view-only" onclick="rm.responsivetable.orderAddtoCart('+ orderNo +')"  href="javascript:void(0);">' + actionText + '</a></tr>';
       
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

'use strict';

rm.confirmdealchanges = {
		addEmailItems1:'<li><div class="checkbox"><input name ="emails" id="checkEmail',
		addEmailItems2:'" class="hidden" type="text" value="',
		addEmailItems3:'"><input name="toEmails" id="check',
		addEmailItems4:'" type="checkbox" checked><label for="check',
		addEmailItems5:'">',
		addEmailItems6:'</label></div></li>',
		emailSize:$('.list-checkbox .checkbox').size(),
		
		bindSelectEmail: function(){
			$('.list-checkbox .checkbox').each(function(index){
				var $thisCheckBox = $('#check'+index);
				
				$thisCheckBox.on('change',function(){
					if($thisCheckBox.is(':checked')){
						$('#checkEmail'+index).attr('name','emails');
					}else{
						$('#checkEmail'+index).removeAttr('name');
					}
				});
			});
		},
		
		bindAddEmail: function(){
			$('#add-email_button').on('click',function(){
				var inputEmail = $('#add-email-filed').val();
				if(!rm.customer.emailInvalid(inputEmail)){
					var selectFiled = '';
					
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems1+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems2+inputEmail;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems3+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems4+rm.confirmdealchanges.emailSize;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems5+inputEmail;
					selectFiled = selectFiled + rm.confirmdealchanges.addEmailItems6;
					
					rm.confirmdealchanges.emailSize = parseInt(rm.confirmdealchanges.emailSize) + 1;
					
					if($('.list-checkbox').html() === ''){
						$('.list-checkbox').addClass('list-checkbox-bordered');
					}					
					$('.list-checkbox').append(selectFiled);
					rm.confirmdealchanges.bindSelectEmail();
					rm.confirmdealchanges.checkSaveButton();
					$('#confirmSend_button').removeAttr('disabled');
					$('#add-email-filed').val('');
					$('#email-error').hide();
				}else{
					$('#email-error').show();
				}
			});
			
		},
		checkSaveButton: function(){
			//if the checkBox is clicked  then judge selected case
			$('input:checkbox[name="toEmails"]').click(function(){
				var flag = false;
				$('#confirmSend_button').attr('disabled','disabled');
				$('.list-checkbox .checkbox').each(function(index){
					var $thisCheckBox = $('#check'+index);
						if($thisCheckBox.is(':checked')){
							flag = true;					
					}
				});
				var $thisCheckBox = $('#checkSendToMe');
				if($thisCheckBox.is(':checked')){
					flag = true;
				}

				if(flag){
					$('#confirmSend_button').removeAttr('disabled');
				}				
			});	
	
		},
		checkSendEmailConfirmation: function(){
			$('input:checkbox[name="sendToMe"]').click(function(){
				//var flag = false;
				$('#confirmSend_button').attr('disabled','disabled');
				
				var $thisCheckBox = $('#checkSendToMe');
				if($thisCheckBox.is(':checked')){
					$('#confirmSend_button').removeAttr('disabled');
				}
				var flag = false;
				$('.list-checkbox .checkbox').each(function(index){
					var $thisCheckBox = $('#check'+index);
						if($thisCheckBox.is(':checked')){
							flag = true;					
					}
				});
				if(flag){
					$('#confirmSend_button').removeAttr('disabled');
				}
				
			});	
	
		},
			
		init: function ()
		{
			rm.confirmdealchanges.bindAddEmail();
		    rm.confirmdealchanges.bindSelectEmail();
			rm.confirmdealchanges.checkSaveButton();
			rm.confirmdealchanges.checkSendEmailConfirmation();
		},
};
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
/* globals document */
/* globals window */
/* globals localStorage */
/* globals sessionStorage */
/* globals $clamp */

'use strict';

rm.portalsso = {
    init: function() {
        if($("#ssologin").length > 0)
        {
            this.redirectToSSOLoginPage();
        }
    },

    redirectToSSOLoginPage: function ()
    {
        var loc = location;

        setTimeout(function(){
            window.location.replace(loc.origin + "/samlsinglesignon/saml/staffPortal/sabmStore/en/customer-search");
        }, 3000);
    }
};