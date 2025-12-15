ACC.contactus = {
	_autoload : ["subject"],

	/* Subject list box when select other option */
	subject : function() {
		$("#otherSubjectFlag").hide();
		var otherSubject = $("#otherSubject").val();
		var subFlag = $("#subjectFlag").val();
		$("#otherSubject").hide();
		if (subFlag == "otherExists") 
		{
			$("#otherSubject").show();
		}
		$("#subject").change(function() {
			var selectedSubject = $('#subject option:selected').val();
			if (selectedSubject == "8") {
				$("#subjectFlag").val("otherExists");
				$("#otherSubject").show();
			} else {
				$("#subjectFlag").val("");
				$("#otherSubject").hide();
			}
		});
	}
};
