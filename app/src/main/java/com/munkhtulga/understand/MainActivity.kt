package com.munkhtulga.understand

import android.app.Application
import androidx.room.Dao
import androidx.room.Room
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
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
    lateinit var bookDao: BookDao
    lateinit var remarkDao: RemarkDao
    lateinit var currentBook: Book
    var lastRemarkStartLocation: Int = 0
        get() = currentBook.lastRemarkStartLocation
        private set
    var lastRemarkEndLocation: Int = 0
        get() = currentBook.lastRemarkEndLocation
        private set

    fun addRemark(start: Int, end: Int, content: String){
        AddRemarkTask(start, end, content).execute() //= currentBook.addRemark
    }

    fun getRemark(start: Int): String = GetRemarkTask(start).execute().get() //= currentBook.getRemark(key)

    inner class AddRemarkTask(val start: Int, val end: Int, val content: String): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            val existingRemark: Remark? = remarkDao.findByStartLocation(start)
            if (existingRemark != null) remarkDao.delete(existingRemark)
            remarkDao.insertAll(Remark(start = start, end = end, content = content, bookTitle = currentBook.title))
        }
    }

    inner class GetRemarkTask(val start: Int): AsyncTask<String, Void, String>() {
        override fun doInBackground(vararg params: String?): String {
            val found: Remark? = remarkDao.findByStartLocation(start)
            currentBook.apply {
                lastRemarkStartLocation = found?.start ?: 0
                lastRemarkEndLocation = found?.end ?: 0
            }
            return found?.content ?: ""
        }
    }
}

class MainActivity : AppCompatActivity() {

    lateinit var app: UnderstandApplication

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

    inner class LoadBooksTask(): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            Log.v("START!", "FETCHING THE BOOKS FROM ROOM")

            val db = Room.databaseBuilder(
                applicationContext,
                AppDatabase::class.java, "remarks"
            ).build()

            app.bookDao = db.bookDao()
            app.remarkDao = db.remarkDao()

            for (book in app.bookDao.getAll()) {
                app.books[book.title] = book
            }
        }

        override fun onPostExecute(result: Unit?) {
            super.onPostExecute(result)
            Log.v("DONE!", "DONE FETCHING BOOKS FROM ROOM!")

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

    inner class StoreBooksTask(): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            Log.v("START!", "STORING THE BOOKS INTO ROOM")
            val books: List<Book> = (0..100).map { Book("Awesome book #${it}") }
            val existingBooks = app.bookDao.getAll()

            for (bookToInsert in books) {
                if (bookToInsert !in existingBooks) app.bookDao.insertAll(bookToInsert)
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

        LoadBooksTask().execute()
    }

    override fun onStop() {
        super.onStop()
        StoreBooksTask().execute()
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
