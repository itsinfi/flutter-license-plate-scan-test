import 'package:flutter_dotenv/flutter_dotenv.dart';
import 'package:parking_ticket_scan_test/services/base_service.dart';

class EnvironmentServiceArgs {
  late String envFileLocation;
  EnvironmentServiceArgs(this.envFileLocation);
}

class EnvironmentService extends BaseService<EnvironmentServiceArgs> {
  late Map<String, String> env;

  @override
  Future<void> init(EnvironmentServiceArgs args) async {
    await dotenv.load(fileName: args.envFileLocation);
    env = dotenv.env;
    isInitialized = true;
  }

  String? get(String key) {
    return env[key];
  }
}
