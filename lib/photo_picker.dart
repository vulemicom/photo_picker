import 'dart:async';

import 'package:flutter/services.dart';

class PhotoPicker {
  static const MethodChannel _channel = const MethodChannel('photo_picker');

  static Future<String> pickPhoto(int source, bool allowEdit) async {
    try {
      var result = await _channel.invokeMethod('pickPhoto', <String, dynamic>{
        'source': source,
        'allowEdit': allowEdit,
      }) as String;
      return result;
    } on PlatformException catch (e) {
      throw 'pickPhoto failed: ${e.message}';
    }
  }
}
