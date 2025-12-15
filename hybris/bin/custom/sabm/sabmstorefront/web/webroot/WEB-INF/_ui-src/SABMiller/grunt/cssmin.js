// cssmin
module.exports = function (grunt) {

    return {

    	options: {
            banner: '/*! <%= pkg.name %> stylesheet <%= grunt.template.today("yyyy-mm-dd") %> */',
            report: 'min'
        },
        minify: {
            expand: true,
            cwd: '<%= settings.dist %>/<%= settings.stylesDir %>',
            src: ['*.css', '!*.min.css'],
            dest: '<%= settings.dist %>/<%= settings.stylesDir %>/',
            ext: '.min.css'
        }
        
    };    

};