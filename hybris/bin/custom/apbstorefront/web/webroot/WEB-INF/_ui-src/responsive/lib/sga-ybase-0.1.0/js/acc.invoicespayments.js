ACC.invoicespayments = {

    _autoload: [
        "bindCurrentSelectedPage",
		"bindInvoicesAndCreditsPage",
		"bindInvoicesAndCreditsFiltering",
		"bindStatementsPage",
		"bindPaymentHistoryPage",
		"bindMakePaymentPage",
		"updatePaymentHistoryRecords"
    ],

    bindCurrentSelectedPage: function () {
		var currentPage = $('#checkPage').val();
		
		if ((currentPage == "directdebit") || (currentPage == "directDebitConfirmation")) {
			$('a[title="Direct Debit"]').addClass("invoice-payments-pages-link-selected");
			$("#invoice-payments-pages").val("directdebit");
			
		} else if (currentPage == "statement") {
			$('a[title="Statements"]').addClass("invoice-payments-pages-link-selected");
			$("#invoice-payments-pages").val("statements");
			
		} else if (currentPage == "invoicedetail") {
			$('a[title="Invoices & Credit"]').addClass("invoice-payments-pages-link-selected");
			$("#invoice-payments-pages").val("invoicesCredits");
			
		} else if (currentPage == "paymentHistory") {
			$('a[title="Payment History"]').addClass("invoice-payments-pages-link-selected");
			$("#invoice-payments-pages").val("paymentHistory");
		}
		
		$("#invoice-payments-pages").change(function () {
			var newPageVal = $("#invoice-payments-pages").val();
			
			if (newPageVal === "invoicesCredits") {
				var url = ACC.config.encodedContextPath + "/invoice";
				window.location.replace(url);
				
			} else if (newPageVal === "statements") {
				var url = ACC.config.encodedContextPath + "/statement";
				window.location.replace(url);
				
			} else if (newPageVal === "directdebit") {
				var url = ACC.config.encodedContextPath + "/directdebit";
				window.location.replace(url);
				
			} else if (newPageVal === "paymentHistory") {
				var url = ACC.config.encodedContextPath + "/paymentHistory";
				window.location.replace(url);
			}
		});
        
    },
	
	bindInvoicesAndCreditsPage: function () {
        $(document).on("submit", '#submitSamIvoices', function (e) {
            var numberOfInvoices = parseInt($("#totalInvoiceCount").val());
			var totalAmountOfInvoices = 0;
			
			for (i=0; i < numberOfInvoices; i++) {
				if ($("input.js-invoice-checkbox" + i).is(":checked")) {
					
					var currentDocType = $("input.js-invoice-doctype" + i).val();
					
					if ((currentDocType === "INVOICE") || (currentDocType === "Invoice")) {
						totalAmountOfInvoices = totalAmountOfInvoices + (parseFloat($("input.js-invoice-amount" + i).val()));
						
					} else if  ((currentDocType === "CREDIT") || (currentDocType === "Credit")) {
						totalAmountOfInvoices = totalAmountOfInvoices + (parseFloat($("input.js-invoice-amount" + i).val()));
					}
					
				}
			}
			
			if (totalAmountOfInvoices < 1) {
				e.preventDefault();
				
				$('#generalErrorMsg').html(ACC.invoiceTotalMsg)
				$('#generalErrorMsg').removeClass("hide");
				$('.pageBodyContent').animate({
					scrollTop: 0
				}, 1000);
                $("html").unblock();
			} else {
				var totalCountOfInvoices = 0;

				for (i=0; i < numberOfInvoices; i++) {
					if ($("input.js-invoice-checkbox" + i).is(":checked")) {
						ACC.invoicespayments.resetInvoiceFormData(i, totalCountOfInvoices);
						totalCountOfInvoices = totalCountOfInvoices + 1;
					} else {
						$(".js-invoice" + i).prop('disabled', true);
					}
				}

				$("#totalPayableAmount").val(totalAmountOfInvoices);
				$("#totalInvoiceCount").val(totalCountOfInvoices);
			}

		});
		
		$(".closed-items-js").click(function (e) {
			$(".fetch-more-invoices-form").find("#status").val("closed");
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices($(".fetch-more-invoices-form"), null, null, "reload"); 
			$.waypoints('refresh');
		});
		
		$(".open-items-js").click(function (e) {
			$(".fetch-more-invoices-form").find("#status").val("open");
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices($(".fetch-more-invoices-form"), null, null, "reload");
			$.waypoints('refresh');
		});

        ACC.invoicespayments.bindInvoicesAndCreditsTableData();

		$('footer').waypoint(ACC.invoicespayments.scrollingHandler, {
			context: $('.pageBodyContent')
		});
	
		$(".js-invoice-select").prop("checked", false);
    },
	
	scrollingHandler: function (event, direction) {
		if (direction === "down") {
			$("#pageLoadingSpinner").html("<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />").show();
			
			if ($(".open-items-js").parent().hasClass("active")) {
				var totalInvoicesCount = $("#openCountTab").html();
			} else {
				var totalInvoicesCount = $("#closedCountTab").html();
			}

			if (totalInvoicesCount === "") {
				totalInvoicesCount = 0;
			} else {
				totalInvoicesCount = parseInt(totalInvoicesCount);
			}

			var invoicesPageSizeFromConfig = parseInt($("#numberOfInvoicePageSize").val());
			var totalNumberOfPages = Math.ceil( totalInvoicesCount / invoicesPageSizeFromConfig);

			var currentPageVal = parseInt($(".fetch-more-invoices-form").find("#page").val());

			if (currentPageVal < (totalNumberOfPages - 1)) {
				console.log("LazyLoad triggered. Current Page Val = " + currentPageVal + ". Total Number of Pages = " + totalNumberOfPages +".");
				currentPageVal++;
				$(".fetch-more-invoices-form").find("#page").val(currentPageVal);
				ACC.invoicespayments.fetchMoreInvoices($(".fetch-more-invoices-form"), null, null, "append");	
			}
			$("#pageLoadingSpinner").hide();
			
			$.waypoints('refresh');
		}
	},
	
	bindInvoicesAndCreditsTableData: function () {
        $('.tr-invoice-ref').each(function (index) {
            var docType = $(".js-invoice-doctype" + index).val();
            var docNum = $('.js-invoice-docno' + index).val();
            if(docType == 'INVOICE' || docType == 'Invoice')
                invoiceIdxMap.set(docNum, index);
            else if(docType == 'CREDIT' || docType == 'Credit') {
                var invoiceRef = $('#js-invoice-debitInvcRefNo' + index).val();
                if(invoiceRef != '')
                    creditInvoiceMap.set(docNum + index, invoiceRef);
            }
        });


        $(".js-invoice-select").on('change', function () {
//            if ($(this).is(':checked')) {
//                var index = $(this).attr('id');
//                var docType = $("input.js-invoice-doctype" + index).val();
//                var docNum = $('.js-invoice-docno' + index).val();
//                if (docType === 'CREDIT' || docType === 'Credit') {
//                    docNum += index;
//                    if (creditInvoiceMap.has(docNum)) {
//                        var invoiceDoc = creditInvoiceMap.get(docNum);
//                        if (invoiceIdxMap.has(invoiceDoc)) {
//                            $('.js-invoice-checkbox' + invoiceIdxMap.get(invoiceDoc)).prop('checked', true);
//                            if (!$('#closeDebitInvRefPopup').is(':checked')) {
//                                $('#debitInvRefPopup').modal('show');
//                                $('#debitInvRefPopup').removeClass('cboxElement');
//                            }
//                        } else {
//                            $(this).prop('checked', false);
//                            var msg = ACC.invoiceDocSelectionErrMsgPrefix + " " + "<strong>" + invoiceDoc + "</strong>" + " " + ACC.invoiceDocSelectionErrMsgSuffix;
//                            ACC.invoicespayments.displayErrorMsg(msg);
//                        }
//                    } else {
//                        $(this).prop('checked', false);
//                        ACC.invoicespayments.displayErrorMsg(ACC.referencedDocNotExistsErrMsg);
//                    }
//                }
//            }
            togglePayButton($(this).is(':checked'));
        });

        $('.closeDebitInvoicePopupBtn').click(function () {
            if($('#closeDebitInvRefPopup').is(':checked')) {
                $.ajax({
                    url: $('#disablePopupForUserUrl').val(),
                    type: 'POST',
                    success: function (callback) {
                        console.log(callback);
                    },
                    error: function (xhr, error) {
                        console.log(error);
                    }
                });
            }
        });


        //select all event
        var allChecked = false;
        $(".js-select-all-invoices").click(function () {
            var noOfInvoices = 0;
            if (!allChecked) {
                $(".js-invoice-select").each(function (index) {
                    var docType = $('.js-invoice-doctype' + index).val();
                    var docNum = $('.js-invoice-docno' + index).val();
                    if(docType == 'CREDIT' || docType == 'Credit') {
                        docNum += index;
                        if(creditInvoiceMap.has(docNum) && invoiceIdxMap.has(creditInvoiceMap.get(docNum))) {
                            noOfInvoices++;
                            var invoiceDoc = creditInvoiceMap.get(docNum);
                            $(this).prop('checked', true);
                            $('.js-invoice-checkbox' + invoiceIdxMap.get(invoiceDoc)).prop('checked', true);
                        }
                    }
                    else {
                        noOfInvoices++;
                        $(this).prop('checked', true);
                    }
                });
                $(".paySelctedJs").removeAttr("disabled");
                $("#numberOfInvoices").val(noOfInvoices);
                allChecked = true;
            }
            else {
                $(".js-invoice-select").each(function () {
                    $(this).prop('checked', false);
                });
                $(".paySelctedJs").attr("disabled", "disabled");
                $("#numberOfInvoices").val(0);
                allChecked = false;
            }
        });
        //select all event

        function togglePayButton(isSelected) {
			var currentNumOfInvoices = parseInt($("#numberOfInvoices").val());
			if (isSelected) {
				var totalNumOfInvoices = currentNumOfInvoices + 1;
			} else {
				if (currentNumOfInvoices == 0) {
					var totalNumOfInvoices = currentNumOfInvoices;
				} else {
					var totalNumOfInvoices = currentNumOfInvoices - 1;
				}
			}

			$("#numberOfInvoices").val(totalNumOfInvoices);
			if (totalNumOfInvoices == 0) {
				$(".paySelctedJs").attr("disabled", "disabled");
			} else {
				$(".paySelctedJs").removeAttr("disabled");
			}
		}

    },
	
	bindInvoicesAndCreditsFiltering: function () {
		
		var currentFormToSumbit = $(".fetch-more-invoices-form");
		
		$(".credit-filter-js").click(function (e) {
			e.preventDefault();
			currentFormToSumbit.find("#documentType").val("credit");
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices(currentFormToSumbit, "Credit", "documentType", "reload");
		});
		
		$("#documentType-js").click(function (e) {
			e.preventDefault();
			
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($(this).hasClass("active")) {
					$(this).removeClass("active");
					$(".facet-docval-js").hide();
				} else {
					$(this).addClass("active");
					$(".facet-docval-js").show();
				}
			}
		});
		
		$("#dueStatus-js").click(function (e) {
			e.preventDefault();
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($(this).hasClass("active")) {
					$(this).removeClass("active");
					$(".facet-dueval-js").hide();
				} else {
					$(this).addClass("active");
					$(".facet-dueval-js").show();
				}
			}
		});
		
		$(".applied-filters-js").click(function (e) {
			e.preventDefault();
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($(this).hasClass("active")) {
					$(this).removeClass("active");
					$(".facet-applied-js").hide();
				} else {
					$(this).addClass("active");
					$(".facet-applied-js").show();
				}
			}
		});
		
		$(".invoice-filter-js").click(function (e) {
			e.preventDefault();
			currentFormToSumbit.find("#documentType").val("invoice");
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices(currentFormToSumbit, "Invoice", "documentType", "reload");
		});
		
		$(".due-now-js").click(function (e) {
			e.preventDefault();
			currentFormToSumbit.find("#dueStatus").val("dueNow");
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices(currentFormToSumbit, "Due Now", "dueStatus", "reload");
		});
		
		$(".not-yet-due-filter-js").click(function (e) {
			e.preventDefault();
			currentFormToSumbit.find("#dueStatus").val("notYetDue");
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices(currentFormToSumbit, "Not Yet Due", "dueStatus", "reload");
		});
		
		$(".js-keyword-filter-go").click(function (e) {
			
			var currentPage = $('#checkPage').val();
			if (currentPage != "paymentHistory") {
				e.preventDefault();
				var currentKeywordEntered = $("#keywords").val();
				currentFormToSumbit.find("#keyword").val(currentKeywordEntered);
				$("#keywords").val("");
				
				$(".fetch-more-invoices-form").find("#page").val(0);
				ACC.invoicespayments.fetchMoreInvoices(currentFormToSumbit, "Keyword: " + currentKeywordEntered, "keyword", "reload");
			}
		});
         
		$(".facet-list-js").on('click', '.remove-filter-js', function (e) {
			e.preventDefault();
			
			var clickedFilterType = $(this).attr("id");
			$(".fetch-more-invoices-form").find("#" + clickedFilterType).val("");
			
			if (clickedFilterType === "keyword") {
				$("#keywords.js-keyword").val("");
			}
			
			currentNumOfFilters = parseInt($("#numberOfAppliedFilters").val());
			$(this).parent().remove();
			
			$("#numberOfAppliedFilters").val((currentNumOfFilters - 1));
			$(".number-of-filters-js").html((currentNumOfFilters - 1));
			
			if ((currentNumOfFilters - 1) == 0) {
				$("#applied-filters").hide();
				$("#filterApplied").hide();
			}
			
			$("#" + clickedFilterType + "-js").show();
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices($(".fetch-more-invoices-form"), null, null, "reload");
		});
         
		$("#applied-filters").on('click', '.clear-all-js', function (e) {
			e.preventDefault();
			
			$(".fetch-more-invoices-form").find("#dueStatus").val("");
			$(".fetch-more-invoices-form").find("#documentType").val("");
			$(".fetch-more-invoices-form").find("#keyword").val("");
			$("#keywords.js-keyword").val("");
			$("#documentType-js").show();
			$("#dueStatus-js").show();
			$("#keyword-js").show();
			
			$("#numberOfAppliedFilters").val(0);
			$(".number-of-filters-js").html(0);
			$(".filter-js").remove();
			$("#applied-filters").hide();
			$("#filterApplied").hide();
			
			$(".fetch-more-invoices-form").find("#page").val(0);
			ACC.invoicespayments.fetchMoreInvoices($(".fetch-more-invoices-form"), null, null, "reload");
		});
		$('html').unblock();
    },
	
	fetchMoreInvoices: function (currentFormToSumbit, currentFilerApplied, currentFilterType, updateType) {
		
		$.blockUI({
			message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
			overlayCSS: {
				opacity: 0.01
			},
			css: {
				backgroundColor: 'transparent',
				color: 'transparent',
				border: 'none',
			}
		});
		
		$.ajax({
			type: currentFormToSumbit.attr('method'),
			url: currentFormToSumbit.attr('action'),
			data: currentFormToSumbit.serialize(),
			async: false,
			success: function (data) {
				$.unblockUI();
				console.log(data);
				
				currentNumOfFilters = parseInt($("#numberOfAppliedFilters").val());
				
				if ((currentFilerApplied != null) && (currentFilterType != null)) {
					$("#" + currentFilterType + "-js").hide();
					$("#numberOfAppliedFilters").val((currentNumOfFilters + 1));
					$(".facet-list-js").append("<li class=\"filter-js "+ currentFilterType + "\"><a id=\"" + currentFilterType +"\" class=\"remove-filter-js\" href=\"\"><span class=\"glyphicon glyphicon-remove\"></span></a>&nbsp;" + currentFilerApplied + "</li>");
					$("#applied-filters").show();
					
					$(".number-of-filters-js").html((currentNumOfFilters + 1));
					if ((currentNumOfFilters + 1) > 0) {
						$("#filterApplied").show();
					} else {
						$("#filterApplied").hide();
					}
				}
				
				ACC.invoicespayments.updateInvoicesRecords(data, currentFilterType, updateType);
				var windowSize = $(window).width();
				if (windowSize < 1280) {
					$.colorbox.close();
				}
			},
			error: function (xmlHttpRequest, errorText, thrownError) {
				$.unblockUI();
				console.log("Load More Invoices AJAX Call Error: "+ thrownError+" "+errorText);
				$('#multiAccountErrorMsg').removeClass("hide");
				$('.pageBodyContent').animate({
						scrollTop: 0
					}, 1000);
			},
			timeout: 10000,
		});
        
    },
	
	updateInvoicesRecords: function (JSONObj, currentFilterType, updateType) {
		if (JSONObj != null) {
			
			if (updateType === "reload") {
				var currentOpenCount = JSONObj.response.openCount;
				var currentClosedCount = JSONObj.response.closedCount;
				var currentCreditCount = JSONObj.response.creditCount;
				var currentInvoiceCount = JSONObj.response.invoiceCount;
				var currentDueNowCount = JSONObj.response.dueNowCount;
				var currentNotYetDueNowCount = JSONObj.response.notYetDueCount;

				$("#openCountTab").html(currentOpenCount);
				$("#closedCountTab").html(currentClosedCount);
				$("#invoiceCount").html(currentInvoiceCount);
				$("#creditCount").html(currentCreditCount);
				$("#dueNow").html(currentDueNowCount);
				$("#notYetDue").html(currentNotYetDueNowCount);
			}
				
			if (currentCreditCount === "0") {
				$(".credit-filter-js").hide();
			} else {
				$(".credit-filter-js").show();
			}
			
			if (currentInvoiceCount === "0") {
				$(".invoice-filter-js").hide();
			} else {
				$(".invoice-filter-js").show();
			}
			
			if (currentDueNowCount === "0") {
				$(".due-now-js").hide();
			} else {
				$(".due-now-js").show();
			}
			
			if (currentNotYetDueNowCount === "0") {
				$(".not-yet-due-filter-js").hide();
			} else {
				$(".not-yet-due-filter-js").show();
			}
			
			if ((currentInvoiceCount === "0") && (currentCreditCount === "0")) {
				$("#documentType-js").hide();
			} else {
				if ($(".filter-js.documentType").length < 1) {
					$("#documentType-js").show();
				}
			}
			
			if ((currentDueNowCount === "0") && (currentNotYetDueNowCount === "0")) {
				$("#dueStatus-js").hide();
			} else {
				if ($(".filter-js.dueStatus").length < 1) {
					$("#dueStatus-js").show();
				}
			}
			
			if (JSONObj.response.invoices.length == 0) {
				
				var numberOfRowOnPage = JSONObj.response.invoices.length;
				if (numberOfRowOnPage < 10) {
					var numberOfClearLoops = 10 - numberOfRowOnPage;
					for (i = numberOfRowOnPage; i < 10; i++) { 
						$(".js-open-invoice" + i).addClass("hide");
					}
				}
				
				$(".paySelectedSectionJS").hide();
				
				if ($(".no-invoices").length == 0) {
					$(".open-tabbody").append("<div class=\"no-invoices\">" + ACC.noInvoicesReturnedMsg + "</div>");
				}
				
				if ($(".no-invoices-closed").length == 0) {
					$(".closed-tabbody").append("<div class=\"no-invoices-closed\">" + ACC.noInvoicesReturnedMsg + "</div>");
				}
			} else {
				$(".paySelectedSectionJS").show();
				
				if ($(".no-invoices").length > 0) {
					$(".no-invoices").remove();
				}
				
				if ($(".no-invoices-closed").length > 0) {
					$(".no-invoices-closed").remove();
				}
				
				// Clear Invocies Table
				
				if (updateType === "reload") {
					$("#openInvoicesTableBody").html("");
					$("#closedInvoicesTableBody").html("");
					$("#currentNumberOfInvoicesShown").val(0);
				}
				
				for (x in JSONObj.response.invoices) {
					
					var invoiceNumberForForm = $("#currentNumberOfInvoicesShown").val();
					
					// Full Column Data
					var dataToAddInTheRow = "";
					
					// Doc Number Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTabledocNoMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Doc Number
					currentDocumentNumber = JSONObj.response.invoices[x].documentNumber;
					currentDocumentLineNumber = JSONObj.response.invoices[x].lineNumber;
					currentDownloadURL = ACC.config.encodedContextPath + "/invoice/download?documentNumber=" +currentDocumentNumber+ "&amp;lineNumber=" + currentDocumentLineNumber;
					
					var currentDocumentType = JSONObj.response.invoices[x].documentType;
					var enableDownloadLink = JSONObj.response.invoices[x].enableDownloadLink;

					for(var i in disableLinkPrefixes){
					    if(currentDocumentNumber.startsWith(disableLinkPrefixes[i])){
					        enableDownloadLink = "false";
					        break;
                        }
                    }

					if ((enableDownloadLink === "true") && (currentDocumentType !== "Payment") && (currentDocumentType !== "PAYMENT")) {
						// Not Payment
						var currentInvoiceTableColumn = "<td headers=\"header1\" id=\"header1\" class=\"responsive-table-cell responsive-table-cell-bold\"><a id=\"documentNumberJS\" href=" + currentDownloadURL + ">" + currentDocumentNumber + "</a><input type=\"hidden\" id=\"documentNumber\" class=\"js-invoice-docno" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + " name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].docNumber\" value=\"" + currentDocumentNumber + "\" /><input type=\"hidden\" id=\"lineNumber\" class=\"js-invoice-lineno" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + " name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].lineNumber\" value=\"" + currentDocumentLineNumber + "\" /></td>";
						dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					} else {
						// Payment
						var currentInvoiceTableColumn = "<td headers=\"header1\" class=\"responsive-table-cell responsive-table-cell-bold\">" + currentDocumentNumber + "<input type=\"hidden\" id=\"documentNumber\" class=\"js-invoice-docno" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + " name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].docNumber\" value=\"" + currentDocumentNumber + "\" /><input type=\"hidden\" id=\"lineNumber\" class=\"js-invoice-lineno" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + " name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].lineNumber\" value=\"" + currentDocumentLineNumber + "\" /></td>";
						dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					}
					
					// Del Number Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTabledelNoMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Del Number
					currentDeliveryNumber = JSONObj.response.invoices[x].deliveryNumber;
					var currentInvoiceTableColumn = "<td id=\"deliveryNumberJS\" headers=\"header2\" class=\"responsive-table-cell\">" + currentDeliveryNumber + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Sold To Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTablesoldToMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Sold to
					currentSoldTo = JSONObj.response.invoices[x].soldToAccount;
					var currentInvoiceTableColumn = "<td id=\"soldToJS\" headers=\"header3\" class=\"responsive-table-cell sold-to-account-name-td\">" + currentSoldTo + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Due Date Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTabledueDateMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Due Date
					if ($(".open-items-js").parent().hasClass("active")) {
						currentInvoiceDueDate = JSONObj.response.invoices[x].invoiceDueDate;
					} else {
						currentInvoiceDueDate = JSONObj.response.invoices[x].invoiceDate;
					}
					var currentInvoiceTableColumn = "<td id=\"invoiceDueDateJS\" headers=\"header4\" class=\"responsive-table-cell\"><span id=\"dueDateJS\">" + currentInvoiceDueDate + "</span>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Due Now && Doc Type
					
					if ($(".open-items-js").parent().hasClass("active")) {
						currentInvoicePaymentMade = JSONObj.response.invoices[x].paymentMade;
						currentInvoiceOverDue = JSONObj.response.invoices[x].overdue;
						
						if (currentInvoicePaymentMade === "true") {
							// Payment Made
							var currentInvoiceTableColumn = "<div id=\"pendingJS\">" + ACC.invoiceTablePaymentPending + "</div></td>";
							dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
						} else if ((currentInvoiceOverDue === "true") && (currentDocumentType !== "Payment") && (currentDocumentType !== "PAYMENT") 
								&& (currentDocumentType !== "Credit") && (currentDocumentType !== "CREDIT")) {
							// Due Now
							var currentInvoiceTableColumn = "<div id=\"overDueJS\">" + ACC.invoiceTableoverDue + "</div></td>";
							dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
						} else {
							var currentInvoiceTableColumn = "</td>";
							dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
						}	
					}
                                                                        
                    // Doc Type Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTabledocTypeMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Doc Type
					var currentInvoiceTableColumn = "<td id=\"docTypeJS\" headers=\"header5\" class=\"responsive-table-cell\">" + currentDocumentType + "<input type=\"hidden\" id=\"documentType\" class=\"js-invoice-doctype" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + "\" name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].documentType\" value=\"" + currentDocumentType + "\" /></td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;   
					
					// Remaining Mobile
					var currentInvoiceTableColumn = "<td class=\"hidden-sm hidden-md hidden-lg\">" + ACC.invoiceTableremainingMobile + "</td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;
					
					// Remaining Type
					if ((currentDocumentType === "Payment") || (currentDocumentType === "PAYMENT")) {
						currentRemainAmount = "&ndash;" + JSONObj.response.invoices[x].remainingAmount;
					} else {
						currentRemainAmount = JSONObj.response.invoices[x].remainingAmount;
					}
					
					if (!currentRemainAmount.includes(".")) {
						currentRemainAmount = currentRemainAmount + ".00";
					}
					
					var currentInvoiceTableColumn = "<td id=\"remainingAmountJS\" headers=\"header6\" class=\"responsive-table-cell invoices-rem-amount invoices-rem-amount-mobile\" id=\"header6\">" + currentRemainAmount + "<input type=\"hidden\" id=\"remainingAmount\" class=\"js-invoice-amount" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + "\" name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + "].remainingAmount\" value=\"" + currentRemainAmount + "\" /><input type=\"hidden\" id=\"paidAmount\" class=\"js-invoice-payamount" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + "\" name=\"asahiSamInvoiceForm[" + invoiceNumberForForm + ".paidAmount\" value=\"" + currentRemainAmount + "\" /></td>";
					dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;   
					
					// Checkbox
                    var enableCheckBox = true;
                    for(var i in disableCheckBoxPrefixes){
                        if(currentDocumentNumber.startsWith(disableCheckBoxPrefixes[i])){
                            enableCheckBox = false;
                        }
                    }
					if ($(".open-items-js").parent().hasClass("active")) {
						currentInvoicePaymentMade = JSONObj.response.invoices[x].paymentMade;
						
						if (enableCheckBox && currentInvoicePaymentMade === "false" && currentDocumentType !== "Payment" && currentDocumentType !== "PAYMENT") {
							// Payment Not Made
							var currentInvoiceTableColumn = "<td headers=\"header7\" class=\"responsive-table-cell checkbox-mobile-fix\"><input id=\"" + invoiceNumberForForm + "\" class=\"js-invoice-select js-invoice-checkbox" + invoiceNumberForForm + " js-invoice" + invoiceNumberForForm + "\" type=\"checkbox\" value=\"true\"></td>";
							dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn;  
						} else {
							// Payment Made
							var currentInvoiceTableColumn = "<td headers=\"header7\" class=\"responsive-table-cell checkbox-mobile-fix\"></td>";
							dataToAddInTheRow = dataToAddInTheRow + currentInvoiceTableColumn; 
						}
					}
					
					var currentInvoicesTableRow = "<tr class=\"tr-invoice-ref responsive-table-item js-open-invoice" + invoiceNumberForForm +"\">"
					currentInvoicesTableRow = currentInvoicesTableRow + dataToAddInTheRow + "</tr>";
					
					if ($(".open-items-js").parent().hasClass("active")) {
						$("#openInvoicesTableBody").append(currentInvoicesTableRow);
						
					} else {
						$("#closedInvoicesTableBody").append(currentInvoicesTableRow);
					}
					
					$("#currentNumberOfInvoicesShown").val((parseInt(invoiceNumberForForm) + 1));
					var totalInvoiceCountAtTop = parseInt($("#totalInvoiceCount").val());
					$("#totalInvoiceCount").val((totalInvoiceCountAtTop + 1));

				}
				
				ACC.invoicespayments.bindInvoicesAndCreditsTableData();
			}
			
		}
    },
	
	bindMakePaymentPage: function () {
		
		$('#paymentRefField').on('keypress', function (event) {
			var regex = new RegExp("^[a-zA-Z0-9\\s]+$");
			var key = String.fromCharCode(!event.charCode ? event.which : event.charCode);
			if (!regex.test(key)) {
			   event.preventDefault();
			   return false;
			}
		});
		
		$('#paymentRefField').bind("paste", function (e) {
			e.preventDefault();
		}); 
		
		$( ".js-amount-field" ).change(function() {
			ACC.invoicespayments.enableUpdateLink($(this));
		});
		
		$( ".js-amount-field" ).keydown(function() {
			ACC.invoicespayments.enableUpdateLink($(this));
		});
		
		$( ".js-amount-field" ).keyup(function() {
			ACC.invoicespayments.enableUpdateLink($(this));
		});
		
		$( ".js-amount-field" ).blur(function() {
			ACC.invoicespayments.enableUpdateLink($(this));
		});
		
		$( "#partial-payment-droplist" ).change(function() {
	
			if ($(this).hasClass("input-field-error")) {
				$(this).removeClass("input-field-error");
				$("#reqField").addClass("hidden");
				$('#generalErrorMsg').addClass("hide");
			}

			var currentSelectedVal = $(this).val();

			if (currentSelectedVal == "default") {
				$("#partialPaymentReason").val("");
			} else {
				$("#partialPaymentReason").val(currentSelectedVal);
			}
            
            if (currentSelectedVal != null) {
                $(this).addClass("selected-droplist")
            }
			
		});
		
		$(document).on("submit", '#asahiSamPaymentForm', function (e) {
			
			if (!($("#partialPaymentSection").hasClass("hide"))) {
				if ($("#partial-payment-droplist").val() == null) {
					e.preventDefault();
					$("#partial-payment-droplist").addClass("input-field-error");
					$("#reqField").removeClass("hidden");
					$('#generalErrorMsg').html(ACC.formValidationMsg)
					$('#generalErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
						}, 1000);
					$.unblockUI();
				}
			}
		});
		
		$(".js-amount-update").click(function (e) {
			e.preventDefault();
			
			var currentRowClicked = $(this).attr("id");
			var currentDocType = $("#paidAmount" + currentRowClicked).attr("docType");
			var enteredAmount = parseFloat($("#paidAmount" + currentRowClicked).val());
			var originalAmount = parseFloat($("#paidAmount" + currentRowClicked).attr("remainingAmount"));
			var ultimateOriginalVal = parseFloat($("#paidAmount" + currentRowClicked).attr("originalAmount"));
			
			if ($("#paidAmount" + currentRowClicked).hasClass("input-field-error")) {
				$("#paidAmount" + currentRowClicked).removeClass("input-field-error");
			}
			
			if (!($("#generalErrorMsg").hasClass("hide"))) {
				$("#generalErrorMsg").addClass("hide");
			}
			
			if ((currentDocType === "INVOICE") || (currentDocType === "Invoice")) {
				
				if ((enteredAmount < 1) || (enteredAmount > ultimateOriginalVal)) {
					$("#paidAmount" + currentRowClicked).val(originalAmount.toFixed(2));
					$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
					
					$('#generalErrorMsg').html(ACC.invoiceAmtValidationMsg);
					$('#generalErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
					}, 1000);
				} else {
					if (!(ACC.invoicespayments.updateTotalDueAmount())) {
						$("#paidAmount" + currentRowClicked).val(originalAmount.toFixed(2));
						$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
						
					} else {
						$("#paidAmount" + currentRowClicked).val(enteredAmount.toFixed(2));
						$("#paidAmount" + currentRowClicked).attr("remainingamount", enteredAmount);
						$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
						
						var addNewCardSelected = $(".checkout-new-card-radio").is(':checked');
						if (((parseInt($("#numberOfCards").val())) > 0) || (addNewCardSelected)) {
							var currentCardType = $("input[name=asahiCreditCardType]").val();
							ACC.checkout.updateSurcharge(currentCardType, "CARD");
						}
					}
				}
				
			} else if ((currentDocType === "CREDIT") || (currentDocType === "Credit")) {
				
				if ((enteredAmount > -1) || (enteredAmount < ultimateOriginalVal)) {
					$("#paidAmount" + currentRowClicked).val(originalAmount.toFixed(2));
					$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
					
					$('#generalErrorMsg').html(ACC.creditAmtValidationMsg);
					$('#generalErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
					}, 1000);
				} else {
					if (!(ACC.invoicespayments.updateTotalDueAmount())) {
						$("#paidAmount" + currentRowClicked).val(originalAmount.toFixed(2));
						$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
					
						$('#generalErrorMsg').html(ACC.creditAmtValidationMsg);
						$('#generalErrorMsg').removeClass("hide");
						$('.pageBodyContent').animate({
								scrollTop: 0
						}, 1000);
					} else {
						$("#paidAmount" + currentRowClicked).val(enteredAmount.toFixed(2));
						$("#paidAmount" + currentRowClicked).attr("remainingamount", enteredAmount);
						$("#" + currentRowClicked + ".js-amount-update").addClass("disabled");
						
						var addNewCardSelected = $(".checkout-new-card-radio").is(':checked');
						if (((parseInt($("#numberOfCards").val())) > 0) || (addNewCardSelected)) {
							var currentCardType = $("input[name=asahiCreditCardType]").val();
							ACC.checkout.updateSurcharge(currentCardType, "CARD");
						}
					}
				}
				
			} else {
				$("#paidAmount" + currentRowClicked).val(originalAmount);
				$("#paidAmount" + currentRowClicked).addClass("input-field-error");
				
				$('#generalErrorMsg').html(ACC.invoiceCreditOnlyMsg)
				$('#generalErrorMsg').removeClass("hide");
				$('.pageBodyContent').animate({
						scrollTop: 0
					}, 1000);
			}
		});
		
		
    },
	
	bindStatementsPage: function () {
		
		$(".js-statements-current").click(function (e) {
			$("#js-statements-current").removeClass("hidden");
			$("#js-statements-last").addClass("hidden");
			$("#js-statements-previous").addClass("hidden");
		});
		
		$(".js-statements-last").click(function (e) {
			$("#js-statements-current").addClass("hidden");
			$("#js-statements-last").removeClass("hidden");
			$("#js-statements-previous").addClass("hidden");
		});
		
		$(".js-statements-previous").click(function (e) {
			$("#js-statements-current").addClass("hidden");
			$("#js-statements-last").addClass("hidden");
			$("#js-statements-previous").removeClass("hidden");
		});
    },
	
	bindPaymentHistoryPage: function () {
		
		$(".view-more-link").click(function () {
			var currentRowNum = (this).id;
			
			if ( $("#viewMoreTableHeader"+ currentRowNum).is(":hidden") ) {
				$( "#viewMoreTableHeader"+ currentRowNum).slideDown(500);
				$( "#viewMoreTable"+ currentRowNum ).slideDown(500);
				$(this).html("View less");
				$(this).parent().parent().parent().css("border-bottom-width", "0px");
			} else {
				$( "#viewMoreTableHeader"+ currentRowNum).slideUp(500);
				$( "#viewMoreTable"+ currentRowNum ).hide();
				$(this).html("View more");
				$(this).parent().parent().parent().css("border-bottom-width", "1px");
			}
		});
		
		$(".pagination-prev-js").click(function () {
			
			if (!($(this).parent().hasClass("disabled"))) {
				var currentPageId = $(".pagination li.active").find(".page-number").attr("id");
				var nextPageId = parseInt(currentPageId);
				$(".payment-history-form").find("#pageNo").val((nextPageId - 2));
				var futurePage = $("#" + (nextPageId - 1)+ ".page-number");
				ACC.invoicespayments.fetchPaymentHistoryRecords(futurePage, true, false);
			}
		});
		
		$(".pagination-next-js").click(function () {
			if (!($(this).parent().hasClass("disabled"))) {
				var currentPageId = $(".pagination li.active").find(".page-number").attr("id");
				var nextPageId = parseInt(currentPageId);
				$(".payment-history-form").find("#pageNo").val(nextPageId);
				var futurePage = $("#" + (nextPageId + 1) + ".page-number");
				ACC.invoicespayments.fetchPaymentHistoryRecords(futurePage, true, false);
			}
		});
		
		$(".page-number").click(function (e) {
			e.preventDefault();
			var currentLink = $(this);
			var nextPageNumberHistory = parseInt($(this).attr("id")) - 1;
			$(".payment-history-form").find("#pageNo").val(nextPageNumberHistory);
			ACC.invoicespayments.fetchPaymentHistoryRecords(currentLink, true, false);
		});
		
		$("#dates-mobile-js").click(function (e) {
			e.preventDefault();
			
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($("#dates-js").hasClass("active")) {
					$("#dates-js").removeClass("active");
					$(".dates-refine-mobile-js").hide();
				} else {
					$("#dates-js").addClass("active");
					$(".dates-refine-mobile-js").show();
				}
			}
		});
		
		$("#keywords-mobile-js").click(function (e) {
			e.preventDefault();
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($("#keywords-js").hasClass("active")) {
					$("#keywords-js").removeClass("active");
					$(".keywords-refine-mobile-js").hide();
				} else {
					$("#keywords-js").addClass("active");
					$(".keywords-refine-mobile-js").show();
				}
			}
		});
		
		$("#applied-filters-mobile-js").click(function (e) {
			e.preventDefault();
			var windowSize = $(window).width();
        	if (windowSize < 1280) {
				if ($("#applied-filters").hasClass("active")) {
					$("#applied-filters").removeClass("active");
					$(".facet-applied-js").hide();
				} else {
					$("#applied-filters").addClass("active");
					$(".facet-applied-js").show();
				}
			}
		});
		
		$(document).on("submit", '.payment-history-form', function (e) {
			e.preventDefault();
			var currentLink = $(".js-keyword-filter-go");
			ACC.invoicespayments.resetNonSelectedFilter("keywords");
			ACC.invoicespayments.fetchPaymentHistoryRecords(currentLink, true, true);
        });
		
		$(".js-date-filter-go-btn").click(function (e) {
			e.preventDefault();
			var currentLink = $(".js-date-filter-go-btn");
			ACC.invoicespayments.resetNonSelectedFilter("dates");
			ACC.invoicespayments.fetchPaymentHistoryRecords(currentLink, true, true);
		});
		
		$('.js-from-date').keyup(function() {
			var toDateBtn = $('.js-to-date');
			if(($(this).val() != '') && (toDateBtn.val() != '')) {
				$('.js-date-filter-go-btn').removeAttr('disabled');
			} else {
				$('.js-date-filter-go-btn').attr('disabled','disabled');
			}
		 });
		
		$('.js-to-date').keyup(function() {
			var fromDateBtn = $('.js-from-date');
			if(($(this).val() != '') && (fromDateBtn.val() != '')) {
				$('.js-date-filter-go-btn').removeAttr('disabled');
			} else {
				$('.js-date-filter-go-btn').attr('disabled','disabled');
			}
		 });
		
		$('.js-keyword').keyup(function() {
			if(($(this).val() != '')) {
				$('.js-keyword-filter-go').removeAttr('disabled');
			} else {
				$('.js-keyword-filter-go').attr('disabled','disabled');
			}
		 });
		
		$(".facet-list-js").on('click', '.remove-payment-filter-js', function (e) {
			e.preventDefault();
			
			var numOfAppliedFilters = parseInt($("#numberOfAppliedHistoryFilters").val());
			$("#numberOfAppliedHistoryFilters").val((numOfAppliedFilters - 1));
			$(".number-of-payment-filters-js").html((numOfAppliedFilters - 1));
			
			
			if ((numOfAppliedFilters - 1) == 0) {
				ACC.invoicespayments.resetPaymentHistoryFilters();
				$("#filterApplied").hide();
			} else {
				
				var filterToRemove = $(this).attr("id");
				$("." + filterToRemove + ".filter-js").remove();
			
				if (filterToRemove === "keyword") {
					
					$(".payment-history-form").find("#keyword").val("");
					$(".js-keyword-filter-go").attr("disabled", "disabled");
					$("#keywords-js").show();
					
				} else if (filterToRemove === "dates") {
					
					$(".payment-history-form").find("#fromDateVal").val("");
					$(".payment-history-form").find("#toDateVal").val("");

					$("#fromDate").val("");
					$("#toDate").val("");
					$(".js-date-filter-go-btn").attr("disabled", "disabled");
					$("#dates-js").show();
				}
				$("#filterApplied").show();
			}
			
			ACC.invoicespayments.fetchPaymentHistoryRecords($(".js-keyword-filter-go"), true, false);
		});
		
		$("#applied-filters").on('click', '.payment-clear-all-js', function (e) {
			e.preventDefault();
			$(".js-date-filter-go-btn").attr("disabled", "disabled");
			$(".js-keyword-filter-go").attr("disabled", "disabled");
			ACC.invoicespayments.resetPaymentHistoryFilters();
			ACC.invoicespayments.fetchPaymentHistoryRecords($(".js-keyword-filter-go"), true, false);
		});
    },
	
	fetchPaymentHistoryRecords: function (currentLink, needToResetPagination, filterAppliedThisTime) {
			
		$.blockUI({
			message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
			overlayCSS: {
				opacity: 0.01
			},
			css: {
				backgroundColor: 'transparent',
				color: 'transparent',
				border: 'none',
			}
		});
		
		if (currentLink.hasClass("js-date-filter-go-btn")) {
			var currentFromDate = $("#fromDate").val();
			var formatedFromDate = ACC.invoicespayments.fixDateFormat(currentFromDate);
			$("#fromDateVal").val(formatedFromDate);

			var currentToDate = $("#toDate").val();
			var formatedToDate = ACC.invoicespayments.fixDateFormat(currentToDate);
			$("#toDateVal").val(formatedToDate);
		}

		var currentFormToSumbit = $(".payment-history-form");
		var needToFetchRecords = true;
		
		if (needToResetPagination) {
			var currentListItem = currentLink.parent();
			needToFetchRecords = !(currentListItem.hasClass("active"));
		}
		
		if (needToFetchRecords) {
			$.ajax({
				type: currentFormToSumbit.attr('method'),
				url: currentFormToSumbit.attr('action'),
				data: currentFormToSumbit.serialize(),
				success: function (data) {

					$.unblockUI();
					
					if (needToResetPagination) {
						var pageSizeHistory = parseInt($(".payment-history-form").find("#paymentHistoryPageSize").val());
						var newTotalNumberOfPages = Math.ceil(data.response.totalRecordsCount / pageSizeHistory);
						ACC.invoicespayments.resetPagination(currentLink, currentListItem, currentFormToSumbit, newTotalNumberOfPages);
					}
					
					if (filterAppliedThisTime) {
						if (currentLink.hasClass("js-keyword-filter-go")) {
							var filterApplied = "keyword";
						} else if (currentLink.hasClass("js-date-filter-go-btn")) {
							var filterApplied = "dates";
						} else {
							var filterApplied = "none";
						}

						var currentFiltersVal = parseInt($("#numberOfAppliedHistoryFilters").val());

						if (filterApplied !== "none") {
							$("#numberOfAppliedHistoryFilters").val((currentFiltersVal + 1));
							
							if (filterApplied === "dates") {
								
								var windowSize = $(window).width();
								if (windowSize < 1280) {
									$(".facet-list-js").append("<li class=\"filter-js "+ filterApplied + "\"><a id=\"" + filterApplied +"\" class=\"remove-payment-filter-js\" href=\"\"><span class=\"glyphicon glyphicon-remove\"></span></a>&nbsp; From:&nbsp;" + $("#fromDateVal").val() + "<br>&nbsp;&nbsp;To:&nbsp;" + $("#toDateVal").val() + "</li>");
								} else {
									$(".facet-list-js").append("<li class=\"filter-js "+ filterApplied + "\"><a id=\"" + filterApplied +"\" class=\"remove-payment-filter-js\" href=\"\"><span class=\"glyphicon glyphicon-remove\"></span></a>&nbsp; From:&nbsp;" + $("#fromDateVal").val() + "<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;To:&nbsp;" + $("#toDateVal").val() + "</li>");
								}
							} else if (filterApplied === "keyword") {
								$(".facet-list-js").append("<li class=\"filter-js "+ filterApplied + "\"><a id=\"" + filterApplied +"\" class=\"remove-payment-filter-js\" href=\"\"><span class=\"glyphicon glyphicon-remove\"></span></a>&nbsp;&nbsp;Keywords:&nbsp;" + $("#keyword").val() + "</li>");
							}
							
							
							$(".number-of-payment-filters-js").html((currentFiltersVal + 1));
							if ((currentFiltersVal + 1) > 0) {
								$(".applied-history-filters-js").show();
								$("#filterApplied").show();
							} else {
								$(".applied-history-filters-js").hide();
								$("#filterApplied").hide();
							}

							if (filterApplied === "keyword") {
								$("#keywords-js").hide();
							} else if (filterApplied === "dates") {
								$("#dates-js").hide();
							}
						}
					}
					
					var windowSize = $(window).width();
					if (windowSize < 1280) {
						$.colorbox.close();
					}
					ACC.invoicespayments.updatePaymentHistoryRecords(data);
				},
				error: function (xmlHttpRequest, errorText, thrownError) {

					$.unblockUI();
					console.log("Pagination AJAX Call Error: "+ thrownError+" "+errorText);
					$('#multiAccountErrorMsg').removeClass("hide");
					$('.pageBodyContent').animate({
							scrollTop: 0
						}, 1000);
				},
				timeout: 10000,
			});
		}
    },
	
	updatePaymentHistoryRecords: function (JSONObj) {
		
		if (JSONObj != null) {
			
			var numberOfRecords = JSONObj.response.totalRecordsCount;
			var toCount = parseInt(JSONObj.response.toCount);
			
			if (parseInt(JSONObj.response.totalRecordsCount) > 0) {
				var fromCount = parseInt(JSONObj.response.fromCount) + 1;
			} else {
				var fromCount = parseInt(JSONObj.response.fromCount);
			}
			
			$("#paymentFromCount").html(fromCount);
			$("#paymentToCount").html(toCount);
			$("#paymentTotalCount").html(numberOfRecords);
			
			if (JSONObj.response.paymentHistory.length == 0) {
				$("#no-payment-invoices").show();
				$(".statements-count-history").hide();
				$(".account-paymenthistory").css("border-bottom-width", "0px");
			} else {
				$("#no-payment-invoices").hide();
				$(".statements-count-history").show();
				$(".account-paymenthistory").css("border-bottom-width", "1px");
			}
			
			
			for (x in JSONObj.response.paymentHistory) {
				
				if ($(".js-payment-row" + x).hasClass("hidden")) {
					$(".js-payment-row" + x).removeClass("hidden");
				}
				
				currentTransactionDate = JSONObj.response.paymentHistory[x].transactionDate;
				$(".js-payment-row" + x).find("#transactionDate").html(currentTransactionDate);
				
				currentAmount = JSONObj.response.paymentHistory[x].amount;
				$(".js-payment-row" + x).find("#paymentAmount").html(currentAmount);
				
				currentPaymentType = JSONObj.response.paymentHistory[x].paymentType;
				$(".js-payment-row" + x).find("#paymentType").html(currentPaymentType);
				
				currentPaymentRef = JSONObj.response.paymentHistory[x].paymentReference;
				$(".js-payment-row" + x).find("#paymentReference").html(currentPaymentRef);
				
				var numberOfCurrentInvoices = JSONObj.response.paymentHistory[x].invoice.length;
				
				if (numberOfCurrentInvoices > 0) {
					$("#viewMoreTableBody" + (parseInt(x) + 1)).empty();
					$("#" + (parseInt(x) + 1) + ".view-more-link").removeClass("hidden");
					
					var tableRowDataToAdd = "";
					
					for (currentInvoice in JSONObj.response.paymentHistory[x].invoice) {
						currentDocNum = JSONObj.response.paymentHistory[x].invoice[currentInvoice].documentNumber;
						currentDocType = JSONObj.response.paymentHistory[x].invoice[currentInvoice].documentType;
						currentRemainingAmount = JSONObj.response.paymentHistory[x].invoice[currentInvoice].paidAmount;
						
						var rowDataForTable = "<tr class=\"mobile-alignment js-invoices-row" + currentInvoice + "\">" + "<td class=\"payment-detail-heading hidden-sm hidden-md hidden-lg\">" + ACC.paymentHistoryTableDocNo + "</td><td id=\"documentNumber\" class=\"payment-detail-item\">" + currentDocNum + "</td> <td class=\"payment-detail-heading hidden-sm hidden-md hidden-lg\">" + ACC.paymentHistoryTableDocType + "</td> <td id=\"documentType\" class=\"payment-detail-item\">" + currentDocType + "</td><td class=\"payment-detail-heading lastCol hidden-sm hidden-md hidden-lg\">" + ACC.paymentHistoryTableAmountPaid + "</td><td id=\"paidAmount\" class=\"payment-detail-item lastCol\">" + currentRemainingAmount + "</td></tr>";
						
						// Appending the current row of invoices.
						tableRowDataToAdd = tableRowDataToAdd + rowDataForTable;
					}
					$("#viewMoreTableBody" + (parseInt(x) + 1)).append(tableRowDataToAdd);
				} else {
					$("#" + (parseInt(x) + 1) + ".view-more-link").addClass("hidden");
				}
				
			}
			
			var numberOfRowOnPage = JSONObj.response.paymentHistory.length;
			if (numberOfRowOnPage < 10) {
				var numberOfClearLoops = 10 - numberOfRowOnPage;
				for (i = numberOfRowOnPage; i < 10; i++) { 
					$(".js-payment-row" + i).addClass("hidden");
				}
			}
			
			$(".responsive-table-item-vm").slideUp(500);
			$(".responsive-table-item-vm").hide();
			$(".view-more-link").html("View more");
		}
    },
	
	resetPagination: function (currentLink, currentListItem, currentForm, numPages) {
		
		if (parseInt(numPages) > 5) {
			$(".page-number-js1").parent().siblings().show();
			var currentPageNum = parseInt(currentLink.attr("id"));
			
			if (currentPageNum == parseInt(numPages)) {
				$(".page-number-js1").attr("id", currentPageNum - 4);
				$(".page-number-js1").html(currentPageNum - 4);
				
				$(".page-number-js2").attr("id", currentPageNum - 3);
				$(".page-number-js2").html(currentPageNum - 3);
				
				$(".page-number-js3").attr("id", currentPageNum - 2);
				$(".page-number-js3").html(currentPageNum - 2);
				
				$(".page-number-js4").attr("id", currentPageNum - 1);
				$(".page-number-js4").html(currentPageNum - 1);
				
				$(".page-number-js5").attr("id", currentPageNum);
				$(".page-number-js5").html(currentPageNum);
				
				$(".page-number-js5").parent().addClass("active");
				$(".page-number-js5").parent().siblings().removeClass("active");
				
			} else if ((currentPageNum + 1) == parseInt(numPages)) {
				$(".page-number-js1").attr("id", currentPageNum - 3);
				$(".page-number-js1").html(currentPageNum - 3);
				
				$(".page-number-js2").attr("id", currentPageNum - 2);
				$(".page-number-js2").html(currentPageNum - 2);
				
				$(".page-number-js3").attr("id", currentPageNum - 1);
				$(".page-number-js3").html(currentPageNum - 1);
				
				$(".page-number-js4").attr("id", currentPageNum);
				$(".page-number-js4").html(currentPageNum);
				
				$(".page-number-js5").attr("id", currentPageNum + 1);
				$(".page-number-js5").html(currentPageNum + 1);
				
				$(".page-number-js4").parent().addClass("active");
				$(".page-number-js4").parent().siblings().removeClass("active")
				
			} else if (((currentPageNum + 2) >= 5) && ((currentPageNum - 2) > 0)) {
				$(".page-number-js1").attr("id", currentPageNum - 2);
				$(".page-number-js1").html(currentPageNum - 2);
				
				$(".page-number-js2").attr("id", currentPageNum - 1);
				$(".page-number-js2").html(currentPageNum - 1);
				
				$(".page-number-js3").attr("id", currentPageNum);
				$(".page-number-js3").html(currentPageNum);
				
				$(".page-number-js4").attr("id", currentPageNum + 1);
				$(".page-number-js4").html(currentPageNum + 1);
				
				$(".page-number-js5").attr("id", currentPageNum + 2);
				$(".page-number-js5").html(currentPageNum + 2);
				
				$(".page-number-js3").parent().addClass("active");
				$(".page-number-js3").parent().siblings().removeClass("active");
				
			} else if (((currentPageNum + 3) >= 5) && ((currentPageNum - 1) > 0)) {
				$(".page-number-js1").attr("id", currentPageNum - 1);
				$(".page-number-js1").html(currentPageNum - 1);
				
				$(".page-number-js2").attr("id", currentPageNum);
				$(".page-number-js2").html(currentPageNum);
				
				$(".page-number-js3").attr("id", currentPageNum + 1);
				$(".page-number-js3").html(currentPageNum + 1);
				
				$(".page-number-js4").attr("id", currentPageNum + 2);
				$(".page-number-js4").html(currentPageNum + 2);
				
				$(".page-number-js5").attr("id", currentPageNum + 3);
				$(".page-number-js5").html(currentPageNum + 3);
				
				$(".page-number-js2").parent().addClass("active");
				$(".page-number-js2").parent().siblings().removeClass("active");
				
			} else if (((currentPageNum + 4) >= 5) && ((currentPageNum == 1))) {
				$(".page-number-js1").attr("id", currentPageNum);
				$(".page-number-js1").html(currentPageNum);
				
				$(".page-number-js2").attr("id", currentPageNum + 1);
				$(".page-number-js2").html(currentPageNum + 1);
				
				$(".page-number-js3").attr("id", currentPageNum + 2);
				$(".page-number-js3").html(currentPageNum + 2);
				
				$(".page-number-js4").attr("id", currentPageNum + 3);
				$(".page-number-js4").html(currentPageNum + 3);
				
				$(".page-number-js5").attr("id", currentPageNum + 4);
				$(".page-number-js5").html(currentPageNum + 4);
				
				$(".page-number-js1").parent().addClass("active");
				$(".page-number-js1").parent().siblings().removeClass("active");
				
			} else if (((currentPageNum + 1) >= 5) && ((currentPageNum - 3) > 0)) {
				$(".page-number-js1").attr("id", currentPageNum - 3);
				$(".page-number-js1").html(currentPageNum - 3);
				
				$(".page-number-js2").attr("id", currentPageNum - 2);
				$(".page-number-js2").html(currentPageNum - 2);
				
				$(".page-number-js3").attr("id", currentPageNum - 1);
				$(".page-number-js3").html(currentPageNum - 1);
				
				$(".page-number-js4").attr("id", currentPageNum);
				$(".page-number-js4").html(currentPageNum);
				
				$(".page-number-js5").attr("id", currentPageNum + 1);
				$(".page-number-js5").html(currentPageNum + 1);
				
				$(".page-number-js4").parent().addClass("active");
				$(".page-number-js4").parent().siblings().removeClass("active");
				
			} else if (currentPageNum == 5) {
				$(".page-number-js1").attr("id", currentPageNum - 4);
				$(".page-number-js1").html(currentPageNum - 4);
				
				$(".page-number-js2").attr("id", currentPageNum - 3);
				$(".page-number-js2").html(currentPageNum - 3);
				
				$(".page-number-js3").attr("id", currentPageNum - 2);
				$(".page-number-js3").html(currentPageNum - 2);
				
				$(".page-number-js4").attr("id", currentPageNum - 1);
				$(".page-number-js4").html(currentPageNum - 1);
				
				$(".page-number-js5").attr("id", currentPageNum);
				$(".page-number-js5").html(currentPageNum);
				
				$(".page-number-js5").parent().addClass("active");
				$(".page-number-js5").parent().siblings().removeClass("active");
			}
			
			if (ACC.invoicespayments.checkFirstOrLastPage(currentPageNum, "1")) {
				$(".pagination-prev").addClass("disabled");
				$(".pagination-prev-js").addClass("disabled");

				$(".pagination-next").removeClass("disabled");
				$(".pagination-next-js").removeClass("disabled");

			} else if (ACC.invoicespayments.checkFirstOrLastPage(currentPageNum, numPages)) {
				$(".pagination-prev").removeClass("disabled");
				$(".pagination-prev-js").removeClass("disabled");

				$(".pagination-next").addClass("disabled");
				$(".pagination-next-js").addClass("disabled");

			} else {
				$(".pagination-prev").removeClass("disabled");
				$(".pagination-prev-js").removeClass("disabled");

				$(".pagination-next").removeClass("disabled");
				$(".pagination-next-js").removeClass("disabled");
			}
			
			if ($(".pagination").css('display') == 'none') {
				$(".pagination").show();
			}
		} else {
			
			if (numPages < 2) {
				$(".pagination").hide();
			} else {
				$(".pagination").show();
				
				for (i = 1; i <= 5; i++) {
					if (i > parseInt(numPages)) {
						$(".page-number-js" + i).parent().hide();
					} else {
						$(".page-number-js" + i).parent().show();
						$(".page-number-js" + i).attr("id", i);
						$(".page-number-js" + i).html(i);
					}
				}

				var currentPageNum = parseInt(currentLink.attr("id"));
				$(".page-number-js" + currentPageNum).parent().addClass("active");
				$(".page-number-js" + currentPageNum).parent().siblings().removeClass("active");
				
				if (ACC.invoicespayments.checkFirstOrLastPage(currentPageNum, "1")) {
					$(".pagination-prev").addClass("disabled");
					$(".pagination-prev-js").addClass("disabled");

					$(".pagination-next").removeClass("disabled");
					$(".pagination-next-js").removeClass("disabled");

				} else if (ACC.invoicespayments.checkFirstOrLastPage(currentPageNum, numPages)) {
					$(".pagination-prev").removeClass("disabled");
					$(".pagination-prev-js").removeClass("disabled");

					$(".pagination-next").addClass("disabled");
					$(".pagination-next-js").addClass("disabled");

				} else {
					$(".pagination-prev").removeClass("disabled");
					$(".pagination-prev-js").removeClass("disabled");

					$(".pagination-next").removeClass("disabled");
					$(".pagination-next-js").removeClass("disabled");
				}
			}
		}
    },
	
	checkFirstOrLastPage: function (currentLink, firstOrLastCheck) {
		var currentPage = currentLink.toString();
		
		if (currentPage === firstOrLastCheck.toString()) {
			return true;
		} else {
			return false;
		}
    },
	
	updateTotalDueAmount: function () {
		
		var totalInvoices = $(".js-amount-field").length;
		var newDueAmount = 0;
		
		for (i = 0; i < totalInvoices; i++) {
			var currentAmount = $("#paidAmount" + i).val();
			newDueAmount = newDueAmount + parseFloat(currentAmount);
		}
		
		if (newDueAmount >= 1) {
			$("#totalPayableAmount").val(newDueAmount.toFixed(2));
			$("#totalAmountSection").html(newDueAmount.toFixed(2));
			$("#originalPayableAmount").attr("initVal", newDueAmount.toFixed(2));
			
			var intitalTotalAmount = parseFloat($("#originalPayableAmount").val());
			
			if (intitalTotalAmount == (newDueAmount.toFixed(2))) {
				$("#partialPaymentSection").addClass("hide");
			} else {
				$("#partialPaymentSection").removeClass("hide");
			}
			
			return true;
		} else {
			$('#generalErrorMsg').html(ACC.invoiceTotalMsg);
			$('#generalErrorMsg').removeClass("hide");
			$('.pageBodyContent').animate({
					scrollTop: 0
				}, 1000);
			
			return false;
		}
    },
	
	fixDateFormat: function (dateString) {
		if (dateString != "") {
			var returnVar = dateString.split(/\D/g);
			return [returnVar[2],returnVar[1],returnVar[0]].join("/");
		} else {
			return dateString;
		}
	},
	
	resetInvoiceFormData: function (currentID, currentCount) {
		$("#documentNumber.js-invoice-docno" + currentID).attr("name", "asahiSamInvoiceForm[" + currentCount + "].docNumber");
		$("#lineNumber.js-invoice-lineno" + currentID).attr("name", "asahiSamInvoiceForm[" + currentCount + "].lineNumber");
		$("#documentType.js-invoice-doctype" + currentID).attr("name", "asahiSamInvoiceForm[" + currentCount + "].documentType");
		$("#remainingAmount.js-invoice-amount" + currentID).attr("name", "asahiSamInvoiceForm[" + currentCount + "].remainingAmount");
		$("#paidAmount.js-invoice-payamount" + currentID).attr("name", "asahiSamInvoiceForm[" + currentCount + "].paidAmount");
	},
	
	enableUpdateLink: function (object) {
		var currentEnteredVal = parseFloat(object.val());
		var originalVal = parseFloat(object.attr("remainingamount"));
		var rowNum = parseFloat(object.attr("rowNum"));

		if (currentEnteredVal == originalVal) {
			$("#" + rowNum + ".js-amount-update").addClass("disabled");
		} else {
			$("#" + rowNum + ".js-amount-update").removeClass("disabled");
		}
	},
	
	resetPaymentHistoryFilters: function () {
		$(".payment-history-form").find("#fromDateVal").val("");
		$(".payment-history-form").find("#toDateVal").val("");
		$(".payment-history-form").find("#keyword").val("");

		$("#fromDate").val("");
		$("#toDate").val("");
		
		$(".js-date-filter-go-btn").attr("disabled", "disabled");
		$(".js-keyword-filter-go").attr("disabled", "disabled");

		$("#applied-filters").hide();
		$("#dates-js").show();
		$("#keywords-js").show();

		$("#numberOfAppliedHistoryFilters").val(0);
		$(".number-of-payment-filters-js").html(0);
		$("#filterApplied").hide();
		$(".filter-js").remove();
	},
	
	resetNonSelectedFilter: function (currentAppliedPaymentFilter) {
		if (currentAppliedPaymentFilter === "keywords") {
			if ($(".filter-js.dates").length < 1) {
				$(".payment-history-form").find("#fromDateVal").val("");
				$(".payment-history-form").find("#toDateVal").val("");

				$("#fromDate").val("");
				$("#toDate").val("");
				
				$(".js-date-filter-go-btn").attr("disabled", "disabled");
			}
		} else if (currentAppliedPaymentFilter === "dates") {
			if ($(".filter-js.keyword").length < 1) {
				$(".payment-history-form").find("#keyword").val("");
				
				$(".js-keyword-filter-go").attr("disabled", "disabled");
			}
		}
	}
};