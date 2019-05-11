package com.munkhtulga.understand

import android.app.Application
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams
import java.io.FileOutputStream
import java.net.URL
import java.nio.channels.Channels


const val BOOK_TEXT = "com.munkhtulga.understand.BOOK_TEXT"
const val FILE_NAME = "dummy.doc"

class UnderstandApplication : Application() {
    private val remarks: HashMap<Int, String> = HashMap()

    fun addRemark(key: Int, value: String) = remarks.put(key, value)
    fun getRemark(key: Int) = remarks[key]
}

class MainActivity : AppCompatActivity() {

    class DownloadFileTask(private val fileOutputStream: FileOutputStream): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            Log.v("START!", "Background task starts")
            fileOutputStream.use {
                val url = URL(params[0])
                val readableByteChannel = Channels.newChannel(url.openStream())
                it.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            Log.v("DONE!", "DOOOOOONE!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout = findViewById<LinearLayout>(R.id.booksLinearLayout)
        DownloadFileTask(openFileOutput(FILE_NAME, Context.MODE_PRIVATE)).execute(
            "https://d9db56472fd41226d193-1e5e0d4b7948acaf6080b0dce0b35ed5.ssl.cf1.rackcdn.com/spectools/docs/wd-spectools-word-sample-04.doc"
        )
        (this.application as UnderstandApplication).addRemark(0, "Mojo got Mojo")

        for (i in 1..100) {
            addButton(layout = linearLayout, id = i)
        }
    }

    private fun addButton(layout: LinearLayout, id: Int) {
        val newButton = Button(this)
        val text = "AMAZING x $id!"

        newButton.text = text
        newButton.setOnClickListener {
            val intentToRead = Intent(this, ReadBookActivity::class.java).apply {
                putExtra(BOOK_TEXT, text)
            }
            startActivity(intentToRead)
        }

        val layoutParams = LinearLayout.LayoutParams(
            LayoutParams.MATCH_PARENT,
            LayoutParams.WRAP_CONTENT
        )
        layout.addView(newButton, layoutParams)
    }
}
