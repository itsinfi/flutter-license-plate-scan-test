import 'dart:io';

import 'package:flutter/services.dart';
import 'package:parking_ticket_scan_test/services/base_service.dart';

const String CHANNEL = 'carrida_sdk_channel';

const String INIT_METHOD_NAME = 'init_carrida_sdk';
const String INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME = 'carrida_sdk_license_key';
const String INIT_METHOD_DEVICE_ACTIVATION_KEY_ARGUMENT_NAME =
    "carrida_sdk_device_activation_key";

const String READ_LP_FROM_IMAGE_METHOD_NAME = 'read_license_plate_from_image';
const String READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME = 'license_plate_image_path';

class CarridaSdkServiceArgs {
  late String licenseKey;
  String? deviceActivationKey;
  CarridaSdkServiceArgs(this.licenseKey, this.deviceActivationKey);
}

class CarridaSdkService extends BaseService<CarridaSdkServiceArgs> {
  final platform = MethodChannel(CHANNEL);

  @override
  Future<void> init(CarridaSdkServiceArgs args) async {
    try {
      final result = await platform.invokeMethod(INIT_METHOD_NAME, {
        INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME: args.licenseKey,
        INIT_METHOD_DEVICE_ACTIVATION_KEY_ARGUMENT_NAME:
            args.deviceActivationKey,
      });

      print('$INIT_METHOD_NAME result: $result');
    } on PlatformException catch (e) {
      print('$INIT_METHOD_NAME error\n${e.code}\n${e.message}');
    }

    isInitialized = true;
  }

  Future<String?> readLicensePlateFromImage(String imagePath) async {
    try {
      final result = await platform.invokeMethod(
        READ_LP_FROM_IMAGE_METHOD_NAME,
        {READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME: imagePath},
      );

      print('$READ_LP_FROM_IMAGE_METHOD_NAME result: $result');
    } on PlatformException catch (e) {
      print('$READ_LP_FROM_IMAGE_METHOD_NAME error\n${e.code}\n${e.message}');
    }
  }
}
