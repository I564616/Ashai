ACC.manageProfiles = {
    sendEmail: function (url) {
        $.ajax(url, {
            method: 'POST',
            success: function (res) {
                $('#generalErrorMsg').addClass('save-cart-success').removeClass('hide').text(res);
                scrollToTop($('.pageBodyContent'), $("#generalErrorMsg"));
            },
            error: function (err) {
                scrollToTop($('.pageBodyContent'), $("#generalErrorMsg"));
            }
        })
    }
}

function scrollToTop (container, scrollTo) {
    // Calculating new position
    // of scrollbar
    var position = scrollTo.offset().top
        - container.offset().top
        + container.scrollTop();

    // Animating scrolling effect
    container.animate({
        scrollTop: position
    });
}