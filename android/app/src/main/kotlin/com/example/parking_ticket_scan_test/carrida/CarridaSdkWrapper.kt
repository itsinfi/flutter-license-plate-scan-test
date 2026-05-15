package com.example.parking_ticket_scan_test.carrida

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall

import android.os.Handler
import android.os.Looper

import at.carrida.sdk.license.License

public class CarridaSdkWrapper {

    companion object {

        private val CHANNEL: String = "carrida_sdk_channel"
        private val INIT_METHOD_NAME: String = "init_carrida_sdk";
        private val LICENSE_KEY_ARGUMENT_NAME: String = "carrida_sdk_license_key";
        
        fun configure(flutterEngine: FlutterEngine) {
            MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                this.CHANNEL
            ).setMethodCallHandler { call, result -> 

                when (call.method) {
                    INIT_METHOD_NAME -> this.initCarridaSdk(call, result)
                    else -> result.notImplemented()
                }
            }
        }

        fun initCarridaSdk(call: MethodCall, result: MethodChannel.Result) {

            Thread {
                try {

                    val licenseKey: String = call.argument<String>(LICENSE_KEY_ARGUMENT_NAME) ?: ""

                    android.util.Log.d(
                        "Init CarridaSDK",
                        "KEY... $licenseKey"
                    )

                    android.util.Log.d(
                        "Init CarridaSDK",
                        "Starting SDK login..."
                    )

                    val loginResult = License(licenseKey).login()

                    android.util.Log.d(
                        "Init CarridaSDK",
                        "Login successful: $loginResult"
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.success("SDK initialized; Login successful:" + loginResult)
                    }
                }
                catch(e: Exception) {
                    val errorMessage = buildString {
                        appendLine("Carrida SDK initialization failed")
                        appendLine()
                        appendLine("Exception Type:")
                        appendLine(e::class.java.name)
                        appendLine()
                        appendLine("Message:")
                        appendLine(e.message ?: "No message")
                        appendLine()
                        appendLine("Cause:")
                        appendLine(e.cause?.toString() ?: "No cause")
                        appendLine()
                        appendLine("Stacktrace:")
                        appendLine(e.stackTraceToString())
                    }

                    android.util.Log.e(
                        "CarridaSDK",
                        errorMessage
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.error(
                            "SDK_INITIALIZATION_ERROR",
                            errorMessage,
                            null
                        )
                    }
                }
            }.start()
        }
    }

}