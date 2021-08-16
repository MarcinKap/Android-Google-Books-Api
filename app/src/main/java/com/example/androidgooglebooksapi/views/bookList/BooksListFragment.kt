package com.example.androidgooglebooksapi.views.bookList

import android.annotation.SuppressLint
import android.app.TaskStackBuilder.create
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
import android.widget.Toast


class BooksListFragment() : BaseFragment() {

//    var freeBookListSize: Int = 0
//    var paidBookListSize: Int = 0

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var editText: EditText

        var freeBookListSize: Int = 0
        var paidBookListSize: Int = 0


        fun newInstance(): BooksListFragment {
            val args = Bundle()
            val fragment = BooksListFragment()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(searchingValue: String): BooksListFragment {
            val args = Bundle()
//            args.putString("searchingValue", searchingValue)
            val fragment = BooksListFragment()
            fragment.arguments = args
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_book_list, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerview_books_list)
        editText = view.findViewById(R.id.edit_text_books_title);
        editText.setHint(resources.getString(R.string.search))

        refreshAdapter(recyclerView, editText)
        setGridLayoutManagerInRecyclerView(recyclerView)

        return view;
    }

    fun refreshAdapter(recyclerView: RecyclerView, editText: EditText) {
        var timer = Timer()
        val DELAY: Long = 500 // Milliseconds

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                timer.cancel()
                timer = Timer()
                timer.schedule(
                    object : TimerTask() {
                        override fun run() {
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

                                                recyclerView.adapter =
                                                    response.body()
                                                        ?.let { BookListAdapter(it.items) }
                                            } else {
                                                recyclerView.adapter = null
                                            }
                                        }
                                    }

                                    override fun onFailure(call: Call<BookList>, t: Throwable) {
                                        if(!isNetworkAvailable(view!!.context)){
                                            Toast.makeText(context, resources.getString(R.string.not_connected), Toast.LENGTH_LONG)
                                                .show()
                                        }else{
                                            Toast.makeText(context, resources.getString(R.string.problem_with_download_data), Toast.LENGTH_LONG)
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
        outState.putString("booksTitle", editText.text.toString())
        outState.putString("currentFragment", "booksListFragment")

        super.onSaveInstanceState(outState)
    }
}