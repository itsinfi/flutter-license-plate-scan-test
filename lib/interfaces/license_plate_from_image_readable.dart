import 'dart:io';

abstract class LicensePlateFromImageReadable {
  Future<String?> readLicensePlateFromImage(File image);
}
