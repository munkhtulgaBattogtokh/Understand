package com.munkhtulga.understand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class EditRemarkActivity : AppCompatActivity() {

    private lateinit var remarkEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_remark)
        remarkEditText = findViewById(R.id.remarkEditText)
    }

    override fun onPause() {
        super.onPause()
        updateRemark()
    }

    private fun updateRemark() {
        (this@EditRemarkActivity.application as UnderstandApplication).addRemark(
            intent.extras?.getInt(REMARK_START) ?: 0,
            remarkEditText.text.toString()
        )
    }
}
