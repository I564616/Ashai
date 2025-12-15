ACC.staffcheckout = {
    _autoload: [
        "initializeStaffCheckout",
    ],

    initializeStaffCheckout: function() {
        var isBdeFlow = $('#isBDEFlow').val() === 'true';
        if (isBdeFlow) {
            initCheckoutForm();
            initEmailCheckboxes();
            initAddEmail();
        }

        function initCheckoutForm () {
            $('#checkoutForm').removeClass('hide');
            $('#checkoutForm').hide();

            $('#cartSummary').removeClass('hide');
            $('#cartSummary').hide();

            $('#newEmailError').hide();

            let customerPermissionCheckbox = $('#customerPermission');

            if (customerPermissionCheckbox.length) {
                customerPermissionCheckbox.change(function () { // bind to checkbox change
                    this.checked ? $('#checkoutForm').show() : $('#checkoutForm').hide() ;
                    this.checked ? $('#cartSummary').show() : $('#cartSummary').hide() ;
                    enableCheckoutButton(); // event hook to update checkout button when customer permission changes
                });
            }

            var bindOrderDetailsTextChange = function () {
                enableCheckoutButton(); // Check whether to enable button on change
            };

            $('#orderDetailsText').on('input propertychange change', bindOrderDetailsTextChange); // bind to event
        };

        function getRowHtml (email, index) {
            return `
                <div class="row">
                    <div class="col-md-3 col-sm-6">
                        <input type="checkbox" name="customerEmail${index + 1}" id="customerID${index + 1}" value="${email}" ${selectedEmails.indexOf(email) > -1 ? 'checked' : ''}>
                        <label for="customerID${index + 1}">${email}</label>
                    </div>
                </div>`;
        };

        function initEmailCheckboxes () {
            if (typeof custEmails !== 'undefined') {
                $('#customerEmailsList').empty();
                custEmails.forEach(function (email, index) {
                    let newRow = getRowHtml(email, index);
                    $('#customerEmailsList').append(newRow);                    
                });

                bindCheckboxInput();
                initCustomerEmailsFormData();
            }
        };

        function enableCheckoutButton () {
            var hasBonusStockProductOnly = $('#hasBonusStockProductOnly').val();
            if ($('#customerPermission').length && $('#customerPermission').is(':checked') && ($('#checkoutPossible').val() === 'enabled' || hasBonusStockProductOnly) &&  $('#customerEmailsList input:checked').length > 0 && $('#orderDetailsText').val().trim()) {
                // enable checkout button only if all conditions are true
                $('#addCheckoutDetails').attr('disabled', false);
                $('#finalcheckoutButton').attr('disabled', false);
            } else {
                $('#addCheckoutDetails').attr('disabled', true);
                $('#finalcheckoutButton').attr('disabled', true);
            }
        };

        function bindCheckboxInput () {
            $('input', '#customerEmailsList').change(function () {
                //when checkbox is clicked
                if (this.checked) {
                    let idx = selectedEmails.indexOf(this.value);
                    // Is newly selected
                    if (idx === -1) selectedEmails.push(this.value);
                } else {
                    let idx = selectedEmails.indexOf(this.value);
                    if (idx > -1) selectedEmails.splice(idx, 1);
                }

                enableCheckoutButton();

                initCustomerEmailsFormData();
            });
        };

        function validateEmail (email) {
           var regex = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/; //Email Validation as per RFC2822 standards. Straight from .net helpfiles.
           return regex.test(email);
        }

        function initAddEmail () {
            $('#addNewEmailBtn').on('click', function () {
                if (validateEmail($('#newEmail').val()) ) {
                    //valid email
                    custEmails.push($('#newEmail').val());
                    $('#newEmail').val('');
                    initEmailCheckboxes();

                    $(this).parent().parent().removeClass('has-error');
                    $('#newEmailError').hide();
                } else {
                    //Invalid email
                    $(this).parent().parent().addClass('has-error');
                    $('#newEmailError').show();
                }
            });
        };

        function initCustomerEmailsFormData () {
            $('#customerEmailsFormData').empty();
            selectedEmails.forEach((e, i) => {
                let el = `<input type="hidden" name="bdeCheckoutForm.customers[${i}].email" value="${e}"></input>`;
                $('#customerEmailsFormData').append(el);
            } );
        };

    },
};