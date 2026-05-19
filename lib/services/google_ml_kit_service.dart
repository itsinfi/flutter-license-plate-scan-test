import 'dart:io';

import 'package:google_mlkit_text_recognition/google_mlkit_text_recognition.dart';
import 'package:parking_ticket_scan_test/interfaces/license_plate_from_image_readable.dart';
import 'package:parking_ticket_scan_test/services/base_service.dart';

class GoogleMlKitServiceArgs {
  TextRecognitionScript script;
  GoogleMlKitServiceArgs() : script = TextRecognitionScript.latin;
}

class GoogleMlKitService extends BaseService<GoogleMlKitServiceArgs>
    implements LicensePlateFromImageReadable {
  TextRecognizer? textRecognizer;

  @override
  Future<void> init(GoogleMlKitServiceArgs args) async {
    textRecognizer = TextRecognizer(script: args.script);
    isInitialized = true;
  }

  @override
  Future<String?> readLicensePlateFromImage(File image) async {
    InputImage inputImage = InputImage.fromFile(image);

    if (!isInitialized || textRecognizer == null) {
      throw Exception('GoogleMlKitService was not initialized yet');
    }

    try {
      final RecognizedText recognizedText = await textRecognizer!.processImage(
        inputImage,
      );

      return recognizedText.text;
    } finally {
      textRecognizer?.close();
    }
  }
}
