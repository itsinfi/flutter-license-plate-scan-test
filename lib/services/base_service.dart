abstract class BaseService<T> {
  bool isInitialized = false;
  Future<void> init(T args);
}
