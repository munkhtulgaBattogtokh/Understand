package com.munkhtulga.understand

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfRenderer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.TextView
import com.github.barteksc.pdfviewer.PDFView
import java.io.File


class ReadBookActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_book)

        val bookText = intent.getStringExtra(BOOK_TEXT)
        findViewById<TextView>(R.id.remarkTextView).apply {
            text = bookText +
                    intent.getStringExtra("NO_FILE")

        }
        renderPdfFileWithBartekscPdfViewer()
    }

    // cannot select text
    private fun renderPdfFileWithBartekscPdfViewer() {
        //val file = File(filesDir.absolutePath, "sample.pdf")
        val pdfView = findViewById<PDFView>(R.id.bookPdfView)
        pdfView.fromAsset("sample.pdf").load()
    }

    // cannot select text
//    private fun renderPdfFileStandardLibrary() {
//        val file = File(filesDir.absolutePath, "dummy.pdf")
//        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
//
//        val bookImageView = findViewById<ImageView>(R.id.bookImageView)
//        val bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.ARGB_8888)
//        val pdfRenderer = PdfRenderer(fileDescriptor)
//        val pageCount = pdfRenderer.pageCount
//
//        val page = pdfRenderer.openPage(0)
//        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
//        page.close()
//
//        pdfRenderer.close()
//
//        bookImageView.setImageBitmap(bitmap)
//    }
}
