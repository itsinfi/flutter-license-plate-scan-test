package com.example.parking_ticket_scan_test.carrida

import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall

import at.carrida.sdk.license.License
import at.carrida.sdk.license.EMSException
import at.carrida.sdk.lpr.LPR
import at.carrida.sdk.lpr.CaptureImage
import at.carrida.sdk.lpr.Result
import at.carrida.sdk.lpr.LicenseException

import android.os.Handler
import android.os.Looper
import android.graphics.BitmapFactory
import android.graphics.Bitmap

import java.io.File
import org.w3c.dom.Document

public class CarridaSdkWrapper {

    companion object {

        private val CHANNEL: String = "carrida_sdk_channel"

        private val INIT_METHOD_NAME: String = "init_carrida_sdk"
        private val INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME: String = "carrida_sdk_license_key"
        private val INIT_METHOD_DEVICE_ACTIVATION_KEY_ARGUMENT_NAME: String = "carrida_sdk_device_activation_key"

        private val READ_LP_FROM_IMAGE_METHOD_NAME: String = "read_license_plate_from_image"
        private val READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME: String = "license_plate_image_path"

        private val LPR_CONFIG_PATH = "app/lpr.ini"

        private val LICENSE_FINGERPRINT: String = LPR.getFingerprint()
        private val LICENSE_COMMENT: String = "TEST"

        private var license: License? = null
        private var activationLicense: String? = null
        // var updateLicense: String? = null

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

        private fun retrieveLicenseDetails(): String? {
            try {
                val licenseDetails: String? = license!!.retireveDetails()

                android.util.Log.d(
                    INIT_METHOD_NAME,
                    "License details: $licenseDetails"
                )

                android.util.Log.d(
                    INIT_METHOD_NAME,
                    "Fingerprint: $LICENSE_FINGERPRINT"
                )

                return licenseDetails
            } catch (e: Exception) {
                android.util.Log.e(
                    INIT_METHOD_NAME,
                    "Failed to read license details..."
                )

                return null
            }
        }

        private fun handleLicenseActivation(deviceActivationKey: String?) {
            /*try {*/
            // TODO: in future: store productKeyId per fingerprint to retrieve deviceActivationKey
            // val licenseDetails: String? = retrieveLicenseDetails()

            // initial activation of license

            android.util.Log.d(
                INIT_METHOD_NAME,
                "Building activation XML..."
            )

            val activationXML: Document? = license!!.buildActivationXML(LICENSE_FINGERPRINT, LICENSE_COMMENT)

            if (activationXML == null) {
                throw Exception("Failed to create activation XML")
            }

            activationLicense = license!!.retireveLicense(activationXML)

            android.util.Log.d(
                INIT_METHOD_NAME,
                "Activation license: $activationLicense"
            )

            if (activationLicense == null) {
                throw Exception("Failed to read activation license")
            }
            /*} catch (e: EMSException) {
                // handle activation for the case of the activation document having been created, but lpr still not being activated

                if (e.message != "831") {
                    throw e
                }

                android.util.Log.d(
                    INIT_METHOD_NAME,
                    "Handling license already being activated..."
                )

                // val updateXML: Document? = license!!.buildUpdateXML(LICENSE_FINGERPRINT, LICENSE_COMMENT)

                // if (updateXML == null) {
                //     throw Exception("Failed to create update XML")
                // }

                // activationLicense = license!!.retireveLicense(updateXML)

                // android.util.Log.d(
                //     INIT_METHOD_NAME,
                //     "Update license: $activationLicense"
                // )

                // if (activationLicense == null) {
                //     throw Exception("Failed to read activation license")
                // }

                if (deviceActivationKey == null) {
                    throw Exception("Device was already activated. Provide the CARRIDA_SDK_DEVICE_ACTIVATION_KEY in the .env to continue (check the license details for the value)") // TODO: remove and handle fingerprint based retrieval via backend
                }

                activationLicense = license!!.retrieveLicense(deviceActivationKey)

                if (activationLicense == null) {
                    throw Exception("Failed to read activation license")
                }
            }

            android.util.Log.d(
                INIT_METHOD_NAME,
                "Activating LPR using activation license..."
            )

            LPR.activateLicense(activationLicense!!)
                
            lprActivated = LPR.isActivated()

            android.util.Log.d(
                INIT_METHOD_NAME,
                "LPR activation status: $lprActivated"
            )

            if (!lprActivated) {
                throw Exception("LPR could not be activated")
            }*/
        } 

        private fun initCarridaSdk(call: MethodCall, result: MethodChannel.Result) {
            Thread {
                try {

                    val licenseKey: String = call.argument<String>(INIT_METHOD_LICENSE_KEY_ARGUMENT_NAME) ?: ""
                    val deviceActivationKey: String? = call.argument<String>(INIT_METHOD_DEVICE_ACTIVATION_KEY_ARGUMENT_NAME)

                    // android.util.Log.d(
                    //     INIT_METHOD_NAME,
                    //     "LICENSE KEY... $licenseKey"
                    // )

                    // android.util.Log.d(
                    //     INIT_METHOD_NAME,
                    //     "DEVICE ACTIVATION KEY... $deviceActivationKey"
                    // )

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "Starting SDK login..."
                    )

                    if (license == null) {
                        license = License(licenseKey)
                    }

                    val loginResult: Boolean = license!!.login()

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "Login successful: $loginResult"
                    )

                    if (!loginResult) {
                        throw Exception("Carrida SDK Login failed")
                    }

                    var lprActivated = LPR.isActivated()

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "LPR activation status: $lprActivated"
                    )

                    if (!lprActivated) {
                        handleLicenseActivation(deviceActivationKey)
                    }

                    lpr = LPR()

                    android.util.Log.d(
                        INIT_METHOD_NAME,
                        "Finishing initialization..."
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.success("SDK initialized; Login successful: $loginResult")
                    }
                } catch (e: Exception) {
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

        private fun readLicensePlateFromImage(call: MethodCall, result: MethodChannel.Result) {
            Thread {
                try {

                    val imagePath: String = call.argument<String>(READ_LP_FROM_IMAGE_PATH_ARGUMENT_NAME) ?: ""

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "IMAGE PATH... $imagePath"
                    )

                    val file: File = File(imagePath)

                    if (!file.exists()) {
                        throw Exception("License plate image does not exist")
                    }

                    val bitmap: Bitmap = BitmapFactory.decodeFile(imagePath)
                    val image: CaptureImage = CaptureImage.fromBitmap(bitmap)

                    // val methodsMessage = buildString {
                    //     appendLine("loaded image; image data:")
                    //     CaptureImage::class.java.declaredMethods
                    //         .sortedBy { it.name }
                    //         .forEach { method ->
                    //             appendLine(
                    //                 "${method.name}(" +
                    //                 method.parameterTypes.joinToString { it.simpleName } +
                    //                 "): ${method.returnType.simpleName}"
                    //             )
                    //         }
                    // }

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        // methodsMessage
                        "loaded image; image data:" + "\n" +
                        "-\tname: " + image.name + "\n" +
                        "-\ttimestamp: " + image.timestamp + "\n" +
                        "-\twidth: " + image.width + "\n" +
                        "-\theight: " + image.height + "\n" +
                        "-\trotation: " + image.rotation
                    )

                    if (lpr == null) {
                        throw Exception("LPR is not initialized")
                    }

                    // if (!LPR.isActivated()) {
                    //     throw Exception("LPR is not activated")
                    // }

                    if (lpr!!.isInitialised()) {
                        throw Exception("LPR is already being used by another thread")
                    }

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "Initializing LPR..."
                    )

                    lpr!!.init(LPR_CONFIG_PATH)

                    if (!lpr!!.isInitialised()) {
                        throw Exception("LPR could not be initialized")
                    }

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "Starting LPR processing..."
                    )

                    val lprResult: Array<out Result> = lpr!!.process(image)

                    android.util.Log.d(
                        READ_LP_FROM_IMAGE_METHOD_NAME,
                        "LPR result: $lprResult"
                    )

                    Handler(Looper.getMainLooper()).post {
                        result.success("LPR result success:" + lprResult.toString())
                    }
                } catch (e: Exception) {
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
                } finally {
                    if (lpr != null && lpr!!.isInitialised()) {
                        android.util.Log.d(
                            READ_LP_FROM_IMAGE_METHOD_NAME,
                            "Finishing LPR process..."
                        )

                        lpr!!.finish()

                        android.util.Log.d(
                            READ_LP_FROM_IMAGE_METHOD_NAME,
                            "Finished LPR process!"
                        )
                    }
                }
            }.start()
        }
    }

}