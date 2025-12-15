var isSearchEvent = false;
$('.js-site-search-input').on('blur click', function () {
    isSearchEvent = true;
});
if ($('#checkPage').val() != 'multiAccount' && $('#checkPage').val() != 'homepage') {
    applySpinner();
}
$(window).bind('load', function () {
    $("html").unblock();
});

$('.button, .btn, button, .js-site-logo, .mini-cart-link').bind('click', function (event) {
    if (($('#checkPage').val() == 'checkoutPage' &&  $(this).parent().context.className.indexOf('js-site-logo') != -1) || checkSpinnerApplicable(event, excludeClasses, excludeIds)) {
        applySpinner();
    }
});

$(document).ajaxStart(function (event) {
    if (checkSpinnerApplicable(event, excludeClassesOnAjax, excludeIdsOnAjax) && !isSearchEvent) {
        applySpinner()
    }
    else if (event === undefined && !isSearchEvent) {
        applySpinner()
    }
}).ajaxStop(function () {
    $("html").unblock();
}).ajaxError(function () {
    $("html").unblock();
});

function checkSpinnerApplicable(event, exClasses, exIds){
    var spinApply= true;
    if ($('#checkPage').val() == 'checkoutPage' || $('#checkPage').val() == 'paymentdetail') {
        spinApply = false;
    }
    if(event != undefined && event.target != undefined && event.target.documentElement != undefined && event.target.documentElement.innerHTML.indexOf('minicart-colorbox') != -1){
        spinApply = false;
    }
    // to disable spinner while clicking buttons with classname "btn-upload"
    if (event.currentTarget.classList && event.currentTarget.classList.contains("btn-upload") || event.currentTarget.classList && event.currentTarget.classList.contains("disable-spinner")) {
        spinApply = false;
    }
    if (spinApply && exClasses != '' && event != undefined && event.target != undefined && event.target.className != undefined) {
        var excludeClsArr = exClasses.split(', ');
        excludeClsArr.push('apb-registration-link');
        for (var item in excludeClsArr) {
            if (event.target.className.indexOf(excludeClsArr[item]) != -1) {
                spinApply = false;
                break;
            }
        }
    }
    if (spinApply && exIds != '' && event != undefined && event.target != undefined && event.target.id != undefined) {
        var excludeIdsArr = exIds.split(', ');
        for (var item in excludeIdsArr) {
            if (event.target.id.indexOf(excludeIdsArr[item]) != -1) {
                spinApply = false;
                break;
            }
        }
    }
    return spinApply;
}

function applySpinner() {
    $("html").block({
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
}