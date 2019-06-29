package com.munkhtulga.understand

import android.app.Application
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Room
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
    val books: HashMap<String, Book> = HashMap()
    lateinit var currentBook: Book
    var lastRemarkLocation: Int = 0
//        get() = currentBook.lastRemarkLocation
        private set

    fun addRemark(key: Int, value: String) {}//= currentBook.addRemark(key, value)
    fun getRemark(key: Int): String? = "DEFAULT REMARK" //= currentBook.getRemark(key)
}

class MainActivity : AppCompatActivity() {

    lateinit var app: UnderstandApplication
    lateinit var bookDao: BookDao

//    class DownloadFileTask(private val fileOutputStream: FileOutputStream): AsyncTask<String, Void, Unit>() {
//        override fun doInBackground(vararg params: String?) {
//            Log.v("START!", "Background task starts")
//            fileOutputStream.use {
//                val url = URL(params[0])
//                val readableByteChannel = Channels.newChannel(url.openStream())
//                it.channel.transferFrom(readableByteChannel, 0, Long.MAX_VALUE)
//            }
//        }
//
//        override fun onPostExecute(result: Unit?) {
//            super.onPostExecute(result)
//            Log.v("DONE!", "DOOOOOONE!")
//        }
//    }

    class LoadBooksTask(val activity: MainActivity): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            Log.v("START!", "FETCHING THE BOOKS FROM ROOM")

            activity.apply {
                val db = Room.databaseBuilder(
                    applicationContext,
                    AppDatabase::class.java, "remarks"
                ).build()

                bookDao = db.bookDao()

                for (book in bookDao.getAll()) {
                    app.books[book.title] = book
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            Log.v("DONE!", "DONE FETCHING BOOKS FROM ROOM!")

            activity.apply {
                val linearLayout = findViewById<LinearLayout>(R.id.booksLinearLayout)
                //        DownloadFileTask(openFileOutput(FILE_NAME, Context.MODE_PRIVATE)).execute(
                //            "https://d9db56472fd41226d193-1e5e0d4b7948acaf6080b0dce0b35ed5.ssl.cf1.rackcdn.com/spectools/docs/wd-spectools-word-sample-04.doc"
                //        )

                for ((title, _) in app.books) {
                    Log.v("BOOKNAME:", title)
                    addButton(layout = linearLayout, text=title)
                }
            }
        }
    }

    class StoreBooksTask(val activity: MainActivity): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            Log.v("START!", "STORING THE BOOKS INTO ROOM")
            activity.apply{
                val books: List<Book> = (0..100).map { Book("Awesome book #${it}") }
                val existingBooks = bookDao.getAll()

                for (bookToInsert in books) {
                    if (bookToInsert !in existingBooks) bookDao.insertAll(bookToInsert)
                }
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            Log.v("DONE!", "DONE STORING BOOKS INTO ROOM!")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        app = application as UnderstandApplication

        LoadBooksTask(this).execute()
    }

    override fun onStop() {
        super.onStop()
        StoreBooksTask(this).execute()
    }

    private fun addButton(layout: LinearLayout, text: String) {
        val newButton = Button(this)

        newButton.text = text
        newButton.setOnClickListener {
            app.currentBook = app.books[text]!!
            val intentToRead = Intent(this, ReadActivity::class.java).apply {
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
