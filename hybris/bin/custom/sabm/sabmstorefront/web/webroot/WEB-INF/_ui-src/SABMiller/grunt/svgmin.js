// svgmin
module.exports = {

	options: {
        plugins: [
            { removeXMLProcInst: false }, // prevent the XML header from being stripped
            { removeViewBox: false },
            { removeUselessStrokeAndFill: false },
            { cleanupIDs: false }
        ]
    },
    dist: {
        files: [{
            expand: true,
            cwd: '<%= settings.src %>/img/<%= settings.svgIconsDir %>/',
            src: ['*.svg'],
            dest: '<%= settings.src %>/img/<%= settings.svgIconsCompressedDir %>/',
        }]
    }
};