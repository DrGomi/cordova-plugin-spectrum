exports.transcode = function (inImage, outImage, success, error, options) {

    if (!options) {
        options = {};
    }

    if (!options.quality) {
        options.quality = 90;
    }

    if (!options.size) {
        options.size = 2048;
    }

    cordova.exec(
        function(result) { success(result); },
        error,
        'ImageTranscoding',
        'transcode', [inImage, outImage, options]
    );

};

exports.crop = function (inImage, outImage, success, error, options) {

    if (!options) {
        error('ERROR: crop method needs options-object!')
    } else {
        // { top: x, left: x, bottom: x, right: x, quality: x, size: x };
        if (!options.quality) {
            options.quality = 97;
        }
    
        if (!options.size) {
            options.size = 448;
        }
    
        cordova.exec(
            function(result) { success(result); },
            error,
            'ImageTranscoding',
            'crop', [inImage, outImage, options]
        );
    }
};
