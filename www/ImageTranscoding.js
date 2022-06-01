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
