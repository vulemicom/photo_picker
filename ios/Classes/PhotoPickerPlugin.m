#import "PhotoPickerPlugin.h"
#import <photo_picker/photo_picker-Swift.h>

@implementation PhotoPickerPlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftPhotoPickerPlugin registerWithRegistrar:registrar];
}
@end
