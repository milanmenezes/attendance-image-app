package com.androidsrc.server

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*

import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.PutObjectRequest
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_attendance.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.File
import java.io.FileNotFoundException
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


class attendance : Activity() {
    private var imagefile: File? = null
    private var course = String()


    private var currentPhotoPath = String()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance)
        val intent = intent
        this.course = intent.getStringExtra("courses")

        val clickImage = findViewById(R.id.clickPhoto) as Button
        val process = findViewById(R.id.process) as Button
        process.isEnabled = false

        clickImage.setOnClickListener {
            imagefile = dispatchTakePictureIntent()
            clickImage.isEnabled = false
            process.isEnabled = true
        }

        process.setOnClickListener {
            process.isEnabled = false

            val credentials = BasicAWSCredentials("AKIA5IWG22RQYFDQBNAJ", "Ksg4lQdx+3c+m8pRgmj4rmDD9/J/G/QE2a7rNPNo")
            val s3Client = AmazonS3Client(credentials)

            val transferUtility = TransferUtility.builder()
                    .context(applicationContext)
                    .awsConfiguration(AWSMobileClient.getInstance().configuration)
                    .s3Client(s3Client)
                    .defaultBucket("attendaceupload")
                    .build()

            //                final File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
            //                        "/" + filename);

            val uploadObserver = transferUtility.upload(imagefile!!.name, imagefile)

            uploadObserver.setTransferListener(object : TransferListener {

                override fun onStateChanged(id: Int, state: TransferState) {
                    if (TransferState.COMPLETED == state) {
                        // Handle a completed download.
                        Log.d("log:", "transfer complete")
                        processImage()
                    }
                }

                override fun onProgressChanged(id: Int, bytesCurrent: Long, bytesTotal: Long) {
                    val percentDonef = bytesCurrent.toFloat() / bytesTotal.toFloat() * 100
                    val percentDone = percentDonef.toInt()
                }

                override fun onError(id: Int, ex: Exception) {
                    // Handle errors
                }

            })

            // If your upload does not trigger the onStateChanged method inside your
            // TransferListener, you can directly check the transfer state as shown here.
            //                if (TransferState.COMPLETED == uploadObserver.getState()) {
            //                    // Handle a completed upload.
            //                }

        }


    }

    private fun processImage(){
        val process = findViewById(R.id.process) as Button
        val lparams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
//        lparams.setMargins(5,0,0,0)Present
        lparams.gravity = Gravity.CENTER
        val hparams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)

        val innerLinear = findViewById(R.id.innerLinear) as LinearLayout
        val queue = Volley.newRequestQueue(this)
        val url = "http://192.168.0.2:5000/process/"+ imagefile!!.name
        val stringRequest = StringRequest(Request.Method.GET, url,
                Response.Listener { response ->
                    // Display the first 500 characters of the response string.
                    //										mTextView.setText("Response is: "+ response.substring(0,500));
                    try {
                        val json = JSONObject(response)
                        Log.d("json",json.toString())
                        val present = json.getJSONArray("present")
                        Log.d("present",present.toString())
                        val absent = json.getJSONArray("absent")

                        val textPresent = TextView(this@attendance)
                        textPresent.layoutParams = hparams
                        textPresent.text = "Present"
                        innerLinear.addView(textPresent)
                        for (i in 0 until present.length()) {
                            val usn = present[i]
                            val text = TextView(this@attendance)
                            text.layoutParams = lparams
                            text.text = usn as String
                            text.tag= usn

                            innerLinear.addView(text)
//                            process.isEnabled = true

                        }
                        val textAbsent = TextView(this@attendance)
                        textAbsent.layoutParams = hparams
                        textAbsent.text = "Absent:"
                        innerLinear.addView(textAbsent)

                        for (i in 0 until absent.length()){
                            val usn = absent[i]
                            val check = CheckBox(this)
                            check.layoutParams=lparams
                            check.text=usn as String
                            check.id=i
                            check.tag=usn
                            innerLinear.addView(check)
                        }

                        val outerLinear = findViewById(R.id.outerLinear) as LinearLayout
                        var saveButton = Button(this)
                        saveButton.layoutParams = lparams
                        saveButton.text = "Save"
                        saveButton.setOnClickListener {
                            saveButton.isEnabled=false
                            var finalPresent = present
                            for (i in 0 until absent.length()){
                                val finalCheck = findViewById(i) as CheckBox
                                if(finalCheck.isChecked){
                                    finalPresent.put(finalCheck.tag)
                                }
                            }
                            Log.d("finalPresent",finalPresent.toString())
                            val updateUrl= "http://192.168.0.2:5000/update/$course/$finalPresent"
                            val attendanceUpdate = StringRequest(Request.Method.GET, updateUrl,
                                    Response.Listener { response ->
                                        if(response=="OK"){
                                            Toast.makeText(this, "Attendance Updated", Toast.LENGTH_SHORT).show()
                                            Log.d("Log","Attedance Updated")
                                        }
                                        else{
                                            Toast.makeText(this, "Update Error", Toast.LENGTH_SHORT).show()
                                            Log.d("Log","Update Error")
                                        }
                                    }, Response.ErrorListener {
                                //								mTextView.setText("That didn't work!");
                                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
                                Log.d("Log","No Internet")
                            })
                            queue.add(attendanceUpdate)
                        }
                        outerLinear.addView(saveButton)


                    }
                    catch (e:JSONException){
                        e.printStackTrace()
                        Toast.makeText(applicationContext, "JError", Toast.LENGTH_SHORT).show()
                    }



                }, Response.ErrorListener {
            //								mTextView.setText("That didn't work!");
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show()
        })

        // Add the request to the RequestQueue.
        queue.add(stringRequest)
    }


    private fun dispatchTakePictureIntent(): File? {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File

            }

            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        "com.androidsrc.server.fileprovider",
                        photoFile)
                Log.d("photoFile", photoFile.absolutePath)
                //                Log.d("photoURI",photoURI.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                return photoFile
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = course + "_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
                imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir      /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.absolutePath
        return image
    }

    companion object {
        internal val REQUEST_IMAGE_CAPTURE = 1
        internal val REQUEST_TAKE_PHOTO = 1
    }


}
