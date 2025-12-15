ACC.autocomplete = {

	_autoload: [
		"bindSearchAutocomplete",
        "bindDisableSearch",
		"asmDuplicationFixes"
	],

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
				select: function (event, ui){
                    window.location.href = ui.item.url;
                },
                open: function( event, ui ) {
    				var autocomplete = $(".ui-autocomplete");
    				
    				 if ($(window).width() < 768){
    					 $(autocomplete).css({left:-136 + "%"}); // @SM
                         $(autocomplete).css({width:230 + "%"}); // @SM
                         $(autocomplete).css({top:51 + "px"}); // @SM
    				 }
    				 else if($(window).width() > 767 && $(window).width() < 1024){
    					 $(autocomplete).css({left:-90 + "%"}); // @SM
                         $(autocomplete).css({width:180 + "%"}); // @SM
                         $(autocomplete).css({top:51 + "px"}); // @SM
    				 }
    				 else if($(window).width() > 1023){
    					 $(autocomplete).css({left:-59 + "%"}); // @SM
                         $(autocomplete).css({width:151 + "%"}); // @SM
                         $(autocomplete).css({top:51 + "px"}); // @SM
    				 }
    			}
			},
			

			_renderItem : function (ul, item){
				
				if (item.type == "autoSuggestion"){
					var renderHtml = "<a href='"+ item.url + "' ><div class='name'>" + item.value + "</div></a>";
					return $("<li>")
							.data("item.autocomplete", item)
							.append(renderHtml)
							.appendTo(ul);
				
				}
				else if (item.type == "productResult"){

					var renderHtml = "<a href='" + item.url + "' >";

					if (item.image != null){
						renderHtml += "<div class='thumb'><img src='" + item.image + "'  /></div>";
					}
                    
                    var total_product_chars = item.value + " " + item.manufacturer;
                    var char_cuttoff = 35;
                    
                    if (total_product_chars.length < char_cuttoff) {
                        renderHtml += 	"<div class='name'><span class='title'>" + item.value + "</span>"+"<span>"+"&nbsp;"+item.manufacturer +"<br>" + item.portalUnitVolume + "</div>"; 
                        //renderHtml += 	"<div class='price'>" + item.price +"</div>";
                        renderHtml += 	"</a>";

                        return $("<li>").data("item.autocomplete", item).append(renderHtml).appendTo(ul);   
                    } else {
                        
                        var tempVal;
                        
                        if (item.value.length > char_cuttoff) {
                            
                            tempVal = item.value.substring(0, char_cuttoff) + "..";
                            
                            renderHtml += 	"<div class='name'><span class='title'>" + tempVal + "</span>"+"<br>" + item.portalUnitVolume + "</div>"; 
                            renderHtml += 	"</a>";
                            
                        } else if (item.value.length < char_cuttoff) {
                            
                            var brand_name_end_pos = (char_cuttoff - item.value.length) - 1;            
                            // Subtracting 1 to account for the space between brand and product name.
                            
                            var remaining_product_name = item.manufacturer.substring(0, brand_name_end_pos) + "..";
                            // This is the remaining product name. 
                            
                            renderHtml += 	"<div class='name'><span class='title'>" + item.value + "</span>"+"<span>"+"&nbsp;"+remaining_product_name +"<br>" + item.portalUnitVolume + "</div>"; 
                            renderHtml += 	"</a>";
                        } else {
                            renderHtml += 	"<div class='name'><span class='title'>" + item.value + "</span>"+"<br>" + item.portalUnitVolume + "</div>"; 
                            renderHtml += 	"</a>";
                        }

                        return $("<li>").data("item.autocomplete", item).append(renderHtml).appendTo(ul);
                    }
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

				$.getJSON(self.options.autocompleteUrl, {term: request.term}, function (data)
				{
					var autoSearchData = [];
					if(data.suggestions != null){
						$.each(data.suggestions, function (i, obj)
						{
							if(i<1){
							autoSearchData.push({
								value: obj.term,
								url: ACC.config.encodedContextPath + "/search?text=" + obj.term,
								type: "autoSuggestion"
							});
							}
						});
					}
					if(data.products != null){
						$.each(data.products, function (i, obj)
								{
									var unitVol = obj.unitVolume;
									var unitVolume= "";
									if(null != unitVol){
										unitVolume = unitVol.name;
									}
									autoSearchData.push({
										value: ACC.autocomplete.escapeHTML(obj.apbBrand.name),
										portalUnitVolume: unitVolume,
										desc: obj.description,
										manufacturer: obj.name,
										url:  ACC.config.encodedContextPath + obj.url,
									//	price: obj.price.formattedValue,
										type: "productResult",
										image: (obj.images!=null && self.options.displayProductImages) ? obj.images[0].url : null // prevent errors if obj.images = null
									});
								});
					}
					self.options.cache[term] = autoSearchData;
					return response(autoSearchData);
				});
			}
			
		});

	
		$search = $(".js-site-search-input");
		if($search.length>0){
			$search.yautocomplete()
		}

	},

	bindDisableSearch: function ()
    {
        $('#js-site-search-input').keyup(function(){
        	$('#js-site-search-input').val($('#js-site-search-input').val().replace(/^\s+/gm,''));
            $('.js_search_button').prop('disabled', this.value == "" ? true : false);
        })
		
		$('.js_search_button').attr('disabled','disabled');
		 $('input[type="text"]').keyup(function() {
			if($(this).val() != '') {
				$('.js_search_button').removeAttr('disabled');
			} else {
				$('.js_search_button').attr('disabled','disabled');
			}
		 });
    
    },

	escapeHTML: function (input) {
		return input.replace(/&/g,'&amp;')
				.replace(/</g,'&lt;')
				.replace(/>/g,'&gt;');
	},

	asmDuplicationFixes: function() {
		$(document).ready(function() {
			$("#asmAutoCompleteCartId").children().slice(1).remove()
			$("#asmAutoComplete").children().slice(1).remove()


			//Unbind keyup from assistedservicestorefront.js
			$("#_asmPersonifyForm input[name='customerName']").off('keyup');

			//Rebind this keyup function from assistedservicestorefront.js without the clear() call
			$("#_asmPersonifyForm input[name='customerName']").keyup(function () {
				if (isErrorDisplayed()) {
					$("input[name='customerName']").removeClass('ASM-input-error');
					if ($('.ASM_alert')) {
						$('.ASM_alert').remove();
					}
					if ($(this).val() === "") {
						$("input[name='cartId']").removeClass('ASM-input-error');
						toggleStartSessionButton($("input[name='cartId']"), true);
						$("input[name='customerId']").val("");
					}
				}
				if ($(this).val() === "") {
					$("input[name='cartId']").val("");
				}
			});

			//Rebind this keyup function with no changes
			$("#_asmPersonifyForm input[name='customerName']").keyup(function (e) {
				$("input[name='customerId']").val("");
				validateNewAccount(this);
				$(this).removeData("hover");
				removeAsmHover();
				toggleBind(false);
				toggleStartSessionButton(this, false);
		
				if ($(this).val().length < 3) {
					toggleCreateAccount(false);
				}
			});
		})
	}

};