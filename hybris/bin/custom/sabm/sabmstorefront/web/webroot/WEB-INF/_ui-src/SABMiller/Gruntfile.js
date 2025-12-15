'use strict';

module.exports = function (grunt) {

    // Show elapsed time at the end
    require('time-grunt')(grunt);

    var _ = require('lodash');

    // Global config object
    var config = {
        pkg: grunt.file.readJSON('package.json'),
        settings: grunt.file.readJSON('paths.json')
    };

    // Load task configs from `grunt` directory
    _.extend(config,
        require('load-grunt-config')(grunt, {
            init: false,
            // lazy load tasks using jit grunt - https://github.com/shootaroo/jit-grunt
            jitGrunt: {
                staticMappings: {
                    sprite: 'grunt-spritesmith',
                    rig: 'grunt-rigger'
                }
            }
        })
    );

    grunt.initConfig(config);

};