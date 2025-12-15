ACC.orderhistory = {

    _autoload: [
        "IESearchParams",
	    "datePickerSetup",
        "exportCsvHandler",
        "generateMaxDateRangeLabel",
        "orderRangeUpdateHandler"
	],

    setMinDate:function(){
        //Change min date and max date of end date calendar when date is picked
        var max = ACC.orderhistory.startCal.datepicker('getDate');
        max.setDate(max.getDate() + maxOrderRange);
        var min = ACC.orderhistory.startCal.datepicker('getDate');

        ACC.orderhistory.endCal.datepicker('option', { minDate: min , maxDate: max});

        //If end is set call verifyDateRange so errors can be hidden if necessary
        var end = ACC.orderhistory.endCal.datepicker('getDate');
        if (end != null) {
            ACC.orderhistory.verifyDateRange(min, end);
        }
    },

    

    verifyDateRange:function(startDate, endDate) {
        //Validate date range and hide/show errors when necessary
        //This function will also add the date values to the "sort by" form if they are valid

        if(startDate != null & endDate != null) {
            $('#dateRangeError').addClass("hide");
            $('#orderHistoryFormError').addClass("hidden");
            $('.order-history-range-calendars').removeClass("has-error");

            params = [startDate,endDate].map(function(val){
               return ACC.orderhistory.FixLocaleDateString(val.toLocaleDateString('en-AU'));
            });
            //Add values to sort by form
            $('#sortForm1').find("input[name='startDate']").val(params[0]);
            $('#sortForm1').find("input[name='endDate']").val(params[1]);
            return true
        }
        else{
            //Show error
            $('#dateRangeError').removeClass("hide");
            $('#orderHistoryFormError').removeClass("hidden");
            $('.order-history-range-calendars').addClass("has-error");
            return false;
        }
    },



    FixLocaleDateString:function(localeDate) {
        //This is needed for IE
        var newStr = "";
        for (var i = 0; i < localeDate.length; i++) {
            var code = localeDate.charCodeAt(i);
            if (code >= 47 && code <= 57) {
                newStr += localeDate.charAt(i);
            }
        }
        return newStr;
    },



    datePickerSetup:function(){
        //Create object for searching query parameters
        ACC.orderhistory.searchParams = new URLSearchParams(window.location.search);

        ACC.orderhistory.startCal = $('#startDateCalendar');
        ACC.orderhistory.endCal = $('#endDateCalendar');
        ACC.orderhistory.startCal.datepicker({
	    	dateFormat: 'dd/mm/yy',
			numberOfMonths: [ 1, 2 ]})
            .on( "change", ACC.orderhistory.setMinDate);

        ACC.orderhistory.endCal.datepicker({
            dateFormat: 'dd/mm/yy',
            numberOfMonths: [ 1, 2 ]
        }).on("change", function() {
            //Call verify date range so errors can be hidden if necessary
            ACC.orderhistory.verifyDateRange(ACC.orderhistory.startCal.datepicker('getDate'), ACC.orderhistory.endCal.datepicker('getDate'));
        });

        //Get start and end date from query if they exist
        var start = ACC.orderhistory.searchParams.has('startDate') ? ACC.orderhistory.searchParams.get('startDate') : null;
        var end = ACC.orderhistory.searchParams.has('endDate') ? ACC.orderhistory.searchParams.get('endDate') : null;
        
        //If query exists and the values are valid, set the default dates
        if(start && end){
            [start,end].map(function(val){
                return encodeURIComponent(ACC.orderhistory.FixLocaleDateString(val));
                //return val.replace(/%2F/g,"/");
            })
            ACC.orderhistory.startCal.datepicker("setDate", start);
            ACC.orderhistory.endCal.datepicker("setDate", end);
            ACC.orderhistory.setMinDate();
        }
            
        
        //Hide datepickers on scroll
        $('.pageBodyContent').scroll(function(){
    		ACC.orderhistory.startCal.datepicker('hide');
    		ACC.orderhistory.startCal.blur();
    		ACC.orderhistory.endCal.datepicker('hide');
    		ACC.orderhistory.endCal.blur();	 
    	})


        //Add click handlers to the calendar icons
        $('.showStartCal').click(function(){
            ACC.orderhistory.startCal.datepicker('show');
        })
        $('.showEndCal').click(function(){
        ACC.orderhistory.endCal.datepicker('show');
        })
    },



    exportCsvHandler:function(){
        $('#exportOrderHistoryCsvBtn').click(function(){
            var locArr = location.href.split("/");
            //Reconstruct url without query params
            var href = locArr.slice(0,2).join('') + location.pathname + "/exportcsv"

            //The call to /exportcsv returns a URL path to a download, redirecting to this will download the file
            $.ajax({
                url: href,
                data: {
                    sort: ACC.orderhistory.searchParams.has('sort') ? ACC.orderhistory.searchParams.get('sort') : "",
                    startDate: ACC.orderhistory.searchParams.has('startDate') ? ACC.orderhistory.searchParams.get('startDate') : "",
                    endDate: ACC.orderhistory.searchParams.has('endDate') ? ACC.orderhistory.searchParams.get('endDate') : "",
                },
                success: function(data) {
                    location.href=data
                }
            });
        })
    },



    generateMaxDateRangeLabel:function(){
        if(typeof maxOrderRange != "undefined"){
            var months = Math.floor(maxOrderRange/30);
            var labelString = "Maximum date range is "
            
            switch(months){
                case 0:
                    labelString += maxOrderRange.toString() + " days."
                    break;
                case 1:
                    labelString += months.toString() + " month."
                    break;
                default:
                    labelString += months.toString() + " months."
            }

            $(".order-history-max-order-range").children("span").html(labelString);
        }
    },



    orderRangeUpdateHandler: function() {
        //Handle update click
        $('#orderRangeUpdateBtn').click(function(){
            startDate = $('#startDateCalendar').datepicker('getDate');
            endDate = $('#endDateCalendar').datepicker('getDate');
            
            if(ACC.orderhistory.verifyDateRange(startDate, endDate)){                
                //Build path
                var href = "https://" + window.location.host + window.location.pathname;
                var newQuery = "?";
                
                //Turn date objects into strings of form DD/MM/YYYY
                params = [startDate,endDate].map(function(val){
                    var str = val.toLocaleDateString('en-AU');
                    return encodeURIComponent(ACC.orderhistory.FixLocaleDateString(str));
                    //return str.replace(/\//g,"%2F");
                });

                //Add paramater names
                params[0] = "startDate=" + params[0];
                params[1] = "endDate=" + params[1];
                
                //Check if we had a sort query already and if so, add it to our new query
                if(ACC.orderhistory.searchParams.has('sort')){
                    newQuery+="sort=" + ACC.orderhistory.searchParams.get('sort') + "&";
                }

                //Reload page with query
                newQuery += params.join('&');
                location.href = href + newQuery;
            }
            else{
                //Remove spinner and block that is applied by button press
                $('html').unblock();
            }
        })
    },

    IESearchParams: function() {
        (function (w) {

            w.URLSearchParams = w.URLSearchParams || function (searchString) {
                var self = this;
                self.searchString = searchString;
                self.get = function (name) {
                    var results = new RegExp('[\?&]' + name + '=([^&#]*)').exec(self.searchString);
                    if (results == null) {
                        return null;
                    }
                    else {
                        return decodeURI(results[1]) || 0;
                    }
                };
                self.has = function (name) {
                    var results = new RegExp('[\?&]' + name).exec(self.searchString); 
                    if (results == null) 
                    { 
                        return false; 
                    } else { 
                        return true; 
                    }
                }
            }
        
        })(window)
    }
    

    
    
}