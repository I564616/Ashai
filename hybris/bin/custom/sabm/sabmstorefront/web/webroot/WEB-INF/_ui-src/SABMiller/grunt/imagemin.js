// imagemin
module.exports = {

	dynamic: {
        options: {
            optimizationLevel: 7,
            progressive: true
        },
        files: [{
            expand: true,
            cwd: '<%= settings.src %>/img/',
            src: [
                '**/*.{png,jpg,gif}',
                '!<%= settings.spriteDir %>/*.png',
                '!<%= settings.svgIconsCompressedDir %>/*.png'
            ],
            dest: '<%= settings.dist %>/<%= settings.imagesDir %>/'
        }]
    }

};