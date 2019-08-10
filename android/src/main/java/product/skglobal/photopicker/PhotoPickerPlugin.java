package product.skglobal.photopicker;

import android.app.Activity;
import android.content.Intent;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import product.skglobal.photopicker.picker.ImagePicker;
import product.skglobal.photopicker.picker.ImagePickerDelegate;

/**
 * PhotoPickerPlugin
 */
public class PhotoPickerPlugin implements MethodCallHandler {
    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "photo_picker");
        channel.setMethodCallHandler(new PhotoPickerPlugin(registrar, registrar.activity()));
    }

    private Activity activity;
    private Registrar registrar;
    private ImagePicker imagePicker;
    private Result result;
    private boolean isSetupRunning = false;

    private ImagePickerDelegate imagePickerDelegate = new ImagePickerDelegate() {
        @Override
        public void imagePickerCancel() {
            result.success("");
            isSetupRunning = false;
        }

        @Override
        public void imagePickerError(String error) {
            result.error("Error", error, null);
            isSetupRunning = false;
        }

        @Override
        public void imagePickerDone(String imageUrl) {
            result.success(imageUrl);
            isSetupRunning = false;
        }
    };

    private PhotoPickerPlugin(Registrar registrar, Activity activity) {
        this.registrar = registrar;
        this.activity = activity;
        imagePicker = new ImagePicker(activity, imagePickerDelegate, 400);

        this.registrar.addActivityResultListener(new PluginRegistry.ActivityResultListener() {
            @Override
            public boolean onActivityResult(int requestCode, int resultCode, Intent intent) {
                if (isSetupRunning && imagePicker != null) {
                    return imagePicker.handleActivityResult(requestCode, resultCode, intent);
                }
                return false;
            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (call.method.equals("pickPhoto")) {
            int source = call.argument("source");
            boolean allowEdit = call.argument("allowEdit");
            this.result = result;
            isSetupRunning = true;
            openChooser(source);
        } else {
            result.notImplemented();
        }
    }

    private void openChooser(int source) {
        if (source == 0) imagePicker.cameraSource();
        else if (source == 1) imagePicker.gallerySource();
        else {
            result.error("Error", "Source not supported", null);
            isSetupRunning = false;
        }
    }
}
