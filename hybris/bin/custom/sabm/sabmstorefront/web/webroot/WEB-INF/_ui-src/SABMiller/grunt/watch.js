// Watch
module.exports = {

	sass: {
        files: [
            '<%= settings.src %>/css/**/*.scss',
            '<%= settings.src %>/modules/**/*.scss',
        ],
        tasks: ['styles']
    },
    scripts: {
        files: [
            '<%= settings.src %>/js/**/*.js',
            '<%= settings.src %>/modules/**/*.js',
        ],
        tasks: ['scripts','concat','uglify'],
        options: {
            spawn: false
        }
    },
    sprites: {
        files: [
            '<%= settings.src %>/img/<%= settings.spriteFolder %>/*.png'
        ],
        tasks: ['images', 'styles'],
        options: {
            spawn: false
        }
    },
    images: {
        files: [
            '<%= settings.src %>/img/**/*.{png,jpg,gif}',
            '!<%= settings.src %>/img/<%= settings.spriteDir %>/*.png',
            '!<%= settings.src %>/img/<%= settings.retinaSpriteDir %>/*.png',
            '!<%= settings.src %>/img/<%= settings.svgIconsDir %>/*.svg'
        ],
        tasks: ['copy:images'],
        options: {
            spawn: false
        }
    },
    icons: {
        files: [
            '<%= settings.src %>/img/<%= settings.svgIconsDir %>/*.svg'
        ],
        tasks: ['icons'],
        options: {
            spawn: false
        }
    },
    html: {
        files: [
            '<%= settings.src %>/templates/**/*',
            '<%= settings.src %>/modules/**/*.hbs',
        ],
        tasks: ['html'],
        options: {
            spawn: false
        }
    },
    json: {
        files: [
            '<%= settings.src %>/templates/data/*.json'
        ],
        tasks: ['json'],
        options: {
            spawn: false
        }
    }
};