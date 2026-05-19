import 'dart:io';

import 'package:flutter/material.dart';
import 'package:parking_ticket_scan_test/scanner/base_scanner.dart';
import 'package:camera/camera.dart' as cam;
import 'package:image/image.dart' as img;

const cam.ResolutionPreset CAMERA_RESOLUTION = cam.ResolutionPreset.high;
const int IMAGE_QUALITY = 90;

class ScannerPage extends StatefulWidget {
  final BaseScanner scanner;

  const ScannerPage({super.key, required this.scanner});

  @override
  State<ScannerPage> createState() => _ScannerPageState();
}

class _ScannerPageState extends State<ScannerPage> {
  cam.CameraController? _controller;
  Future<void>? _initializeControllerFuture;
  int _cameraIndex = 0;
  bool? _result;

  @override
  void initState() {
    super.initState();
    _setupCamera(_cameraIndex);
  }

  Future<void> _setupCamera(int cameraIndex) async {
    final cameras = await cam.availableCameras();
    final camera = cameras[cameraIndex % cameras.length];

    _controller = cam.CameraController(
      camera,
      CAMERA_RESOLUTION,
      enableAudio: false,
    );

    _initializeControllerFuture = _controller!.initialize();

    setState(() {});
  }

  Future<void> _switchCamera() async {
    _controller?.dispose();
    _cameraIndex++;
    await _setupCamera(_cameraIndex);
  }

  Future<void> _handleScan() async {
    if (_controller != null && _controller!.value.isInitialized) {
      final cam.XFile file = await _controller!.takePicture();

      final bytes = await file.readAsBytes();
      final original = img.decodeImage(bytes);

      if (original == null) return;

      final resized = img.copyResize(
        original,
        width: widget.scanner.width,
        height: widget.scanner.height,
      );

      final resizedFile = File(file.path.replaceFirst('.jpg', '_scan.jpg'));

      final finalFile = await resizedFile.writeAsBytes(
        img.encodeJpg(resized, quality: IMAGE_QUALITY),
      );

      final result = await widget.scanner.scan(finalFile, "LTK 429");

      setState(() {
        _result = result;
      });
    }
  }

  @override
  void dispose() {
    _controller?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    late Color containerBackgroundColor;

    switch (_result) {
      case true:
        containerBackgroundColor = Colors.lightGreen.withAlpha(100);
        break;
      case false:
        containerBackgroundColor = Colors.red.withAlpha(100);
        break;
      default:
        containerBackgroundColor = Colors.transparent;
    }

    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.scanner.name),
      ),
      body: _controller == null
          ? const Center(child: CircularProgressIndicator())
          : FutureBuilder(
              future: _initializeControllerFuture,
              builder: (context, snapshot) {
                if (snapshot.connectionState == ConnectionState.done) {
                  return Stack(
                    fit: StackFit.expand,
                    children: [
                      cam.CameraPreview(_controller!),
                      SafeArea(
                        child: Padding(
                          padding: const EdgeInsets.all(16.0),
                          child: Column(
                            children: [
                              Padding(
                                padding: const EdgeInsets.all(16.0),
                                child: Visibility(
                                  visible: _result != null,
                                  child: Container(
                                    color: containerBackgroundColor,
                                    width: double.infinity,
                                    child: Padding(
                                      padding: EdgeInsetsGeometry.all(16.0),
                                      child: Text(
                                        'result: $_result',
                                        textAlign: TextAlign.center,
                                      ),
                                    ),
                                  ),
                                ),
                              ),

                              const Spacer(),

                              Row(
                                mainAxisAlignment:
                                    MainAxisAlignment.spaceBetween,
                                children: [
                                  ElevatedButton(
                                    onPressed: _switchCamera,
                                    child: const Icon(
                                      Icons.switch_camera_outlined,
                                    ),
                                  ),
                                  ElevatedButton(
                                    onPressed: _handleScan,
                                    child: const Text('scan.'),
                                  ),
                                ],
                              ),
                            ],
                          ),
                        ),
                      ),
                    ],
                  );
                } else {
                  return const Center(child: CircularProgressIndicator());
                }
              },
            ),
    );
  }
}
