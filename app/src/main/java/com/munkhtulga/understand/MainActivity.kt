package com.munkhtulga.understand

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.LinearLayout.LayoutParams


const val BOOK_TEXT = "com.munkhtulga.understand.BOOK_TEXT"


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val linearLayout = findViewById<LinearLayout>(R.id.booksLinearLayout)

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
