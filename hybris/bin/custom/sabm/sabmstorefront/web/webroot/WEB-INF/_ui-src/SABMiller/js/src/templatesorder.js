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
