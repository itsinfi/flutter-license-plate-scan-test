import 'dart:io';

import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';

class CarridaScanner extends BaseScanner {
  @override
  String name = "CARRIDA";

  @override
  int width = 1280;

  @override
  int height = 720;

  @override
  Future<bool> scan(File image) {
    return Future.microtask(() => true);
  }
}
