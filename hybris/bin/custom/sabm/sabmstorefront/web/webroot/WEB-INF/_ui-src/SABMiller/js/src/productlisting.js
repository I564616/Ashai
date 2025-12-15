
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

