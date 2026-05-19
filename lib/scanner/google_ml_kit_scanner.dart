import 'dart:io';

import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';
import 'package:parking_ticket_scan_test/services/google_ml_kit_service.dart';

class GoogleMlKitScanner extends BaseScanner {
  @override
  String name = "GOOGLE_ML_KIT";

  @override
  int width = 1280;

  @override
  int height = 720;

  GoogleMlKitService service;
  GoogleMlKitScanner(this.service);

  @override
  Future<bool> scan(File image, String expectedLicensePlate) async {
    print('!!!!!scann');

    final String? licensePlate = await service.readLicensePlateFromImage(image);

    print(
      'licensePlate: $licensePlate; expectedLicensePlate: $expectedLicensePlate',
    );

    if (licensePlate == null) {
      return false;
    }

    return licensePlate
        .split('\n')
        .any(
          (text) =>
              text.toLowerCase().contains(expectedLicensePlate.toLowerCase()),
        );
  }
}
