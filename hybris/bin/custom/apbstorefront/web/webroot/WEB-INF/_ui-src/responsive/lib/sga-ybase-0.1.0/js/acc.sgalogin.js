ACC.sgalogin = {

    _autoload: [
        "creditCheckForCartPage",
        "disableAddToCartBtn",
        "creditCheckForCheckoutPage"
    ],

    isLoggedInHomepage: function () {
        var currentPage = $("#checkPage").val();

        if (($(".sga-logged-in-user-options").length > 0) || (currentPage === "registerConfirmationPage")) {
            return true;
        } else {
            return false;
        }
    },

    isLoggedOutHomepage: function () {
        var isLoggedOutHomePageElementActive = $(".sga-homepage-info-btns").length > 0;
        return isLoggedOutHomePageElementActive;
    },

    makeLoginCall: function () {
        var currentPage = $("#checkPage").val();
        var isUserLoggedIn = ($("#userIsLoggedIn").length == 1);

        if (isUserLoggedIn) {
            if ((currentPage !== "cartPage") && (currentPage !== "checkoutPage")) {
                var makeTheLoginCall = $("#makeLoginCall").val();

                if (($("#_asm").length > 0) && (makeTheLoginCall === "")) {
                    makeTheLoginCall = "true";
                }
                
                //onCreditBlock will be null when a new tab is opened, but makelogincall will be false
                if (makeTheLoginCall==="true" || sessionStorage.getItem("onCreditBlock") == null) {
                    ACC.sgalogin.fetchUserDetails();
                } else {
                    ACC.sgalogin.creditCheckMessageSGA();
                }
            }
        }
    },

    fetchUserDetails: function () {
        $.ajax({
            dataType: 'json',
            url: ACC.config.encodedContextPath + '/login/validateCustomerCreditAndInclusionList',

            success: function (data) {

            	var isApprovalPending = $('input[name=isApprovalPending]').val();
                sessionStorage.setItem("onCreditBlock", "false");
                sessionStorage.setItem("onCloseToBlock", "false");
                sessionStorage.setItem("loginInterfaceError", "false");
                console.log(data);

                var numberOfMessages = 0;

                for (x in data.response.errors) {
                    numberOfMessages ++;
                    console.log("Error #" + x + ": " + data.response.errors[x].errorCode);

                    // This is to update the session variable for credit block:
                    if (data.response.errors[x].errorCode === "credit_block"  || data.response.errors[x].errorCode === "products_block") {
                    	sessionStorage.setItem('isApprovalPending', isApprovalPending);
                        sessionStorage.setItem("onCreditBlock", "true");
                        sessionStorage.setItem("onCreditBlockMessage", data.response.errors[x].error);
                    } else if (data.response.errors[x].errorCode === "close_to_credit_block") {
                        sessionStorage.setItem('isApprovalPending', isApprovalPending);
                        sessionStorage.setItem("onCloseToBlock", "true");
                        sessionStorage.setItem("onCloseToBlockMessage", data.response.errors[x].error);
                    } else {
                        sessionStorage.setItem("loginInterfaceError", "true");
                        sessionStorage.setItem("loginInterfaceErrorMessage", data.response.errors[x].error)
                    }
                }

                console.log("[Login Interface] No. of errors found from ECC: " + numberOfMessages);

                ACC.sgalogin.creditCheckMessageSGA();
                ACC.minicart.updateMiniCartDisplay();
            },

            error: function (xmlHttpRequest, errorText, thrownError) {
                console.log("Login Interface AJAX Call Error: "+ thrownError+" "+errorText);
                ACC.sgalogin.creditCheckMessageSGA();
            },
            timeout: 10000
        });
    },

    creditCheckMessageSGA:function(){
        var currentPage = $("#checkPage").val();
        var isUserLoggedIn = ($("#userIsLoggedIn").length == 1);

        var closeToBlockHtmlTemplate = function (errorHtml) {
            return `
                <div class="credit-block-error-container">
                    <div class="glyphicon glyphicon-alert pull-left credit-block-error-glyphicon"></div>
                    <div class="credit-block-error-message">
                        ${errorHtml}
                        <div class="nap-line"></div>
                    </div>
                </div>`;
        };

        if (isUserLoggedIn) {
            if ( (currentPage === "checkoutPage") ||
                (currentPage === "homepage") ||
                (currentPage === "productGrid") ||
                (currentPage === "productDetails") ||
                (currentPage === "searchGrid") ||
                (currentPage === "orders") ||
                (currentPage === "order") ||
                (currentPage === "saved-carts") ||
                (currentPage === "savedCartDetailsPage") ||
                (currentPage === "quickOrderPage") ||
                (currentPage === "invoicedetail") ||
                (currentPage === "statement") ||
                (currentPage === "paymentHistory") ||
                (currentPage === "directdebit") ||
                (currentPage === "emptyCart") ||
                (currentPage === "cartPage")
            ) {

                $(".alert-interface-error").remove();

                var isBlocked = (sessionStorage.getItem("onCreditBlock") === "true");

                if (isBlocked) {
                	// ensure approval pending is updated
                    sessionStorage.setItem("isApprovalPending", $('input[name=isApprovalPending]').val())
                	var isApprovalPending = $('input[name=isApprovalPending]').val() || sessionStorage.getItem("isApprovalPending");
                    var creditBlockPending = $('#order-only-pending-block').html();
                    var orderAndPayCreditBlock = $('#order-and-pay-credit-block').html();
                    var accessType = $('input[name=accessType]').val();

                    // will test if html string start from '&lt;'
                    var regex = new RegExp('^&lt;', 'g');
                    var globalAlerts = $('.global-alerts');
                    globalAlerts.append($('<div />', {
                        "class": 'alert alert-warning alert-dismissable alert-interface-error',
                        "id": 'creditBlockError'}));
                    if (ACC.sgalogin.isLoggedInHomepage()) {
                        $('#creditBlockError').addClass('alert-interface-error-homepage');
                    };

                    //onCreditBlockMessage contains escaped HTML, we need to decode this before displaying
                    var creditBlockMessage = sessionStorage.getItem("onCreditBlockMessage");
                    var parsed = new DOMParser().parseFromString(creditBlockMessage, "text/html");
                 // some contents are string while some are html elements
                    creditBlockMessage = regex.test(creditBlockMessage) ? parsed.documentElement.innerText : parsed.documentElement.innerHTML;
                    if ((accessType === 'PAY_AND_ORDER' && isApprovalPending === 'true') || (accessType === 'PAY_ONLY' && isApprovalPending === 'true')) {
                        // need to store it
                        sessionStorage.setItem("onCreditBlockMessage", creditBlockPending);
                        creditBlockMessage = creditBlockPending;
                    } else if (((accessType === 'PAY_AND_ORDER' && isApprovalPending === 'false') || (accessType === 'PAY_ONLY' && isApprovalPending === 'false')) 
                        && (typeof creditBlockMessage === 'undefined')) {
                        
                        sessionStorage.setItem("onCreditBlockMessage", orderAndPayCreditBlock);
                        creditBlockMessage = orderAndPayCreditBlock;
                    }

                    document.getElementById("creditBlockError").innerHTML = "<div class='credit-block-error-container'><div class='glyphicon glyphicon-alert pull-left credit-block-error-glyphicon'>&nbsp;</div>" + creditBlockMessage + "</div>";
                    ACC.sgalogin.disableAddToCartBtn();
                    //track credit block error
                    ACC.track.trackCheckoutError("Your account is on hold.","asahiCheckoutError");
                }

                var loginInterfaceErrorReturned = (sessionStorage.getItem("loginInterfaceError") === "true");

                if (loginInterfaceErrorReturned) {

                    $("#unavailProducts").remove();

                    if (ACC.sgalogin.isLoggedInHomepage()) {
                        $('.global-alerts').append($('<div />', {
                            "class": 'alert alert-warning alert-dismissable alert-interface-error alert-interface-error-homepage',
                            "id": 'loginInterfaceError'}));
                    } else {
                        $('.global-alerts').append($('<div />', {
                            "class": 'alert alert-warning alert-dismissable alert-interface-error',
                            "id": 'loginInterfaceError'}));
                    };

                    document.getElementById("loginInterfaceError").innerHTML = "<div class='glyphicon glyphicon-alert'>&nbsp;</div>" + sessionStorage.getItem("loginInterfaceErrorMessage");
                }
            }

            if ((currentPage === "homepage") || (currentPage === "invoicedetail") || (currentPage === "quickOrderPage") || (currentPage === "orders") || (currentPage === "saved-carts")) {
                var isCloseToBlock = (sessionStorage.getItem("onCloseToBlock") === "true");

                if (isCloseToBlock) {
                    var isApprovalPending = $('input[name=isApprovalPending]').val() || sessionStorage.getItem("isApprovalPending");
                    var accessType = $('input[name=accessType]').val();
                    var isNAPGroup = $('input[name=isNAPGroup]').val();
                    var closeToBlockErrors = $('#close-to-block-errors');
                    
                    var globalAlerts = $('.global-alerts');
                    globalAlerts.append($('<div />', {
                        "class": 'alert alert-warning alert-dismissable alert-interface-error',
                        "id": 'closeToBlockError'}));
                    if (ACC.sgalogin.isLoggedInHomepage()) {
                        $('#closeToBlockError').addClass('alert-interface-error-homepage');
                    };

                    if (accessType === 'PAY_AND_ORDER' || accessType === 'PAY_ONLY') {
                        var html = $('#order-and-pay', closeToBlockErrors).html();
                        html = closeToBlockHtmlTemplate(html);
                        $('#closeToBlockError').append(html);
                    } else if(accessType === 'ORDER_ONLY') {
                        var html = $('#order-only', closeToBlockErrors).html();
                        html = closeToBlockHtmlTemplate(html);
                        $('#closeToBlockError').append(html);
                    }

                    if (isApprovalPending === 'true') {
                        $('#closeToBlockError').empty();
                        var html = $('#order-only-pending', closeToBlockErrors).html();
                        html = closeToBlockHtmlTemplate(html);
                        $('#closeToBlockError').append(html);
                    }

                    if (isNAPGroup === 'false' && accessType !== 'PAY_ONLY') {
                        $('#closeToBlockError .nap-line').append($('#nap-user', closeToBlockErrors).html());
                        $('#closeToBlockError .nap-line').css('margin-top', '10px');
                    }
                }

            }
        }
        $.unblockUI();
    },

    disableAddToCartBtn: function () {

        var currentPage = $("#checkPage").val();

        if ((currentPage === "homepage") || (currentPage === "productGrid") || (currentPage === "productDetails") || (currentPage === "searchGrid") || (currentPage === "cartPage") || (currentPage === "checkoutPage") || (currentPage === "orders") || (currentPage === "order") || (currentPage === "saved-carts") || (currentPage === "savedCartDetailsPage") || (currentPage === "quickOrderPage") || (currentPage === "invoicedetail") || (currentPage === "statement") || (currentPage === "paymentHistory") || (currentPage === "directdebit")) {

            var isBlocked = (sessionStorage.getItem("onCreditBlock") === "true");

            if (isBlocked) {
                $(".addToCartButtonPLP").attr("disabled","disabled");
                $(".js-qty-selector-plus").attr("disabled","disabled");
                $(".js-qty-selector-minus").attr("disabled","disabled");
                $(".re-order").attr("disabled","disabled");
                $(".js-continue-checkout-button").attr("disabled","disabled");
                $(".js-custom-checkout-button").attr("disabled","disabled");
                $(".reorder-button").attr("disabled","disabled");
                $(".quickOrderButton").attr("disabled","disabled");
                $("#addToCartButton").attr("disabled","disabled");
                $(".js-qty-selector-input").attr("disabled","disabled");

                $(".js-qty-selector").addClass("product-unavailable-container");
                $(".js-cart-qty-selector").addClass("product-unavailable-container");
                $(".js-qty-selector-quickorder").addClass("product-unavailable-container");
                $(".qty-selector").addClass("product-unavailable-container");
                $(".quick-order-add-js").addClass("product-unavailable-container");
            }
        }
    },

    validateCheckoutCart: function () {
        $.ajax({
            dataType: 'json',
            url: ACC.config.encodedContextPath + '/checkout/single/validateCheckoutCart',
            async:false,

            beforeSend: function() {
                $.blockUI({
                    message: "<img src='" + ACC.config.commonResourcePath + "/images/spinner.gif' />",
                    overlayCSS: {
                        opacity: 0.01
                    },
                    centerY: false,
                    css: {
                        top: '40%',
                        backgroundColor: 'transparent',
                        color: 'transparent',
                        border: 'none',
                    }
                });
            },

            success: function (data) {
                sessionStorage.setItem("onCreditBlock", "false");
                sessionStorage.setItem("loginInterfaceError", "false");

                $.unblockUI();
                console.log(data);

                var numberOfMessages = 0;

                for (x in data.response.errors) {
                    numberOfMessages ++;
                    console.log("Error #" + x + ": " + data.response.errors[x].errorCode);

                    // This is to update the session variable for credit block:
                    if (data.response.errors[x].errorCode === "credit_block") {
                        sessionStorage.setItem("onCreditBlock", "true");
                        sessionStorage.setItem("onCreditBlockMessage", data.response.errors[x].error);
                    }
                    else if(data.response.errors[x].errorCode === "exclude_product_error"){
                        sessionStorage.setItem("onCreditBlock", "true");
                        sessionStorage.setItem("onCreditBlockMessage", data.response.errors[x].error);
                    }
                    else if(data.response.errors[x].errorCode === "products_block") {
                        sessionStorage.setItem("onCreditBlock", "true");
                        sessionStorage.setItem("onCreditBlockMessage", data.response.errors[x].error);
                    }
                    else {
                        sessionStorage.setItem("loginInterfaceError", "true");
                        sessionStorage.setItem("loginInterfaceErrorMessage", data.response.errors[x].error);
                    }
                }

                console.log("[Login Interface] No. of errors found from ECC: " + numberOfMessages);
            },

            error: function (xmlHttpRequest, errorText, thrownError) {
                console.log("Login Interface AJAX Call Error: "+ thrownError+" "+errorText);
                $.unblockUI();
            },
            timeout: 20000
        });
    },

    creditCheckForCartPage: function () {
        var currentPage = $("#checkPage").val();

        if ((currentPage == "cartPage") || (currentPage == "emptyCart")) {
            var cartInclusionErrorCode = $("#cartInclusionErrorType").val();

            if (cartInclusionErrorCode === "credit_block") {
                var errorMessage = ACC.cartCallCreditBlockErrorMessage;

                sessionStorage.setItem("onCreditBlock", "true");
                sessionStorage.setItem("loginInterfaceError", "false");
                //sessionStorage.setItem("onCreditBlockMessage", ACC.cartCallCreditBlockErrorMessage);

            } else if (cartInclusionErrorCode === "") {
                sessionStorage.setItem("onCreditBlock", "false");
                sessionStorage.setItem("loginInterfaceError", "false");

            } else {
                var errorMessage = ACC.cartCallFailureErrorMessage;

                sessionStorage.setItem("loginInterfaceError", "true");
                sessionStorage.setItem("onCreditBlock", "false");
                sessionStorage.setItem("loginInterfaceErrorMessage", errorMessage);
            }
            ACC.sgalogin.creditCheckMessageSGA();
        }
    },

    creditCheckForCheckoutPage: function () {
        if($("#checkPage").val() == 'checkoutPage')
            ACC.sgalogin.creditCheckMessageSGA();
    }
};

$( window ).bind("load", function() {
    ACC.sgalogin.makeLoginCall();
    if($('#checkoutPossible').length > 0 && $('#checkoutPossible').val() == 'enabled' && sessionStorage.getItem('onCreditBlock') == 'false' ){
        if ($('#customerPermission').length && !$('#customerPermission').is(':checked')) { //bde checkout flow. Don't enable button until user ticks customer permission checkbox.
            $('#addCheckoutDetails').attr('disabled', true);
            $('#finalcheckoutButton').attr('disabled', true);
            return;
        };

        $('#addCheckoutDetails').attr('disabled', false);
    }
});
$(".js-continue-checkout-button").on("click",ACC.sgalogin.validateCheckoutCart);