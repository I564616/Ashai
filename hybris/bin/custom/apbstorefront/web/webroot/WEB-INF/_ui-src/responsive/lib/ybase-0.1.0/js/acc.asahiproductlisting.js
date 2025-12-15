ACC.asahiproductlisting = {

	infiniteScrollingConfig: {
		offset: function(){
			return $('.pageBodyContent').innerHeight();
		},
		
		context: $('.pageBodyContent')},
	currentPage: 0,
	numberOfPages: 0,
	baseQuery: $("#sortForm1 input[name='q']").val() || "",
	isAnonymousUser:true,
	plpspinner: $("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />"),
	/*It calls when user scroll position at the bottom */
	triggerLoadMoreResults: function ()
	{
		if (parseInt(ACC.asahiproductlisting.currentPage) +1 < ACC.asahiproductlisting.numberOfPages)
		{
			// show the page loader
			ACC.asahiproductlisting.loadMoreResults(parseInt(ACC.asahiproductlisting.currentPage) + 1);

		}
	},

	scrollingHandler: function (event, direction)
	{
		if (direction === "down")
		{
			ACC.asahiproductlisting.triggerLoadMoreResults();
		}
	},

	/*
	 Get 12 results at a time. 
	 When page scrolls down it loads more results on page
	 */
	loadMoreResults: function (page)
	
	{
		var isProductListPage = $("ul#resultsList").length > 0;
		var isProductGridPage = $("#resultsGrid").length > 0;
		if (isProductListPage || isProductGridPage)
		{
			$('#showPLPSpinner').removeClass('hide');
			$.ajax({
				url:window.location.pathname + "/results?q=" + ACC.asahiproductlisting.baseQuery + "&page=" + page,
				beforeSend: function(){
					 $("#showPLPSpinner").html(ACC.asahiproductlisting.plpspinner).show();
		            },
				success: function (data)
				{
				
					if (data.pagination !== undefined)
					{
						if (isProductListPage)
						{ //Product List Page
							$("ul#resultsList").append($.tmpl($("#resultsListItemsTemplate"), data)).listview('refresh');
						}
						if (isProductGridPage)
						{ //Product Grid Page
							ACC.asahiproductlisting.isAnonymousUser = data.isAnonymousUser;
							if(!(jQuery.isEmptyObject(data.results))){
								ACC.asahiproductlisting.addFullProductName(data);
							}
							$("#resultsGrid").append($.tmpl($("#resultsGridItemsTemplate"), data));
							
							$("#showPLPSpinner").hide();
							ACC.productDetail.disabledPlusMinusBtn();
							if(!(jQuery.isEmptyObject(data.results)) && !ACC.asahiproductlisting.isAnonymousUser){
								var isPriceFetch = $('[name="showPriceOnPLP"]').val();
								if(isPriceFetch == 'true'){
									ACC.asahiproductlisting.fetchPrices(data.results);		
								}
							}
						}
						
						if(data.pagination.currentPage + 1 === data.pagination.numberOfPages ){
							$('#productLoaded').removeClass('hide');
						}
						ACC.asahiproductlisting.updatePaginationInfos(data.pagination);
						$("footer").waypoint(ACC.asahiproductlisting.infiniteScrollingConfig); // reconfigure waypoint eventhandler
						
						ACC.asahiproductlisting.footerInView();
					}
				},
				error:function(xmlHttpRequest, errorText, thrownError){
					 console.error("Error: "+ thrownError +" "+ errorText);
				}
			});
		}
	},
	/*Concatenate brand name and product name
	 * and add in new property */
	addFullProductName:function(data){
		var resultData = $(data);
		var objData = resultData[0].results;
		$.each(objData, function( index, value ) {
			 var pName = value.name;
			 var bName;
			 
			 if(value.brand!== null){
				 bName = value.brand.name;
			}
			else{
				bName = '';
			}
			 var productName = '<b>'+bName+'</b>' + ' ' + pName ;
			 
			 value.productName = productName;
		});
	},
	updatePaginationInfos: function (paginationInfo)
	{
		ACC.asahiproductlisting.currentPage = parseInt(paginationInfo.currentPage);
		ACC.asahiproductlisting.numberOfPages = parseInt(paginationInfo.numberOfPages);
	},

	bindShowMoreResults: function (showMoreResultsArea)
	{
		this.showMoreResultsArea = showMoreResultsArea;

		$("footer").waypoint(ACC.asahiproductlisting.scrollingHandler, ACC.asahiproductlisting.infiniteScrollingConfig);
	},

	bindSortingSelector: function ()
	{
		$('#sortForm1, #sortForm2').change(function ()
		{
			this.submit();
		});
	},

	initialize: function ()
	{
		with (ACC.asahiproductlisting)
		{
			
			$("#resultsGrid").html('');
			loadMoreResults(ACC.asahiproductlisting.currentPage);
			bindShowMoreResults($('footer'));
			bindSortingSelector();
		}
	},
	/*This call get price of each product when loading
	 * on Basis of Product ID 
	 * @param {Array} productCodes are array of products
	 * */
	
	fetchPrices : function (productCodes)
	{
		
		var products = new Array();
		productCodes.forEach(function (item) {
			products.push(item.code);
		});
		var fetchPriceUrl = $('[name="fetchPriceUrl"]').val();
		$.ajax({
		    type: 'post',
		    contentType: 'application/json',
		    dataType: 'json',
		    url: fetchPriceUrl,
		    data: JSON.stringify({products:products}),
    		beforeSend: function(xhr){
				var csrfToken = ACC.config.CSRFToken;
		        xhr.setRequestHeader('x-csrf-token', csrfToken);
	        },
		    success: function (data)
			{
	    		$.each(data , function(key , value){ // First Level
		    		var netPrice = value.netPrice.formattedValue;
		    		$('#price_ns_'+key).html(netPrice);
		    	});
		    	$(".showPriceSpinner").html(ACC.cartitem.spinner).hide();
		    	$("footer").waypoint(ACC.asahiproductlisting.infiniteScrollingConfig);		    	
		
			},
		    error: function (xhr, errorText, thrownError) {
		    	var errorVal= $('input[name="textForNoPrice"]').val();
		    	products.forEach(function(value){ // First Level
		    		$('#price_ns_'+value).html(errorVal);
		    	});
	            $("#priceUpdateFailedErr").removeClass("hide");

	        },
		  });

	            
	},
	
	footerInView : function () {
		var isProductListPage = $("ul#resultsList").length > 0;
		var isProductGridPage = $("#resultsGrid").length > 0;
		if (isProductListPage || isProductGridPage) {
			var currentPage = $('#checkPage').val();
			if (currentPage == "searchGrid") {
				if (($(".search-grid-page-result-grid-slot").offset().top + $(".search-grid-page-result-grid-slot").height()) <= $(window).height()) {
					ACC.asahiproductlisting.triggerLoadMoreResults();
				 }
			} else {
				if (($(".product-grid-right-result-slot").offset().top + $(".product-grid-right-result-slot").height()) <= $(window).height()) {
					ACC.asahiproductlisting.triggerLoadMoreResults();
				 }
			}
		}
	}
	
};

$(document).ready(function ()
{
	ACC.asahiproductlisting.initialize();
});