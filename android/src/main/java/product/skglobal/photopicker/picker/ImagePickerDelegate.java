package product.skglobal.photopicker.picker;

public interface ImagePickerDelegate {
    void imagePickerCancel();

    void imagePickerError(final String error);

    void imagePickerDone(final String imageUrl);
}
