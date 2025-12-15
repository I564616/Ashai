$(function () {
    'use strict';
    var ACC;
    rm.customer.init();
    if($('.page-confirmDealsChange').length){
    	rm.confirmdealchanges.init();
    }
    rm.utilities.init();
    /* Used to initiate modals using magnifique */
    if($('.mfp-hide').length){
        rm.modals.init();
        console.log('modal init');
    }

    if($('.table').length){
        rm.responsivetable.init();
    }

    rm.datepickers.init();

    rm.portalsso.init();

});