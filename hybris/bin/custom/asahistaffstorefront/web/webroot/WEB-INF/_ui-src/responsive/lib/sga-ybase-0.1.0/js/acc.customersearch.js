ACC.customersearch = {
    _autoload: [
		"makeTableSortable",
		"pagination"
	],
    makeTableSortable: function () {
        var sort = $('input[name=sort]').val();
        var sorting = (sort === '' || sort === ':customername-asc') ? '#sort-asc' : '#sort-desc';
        $(sorting).addClass('bold');
    },

    pagination: function () {
        $(document).on('click', '.pagination li a', function (event) {
            event.preventDefault();
            var page = event.target.search.replace('?page=', '');
            $('input[name=page]').val(page);
            $('#customerSearch_button').click();
        })
    },

    sort: function(sort) {
        $('input[name=sort]').val(sort);
        $('#customerSearch_button').click();
    },
    decodeHtmlEntity: function(encodedString) {
    var parser = new DOMParser();
    var doc = parser.parseFromString(encodedString, 'text/html');
    return doc.documentElement.textContent;
	}
};

app.controller('customerSearchCtrl', function($scope) {
    $scope.all_blank = false;

    $scope.submit = function ($event) {
        //debugger
        this.validate();
        if (this.all_blank) {
            $event.preventDefault();
        }
    };

    $scope.updateData = function (t, e) {
        //debugger
        if (t && e) {
            this[t] = ACC.customersearch.decodeHtmlEntity(e);
        }
    };

    $scope.validate = function () {
        //debugger
        if ($('#accountNumber').val() === '' &&
            $('#accountName').val() === '' &&
            $('#email').val() === '' &&
            $('#address').val() === '' &&
            $('#suburb').val() === '' &&
            $('#postcode').val() === '') {
            this.all_blank = true;
        } else {
            this.all_blank = false;
        }
    }
});