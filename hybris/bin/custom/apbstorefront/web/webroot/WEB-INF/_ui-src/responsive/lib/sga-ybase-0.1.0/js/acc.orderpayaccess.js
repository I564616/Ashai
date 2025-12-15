var ACC = ACC || {}; // make sure ACC is available

if (($("#checkPage").val() === 'homepage') || ($("#checkPage").val() === 'invoicedetail')) {
    ACC.orderpayaccess = {
        _autoload: [
			"bindAccessRequestLinks"
        ],
		
		bindAccessRequestLinks: function () {
			
            $(document).on("click", '.request-access-js', function (e) {
				e.preventDefault();

				var accessRequestType = $(this).attr('accessRequestType');
				
				$.ajax({
					url: ACC.config.encodedContextPath + $(this).attr('action') + accessRequestType,
					type: $(this).attr('method'),
					success: function (data) {
						
						if (data == true) {
							var redirectURL = ACC.config.encodedContextPath + "/requestAccess?code=" + accessRequestType;
							sessionStorage.setItem('isApprovalPending', 'true'); // approval pending must be true when requesting access
							window.location.replace(redirectURL);
							
						} else if (data == false) {
							var redirectURL = ACC.config.encodedContextPath + "/contactus";
							window.location.replace(redirectURL);
							
						} else {
							$('#multiAccountErrorMsg').removeClass("hide");
							 $('.pageBodyContent').animate({
								scrollTop: 0
								}, 1000);
							console.log("Server Error happened when providing " + accessRequestType + " access.");
						}
						
						
						
					},
					error: function (jqXHR, textStatus, errorThrown) {
						$('#multiAccountErrorMsg').removeClass("hide");
						 $('.pageBodyContent').animate({
							scrollTop: 0
							}, 1000);
						console.log("The following error occurred with " + accessRequestType + " Access Request: " + textStatus, errorThrown);
					}
				});
			});
        }
    };
}