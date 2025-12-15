// assemble
module.exports = function(grunt) {

    return {

    	options: {
            production: grunt.cli.tasks.length === 0 || grunt.cli.tasks[0] === 'build',
            partials: [
                '<%= settings.src %>/templates/partials/*.hbs',
                '<%= settings.src %>/modules/**/*.hbs'
            ],
            layout: ['<%= settings.src %>/templates/layouts/layout.hbs'],
            data: ['<%= settings.src %>/templates/data/*.json'],
            helpers: ['<%= settings.src %>/templates/helpers/*.js']
        },
        pages: {
            src: [
                '<%= settings.src %>/templates/pages/**/*.hbs'
            ],
            dest: '<%= settings.dist %>'
        }

    };

};