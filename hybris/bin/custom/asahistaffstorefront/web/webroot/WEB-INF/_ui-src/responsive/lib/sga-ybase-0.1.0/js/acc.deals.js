var DESELECT_ALL = "Deselect all";
var SELECT_ALL = "Select all";

ACC.deals = {

    dealsForm: document.querySelector("#dealsForm"),
    checkboxes: document.querySelectorAll(".multi-checkboxes .checkbox input[type=checkbox]"),
    additionalSection: $('#additional-section'),
    customerEmails: $('#customer-emails'),
    requiredMsg: $('#deals-template .alert-danger.required'),
    updateBtn: $('.deals .js-update-btn'),

    checked: {},
    originalChecked: [],
    selectedEmailsArr: [],
    emails: [],

    init: function () {
        if (!$('#send-confirm').is(":checked")) {
            this.additionalSection.hide();
        }
        // initialize customer emails
        this.emails = this._handleEmails();
    },

    handleCheckbox: function () {
        var dealsForm = this.dealsForm;
        var checkboxes = this.checkboxes;
        var self = this;
        if (dealsForm && checkboxes) {
            var selectAll = dealsForm.querySelector("#selectAll");
            var len = parseInt(dealsForm.dataset['dealsAmount']);

            // check/uncheck checkboxes based on notification settings
            this._resetEnabled(this.checkboxes);

            // initiate select all button status
            changeStatus(selectAll, len === this.originalChecked.length);

            for(var i = 0; i < checkboxes.length; i ++) {
                checkboxes[i].addEventListener("change", function (e) {
                    if (self.updateBtn.attr('disabled') === 'disabled') {
                        self.updateBtn.attr('disabled', false);
                    }
                    var localActive = [];
                    self.checked[e.target.value] = e.target.checked
                    for(var i in self.checked) {
                        if (self.checked[i]) {
                            localActive.push(i);
                        }
                    }

                    // if match original checked status, disable update button
                    if (JSON.stringify(localActive) === JSON.stringify(self.originalChecked)) {
                        self.updateBtn.attr('disabled', true);
                    }

                    changeStatus(selectAll, len === localActive.length);
                });
            }
        }
    },

    addItem: function (target) {
        var newEmail = $('#' + target).val();
        if (newEmail !== '') {
            // if want to add email already exists in email list, show alert
            if (this.emails.indexOf(newEmail) !== -1) {
                this.requiredMsg.text('The email has already exists in the list.');
            } else if (!this._validateEmail(newEmail)) {
                this.requiredMsg.text('Please enter a valid email.');
            } else {
                this.emails.push(newEmail);
                this._addAdditionalEmail(newEmail);
                this.requiredMsg.text('');
                $('#addEmail').val('');
            }
        } else {
           this.requiredMsg.text('Please enter a email.');
        }
    },

    selectedEmails: function (e) {
        var checkboxes = $('#customer-emails input[type=checkbox]');
        var emails = [];
        for(var i = 0; i < checkboxes.length; i ++) {
            var ck = checkboxes[i];
            if (ck.checked) {
                emails.push(ck.value);
            }
        }
        this.selectedEmailsArr = emails;

        // remove error message
        this.requiredMsg.text('');
    },

    onSubmit: function (customerAccount) {
        var url = $('#dealsForm').attr('action');
        var isChecked = $('#send-confirm').is(":checked");
        var self = this;
        var data = {
            dealsToActivate: [],
            dealsToRemove: [],
            customerEmails: isChecked ? this.selectedEmailsArr : [],
            dealsDetails: $('textarea[name=dealsDetails]').val(),
            customerAccount: customerAccount
        }
        for(var i in self.checked) {
            if (this.checked[i] && this.originalChecked.indexOf(i) === -1) {
                data.dealsToActivate.push(i);
            }
            if (!this.checked[i] && this.originalChecked.indexOf(i) !== -1) {
                data.dealsToRemove.push(i);
            }
        }
        if (isChecked === false || isChecked === true && this.selectedEmailsArr.length !== 0) {
            $.ajax({
                url,
                type: 'POST',
                data: JSON.stringify(data),
                contentType: 'application/json',
                success: function (result) {
                    self._resetAdditionalDetails();
                    if (result.status === 'success') {
                        $('.deals-alerts .alert.success').removeClass('hidden').text(result.message);
                    } else {
                        $('.deals-alerts .alert.fail').removeClass('hidden').text(result.message);
                    }
                    $('#deals-template').modal('hide');

                    // update original checked status
                    self.checkboxes = document.querySelectorAll(".multi-checkboxes .checkbox input[type=checkbox]");
                    self._resetEnabled(self.checkboxes);
                    self.updateBtn.attr('disabled', true);
                },
                error: function (error) {
                    $('.deals-alerts .alert.fail').removeClass('hidden').text('Deals updated failed for ' + $('.deal-customer').text());
                    $('#deals-template').modal('hide');
                }
            });
        } else {
            this.requiredMsg.text('Please select at least one customer email.');
        }
    },

    onChange: function (ele) {
        if (ele.checked) {
            this.additionalSection.show();
        } else {
            this.additionalSection.hide();
        }
    },

    search: function () {
        var storage = ['page', 'sort', 'accountPayerNumber', 'customerName', 'email', 'address', 'postcode', 'suburb', 'expiryDateMonth'];
        for(var i = 0; i < storage.length; i++) {
            var s = storage[i];
            $('input[name=' + s + ']').val(sessionStorage.getItem(s));
        }
        $('#customerSearchForm').submit();
    },

    removeError: function () {
        this.requiredMsg.text('');
    },

    selectAll: function (e) {
        var localActive = [];
        e.parentElement.querySelector("#select-text").textContent = e.checked ? DESELECT_ALL : SELECT_ALL;
        for(var i = 0; i < this.checkboxes.length; i ++) {
            this.checkboxes[i].checked = e.checked;
            this.checked[this.checkboxes[i].value] = e.checked;
            if (this.checkboxes[i].checked) {
                localActive.push(this.checkboxes[i].value);
            }
        }

        // if match original checked status, disable update button
        this.updateBtn.attr('disabled', JSON.stringify(localActive) === JSON.stringify(this.originalChecked));
    },

    openModal: function (e) {
        var self = this;
        var target = e.dataset['modalTarget']
        var modalTargetId = '#' + target;
        $(modalTargetId).modal({ show: true });
        $(modalTargetId).removeClass("cboxElement");

        $(modalTargetId).on('shown.bs.modal', function () {
            self._tabbing(target);
            self._renderEmails(self.emails);
        });
    },

    _resetEnabled: function (checkboxes) {
        var originalEnabled = []
        this.originalChecked = this._scanCheckboxes(checkboxes, originalEnabled);
        for(var i = 0; i < this.originalChecked.length; i++) {
            this.checked[this.originalChecked[i]] = true;
        }
   },

    _scanCheckboxes: function (checkboxes) {
        var originalEnabled = [];
        // check/uncheck checkboxes based on notification settings
        for(var i = 0; i < checkboxes.length; i ++) {
            var ck = checkboxes[i];
            if (ck.checked) {
                originalEnabled.push(ck.value);
            }
        }

        return  originalEnabled;
    },

    _handleEmails: function () {
        if (this.customerEmails.length !== 0) {
            this.emails = this.customerEmails.data('emails').replace(/[\[\]\s]+/g, '').split(',');
            return this.emails;
        }

        return null;
    },

    _renderEmails: function (emails) {
        var layout = '';
        for(var i = 0; i < emails.length; i++) {
            layout += '<label class="display-flex position-relative pl-0" >' +
                '<input type="checkbox" onchange="ACC.deals.selectedEmails(this)" value="' + emails[i] + '" />' +
                '<span class="checkmark"></span>' +
                '<span class="label-text">' + emails[i] + '</span>' +
            '</label>';
        }
        this.customerEmails.html(layout);
    },

    _addAdditionalEmail: function (email) {
        var layout = '<label class="display-flex position-relative pl-0" >' +
                    '<input type="checkbox" onchange="ACC.deals.selectedEmails(this)" value="' + email + '" />' +
                    '<span class="checkmark"></span>' +
                    '<span class="label-text">' + email + '</span>' +
                '</label>';
        this.customerEmails.append(layout);
    },

    _validateEmail: function (email) {
       var regex = /^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?$/; //Email Validation as per RFC2822 standards. Straight from .net helpfiles.
       return regex.test(email);
    },

   _tabbing: function (id) {
       var self = this;
       var capture = $('#' + id).focus().keydown(function handleKeydown (event) {
            var key = event.key.toLowerCase();
           if (key === 'tab') {
                var tabbable = $()
                               // All form elements can receive focus.
                               .add( capture.find( "button, input" ) )
                               // Any element that has a non-(-1) tabindex can receive focus.
                               .add( capture.find( "[tabindex]:not([tabindex='-1'])" ) );
               var target = $( event.target );
               if ( event.shiftKey ) {

                   if ( target.is( capture ) || target.is( tabbable.first() ) ) {

                       // Force focus to last element in container.
                       event.preventDefault();
                       tabbable.last().focus();

                   }

               // Forward tabbing (Key: Tab).
               } else {
                   if ( target.is( tabbable.last() ) ) {

                       // Force focus to first element in container.
                       event.preventDefault();
                       tabbable.first().focus();
                   }

               }
           } else if (key === 'enter') {

           } else {
               return ;
           }
       });
   },

   _resetErrorMsg: function () {
        $('.deals-alerts .alert.fail').addClass('hidden');
        $('.deals-alerts .alert.success').addClass('hidden');
   },

   _resetAdditionalDetails: function () {
        this.selectedEmailsArr = [];
        $('textarea[name=dealsDetails]').val('');
        $('#addEmail').val('');
   }
}

function changeStatus (obj, bool) {
    obj.parentElement.querySelector("#select-text").textContent = bool ? DESELECT_ALL : SELECT_ALL;
    obj.checked = bool;
}

// loading script in user details page
$(".page-asahiDeals").ready(function() {
	with (ACC.deals) {
		handleCheckbox();
		init();
	}
});