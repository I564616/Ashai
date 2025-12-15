ACC.contactus = {
	_autoload: ["subject"],

	/* Subject list box when select other option */
	subject : function() {
		$("#otherSubjectFlag").hide();
		var otherSubject = $("#otherSubject").val();
		var subFlag = $("#subjectFlag").val();
		$("#otherSubject").hide();
		if (subFlag == "otherExists") 
		{
			$("#otherSubject").show();
			$("#subject").change(function() {
				var selectedSubject = $('#subject option:selected').val();
				if (selectedSubject == "8" || selectedSubject == "27") {
					$("#subjectFlag").val("otherExists");
					$("#otherSubject").show();
				} else {
					$("#subjectFlag").val("");
					$("#otherSubject").hide();
				}
			});
		}

	},

	removeLoginErrorMessage : function(){
		$("#sendMessageHeader").style.display="none";
		

		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("menuoption6login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="none";
		
	},

	setCallbackEvents:function(){
		// hiding error fields
		$("#popupError").css({"display": "none"});
		$("#formSuccess").css({"display": "none"});
		
		// adding contactus autofilled values to callback form
		$("input#callbackEnquiryType").val("REQUEST_CALLBACK");
		if ( $("input#accountNo").val() ) {
			$("#callbackAccountNo").val( $("input#accountNo").val() );
		}
		if ( $("input#companyName").val() ) {
			$("#callbackCompanyName").val( $("input#companyName").val() );
		}
		if ( $("input#emailAddress").val() ) {
			$("#callbackEmailAddress").val( $("input#emailAddress").val() );
		}
		if ( $("input#asahiSalesRepName").val() ) {
			$("#callbackSalesRepName").val( $("input#asahiSalesRepName").val() );
		}
		if ( $("input#asahiSalesRepEmail").val() ) {
			$("#callbackSalesRepEmail").val( $("input#asahiSalesRepEmail").val() );
		}


		// binding callback popup to button
		$("button#callbackPopupButton").click(function() {
			ACC.contactus.resetPopupForm();
			// showing popup form
			$("#callbackPopupLayer").modal("show");
			$('#callbackPopupLayer').removeClass("cboxElement");
			$('html').unblock();
		});
		
		
		// form validation for callback popup
		$("button#popupSendButton").on("click", function() {		
			$.ajax({
				type : 'GET',
				url : 'contactus/requestCallBack',
				data : $('#callbackForm').serialize(),
				success: function(response) {
					if(response.errors){
						var valid = ACC.contactus.validatePopupForm(response);
					}
					else {
						// showing success popup
						$("#formCallback").css({"display": "none"});
						$("#formSuccess").css({"display": "block"});
					}
				},
				error: function(response) {
					console.log(response);
				}
			});
		});
	
	},

	validatePopupForm: function(response){
		// response should be in form 
	// { 
	//	 errorFields: [name, contactNumber] 
	// }
	// or similar, just modify if statements below
	
	// resetting error fields
	var valid = true;
	ACC.contactus.hidePopupErrorFields();

	// if name invalid
	if ( response.errors.name ) {
		valid = false;
		$("#popupName").addClass("has-error");
		$("#callbackNameHelpBlock").css({"display": "block"});
	}

	// if contact number invalid
	if ( response.errors.contactNumber ) {
		valid = false;
		$("#popupContactNumber").addClass("has-error");
		$("#callbackContactNumberHelpBlock").css({"display": "block"});
		$("#callbackContactNumber").val(""); 	// empty contact number input field if error
	} 

	// show top error if one is invalid
	if (!valid) {
		$("#popupError").css({"display": "block"});
	}

	return valid;
	},

	resetPopupForm: function(){
		// reset shown fields
	$("#formCallback").css({"display": "block"});
	$("#formSuccess").css({"display": "none"});
	
	//checking for name autofill and resetting text fields
	if ( $("input#name").val() ) {
		$("#callbackName").val( $("input#name").val() );
	}
	else {
		$("#callbackName").val("");
	}
	$("#callbackContactNumber").val("");
	$("#callbackFurtherdetail").val("");
	
	ACC.contactus.hidePopupErrorFields();
	},

	hidePopupErrorFields: function(){
	$("#popupError").css({"display": "none"});
	$("#callbackNameHelpBlock").css({"display": "none"});
	$("#callbackContactNumberHelpBlock").css({"display": "none"});
	$("#popupName").removeClass("has-error");
	$("#popupContactNumber").removeClass("has-error");

	// reset shown fields
	$("#callbackForm").css({"display": "block"});
	$("#formSuccess").css({"display": "none"});
	},

	fixBelowForms: function(counter,formNo){
		for(let i = formNo+1; i < counter+1; i++){
			$form = $('#discrepancy-form-'+i)
			$form.find('input[id="materialNumber"]').attr('name','discrepancies['+(i-1)+'].materialNumber');
			$form.find('input[id="qtyWithDelIssue"]').attr('name','discrepancies['+(i-1)+'].qtyWithDelIssue');
			$form.find('input[id="expectedTotalPay"]').attr('name','discrepancies['+(i-1)+'].expectedTotalPay');
			$form.find('input[id="expectedQty"]').attr('name','discrepancies['+(i-1)+'].expectedQty');
			$form.attr('id','discrepancy-form-'+(i-1));

		}
	},

	showExtraFields: function(){
		document.getElementById("delivery-wrapper").style.display="block";
		document.getElementById("further-detail").style.display="none";
		document.getElementById("additional-information").style.display="block";

		$('html').unblock();
	},

	hideExtraFields: function(){
		document.getElementById("delivery-wrapper").style.display="none";
		document.getElementById("further-detail").style.display="block";
		document.getElementById("additional-information").style.display="none";

		$('html').unblock();
	},

	getInvoice: function(){
            var method = "GET";
            var deliveryNumber = $("#deliveryNumber").val();
            $.ajax({
                url: ACC.config.encodedContextPath + '/invoice/delivery',
                data:{'deliveryNumber': deliveryNumber},
                contentType :'application/download',
                type:method,
                success: function (data, textStatus, xhr) {
					if(data === "For Delivery " + deliveryNumber + " Invoice Not Found."){
						document.getElementById("deliveryError").style.display="block";
						$("#globalErrorMessage").show();

					}
					else{
					var currentUrl = location.href;
					var invoiceURL = currentUrl.substr(0, currentUrl.lastIndexOf("/") + 1);
					invoiceURL += "invoice/delivery?deliveryNumber=" + deliveryNumber;
                    $("#pdfcontent").attr('src', invoiceURL);
                    $("#pdfcontentframe").show();

					};
                },
            error: function (xhr, textStatus, error) {
                console.error(error);
            }
        });

    },

	downloadInvoice: function(){
            var method = "GET";
            var deliveryNumber = $("#deliveryNumber").val();
            $.ajax({
                url: ACC.config.encodedContextPath + '/invoice/delivery',
                data:{'deliveryNumber': deliveryNumber},
                contentType :'application/download',
                type:method,
                success: function (data, textStatus, xhr) {
					if(data === "For Delivery " + deliveryNumber + " Invoice Not Found."){
						document.getElementById("deliveryError").style.display="block";
						$("#globalErrorMessage").show();
						
					}
					else{
						var currentUrl = location.href;
						var invoiceURL = currentUrl.substr(0, currentUrl.lastIndexOf("/") + 1);
						invoiceURL += "invoice/delivery?deliveryNumber=" + deliveryNumber;			
						window.open(invoiceURL);
					};
                },
            error: function (xhr, textStatus, error) {
                console.error(error);
            }
        });

    },

	displayMaterialNoError: function(){
		$("#materialNoError").css("display", "block");
		document.getElementById("materialNoError").style.display="block";
	}
}

	

	





	$(document).ready(function() {
		var x = document.referrer;
		
		//if user navigates to contact us page using payment plan link, display payment plan contact form by default
		var previousUrl = x.substring(x.lastIndexOf('/')+1);
		if ((previousUrl == "paymentPlan") || (previousUrl == "paymentPlan=true")){
			document.getElementById("menuoption3button").click();
			document.getElementById("menu3sub4").click();
		}

		with (ACC.contactus) {
			subject();
			setCallbackEvents();
			
		}
	});




/* Contact Us type selection behaviour */

var contactUsLandingPermission = $("contactUsIsLoggedIn");

var isUpdatedForm = $("contactusUpdateAvailable");

var contactUsEnquiryForm = $("#apbContactUsForm"); //reference to form 

var contactUsEnquiryType = contactUsEnquiryForm.find('input[id="enquiryType"]'); 

var contactUsEnquirySubType = contactUsEnquiryForm.find('input[id="enquirySubType"]'); 

var contactUsDeliveryNumber = contactUsEnquiryForm.find('input[id="deliveryNumber"]'); 

var contactUsName = contactUsEnquiryForm.find('input[id="name"]'); 

var contactUsEmailAddress = contactUsEnquiryForm.find('input[id="emailAddress"]'); 

var contactUsContactNumber = contactUsEnquiryForm.find('input[id="contactNumber"]'); 

var contactUsDiscrepancies = contactUsEnquiryForm.find('input[id="discrepancies"]'); 

var contactUsaddInfo = contactUsEnquiryForm.find('input[id="addInfo"]'); 

var contactUsMessage = contactUsEnquiryForm.find('input[id="message"]'); 


/////////////////////////////// Menu options ///////////////////////////////

$(document).on("click", "#menuoption1button", function() {
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	contactUsEnquiryType.val("WEBSITE_SUPPORT");
	ACC.contactus.hideExtraFields();

	/* for loop to hide all other type buttons*/

	var y = document.getElementById("menuoption1button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");
		}
	}
	var isLoginFlag = window.customerLoggedIn;
	document.getElementById("menureselect").style.display="block";
	document.getElementById("submenuselectmessage").style.display="block";
	document.getElementById("menuoption1button").classList.add("selected-button");
	document.getElementById("defaultcontactform").style.display="none";
	document.getElementById("menuoption1button").disabled= true;
	$('html').unblock();
	enquiry_type = "WEBSITE_SUPPORT";
	contactUsFormTypeNumber="1";
});

$(document).on("click", "#menuoption2button", function() {
	contactUsEnquiryType.val("REPORT_DEL_ISSUE");
	ACC.contactus.hideExtraFields();

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"

	document.getElementById("subtypereselectbutton").style.display="block";

	/* for loop */
	var y = document.getElementById("menuoption2button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 7; i++){
		var x = "menu2sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");

		}
	}

	document.getElementById("menureselect").style.display="block";
	document.getElementById("submenuselectmessage").style.display="block";
	document.getElementById("menuoption2button").classList.add("selected-button");

	document.getElementById("menuoption1button").style.display="none";
	document.getElementById("menuoption3button").style.display="none";
	document.getElementById("defaultcontactform").style.display="none";		

	document.getElementById("menuoption2").classList.remove("mid-option");
	document.getElementById("menuoption2button").disabled= true;
	$('html').unblock();
	enquiry_type = "REPORT_DEL_ISSUE";
	contactUsFormTypeNumber="2";

});

$(document).on("click", "#menuoption3button", function() {
	contactUsEnquiryType.val("MANAGE_ACC");
	ACC.contactus.hideExtraFields();

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"


	/* for loop */
	var y = document.getElementById("menuoption3button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");

		}
	}

	document.getElementById("menureselect").style.display="block";
	document.getElementById("menuoption3button").classList.add("selected-button");

	document.getElementById("menuoption1button").style.display="none";
	document.getElementById("menuoption2button").style.display="none";
	
	document.getElementById("menuoption3").classList.remove("menu-button-right");
	document.getElementById("menuoption3button").disabled= true;
	$('html').unblock();
	enquiry_type = "MANAGE_ACC";
	contactUsFormTypeNumber="3";

});

$(document).on("click", "#menuoption4button", function() {
	contactUsEnquiryType.val("REG_SUPPORT");
	ACC.contactus.hideExtraFields();

	
	document.getElementById("menuoption6login").style.display="none";

	document.getElementById("globalErrorMessage").style.display="none"

	/* for loop */
	var y = document.getElementById("menuoption4button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 4; i++){
		var x = "menu4sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");

		}
	}

	document.getElementById("menureselect").style.display="block";
	document.getElementById("submenuselectmessage").style.display="block";
	document.getElementById("menuoption4button").classList.add("selected-button");

	document.getElementById("defaultcontactform").style.display="none";
	document.getElementById("menuoption4button").disabled= true;
	enquiry_type = "REG_SUPPORT";
	$('html').unblock();
	contactUsFormTypeNumber="4";

});

$(document).on("click", "#menuoption5button", function() {
	contactUsEnquiryType.val("AMEND_AN_ORDER");
	ACC.contactus.hideExtraFields();

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"

	/* for loop */
	var y = document.getElementById("menuoption5button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 4; i++){
		var x = "menu5sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");

		}
	}

	document.getElementById("menureselect").style.display="block";
	document.getElementById("submenuselectmessage").style.display="block";
	document.getElementById("menuoption5button").classList.add("selected-button");

	document.getElementById("menuoption4button").style.display="none";
	document.getElementById("menuoption6button").style.display="none";

	document.getElementById("defaultcontactform").style.display="none";

	document.getElementById("menuoption5").classList.remove("mid-option");
	document.getElementById("menuoption5button").disabled= true;

	$('html').unblock();
	enquiry_type = "AMEND_AN_ORDER";
	contactUsFormTypeNumber="5";

});

$(document).on("click", "#menuoption6button", function() {

	contactUsEnquiryType.val("INCORRECT_CHARGE");
	document.getElementById("adminFooter").style.display="block";
	document.getElementById("globalErrorMessage").style.display="none"
	if (!contactUsIsLoggedIn){
	document.getElementById("menuoption6login").style.display="block";
		//document.getElementById("menu2submenu2").classList.add("menu-below-error");
		document.getElementById("globalErrorMessage").style.display="block";
	}
	else{
		document.getElementById("defaultcontactform").style.display="block"
		ACC.contactus.showExtraFields();
		document.getElementById("row1field2").style.display="none"
		document.getElementById("row1field3").style.display="block"
		document.getElementById("row1field4").style.display="none"
		document.getElementById("row1field1").style.display="block"
		document.getElementById("row1field6").style.display="block"
		document.getElementById("row1field5").style.display="none"
		
		document.getElementById("delivery-browse-responsive").style.display="block"

		
		/* for loop */
		var y = document.getElementById("menuoption6button").id;
		for (let i = 1; i < 9; i++){
			var z = "menuoption" + i.toString();
			var x = "menuoption" + i.toString() + "button";
			if (x != y){
				document.getElementById(x).style.display="none";
				document.getElementById(z).style.display="none";
		}
	}

	document.getElementById("menureselect").style.display="block";
	document.getElementById("menuoption6").classList.remove("menu-button-right");
	document.getElementById("menuoption6button").classList.add("selected-button");
	document.getElementById("menuoption6button").disabled= true;
	contactUsFormTypeNumber="6";
	}

	$('html').unblock();

});

$(document).on("click", "#menuoption7button", function() {
	contactUsEnquiryType.val("ASSISTANCE");
	ACC.contactus.hideExtraFields();

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"


	/* for loop */
	var y = document.getElementById("menuoption7button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}

	/* for loop to display all corresponding subtype buttons*/
	for (let i = 1; i < 4; i++){
		var x = "menu7sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="block";
			document.getElementById(x).classList.remove("selected-option-margin");

		}
	}
	document.getElementById("menuoption7button").classList.add("selected-button");
	document.getElementById("menureselect").style.display="block";
	document.getElementById("defaultcontactform").style.display="none";
	document.getElementById("subtypereselectbutton").style.display="block";
	document.getElementById("menuoption7button").disabled= true;
	enquiry_type = "ASSISTANCE";
	$('html').unblock();
	contactUsFormTypeNumber="7";


});

$(document).on("click", "#menuoption8button", function() {
	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
	contactUsEnquiryType.val("OTHER");
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	document.getElementById("sendMessageHeader").style.display="block";

	ACC.contactus.hideExtraFields();

	/* for loop */
	var y = document.getElementById("menuoption8button").id;
	for (let i = 1; i < 9; i++){
		var z = "menuoption" + i.toString();
		var x = "menuoption" + i.toString() + "button";
		if (x != y){
			document.getElementById(x).style.display="none";
			document.getElementById(z).style.display="none";
		}
	}
	document.getElementById("menureselect").style.display="block";
	document.getElementById("defaultcontactform").style.display=("block");
	document.getElementById("menuoption8button").classList.add("selected-button");
	document.getElementById("menuoption8").classList.remove("mid-option");
	document.getElementById("menuoption8button").disabled= true;
	enquiry_type = "OTHER";
	$('html').unblock();
	contactUsFormTypeNumber="8";

});






















/////////////////////////////// Menu: Change selection ///////////////////////////////

	$(document).on("click", "#menureselectbutton", function() {
		location.reload();
		document.getElementById("menu1sub2").classList.add("mid-option");
		document.getElementById("menu1sub5").classList.add("mid-option");
		document.getElementById("menu2sub2").classList.add("mid-option");
		document.getElementById("menu2sub5").classList.add("mid-option");
		document.getElementById("menu3sub2").classList.add("mid-option");
		document.getElementById("menu3sub5").classList.add("mid-option");
		document.getElementById("delivery-browse-responsive").style.display="none"

		document.getElementById("menu4sub2").classList.add("mid-option");
		document.getElementById("menu5sub2").classList.add("mid-option");

		document.getElementById("menu7sub2").classList.add("mid-option");

		document.getElementById("accFooter").style.display="none";
		document.getElementById("cicFooter").style.display="none";
		document.getElementById("productFooter").style.display="none";
		document.getElementById("consumerFooter").style.display="none";
		document.getElementById("adminFooter").style.display="none";

		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("sendMessageHeader").style.display="none";
		document.getElementById("menu2submenu2").classList.remove("menu-below-error");
		document.getElementById("menu2sub1login").style.display="none"
		document.getElementById("menu2sub2login").style.display="none"
		document.getElementById("menu2sub3login").style.display="none"
		document.getElementById("menu3sub4login").style.display="none"

		document.getElementById("defaultFieldOne").style.display="block";
		document.getElementById("defaultFieldTwo").style.display="block";
	
		document.getElementById("linkFieldOne").style.display="none";
		document.getElementById("linkFieldTwoAlb").style.display="none";
		document.getElementById("linkFieldThree").style.display="none";
		document.getElementById("linkFieldFour").style.display="none";
		document.getElementById("menuoption6login").style.display="none";
		ACC.contactus.hideExtraFields();

		document.getElementById("deliveryError").style.display="none";


		document.getElementById("globalErrorMessage").style.display="none"

		/* for loop to display all buttons and their divs*/
		for (let i = 1; i < 9; i++){
			var x = "menuoption" + i.toString() +"button";
			var z = "menuoption" + i.toString();
			document.getElementById(x).disabled= false;
			document.getElementById(x).style.display="block";
			document.getElementById(z).style.display="block";
			document.getElementById(x).classList.remove("selected-button");
			}
		
		/* for loop to hide all corresponding menu 1 subtype buttons*/
		for (let i = 1; i < 6; i++){
			var x = "menu1sub" + i.toString() +"button";
			var z = "menu1sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");

		}

		/* for loop to hide all corresponding menu 2 subtype buttons*/
		for (let i = 1; i < 7; i++){
			var x = "menu2sub" + i.toString() +"button";
			var z = "menu2sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");

			}

		/* for loop to hide all corresponding menu 3 subtype buttons*/
		for (let i = 1; i < 6; i++){
			var x = "menu3sub" + i.toString() +"button";
			var z = "menu3sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");
			document.getElementById(x).classList.remove("selected-button");
			// document.getElementById("menu3submenu2").classList.remove("menu-below-error");

			}

		/* for loop to hide all corresponding menu 4 subtype buttons*/
		for (let i = 1; i < 4; i++){
			var x = "menu4sub" + i.toString() +"button";
			var z = "menu4sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");

		}

		/* for loop to hide all corresponding menu 5subtype buttons*/
		for (let i = 1; i < 4; i++){
			var x = "menu5sub" + i.toString() +"button";
			var z = "menu5sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");

			}

		/* for loop to hide all corresponding menu 7 subtype buttons*/
		for (let i = 1; i < 4; i++){
			var x = "menu7sub" + i.toString() +"button";
			var z = "menu7sub" + i.toString();
			document.getElementById(z).style.display="none";
			document.getElementById(z).classList.remove("selected-option-margin");
			document.getElementById(x).classList.remove("selected-button");

			}


		document.getElementById("menureselect").style.display="none";
		document.getElementById("submenuselectmessage").style.display="none";
		document.getElementById("subtypereselect").style.display="none";
		document.getElementById("menuoption7button").classList.remove("selected-button");


		document.getElementById("defaultcontactform").style.display="none";

		document.getElementById("menuoption2").classList.add("mid-option");
		document.getElementById("menuoption5").classList.add("mid-option");
		document.getElementById("menuoption8").classList.add("mid-option");

		document.getElementById("menuoption3").classList.add("menu-button-right");
		document.getElementById("menuoption6").classList.add("menu-button-right");
		document.getElementById("menu2sub3").classList.add("menu-button-right");
		document.getElementById("menu2sub6").classList.add("menu-button-right");
		document.getElementById("menu3sub3").classList.add("menu-button-right");
		document.getElementById("menu4sub3").classList.add("menu-button-right");
		document.getElementById("menu5sub3").classList.add("menu-button-right");
		document.getElementById("menu7sub3").classList.add("menu-button-right");


		$('html').unblock();
	});















/////////////////////////////// Submenu: Change selection ///////////////////////////////

$(document).on("click", "#subtypereselectbutton", function() {

	//name  error clear
	$('#name').removeClass("discrepancy-field-error");
	$(nameError).css("display", "none");

	//email error clear
	$('#email').removeClass("discrepancy-field-error");
	$(emailAddressError).css("display", "none");

	//contact number error clear
	$('#contactNumber').removeClass("discrepancy-field-error");
	$(contactNumberError).css("display", "none");

	//delivery number error clear
	$(deliveryNumber).removeClass("discrepancy-field-error");
	$(deliveryError).css("display", "none");

	//material number error clear
	$(materialNoError).css("display", "none");
	$(materialNumber).removeClass("discrepancy-field-error");

	//delivery issue error clear
	$(delIssueError).css("display", "none");
	$(qtyWithDelIssue).removeClass("discrepancy-field-error");

	//expected qty error clear
	$(qtyExpectedError).css("display", "none");
	$(expectedQty).removeClass("discrepancy-field-error");

	//expected total pay error clear
	$(expectedChargeError).css("display", "none");
	$(expectedTotalPay).removeClass("discrepancy-field-error");

	//qty received error clear
	$(qtyReceivedError).css("display", "none");
	$(qtyReceived).removeClass("discrepancy-field-error");

	//actual total pay error clear
	$(actualChargeError).css("display", "none");
	$(actualTotalPay).removeClass("discrepancy-field-error");

	document.getElementById("menu1sub2").classList.add("mid-option");
	document.getElementById("menu1sub5").classList.add("mid-option");
	document.getElementById("menu2sub2").classList.add("mid-option");
	document.getElementById("menu2sub5").classList.add("mid-option");
	document.getElementById("menu3sub2").classList.add("mid-option");
	document.getElementById("menu3sub5").classList.add("mid-option");
	document.getElementById("subtypereselect").style.display="none";
	document.getElementById("delivery-browse-responsive").style.display="none"
	document.getElementById("deliveryError").style.display="none";


	while(discrepancyFormsCounter > 0){
			$("#delete-report-row-button").click();
			$("#addReportRowButton").show();
			$("#delete-button-spawner").addClass('hidden').show();
			$('#discrepancy-form-0').find('input').val("");
			//$("#materialNumber").reset();
			//$("qtyWithDelIssue").value('');
			//$("expectedQty").value('');
			//$("qtyReceived").value('');
		}
	
		$('#discrepancy-form-0').find('input').val("");


	// document.getElementById("menu3submenu2").classList.remove("menu-below-error");
	// document.getElementById("menu3sub1login").style.display="none"
		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("accFooter").style.display="none";
		document.getElementById("cicFooter").style.display="none";
		document.getElementById("productFooter").style.display="none";
		document.getElementById("consumerFooter").style.display="none";
		document.getElementById("adminFooter").style.display="none";
	/* for loop to display all buttons and their divs*/
	if (contactUsFormTypeNumber === "1"){
		for (let i = 1; i < 6; i++){
			var x = "menu1sub" + i.toString();
			var y = "menu1sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}
	if (contactUsFormTypeNumber === "2"){
		for (let i = 1; i < 7; i++){
			var x = "menu2sub" + i.toString();
			var y = "menu2sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}
	if (contactUsFormTypeNumber === "3"){
		for (let i = 1; i < 6; i++){
			var x = "menu3sub" + i.toString();
			var y = "menu3sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}
	if (contactUsFormTypeNumber === "4"){
		for (let i = 1; i < 4; i++){
			var x = "menu4sub" + i.toString();
			var y = "menu4sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}
	if (contactUsFormTypeNumber === "5"){
		for (let i = 1; i < 4; i++){
			var x = "menu5sub" + i.toString();
			var y = "menu5sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}
	if (contactUsFormTypeNumber === "7"){
		for (let i = 1; i < 4; i++){
			var x = "menu7sub" + i.toString();
			var y = "menu7sub" + i.toString() + "button";
			document.getElementById(x).style.display="block";
			document.getElementById(y).classList.remove("selected-button");
			}
	}


document.getElementById("defaultcontactform").style.display="none";
document.getElementById("sendMessageHeader").style.display="none";


document.getElementById("defaultFieldOne").style.display="block";
document.getElementById("defaultFieldTwo").style.display="block";
document.getElementById("linkFieldOne").style.display="none";
document.getElementById("linkFieldTwoAlb").style.display="none";
document.getElementById("linkFieldThree").style.display="none";
document.getElementById("linkFieldFour").style.display="none";
ACC.contactus.hideExtraFields();



$('html').unblock();


});
















/////////////////////////////// Submenu: Select subtype ///////////////////////////////

$(document).on("click", "#menu1sub1", function() {
	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
	contactUsEnquirySubType.val("LOGIN_ISSUE");
	document.getElementById("sendMessageHeader").style.display="block";
/* available for anonymous, no force log in */
	var y = document.getElementById("menu1sub1").id;
	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}

	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu1sub1button").classList.add("selected-button");
	$('html').unblock();
	}
	document.getElementById("menu2sub1login").style.display="none"
	document.getElementById("menu2sub2login").style.display="none"
	document.getElementById("menu2sub3login").style.display="none"
	document.getElementById("menu3sub4login").style.display="none"
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
});

$(document).on("click", "#menu1sub2", function() {
contactUsEnquirySubType.val("ORDERING_ISSUE");
/* not available for anonymous, no force log in */
document.getElementById("sendMessageHeader").style.display="block";

document.getElementById("accFooter").style.display="block";
document.getElementById("equipmentContactUs").style.display="block";
document.getElementById("menu1sub2").classList.remove("mid-option");

	var y = document.getElementById("menu1sub2").id;
	enquiry_sub_type = "ORDERING_ISSUE"

	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu1sub2button").classList.add("selected-button");
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");
	document.getElementById("menu2sub1login").style.display="none"
	document.getElementById("menu2sub2login").style.display="none"
	document.getElementById("menu2sub3login").style.display="none"
	document.getElementById("menu3sub4login").style.display="none"
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu1sub3", function() {
contactUsEnquirySubType.val("PAYMENT_ISSUE");

document.getElementById("accFooter").style.display="block";
document.getElementById("equipmentContactUs").style.display="block";
/* notavailable for anonymous, no force log in */
document.getElementById("sendMessageHeader").style.display="block";

	var y = document.getElementById("menu1sub3").id;
	enquiry_sub_type = "PAYMENT_ISSUE"

	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";		
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu1sub3button").classList.add("selected-button");

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu1sub4", function() {
	contactUsEnquirySubType.val("SUGGESTIONS");
	document.getElementById("sendMessageHeader").style.display="block";

	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
	var y = document.getElementById("menu1sub4").id;
	enquiry_sub_type = "SUGGESTIONS"

	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu1sub4button").classList.add("selected-button");
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");
	document.getElementById("menu2sub1login").style.display="none"
	document.getElementById("menu2sub2login").style.display="none"
	document.getElementById("menu2sub3login").style.display="none"
	document.getElementById("menu3sub4login").style.display="none"
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu1sub5", function() {
	contactUsEnquirySubType.val("OTHER");
	document.getElementById("sendMessageHeader").style.display="block";

	document.getElementById("accFooter").style.display="block";	
	document.getElementById("equipmentContactUs").style.display="block";
	document.getElementById("menu1sub5").classList.remove("mid-option");

	var y = document.getElementById("menu1sub5").id;
	enquiry_sub_type = "OTHER"
	for (let i = 1; i < 6; i++){
		var x = "menu1sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}

		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("menuoption6login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="none"
		document.getElementById("defaultcontactform").style.display="block";
		document.getElementById("subtypereselect").style.display="block";
		document.getElementById("menu1sub5button").classList.add("selected-button");
	$('html').unblock();
	}
});

$(document).on("click", "#menu2sub1", function() {

	if (!contactUsIsLoggedIn){
		document.getElementById("menu2sub1login").style.display="block";
		document.getElementById("menu2submenu2").classList.add("menu-below-error");
		 document.getElementById("globalErrorMessage").style.display="block";
		 document.getElementById("menu2sub2login").style.display="none";
		document.getElementById("menu2sub3login").style.display="none";

	}
	else{
		ACC.contactus.showExtraFields();
		document.getElementById("cicFooter").style.display="block";
		document.getElementById("row1field3").style.display="none";
		document.getElementById("row1field4").style.display="none";
		document.getElementById("row1field1").style.display="block";
		document.getElementById("row1field2").style.display="block";
		document.getElementById("row1field5").style.display="none";
		document.getElementById("row1field6").style.display="none";
		document.getElementById("delivery-browse-responsive").style.display="block";

		contactUsEnquirySubType.val("DAMAGED_PRODUCTS");
		var y = document.getElementById("menu2sub1").id;
		for (let i = 1; i < 7; i++){
			var x = "menu2sub" + i.toString();
			if (x != y){
				document.getElementById(x).style.display="none";
			}	
		document.getElementById("subtypereselect").style.display="block";
		document.getElementById("menu2sub1button").classList.add("selected-button");
		document.getElementById("sendMessageHeader").style.display="block";
		document.getElementById("defaultcontactform").style.display="block";
		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("menuoption6login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="none"

	};




	}

	$('html').unblock();
	
});

$(document).on("click", "#menu2sub2", function() {
	if (!contactUsIsLoggedIn){
		document.getElementById("menu2sub2login").style.display="block";
		 document.getElementById("menu2sub1login").style.display="none";
		document.getElementById("menu2sub3login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="block";
		document.getElementById("menu2submenu2").classList.add("menu-below-error");

	}
	else{
		document.getElementById("cicFooter").style.display="block";
		document.getElementById("menu2sub2").classList.remove("mid-option");
		ACC.contactus.showExtraFields();
		document.getElementById("cicFooter").style.display="block";
        document.getElementById("row1field3").style.display="none";
        document.getElementById("row1field4").style.display="block";
        document.getElementById("row1field1").style.display="block";
        document.getElementById("row1field2").style.display="none";
        document.getElementById("row1field5").style.display="block";
        document.getElementById("row1field6").style.display="none";
		contactUsEnquirySubType.val("INCORRECT_PRODUCTS");
		var y = document.getElementById("menu2sub2").id;
		for (let i = 1; i < 7; i++){
			var x = "menu2sub" + i.toString();
			if (x != y){
				document.getElementById(x).style.display="none";
			};
			document.getElementById("subtypereselect").style.display="block";
			document.getElementById("menu2sub2button").classList.add("selected-button");
			document.getElementById("menu2sub2").classList.add("mid-option");

			document.getElementById("sendMessageHeader").style.display="block";
			document.getElementById("defaultcontactform").style.display="block"; //change to invoice discrepancy
			document.getElementById("menu3sub4login").style.display="none"
			document.getElementById("menuoption6login").style.display="none";
			document.getElementById("globalErrorMessage").style.display="none"
			document.getElementById("defaultcontactform").style.display="block"
			document.getElementById("delivery-browse-responsive").style.display="block";

	};

	}
	$('html').unblock();
});

$(document).on("click", "#menu2sub3", function() {


	if (!contactUsIsLoggedIn){
		document.getElementById("menu2sub3login").style.display="block";
		document.getElementById("menu2submenu2").classList.add("menu-below-error");
		document.getElementById("globalErrorMessage").style.display="block";
		document.getElementById("menu2sub1login").style.display="none";
		document.getElementById("menu2sub2login").style.display="none";
		
	}
	else{
		document.getElementById("cicFooter").style.display="block";
		ACC.contactus.showExtraFields();
		document.getElementById("cicFooter").style.display="block";
		document.getElementById("row1field3").style.display="none";
		document.getElementById("row1field4").style.display="block";
		document.getElementById("row1field1").style.display="block";
		document.getElementById("row1field2").style.display="none";
		document.getElementById("row1field5").style.display="block";
		document.getElementById("row1field6").style.display="none";
		contactUsEnquirySubType.val("WRONG_QTY");

		document.getElementById("defaultcontactform").style.display="block"
		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("menuoption6login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="none"
		document.getElementById("sendMessageHeader").style.display="block";
		var y = document.getElementById("menu2sub3").id;

		for (let i = 1; i < 7; i++){
			var x = "menu2sub" + i.toString();
			if (x != y){
				document.getElementById(x).style.display="none";
			}
		
		document.getElementById("menu2sub3").classList.remove("menu-button-right");
		document.getElementById("subtypereselect").style.display="block";
		document.getElementById("menu2sub3button").classList.add("selected-button");
		document.getElementById("sendMessageHeader").style.display="block";
		document.getElementById("delivery-browse-responsive").style.display="block";

		};

	}
	$('html').unblock();
			
		});
	
$(document).on("click", "#menu2sub4", function() {
	contactUsEnquirySubType.val("PROD_WITH_QUAL_ISSUE");

	document.getElementById("sendMessageHeader").style.display="block";
	document.getElementById("menu2sub1login").style.display="none";
	document.getElementById("menu2sub2login").style.display="none";
	document.getElementById("menu2sub3login").style.display="none";
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");
	document.getElementById("consumerFooter").style.display="block";

	var y = document.getElementById("menu2sub4").id;

	for (let i = 1; i < 7; i++){
		var x = "menu2sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu2sub4button").classList.add("selected-button");
	document.getElementById("menu2sub1login").style.display="none";
	document.getElementById("menu2sub2login").style.display="none";
	document.getElementById("menu2sub3login").style.display="none";
	document.getElementById("menu3sub4login").style.display="none";
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});
	
$(document).on("click", "#menu2sub5", function() {
	contactUsEnquirySubType.val("WHEN_ORDER_DEL");
	document.getElementById("cicFooter").style.display="block";
	document.getElementById("menu2sub5").classList.remove("mid-option");

	document.getElementById("sendMessageHeader").style.display="block";
	document.getElementById("menu2sub1login").style.display="none";
	document.getElementById("menu2sub2login").style.display="none";
	document.getElementById("menu2sub3login").style.display="none";
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");

	var y = document.getElementById("menu2sub5").id;

	for (let i = 1; i < 7; i++){
		var x = "menu2sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu2sub5button").classList.add("selected-button");
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});


$(document).on("click", "#menu2sub6", function() {
	contactUsEnquirySubType.val("OTHER");
	document.getElementById("cicFooter").style.display="block";

	document.getElementById("sendMessageHeader").style.display="block";
	document.getElementById("menu2sub1login").style.display="none";
	document.getElementById("menu2sub2login").style.display="none";
	document.getElementById("menu2sub3login").style.display="none";
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");

	var y = document.getElementById("menu2sub6").id;
	document.getElementById("menu2submenu2").classList.remove("menu-below-error");

	for (let i = 1; i < 7; i++){
		var x = "menu2sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("menu2sub6").classList.remove("menu-button-right");
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu2sub6button").classList.add("selected-button");

	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu3sub1", function() {
	contactUsEnquirySubType.val("ACC_ENQUIRY");

	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
	document.getElementById("sendMessageHeader").style.display="block";
	// document.getElementById("menu3sub1login").style.display="block";
	document.getElementById("defaultcontactform").style.display="block";
	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != "menu3sub1"){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu3sub1button").classList.add("selected-button");
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	}

$('html').unblock();

});

$(document).on("click", "#menu3sub2", function() {
	contactUsEnquirySubType.val("UPDATE_ACC");

	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
		document.getElementById("menu3sub2").classList.remove("mid-option");

	document.getElementById("sendMessageHeader").style.display="block";

			var y = document.getElementById("menu3sub2").id;
	
	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu3sub2button").classList.add("selected-button");

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu3sub3", function() {
	contactUsEnquirySubType.val("DEACT_EXISTING_ACC");
	document.getElementById("sendMessageHeader").style.display="block";
	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";

	var y = document.getElementById("menu3sub3").id;

	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("menu3sub3").classList.remove("menu-button-right");
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu3sub3button").classList.add("selected-button");

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	$('html').unblock();
	}
});

$(document).on("click", "#menu3sub4", function() {

	if (!contactUsIsLoggedIn){
		document.getElementById("menu3sub4login").style.display="block";
		 document.getElementById("globalErrorMessage").style.display="block";
	}
	else{
		document.getElementById("adminFooter").style.display="block";
		contactUsEnquirySubType.val("REQ_PAYMENT_PLAN");
		document.getElementById("sendMessageHeader").style.display="block";

	var y = document.getElementById("menu3sub4").id;

	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu3sub4button").classList.add("selected-button");

	}}
	$('html').unblock();
});

$(document).on("click", "#menu3sub5", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("OTHER");

	document.getElementById("accFooter").style.display="block";	
	document.getElementById("equipmentContactUs").style.display="block";
	document.getElementById("menu3sub5").classList.remove("mid-option");

	var y = document.getElementById("menu3sub5").id;

	for (let i = 1; i < 6; i++){
		var x = "menu3sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}

	document.getElementById("menu3sub4login").style.display="none"
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu3sub5button").classList.add("selected-button");

	$('html').unblock();
	}
});

$(document).on("click", "#menu4sub1", function() {
	contactUsEnquirySubType.val("LIFESTYLE_CUSTOMER");

	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="none";
	document.getElementById("sendMessageHeader").style.display="block";

	document.getElementById("defaultFieldOne").style.display="none";
	document.getElementById("defaultFieldTwo").style.display="none";

	document.getElementById("linkFieldOne").style.display="block";
	document.getElementById("linkFieldTwoAsahi").style.display="block";
	document.getElementById("linkFieldThree").style.display="block";
	document.getElementById("linkFieldFour").style.display="block";


	var y = document.getElementById("menu4sub1").id;

	for (let i = 1; i < 4; i++){
		var x = "menu4sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}

		document.getElementById("menu3sub4login").style.display="none"
		document.getElementById("menuoption6login").style.display="none";
		document.getElementById("globalErrorMessage").style.display="none"
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu4sub1button").classList.add("selected-button");
	document.getElementById("defaultcontactform").style.display="block";

	$('html').unblock();
	}
});

$(document).on("click", "#menu4sub2", function() {

	document.getElementById("accFooter").style.display="block";	
	document.getElementById("equipmentContactUs").style.display="block";
	document.getElementById("menu4sub2").classList.remove("mid-option");

	contactUsEnquirySubType.val("CREATE_ONLINE_ACC");
	document.getElementById("sendMessageHeader").style.display="block";
	document.getElementById("defaultFieldOne").style.display="none";
	document.getElementById("defaultFieldTwo").style.display="none";
	document.getElementById("linkFieldTwoAsahi").style.display="none";

	document.getElementById("linkFieldOne").style.display="block";
	document.getElementById("linkFieldTwoAlb").style.display="block";
	document.getElementById("linkFieldThree").style.display="block";
	document.getElementById("linkFieldFour").style.display="block";
	
	var y = document.getElementById("menu4sub2").id;

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	for (let i = 1; i < 4; i++){
		var x = "menu4sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
		document.getElementById("defaultcontactform").style.display="block";

	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu4sub2button").classList.add("selected-button");

	$('html').unblock();
	}
});

$(document).on("click", "#menu4sub3", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("OTHER");
	document.getElementById("accFooter").style.display="block";
	document.getElementById("equipmentContactUs").style.display="block";
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu4sub3").id;

	for (let i = 1; i < 4; i++){
		var x = "menu4sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("menu4sub3").classList.remove("menu-button-right");
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu4sub3button").classList.add("selected-button");

	$('html').unblock();
	}
});

$(document).on("click", "#menu5sub1", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("CHANGE_ORDER");
	document.getElementById("cicFooter").style.display="block";

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu5sub1").id;

	for (let i = 1; i < 4; i++){
		var x = "menu5sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu5sub1button").classList.add("selected-button");

	$('html').unblock();
}
});

$(document).on("click", "#menu5sub2", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("CANCEL_ORDER");
	document.getElementById("cicFooter").style.display="block";	document.getElementById("menu5sub2").classList.remove("mid-option");

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu5sub2").id;

	for (let i = 1; i < 4; i++){
		var x = "menu5sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu5sub2button").classList.add("selected-button");

	$('html').unblock();
	}
});

$(document).on("click", "#menu5sub3", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("OTHER");

	document.getElementById("cicFooter").style.display="block";
	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu5sub3").id;

	for (let i = 1; i < 4; i++){
		var x = "menu5sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("menu5sub3").classList.remove("menu-button-right");
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu5sub3button").classList.add("selected-button");

	$('html').unblock();
	}
});


$(document).on("click", "#menu7sub1", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("PRODUCT_INFO");
	document.getElementById("productFooter").style.display="block";

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu7sub1").id;

	for (let i = 1; i < 4; i++){
		var x = "menu7sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu7sub1button").classList.add("selected-button");

	$('html').unblock();
	}
});

$(document).on("click", "#menu7sub2", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("STOCK_AVAIL");

	document.getElementById("cicFooter").style.display="block";
	document.getElementById("productFooter").style.display="none";
	document.getElementById("menu7sub2").classList.remove("mid-option");

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
	var y = document.getElementById("menu7sub2").id;

	for (let i = 1; i < 4; i++){
		var x = "menu7sub" + i.toString();
		if (x != y){
			document.getElementById(x).style.display="none";
		}
	
	document.getElementById("defaultcontactform").style.display="block";
	document.getElementById("subtypereselect").style.display="block";
	document.getElementById("menu7sub2button").classList.add("selected-button");


	$('html').unblock();
}
});

$(document).on("click", "#menu7sub3", function() {
	document.getElementById("sendMessageHeader").style.display="block";
	contactUsEnquirySubType.val("OTHER");
	document.getElementById("productFooter").style.display="block";

	document.getElementById("menuoption6login").style.display="none";
	document.getElementById("globalErrorMessage").style.display="none"
var y = document.getElementById("menu7sub3").id;

for (let i = 1; i < 4; i++){
	var x = "menu7sub" + i.toString();
	if (x != y){
		document.getElementById(x).style.display="none";
	}
}

document.getElementById("defaultcontactform").style.display="block";
document.getElementById("menu5sub3").classList.remove("menu-button-right");
document.getElementById("subtypereselect").style.display="block";
document.getElementById("menu7sub3button").classList.add("selected-button");

$('html').unblock();

});






/////////////////////////////// Discrepancy Form ///////////////////////////////

var discrepancyFormsCounter = 0;
//var forms = []; // variable to hold input

    $('#add-report-row-button').on('click', function() { 
		discrepancyFormsCounter++;  //counter of entries in discrepancy form increases
		var $temp_address=$('#discrepancy-form-0').clone(true); //clones first discrepancy and retains ID
		$temp_address.attr('id','discrepancy-form-'+discrepancyFormsCounter);
		$temp_address.find("input").val("");
		$temp_address.appendTo('.discrepancy-forms-wrapper');
		$temp_address.find("textarea").val("");
		$temp_address.find('input[id="materialNumber"]').attr('name','discrepancies['+discrepancyFormsCounter+'].materialNumber');
		$temp_address.find('input[id="qtyWithDelIssue"]').attr('name','discrepancies['+discrepancyFormsCounter+'].qtyWithDelIssue');
		$temp_address.find('input[id="expectedTotalPay"]').attr('name','discrepancies['+discrepancyFormsCounter+'].expectedTotalPay');
		$temp_address.find('input[id="expectedQty"]').attr('name','discrepancies['+discrepancyFormsCounter+'].expectedQty');
		$temp_address.find('input[id="qtyReceived"]').attr('name','discrepancies['+discrepancyFormsCounter+'].qtyReceived');
		$temp_address.find('input[id="materialNumber"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="qtyWithDelIssue"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="expectedTotalPay"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="expectedQty"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="qtyReceived"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="actualTotalPay"]').removeClass("discrepancy-field-error");
		$temp_address.find(".plus-button").hide();
		$temp_address.find("#delete-button-spawner").removeClass('hidden').show();
		$('html').unblock();


    });
    $('#add-report-row-button-responsive').on('click', function() { // on click of the + button
		//or mobile button click
		discrepancyFormsCounter++;  //counter of entries in discrepancy form increases
		var $temp_address=$('#discrepancy-form-0').clone(true); //clones first discrepancy and retains ID
		$temp_address.attr('id','discrepancy-form-'+discrepancyFormsCounter);
		$temp_address.find("input").val("");
		$temp_address.appendTo('.discrepancy-forms-wrapper');
		$temp_address.find("textarea").val("");
		$temp_address.find('input[id="materialNumber"]').attr('name','discrepancies['+discrepancyFormsCounter+'].materialNumber');
		$temp_address.find('input[id="qtyWithDelIssue"]').attr('name','discrepancies['+discrepancyFormsCounter+'].qtyWithDelIssue');
		$temp_address.find('input[id="expectedTotalPay"]').attr('name','discrepancies['+discrepancyFormsCounter+'].expectedTotalPay');
		$temp_address.find('input[id="expectedQty"]').attr('name','discrepancies['+discrepancyFormsCounter+'].expectedQty');
		$temp_address.find('input[id="materialNumber"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="qtyWithDelIssue"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="expectedTotalPay"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="expectedQty"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="qtyReceived"]').removeClass("discrepancy-field-error");
		$temp_address.find('input[id="actualTotalPay"]').removeClass("discrepancy-field-error");
		$temp_address.find(".plus-button").hide();
		$temp_address.find("#delete-button-spawner").removeClass('hidden').show();
		$('html').unblock();

	
		});

    $('#delete-report-row-button').on('click',function(){ //on click of the button
	  var lastRow = $(this).parents('.discrepancy-form');
	  
	  var lastRowId = lastRow.attr('id');
	  var n = lastRowId.lastIndexOf('-');
	  var result = lastRowId.substring(n + 1);
	  if (result!= discrepancyFormsCounter){

		ACC.contactus.fixBelowForms(discrepancyFormsCounter, parseInt(result))
	  }
      $(this).parents(".discrepancy-form").remove(); //deletes the latest form element
	  discrepancyFormsCounter--;
	  $('html').unblock();

	});

	$('#delete-report-row-responsive').on('click',function(){ //on click of the button
		var lastRow = $(this).parents('.discrepancy-form');
		var lastRowId = lastRow.attr('id');
		var n = lastRowId.lastIndexOf('-');
		var result = lastRowId.substring(n + 1);
		if (result!= discrepancyFormsCounter){
			ACC.contactus.fixBelowForms(discrepancyFormsCounter, parseInt(result))
		  }		
		$(this).parents(".discrepancy-form").remove(); //deletes the latest form element
		discrepancyFormsCounter--;
		$('html').unblock();
	  });








/////////////////////////////// Get Invoice ///////////////////////////////

	$(document).on("click", "#deliverybrowsebutton", function () {
		var deliveryNumber = $("#deliveryNumber").val();
		var deliveryNumberLength = deliveryNumber.toString().length;
		if (deliveryNumberLength === 10 && deliveryNumber.indexOf("08")==0){
			ACC.contactus.getInvoice();
			console.log("valid");
			$('#deliveryError').css("display", "none");
			$('html').unblock();

		}
		else{
			console.log("invalid");
			document.getElementById("deliveryError").style.display="block";
			$('#deliveryNumber').addClass("discrepancy-field-error");
			$('html').unblock();
		}

	});

	$(document).on("click", "#deliverybrowsebuttonresponsive", function () {
		var deliveryNumber = $("#deliveryNumber").val();
		var deliveryNumberLength = deliveryNumber.toString().length;
		if (deliveryNumberLength === 10 && deliveryNumber.indexOf("08")==0){
			ACC.contactus.downloadInvoice();
			console.log("valid");
			$('#deliveryError').css("display", "none");
			$('html').unblock();

		}
		else{
			document.getElementById("deliveryError").style.display="block";
			$('html').unblock();

		}

	});

	 $(document).on("click", "#formSubmitButton", function (event) {

		var nameBool = true;
		var emailAddressBool = true;
		var numberBool = true;
		var deliveryNumberBool = true;
		var materialNumberBool = true;
		var expectedChargeBool = true;
		var actualChargeBool = true;
		var delIssueBool = true;
		var expectedQtyBool = true;

		// enquiry/message validation
		if((/^$/.test(furtherdetail.value)) && ((contactUsEnquiryType.val() === "MANAGE_ACC") || (contactUsEnquiryType.val() === "WEBSITE_SUPPORT") || (contactUsEnquiryType.val() === "REG_SUPPORT") || (contactUsEnquiryType.val() === "AMEND_AN_ORDER") || (contactUsEnquiryType.val() === "ASSISTANCE") || (contactUsEnquiryType.val() === "OTHER"))){		
			event.preventDefault();
			document.getElementById('furtherdetail').style.borderColor='#AE423C';
			$(furtherDetailError).css("display", "block");
			$(globalErrorMessage).css("display", "block");
			$('html').unblock();

		}
		else{
			document.getElementById('furtherdetail').style.borderColor='inherit';
			$(furtherDetailError).css("display", "none");

		}

		//name validation
		if((/^$/.test(contactUsName.val()))){
			event.preventDefault();
			nameBool = false;
			$('#name').addClass("discrepancy-field-error");
			$(nameError).css("display", "block");
			$(globalErrorMessage).css("display", "block");
			$('html').unblock();

		}
		else{
			nameBool = true;
			$('#name').removeClass("discrepancy-field-error");
			$(nameError).css("display", "none");

		}
		//email validation
		if(!(/\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,4}\b/.test(contactUsEmailAddress.val()))){
			event.preventDefault();
			emailAddressBool = false;
			$(emailAddress).addClass("discrepancy-field-error");
			$(emailAddressError).css("display", "block");
			$(globalErrorMessage).css("display", "block");
			$('html').unblock();

		}
		else{
			emailAddressBool = true;
			$(emailAddress).removeClass("discrepancy-field-error");
			$(emailAddressError).css("display", "none");

		}

		//delivery number validation
	 	if(!(/08\d{8}/.test(contactUsDeliveryNumber.val())) && ((contactUsEnquiryType.val() === "INCORRECT_CHARGE") || (contactUsEnquirySubType.val() === "DAMAGED_PRODUCTS") || (contactUsEnquirySubType.val() === "INCORRECT_PRODUCTS") || (contactUsEnquirySubType.val() === "WRONG_QTY"))){
			event.preventDefault();
			deliveryNumberBool = false;
			$(deliveryNumber).addClass("discrepancy-field-error");
			$(deliveryError).css("display", "block");
			$(globalErrorMessage).css("display", "block");
			$('html').unblock();
		}
		else{
			deliveryNumberBool = true;
			$(deliveryNumber).removeClass("discrepancy-field-error");
			$(deliveryError).css("display", "none");

		}

		//contact number validation
		if(!(/^(?:\+?(61))? ?(?:\((?=.*\)))?(0?[2-57-8])\)? ?(\d\d(?:[- ](?=\d{3})|(?!\d\d[- ]?\d[- ]))\d\d[- ]?\d[- ]?\d{3})$/.test(contactUsContactNumber.val()))){
			event.preventDefault();
			numberBool = false;
			$(contactNumber).addClass("discrepancy-field-error");
			$(contactNumberError).css("display", "block");
			$(globalErrorMessage).css("display", "block");
			$('html').unblock();

		}
		else{
			numberBool = true;
			$(contactNumber).removeClass("discrepancy-field-error");
			$(contactNumberError).css("display", "none");

		}

		
		//materialNumber field validation

		if((contactUsEnquiryType.val() === "INCORRECT_CHARGE") || (contactUsEnquirySubType.val() === "DAMAGED_PRODUCTS" || contactUsEnquirySubType.val() === "INCORRECT_PRODUCTS") || (contactUsEnquirySubType.val() === "WRONG_QTY")){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(materialNumber.value)){
					materialNumberBool = true;
					$("#materialNoError").css("display", "none");
					$(materialNumber).removeClass("discrepancy-field-error");
				}
				else{
					event.preventDefault();
					materialNumberBool = false;
					$("#materialNoError").css("display", "block");
					$(materialNumber).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(i=0; i < discrepancyFormsCounter+1; i++){
					if($.isNumeric(materialNumber[i].value)){
						materialNumberBool = true;
						$("#materialNoError").css("display", "none");
						$(materialNumber).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						materialNumberBool = false;
						$("#materialNoError").css("display", "block");
						$(materialNumber[i]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
					}
				}
			}
		}

		//expected charge field validation
		if(contactUsEnquiryType.val() === "INCORRECT_CHARGE"){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(expectedTotalPay.value)){
					expectedChargeBool = true;
					$("#expectedChargeError").css("display", "none");
					$(expectedTotalPay).removeClass("discrepancy-field-error");
				}
				else{
					event.preventDefault();
					expectedChargeBool = false;
					$("#expectedChargeError").css("display", "block");
					$(expectedTotalPay).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(j=0; j < discrepancyFormsCounter+1; j++){
					if($.isNumeric(expectedTotalPay[j].value)){
						expectedChargeBool = true;
						$("#expectedChargeError").css("display", "none");
						$(expectedTotalPay).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						expectedChargeBool = false;
						$("#expectedChargeError").css("display", "block");
						$(expectedTotalPay[j]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
						
					}
				}
			}
		}


		//actual charge field validation
		if(contactUsEnquiryType.val() === "INCORRECT_CHARGE"){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(actualTotalPay.value)){
					actualChargeBool = true;
					$("#actualChargeError").css("display", "none");
					$(actualTotalPay).removeClass("discrepancy-field-error");
				}
				else{
					event.preventDefault();
					actualChargeBool = false;
					$("#actualChargeError").css("display", "block");
					$(actualTotalPay).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(i=0; i < discrepancyFormsCounter+1; i++){
					if($.isNumeric(actualTotalPay[i].value)){
						actualChargeBool = true;
						$("#actualChargeError").css("display", "none");
						$(actualTotalPay).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						actualChargeBool = false;
						$("#actualChargeError").css("display", "block");
						$(actualTotalPay[i]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
					}
				}
			}
		}

		//delissue field validation
		if((contactUsEnquirySubType.val() === "DAMAGED_PRODUCTS")){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(qtyWithDelIssue.value)){
					delIssueBool = true;
					$("#delIssueError").css("display", "none");
					$(qtyWithDelIssue).removeClass("discrepancy-field-error");
				}
				else{
					event.preventDefault();
					delIssueBool = false;
					$("#delIssueError").css("display", "block");
					$(qtyWithDelIssue).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(i=0; i < discrepancyFormsCounter+1; i++){
					if($.isNumeric(qtyWithDelIssue[i].value)){
						delIssueBool = true;
						$("#delIssueError").css("display", "none");
						$(qtyWithDelIssue).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						delIssueBool = false;
						$("#delIssueError").css("display", "block");
						$(qtyWithDelIssue[i]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
					}
				}
			}
		}


		//expectedQty field validation
		if((contactUsEnquirySubType.val() === "WRONG_QTY") || (contactUsEnquirySubType.val() === "INCORRECT_PRODUCTS")){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(expectedQty.value)){
					expectedQtyBool = true;
					$(qtyExpectedError).css("display", "none");
					$(expectedQty).removeClass("discrepancy-field-error");

				}
				else{
					event.preventDefault();
					expectedQtyBool = false;
					$("#qtyExpectedError").css("display", "block");
					$(expectedQty).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(i=0; i < discrepancyFormsCounter+1; i++){
					if($.isNumeric(expectedQty[i].value)){
						expectedQtyBool = true;
						$("#qtyExpectedError").css("display", "none");
						$(expectedQty).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						expectedQtyBool = false;
						$("#qtyExpectedError").css("display", "block");
						$(expectedQty[i]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
					}
				}
			}
		}

		//qtyReceived field validation
		if((contactUsEnquirySubType.val() === "WRONG_QTY") || (contactUsEnquirySubType.val() === "INCORRECT_PRODUCTS")){
			if(discrepancyFormsCounter === 0){
				if($.isNumeric(qtyReceived.value)){
					qtyReceivedBool = true;
					$("#qtyReceivedError").css("display", "none");
					$(qtyReceived).removeClass("discrepancy-field-error");
				}
				else{
					event.preventDefault();
					qtyReceivedBool = false;
					$("#qtyReceivedError").css("display", "block");
					$(qtyReceived).addClass("discrepancy-field-error");
					$(globalErrorMessage).css("display", "block");
				}
			}
			else{
				for(i=0; i < discrepancyFormsCounter+1; i++){
					if($.isNumeric(qtyReceived[i].value)){
						qtyReceivedBool = true;
						$("#qtyReceivedError").css("display", "none");
						$(qtyReceived).removeClass("discrepancy-field-error");
					}
					else{
						event.preventDefault();
						qtyReceivedBool = false;
						$("#qtyReceivedError").css("display", "block");
						$(qtyReceived[i]).addClass("discrepancy-field-error");
						$(globalErrorMessage).css("display", "block");
					}
				}
			}
		}

			
});



$('#pdfFile').on('change', function() { 
	if(pdfFile.value !== ''){
		if(!(this.files[0].size <= 5242880)){	//add ! sign before condition
			this.value='';
			$('#fileTooBigError').css("display", "block");
		}
		else{
			$('#fileTooBigError').css("display", "none");
		}
	}

	if(pdfFile.value !== ''){
		const lastDot = pdfFile.value.lastIndexOf('.');
		const ext = pdfFile.value.substring(lastDot + 1);
		
		if(!(ext === 'pdf' || ext ==='jpg'|| ext === 'jpeg' || ext === 'gif' || ext === 'png' || ext === 'PDF' || ext ==='JPG'|| ext === 'JPEG' || ext === 'GIF' || ext === 'PNG')){
			this.value='';
			$('#invalidFileError').css("display", "block");
		}
		else{
			$('#invalidFileError').css("display", "none");
		}
	}
	else{
		$('html').unblock();
	}


});
