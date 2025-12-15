// spritesheets
module.exports = {

	dist: {
        src: ['<%= settings.src %>/img/<%= settings.spriteDir %>/*.png'],
        destImg: '<%= settings.dist %>/<%= settings.imagesDir %>/<%= settings.spriteDir %>.png',
        destCSS: '<%= settings.src %>/css/core/_sprites.scss',
        cssFormat: 'scss',
        imgPath: '../img/<%= settings.spriteDir %>.png'
    },
    retina: {
        src: ['<%= settings.src %>/img/<%= settings.retinaSpriteDir %>/*.png'],
        destImg: '<%= settings.dist %>/<%= settings.imagesDir %>/<%= settings.retinaSpriteDir %>.png',
        destCSS: '<%= settings.src %>/css/core/_retinasprites.scss',
        cssFormat: 'scss',
        imgPath: '../img/<%= settings.retinaSpriteDir %>.png'
    }

};