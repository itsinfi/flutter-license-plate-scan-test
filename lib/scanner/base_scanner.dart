import 'dart:io';

abstract class BaseScanner {
  abstract String name;

  abstract int width;
  abstract int height;

  Future<bool> scan(File image);
}
