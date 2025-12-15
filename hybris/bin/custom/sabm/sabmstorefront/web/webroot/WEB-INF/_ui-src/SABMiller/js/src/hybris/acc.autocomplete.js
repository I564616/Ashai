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
