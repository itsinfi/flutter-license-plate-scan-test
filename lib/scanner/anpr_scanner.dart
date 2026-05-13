import 'dart:io';

import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';

class AnprScanner extends BaseScanner {
  @override
  String name = "ANPR";

  @override
  int width = 1280;

  @override
  int height = 720;

  @override
  Future<bool> scan(File image) {
    return Future.microtask(() => false);
  }
}
