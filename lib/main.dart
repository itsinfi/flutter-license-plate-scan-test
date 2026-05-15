import 'package:flutter/material.dart';
import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:parking_ticket_scan_test/services/carrida_sdk_service.dart';
import 'package:parking_ticket_scan_test/pages/home_page.dart';
import 'package:parking_ticket_scan_test/services/environment_service.dart';

void main() {
  runApp(const App());
}

class App extends StatefulWidget {
  const App({super.key});

  final title = 'License Plat Scan Test';

  @override
  State<App> createState() => AppState();
}

class AppState extends State<App> {
  Future<void>? _initAppFuture;
  bool isInitialized = false;

  late EnvironmentService environmentService;
  late CarridaSdkService carridaSdkService;

  @override
  void initState() {
    super.initState();

    carridaSdkService = CarridaSdkService();

    _initAppFuture = initApp();

    setState(() {
      isInitialized = true;
    });
  }

  Future<void> initApp() async {
    environmentService = EnvironmentService();
    await environmentService.init(EnvironmentServiceArgs('.env'));

    carridaSdkService = CarridaSdkService();
    String licenseKey = environmentService.get('CARRIDA_SDK_LICENSE_KEY') ?? '';
    await carridaSdkService.init(CarridaSdkServiceArgs(licenseKey));
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
                  return HomePage(title: widget.title);
                } else {
                  return const Center(child: CircularProgressIndicator());
                }
              },
            ),
    );
  }
}
