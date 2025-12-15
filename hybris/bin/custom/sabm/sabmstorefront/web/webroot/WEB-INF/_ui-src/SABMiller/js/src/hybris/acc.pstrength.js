ACC.pwdstrength = {

	bindAll: function ()
	{
		this.bindPStrength();
	},
	bindPStrength: function ()
	{
		$('.strength').pstrength({ verdicts: [ACC.pwdStrengthVeryWeak,
			ACC.pwdStrengthWeak,
			ACC.pwdStrengthMedium,
			ACC.pwdStrengthStrong,
			ACC.pwdStrengthVeryStrong],
			tooShort: ACC.pwdStrengthTooShortPwd,
			minCharText: '' });
	}

};

$(document).ready(function ()
{
	ACC.pwdstrength.bindAll();
});
