/* globals document */
/* globals window */
/* globals localStorage */
/* globals sessionStorage */
/* globals $clamp */

'use strict';

rm.portalsso = {
    init: function() {
        if($("#ssologin").length > 0)
        {
            this.redirectToSSOLoginPage();
        }
    },

    redirectToSSOLoginPage: function ()
    {
        var loc = location;

        setTimeout(function(){
            window.location.replace(loc.origin + "/samlsinglesignon/saml/staffPortal/sabmStore/en/customer-search");
        }, 3000);
    }
};
