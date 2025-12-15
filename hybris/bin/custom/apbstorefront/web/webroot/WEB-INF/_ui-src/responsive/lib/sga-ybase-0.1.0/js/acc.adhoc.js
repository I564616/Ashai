//double-check to remove cboxElement
$('#js-checkout-popup').click(function(){
    $('#colorbox').css('display', 'none');
    $('#cboxOverlay').css('display', 'none');
    $.ajax({
        url: $('#disableCCInfoPopupUrl').val(),
        method: 'post',
        success: function () {
            console.log('user response saved');
        },
        error:function(){
            console.error('error is saving response, check logs for more details');
        }
    });
});