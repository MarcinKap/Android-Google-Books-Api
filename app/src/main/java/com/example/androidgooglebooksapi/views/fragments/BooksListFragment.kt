package com.example.androidgooglebooksapi.views.fragments

import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.androidgooglebooksapi.R
import com.example.androidgooglebooksapi.models.bookList.BookList
import android.text.Editable
import android.text.TextWatcher

import androidx.recyclerview.widget.GridLayoutManager.SpanSizeLookup
import com.example.androidgooglebooksapi.api.RetrofitInstance
import com.example.androidgooglebooksapi.models.bookList.Items
import com.example.androidgooglebooksapi.views.BaseFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.ArrayList

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.transition.TransitionInflater
import android.widget.Toast
import androidx.core.app.SharedElementCallback
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.activityViewModels
import com.example.androidgooglebooksapi.MainActivity
import com.example.androidgooglebooksapi.views.adapters.BookListAdapter
import com.example.androidgooglebooksapi.views.viewModel.DataViewModel
import kotlinx.android.synthetic.main.activity_main.*


class BooksListFragment() : BaseFragment() {

    private val viewModel: DataViewModel by activityViewModels()

    //    var freeBookListSize: Int = 0
//    var paidBookListSize: Int = 0
    private var freeBookListSize: Int = 0
    private var paidBookListSize: Int = 0
    private lateinit var editText: EditText
    private lateinit var recyclerView: RecyclerView


    companion object {
        fun newInstance(): BooksListFragment {
            val args = Bundle()
            val fragment = BooksListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_book_list, container, false)

        recyclerView = view.findViewById(R.id.recyclerview_books_list)
        editText = view.findViewById(R.id.edit_text_books_title);
        setAdapter(editText)
        setGridLayoutManagerInRecyclerView(recyclerView)
        exitTransition = TransitionInflater.from(context)
            .inflateTransition(R.transition.image_shared_element_transition)


        return view;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        postponeEnterTransition()

        viewModel.data.observe(viewLifecycleOwner) {
            recyclerView.adapter = BookListAdapter(it.items, this@BooksListFragment)
            scrollToPosition()


            (view.parent as? ViewGroup)?.doOnPreDraw {
                startPostponedEnterTransition()
                if (MainActivity.currentPositionOnMainList == 0) {
                    recyclerView.scheduleLayoutAnimation()
                }
            }
        }
        if (recyclerView.adapter != null) {
            prepareTransitions()
            postponeEnterTransition()
        }

    }


    fun setAdapter(editText: EditText) {
        var timer = Timer()
        val DELAY: Long = 1000 // Milliseconds

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (editText.hasFocus()) {
                    timer.cancel()
                    timer = Timer()
                    timer.schedule(
                        object : TimerTask() {
                            override fun run() {
                                MainActivity.currentPositionOnMainList = 0
                                MainActivity.currentPositionToShowOnSmallList = 0

                                RetrofitInstance.getApiRepository.getBookList(editText.text.toString())
                                    .enqueue(object : Callback<BookList> {
                                        override fun onResponse(
                                            call: Call<BookList>,
                                            response: Response<BookList>
                                        ) {
                                            if (response.isSuccessful) {
                                                if (response.body()?.items != null) {
                                                    response.body()
                                                        ?.let { setFreeAndPaidBookListSize(it.items) }
                                                    setGridLayoutManagerInRecyclerView(recyclerView)


                                                    viewModel.setData(response.body()!!)
//                                                    recyclerView.scheduleLayoutAnimation()
                                                } else {
//                                                    viewModel.setData(null)
                                                    recyclerView.adapter = null
                                                }
                                            }
                                        }

                                        override fun onFailure(call: Call<BookList>, t: Throwable) {
                                            if (!isNetworkAvailable(view!!.context)) {
                                                Toast.makeText(
                                                    context,
                                                    resources.getString(R.string.not_connected),
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    resources.getString(R.string.problem_with_download_data),
                                                    Toast.LENGTH_LONG
                                                )
                                                    .show()
                                            }
                                        }
                                    }
                                    )
                            }
                        },
                        DELAY
                    )
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })
    }
    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun setGridLayoutManagerInRecyclerView(recyclerView: RecyclerView) {
        var spanCount = 2
        if (context?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 4
        }
        val gridLayout = GridLayoutManager(activity, spanCount)
        gridLayout.spanSizeLookup = object : SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position == 0 || (freeBookListSize != 0 && position == freeBookListSize + 1)) {
                    return spanCount;
                } else {
                    return 1;
                }
            }
        }
        recyclerView.apply {
            layoutManager = gridLayout
        }
    }


    /**
     * Scrolls the recycler view to show the last viewed item in the grid. This is important when
     * navigating back from the grid.
     */
    private fun scrollToPosition() {
        recyclerView.addOnLayoutChangeListener(object : View.OnLayoutChangeListener {
            override fun onLayoutChange(
                v: View,
                left: Int,
                top: Int,
                right: Int,
                bottom: Int,
                oldLeft: Int,
                oldTop: Int,
                oldRight: Int,
                oldBottom: Int
            ) {
                recyclerView.removeOnLayoutChangeListener(this)
                val layoutManager: RecyclerView.LayoutManager? = recyclerView.getLayoutManager()
                val viewAtPosition =
                    layoutManager?.findViewByPosition(MainActivity.currentPositionOnMainList)
                layoutManager?.scrollToPosition(MainActivity.currentPositionOnMainList)
            }
        })
    }

    /**
     * Prepares the shared element transition to the pager fragment, as well as the other transitions
     * that affect the flow.
     */
    private fun prepareTransitions() {

        setExitSharedElementCallback(
            object : SharedElementCallback() {
                override fun onMapSharedElements(
                    names: List<String>,
                    sharedElements: MutableMap<String, View>
                ) {
                    // Locate the ViewHolder for the clicked position.
                    val selectedViewHolder = recyclerView
                        .findViewHolderForAdapterPosition(MainActivity.currentPositionOnMainList)
                        ?: return


                    // Map the first shared element name to the child ImageView.
                    sharedElements[names[0]] =
                        selectedViewHolder.itemView.findViewById(R.id.image_book)
                }
            })
    }


    fun setFreeAndPaidBookListSize(itemsList: ArrayList<Items>) {
        freeBookListSize = 0
        paidBookListSize = 0
        itemsList.forEach {
            if ("FREE".equals(it.saleInfo.saleability)) {
                freeBookListSize++
            } else {
                paidBookListSize++
            }
        }
    }


    override fun onSaveInstanceState(outState: Bundle) {

        super.onSaveInstanceState(outState)
    }
}