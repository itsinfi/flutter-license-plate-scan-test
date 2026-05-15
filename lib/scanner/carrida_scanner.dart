import 'dart:io';

import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';
import 'package:parking_ticket_scan_test/services/carrida_sdk_service.dart';

class CarridaScanner extends BaseScanner {
  @override
  String name = "CARRIDA";

  @override
  int width = 1280;

  @override
  int height = 720;

  CarridaSdkService service;
  CarridaScanner(this.service);

  @override
  Future<bool> scan(File image) async {
    final String? licensePlate = await service.readLicensePlateFromImage(
      image.path,
    );

    return await Future.microtask(() => true);
  }
}
