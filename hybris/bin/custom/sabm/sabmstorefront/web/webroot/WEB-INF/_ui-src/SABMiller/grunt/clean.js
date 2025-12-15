// clean
module.exports = {

    options: {
        force: true
    },
    all: {
        src: ['<%= settings.dist %>/*','<%= settings.src %>/css/_sprites.scss','<%= settings.src %>/css/_retinasprites.scss']
    },
    styles: {
        src: ['<%= settings.dist %>/<%= settings.stylesDir %>/*','<%= settings.src %>/css/_sprites.scss','<%= settings.src %>/css/_retinasprites.scss']
    },
    scripts: {
        src: ['<%= settings.dist %>/<%= settings.scriptsDir %>/*']
    },
    images: {
        src: ['<%= settings.dist %>/<%= settings.imagesDir %>/*']
    },
    tempHtml: {
        src: ['<%= settings.dist %>/templates']
    },
    html: {
        src: ['<%= settings.dist %>/*.html']
    },
    json: {
        src: ['<%= settings.dist %>/*.html']
    }

};