package com.munkhtulga.understand

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText

class EditRemarkActivity : AppCompatActivity() {

    private lateinit var remarkEditText: EditText
    private var remarkStart: Int = 0
    private var remarkEnd: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_remark)

        remarkStart = intent.extras?.getInt(REMARK_START) ?: 0
        remarkEnd = intent.extras?.getInt(REMARK_END) ?: 0
        val remarkText: String = (this@EditRemarkActivity.application as UnderstandApplication).getRemark(remarkStart)
        remarkEditText = findViewById<EditText>(R.id.remarkEditText).apply {
            setText(remarkText)
        }
    }

    override fun onPause() {
        super.onPause()
        updateRemark()
    }

    private fun updateRemark() {
        (this@EditRemarkActivity.application as UnderstandApplication).addRemark(
            remarkStart,
            remarkEnd,
            remarkEditText.text.toString()
        )
    }
}
