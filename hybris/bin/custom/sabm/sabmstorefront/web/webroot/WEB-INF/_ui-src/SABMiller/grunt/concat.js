// concat
module.exports = {
	
	options: {
        separator: ';'
    },
    dist: {
        src: [
            '<%= settings.dist %>/<%= settings.scriptsDir %>/libs.js',
            '<%= settings.dist %>/<%= settings.scriptsDir %>/plugins.js',
            '<%= settings.dist %>/<%= settings.scriptsDir %>/src.js',
            '<%= settings.dist %>/<%= settings.scriptsDir %>/main.js',
            '<%= settings.dist %>/<%= settings.scriptsDir %>/app.js',
        ],
        dest: '<%= settings.dist %>/<%= settings.scriptsDir %>/scripts.js'
    }
            
};