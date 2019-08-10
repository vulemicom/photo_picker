package product.skglobal.photopicker.picker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import android.util.Log;
import java.io.File;
import java.net.URI;

/**
 * This is the entry point of ImagePicker library
 * Created by egistli on 2016/5/11.
 */
public class ImagePicker {
    public static final String TAG = ImagePicker.class.getSimpleName();

    private static final int REQUEST_PICK_IMAGE = 110;

    private static final String TEMP_IMAGE_NAME = "image-picker-temp.jpg";

    final Activity activity;
    final Context context;
    private final ImagePickerDelegate callback;
    private final int maxLength;

    public ImagePicker(final Activity activity, final ImagePickerDelegate callback, final int maxLength) {
        this.activity = activity;
        this.context = activity;
        this.callback = callback;
        this.maxLength = maxLength;
    }

    /**
     * This is to process activity result for activities
     * Just delegate the handling in activity by calling
     * imagePicker.handleActivityResult(), when it's handled
     * the result will be `true`, otherwise it'll be `false`.
     */
    public boolean handleActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (resultCode == Activity.RESULT_CANCELED) {
            callback.imagePickerCancel();
            return true;
        }

        if (requestCode == REQUEST_PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "RAWRESULT" + CropImage.getPickImageResultUri(activity, data).toString());
            CropImage.activity(CropImage.getPickImageResultUri(activity, data))
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(activity);
            return true;
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            try {
                if (resultCode == Activity.RESULT_OK) {

                    Uri resultUri = result.getUri();
                    String path = new File(new URI(resultUri.toString())).getAbsolutePath();
                    boolean scaleResult = BitmapUtility.scaleImage(path, maxLength, maxLength);
                    Log.d(TAG, "Scale result " + scaleResult);
                    callback.imagePickerDone(path);
                } else {
                    callback.imagePickerError("Image not available in local device. Please download this image first");
                }
            } catch (Exception e) {
                callback.imagePickerError("Fail when create file from uri");
            }
            return true;
        }
        return false;
    }

    public void cameraSource() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        final Intent takePhotoIntent = CropImage.getCameraIntent(activity, null);
        activity.startActivityForResult(takePhotoIntent, REQUEST_PICK_IMAGE);
    }

    public void gallerySource() {
        final Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        activity.startActivityForResult(pickIntent, REQUEST_PICK_IMAGE);
    }

//    private File getTempFile() {
//        File imageFile = new File(context.getExternalCacheDir(), TEMP_IMAGE_NAME);
//        if (imageFile.exists()) {
//            imageFile.delete();
//        }
//        imageFile.getParentFile().mkdirs();
//        return imageFile;
//    }
}
