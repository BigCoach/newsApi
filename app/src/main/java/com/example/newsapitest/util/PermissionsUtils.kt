package com.example.newsapitest.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment


object PermissionsUtils {

    val REQUEST_STORAGE = 0
    val REQUEST_LOCATION = 1
    val REQUEST_PHONE_STATE = 2
    val REQUEST_AUDIO_RECORD = 3
    val REQUEST_CALL_PHONE = 4
    val REQUEST_CAMERA = 5
    val REQUEST_CONTACT = 6
    val REQUEST_CAMERA_FOR_PROFILE_PHOTO = 7
    val REQUEST_STORAGE_FOR_PROFILE_PHOTO = 8
    val REQUEST_CAMERA_AUDIO = 9

    val REQUEST_CAMERA_AND_STORAGE = 10


    var PERMISSIONS_LOCATION = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    var PERMISSION_CALL = arrayOf(Manifest.permission.CALL_PHONE)

    var PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var PERMISSIONS_CAMERA_AND_STORAGE = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    var PERMISSIONS_RECORD_AUDIO = arrayOf(Manifest.permission.RECORD_AUDIO)
    var PERMISSION_CAMERA = arrayOf(Manifest.permission.CAMERA)
    var PERMISSION_CONTACT = arrayOf(Manifest.permission.READ_CONTACTS)


    fun verifyPermissions(grantResults: IntArray): Boolean {
        if (grantResults.isEmpty()) {
            return false
        }

        for (result in grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    fun shouldShowRequestForLocationPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    fun shouldShowRequestForAudioPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.RECORD_AUDIO
        )
    }


    fun shouldShowRequestForStoragePermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) || ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    fun shouldShowRequestForStoragePermission(fragment: Fragment): Boolean {
        return fragment.shouldShowRequestPermissionRationale(
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) || fragment.shouldShowRequestPermissionRationale(
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
    }

    fun shouldShowRequestForCallPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CALL_PHONE
        )
    }

    fun shouldShowRequestForCameraPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.CAMERA
        )
    }

    fun shouldShowRequestForContactPermission(activity: Activity): Boolean {
        return ActivityCompat.shouldShowRequestPermissionRationale(
            activity,
            Manifest.permission.READ_CONTACTS
        )
    }

    fun needsStoragePermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkForCameraAndStoragePermission(activity: Activity): Boolean {
        return (ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED)
    }


    fun checkSelfPermissionForLocation(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfPermissionForAudioRecording(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfForCallPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CALL_PHONE
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfForCameraPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.CAMERA
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun checkSelfForContactPermission(activity: Activity): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.READ_CONTACTS
        ) != PackageManager.PERMISSION_GRANTED
    }

    fun requestPermissions(activity: Activity, permissions: Array<String>, requestCode: Int) {
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    fun requestPermissions(fragment: Fragment, permissions: Array<String>, requestCode: Int) {
        fragment.requestPermissions(permissions, requestCode)
    }

    fun isAudioRecordingPermissionGranted(context: Context): Boolean {
        val permission = "android.permission.RECORD_AUDIO"
        val res = context.checkCallingOrSelfPermission(permission)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun isCameraPermissionGranted(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(Manifest.permission.CAMERA)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun isCallPermissionGranted(context: Context): Boolean {
        val res = context.checkCallingOrSelfPermission(Manifest.permission.CALL_PHONE)
        return res == PackageManager.PERMISSION_GRANTED
    }

    fun checkPermissionForCameraAndMicrophone(context: Context): Boolean {
        val resultCamera = ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
        val resultMic = ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO)
        return resultCamera == PackageManager.PERMISSION_GRANTED && resultMic == PackageManager.PERMISSION_GRANTED
    }
}
