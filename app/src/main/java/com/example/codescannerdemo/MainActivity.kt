package com.example.codescannerdemo

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.psd.code_scanner.*


class MainActivity : AppCompatActivity() {
	private lateinit var codeScanner: CodeScanner

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

		codeScanner = CodeScanner(this, scannerView)

		// Parameters (default values)z
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
				//var url ="upi://pay?pa=shvmgupt16@okicici&pn=Shivam%20Gupt&aid=uGICAgIDAuIX5GA"
				//upi://pay?pa=8989161520@okbizaxis&pn=VIKASH%20TECH&mc=7372&aid=uGICAgICtuLKwPQ&tr=BCR2DN6TSWJJBHQG
				//upi://pay?pa=9996468979@axl&pn=Rakesh&mc=0000&mode=02&purpose=00
				//upi://pay?pa=amitmitu91@okaxis&pn=Amit%20jangid&aid=uGICAgICN7Oj1UA

				Log.d("decodeCallback___", it.text)

				try {
					if (it.text.startsWith("upi:")) {
						val removeKey1 = "upi:"
						val endpoint = "="
						val endpointChar = endpoint.single()

						val sb: StringBuffer = StringBuffer(it.text)
						removeKey(sb, removeKey1, endpointChar)

						var removeKey2 = ""
						val endpoint2 = " "
						val endpointChar2 = endpoint2.single()

						val sb2: StringBuffer = StringBuffer(sb.toString())
						if (sb2.contains("&mc")) {
							removeKey2 = "&mc"
						} else {
							removeKey2 = "&aid"
						}

						removeKey(sb2, removeKey2, endpointChar2)
						var upiIdOrName = sb2.toString()
						var upiId = upiIdOrName.split("&pn=")

						Log.d("decodeCallback___", "UpiId-- " + upiId[0] + ",      Name-- " + upiId[1].replace("%20", " "))

						Toast.makeText(this, "Scan result: ${upiId[0]}--${upiId[1]}", Toast.LENGTH_LONG).show()

					}else{
						Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()
					}
				} catch (e: Exception) {
					e.printStackTrace()
					Toast.makeText(this, "Scan result: ${it.text}", Toast.LENGTH_LONG).show()

				}

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

	//end  leftRecyclerView method
	fun removeKey(url: StringBuffer, removeKey: String?,endPoint:Char){
		val sIndex = url.indexOf(removeKey)
		try {
			while (url[sIndex] != endPoint) {
				url.deleteCharAt(sIndex)
			}
			url.deleteCharAt(sIndex)
		} catch (e: Exception) {
			e.printStackTrace()
		}
	}

}