package com.example.codescannerdemo

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.psd.code_scanner.*
import androidx.core.app.ActivityCompat.requestPermissions

import android.content.pm.PackageManager

import android.os.Build




class MainActivity : AppCompatActivity() {
	private lateinit var codeScanner: CodeScanner

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

		codeScanner = CodeScanner(this, scannerView)

		// Parameters (default values)
		codeScanner.camera = CodeScanner.CAMERA_BACK // or CAMERA_FRONT or specific camera id
		codeScanner.formats = CodeScanner.ALL_FORMATS // list of type BarcodeFormat,
		// ex. listOf(BarcodeFormat.QR_CODE)
		codeScanner.autoFocusMode = AutoFocusMode.SAFE // or CONTINUOUS
		codeScanner.scanMode = ScanMode.SINGLE // or CONTINUOUS or PREVIEW
		codeScanner.isAutoFocusEnabled = true // Whether to enable auto focus or not
		codeScanner.isFlashEnabled = false // Whether to enable flash or not

		// Callbacks
		codeScanner.decodeCallback = DecodeCallback {
			runOnUiThread {
				Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
			//	var url ="upi://pay?pa=shvmgupt16@okicici&pn=Shivam%20Gupt&aid=uGICAgIDAuIX5GA"
				Log.d("decodeCallback___", it.text)
			}
		}

		codeScanner.errorCallback = ErrorCallback { // or ErrorCallback.SUPPRESS
			runOnUiThread {
				Toast.makeText(
					this, "Camera initialization error: ${it.message}",
					Toast.LENGTH_LONG
				).show()
			}
		}

		scannerView.setOnClickListener {
			codeScanner.startPreview()
		}
	}

	override fun onStart() {
		super.onStart()

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			val hasCameraPermission = checkSelfPermission(Manifest.permission.CAMERA)
			val permissions: MutableList<String> = ArrayList()
			if (hasCameraPermission != PackageManager.PERMISSION_GRANTED) {
				permissions.add(Manifest.permission.CAMERA)
			}
			if (!permissions.isEmpty()) {
				requestPermissions(permissions.toTypedArray(), 111)
			}
		}
	}
	override fun onResume() {
		super.onResume()
		codeScanner.startPreview()
	}

	override fun onPause() {
		codeScanner.releaseResources()
		super.onPause()
	}
}