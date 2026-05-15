package com.example.parking_ticket_scan_test.carrida

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall

import at.carrida.sdk.license.License
import at.carrida.sdk.lpr.LPR
import at.carrida.sdk.lpr.CaptureImage

import android.os.Handler
import android.os.Looper
import android.graphics.BitmapFactory

import java.io.File

public class CarridaSdkWrapper {

    companion object {

        private val CHANNEL: String = "carrida_sdk_channel"

        private val INIT_METHOD_NAME: String = "init_carrida_sdk"
        private val INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME: String = "carrida_sdk_license_key"

        private val READ_LP_FROM_IMAGE_METHOD_NAME: String = "read_license_plate_from_image"
        private val READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME: String = "license_plate_image_path"

        private var license: License? = null
        private var lpr: LPR? = null
        
        fun configure(flutterEngine: FlutterEngine) {
            MethodChannel(
                flutterEngine.dartExecutor.binaryMessenger,
                CHANNEL
            ).setMethodCallHandler { call, result -> 

                when (call.method) {
                    INIT_METHOD_NAME -> initCarridaSdk(call, result)
                    READ_LP_FROM_IMAGE_METHOD_NAME -> readLicensePlateFromImage(call, result)
                    else -> result.notImplemented()
                }
            }
        }

        fun initCarridaSdk(call: MethodCall, result: MethodChannel.Result) {
            Thread {
                try {

                    val licenseKey: String = call.argument<String>(INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME) ?: ""

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "KEY... $licenseKey"
                    )

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "Starting SDK login..."
                    )

                    if (license == null) {
                        license = License(licenseKey)
                        lpr = LPR()
                    }

                    val loginResult = license!!.login()

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "Login successful: $loginResult"
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.success("SDK initialized; Login successful:" + loginResult)
                    }
                }
                catch(e: Exception) {
                    val errorMessage = buildString {
                        appendLine("${INIT_METHOD_NAME} failed")
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
                        INIT_METHOD_NAME,
                        errorMessage
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.error(
                            "${INIT_METHOD_NAME}_error",
                            errorMessage,
                            null
                        )
                    }
                }
            }.start()
        }

        fun readLicensePlateFromImage(call: MethodCall, result: MethodChannel.Result) {
            Thread {
                try {

                    val imagePath: String = call.argument<String>(READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME) ?: ""

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "IMAGE PATH... $imagePath"
                    )

                    val file = File(imagePath)

                    if (!file.exists()) {
                        throw Exception("LICENSE PLATE IMAGE DOES NOT EXIST")
                    }

                    val bitmap = BitmapFactory.decodeFile(imagePath)
                    val image = CaptureImage.fromBitmap(bitmap)

                    val methodsMessage = buildString {
                        appendLine("loaded image; image data:")
                        CaptureImage::class.java.declaredMethods
                            .sortedBy { it.name }
                            .forEach { method ->
                                appendLine(
                                    "${method.name}(" +
                                    method.parameterTypes.joinToString { it.simpleName } +
                                    "): ${method.returnType.simpleName}"
                                )
                            }
                    }

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        methodsMessage
                        // "loaded image; image data:" + "\n" +
                        // "-\tname: " + image.getName() + "\n" +
                        // "-\ttimestamp: " + image.getTimestamp() + "\n" +
                        // "-\twidth: " + image.getWidth() + "\n" +
                        // "-\theight: " + image.getHeight() + "\n" +
                        // "-\trotation: " + image.getRotation()
                    )

                    if (lpr == null) {
                        throw Exception("LPR WAS NOT INITIALIZED")
                    }

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "Starting LPR processing..."
                    )

                    val lprResult = null // TODO: continue here; add !!

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "LPR result: $lprResult"
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.success("LPR result success:" + lprResult)
                    }
                }
                catch(e: Exception) {
                    val errorMessage = buildString {
                        appendLine("${READ_LP_FROM_IMAGE_METHOD_NAME} failed")
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
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        errorMessage
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.error(
                            "${READ_LP_FROM_IMAGE_METHOD_NAME}_error",
                            errorMessage,
                            null
                        )
                    }
                }
            }.start()
        }
    }

}