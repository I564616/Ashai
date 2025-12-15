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
