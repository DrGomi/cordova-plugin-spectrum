package com.drgomi.cordova.spectrum;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.spectrum.SpectrumSoLoader;
import com.facebook.spectrum.Spectrum;
import com.facebook.spectrum.logging.SpectrumLogcatLogger;
import com.facebook.spectrum.plugins.SpectrumPlugin;
import com.facebook.spectrum.plugins.SpectrumPluginJpeg;
import com.facebook.spectrum.EncodedImageSink;
import com.facebook.spectrum.EncodedImageSource;
import com.facebook.spectrum.SpectrumException;
import com.facebook.spectrum.SpectrumResult;
import com.facebook.spectrum.image.ImageSize;
import com.facebook.spectrum.Configuration;
import com.facebook.spectrum.options.TranscodeOptions;
import com.facebook.spectrum.requirements.EncodeRequirement;
import com.facebook.spectrum.requirements.ResizeRequirement;
import static com.facebook.spectrum.image.EncodedImageFormat.JPEG;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;


/**
 * This class echoes a string called from JavaScript.
 */
public class ImageTranscoding extends CordovaPlugin {

    public CallbackContext callbackContext;

    public Spectrum mSpectrum;

    @Override
    protected void pluginInitialize() {
      super.pluginInitialize();
  
      SpectrumSoLoader.init(getContext());
  
      mSpectrum = Spectrum.make(
              new SpectrumLogcatLogger(Log.INFO),
              new SpectrumPlugin[]{SpectrumPluginJpeg.get()}
      ); // JPEG plugin
  
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("transcode")) {
            cordova.getThreadPool().execute(() -> {
              try {

                final String fileInPath = args.getString(0);
                final String fileOutPath = args.getString(1);

                final JSONObject options = args.optJSONObject(2);
                final int quality = options.getInt("quality");
                final int size = options.getInt("size");

                transcode(
                  getContext(), 
                  mSpectrum, 
                  fileInPath, 
                  fileOutPath,
                  quality,
                  size,
                  result -> callbackContext.success(result));
   
              } catch (Exception e) {
                e.printStackTrace();
                callbackContext.error("spectrum.transcode ERROR:" + e.getMessage());
              }
            });
              return true;
        }
        return false;
    }


    public void transcode(final Context context, 
                            final Spectrum mSpectrum, 
                            final String fileInPath, 
                            final String fileOutPath,
                            final Integer quality,
                            final Integer size,
                            final StringRunnable completion) 
        throws JSONException {

        final File imageFile = new File(fileInPath);
        final Uri imageUri = Uri.fromFile(imageFile);
    
        try (final InputStream inputStream = context.getContentResolver().openInputStream(imageUri)) {

          final Configuration configuration = Configuration.Builder()
                  .setInterpretMetadata(true)
                  .setSamplingMethod(Configuration.SamplingMethod.Bicubic)
                  .setPropagateChromaSamplingModeFromSource(true)
                  .setUseTrellis(true)
                  .setUseProgressive(true)
                  .setUseOptimizeScan(true)
                  .setUseCompatibleDcScanOpt(true)
                  .build();

          final TranscodeOptions transcodeOptions =
          TranscodeOptions.Builder(new EncodeRequirement(JPEG, quality))
                  .resize(ResizeRequirement.Mode.EXACT_OR_SMALLER, new ImageSize(size, size))
                  .configuration(configuration)
                  .build();
    
          final SpectrumResult result = mSpectrum.transcode(
                  EncodedImageSource.from(inputStream),
                  EncodedImageSink.from(fileOutPath),
                  transcodeOptions,
                  "upload_flow_callsite_identifier");
    
          completion.run(result.toString());
    
        } catch (final IOException e) {
          throw new JSONException(e.getMessage());
        } catch (final SpectrumException e) {
          throw new JSONException(e.getMessage());
        }
      }

    private Context getContext() {

        return this.cordova.getActivity().getApplicationContext();
    }

    public interface StringRunnable {

        void run(String result);
    }
}
