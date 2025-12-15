// grunticon
module.exports = {
    icons: {
        files: [{
            expand: true,
            cwd: '<%= settings.src %>/img/<%= settings.svgIconsCompressedDir %>/',
            src: ['*.svg', '*.png'],
            dest: '<%= settings.dist %>/<%= settings.imagesDir %>/icons/'
        }],
        options: {
            colors: {
               sharing: "#ffffff",
               follow: "#eadfc4",
               altshare: "#b3852e"
            },
            enhanceSVG: true,
            dynamicColorOnly: true //not working in grunticon v1.3.0 (maybe they'll fix it one day)
        }
    }
};