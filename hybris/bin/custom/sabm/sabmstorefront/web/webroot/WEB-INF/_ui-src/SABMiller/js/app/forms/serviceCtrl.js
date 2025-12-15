CUB.controller("serviceCtrl", [
    "$scope",
    "$http",
    "$window",
    "angularUtils",
    function ($scope, $http, $window, angularUtils) {
        $scope.message = "";
        $scope.messageType = "";

        $scope.serviceRequestInit = function () {
            $scope.sr = {};
            $scope.delIssueChecks = [];
            $scope.$watch("serviceRequest.$valid", function (validity) {
                var prevUrl = document.referrer;
                var currentURL = document.URL;
                if (
                    currentURL.indexOf("business") >= 0 &&
                    prevUrl.indexOf("orderConfirmation") >= 0
                ) {
                    $scope.sr.type = "pickup";
                }

                if (validity) {
                    $scope.notComplete = false;
                    $("#form-btn").prop("disabled", false); // enable when valid
                } else {
                    $scope.notComplete = true;
                    $("#form-btn").prop("disabled", true); // disable when invalid
                    console.log("form not valid");
                }
            });
        };

        $scope.checkValid = function (form, type) {
            $("#keg-issue-upload-failure").remove();

            var type = $scope.sr.type,
                formObj = {};

            formObj = $scope.sr;
            formObj.request = $scope[type];

            if (!form.$valid) {
                $scope.notComplete = true;
                return;
            }
            $scope.notComplete = false;

            if (!$scope.notComplete) {
                // Disable button while request is in progress
                var $btn = $("#form-btn");
                $btn.prop("disabled", true);

                var phoneNumber = "";

                var bu_name_pk = formObj.bu;
                var bu_name = "";
                var bu_pk = "";
                var bu_code = "";

                if (bu_name_pk) {
                    var bu_array = bu_name_pk.split("--");

                    bu_name = bu_array[0];
                    bu_code = bu_array[1].replace(/^0+/, '');
                    bu_pk = "pk_value";
                }

                if (formObj.phoneNumber) {
                    phoneNumber = formObj.phoneNumber.toString();
                }

                let finalEmail = formObj.email || $('#asahiStaffEmail').val() || $("#liveChatCustomDetails").attr("data-email");

                var dataObj = {
                    emailAddress: finalEmail,
                    businessUnit: bu_name,
                    preferredContactMethod: formObj.prefcontact,
                    requestType: formObj.type,
                    name: formObj.name,
                    phoneNumber: phoneNumber,
                    data: formObj.request,
                };

                if (type != "KEG_ISSUE") {
                    $http
                        .post("/businessEnquiry/send", dataObj)
                        .success(function (data, status) {
                            window.location = window.location.origin + "/businessEnquiry/enquirySent";
                        })
                        .error(function (error) {
                            window.location = error.redirectUrl;
                        })
                        .finally(function () {
                            $btn.prop("disabled", false); // enable after completion
                        });
                } else {
                    var productNameCode = $("#Product_Name__code").val();
                    var productName = "";
                    var productCode = "";
                    var kegBestBeforeAvailable = $(".keg_bestbefore_avl:checked").val();
                    var kegBestBeforeDate = $("#keg_bestbefore_date").val();

                    kegBestBeforeDate = kegBestBeforeAvailable === "No" ? "NA" : kegBestBeforeDate;

                    if (productNameCode) {
                        var productNameCodeArray = productNameCode.split("--");
                        productName = productNameCodeArray[0];
                        productCode = parseInt(productNameCodeArray[1], 10);
                    }

                    var name = dataObj.name;
                    var businessUnitName = "CUB";
                    var preferredContactMethod = dataObj.preferredContactMethod;
                    var emailAddress = dataObj.emailAddress;
                    var phnNumber = dataObj.phoneNumber;
                    var reqType = "Keg Quality Complaint";
                    var kegNumber = $("#keg-number").val();
                    var kegProblem = $("#keg-other").val();
                    var timecode = $("#time-code").val();
                    var plantCode = $("#plant-code").val();
                    var userPK = $("#liveChatCustomDetails").attr("data-userpk");
                    var reasonCode = formObj.reasonCode;

                    var requestData = JSON.stringify({
                        "name": name,
                        "businessUnit": businessUnitName,
                        "preferredContactMethod": preferredContactMethod,
                        "emailAddress": emailAddress,
                        "phoneNumber": phnNumber,
                        "requestType": reqType,
                        "data": {
                            "brand": bu_code,
                            "number": kegNumber,
                            "bestBeforeDate": kegBestBeforeDate,
                            "kegProblem": kegProblem,
                            "timecode": timecode,
                            "plantcode": plantCode,
                            "sku": productCode,
                            "userPk": userPK,
                            "bestBeforeDateAvailable": kegBestBeforeAvailable,
                            "reasonCode": reasonCode
                        },
                    });

                    function displayErrorMessage() {
                        if ($("#keg-issue-upload-failure").length === 0) {
                            $("#form-btn").after(`
                             <span class="error-bold" ng-show="notComplete" id="keg-issue-upload-failure">
                             There was an error processing your request. Please try again.
                             </span>
                             `);
                        }
                    }

                    $.ajax({
                        type: "POST",
                        url: "/businessEnquiry/createKegIssue",
                        data: requestData,
                        contentType: "application/json",
                        success: function (response) {
                            if (response.status == "success") {
                                window.location = window.location.origin + "/businessEnquiry/enquirySent";
                            }
                        },
                        error: function (response) {
                            displayErrorMessage();
                            console.error("Error details:", response);
                        },
                        complete: function () {
                            $btn.prop("disabled", false); // enable after completion
                        }
                    });
                }
            }
        };

        $scope.updateDelIssueChecks = function (i) {
            if (angularUtils.arrayContains($scope.delIssueChecks, i)) {
                $scope.delIssueChecks.splice(
                    $scope.delIssueChecks.indexOf(i),
                    1
                );
            } else {
                $scope.delIssueChecks.push(i);
            }
        };
    },
]);


