// Sass
module.exports = {

	dist: {
		options: {
			outputStyle: 'expanded',
			precision: 10
		},
		files: {
			'<%= settings.dist %>/<%= settings.stylesDir %>/style.css': '<%= settings.src %>/css/style.scss',
			'<%= settings.dist %>/<%= settings.stylesDir %>/IEFix.css': '<%= settings.src %>/css/IEFix.scss',
			'<%= settings.portalDist %>/<%= settings.stylesDir %>/style.css': '<%= settings.src %>/css/portalStyle.scss'
		}
	}
};