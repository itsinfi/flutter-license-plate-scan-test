import 'package:flutter/services.dart';

class CarridaSdkBridge {
  static const String CHANNEL = "carrida_sdk_channel";

  static const platform = MethodChannel(CHANNEL);

  static Future<void> initialize() async {
    try {
      print('!!!!!' + await platform.invokeMethod('init_carrida_sdk'));
    } on PlatformException catch (e) {
      print('init_carrida_sdk error');
      print(e.code);
      print(e.message);
    }
  }
}
