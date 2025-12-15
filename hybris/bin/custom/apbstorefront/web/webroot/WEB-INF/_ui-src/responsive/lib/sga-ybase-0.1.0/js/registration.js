ACC.registration = {
	/**
	 * Change event for customer registration 
	 * then form appear different for Yes or no 
	 */
	activateRegistrationForm : function() {
		$(document).on("change", "#customer-select", function(e) {
			e.preventDefault();
			var optVal = $("#customer-select option:selected").val();
			
			if (optVal == 'true') {
				$("#customerType").val(optVal);
				$("#registerCustomerTypeYes").val(optVal);
				$("#registerCustomerTypeYes").prop('checked', true);
				$("#self-registration").show();
				
				// sga site check and request registration
				if($('#sgaSite').length) {
					  $("#sga-request-registration").hide();
					  
					}
				else{
					
					$("#request-registration").hide();
					
				}
				
                $("#termCondOther").attr("href", "/storefront/apb/en/AUD/termsAndLegal");
                $("#termLondOther").removeClass("hidden");
				$("#termCondOther").addClass("hidden");
			}
			else if (optVal == 'false') {
				
				$("#customerType").val(optVal);
				$("#registerCustomerTypeNo").val(optVal);
				$("#registerCustomerTypeNo").prop('checked', false);
				// sga site check and request registration
				if($('#sgaSite').length) {
					 $("#sga-request-registration").show();
					}
				else{
					$("#request-registration").show();
					
				}
				$("#self-registration").hide();
			}
			else if (optVal == '') {
				$("#self-registration").hide();
				$("#request-registration").hide();
			}
		});
		
		$('.access-type-radio').change(function() {
        	var activeCheckbox =  $(this).attr("name");

			if (activeCheckbox === "orderOnly") {
				$("#payOnly").prop('checked', false);
				$("#orderAndPay").prop('checked', false);

			} else if (activeCheckbox === "payOnly") {
				$("#orderOnly").prop('checked', false);
				$("#orderAndPay").prop('checked', false);

			} else if (activeCheckbox === "orderAndPay") {
				$("#orderOnly").prop('checked', false);
				$("#payOnly").prop('checked', false);
			}
		});
	},
	/* Registration Role */
	roleUser : function() {
		$(document).on("change", "#role", function(e) {
			if ($(this).val() == 'OTHER') {
				$("#registerRoleOther").show();
				$("#registerRoleOtherTemp").val("rolexxxx");
			} else {
				$("#registerRoleOther").hide();
				$("#registerRoleOtherTemp").val('');
			}
		});
	},
	/* Request Registration - handle radio button for trust */
	activateTrustForm : function() {
		$(document).on("click", "#applicantCarry", function(e) {
			if ($(this).val() == 'true') {
				$("#applicantCarryDiv").show();
			} else {
				$("#applicantCarryDiv").hide();
			}
		});
	},
	/* Request Registration - change on same invoice check box */
	sameInvoiceustForm : function() {
		 $('#sameasInvoiceAddress').click(function(){
			 populateDeliveryAddress();
			 setDataOnKeyUp();
		});
	},
	/* Request Registration -Liquor License Premises change on same as delivery address check box */
	sameDeliveryAddressLiquorLicenseForm : function() {
		 $('#sameasDeliveryAddressLPA').click(function(){
			 populateDeliveryAddressForLPA();
			 setDataOnKeyUpLPA();
		});
	},
	/* Request Registration - Add Another form */
	addAnotherForm : function() {
		var addAnother = $("#addAnotherValue").val();
		if(addAnother==='true')
			{
				$("#addAnotherForm").show();
				$("#addAnother").hide();
			}
		$(document).on("click", "#addAnother", function(e) 
			{
			$("#addAnotherForm").show();
			$("#addAnother").hide();
			$("#addAnotherValue").val(true);
			$("#addAnotherLink").prop('checked', true);
			return false;
		});
	},

	// Multi account registration
    addAccount: function () {
        var rows = $(".fields-wrapper").find(".fields-row");
        var copyRow = rows[0].cloneNode(true);
        var button = copyRow.querySelector(".textButton");
        var div = document.createElement('div');
        var currentTime = Date.now();
        var rowPrefix = "row-";
        var buttonPrefix = "button-";

        handleInputNames(copyRow, rows.length * 1);
        removeValidation(copyRow);

        button.id = buttonPrefix + currentTime;
        if (rows.length * 1 + 1 > 1) {
            rows[0].querySelector(".textButton").disabled = false;
            button.disabled = false
        }

        div.id = rowPrefix + currentTime;
        div.className = copyRow.classList.value
        div.innerHTML = copyRow.innerHTML;
        $(".fields-wrapper").append(div);
    },

    removeAccount: function (e) {
        var rowPrefix = "row-";
        var buttonPrefix = "button-";
        var digit = e.id.replace(buttonPrefix, '');
        var row = document.querySelector("#" + rowPrefix + digit);
        row.remove();

        // after removing row, re-sort input list
        const newRows = document.querySelectorAll(".fields-wrapper .fields-row");
        for(var i = 0; i < newRows.length * 1; i ++) {
            handleInputNames(newRows[i], i);
        }
        // if there is only one row, disable remove button
        newRows[0].querySelector(".textButton").disabled = (newRows.length * 1) === 1;
    },

};
$(document).ready(function() {
	with (ACC.registration) {
		activateRegistrationForm();
		roleUser();
		customerExists();
		roleOtherSet();
		activateTrustForm();
		sameInvoiceustForm();
		addAnotherForm();
		trustControl();
		populateDeliveryAddressOnLoad();
		lostPromtAlertMessage();
		setDataOnKeyUp();
		sameDeliveryAddressLiquorLicenseForm();
		populateDeliveryAddressOnLoadLPA();
		setDataOnKeyUpLPA();
	}
});

function customerExists() {
	var customer = $("#customerType").val();
	$("#customer-select").val(customer);
	var customerType = $("#customer-select option:selected").val();
	$("#customerType").val(customerType);
	if (customer === "true") {
		$("#self-registration").show();
		$("#request-registration").hide();
        $("#termCondOther").attr("href", "/storefront/apb/en/AUD/termsAndLegal");
        $("#termLondOther").removeClass("hidden");
		$("#termCondOther").addClass("hidden");
	}
	if (customerType === '') {
		$("#request-registration").hide();
		$("#self-registration").hide();
	}
	if (customer === "false") {
		$("#request-registration").show();
		$("#self-registration").hide();
	}
	var requestCustomer = $("#requestCustomerType").val();
	if (customer === "false" || requestCustomer === "false") {
		$("#customer-select").val(requestCustomer);
		$("#request-registration").show();
		$("#self-registration").hide();
	}
}

function roleOtherSet() {
	if ($("#registerRoleOther").val() === ''
			&& $("#role option:selected").val() === 'OTHER') {
		$("#registerRoleOther").show();
	}
}
function trustControl() {
	var trust = $('input[name=applicantCarry]:radio:checked');
	if (trust.val() === 'true') {
		$("#applicantCarryDiv").show();
	} else {
		$("#applicantCarryDiv").hide();
	}
}
function populateDeliveryAddress()
{
	if($('#sameasInvoiceAddress')[0]!=null && $('#sameasInvoiceAddress')[0].checked )
    {  
		deliveryDataSet();
		setStateToDeliveryAddress();
		$('#stateInvoice').change(function(){
			 $('#stateDelivery').val($(this).val()).prop('selected', true);
		});
	}
	else
		{
		 $("#shippingStreet").val('');
		 $("#shippingSuburb").val('');
		 $("#stateDelivery").val('');
		 $("#postcodeDelivery").val('');
		 $('#shippingStreet').attr('readonly', false);
		 $("#shippingSuburb").attr('readonly', false);
		 $("#postcodeDelivery").attr('readonly', false);
		 $("#stateDelivery").attr('readonly', false);
		}
}

function setStateToDeliveryAddress()
{
	var state = $("#stateInvoice :selected").val();
	$('#stateDelivery').val(state).prop('selected', true);
}




function populateDeliveryAddressForLPA()
{
	
	if($('#sameasDeliveryAddressLPA')[0]!=null && $('#sameasDeliveryAddressLPA')[0].checked )
    {  
		deliveryDataSetLPA();
	}
	else
		{
		$("#licensedPremisesAddress").val('');
		}
}

function populateDeliveryAddressOnLoad()
{
	
	if($('#sameasInvoiceAddress')[0]!=null && $('#sameasInvoiceAddress')[0].checked )
    {  
		deliveryDataSet();
	}
	else
		{
		 $("#shippingStreet").val();
		 $("#shippingSuburb").val();
		// $("#stateDelivery").val();
		 $("#postcodeDelivery").val();
		 $('#shippingStreet').attr('readonly', false);
		 $("#shippingSuburb").attr('readonly', false);
		 $("#postcodeDelivery").attr('readonly', false);
		}
}

function populateDeliveryAddressOnLoadLPA()
{
	if($('#sameasDeliveryAddressLPA')[0]!=null && $('#sameasDeliveryAddressLPA')[0].checked )
    {  
		deliveryDataSetLPA();
	}
	else
		{
		 $("#licensedPremisesAddress").val();
		}
}


function deliveryDataSet()
{
	var ss = $("#streetNumber").val()+" "+$("#streetName").val()+" "+$("#streetAbreviation").val()+" "+$("#unitNoShopNo").val()+"  "+$("#level").val();
	$("#shippingStreet").val(ss.trim());
	$("#shippingSuburb").val($("#suburb").val());  
	$("#postcodeDelivery").val($("#postcodeInvoice").val());
	$('#shippingStreet').attr('readonly', 'readonly');
	$("#shippingSuburb").attr('readonly', 'readonly');
	$("#stateDelivery").attr('readonly', 'readonly');
	$("#postcodeDelivery").attr('readonly', 'readonly');
}

function deliveryDataSetLPA()
{
	var ss = $("#shippingStreet").val()+", "+$("#shippingSuburb").val()+", ";
	var state = $("#stateDelivery :selected").text();
	var postCode = $("#postcodeDelivery").val();
	var deliveryAddress = ss.trim() + state.trim() + ", " + postCode.trim();
	$("#licensedPremisesAddress").val(deliveryAddress.trim());
}


function lostPromtAlertMessage()
{
	$('a').click(function(e) {
		var target = $(e.target);
		if(target.is('#termCondOther')){
			return true;
		}
		else{
			var formValue=null;
			var inputValue = "";
			$('#apbRequestRegisterForm input').each(
				    function(index){  
				        var input = $(this);
				        inputValue = inputValue + input.val()+",";
				    	}
				);
			if (inputValue.indexOf('false')>45) {
						return confirm("Warning! \n\nYour registration request data will be lost. Please click to confirm.");
			}
			
		}
		
	})
}
function setDataOnKeyUp()
 {
	$('.form-control-1').on(
			'input',
			function() {
				if ($('#sameasInvoiceAddress')[0] != null
						&& $('#sameasInvoiceAddress')[0].checked) {
					var allvals = $('.form-control-1').map(function() {
						return this.value;
					}).get().join(' ');
					$('.form-control2').val(allvals);
				}
			});
	if ($('#sameasInvoiceAddress')[0] != null
			&& $('#sameasInvoiceAddress')[0].checked) {
		$('#suburb').keyup(function() {
			$('#shippingSuburb').val($(this).val());
		});
		$('#postcodeInvoice').keyup(function() {
			$('#postcodeDelivery').val($(this).val());
		});
	}
}

function setDataOnKeyUpLPA()
{
	$('.form-control-1').on(
			'input',
			function() {
				if ($('#sameasDeliveryAddressLPA')[0] != null
						&& $('#sameasDeliveryAddressLPA')[0].checked) {
					var allvals = $('.form-control-1').map(function() {
						return this.value;
					}).get().join(' ');
					$('.form-control2').val(allvals.trim());
				}
			});
	if ($('#sameasDeliveryAddressLPA')[0] != null
			&& $('#sameasDeliveryAddressLPA')[0].checked) {
		
		
		//-------------------
		$('#shippingSuburb').keyup(function() {
			//-
		});
		$('#stateInvoice').keyup(function() {
			//$('#stateDelivery').val($(this).val());
		});
		$('#postcodeInvoice').keyup(function() {
			//$('#postcodeDelivery').val($(this).val());
		});
	}
}

function handleInputNames (rows, rowLength) {
    var accountIdInput = rows.querySelector('input[name*="abnAccountId"]');
    var abnNumberInput = rows.querySelector('input[name*="abnNumber"]');
    var samAccessSelect = rows.querySelector('select[name*="samAccess"]');

    accountIdInput.id = reNameInputId(accountIdInput.id, rowLength);
    abnNumberInput.id = reNameInputId(abnNumberInput.id, rowLength);
    samAccessSelect.id = reNameInputId(samAccessSelect.id, rowLength);

    accountIdInput.name = "albCompanyInfoData[" + rowLength + "].abnAccountId";
    abnNumberInput.name = "albCompanyInfoData[" + rowLength + "].abnNumber";
    samAccessSelect.name = "albCompanyInfoData[" + rowLength + "].samAccess";
}

function reNameInputId(id, index) {
    var split = id.split('.');
    split[split.length - 1] = index;
    return split.join('.');
}

function removeValidation (rows) {
    var inputs = rows.querySelectorAll("input");
    var selects = rows.querySelectorAll("select");
    var helpBlock = rows.querySelectorAll('.help-block');
    var fromGroup = rows.querySelectorAll('.form-group');

    for(var i = 0; i < inputs.length; i ++) {
        inputs[i].defaultValue = "";
        inputs[i].value = "";
    }

    for(var i = 0; i < selects.length; i ++) {
        selects[i].defaultValue = "";
        selects[i].value = "";
    }

    for(var i = 0; i < fromGroup.length; i ++) {
        if (fromGroup[i].className.indexOf("has-error") !== -1) {
            fromGroup[i].classList.remove("has-error");
        }
    }

    if (helpBlock.length !== 0) {
        for(var i = 0; i < helpBlock.length; i ++) {
            if (helpBlock[i]) {
                helpBlock[i].innerHTML = "";
            }
        }
    }
}

$('.sga-registration-link').click(function(){
    window.open('https://asahi.applyeasy.com.au/credit/introduction/', '_blank');
});
