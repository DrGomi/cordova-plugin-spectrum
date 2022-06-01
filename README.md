### cordova-plugin-spectrum
this cordova plugin embeds facebooks [Spectrum](https://github.com/facebookincubator/spectrum) library to enable image de-/en-/transcoding in Android applications

### current limitations of this plugin:
- Android only
- Only for `transcoding` from & to JPG image files

### currently the only implemented method is `transcode` which transcodes & resizes jpg images 

```js
    cordova.plugins.spectrum.transcode(
        inImgPath,  // :String => path to encoded image
        outImgPath, // :String => path to transcoded image
        function(res){ },  // success callback
        function(error){ }, // error callback
        { // if option is missing defaults to: { quality: 90, size: 2048 }
            quality: 86, // compression quality
            size: 2048  //  final image does not have any side length larger than 2048
        }
    );
```
