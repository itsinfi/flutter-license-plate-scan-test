import 'package:flutter/material.dart';
import 'package:parking_ticket_scan_test/pages/scanner_page.dart';
import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';
import 'package:parking_ticket_scan_test/scanner/carrida_scanner.dart';
import 'package:parking_ticket_scan_test/services/carrida_sdk_service.dart';

class HomePage extends StatefulWidget {
  final String title;
  final List<BaseScanner> scanners;

  const HomePage({super.key, required this.title, required this.scanners});

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.start,
          crossAxisAlignment: CrossAxisAlignment.center,
          children: widget.scanners
              .map<Widget>(
                (BaseScanner s) => Padding(
                  padding: EdgeInsets.all(16.0),
                  child: ElevatedButton(
                    onPressed: () {
                      Navigator.of(context).push(
                        MaterialPageRoute(
                          builder: (context) => ScannerPage(scanner: s),
                        ),
                      );
                    },
                    child: Text(s.name),
                  ),
                ),
              )
              .toList(),
        ),
      ),
    );
  }
}
