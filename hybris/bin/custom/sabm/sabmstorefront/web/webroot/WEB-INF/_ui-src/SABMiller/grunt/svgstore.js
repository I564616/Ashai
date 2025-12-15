// SVG Store
module.exports = {

	options: {
      prefix : 'icon-', // This will prefix each ID
      svg: { // will add and overide the the default xmlns="http://www.w3.org/2000/svg" attribute to the resulting SVG
        viewBox : '0 0 100 100',
        xmlns: 'http://www.w3.org/2000/svg',
        style: 'display: none;'
      },
      cleanup: ['fill']
    },
    default: {
    	files: {
    		'<%= settings.tags %>/template/svg.tag':
    		['<%= settings.src %>/img/svg/*.svg']
    	}
    }
};