ACC.planogram = {

    _autoload: [
    ],

    errorMsg: $('#uploader-template .modal-content .alert-danger.required'),
    uploadUrl: ACC.config.encodedContextPath + '/my-account/planograms/add',
    removeUrl: ACC.config.encodedContextPath + '/my-account/planograms/remove',
    removeAllUrl: ACC.config.encodedContextPath + '/my-account/planograms/bulkRemove',
    additionalPlanogramsItems: $('.additionalPlanograms').find('.responsive-table-cell .view'),
    defaultPlanogramsItems: $('.defaultPlanograms').find('.responsive-table-cell .view'),

    upload: function (ele) {
        var self = this;
        var file = $('input[name=file]').get(0).files[0];
        var documentName = $('input[name=documentName]').val();
        var formData = new FormData();
        formData.append('file', file);
        formData.append('documentName', documentName);

        if (documentName === '') {
            this.errorMsg.removeClass('hidden');
        } else {
            setTimeout(function () {
                $('.blockUI').css({ 'z-index': 2000 });
            });
            ele.disabled = true
            $.ajax({
                url: this.uploadUrl,
                type: 'POST',
                data: formData,
                processData: false,
                contentType: false,
                success: function (res) {
                    if (res === 'SUCCESS') {
                        sessionStorage.setItem('addItem', res);
                        setTimeout(function () {
                            location.reload();
                        }, 1000);
                    }
                },
                error: function (error) {
                    $("html").unblock();
                    console.log(error);
                }
            });
        }
    },

    remove: function (ele) {
        $.ajax({
            url: this.removeUrl,
            type: 'POST',
            data: JSON.stringify({ code: ele.dataset['code']}),
            contentType: 'application/json',
            success: function (res) {
                if (res === 'SUCCESS') {
                    setTimeout(function () {
                        location.reload();
                    }, 1000);
                }
            },
            error: function (error) {
                $("html").unblock();
                console.log(error);
            }
        });
    },

    removeAll: function () {
        $.ajax({
            url: this.removeAllUrl,
            type: 'POST',
            success: function (res) {
                if (res === 'SUCCESS') {
                    location.reload();
                }
            },
            error: function (error) {
                $("html").unblock();
                console.log(error);
            }
        });
    },

    view: function (ele, mediaUrl, target) {
        var self = this;
        this._previewFile(ele, target, mediaUrl, document.body.clientWidth, parseInt(screenXsMax.replace('px', '')));
        $(window).resize(function (value) {
            self._previewFile(ele, target, mediaUrl, value, screenXsMax);
        });

        // target different file preview section
        var items = target === 'pdfcontent' ? this.additionalPlanogramsItems : this.defaultPlanogramsItems;
        for(var i = 0; i < items.length; i ++) {
            this._toggleView(ele, items);
        }
    },

    openModal: function (e) {
        var self = this;
        this.onFocus();
        var target = document.getElementById(e.id);
        var file = target.files[0];

        var fileMaxSize = e.dataset['fileMaxSize'];
        if ($.isNumeric(fileMaxSize) && file) {
            if (file.size > parseFloat(fileMaxSize)) {
                $('.uploader-error').show();
                return false;
            }
        }

        if (file) {
            var fileName = file.name;
            var modalTargetId = '#' + target.dataset['modalTarget'];
            $(modalTargetId).modal({show: true});
            $(modalTargetId).removeClass("cboxElement");
            $('input[name=documentName]').val(fileName);
            $(modalTargetId).on('shown.bs.modal', function () {
                self._tabbing('uploader-template');
            });
        }
    },

    onFocus: function () {
        this.errorMsg.addClass('hidden');
    },

   _tabbing: function (id) {
       var self = this;
       var capture = $('#' + id).focus().keydown(function handleKeydown (event) {
            var key = event.key.toLowerCase();
           if (key === 'tab') {
                var tabbable = $()
                               // All form elements can receive focus.
                               .add( capture.find( "button, input" ) )
                               // Any element that has a non-(-1) tabindex can receive focus.
                               .add( capture.find( "[tabindex]:not([tabindex='-1'])" ) );
               var target = $( event.target );
               if ( event.shiftKey ) {

                   if ( target.is( capture ) || target.is( tabbable.first() ) ) {

                       // Force focus to last element in container.
                       event.preventDefault();
                       tabbable.last().focus();

                   }

               // Forward tabbing (Key: Tab).
               } else {
                   if ( target.is( tabbable.last() ) ) {

                       // Force focus to first element in container.
                       event.preventDefault();
                       tabbable.first().focus();
                   }

               }
           } else if (key === 'enter') {
                self.upload();
           } else {
               return ;
           }
       });
   },

   _previewFile: function (ele, target, mediaUrl, currentSize, resizedSize) {
      // open pdf in new tab in small device
      if (currentSize < resizedSize ) {
          window.open(mediaUrl, '_target');
      } else {
           var parent = $(ele).parent().parent();
           var view = parent.find('.view');
           view[0].textContent = view[0].textContent.toLowerCase() !== 'close' ? 'close' : 'view';
           if (view[0].textContent.toLowerCase() === 'view') {
               $('#' + target).attr('src', '');
               $('#' + target + 'frame').hide();
               parent.removeClass('viewing');
           } else {
               $('#' + target).attr('src', mediaUrl);
               $('#' + target + 'frame').show();
               parent.addClass('viewing');
           }
      }
  },

  _toggleView: function (ele, items) {
       var parent = $(ele).parent().parent();
       var view = parent.find('.view');
       for(var i = 0; i < items.length; i ++) {
           var item = $(items[i]);
           var index = item.data('index');
           if (index !== parseInt(view.data('index'))) {
               item.text('view');
               item.parent().parent().removeClass('viewing');
           }
       }
  }
}