package com.munkhtulga.understand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ReadBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_book)

        val bookText = intent.getStringExtra(BOOK_TEXT)
        findViewById<TextView>(R.id.bookText).apply {
            text = bookText
        }
    }
}
