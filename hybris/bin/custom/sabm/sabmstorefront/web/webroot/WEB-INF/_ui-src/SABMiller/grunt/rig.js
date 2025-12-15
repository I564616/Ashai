// rig
module.exports = {
	// Site files to build
	core: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/main.js': ['<%= settings.src %>/js/main.js']
        }
    },
    src: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/src.js': ['<%= settings.src %>/js/src.js']
        }
    },
    plugins: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/plugins.js': ['<%= settings.src %>/js/plugins.js']
        }
    },
    app: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/app.js': ['<%= settings.src %>/js/app.js']
        }
    },
    libs: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/libs.js': ['<%= settings.src %>/js/libs.js']
        }
    },
    // Portal files to build
    portalCore: {
        files: {
            '<%= settings.portalDist %>/<%= settings.scriptsDir %>/main.js': ['<%= settings.src %>/js/portal/main.js']
        }
    },
    portalSrc: {
        files: {
            '<%= settings.portalDist %>/<%= settings.scriptsDir %>/src.js': ['<%= settings.src %>/js/portal/src.js']
        }
    },
    portalPlugins: {
        files: {
            '<%= settings.portalDist %>/<%= settings.scriptsDir %>/plugins.js': ['<%= settings.src %>/js/portal/plugins.js']
        }
    },
    portalApp: {
        files: {
            '<%= settings.portalDist %>/<%= settings.scriptsDir %>/app.js': ['<%= settings.src %>/js/portal/app.js']
        }
    },
    liveChatApp: {
        files: {
            '<%= settings.dist %>/<%= settings.scriptsDir %>/livechat.js': ['<%= settings.src %>/js/livechat.js']
        }
    }
};