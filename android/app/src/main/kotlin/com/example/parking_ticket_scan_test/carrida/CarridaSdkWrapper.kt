package com.example.parking_ticket_scan_test.carrida

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

import at.carrida.sdk.license.License

public class CarridaSdkWrapper {

    companion object {

        private val CHANNEL = "carrida_sdk_channel"
        private val LICENSE_KEY = "" // TODO: remove from here
        
        fun configure(flutterEngine: FlutterEngine) {
            MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                this.CHANNEL
            ).setMethodCallHandler { call, result -> 

                when (call.method) {
                    "init_carrida_sdk" -> this.initCarridaSdk(result)
                    else -> result.notImplemented()
                }
            }
        }

        fun initCarridaSdk(result: MethodChannel.Result) {
            try {
                val loginResult = License(this.LICENSE_KEY).login()
                result.success("SDK initialized; Login successful:" + loginResult)
            }
            catch(e: Exception) {
                result.error("SDK_INITIALIZATION_ERROR", e.stackTraceToString(), null)
            }
        }
    }

}