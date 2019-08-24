package com.munkhtulga.understand

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.activity_remark_detail.*
import kotlinx.android.synthetic.main.remark_detail.view.*

/**
 * A fragment representing a single Remark detail screen.
 * This fragment is either contained in a [RemarkListActivity]
 * in two-pane mode (on tablets) or a [RemarkDetailActivity]
 * on handsets.
 */
class RemarkDetailFragment : Fragment() {

    private var item: Remark? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            if (it.containsKey(ARG_ITEM_ID)) {
                // Load the dummy content specified by the fragment
                // arguments. In a real-world scenario, use a Loader
                // to load content from a content provider.
                item = object : AsyncTask<String, Void, Remark>() {
                    override fun doInBackground(vararg p0: String?): Remark =
                        (activity?.application as UnderstandApplication).remarkDao.findByStartLocation(
                            it.getInt(ARG_ITEM_ID)
                        )!!
                }.execute().get()

                activity?.toolbar_layout?.title = item?.content
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.remark_detail, container, false)

        // Show the dummy content as text in a TextView.
        item?.let {
            rootView.remark_detail.text = it.content
        }

        return rootView
    }

    companion object {
        /**
         * The fragment argument representing the item ID that this fragment
         * represents.
         */
        const val ARG_ITEM_ID = "item_id"
    }
}
