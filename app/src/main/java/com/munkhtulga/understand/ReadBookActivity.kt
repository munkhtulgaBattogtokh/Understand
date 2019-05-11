package com.munkhtulga.understand

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import org.apache.poi.hwpf.HWPFDocument
import java.io.File
import java.io.FileInputStream
import org.apache.poi.hwpf.extractor.WordExtractor
import android.text.style.BackgroundColorSpan
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.*

const val REMARK_START = "com.munkhtulga.understand.remark_start"

class ReadBookActivity : AppCompatActivity() {

    private lateinit var remarkEditText: TextView
    private lateinit var bookTextView: TextView
    private lateinit var bookText: SpannableString

    inner class RemarkClickableSpan(private val start: Int): ClickableSpan() {
        override fun onClick(widget: View) {
            remarkEditText.text = (this@ReadBookActivity.application as UnderstandApplication).getRemark(start)
        }
    }

    private val actionModeCallBack = object :  ActionMode.Callback {
        override fun onDestroyActionMode(mode: ActionMode?) {}
        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean { return false }
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater: MenuInflater = mode!!.menuInflater
            inflater.inflate(R.menu.text_remark_menu, menu)
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.text_remark_menuitem -> {
                    this@ReadBookActivity.bookTextView.apply {
                        val remarkStart = remark(bookText)
                        movementMethod = LinkMovementMethod.getInstance()
                        highlightColor = Color.YELLOW

                        val intentToEditRemark = Intent(this@ReadBookActivity, EditRemarkActivity::class.java).apply {
                            putExtra(REMARK_START, remarkStart)
                        }
                        startActivity(intentToEditRemark)
                    }
                    mode?.finish()
                    true
                }
                else -> false
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_book)

        remarkEditText = findViewById(R.id.remarkTextView)
        bookTextView = findViewById(R.id.bookTextView)
        bookText = SpannableString(bookText())

        remarkEditText.apply {
            val bookText = intent.getStringExtra(BOOK_TEXT)
            text = "$bookText${intent.getStringExtra("NO_FILE")}"
        }

        bookTextView.apply {
            text = bookText()
            customSelectionActionModeCallback = actionModeCallBack
        }
    }


    private fun bookText(): String {
        val file = File(filesDir.absolutePath, "dummy.doc")
        val fStream = FileInputStream(file.absolutePath)
        val doc = HWPFDocument(fStream) // maybe redundant
        val wordExtractor = WordExtractor(doc)

        var content = ""
        val paragraphs = wordExtractor.paragraphText
        for (paragraph in paragraphs) {
            content += paragraph.toString()
        }
        fStream.close()
        Log.v("WORDFILE", content)
        return content
    }

    private fun remark(srcString: SpannableString): Int {
        val start = bookTextView.selectionStart
        val end = bookTextView.selectionEnd
        srcString.setSpan(BackgroundColorSpan(Color.YELLOW), start, end,0)
        srcString.setSpan(RemarkClickableSpan(start), start, end,0)

        bookTextView.text = srcString
        return start
    }
    
    // all features in, but basically just google docs; use WebView; don't want margins and the PDFness.
//    private fun renderPdfWithGoogleDocs(pdfLink: String) {
//        val webView = findViewById<WebView>(R.id.bookWebView)
//        webView.settings.javaScriptEnabled = true
//        webView.webViewClient = WebViewClient()
//        webView.loadUrl("http://docs.google.com/gview?url=$pdfLink")
//    }

    // cannot select text; use PdfView
//    private fun renderPdfFileWithBartekscPdfViewer() {
//        //val file = File(filesDir.absolutePath, "sample.pdf")
//        val pdfView = findViewById<PDFView>(R.id.bookPdfView)
//        pdfView.fromAsset("sample.pdf").load()
//    }

    // cannot select text; use ImageView
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
