// autoprefixr
module.exports = {
	
	options: {
        browsers: ['last 2 version', 'ie 8', 'ie 9']
    },
    // prefix the specified file
    single_file: {
        expand: true,
        flatten: true,
        src: '<%= settings.dist %>/<%= settings.stylesDir %>/style.css',
        dest: '<%= settings.dist %>/<%= settings.stylesDir %>/'
    }

};