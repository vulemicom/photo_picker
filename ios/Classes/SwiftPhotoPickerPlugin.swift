import Flutter
import UIKit

public class SwiftPhotoPickerPlugin: NSObject, FlutterPlugin,UINavigationControllerDelegate, UIImagePickerControllerDelegate {
    let SOURCE_CAMERA = 0
    let SOURCE_GALLERY = 1
    var viewController: UIViewController
    var result:FlutterResult? = nil
    
    public init(viewController:UIViewController) {
        self.viewController = viewController
    }
    
    public static func register(with registrar: FlutterPluginRegistrar) {
        let channel = FlutterMethodChannel(name: "photo_picker", binaryMessenger: registrar.messenger())
        let rootVC = UIApplication.shared.delegate?.window??.rootViewController
        let instance = SwiftPhotoPickerPlugin(viewController: rootVC!)
        registrar.addMethodCallDelegate(instance, channel: channel)
    }
    
    
    public func handle(_ call: FlutterMethodCall, result: @escaping FlutterResult) {
        if ("pickPhoto" == call.method) {
            let arguments = call.arguments as! NSDictionary
            let source = arguments["source"] as! Int
            let allowEdit = arguments["allowEdit"] as! Bool
            self.result = result
            self.pickPhoto(source: source, allowEdit: allowEdit, flutterResult: result)
        }
        else {
            result(FlutterMethodNotImplemented)
        }
    }
    
    private func pickPhoto(source: Int, allowEdit: Bool, flutterResult: FlutterResult) {
        let imgPicker = UIImagePickerController()
        imgPicker.delegate = self
        imgPicker.allowsEditing = allowEdit
        if source == SOURCE_CAMERA {
            if UIImagePickerController.isSourceTypeAvailable(.camera) {
                imgPicker.sourceType = .camera
            }
            else {
                flutterResult(FlutterError.init(code: "invalid_source", message: "Not support camera", details: nil))
                return
            }
        }
        else if source == SOURCE_GALLERY {
            if UIImagePickerController.isSourceTypeAvailable(.photoLibrary) {
                imgPicker.sourceType = .photoLibrary
            }
            else {
                flutterResult(FlutterError.init(code: "invalid_source", message: "Not support photo library", details: nil))
                return
            }
        }
        else {
            flutterResult(FlutterError.init(code: "invalid_source", message: "Invalid image source", details: nil))
            return
        }
        viewController.present(imgPicker, animated: true, completion: nil)
    }
    
    public func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [String : Any]) {
        let image = info[UIImagePickerControllerEditedImage] as? UIImage
        let resizedImage = image?.resizeWith(width: 400.0)
        let imageData = UIImagePNGRepresentation(resizedImage!)
        let guid = ProcessInfo.processInfo.globallyUniqueString
        let tempFile = "photo_picker_\(guid).png"
        let tempPath = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent(tempFile).path

        if FileManager.default.createFile(atPath: tempPath, contents: imageData, attributes: nil) {
            result!(tempPath)
        }
        else {
            result!(FlutterError.init(code: "create_error", message: "Temporary file could not be created", details: nil))
        }
        
        viewController.dismiss(animated: true, completion: nil)
    }
    
    
}

extension UIImage {
    
    func resizeWith(percentage: CGFloat) -> UIImage? {
        let imageView = UIImageView(frame: CGRect(origin: .zero, size: CGSize(width: size.width * percentage, height: size.height * percentage)))
        imageView.contentMode = .scaleAspectFit
        imageView.image = self
        UIGraphicsBeginImageContextWithOptions(imageView.bounds.size, false, scale)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        imageView.layer.render(in: context)
        guard let result = UIGraphicsGetImageFromCurrentImageContext() else { return nil }
        UIGraphicsEndImageContext()
        return result
    }
    
    func resizeWith(width: CGFloat) -> UIImage? {
        let imageView = UIImageView(frame: CGRect(origin: .zero, size: CGSize(width: width, height: CGFloat(ceil(width/size.width * size.height)))))
        imageView.contentMode = .scaleAspectFit
        imageView.image = self
        UIGraphicsBeginImageContextWithOptions(imageView.bounds.size, false, scale)
        guard let context = UIGraphicsGetCurrentContext() else { return nil }
        imageView.layer.render(in: context)
        guard let result = UIGraphicsGetImageFromCurrentImageContext() else { return nil }
        UIGraphicsEndImageContext()
        return result
    }
    
}
