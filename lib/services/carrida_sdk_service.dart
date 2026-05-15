import 'package:flutter/services.dart';
import 'package:parking_ticket_scan_test/services/base_service.dart';

const String CHANNEL = 'carrida_sdk_channel';
const String INIT_METHOD_NAME = 'init_carrida_sdk';
const String LICENSE_KEY_ARGUMENT_NAME = 'carrida_sdk_license_key';

class CarridaSdkServiceArgs {
  late String licenseKey;
  CarridaSdkServiceArgs(this.licenseKey);
}

class CarridaSdkService extends BaseService<CarridaSdkServiceArgs> {
  final platform = MethodChannel(CHANNEL);

  @override
  Future<void> init(CarridaSdkServiceArgs args) async {
    try {
      final result = await platform.invokeMethod(INIT_METHOD_NAME, {
        LICENSE_KEY_ARGUMENT_NAME: args.licenseKey,
      });

      print('$INIT_METHOD_NAME result: $result');
    } on PlatformException catch (e) {
      print('$INIT_METHOD_NAME error\n${e.code}\n${e.message}');
    }

    isInitialized = true;
  }
}
