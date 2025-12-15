function waitForElement(){
    if(typeof angular !== "undefined"){
        /* ===== Modules ===== */

//= app/shared/module.js

/* ===== Controllers ===== */

//= app/forms/formsCtrl.js
//= app/forms/updatePasswordCtrl.js
//= app/forms/serviceCtrl.js
//= app/deals/dealsCtrl.js
//= app/cart/cartCtrl.js
//= app/forms/contactUsCtrl.js
//= app/forms/registrationRequestCtrl.js
//= app/notifications/notificationsCtrl.js

//= app/cart/smartOrdersCtrl.js

//= app/shared/messageCtrl.js

//= app/shared/breakpoint.js
//= app/product/PDPCtrl.js

//= app/forms/personalAssistanceCtrl.js

//= app/reward/rewardCtrl.js
//
//= app/components/faqCtrl.js
//= app/invoice/invoiceCtrl.js

/* ===== Directives ===== */

//= app/deals/dealsDirects.js
//= app/forms/formsDirects.js
//= app/forms/serviceDirects.js
//= app/deals/ng-switchery.js

    }
    else{
        setTimeout(waitForElement, 250);
    }
}

if(typeof parent.smarteditJQuery == "undefined"){
    $('#CUB').attr('ng-app', 'CUB');
}

waitForElement();