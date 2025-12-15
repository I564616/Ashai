// BrowserSync
module.exports = {

    options: {
        watchTask: true,
        debugInfo: true,
        host: '0.0.0.0',
        server: {
            baseDir: '<%= settings.dist %>'
        }
    },
    bsFiles: {
        src: [
            '<%= settings.dist %>/<%= settings.stylesDir %>/*.css',
            '<%= settings.dist %>/<%= settings.scriptsDir %>/*.js',
            '<%= settings.dist %>/*.html',
            '<%= settings.dist %>/*.cshtml'
        ]
    }

};