package com.munkhtulga.understand

import android.content.Intent
import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.util.Log
import android.view.*
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_read.*
import org.apache.poi.hwpf.HWPFDocument
import org.apache.poi.hwpf.extractor.WordExtractor
import java.io.File
import java.io.FileInputStream

const val REMARK_START = "com.munkhtulga.understand.REMARK_START"
const val REMARK_END = "com.munkhtulga.understand.REMARK_END"

lateinit var selectedSpan: ReadActivity.RemarkClickableSpan

class ReadActivity : AppCompatActivity() {
    private lateinit var remarkTextView: TextView
    private lateinit var bookTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar?.setDisplayShowTitleEnabled(false)

        fab.setOnClickListener {
            val app = (application as UnderstandApplication)
            startEditActivity(
                app.lastRemarkStartLocation,
                app.lastRemarkEndLocation
            )
        }

        remarkTextView = findViewById(R.id.remarkTextView)
        bookTextView = findViewById<TextView>(R.id.bookTextView).apply { setText(text, TextView.BufferType.SPANNABLE) }

        bookTextView.apply {
            customSelectionActionModeCallback = remarkActionModeCallBack
        }
        RestoreRemarksTask().execute()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.overflow_menu, menu)
        return true
    }

    override fun onResume() {
        super.onResume()
        remarkTextView.text = (this@ReadActivity.application as UnderstandApplication).getRemark(
            (application as UnderstandApplication).lastRemarkStartLocation
        )
    }

    inner class RestoreRemarksTask(): AsyncTask<String, Void, Unit>() {
        override fun doInBackground(vararg params: String?) {
            (application as UnderstandApplication).apply {
                val remarks = remarkDao.getAllByBookTitle(currentBook.title)
                for (r in remarks) {
                    remark(bookTextView.text as SpannableString, r.start, r.end)
                }
            }
        }
    }

    private val remarkActionModeCallBack = object :  ActionMode.Callback {
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
                    this@ReadActivity.bookTextView.apply {
                        val (remarkStart, remarkEnd) = remark(
                            bookTextView.text as SpannableString,
                            start = bookTextView.selectionStart,
                            end = bookTextView.selectionEnd
                        )
                        startEditActivity(remarkStart, remarkEnd)
                    }
                    mode?.finish()
                    true
                }
                else -> false
            }
        }
    }

    private val startEditActivity = { remarkStart: Int, remarkEnd: Int ->
        val intentToEditRemark = Intent(this, EditRemarkActivity::class.java).apply {
            putExtra(REMARK_START, remarkStart)
            putExtra(REMARK_END, remarkEnd)
        }
        startActivity(intentToEditRemark)
    }

    inner class RemarkClickableSpan(val start: Int, val end: Int): ClickableSpan() {
        private var selected: Boolean = false
            set(isSelected) {
                field = isSelected
                (bookTextView.text as SpannableString).setSpan(
                    BackgroundColorSpan(if (isSelected) Color.GREEN else Color.YELLOW), start, end, 0
                )
                if (isSelected) {
                    if (::selectedSpan.isInitialized) { selectedSpan.selected = false }
                    selectedSpan = this
                }
            }

        override fun onClick(widget: View) {
            remarkTextView.text = (this@ReadActivity.application as UnderstandApplication).getRemark(start)
            selected = true
        }
    }

    private fun remark(srcString: SpannableString, start: Int, end: Int): Pair<Int, Int> {
        srcString.setSpan(BackgroundColorSpan(Color.YELLOW), start, end,0)
        srcString.setSpan(RemarkClickableSpan(start, end), start, end,0)

//        (this@ReadActivity.application as UnderstandApplication).currentBook.remarkEnds[start] = end
        bookTextView.apply {
            text = srcString
            movementMethod = LinkMovementMethod.getInstance()
            highlightColor = Color.YELLOW
        }
        return Pair(start, end)
    }

//    private fun bookText(): String {
//        val file = File(filesDir.absolutePath, "dummy.doc")
//        val fStream = FileInputStream(file.absolutePath)
//        val doc = HWPFDocument(fStream) // maybe redundant
//        val wordExtractor = WordExtractor(doc)
//
//        var content = ""
//        val paragraphs = wordExtractor.paragraphText
//        for (paragraph in paragraphs) {
//            content += paragraph.toString()
//        }
//        fStream.close()
//        Log.v("WORDFILE", content)
//        return content
//    }
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