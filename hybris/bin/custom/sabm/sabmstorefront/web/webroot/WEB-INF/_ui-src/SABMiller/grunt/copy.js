// copy
module.exports = {

	fonts: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/fonts',
            dest: '<%= settings.dist %>/fonts/',
            src: [
                '**/*' // copy all font types within font directory
            ]
        }]
    },
    bsFonts: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/bower_components/bootstrap-sass-official/assets/fonts/bootstrap/',
            dest: '<%= settings.dist %>/fonts/',
            src: [
                '**/*' // copy all font types within font directory
            ]
        }]
    },
    images: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/img',
            dest: '<%= settings.dist %>/img',
            src: [
                '**/*', // copy all image types within img directory and any subdirectories
                '!<%= settings.spriteFolder %>/*.png',
                '!<%= settings.retinaSpriteFolder %>/*.png',
                '!<%= settings.svgIconsDir %>/*',
                '!<%= settings.svgIconsCompressedDir %>/*.svg'
            ]
        }]
    },
    svg: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.tags %>/template/',
            dest: '<%= settings.portalTags %>/desktop/template/',
            src: 'svg.tag'
        }]
    },
    jqueryLib: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/bower_components/jquery/dist/',
            dest: '<%= settings.dist %>/<%= settings.scriptsDir %>/libs/',
            src: '*.min.js' // ONLY copy minified file.
        }]
    },
    respondLib: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/bower_components/respond/dest/',
            dest: '<%= settings.dist %>/<%= settings.scriptsDir %>/libs/',
            src: 'respond.min.js'
        }]
    },
    angularLib: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/js/libs/',
            dest: '<%= settings.dist %>/<%= settings.scriptsDir %>/libs/',
            src: 'angular.min.js'
        }]
    },
    angularAnimationLib: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/js/libs/',
            dest: '<%= settings.dist %>/<%= settings.scriptsDir %>/libs/',
            src: 'angular-animate.min.js'
        }]
    },
    html: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.dist %>/templates/pages',
            dest: '<%= settings.dist %>',
            src: '**/*.html'
        }]
    },
    html: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.dist %>/templates/pages',
            dest: '<%= settings.dist %>',
            src: '**/*.html'
        }]
    },
    json: {
        files: [{
            expand: true,
            dot: true,
            cwd: '<%= settings.src %>/templates/jsondata',
            dest: '<%= settings.dist %>/jsondata',
            src: '**/*.json'
        }]
    }

};