import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';
import 'package:parking_ticket_scan_test/scanner/carrida_scanner.dart';
import 'package:parking_ticket_scan_test/scanner/google_ml_kit_scanner.dart';
import 'package:parking_ticket_scan_test/services/carrida_sdk_service.dart';
import 'package:parking_ticket_scan_test/pages/home_page.dart';
import 'package:parking_ticket_scan_test/services/environment_service.dart';
import 'package:parking_ticket_scan_test/services/google_ml_kit_service.dart';

void main() {
  runApp(const App());
}

class App extends StatefulWidget {
  const App({super.key});

  final title = 'License Plate Scan Test';

  @override
  State<App> createState() => AppState();
}

class AppState extends State<App> {
  Future<void>? _initAppFuture;
  bool isInitialized = false;

  late EnvironmentService environmentService;
  late CarridaSdkService carridaSdkService;
  late GoogleMlKitService googleMlKitService;

  final List<BaseScanner> scanners = [];

  @override
  void initState() {
    super.initState();

    _initAppFuture = initApp();

    setState(() {
      isInitialized = true;
    });
  }

  Future<void> configureCarridaScanner() async {
    carridaSdkService = CarridaSdkService();
    String licenseKey = environmentService.get('CARRIDA_SDK_LICENSE_KEY') ?? '';
    String? deviceActivationKey = environmentService.get(
      'CARRIDA_SDK_DEVICE_ACTIVATION_KEY',
    );
    await carridaSdkService.init(
      CarridaSdkServiceArgs(licenseKey, deviceActivationKey),
    );
    final carridaScanner = CarridaScanner(carridaSdkService);
    scanners.add(carridaScanner);
  }

  Future<void> configureGoogleMlKitScanner() async {
    googleMlKitService = GoogleMlKitService();
    await googleMlKitService.init(GoogleMlKitServiceArgs());
    final googleMlKitScanner = GoogleMlKitScanner(googleMlKitService);
    scanners.add(googleMlKitScanner);
  }

  Future<void> initApp() async {
    environmentService = EnvironmentService();
    await environmentService.init(EnvironmentServiceArgs('.env'));

    // // Configure CARRIDA scanner
    // await configureCarridaScanner();

    // Configure Google ML Kit scanner
    await configureGoogleMlKitScanner();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: widget.title,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: !isInitialized
          ? const Center(child: CircularProgressIndicator())
          : FutureBuilder(
              future: _initAppFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.done) {
                  return HomePage(title: widget.title, scanners: scanners);
                } else {
                  return const Center(child: CircularProgressIndicator());
                }
              },
            ),
    );
  }
}
