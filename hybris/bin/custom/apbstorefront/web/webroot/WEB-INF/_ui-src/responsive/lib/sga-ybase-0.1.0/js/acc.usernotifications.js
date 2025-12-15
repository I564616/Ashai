var DESELECT_ALL = "Deselect all";
var SELECT_ALL = "Select all";
ACC.userNotifications = {
    apbUpdateProfileForm: document.querySelector("#apbUpdateProfileForm"),
    checkboxes: document.querySelectorAll(".notifications .checkbox input[type=checkbox]"),
    handleCheckbox: function () {
        var apbUpdateProfileForm = this.apbUpdateProfileForm;
        var checkboxes = this.checkboxes;

        if (apbUpdateProfileForm && checkboxes) {
            var deliveryday = apbUpdateProfileForm.querySelector("#PUBLIC_HOLIDAY_ALT_DELIVERY");
            var noDeliveryday = apbUpdateProfileForm.querySelector("#PUBLIC_HOLIDAY_NO_DELIVERY");
            var selectAll = apbUpdateProfileForm.querySelector("#selectAll");
            var enabled = [];
            var len = 0;

            if (apbUpdateProfileForm.dataset.notifications) {
                var notifications = JSON.parse(apbUpdateProfileForm.dataset.notifications).notificationPreferences;
                for(var i in notifications) {
                    len++
                }

                // check/uncheck checkboxes based on notification settings
                for(var i = 0; i < checkboxes.length; i ++) {
                    var ck = checkboxes[i];
                    ck.checked = notifications[ck.id].emailEnabled;
                    if (notifications[ck.id].emailEnabled) {
                        enabled.push(ck)
                    }
                }

                // initiate select all button status
                changeStatus(selectAll, len === enabled.length);

                // make sure hidden field is synced
                if (deliveryday && noDeliveryday) {
                    deliveryday.addEventListener('change', function (e) {
                        noDeliveryday.checked = e.target.checked;
                    });
                }

                for(var i = 0; i < checkboxes.length; i ++) {
                    checkboxes[i].addEventListener("change", function (e) {
                        var numOfChecked = [];
                        for(var i = 0; i < checkboxes.length; i ++) {
                            if (checkboxes[i].checked) {
                                numOfChecked.push(checkboxes[i]);
                            }
                        }
                        changeStatus(selectAll, len === numOfChecked.length);
                    });
                }
            }
        }
    },

    selectAll: function (e) {
        e.parentElement.querySelector("#select-text").textContent = e.checked ? DESELECT_ALL : SELECT_ALL;
        for(var i = 0; i < this.checkboxes.length; i ++) {
            this.checkboxes[i].checked = e.checked;
        }
    },

    toolTip: function () {
        // support tooltip html string
        $('.payment-info-icon').tooltip({
           content: function () {
               return $(this).prop('title');
           }
       });
    }
}

function changeStatus (obj, bool) {
    obj.parentElement.querySelector("#select-text").textContent = bool ? DESELECT_ALL : SELECT_ALL;
    obj.checked = bool;
}

// loading script in user details page
$(".page-update-profile").ready(function() {
	with (ACC.userNotifications) {
		handleCheckbox();
		toolTip();
	}
});