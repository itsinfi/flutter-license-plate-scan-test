import 'package:flutter/material.dart';
import 'package:parking_ticket_scan_test/carrida/carrida_sdk_bridge.dart';
import 'package:parking_ticket_scan_test/pages/home_page.dart';

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
  Future<void>? _initializeCarridaSdkFuture;
  bool carridaSdkIsInitialized = false;

  @override
  void initState() {
    super.initState();

    _initializeCarridaSdkFuture = CarridaSdkBridge.initialize();

    setState(() {
      carridaSdkIsInitialized = true;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: widget.title,
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.deepPurple),
      ),
      home: !carridaSdkIsInitialized
          ? const Center(child: CircularProgressIndicator())
          : FutureBuilder(
              future: _initializeCarridaSdkFuture,
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
